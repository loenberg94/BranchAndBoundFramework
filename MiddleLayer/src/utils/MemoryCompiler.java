package utils;

import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.*;

public class MemoryCompiler {

    private JavaCompiler javac;
    private DynamicClassLoader classLoader;
    private Iterable<String> options;
    private boolean ignoreWarnings = false;

    private HashMap<String,SourceCode> sourceCodes = new HashMap<>();

    public static MemoryCompiler newInstance(){
        return new MemoryCompiler();
    }

    private MemoryCompiler(){
        this.javac = ToolProvider.getSystemJavaCompiler();
        this.classLoader = new DynamicClassLoader(ClassLoader.getSystemClassLoader());
    }

    public MemoryCompiler useParentClassLoader(ClassLoader parent){
        this.classLoader = new DynamicClassLoader(parent);
        return this;
    }

    public DynamicClassLoader getClassloader() {
        return classLoader;
    }

    public MemoryCompiler useOptions(String... options){
        this.options = Arrays.asList(options);
        return this;
    }

    public MemoryCompiler ignoreWarnings(){
        ignoreWarnings = true;
        return this;
    }

    public Map<String, Class<?>> compileAll() throws Exception {
        if (sourceCodes.size() == 0) {
            throw new Exception("No source code to compile");
        }

        Collection<SourceCode> compilationUnits = sourceCodes.values();
        CompiledCode[] code;

        code = new CompiledCode[compilationUnits.size()];
        Iterator<SourceCode> iter = compilationUnits.iterator();

        for (int i = 0; i < code.length; i++) {
            code[i] = new CompiledCode(iter.next().getClassName());
        }

        DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<>();
        ExtendedJavaFileManager fileManager = new ExtendedJavaFileManager(javac.getStandardFileManager(null, null, null), classLoader);
        JavaCompiler.CompilationTask task = javac.getTask(null, fileManager, collector, options, null, compilationUnits);
        boolean result = task.call();

        if (!result || collector.getDiagnostics().size() > 0) {
            StringBuffer exceptionMsg = new StringBuffer();
            exceptionMsg.append("Unable to compile the source");
            boolean hasWarnings = false;
            boolean hasErrors = false;
            for (Diagnostic<? extends JavaFileObject> d : collector.getDiagnostics()) {
                switch (d.getKind()) {
                    case NOTE:
                    case MANDATORY_WARNING:
                    case WARNING:
                        hasWarnings = true;
                        break;
                    case OTHER:
                    case ERROR:
                    default:
                        hasErrors = true;
                        break;
                }
                exceptionMsg.append("\n").append("[kind=").append(d.getKind());
                exceptionMsg.append(", ").append("line=").append(d.getLineNumber());
                exceptionMsg.append(", ").append("message=").append(d.getMessage(Locale.US)).append("]");
            }
            if (hasWarnings && !ignoreWarnings || hasErrors) {
                throw new Exception(exceptionMsg.toString());
            }
        }
        Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
        for (String className : sourceCodes.keySet()) {
            classes.put(className, classLoader.loadClass(className));
        }
        return classes;
    }

    public Class<?> compile(String className, String sourceCode) throws Exception {
        return addSource(className,sourceCode).compileAll().get(className);
    }

    public MemoryCompiler addSource(String className, String sourceCode) throws Exception {
        sourceCodes.put(className,new SourceCode(className,sourceCode));
        return this;
    }


    /**
     * private classes
     */

    private class CompiledCode extends SimpleJavaFileObject {
        private ByteArrayOutputStream baos = new ByteArrayOutputStream();

        private String className;

        public CompiledCode(String className) throws Exception {
            super(new URI(className), Kind.CLASS);
            this.className = className;
        }

        public String getClassName() {
            return className;
        }

        @Override
        public OutputStream openOutputStream() throws IOException {
            return baos;
        }

        public byte[] getByteCode() {
            return baos.toByteArray();
        }
    }

    private class SourceCode extends SimpleJavaFileObject {
        private String contents = null;
        private String className;

        public SourceCode(String className, String contents) throws Exception {
            super(URI.create("string:///" + className.replace('.', '/')
                    + Kind.SOURCE.extension), Kind.SOURCE);
            this.contents = contents;
            this.className = className;
        }

        public String getClassName() {
            return className;
        }

        public CharSequence getCharContent(boolean ignoreEncodingErrors)
                throws IOException {
            return contents;
        }
    }

    private class ExtendedJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {
        private List<CompiledCode> compiledCode = new ArrayList<CompiledCode>();
        private DynamicClassLoader cl;

        protected ExtendedJavaFileManager(JavaFileManager fileManager,
                                                  DynamicClassLoader cl) {
            super(fileManager);
            this.cl = cl;
        }

        @Override
        public JavaFileObject getJavaFileForOutput(
                JavaFileManager.Location location, String className,
                JavaFileObject.Kind kind, FileObject sibling) throws IOException {
            try {
                CompiledCode innerClass = new CompiledCode(className);
                compiledCode.add(innerClass);
                cl.addCode(innerClass);
                return innerClass;
            } catch (Exception e) {
                throw new RuntimeException(
                        "Error while creating in-memory output file for "
                                + className, e);
            }
        }

        @Override
        public ClassLoader getClassLoader(JavaFileManager.Location location) {
            return cl;
        }
    }

    private class DynamicClassLoader extends ClassLoader {
        private Map<String, CompiledCode> customCompiledCode = new HashMap<>();

        public DynamicClassLoader(ClassLoader parent) {
            super(parent);
        }

        public void addCode(CompiledCode cc) {
            customCompiledCode.put(cc.getName(), cc);
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            CompiledCode cc = customCompiledCode.get(name);
            if (cc == null) {
                return super.findClass(name);
            }
            byte[] byteCode = cc.getByteCode();
            return defineClass(name, byteCode, 0, byteCode.length);
        }
    }
}
