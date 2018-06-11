package com.mal.tests;

import bb_framework.enums.ConstraintType;
import bb_framework.enums.NodeStrategy;
import bb_framework.enums.ProblemType;
import bb_framework.interfaces.Bound;
import bb_framework.utils.Constraint;
import bb_framework.utils.Node;
import bb_framework.utils.Problem;

import java.util.HashMap;

public class Knapsack_24coef extends Problem {

    final static double[] leftHandSide = new double[] {382745,799601,909247,729069,467902,44328,34610,698150,823460,
            903959,853665,551830,610856,670702,488960,951111,323046,446298,931161,31385,496951,264724,224916,169684};
    final static double[] dataset      = new double[] {825594,
            1677009,
            1676628,
            1523970,
            943972,
            97426,
            69666,
            1296457,
            1679693,
            1902996,
            1844992,
            1049289,
            1252836,
            1319836,
            953277,
            2067538,
            675367,
            853655,
            1826027,
            65731,
            901489,
            577243,
            466257,
            369261};

    private Knapsack_24coef(Constraint[] constraints, Bound bound, NodeStrategy strategy, ProblemType type, boolean lp) {
        super("",constraints, bound, strategy, type, lp, 0.5);
    }

    private static class knapsackBounds implements Bound {

        @Override
        public double Lowerbound(Node node, double[] set, Problem problem) {
            float sum = 0;
            float weight = 0;
            for (int cs_i:node.getCurrentSolution().keySet()){
                sum += node.getCurrentSolution().get(cs_i) * set[cs_i];
                weight += node.getCurrentSolution().get(cs_i) * problem.getConstraints()[0].getD_lhs()[cs_i];
            }
            for(int i = 0; i < set.length; i++){
                if(!node.getCurrentSolution().containsKey(i)){
                    if(weight + problem.getConstraints()[0].getD_lhs()[i] <= problem.getConstraints()[0].getRhs()){
                        sum += set[i];
                        weight += problem.getConstraints()[0].getD_lhs()[i];
                    }
                }
            }
            return sum;
        }

        @Override
        public double Upperbound(Node node, double[] set, Problem problem) {
            return 0;
        }
    }

    public static Knapsack_24coef CreateNew(){
        Constraint[] constraints = new Constraint[] {new Constraint(leftHandSide,6404180,ConstraintType.LEQ, false)};
        return new Knapsack_24coef(constraints, new knapsackBounds(),NodeStrategy.BEST_FIRST, ProblemType.MAXIMIZATION, true);
    }

    public double[] getDataset() {
        return dataset;
    }

    public Bound getBound(){
        return new knapsackBounds();
    }
}
