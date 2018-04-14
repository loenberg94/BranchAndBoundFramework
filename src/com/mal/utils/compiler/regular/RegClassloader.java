package com.mal.utils.compiler.regular;

import com.mal.framework.abstract_classes.Dataset;
import com.mal.framework.interfaces.Bound;
import com.mal.framework.interfaces.Constraint;
import com.mal.framework.interfaces.ObjectiveFunction;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

@SuppressWarnings("FieldCanBeLocal")
public class RegClassloader {
    private ObjectiveFunction oF;
    private ArrayList<Constraint> cs;
    private Dataset data;

    public void loadGeneralClasses(File root) throws MalformedURLException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        String[] files = root.list();
        if(files.length > 0) {
            URLClassLoader classLoader = new URLClassLoader(new URL[] {root.toURI().toURL()});
            for(String file:files){
                String[] tmp = file.split("\\.");
                if (tmp.length > 1){
                    Class<?> cls = Class.forName(tmp[0], true, classLoader);
                    if (tmp[0].equals("of")){
                        this.oF = (ObjectiveFunction) cls.getConstructor().newInstance();
                    }
                    else if (tmp[0].startsWith("cs_")){
                        Constraint tmp_c = (Constraint) cls.getConstructor().newInstance();
                        this.cs.add(tmp_c);
                    }
                    else if (tmp[0].equals("data")){
                        this.data = (Dataset) cls.getConstructor().newInstance();
                    }
                }
            }
        }
    }

    public Bound loadBoundClass(File root) throws MalformedURLException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        URLClassLoader classLoader = new URLClassLoader(new URL[] {root.toURI().toURL()});
        String[] tmp = root.getName().split("\\.");
        Class<? extends Bound> cls = (Class<? extends Bound>) Class.forName(tmp[0], true, classLoader);
        return cls.getConstructor().newInstance();
    }
}
