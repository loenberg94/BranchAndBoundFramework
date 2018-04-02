package com.mal.utils.compiler.memory;

import javax.tools.*;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MemoryCompiler {
    JavaCompiler compiler;
    StandardJavaFileManager standardJavaFileManager;

    public MemoryCompiler(){
        compiler = ToolProvider.getSystemJavaCompiler();
        standardJavaFileManager = compiler.getStandardFileManager(null,null,null);
    }

    public Method compileStatic(String methodName, String className, String source) throws ClassNotFoundException {
        Map<String, byte[]> classBytes = compile(className + ".java", source);
        MemoryClassLoader loader = new MemoryClassLoader(classBytes);
        Class cls = loader.loadClass(className);
        Method[] methods = cls.getDeclaredMethods();
        for(Method method : methods){
            if (method.getName().equals(methodName)){
                if (!method.isAccessible()) method.setAccessible(true);
                return method;
            }
        }
        throw new NoSuchMethodError(methodName);
    }

    public Map<String, byte[]> compile(String filename, String source){
        return compile(filename, source, new PrintWriter(System.err), null, null);
    }

    private Map<String, byte[]> compile(String filename, String source, Writer err, String sourcepath, String classpath){
        MemoryFileManager fileManager = new MemoryFileManager(standardJavaFileManager);
        List<JavaFileObject> compUnits = Arrays.asList(fileManager.makeStringSource(filename, source));
        return compile(compUnits, fileManager, err, sourcepath, classpath);
    }

    private Map<String,byte[]> compile(List<JavaFileObject> compUnits, MemoryFileManager fileManager, Writer err, String sourcepath, String classpath) {
        DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<>();

        List<String> options = new ArrayList();
        options.add("-Xlint:all");
        options.add("-deprecation");

        if (sourcepath != null){
            options.add("-sourcepath");
            options.add(sourcepath);
        }
        if (classpath != null){
            options.add("-classpath");
            options.add(classpath);
        }

        JavaCompiler.CompilationTask task = compiler.getTask(err, fileManager, diagnosticCollector, options, null, compUnits);
        if(!task.call()){
            PrintWriter perr = new PrintWriter(err);
            for(Diagnostic diagnostic : diagnosticCollector.getDiagnostics()){
                perr.println(diagnostic);
            }
            perr.flush();
            return null;
        }

        Map<String, byte[]> classbytes = fileManager.getClassbytes();
        fileManager.close();

        return classbytes;
    }


}
