package com.mal;

import com.mal.UI.mainform;
import com.mal.framework.BranchAndBound;
import com.mal.framework.enums.NodeStrategy;
import com.mal.framework.interfaces.Bound;
import com.mal.framework.utils.Node;
import com.mal.framework.utils.Problem;
import com.mal.framework.utils.Result;
import com.mal.tests.Knapsack;
import com.mal.tests.Knapsack_24coef;
import com.mal.tests.Knapsack_7coef;
import com.mal.utils.compiler.Compiler;
import com.mal.utils.compiler.CustomClassloader;
import com.mal.utils.cplex.Cplex;
import javafx.util.Pair;

import javax.swing.*;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.Comparator;
import java.util.PriorityQueue;

public class Main {

    public static void main(String[] args) {
        /*JFrame frame = new JFrame("mainform");
        frame.setSize(new Dimension(600,400));
        frame.setContentPane(new mainform().mainpanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);*/

        Knapsack knapsack_bst  = Knapsack.CreateNew();
        Knapsack knapsack_dpth = Knapsack.CreateNew();
        knapsack_dpth.strategy = NodeStrategy.DEPTH_FIRST;
        Knapsack knapsack_brth = Knapsack.CreateNew();
        knapsack_brth.strategy = NodeStrategy.BREADTH_FIRST;
        /*Knapsack_24coef knapsack_24coef_bst = Knapsack_24coef.CreateNew();
        Knapsack_24coef knapsack_24coef_dpth = Knapsack_24coef.CreateNew();
        knapsack_24coef_dpth.strategy = NodeStrategy.DEPTH_FIRST;
        Knapsack_24coef knapsack_24coef_brth = Knapsack_24coef.CreateNew();
        knapsack_24coef_brth.strategy = NodeStrategy.BREADTH_FIRST;*/
        /*try {
            Cplex cplex = new Cplex();
            double[] resArr = cplex.ip_solve(knapsack_bst.getDataset(),knapsack_bst.getConstraints(),knapsack_bst.type);
            for(double res:resArr){
                System.out.printf("%f\n",res);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        /*BranchAndBound bnb = new BranchAndBound(new Problem[] {knapsack_bst,knapsack_dpth,knapsack_brth},knapsack_bst.getDataset());
        bnb.Solve();
        Result[] res = bnb.getResults();
        for(Result r:res){
            System.out.printf("\n\nStrategy: %s\n",r.getStrategy().toString());
            System.out.printf("\nObjective value: %f\n",r.getObjectiveValue());
            System.out.printf("time: %f seconds\n",r.getRuntime());
            System.out.printf("nodes: %d\n",r.getNr_of_nodes());
            System.out.printf("solution: ");
            for(int s:r.getSolution()){
                System.out.printf("%d ",s);
            }
        }*/
        /*try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        //String path = "C:\\Users\\loenb\\Documents\\Projects\\BB_Framework\\out\\production\\BB_Framework\\com\\mal\\tests\\loadtest.class";
        String path = "C:\\Users\\loenb\\Documents\\Projects\\BB_Framework\\testfiles\\Loadtest.java";
        File file = new File(path);
        /*Pair<Boolean, DiagnosticCollector<JavaFileObject>> t = Compiler.compileClass(new File(path));
        if(t.getKey()){
            System.out.printf("SUCCES");
        }
        else{
            System.out.printf("%b, %s", t.getKey(),t.getValue().getDiagnostics().get(0).getSource());
        }*/

        CustomClassloader.loadObject(file);
    }
}
