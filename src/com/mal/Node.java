package com.mal;

import com.mal.utils.compiler.BranchValue;

public class Node {
    public double lowerbound;
    public double upperbound;
    public final Node parent;
    public final boolean nodeIncluded;
    public final BranchValue value;

    public final int depth;

    public Node (Node par, BranchValue val, boolean included){
        this.parent = par;
        this.value = val;
        this.nodeIncluded = included;
        this.depth = (par==null)?0:par.depth +1;
    }
}
