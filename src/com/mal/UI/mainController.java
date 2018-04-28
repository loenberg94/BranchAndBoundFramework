package com.mal.UI;

import com.mal.UI.utils.FileLocater;
import com.mal.UI.utils.listview_item;
import com.mal.UI.utils.resources;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.HashMap;

public class mainController {
    HashMap<Integer,listview_item> list_items = new HashMap<>();

    @FXML
    private TextField bvaltv;

    @FXML
    private AnchorPane change_anchorpane;

    @FXML
    private TextField changetf;

    @FXML
    private TextField bndfiletv;

    @FXML
    private Button bndfile_browse_btn;

    @FXML
    private Button listview_add_btn;

    @FXML
    private CheckBox lpr_checkbox;

    @FXML
    private RadioButton strat_bst_radbtn;

    @FXML
    private RadioButton strat_dpth_radbtn;

    @FXML
    private RadioButton strat_brth_radbtn;

    @FXML
    private ListView change_listview;

    @FXML
    protected void handleListviewAddBtn(ActionEvent event){
        String s = changetf.getText();
        list_items.put(change_listview.getItems().size(),new listview_item(s));
        change_listview.getItems().add(change_listview.getItems().size(),s);
        changetf.setText("");
        change_anchorpane.setDisable(false);
    }

    @FXML
    protected void handleLPRelaxationCheckbox(ActionEvent event){
        if (lpr_checkbox.isSelected()){
            bvaltv.setDisable(false);
        }
        else{
            bvaltv.setDisable(true);
        }
        listview_item item = list_items.get(change_listview.getSelectionModel().getSelectedIndex());
        item.setBound_file(bndfiletv.getText());
        item.setBranch_val(bvaltv.getText());
        item.setLp_relaxation(lpr_checkbox.isSelected());
        item.setStrat_brth(strat_brth_radbtn.isSelected());
        item.setStrat_bst(strat_bst_radbtn.isSelected());
        item.setStrat_dpth(strat_dpth_radbtn.isSelected());
    }

    @FXML
    protected void handleListviewItemClicked(MouseEvent event){
        listview_item item = list_items.get(change_listview.getSelectionModel().getSelectedIndex());
        strat_brth_radbtn.setSelected(item.isStrat_brth());
        strat_bst_radbtn.setSelected(item.isStrat_bst());
        strat_dpth_radbtn.setSelected(item.isStrat_dpth());
        lpr_checkbox.setSelected(item.isLp_relaxation());
        bvaltv.setText(item.getBranch_val());
        bndfiletv.setText(item.getBound_file());
        bvaltv.setDisable(!item.isLp_relaxation());
    }

    @FXML
    protected void handleButtonChangeListener(ActionEvent event){
        listview_item item = list_items.get(change_listview.getSelectionModel().getSelectedIndex());
        item.setLp_relaxation(lpr_checkbox.isSelected());
        item.setStrat_brth(strat_brth_radbtn.isSelected());
        item.setStrat_bst(strat_bst_radbtn.isSelected());
        item.setStrat_dpth(strat_dpth_radbtn.isSelected());
    }

    @FXML
    protected void handleTextChangeListener(KeyEvent event){
        listview_item item = list_items.get(change_listview.getSelectionModel().getSelectedIndex());
        item.setBound_file(bndfiletv.getText());
        item.setBranch_val(bvaltv.getText());
    }

    @FXML
    protected void handleBrowseBtnClicked(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(resources.filechooserpath));
        fileChooser.setTitle("Locate bound file");
        String s = fileChooser.showOpenDialog(change_anchorpane.getScene().getWindow()).getAbsolutePath();
        bndfiletv.setText(s);
        listview_item item = list_items.get(change_listview.getSelectionModel().getSelectedIndex());
        item.setBound_file(bndfiletv.getText());
    }

}
