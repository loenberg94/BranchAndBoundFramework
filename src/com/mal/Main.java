package com.mal;

import bb_framework.enums.NodeStrategy;
import com.mal.tests.Knapsack;
import com.mal.utils.compiler.CustomClassloader;

import java.io.File;

public class Main {

    public static void main(String[] args) {
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
