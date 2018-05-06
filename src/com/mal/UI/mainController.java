package com.mal.UI;

import bb_framework.BranchAndBound;
import bb_framework.enums.ConstraintType;
import bb_framework.enums.NodeStrategy;
import bb_framework.enums.ProblemType;
import bb_framework.interfaces.Bound;
import bb_framework.utils.Constraint;
import bb_framework.utils.Problem;
import bb_framework.utils.Result;
import utils.Compiler;
import utils.CustomClassloader;
import com.mal.UI.utils.listview_item;
import com.mal.UI.utils.resources;
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

import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;

public class mainController {
    HashMap<Integer,listview_item> list_items = new HashMap<>();

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

    @FXML
    private ProgressBar progBar;
    @FXML
    private Label progBarLab;

    @FXML
    private GridPane resultViewGrid;



    @FXML
    public void initialize(){
        if (res == null || res.length == 0){
            resultViewGrid.setDisable(true);
        }

        nXAxis.setLabel("Problem");
        nYAxis.setLabel("Nodes created");
        tXAxis.setLabel("Problem");
        tYAxis.setLabel("Seconds");
    }

    @FXML
    protected void handleListviewAddBtn(ActionEvent event){
        String s = changetf.getText();
        list_items.put(change_listview.getItems().size(),new listview_item(s));
        change_listview.getItems().add(change_listview.getItems().size(),s);
        changetf.setText("");
        change_listview.getSelectionModel().select(change_listview.getItems().size() - 1);
        change_anchorpane.setDisable(false);
    }

    private void updateResultView(Result result){
        if(res != null || res.length > 0){
            resultViewGrid.setDisable(false);
        }
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
        fileChooser.setInitialDirectory(new File(resources.filechooserpath));
        fileChooser.setTitle("Locate coefficient file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files","*.txt"));
        String s = fileChooser.showOpenDialog(change_anchorpane.getScene().getWindow()).getAbsolutePath();
        coefTF.setText(s);
    }

    @FXML
    protected void handleConsBrowseBtnClicked(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(resources.filechooserpath));
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
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Java files","*.java"),
                new FileChooser.ExtensionFilter("Class files", "*.class"));
        File s = fileChooser.showOpenDialog(change_anchorpane.getScene().getWindow());
        bndfiletv.setText((s!=null)?s.getAbsolutePath():"");
        listview_item item = list_items.get(change_listview.getSelectionModel().getSelectedIndex());
        item.setBound_file(bndfiletv.getText());
    }

    private void updateProgressString(int curr, int total){
        progBarLab.setText(String.format("Problem %d out of %d solved",curr, total));
    }

    private void updateProgressBar(double curr, double total){
        progBar.setProgress(curr/total);
    }

    private Constraint[] getConstraints(){
        if(consTF.getText().matches("^[a-zA-Z0-9:\\\\ ]*.txt$")){
            File file = new File(consTF.getText());
            if (file.exists()) {
                try {
                    ArrayList<Constraint> tmp = new ArrayList<>();
                    String st;
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    while((st = reader.readLine()) != null){
                        int i = 0;
                        double[] lhs = new double[Integer.valueOf(coefNrTF.getText())];
                        ConstraintType tp = null;
                        String[] string = st.split(" ");
                        while (string[i].matches("^[0-9]+[.0-9]*$")) {
                            lhs[i] = Double.valueOf(string[i]);
                            i++;
                        }
                        switch (string[i]){
                            case "<=":
                                tp = ConstraintType.LEQ;
                                break;
                            case ">=":
                                tp = ConstraintType.EQUALS;
                                break;
                            case "==":
                            case "=":
                                tp = ConstraintType.GEQ;
                                break;
                        }
                        i++;
                        tmp.add(new Constraint(lhs,Double.valueOf(string[i]),tp));
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
                //bnd = (Bound) CustomClassloader.loadObject(file);
            } catch (MalformedURLException | ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
            return bnd;
        }
        return null;
    }

    private NodeStrategy getStrategy(listview_item item){
        if(item.isStrat_bst()){
            return NodeStrategy.BEST_FIRST;
        }
        else if(item.isStrat_dpth()){
            return NodeStrategy.DEPTH_FIRST;
        }
        return NodeStrategy.BREADTH_FIRST;
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
        else if (coefTF.getText().matches("^[a-zA-Z0-9:\\\\ ]*.txt$")){
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
            bnb.Solve();
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
        for(listview_item lv:list_items.values()){
            if(lv.getBound_file().isEmpty()){
                ret = false;
                Alert alert = new Alert(Alert.AlertType.ERROR,String.format("Bound file of: %s must be set",lv.getName()), ButtonType.OK);
                alert.showAndWait();
            }
        }
        return ret;
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
            for(listview_item item:list_items.values()){
                Bound bounds = getBound(item.getBound_file());
                NodeStrategy strategy = getStrategy(item);

                double bval = (bvaltv.getText().equals(""))?0.5:(Double.valueOf(bvaltv.getText()));
                Problem tmp = new Problem(item.getName(),constraints,bounds,strategy,problemType,lpr_checkbox.isSelected(),bval);
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