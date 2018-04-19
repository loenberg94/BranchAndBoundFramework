package com.mal.tests;

import com.mal.framework.enums.ConstraintType;
import com.mal.framework.enums.NodeStrategy;
import com.mal.framework.enums.ProblemType;
import com.mal.framework.interfaces.Bound;
import com.mal.framework.utils.Constraint;
import com.mal.framework.utils.Problem;

import java.util.HashMap;

public class Knapsack_8coef extends Problem {
    final static double[] leftHandSide = new double[] {25,35,45,5,25,3,2,2};
    final static double[] dataset      = new double[] {350,400,450,20,70,8,5,5};

    private Knapsack_8coef(Constraint[] constraints, Bound bound, NodeStrategy strategy, ProblemType type, boolean lp) {
        super(constraints, bound, strategy, type, lp, 0.5);
    }

    private static class knapsackBounds implements Bound {
        @Override
        public float Lowerbound(HashMap<Integer,Float> currentSolution, double[] set, Constraint[] constraints) {
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
        public float Upperbound(HashMap<Integer,Float> currentSolution, double[] set, Constraint[] constraints) {
            return 0;
        }
    }

    public static Knapsack_8coef CreateNew(){
        Constraint[] constraints = new Constraint[] {new Constraint(leftHandSide,104,ConstraintType.LEQ)};
        return new Knapsack_8coef(constraints, new knapsackBounds(),NodeStrategy.BEST_FIRST, ProblemType.MAXIMIZATION, true);
        
    }

    public double[] getDataset() {
        return dataset;
    }

    public Bound testBound(){
        return new knapsackBounds();
    }
}
