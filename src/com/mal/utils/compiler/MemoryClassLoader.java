package com.bb.utils.compiler;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class MemoryClassLoader extends URLClassLoader{
    private Map<String, byte[]> classbytes;

    public MemoryClassLoader(Map<String, byte[]> classBytes, String classpath, ClassLoader parent) {
        super(toURLs(classpath), parent);
        this.classbytes = classBytes;
    }

    public MemoryClassLoader(Map<String, byte[]> classBytes, String classpath){
        this(classBytes, classpath, ClassLoader.getSystemClassLoader());
    }

    public MemoryClassLoader(Map<String, byte[]> classBytes){
        this(classBytes, null, ClassLoader.getSystemClassLoader());
    }

    public Class load(String classname) throws ClassNotFoundException{
        return loadClass(classname);
    }

    public Iterable<Class> loadAll() throws ClassNotFoundException{
        List<Class> classes = new ArrayList<>(classbytes.size());
        for(String name : classbytes.keySet()){
            classes.add(loadClass(name));
        }
        return classes;
    }

    protected Class findClass(String name) throws ClassNotFoundException {
        byte[] buf = classbytes.get(name);
        if (buf != null){
            classbytes.put(name,null);
            return defineClass(name, buf, 0, buf.length);
        }
        else{
            return super.findClass(name);
        }
    }

    private static URL[] toURLs(String classpath){
        if (classpath == null){
            return new URL[0];
        }

        List<URL> list = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(classpath, File.pathSeparator);
        while (st.hasMoreTokens()){
            String token = st.nextToken();
            File file = new File(token);
            if(file.exists()){
                try{
                    list.add(file.toURI().toURL());
                } catch (MalformedURLException e){ }
            }
            else{
                try{
                    list.add(new URL(token));
                } catch (MalformedURLException e){ }
            }
        }
        URL[] res = new URL[list.size()];
        list.toArray(res);
        return res;
    }
}
