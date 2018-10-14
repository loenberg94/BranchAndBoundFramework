package utils;

import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.*;

public class MemoryCompiler {

    private JavaCompiler javac;
    private DynamicClassloader classloader;
    private Iterable<String> options;
    private boolean ignoreWarnings = false;

    private HashMap<String,SourceCode> sourceCodes = new HashMap<>();

    public static MemoryCompiler newInstance(){
        return new MemoryCompiler();
    }

    private MemoryCompiler(){
        this.javac = ToolProvider.getSystemJavaCompiler();
        this.classloader = new DynamicClassloader(ClassLoader.getSystemClassLoader());
    }

    public MemoryCompiler useParentClassLoader(ClassLoader parent){
        this.classloader = new DynamicClassloader(parent);
        return this;
    }

    public DynamicClassloader getClassloader() {
        return classloader;
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
        if(sourceCodes.size() == 0){
            throw new Exception("No source codes to compile");
        }

        Collection<SourceCode> compilationUnits = sourceCodes.values();
        CompiledCode[] code;

        code = new CompiledCode[compilationUnits.size()];
        Iterator<SourceCode> iter = compilationUnits.iterator();
        for(int i = 0; i < code.length;i++){
            code[i] = new CompiledCode(iter.next().getClassname());
        }

        DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<>();
        ExtendedJavaFileManager fileManager = new ExtendedJavaFileManager(javac.getStandardFileManager(collector,null,null),classloader);
        JavaCompiler.CompilationTask task = javac.getTask(null,fileManager,collector,options,null,compilationUnits);
        boolean result = task.call();

        if(!result || collector.getDiagnostics().size() > 0){
            StringBuffer message = new StringBuffer();
            message.append("Unable to compile the source code");
            boolean hasWarnings = false;
            boolean hasErrors = false;
            for(Diagnostic<? extends JavaFileObject> d: collector.getDiagnostics()){
                switch (d.getKind()){
                    case NOTE:
                    case WARNING:
                    case MANDATORY_WARNING:
                        hasWarnings = true;
                        break;
                    case OTHER:
                    case ERROR:
                    default:
                        hasErrors = true;
                        break;
                }
                message.append("\n").append("[kind=").append(d.getKind());
                message.append(", ").append("line=").append(d.getLineNumber());
                message.append(", ").append("message=").append(d.getMessage(Locale.US)).append("]");

                if(hasWarnings && !ignoreWarnings || hasErrors){
                    throw new Exception(message.toString());
                }
            }
        }

        Map<String,Class<?>> classes = new HashMap<>();
        for(String classname: sourceCodes.keySet()){
            classes.put(classname,classloader.loadClass(classname));
        }
        return classes;
    }

    public Class<?> compile(String className, String sourceCode) throws Exception {
        return addSource(className,sourceCode).compileAll().get(className);
    }

    public MemoryCompiler addSource(String className, String sourceCode){
        sourceCodes.put(className,new SourceCode(sourceCode,className));
        return this;
    }


    /**
     * private classes
     */

    private class CompiledCode extends SimpleJavaFileObject {
        private ByteArrayOutputStream baos = new ByteArrayOutputStream();
        private String classname;

        protected CompiledCode(String classname) {
            super(URI.create(classname),Kind.CLASS);
            this.classname = classname;
        }

        public String getClassname() {
            return classname;
        }

        @Override
        public OutputStream openOutputStream() throws IOException {
            return this.baos;
        }

        public byte[] getByteCode(){
            return this.baos.toByteArray();
        }
    }

    private class SourceCode extends SimpleJavaFileObject {
        private String content = null;
        private String classname;

        public SourceCode(String content, String classname){
            super(URI.create("string:///" + classname.replace('.','/') + Kind.SOURCE.extension),Kind.SOURCE);
            this.content = content;
            this.classname = classname;
        }

        public String getClassname() {
            return classname;
        }

        public String getContent() {
            return content;
        }
    }

    private class ExtendedJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {
        private List<CompiledCode> compiledCodes = new ArrayList<>();
        private DynamicClassloader cl;

        protected ExtendedJavaFileManager(JavaFileManager fileManager, DynamicClassloader cl) {
            super(fileManager);
            this.cl = cl;
        }

        @Override
        public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
            CompiledCode innerclass = new CompiledCode(className);
            compiledCodes.add(innerclass);
            /*
            cl.addCode(innerclass);
             */
            return innerclass;
        }

        @Override
        public ClassLoader getClassLoader(Location location) {
            return cl;
        }
    }

    private class DynamicClassloader extends ClassLoader {
        private Map<String, CompiledCode> customCompiledCode = new HashMap<>();

        public DynamicClassloader(ClassLoader parent){
            super(parent);
        }

        public void addCode(CompiledCode c){
            customCompiledCode.put(c.getName(),c);
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            CompiledCode cc = customCompiledCode.get(name);
            if(cc == null){
                return super.findClass(name);
            }
            byte[] byteCode = cc.getByteCode();
            return defineClass(name,byteCode,0,byteCode.length);
        }
    }
}
