package com.mal.tests;

import bb_framework.enums.ConstraintType;
import bb_framework.enums.NodeStrategy;
import bb_framework.enums.ProblemType;
import bb_framework.interfaces.Bound;
import bb_framework.utils.Constraint;
import bb_framework.utils.Problem;

import java.util.HashMap;

public class Knapsack_7coef extends Problem {
    final static double[] leftHandSide = new double[] {41,50,49,59,55,57,60};
    final static double[] dataset      = new double[] {442,525,511,593,546,564,617};

    private Knapsack_7coef(Constraint[] constraints, Bound bound, NodeStrategy strategy, ProblemType type, boolean lp) {
        super("",constraints, bound, strategy, type, lp, 0.5);
    }

    private static class knapsackBounds implements Bound {
        @Override
        public double Lowerbound(HashMap<Integer,Double> currentSolution, double[] set, Constraint[] constraints) {
            float sum = 0;
            double weight = 0;
            for (int cs_i:currentSolution.keySet()){
                sum += currentSolution.get(cs_i) * set[cs_i];
                weight += currentSolution.get(cs_i) * constraints[0].getLhs()[cs_i];
            }
            for(int i = 0; i < set.length; i++){
                if(!currentSolution.containsKey(i)){
                    if(weight + constraints[0].getLhs()[i] <= constraints[0].getRhs()){
                        sum += set[i];
                        weight += constraints[0].getLhs()[i];
                    }
                }
            }
            return sum;
        }

        @Override
        public double Upperbound(HashMap<Integer,Double> currentSolution, double[] set, Constraint[] constraints) {
            return 0;
        }
    }

    public static Knapsack_7coef CreateNew(){
        Constraint[] constraints = new Constraint[] {new Constraint(leftHandSide,170,ConstraintType.LEQ)};
        return new Knapsack_7coef(constraints, new knapsackBounds(),NodeStrategy.BEST_FIRST, ProblemType.MAXIMIZATION, true);
        
    }

    public double[] getDataset() {
        return dataset;
    }

    public Bound testBound(){
        return new knapsackBounds();
    }
}
