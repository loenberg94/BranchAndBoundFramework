package utils;

import javafx.util.Pair;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Compiler {
    public static Pair<Boolean,DiagnosticCollector<JavaFileObject>> compileClass(File file){
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        JavaCompiler compiler = javax.tools.ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics,null,null);

        List<String> optionList = new ArrayList<>();
        optionList.add("-classpath");
        optionList.add(System.getProperty("java.class.path"));

        Iterable<? extends JavaFileObject> compilationUnit = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(file));
        JavaCompiler.CompilationTask task = compiler.getTask(null,fileManager,diagnostics,optionList,null,compilationUnit);

        if(task.call()){
            try {
                fileManager.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new Pair<>(true,null);
        }
        return new Pair<>(false,diagnostics);
    }
}
