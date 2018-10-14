package com.mal.UI;

import bb_framework.BranchAndBound;
import bb_framework.enums.ConstraintType;
import bb_framework.enums.NodeStrategy;
import bb_framework.enums.ProblemType;
import bb_framework.interfaces.Bound;
import bb_framework.utils.Constraint;
import bb_framework.utils.Problem;
import bb_framework.utils.Result;
import com.mal.UI.utils.MultiThreadCompiler;
import com.mal.UI.utils.FileIO;
import utils.Compiler;
import com.mal.UI.utils.ProblemInstance;
import com.mal.UI.utils.Resources;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.util.Pair;

import javax.swing.*;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;

public class MainController {
    HashMap<Integer, ProblemInstance> list_items = new HashMap<>();
    MultiThreadCompiler multiThreadCompiler = new MultiThreadCompiler(list_items);

    int i = 0;
    Result[] res;

    @FXML
    private ComboBox pTypeCombobox;
    @FXML
    private TextField bvaltv;
    @FXML
    private AnchorPane change_anchorpane;
    @FXML
    private TextField changetf;
    @FXML
    private TextField bndfiletv;
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
    private BarChart<String,Integer> nodesBarChart;
    @FXML
    private CategoryAxis nXAxis;
    @FXML
    private NumberAxis nYAxis;

    @FXML
    private BarChart<String,Double> timeBarChart;
    @FXML
    private CategoryAxis tXAxis;
    @FXML
    private NumberAxis tYAxis;

    @FXML
    private Label resViewNameLab;
    @FXML
    private Label resViewStratLab;
    @FXML
    private Label resViewNodesLab;
    @FXML
    private Label resViewTimeLab;
    @FXML
    private Label resViewOVLab;
    @FXML
    private Button resPrevBtn;
    @FXML
    private Button resNextBtn;

    @FXML
    private TextField coefNrTF;

    @FXML
    private TextField consTF;
    @FXML
    private TextField coefTF;

    @SuppressWarnings("SpellCheckingInspection")
    @FXML
    private ProgressBar progBar;
    @SuppressWarnings("SpellCheckingInspection")
    @FXML
    private Label progBarLab;

    @FXML
    private GridPane resultViewGrid;



    @FXML
    public void initialize(){
        if (res == null || res.length == 0){
            resultViewGrid.setDisable(true);
        }
        resPrevBtn.setDisable(true);
        resNextBtn.setDisable(true);
        nXAxis.setLabel("Problem");
        nYAxis.setLabel("Nodes created");
        tXAxis.setLabel("Problem");
        tYAxis.setLabel("Seconds");
    }

    @FXML
    protected void handleListviewAddBtn(ActionEvent event){
        String s = changetf.getText();
        list_items.put(change_listview.getItems().size(),new ProblemInstance(s));
        change_listview.getItems().add(change_listview.getItems().size(),s);
        changetf.setText("");
        change_listview.getSelectionModel().select(change_listview.getItems().size() - 1);
        change_anchorpane.setDisable(false);
    }

    private void updateResultView(Result result){
        if(res != null || res.length > 0){
            resultViewGrid.setDisable(false);
        }
        if(res.length > 1 && i != res.length - 1) resNextBtn.setDisable(false);
        resViewNameLab.setText(result.getP_name());
        resViewStratLab.setText(result.getStrategy().toString());
        resViewNodesLab.setText(String.valueOf(result.getNr_of_nodes()));
        resViewTimeLab.setText(String.valueOf(result.getRuntime()));
        resViewOVLab.setText(String.valueOf(result.getObjectiveValue()));
    }

    private void updateNodesBarchart(Result[] res){
        XYChart.Series<String,Integer> series = new XYChart.Series<>();
        for(Result r:res){
            series.getData().add(new XYChart.Data(r.getP_name(),r.getNr_of_nodes()));
        }
        nodesBarChart.getData().add(series);
        for (XYChart.Series<String, Integer> s : nodesBarChart.getData()) {
            for (XYChart.Data<String, Integer> d : s.getData()) {
                Tooltip.install(d.getNode(), new Tooltip(String.valueOf(d.getYValue())));

                //Adding class on hover
                d.getNode().setOnMouseEntered(event -> d.getNode().getStyleClass().add("onHover"));

                //Removing class on exit
                d.getNode().setOnMouseExited(event -> d.getNode().getStyleClass().remove("onHover"));
            }
        }
    }

    @FXML
    protected void handleCoefBrowseBtnClicked(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(Resources.filechooserpath));
        fileChooser.setTitle("Locate coefficient file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files","*.txt"));
        String s = fileChooser.showOpenDialog(change_anchorpane.getScene().getWindow()).getAbsolutePath();
        coefTF.setText(s);
    }

    @FXML
    protected void handleConsBrowseBtnClicked(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(Resources.filechooserpath));
        fileChooser.setTitle("Locate constraint file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files","*.txt"));
        String s = fileChooser.showOpenDialog(change_anchorpane.getScene().getWindow()).getAbsolutePath();
        consTF.setText(s);
    }

    private void updateTimeBarchart(Result[] res){
        XYChart.Series<String,Double> series = new XYChart.Series<>();
        for(Result r:res){
            series.getData().add(new XYChart.Data(r.getP_name(),r.getRuntime()));
        }
        timeBarChart.getData().add(series);
        for (XYChart.Series<String, Double> s : timeBarChart.getData()) {
            for (XYChart.Data<String, Double> d : s.getData()) {
                Tooltip.install(d.getNode(), new Tooltip(String.valueOf(d.getYValue())));

                //Adding class on hover
                d.getNode().setOnMouseEntered(event -> d.getNode().getStyleClass().add("onHover"));

                //Removing class on exit
                d.getNode().setOnMouseExited(event -> d.getNode().getStyleClass().remove("onHover"));
            }
        }
    }

    @FXML
    protected void handlePrevResBtnClicked(ActionEvent event){
        i--;
        if(i == 0){
            resPrevBtn.setDisable(true);
        }
        if(i < res.length - 2){
            resNextBtn.setDisable(false);
        }
        updateResultView(res[i]);
    }

    @FXML
    protected void handleNextResBtnClicked(ActionEvent event){
        i++;
        if(i == res.length - 1){
            resNextBtn.setDisable(true);
        }
        if(i > 0){
            resPrevBtn.setDisable(false);
        }
        updateResultView(res[i]);
    }

    @FXML
    protected void handleLPRelaxationCheckbox(ActionEvent event){
        if (lpr_checkbox.isSelected()){
            bvaltv.setDisable(false);
        }
        else{
            bvaltv.setDisable(true);
        }
        ProblemInstance instance = list_items.get(change_listview.getSelectionModel().getSelectedIndex());
        instance.lpRelaxation = lpr_checkbox.isSelected();
    }

    @FXML
    protected void handleListviewItemClicked(MouseEvent event){
        ProblemInstance instance = list_items.get(change_listview.getSelectionModel().getSelectedIndex());
        switch (instance.strategy){
            case BEST_FIRST:
                strat_bst_radbtn.setSelected(true);
                break;
            case DEPTH_FIRST:
                strat_dpth_radbtn.setSelected(true);
                break;
            case BREADTH_FIRST:
                strat_brth_radbtn.setSelected(true);
                break;
        }

        lpr_checkbox.setSelected(instance.lpRelaxation);
        bvaltv.setText(String.valueOf(instance.branchValue));
        bndfiletv.setText(instance.boundFile);
        bvaltv.setDisable(!instance.lpRelaxation);
    }

    @FXML
    protected void handleButtonChangeListener(ActionEvent event){
        ProblemInstance instance = list_items.get(change_listview.getSelectionModel().getSelectedIndex());
        Control control = (Control) event.getSource();
        switch (control.getId()){
            case "lpr_checkbox":
                instance.lpRelaxation = lpr_checkbox.isSelected();
                break;
            case "strat_brth_radbtn":
                instance.strategy = NodeStrategy.BREADTH_FIRST;
                break;
            case "strat_bst_radbtn":
                instance.strategy = NodeStrategy.BEST_FIRST;
                break;
            case "strat_dpth_radbtn":
                instance.strategy = NodeStrategy.DEPTH_FIRST;
                break;
            default:
                break;
        }
    }

    @FXML
    protected void handleTextChangeListener(KeyEvent event){
        ProblemInstance instance = list_items.get(change_listview.getSelectionModel().getSelectedIndex());
        switch (((TextField) event.getSource()).getId()){
            case "bndfiletv":
                instance.boundFile = bndfiletv.getText();
                break;
            case "bvaltv":
                instance.branchValue = Double.valueOf(bvaltv.getText());
                break;
            default:
                break;
        }
    }

    @FXML
    protected void handleBrowseBtnClicked(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(Resources.filechooserpath));
        fileChooser.setTitle("Locate bound file");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Java files","*.java"),
                new FileChooser.ExtensionFilter("Class files", "*.class"));
        File s = fileChooser.showOpenDialog(change_anchorpane.getScene().getWindow());
        bndfiletv.setText((s!=null)?s.getAbsolutePath():"");
        ProblemInstance instance = list_items.get(change_listview.getSelectionModel().getSelectedIndex());
        instance.boundFile = bndfiletv.getText();
    }

    @FXML
    protected void handleImportBtnClicked(ActionEvent event){

    }

    @FXML
    protected void handleSaveBtnClicked(ActionEvent event){
        String filename = getFilename();
        //TODO: Does not work as intended yet - BNB ZIP FILE INSTEAD
        if(filename != null && filename != ""){
            filename = filename + ".bnb";
            try {
                FileIO.createFile(0,filename, "",true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    protected void handleExportBtnClicked(ActionEvent event){

    }




    private void updateProgressString(int curr, int total){
        progBarLab.setText(String.format("Problem %d out of %d solved",curr, total));
    }

    private void updateProgressBar(double curr, double total){
        progBar.setProgress(curr/total);
    }

    private Constraint[] getConstraints(){
        if(consTF.getText().matches("^[a-zA-Z0-9:_\\\\ ]*.txt$")){
            File file = new File(consTF.getText());
            if (file.exists()) {
                try {
                    ArrayList<Constraint> tmp = new ArrayList<>();
                    String st;
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    while((st = reader.readLine()) != null){
                        int i = 0;
                        boolean i_constraint = false;
                        ConstraintType tp = null;
                        String[] string = st.split(" ");
                        double[] d_lhs = new double[Integer.valueOf(coefNrTF.getText())];
                        ArrayList<String> s_lhs = new ArrayList<>();
                        if (st.matches("^(x[0-9]+( )*)* [<>=]+ [0-9]+$")){
                            i_constraint = true;
                            while(string[i].matches("x[0-9]+")){
                                s_lhs.add(string[i].replace("x",""));
                                i++;
                            }
                        }
                        else{
                            while (string[i].matches("^[0-9]+[.0-9]*$")) {
                                d_lhs[i] = Double.valueOf(string[i]);
                                i++;
                            }
                        }

                        switch (string[i]){
                            case "<=":
                                tp = ConstraintType.LEQ;
                                break;
                            case "<":
                                tp = ConstraintType.LT;
                                break;
                            case ">":
                                tp = ConstraintType.GT;
                                break;
                            case ">=":
                                tp = ConstraintType.GEQ;
                                break;
                            case "==":
                            case "=":
                                tp = ConstraintType.EQUALS;
                                break;
                        }
                        i++;
                        if(i_constraint){
                            tmp.add(new Constraint(s_lhs.toArray(new String[]{}),Double.valueOf(string[i]),tp,i_constraint));
                        }
                        else{
                            tmp.add(new Constraint(d_lhs,Double.valueOf(string[i]),tp,i_constraint));
                            s_lhs = null;
                        }

                    }
                    return tmp.toArray(new Constraint[]{});
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private File formatFileStructure(File org){
        try {
            StringBuilder org_f = new StringBuilder();
            BufferedReader bfr = new BufferedReader(new FileReader(org));
            String st;
            while ((st = bfr.readLine())!= null) {
                if(st.contains("package")){
                }
                else{
                    org_f.append(st + "\n");
                }
            }

            File out = new File("bound/" + org.getName());
            if(out.getParentFile().exists() || out.getParentFile().mkdir()){
                Writer writer = null;
                try {
                    writer = new FileWriter(out);
                    writer.write(org_f.toString());
                    writer.flush();
                } finally {
                    writer.close();
                }
            }
            return out;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Bound getBound(String filename){
        File file = formatFileStructure(new File(filename));
        Pair<Boolean, DiagnosticCollector<JavaFileObject>> res = Compiler.compileClass(file);
        if(res.getKey()){
            Bound bnd = null;
            try {
                URLClassLoader classLoader = new URLClassLoader(new URL[]{new File("./").toURI().toURL()});
                String cName = (file.getName().replace(".java",""));
                Class<?> cls = classLoader.loadClass(cName);
                bnd = (Bound) cls.getDeclaredConstructor().newInstance();
            } catch (MalformedURLException | ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
            return bnd;
        }
        return null;
    }

    private NodeStrategy getStrategy(ProblemInstance item){
        return item.strategy;
    }

    private ProblemType getProblemType(){
        if(pTypeCombobox.getSelectionModel().getSelectedItem().equals("Maximization")){
            return ProblemType.MAXIMIZATION;
        }
        return ProblemType.MINIMIZATION;
    }

    private double[] getValuesFromString(String[] st, int size){
        double[] tmp = new double[size];
        for(int i = 0; i < size; i++){
            tmp[i] = Double.valueOf(st[i]);
        }
        return tmp;
    }

    private double[] getDataset(){
        int size = Integer.valueOf(coefNrTF.getText());
        double[] tmp = null;
        if (coefTF.getText().matches("^[0-9. ]*$")){
            String[] coefs = coefTF.getText().split(" ");
            if(size == coefs.length){
                tmp = getValuesFromString(coefs,size);
                return tmp;
            }
            Alert alert = new Alert(Alert.AlertType.ERROR,"Number of coeffiecients specified, and number of actual coefficients dosn't correspond",ButtonType.OK);
            alert.showAndWait();
        }
        else if (coefTF.getText().matches("^[a-zA-Z0-9_:\\\\ ]*.txt$")){
            File file = new File(coefTF.getText());
            if(file.exists()){
                try {
                    String line;
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    while((line = reader.readLine()) != null){
                        if(line.matches("^[0-9. ]*$")){
                            tmp = getValuesFromString(line.split(" "),size);
                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return tmp;
            }
            Alert alert = new Alert(Alert.AlertType.ERROR,String.format("File: %s, doesn't exist.",coefTF.getText()),ButtonType.OK);
            alert.showAndWait();
        }
        return null;
    }

    class solveThread extends Thread {
        private BranchAndBound bnb;

        public solveThread(BranchAndBound branchAndBound){
            this.bnb = branchAndBound;
        }

        @Override
        public void run() {
            bnb.Solve(null);
            res = bnb.getResults();
            Platform.runLater(() -> {
                updateResultView(res[0]);
                updateTimeBarchart(res);
                updateNodesBarchart(res);
            });
        }
    }

    private boolean checkUI(){
        boolean ret = true;
        if(pTypeCombobox.getSelectionModel().getSelectedItem() == null){
            ret = false;
            Alert alert = new Alert(Alert.AlertType.ERROR,"Problemtype must be set", ButtonType.OK);
            alert.showAndWait();
        }
        if(coefNrTF.getText().equals("")){
            ret = false;
            Alert alert = new Alert(Alert.AlertType.ERROR,"Coefficient number must be set", ButtonType.OK);
            alert.showAndWait();
        }
        if(coefTF.getText().equals("")){
            ret = false;
            Alert alert = new Alert(Alert.AlertType.ERROR,"Coefficients must be set", ButtonType.OK);
            alert.showAndWait();
        }
        if(consTF.getText().equals("")){
            ret = false;
            Alert alert = new Alert(Alert.AlertType.ERROR,"Constraints must be set", ButtonType.OK);
            alert.showAndWait();
        }
        for(ProblemInstance lv:list_items.values()){
            if(lv.boundFile.isEmpty()){
                ret = false;
                Alert alert = new Alert(Alert.AlertType.ERROR,String.format("Bound file of: %s must be set",lv.name), ButtonType.OK);
                alert.showAndWait();
            }
        }
        return ret;
    }

    private String getFilename(){
        return JOptionPane.showInputDialog(
                JOptionPane.getRootFrame(),
                "Specify filename",
                "Input box",
                JOptionPane.PLAIN_MESSAGE
        );
    }

    @FXML
    protected void handleRunBtnClicked(ActionEvent event){
        if(checkUI()){
            BranchAndBound branchAndBound;
            updateProgressString(0,list_items.size());
            updateProgressBar(0,list_items.size());

            Constraint[] constraints = getConstraints();
            ProblemType problemType = getProblemType();
            Problem[] problems = new Problem[list_items.size()];
            double[] dataset = getDataset();

            int i = 0;
            for(ProblemInstance item:list_items.values()){
                Bound bounds = getBound(item.boundFile);
                NodeStrategy strategy = getStrategy(item);

                double bval = (bvaltv.getText().equals(""))?0.5:(Double.valueOf(bvaltv.getText()));
                Problem tmp = new Problem(item.name,constraints,bounds,strategy,problemType,lpr_checkbox.isSelected(),bval);
                problems[i] = tmp;
                tmp = null;
                i++;
            }

            branchAndBound = new BranchAndBound(problems,dataset);
            branchAndBound.getResultsSolvedProperty().addListener((observable, oldValue, newValue) -> {
                Platform.runLater(() -> {
                    updateProgressString(newValue.intValue(),list_items.size());
                    updateProgressBar(newValue.doubleValue(),list_items.size());
                });
            });
            solveThread thread = new solveThread(branchAndBound);
            thread.start();
        }
    }
}