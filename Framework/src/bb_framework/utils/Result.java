package bb_framework.utils;

import bb_framework.enums.NodeStrategy;

public class Result {
    final String p_name;
    final int nr_of_nodes;
    final double runtime;

    private int[] solution;
    private double objectiveValue;

    NodeStrategy strategy;

    public Result(int nodes, double time, int size, NodeStrategy strategy, String name){
        this.nr_of_nodes = nodes;
        this.runtime = time;
        this.solution = new int[size];
        this.strategy = strategy;
        this.p_name = name;
    }

    public void setSolution(int i, Double included) {
        this.solution[i] = (included==1.0)?1:0;
    }
    public void setObjectiveValue(double val){ objectiveValue = val; }
    public int[] getSolution(){
        return this.solution;
    }
    public double getRuntime() {
        return runtime;
    }
    public double getObjectiveValue() {
        return objectiveValue;
    }
    public int getNr_of_nodes() {
        return nr_of_nodes;
    }
    public String getP_name() { return p_name; }

    public NodeStrategy getStrategy() {
        return strategy;
    }
}
