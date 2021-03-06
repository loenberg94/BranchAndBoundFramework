package bb_framework.utils;

import bb_framework.interfaces.Dataset;
import bb_framework.types.Coefficient;

public class Node {
    private Node parent;
    public final boolean included;
    public final int index;

    public double lowerbound;
    public double upperbound;
    public final int depth;

    public boolean feasible = false;

    private double[] currentBoundSolution;

    private int children = 0;

    public Node (Node par, boolean included){
        if (par != null) par.incrementChildren();
        this.parent = par;
        this.included = included;
        this.depth = (par==null)?-1:par.depth +1;
        this.index = this.depth;
    }

    public Node (Node par, boolean included, int index){
        if (par != null) par.incrementChildren();
        this.parent = par;
        this.included = included;
        this.depth = (par==null)?-1:par.depth+1;
        this.index = index;
    }

    public double getObjectiveValue(Dataset dataset){
        double sum = 0;
        Node curr = this;
        while(curr.depth > -1){
            Coefficient coefficient = dataset.get(curr.index);
            if(coefficient.isValue()){
                sum += curr.included ? (Double) dataset.get(curr.index).getVal():0;
            }
            else if(coefficient.isIndex()){
                sum += curr.included ? 1:0;
            }
            curr = curr.parent;
        }
        return sum;
    }

    public Node transferBoundSolutionToSolution(){
        DisjointSet ds = new DisjointSet(currentBoundSolution.length);
        ds.InitializeWithCurrentSolution(this);

        Node par = this;
        int csSet = this.depth == -1?this.depth:ds.Find(this.index);
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

    private void incrementChildren(){
        this.children++;
    }

    private void decrementChildren(){
        this.children--;
    }

    public void free(){
        Node curr = this;
        Node prev;
        while(curr.children == 0){
            prev = curr;
            curr.currentBoundSolution = null;
            curr.parent.decrementChildren();
            curr = curr.parent;
            prev.parent = null;
        }
    }
}