package com.mal;

import bb_framework.BranchAndBound;
import bb_framework.enums.NodeStrategy;
import bb_framework.utils.Cplex;
import bb_framework.utils.Problem;
import bb_framework.utils.Result;
import bb_framework.testFiles.Knapsack;
import com.mal.UI.utils.BnbFile;
import com.mal.UI.utils.ProblemInstance;
import com.mal.UI.utils.Settings;

import java.io.IOException;
import java.util.HashMap;

public class Main {

    private static void testFramework(){
        Knapsack knapsack_bst  = Knapsack.CreateNew();
        Knapsack knapsack_dpth = Knapsack.CreateNew();
        knapsack_dpth.strategy = NodeStrategy.DEPTH_FIRST;
        Knapsack knapsack_brth = Knapsack.CreateNew();
        knapsack_brth.strategy = NodeStrategy.BREADTH_FIRST;
        try {
            Cplex cplex = new Cplex();
            double[] resArr = cplex.ip_solve(knapsack_bst.getDataset(),new HashMap<>(),knapsack_bst.getConstraints(),knapsack_bst.type);
            for(double res:resArr){
                System.out.printf("%d ", (int)res);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        BranchAndBound bnb = new BranchAndBound(new Problem[] {knapsack_bst,knapsack_dpth,knapsack_brth},knapsack_bst.getDataset());
        bnb.Solve(null);
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
        }
    }

    private static void testBnbFile() {
        ProblemInstance[] instances = new ProblemInstance[]{
                new ProblemInstance("BestFist",NodeStrategy.BEST_FIRST,true,"C:\\Users\\loenb\\Desktop\\test files\\tspbounds.java"),
                new ProblemInstance("DepthFirst", NodeStrategy.DEPTH_FIRST, true, "C:\\Users\\loenb\\Desktop\\test files\\tspbounds.java"),
                new ProblemInstance("BreadthFirst", NodeStrategy.BREADTH_FIRST, false, "C:\\Users\\loenb\\Desktop\\test files\\knapsack_bounds.java")
        };
        Settings settings = new Settings("5 3 2 5 6 7 8 5 3","3 4 5 6 7 8 2 3 <= 45",instances);

        BnbFile testFile = new BnbFile("test");
        try {
            testFile.write(settings);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        testBnbFile();
    }
}
