package com.mal.tests;


import bb_framework.interfaces.Bound;
import bb_framework.utils.Constraint;
import bb_framework.utils.Node;
import bb_framework.utils.Problem;
import bb_framework.BranchAndBound;
import java.util.HashMap;
import javafx.util.Pair;

public class tspbounds implements Bound {
    @Override
    public double Lowerbound(Node node, double[] set, Problem problem) {
        return 0;
    }

    @Override
    public double Upperbound(Node node, double[] set, Problem problem) {
        return Double.MAX_VALUE;
    }
}