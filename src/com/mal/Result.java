package com.bb;

import java.util.ArrayList;

/**
 * This class is used to keep all the parameters to compare different
 * problem instances on, s.a. time, and number of nodes created.
 * @param <T> problem specific result value
 */
public class Result<T> {
    final int nr_of_nodes;
    final long runtime;

    private ArrayList<T> solution;

    public Result(int nodes, long time){
        this.nr_of_nodes = nodes;
        this.runtime = time;
        this.solution = new ArrayList<>();
    }

    public void addToSolution(T variable){
        solution.add(variable);
    }

    public ArrayList<T> getSolution(){
        return this.solution;
    }
}
