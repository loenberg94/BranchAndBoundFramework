package com.mal.UI;

import com.mal.UI.utils.FileLocater;

import javax.swing.*;

public class mainform {
    public JPanel mainpanel;
    private JTextField ds_file_tf;
    private JTextField cs_file_tf;
    private JButton ds_browse_btn;
    private JButton cs_browse_btn;
    private JPanel general_data;
    private JPanel special_data;
    private JTextField problem_tf;
    private JButton nextButton;
    private JList list1;

    public mainform() {
        ds_browse_btn.addActionListener(e -> ds_file_tf.setText(FileLocater.locateFile(mainpanel)));
        cs_browse_btn.addActionListener(e -> {
            String[] files = FileLocater.locateFiles(mainpanel);
            StringBuilder builder = new StringBuilder();
            for(int i = 0; i < files.length; i++){
                if(i == files.length - 1){
                    builder.append(files[i]);
                }
                else{
                    builder.append(files[i]);
                    builder.append(";");
                }
            }
            cs_file_tf.setText(builder.toString());
        });
    }
}
