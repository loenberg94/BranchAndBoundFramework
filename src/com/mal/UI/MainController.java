package com.mal.UI;

import bb_framework.BranchAndBound;
import bb_framework.enums.ConstraintType;
import bb_framework.enums.NodeStrategy;
import bb_framework.enums.ProblemType;
import bb_framework.interfaces.Bound;
import bb_framework.interfaces.Dataset;
import bb_framework.types.Coefficient;
import bb_framework.types.Index;
import bb_framework.types.Value;
import bb_framework.types.Vector;
import bb_framework.utils.Constraint;
import bb_framework.utils.Problem;
import bb_framework.utils.Result;
import com.mal.UI.utils.*;
import kotlin.text.Regex;
import utils.Compiler;
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
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.HashMap;

public class MainController {
    HashMap<Integer, ProblemInstance> list_items = new HashMap<>();
    MultiThreadCompiler multiThreadCompiler = new MultiThreadCompiler(list_items);

    int i = 0;
    Result[] res;

    String dataset;
    String constraints;

    @FXML
    private ComboBox pTypeCombobox;
    @FXML
    private TextField bvaltv;
    @FXML
    private AnchorPane change_anchorpane;
    @FXML
    private TextField changetf;
    @FXML
    private Label bndfiletv;
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
    private CheckBox compiledCB;


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
        ProblemInstance pi = new ProblemInstance(s);
        pi.getIsCompiled().addListener((observable, oldValue, newValue) -> {
            if(pi.equals(list_items.get(change_listview.getSelectionModel().getSelectedIndex()))){
                compiledCB.setSelected(newValue);
            }
        });
        list_items.put(change_listview.getItems().size(),pi);
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
        String s = FileIO.locateFile(change_anchorpane.getScene().getWindow(),"Locate coefficient file");
        dataset = getStringFromFile(s);
        coefTF.setText(s);
    }

    @FXML
    protected void handleConsBrowseBtnClicked(ActionEvent event){
        String s = FileIO.locateFile(change_anchorpane.getScene().getWindow(),"Locate constraint file");
        constraints = getStringFromFile(s);
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
        bndfiletv.setText(instance.getFileName());
        bvaltv.setDisable(!instance.lpRelaxation);
        compiledCB.setSelected(instance.getBound() != null);
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

    @SuppressWarnings("Duplicates")
    private JavaFile getFile(String path){
        JavaFile ret = null;
        try {
            BufferedReader stream = new BufferedReader(new FileReader(path));
            File tmp = new File(path);

            StringBuilder st = new StringBuilder();
            String line = null;

            boolean packageFound = false;

            while ((line = stream.readLine()) != null){
                if(packageFound){
                    st.append(line);
                }
                else{
                    if(line.matches("(package)[a-zA-Z0-9 ._;]*")){
                        packageFound = true;
                    } else if(line.matches("(import|public)[a-zA-Z0-9 ._;]*")){
                        packageFound = true;
                        st.append(line);
                    }
                }
            }
            ret = new JavaFile(tmp.getName(),st.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
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
        ProblemInstance instance = list_items.get(change_listview.getSelectionModel().getSelectedIndex());
        instance.boundFile = (s!=null)?s.getAbsolutePath():"";
        bndfiletv.setText(instance.getFileName());
        multiThreadCompiler.compile(change_listview.getSelectionModel().getSelectedIndex(),getFile(s.getAbsolutePath()));
    }

    private String getStringFromFile(String file) {
        if((new File(file)).exists()){
            try {
                StringBuilder sb = new StringBuilder();
                BufferedReader reader = null;
                reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null){
                    sb.append(line + "\n");
                }
                return sb.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return "";
            }
        }
        return "";
    }

    @FXML
    protected void handleImportBtnClicked(ActionEvent event){
        //TODO custom load and update methods required to load the BnbFile and update the UI
        String filepath = FileIO.locateFile(change_anchorpane.getScene().getWindow(),"Open BnB File");
        System.out.println(filepath);
    }

    private void updateListItems(){

    }

    //TODO: Migrate to only accepting Dataset and Constraints as text files not raw text-input
    //BnbFile will have to contain the added dataset and constraint text files as well

    @FXML
    protected void handleSaveBtnClicked(ActionEvent event){
        String filename = getFilename();
        //TODO: Error Handling
        //CASES WHERE WE WONT CREATE FILE:
        //) No Coefficients or Constraints specified
        //) No ProblemInstances in list_items
        if(filename != null && filename != ""){
            BnbFile bnbFile = new BnbFile(filename);
            ProblemInstance[] problemInstances = new ProblemInstance[list_items.size()];
            for(int i = 0; i < list_items.size(); i++){
                problemInstances[i] = list_items.get(i).copy();
            }
            Settings settings = new Settings(dataset,constraints, getProblemType(),problemInstances);
            try {
                bnbFile.write(settings);
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
        if(constraints != ""){
            try {
                ArrayList<Constraint> tmp = new ArrayList<>();
                String st;
                BufferedReader reader = new BufferedReader(new StringReader(constraints));
                while((st = reader.readLine()) != null){
                    int i = 0;
                    boolean i_constraint = false;
                    ConstraintType tp = null;
                    String[] string = st.split(" ");
                    ArrayList<Coefficient> lhs = new ArrayList<>(Integer.valueOf(coefNrTF.getText()));
                    if (st.matches("^(x[0-9]+( )*)* [<>=]+ [0-9]+$")){
                        i_constraint = true;
                        while(string[i].matches("x[0-9]+")){
                            int index = Integer.valueOf(string[i].substring(1));
                            lhs.add(new Index(index));
                            i++;
                        }
                    }
                    else{
                        while (string[i].matches("^[0-9]+[.0-9]*$")) {
                            lhs.add(new Value(Double.valueOf(string[i])));
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
                        tmp.add(new Constraint(lhs.toArray(new Index[]{}),Double.valueOf(string[i]),tp));
                    }
                    else{
                        tmp.add(new Constraint(lhs.toArray(new Value[]{}),Double.valueOf(string[i]),tp));
                    }
                    lhs = null;
                }
                return tmp.toArray(new Constraint[]{});
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
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

    @Deprecated
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

    protected Coefficient[] getValuesFromString(String[] st, int size){
        Coefficient[] tmp = new Coefficient[size];
        for(int i = 0; i < size; i++){
            tmp[i] = new Value(Double.valueOf(st[i]));
        }
        return tmp;
    }

    protected Dataset getDataset(){
        int size = Integer.valueOf(coefNrTF.getText());
        Coefficient[] tmp = null;

        if (dataset != ""){
            try {
                String line;
                BufferedReader reader = new BufferedReader(new StringReader(dataset));
                while((line = reader.readLine()) != null){
                    if(line.matches("^[0-9. ]*$")){
                        tmp = getValuesFromString(line.split(" "),size);
                    }
                    //TODO: throw exception if dataset isn't correct format
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new Vector(tmp);
        }
        return null;
    }

    class SolveThread extends Thread {
        private BranchAndBound bnb;

        public SolveThread(BranchAndBound branchAndBound){
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
            Dataset dataset = getDataset();

            int i = 0;
            for(ProblemInstance item:list_items.values()){
                Bound bounds = item.getBound();
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
            SolveThread thread = new SolveThread(branchAndBound);
            thread.start();
        }
    }
}