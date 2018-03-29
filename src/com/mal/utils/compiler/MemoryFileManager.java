package com.mal.utils.compiler;

import javax.tools.*;
import java.io.*;
import java.net.URI;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.Map;

public class MemoryFileManager extends ForwardingJavaFileManager{
    private final static String EXT = ".java";
    private Map<String, byte[]> classbytes;

    public MemoryFileManager(StandardJavaFileManager standardJavaFileManager) {
        super(standardJavaFileManager);
        classbytes = new HashMap<>();
    }

    public Map<String, byte[]> getClassbytes() {
        return classbytes;
    }

    public void close() {
        classbytes = null;
    }

    private static class StringInputBuffer extends SimpleJavaFileObject{
        final String code;

        protected StringInputBuffer(String filename, String source) {
            super(toURI(filename), Kind.SOURCE);
            code = source;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
            return CharBuffer.wrap(code);
        }
    }

    private class ClassOutputBuffer extends SimpleJavaFileObject {
        private String name;

        protected ClassOutputBuffer(String name) {
            super(toURI(name), Kind.CLASS);
            this.name = name;
        }

        @Override
        public OutputStream openOutputStream() throws IOException {
            return new FilterOutputStream(new ByteArrayOutputStream()){
                public void close() throws IOException {
                    out.close();
                    ByteArrayOutputStream bos = (ByteArrayOutputStream) out;
                    classbytes.put(name, bos.toByteArray());
                }
            };
        }
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
        if (kind == JavaFileObject.Kind.CLASS){
            return new ClassOutputBuffer(className);
        }
        return super.getJavaFileForOutput(location,className,kind,sibling);
    }

    public JavaFileObject makeStringSource(String filename, String code){
        return new StringInputBuffer(filename, code);
    }

    static URI toURI(String name){
        File file = new File(name);
        if(file.exists()){
            return file.toURI();
        }else {
            try {
                StringBuilder sb = new StringBuilder();
                sb.append("mfm:///");
                sb.append(name.replace(".", "/"));
                if(name.endsWith(EXT)){
                    sb.replace(sb.length() - EXT.length(), sb.length(), EXT);
                }
                return URI.create(sb.toString());
            }
            catch (Exception e){
                return URI.create("mfm:///com/sun/script/java/java_source");
            }
        }
    }
}
