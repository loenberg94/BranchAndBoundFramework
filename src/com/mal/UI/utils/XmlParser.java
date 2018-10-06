package com.mal.UI.utils;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;

public class XmlParser {

    public static <T> void parseToFile(String filepath, T obj) throws FileNotFoundException {
        XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(filepath)));
        encoder.writeObject(obj);
        encoder.close();
    }

    public static <T> String parseToString(T obj){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLEncoder encoder = new XMLEncoder(baos);
        encoder.writeObject(obj);
        encoder.close();
        return baos.toString();
    }

    public static <T> T retrieveFromFile(String filepath) throws FileNotFoundException {
        XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(filepath)));
        return (T) decoder.readObject();
    }
}
