package com.mal.UI.utils;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static javax.swing.JOptionPane.YES_OPTION;

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

        File tmp = new File(filepath);
        if(tmp.exists()){
            int choice = JOptionPane.showConfirmDialog(JOptionPane.getRootFrame(),"Overwrite existing file?","File already exists", JOptionPane.YES_NO_OPTION);
            if(choice == YES_OPTION){
                tmp.delete();
                ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(filepath));

                String settingsXML = settings.toXML();
                HashMap<String, File> boundJavaFiles = new HashMap<>();
                for(ProblemInstance instance:settings.instances){
                    if(!(instance.boundFile == "" || instance.boundFile == null)){
                        String bfName = instance.getFileName();
                        if(!boundJavaFiles.containsKey(bfName)){
                            boundJavaFiles.put(bfName,tmp);
                        }
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
        }
    }

    public BnbFile(String name){
        filename = name;
    }
}
