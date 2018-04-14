package com.mal;

import com.mal.UI.mainform;
import com.mal.utils.compiler.memory.MemoryCompiler;

import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Main {

    public static void main(String[] args) {
        JFrame frame = new JFrame("mainform");
        frame.setSize(new Dimension(600,400));
        frame.setContentPane(new mainform().mainpanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }



    /*
    * MemoryCompiler compiler = new MemoryCompiler();

        StringWriter writer = new StringWriter();
        PrintWriter out = new PrintWriter(writer);
        out.println("public class HelloWorld {");
        out.println("  public static void main(String test) {");
        out.println("    System.out.println(\"This is in another java file\");");
        out.println("  }");
        out.println("}");
        out.close();

        try {
            Method method = compiler.compileStatic("main", "HelloWorld", writer.toString());
            method.invoke(null, "");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    * */
}
