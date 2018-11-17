package com.mal.UI.utils;

import bb_framework.enums.NodeStrategy;
import bb_framework.interfaces.Bound;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.io.File;

public class ProblemInstance {
    public String name;
    public NodeStrategy strategy;
    public boolean lpRelaxation;
    public double branchValue;
    public String boundFile;
    private Bound bound = null;

    private BooleanProperty isCompiled = new SimpleBooleanProperty();

    public ProblemInstance(String nm){
        name = nm;
        strategy = NodeStrategy.BEST_FIRST;
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

    public Bound getBound() {
        return bound;
    }

    public void setBound(Bound bound) {
        this.bound = bound;
        isCompiled.setValue(bound != null);
    }

    public BooleanProperty getIsCompiled(){
        return this.isCompiled;
    }

    public String getFileName(){
        return new File(this.boundFile).getName();
    }

    public ProblemInstance copy(){
        return new ProblemInstance(this.name, this.strategy, this.lpRelaxation, this.boundFile);
    }
}
