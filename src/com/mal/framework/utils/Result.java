package com.mal.framework.utils;

import com.mal.framework.enums.NodeStrategy;

public class Result {
    final int nr_of_nodes;
    final double runtime;

    private int[] solution;
    private double objectiveValue;

    NodeStrategy strategy;

    public Result(int nodes, double time, int size, NodeStrategy strategy){
        this.nr_of_nodes = nodes;
        this.runtime = time;
        this.solution = new int[size];
        this.strategy = strategy;
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

    public NodeStrategy getStrategy() {
        return strategy;
    }
}
