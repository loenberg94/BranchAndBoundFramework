package com.mal;

import com.mal.UI.mainform;
import com.mal.framework.BranchAndBound;
import com.mal.framework.enums.NodeStrategy;
import com.mal.framework.utils.Node;
import com.mal.framework.utils.Problem;
import com.mal.framework.utils.Result;
import com.mal.tests.Knapsack;
import com.mal.tests.Knapsack_24coef;
import com.mal.tests.Knapsack_7coef;
import com.mal.utils.cplex.Cplex;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
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

        Node tmp1 = new Node(null,0,true);
        Node tmp2 = new Node(tmp1,0,true);
        Node tmp3 = new Node(tmp2,0,true);
        Node tmp4 = new Node(tmp3,0,true);
        Node tmp5 = new Node(tmp1,0,true);
        Node tmp6 = new Node(tmp2,0,true);
        Node tmp7 = new Node(tmp3,0,true);

        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(o -> o.depth));
        queue.add(tmp1);
        queue.add(tmp2);
        queue.add(tmp3);
        queue.add(tmp4);
        queue.add(tmp5);
        queue.add(tmp6);
        queue.add(tmp7);
        while (queue.size() > 0){
            System.out.printf("depth: %d\n",queue.poll().depth);
        }
    }
}
