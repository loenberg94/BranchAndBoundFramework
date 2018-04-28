package com.mal.framework.utils;

import com.mal.framework.enums.NodeStrategy;
import com.mal.framework.enums.ProblemType;
import com.mal.framework.interfaces.Bound;
import com.mal.utils.cplex.Cplex;

public class Problem {
    private Constraint[] constraints;
    private Bound bounds;

    private final boolean LP_Relaxation;
    private final double lp_branch_condition_val;

    public NodeStrategy strategy;
    public final ProblemType type;

    public double[] Lowerbound(Node node, double[] set){
        if (node.depth == set.length){
            node.lowerbound = node.getObjectiveValue(set);
            return null;
        }

        if (!LP_Relaxation || type.equals(ProblemType.MAXIMIZATION)){
            node.lowerbound = bounds.Lowerbound(node.getCurrentSolution(),set,constraints);
        }
        else {
            try {
                Cplex cplex = new Cplex();
                double[] retArr = cplex.lp_relaxation(set,node.getCurrentSolution(),constraints,type);
                if(retArr != null){
                    node.lowerbound = calculateBound(retArr, set);
                    node.setCurrentBoundSolution(retArr);
                }
                cplex.release();
                cplex = null;
                return retArr;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public double[] Upperbound(Node node, double[] set){
        if (node.depth == set.length){
            node.upperbound = node.getObjectiveValue(set);
            return null;
        }

        if (!LP_Relaxation || type.equals(ProblemType.MINIMIZATION)){
            node.upperbound = bounds.Upperbound(node.getCurrentSolution(),set,constraints);
        }
        else {
            try {
                Cplex cplex = new Cplex();
                double[] retArr = cplex.lp_relaxation(set,node.getCurrentSolution(),constraints,type);
                if (retArr != null){
                    node.upperbound = calculateBound(retArr,set);
                    node.setCurrentBoundSolution(retArr);
                }
                cplex.release();
                cplex = null;
                return retArr;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private float calculateBound(double[] solution, double[] set){
        float sum = 0;
        for(int i = 0; i < set.length; i++){
            sum += solution[i] * set[i];
        }
        return sum;
    }

    public int getBranchIndex(double[] branchSet){
        int index = -1;
        double diff = 1;
        for(int i = 0; i < branchSet.length; i++){
            if(branchSet[i] < 1 && branchSet[i] > 0){
                double tmpDiff;
                if(branchSet[i] > lp_branch_condition_val){
                    tmpDiff = branchSet[i] - lp_branch_condition_val;
                }
                else{
                    tmpDiff = lp_branch_condition_val - branchSet[i];
                }
                if(diff > tmpDiff){
                    diff = tmpDiff;
                    index = i;
                }
            }
        }
        return index;
    }

    public Constraint[] getConstraints() {
        return constraints;
    }

    public boolean isLP_Relaxation() {
        return LP_Relaxation;
    }

    public Problem(Constraint[] constraints, Bound bound, NodeStrategy strategy, ProblemType type){
        this.constraints = constraints;
        this.bounds = bound;
        this.strategy = strategy;
        this.type = type;
        this.LP_Relaxation = false;
        this.lp_branch_condition_val = -1;
    }

    public Problem(Constraint[] constraints, Bound bound, NodeStrategy strategy, ProblemType type, boolean lp, double lp_bcv){
        this.constraints = constraints;
        this.bounds = bound;
        this.strategy = strategy;
        this.type = type;
        this.LP_Relaxation = lp;
        this.lp_branch_condition_val = lp_bcv;
    }
}