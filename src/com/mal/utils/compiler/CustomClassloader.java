package com.mal.utils.compiler;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class CustomClassloader {
    private static String createClassName(File file){
        return file.getParent();
    }

    public static <T> T loadObject(File file){
        File f = new File(createClassName(file));
        System.out.printf("%s\n",f.getPath());
        URLClassLoader classLoader = null;
        try {
            classLoader = new URLClassLoader(new URL[]{f.toURI().toURL()});
            Class<?> cls = classLoader.loadClass(("extraClasses.Loadtest"));
            T retClass = (T) cls.getDeclaredConstructor().newInstance();
            return retClass;
        } catch (MalformedURLException | InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
