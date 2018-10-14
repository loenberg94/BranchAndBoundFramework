package com.mal.UI.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class BnbFile {
    private final String localFileDirectory = System.getenv("APPDATA") + "\\BranchAndBound\\Problems\\";

    String filename;

    ArrayList<ZipEntry> files;

    private Settings load(String path){
        throw new UnsupportedOperationException();
    }

    public void write(Settings settings) throws IOException {
        String filepath = localFileDirectory + filename + ".bnb";
        FileIO.checkDirectoryPath(filepath,true);
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(filepath));

        String settingsXML = settings.toXML();
        HashMap<String, File> boundJavaFiles = new HashMap<>();
        for(ProblemInstance instance:settings.instances){
            File tmp = new File(instance.boundFile);
            if(!boundJavaFiles.containsKey(tmp.getName())){
                boundJavaFiles.put(tmp.getName(),tmp);
            }
        }

        ZipEntry settingsEntry = new ZipEntry("settings.xml");
        zos.putNextEntry(settingsEntry);
        zos.write(settingsXML.getBytes());

        for(String file:boundJavaFiles.keySet()){
            ZipEntry javaZipEntry = new ZipEntry(file);
            zos.putNextEntry(javaZipEntry);

            File javaInFile = boundJavaFiles.get(file);
            FileInputStream fis = new FileInputStream(javaInFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) >= 0){
                zos.write(buffer,0,length);
            }
            fis.close();
        }
        zos.close();
    }

    public BnbFile(String name){
        filename = name;
    }
}
