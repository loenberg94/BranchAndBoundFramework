package utils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class CustomClassloader {

    public static <T> T loadObject(File file){
        URLClassLoader classLoader;
        try {
            classLoader = new URLClassLoader(new URL[]{new File("./").toURI().toURL()});
            String cName = (file.getName().replace(".java",""));
            Class<?> cls = classLoader.loadClass(cName);
            return (T) cls.getDeclaredConstructor().newInstance();
        } catch (MalformedURLException | InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
