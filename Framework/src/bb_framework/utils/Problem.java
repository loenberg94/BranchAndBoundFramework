package bb_framework.utils;

import bb_framework.interfaces.Bound;
import bb_framework.enums.NodeStrategy;
import bb_framework.enums.ProblemType;
import bb_framework.interfaces.Dataset;
import bb_framework.testFiles.Knapsack;

public class Problem {
    private String p_name;
    private Constraint[] constraints;
    private Bound bounds;

    private final boolean lpRelaxation;
    private final double lp_branch_condition_val;

    public NodeStrategy strategy;
    public final ProblemType type;

    public double[] Lowerbound(Node node, Dataset set){
        if (node.depth == set.size() -1){
            node.lowerbound = node.getObjectiveValue(set);
            return null;
        }

        if (!lpRelaxation || type.equals(ProblemType.MAXIMIZATION)){
            double lb = bounds.Lowerbound(node,set,this);
            node.lowerbound = lb;
        }
        else {
            try {
                double[] retArr = Cplex.lp_relaxation(set,node,constraints,type);
                if(retArr != null){
                    node.lowerbound = calculateObjValue(retArr, set);
                    node.setCurrentBoundSolution(retArr);
                }
                return retArr;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public double[] Upperbound(Node node, Dataset set){
        if (node.depth == set.size() - 1){
            node.upperbound = node.getObjectiveValue(set);
            return null;
        }

        if (!lpRelaxation || type.equals(ProblemType.MINIMIZATION)){
            node.upperbound = bounds.Upperbound(node,set,this);
        }
        else {
            try {
                double[] retArr = Cplex.lp_relaxation(set,node,constraints,type);
                if (retArr != null){
                    node.upperbound = calculateObjValue(retArr,set);
                    node.setCurrentBoundSolution(retArr);
                }
                return retArr;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private float calculateObjValue(double[] solution, Dataset set){
        float sum = 0;
        for(int i = 0; i < set.size(); i++){
            //TODO: Change to use index as well
            sum += solution[i] * (Double) set.get(i).getVal();
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

    public boolean isLpRelaxation() {
        return lpRelaxation;
    }

    public String getP_name() {
        return p_name;
    }

    public void updateConstraints(Constraint[] constraint){
        this.constraints = constraint;
    }

    public void updateBound(Bound bnd){ this.bounds = bnd; }

    public Problem(String name, Constraint[] constraints, Bound bound, NodeStrategy strategy, ProblemType type){
        this.constraints = constraints;
        this.bounds = bound;
        this.strategy = strategy;
        this.type = type;
        this.lpRelaxation = false;
        this.lp_branch_condition_val = -1;
        this.p_name = name;
    }

    public Problem(String name,Constraint[] constraints, Bound bound, NodeStrategy strategy, ProblemType type, boolean lp, double lp_bcv){
        this.constraints = constraints;
        this.bounds = bound;
        this.strategy = strategy;
        this.type = type;
        this.lpRelaxation = lp;
        this.lp_branch_condition_val = lp_bcv;
        this.p_name = name;
    }

    public Problem copy(){
        return new Problem(this.p_name, this.constraints, this.bounds, this.strategy, this.type);
    }

}