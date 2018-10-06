package com.mal.UI.utils;

import bb_framework.enums.NodeStrategy;

public class ProblemInstance{
    public String name;
    public NodeStrategy strategy;
    public boolean lpRelaxation;
    public double branchValue;
    public String boundFile;

    public ProblemInstance(String nm){
        name = nm;
        strategy = null;
        lpRelaxation = false;
        branchValue = 0.5;
        boundFile = "";
    }
}
