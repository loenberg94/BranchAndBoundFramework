package bb_framework.utils;

import java.util.HashMap;

import java.util.Set;

public class Node {
    private Node parent;
    public final boolean included;
    public final int index;

    public double lowerbound;
    public double upperbound;
    public final int depth;

    public boolean feasible = false;

    private double[] currentBoundSolution;

    public Node (Node par, boolean included){
        this.parent = par;
        this.included = included;
        this.depth = (par==null)?-1:par.depth +1;
        this.index = this.depth;
    }

    public Node (Node par, boolean included ,int index){
        this.parent = par;
        this.included = included;
        this.depth = (par==null)?-1:par.depth+1;
        this.index = index;
    }

    public double getObjectiveValue(double[] dataset){
        double sum = 0;
        Node curr = this;
        while(curr.depth > -1){
            sum += curr.included ? dataset[curr.index]:0;
            curr = curr.parent;
        }
        return sum;
    }

    public Node transferBoundSolutionToSolution(){
        DisjointSet ds = new DisjointSet(currentBoundSolution.length);
        ds.InitializeWithCurrentSolution(this);

        Node par = this;
        int csSet = this.index == -1?this.index:ds.Find(this.index);
        for(int i = 0; i < currentBoundSolution.length;i++){
                if(csSet != ds.Find(i)){
                    Node tmp = new Node(par,currentBoundSolution[i] == 1?true:false, i);
                    par = tmp;
                }
        }
        par.upperbound = this.upperbound;
        par.lowerbound = this.lowerbound;
        return par;
    }

    @Deprecated
    public int[] getCurrentSolution(int size){
        int[] tmp = new int[size];
        for(int i = 0; i < size; i++) tmp[i] = -1;
        Node curr = this;
        while(curr.depth > -1){
            tmp[curr.index] = curr.included?1:0;
            curr = curr.parent;
        }
        return tmp;
    }


    public double[] getCurrentBoundSolution() {
        return currentBoundSolution;
    }

    public void setCurrentBoundSolution(double[] currentBoundSolution) {
        this.currentBoundSolution = currentBoundSolution.clone();
    }

    public Node getParent() {
        return this.parent;
    }

    public void free(){
        this.currentBoundSolution = null;
        this.parent = null;
    }
}