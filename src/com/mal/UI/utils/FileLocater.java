package com.mal.UI.utils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class FileLocater {
    public static String locateFile(Component component){
        JFileChooser chooser = new JFileChooser(resources.filechooserpath);
        if (chooser.showDialog(component, "Choose") == JFileChooser.APPROVE_OPTION){
            File file = chooser.getSelectedFile();
            return file.getAbsolutePath();
        }
        return "";
    }

    public static String[] locateFiles(Component component){
        JFileChooser chooser = new JFileChooser(resources.filechooserpath);
        chooser.setMultiSelectionEnabled(true);
        if (chooser.showDialog(component, "Choose") == JFileChooser.APPROVE_OPTION){
            File[] files = chooser.getSelectedFiles();
            String[] file_paths = new String[files.length];
            for(int i = 0; i < files.length; i++){
                file_paths[i] = files[i].getAbsolutePath();
            }
            return file_paths;
        }
        return new String[] {""};
    }
}
