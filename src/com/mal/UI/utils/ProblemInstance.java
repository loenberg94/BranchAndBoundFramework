package com.mal.UI.utils;

import bb_framework.enums.NodeStrategy;
import bb_framework.interfaces.Bound;

public class ProblemInstance{
    public String name;
    public NodeStrategy strategy;
    public boolean lpRelaxation;
    public double branchValue;
    public String boundFile;
    public Bound bound;

    public ProblemInstance(String nm){
        name = nm;
        strategy = null;
        lpRelaxation = false;
        branchValue = 0.5;
        boundFile = "";
    }

    public ProblemInstance(String nm, NodeStrategy st, boolean lp, String bf){
        name = nm;
        strategy = st;
        lpRelaxation = lp;
        branchValue = 0.5;
        boundFile = bf;
    }
}
