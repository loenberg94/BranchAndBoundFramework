package com.mal.tests;

import bb_framework.enums.ConstraintType;
import bb_framework.enums.NodeStrategy;
import bb_framework.enums.ProblemType;
import bb_framework.interfaces.Bound;
import bb_framework.utils.Constraint;
import bb_framework.utils.Problem;

import java.util.HashMap;

public class Knapsack extends Problem {

    //final static double[] leftHandSide = new double[] {70,73,77,80,82,87,90,94,98,106,110,113,115,118,120};
    public final static double[] leftHandSide = new double[] {120,118,115,113,110,106,98,94,90,87,82,80,77,73,70};
    //final static double[] dataset      = new double[] {135,139,149,150,156,163,173,184,192,201,210,214,221,229,240};
    final static double[] dataset      = new double[] {240,229,221,214,210,201,192,184,173,163,156,150,149,139,135};

    private Knapsack(Constraint[] constraints, Bound bound, NodeStrategy strategy, ProblemType type, boolean lp) {
        super("",constraints, bound, strategy, type, lp, 0.5);
    }

    public static class knapsackBounds implements Bound {
        @Override
        public double Lowerbound(HashMap<Integer,Double> currentSolution, double[] set, Constraint[] constraints) {
            float sum = 0;
            float weight = 0;
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

    public static Knapsack CreateNew(){
        Constraint[] constraints = new Constraint[] {new Constraint(leftHandSide,750,ConstraintType.LEQ)};
        return new Knapsack(constraints, new knapsackBounds(),NodeStrategy.BEST_FIRST, ProblemType.MAXIMIZATION, true);
    }

    public double[] getDataset() {
        return dataset;
    }

    public Bound getBound(){
        return new knapsackBounds();
    }
}