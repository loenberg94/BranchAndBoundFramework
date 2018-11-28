package com.mal.UI.utils;

public class JavaFile {
    String filename;
    String content;

    public JavaFile(String fn, String cnt){
        filename = fn;
        content = cnt;
    }

    public String getFilename() {
        return filename;
    }

    public String getContent() {
        return content;
    }
}
