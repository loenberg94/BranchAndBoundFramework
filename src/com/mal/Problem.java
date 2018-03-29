package com.mal;

import com.mal.enums.NodeStrategy;
import com.mal.enums.ProblemType;
import com.mal.interfaces.Bound;
import com.mal.interfaces.Constraint;
import com.mal.interfaces.Dataset;
import com.mal.interfaces.ObjectiveFunction;

public class Problem {
    private ObjectiveFunction objectiveFunction;
    private Constraint[] constraints;
    private Bound bounds;

    private final boolean LP_Relaxation;

    final NodeStrategy strategy;
    final ProblemType type;

    public double Lowerbound(Node node, Dataset set){
        if(this.bounds != null){
            return bounds.Lowerbound(node, set);
        }
        return Double.POSITIVE_INFINITY;
    }

    public double Upperbound(Node node, Dataset set){
        if(this.bounds != null){
            return bounds.Upperbound(node, set);
        }
        return Double.NEGATIVE_INFINITY;
    }

    public Constraint[] getConstraints() {
        return constraints;
    }

    public Problem(ObjectiveFunction objectiveFunction, Constraint[] constraints, Bound bound, NodeStrategy strategy, ProblemType type, boolean lp){
        this.objectiveFunction = objectiveFunction;
        this.constraints = constraints;
        this.bounds = bound;
        this.strategy = strategy;
        this.type = type;
        this.LP_Relaxation = lp;
    }
}