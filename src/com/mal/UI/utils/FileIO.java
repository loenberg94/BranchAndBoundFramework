package com.mal.UI.utils;

import javax.swing.*;
import java.awt.*;
import java.io.*;

import static javax.swing.JOptionPane.YES_OPTION;

public class FileIO {

    private static final String localFileDirectory = System.getenv("APPDATA") + "\\BranchAndBound\\";

    private static boolean checkDirectoryPath(String path, boolean createIfNonExisting) {
        File tmp = new File(path);
        if(tmp.getParentFile().exists()){
            return true;
        }
        else {
            if(createIfNonExisting){
                tmp.getParentFile().mkdir();
                return true;
            }
            return false;
        }
    }

    private static String getFolder(int id) {
        switch (id){
            case 0:
                return "\\problems\\";
            case 1:
                return "\\classes\\";
            default:
                return "";
        }
    }

    public static void createFile(int folder, String filename, String fileContent, boolean cine) throws IOException {
        String path = localFileDirectory + getFolder(folder) + filename;
        if(checkDirectoryPath(path, cine)){
            File tmp = new File(path);
            BufferedWriter fw = new BufferedWriter(new FileWriter(path));
            if(tmp.exists()){
                int value = JOptionPane.showConfirmDialog(JOptionPane.getRootFrame(),"Overwrite existing file?","File already exists", JOptionPane.YES_NO_OPTION);
                if(value == YES_OPTION){
                    tmp.delete();
                    tmp.createNewFile();
                    fw.write(fileContent);
                    fw.close();
                }
            }
            else {
                fw.write(fileContent);
                fw.close();
            }
        }
    }

    public static String locateFile(Component component){

        return "";
    }

    public static String locateDirectory(Component component){

        return "";
    }

    public static String[] locateFiles(Component component){

        return new String[] {""};
    }
}
