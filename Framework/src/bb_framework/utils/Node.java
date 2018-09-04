package bb_framework.utils;

import java.util.HashMap;

import java.util.Set;

public class Node {
    private Node parent;
    private final boolean included;

    public double lowerbound;
    public double upperbound;
    public final int depth;
    private HashMap<Integer,Double> currentSolution;

    public boolean feasible = false;

    private double[] currentBoundSolution;

    public Node (Node par, boolean included){
        //this.parent = par;
        this.included = included;
        this.depth = (par==null)?-1:par.depth +1;
        this.currentSolution = (this.depth > 0)?/*new HashMap<>(par.getCurrentSolution())*/ (HashMap<Integer, Double>) par.currentSolution.clone() :new HashMap<>();
        if(this.depth > 0) this.currentSolution.put(this.depth,((included)?1.0:0.0));
    }

    public Node (Node par, boolean included, int index){
        //this.parent = par;
        this.included = included;
        this.depth = (par==null)?-1:par.depth+1;
        this.currentSolution = (this.depth > 0)?/*new HashMap<>(par.getCurrentSolution())*/ (HashMap<Integer, Double>) par.currentSolution.clone() :new HashMap<>();
        this.currentSolution.put(index,((included)?1.0:0.0));
    }

    private Node(HashMap cs, double[] cbs){
        included = true;
        depth = -1;
        this.currentSolution = (HashMap<Integer, Double>) cs.clone();
        this.currentBoundSolution = cbs.clone();
    }

    public HashMap<Integer, Double> getCurrentSolution() {
        return currentSolution;
    }

    public double getObjectiveValue(double[] dataset){
        double sum = 0;

        Set<Integer> keys = this.currentSolution.keySet();
        for(int i:keys){
            sum += this.currentSolution.get(i) * dataset[i];
        }
        return sum;
    }

    public void transferBoundSolutionToSolution(){
        for(int i = 0; i < this.currentBoundSolution.length; i++){
            if(!this.currentSolution.containsKey(i)){
                this.currentSolution.put(i,this.currentBoundSolution[i]);
            }
        }
    }

    public double[] getCurrentBoundSolution() {
        return currentBoundSolution;
    }

    public void setCurrentBoundSolution(double[] currentBoundSolution) {
        double[] tmp = new double[currentBoundSolution.length];
        for(int i = 0; i < currentBoundSolution.length; i++){
            tmp[i] = currentBoundSolution[i];
        }
        this.currentBoundSolution = tmp;
    }

    public Node clone(){
        return new Node(this.currentSolution,this.currentBoundSolution);
    }

    public void free(){
        this.currentSolution = null;
        this.currentBoundSolution = null;
        this.parent = null;
    }
}
