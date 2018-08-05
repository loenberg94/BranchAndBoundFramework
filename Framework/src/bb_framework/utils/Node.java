package bb_framework.utils;

import java.util.HashMap;

import java.util.Set;

public class Node {
    public double lowerbound;
    public double upperbound;
    private final boolean nodeIncluded;
    private final double value;
    public final int depth;
    private HashMap<Integer,Double> currentSolution;

    public boolean feasible = false;

    private double[] currentBoundSolution;

    public Node (Node par, double val, boolean included){
        this.value = val;
        this.nodeIncluded = included;
        this.depth = (par==null)?-1:par.depth +1;

        this.currentSolution = (this.depth > 0)?new HashMap<>(par.getCurrentSolution()):new HashMap<>();
        if(this.depth > 0) this.currentSolution.put(this.depth,((included)?1.0:0.0));
    }

    public Node (Node par, double val, boolean included, int index){
        //this.parent = par;
        this.value = val;
        this.nodeIncluded = included;
        this.depth = (par==null)?-1:par.depth+1;

        this.currentSolution = (this.depth > 0)?new HashMap<>(par.getCurrentSolution()):new HashMap<>();
        this.currentSolution.put(index,((included)?1.0:0.0));
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

    public void free(){
        this.currentSolution = null;
        this.currentBoundSolution = null;
    }
}
