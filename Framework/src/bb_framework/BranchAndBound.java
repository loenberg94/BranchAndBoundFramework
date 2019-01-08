package bb_framework;

import bb_framework.enums.ConstraintType;
import bb_framework.enums.NodeStrategy;
import bb_framework.enums.ProblemType;
import bb_framework.interfaces.Dataset;
import bb_framework.utils.*;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.util.Pair;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.logging.Level;

import static bb_framework.enums.ProblemType.MAXIMIZATION;

public class BranchAndBound {
    Logging log = new Logging();

    int z = 0;
    private Problem[] problems;
    private Dataset dataset;
    private Result[] results;
    int total_nodes = 0;

    IntegerProperty resultsSolved;

    private PriorityQueue<Node> nodepool;

    double incumbent;
    Node best;

    boolean isRelavant = false;

    public BranchAndBound(Problem[] instances, Dataset set){
        this.problems = instances;
        this.dataset = set;
        this.results = new Result[instances.length];
        this.resultsSolved = new SimpleIntegerProperty();
    }

    public void Solve(Node init){
        log.Log("Solve method called", Level.INFO);
        if(init ==null){
            resultsSolved.setValue(0);
        }

        for(int i = 0; i < problems.length; i++){
            final long start_time = System.currentTimeMillis();

            int extreme = (problems[i].type==MAXIMIZATION)?Integer.MIN_VALUE:Integer.MAX_VALUE;
            Node root;
            if (init == null){
                root = new Node(null,false/*, extreme*/);
            }
            else{
                root = init;
            }

            problems[i].Upperbound(root,dataset);
            problems[i].Lowerbound(root,dataset);

            Comparator<Node> comparator = getComparator(problems[i],problems[i].strategy);
            nodepool = new PriorityQueue<>(comparator);
            nodepool.add(root);
            best = root;
            incumbent = extreme;

            while(!nodepool.isEmpty()) {
                Node node = nodepool.poll();

                int[] cSol = node.getCurrentSolution(dataset.size());
                isRelavant = cSol[0] == 0 && cSol[1] == 1 && cSol[2] == 1 &&cSol[3] == 0 && cSol[4] == 1 && cSol[5] == 1;

                boolean pruned = false;
                switch (problems[i].type){
                    case MINIMIZATION:
                        if (node.depth == dataset.size() - 1){
                            pruned = true;
                            double s_val = node.getObjectiveValue(dataset);
                            if(node.feasible && s_val < incumbent){
                                incumbent = s_val;
                                best = node;
                            }
                            else{
                                node.free();
                            }
                        }
                        else if(best.lowerbound < node.lowerbound){
                            pruned = true;
                        }
                        else if (node.lowerbound == node.upperbound) {
                            pruned = true;
                            double s_val = node.getObjectiveValue(dataset);
                            if(node.feasible && s_val < incumbent){
                                incumbent = s_val;
                                best = node;
                            }
                            else{
                                node.free();
                            }
                        }
                        break;
                    case MAXIMIZATION:
                        if(node.depth == dataset.size() - 1){
                            //log.Log("1: Node has reached max depth",Level.INFO);
                            pruned = true;
                            double val = node.getObjectiveValue(dataset);
                            if (val > incumbent) {
                                //log.Log("1.1: Value was better than incumbent, value: " + String.valueOf(val),Level.INFO);
                                incumbent = val;
                                best = node;
                            }
                            else{
                                node.free();
                            }
                        }
                        else if(best.lowerbound > node.upperbound){
                            //log.Log(String.format("2: best.lowerbound: %f, node.upperbound: %f", best.lowerbound, node.upperbound),Level.INFO);
                            pruned = true;
                        } else if (node.lowerbound == node.upperbound) {
                            //log.Log("3: Lowerbound equals Upperbound - optimal solution for branch found",Level.INFO);
                            pruned = true;
                            double s_val = node.upperbound;
                            if(s_val > incumbent){
                                //log.Log("3.1: Solution value is greater than current best value, value: " + String.valueOf(s_val),Level.INFO);
                                incumbent = s_val;
                                best = node.transferBoundSolutionToSolution();
                            }
                            else{
                                node.free();
                            }
                        }
                        break;
                }

                if (!pruned){
                    if(problems[i].isLpRelaxation()){
                        int branch_index;
                        if(problems[i].type.equals(MAXIMIZATION)){
                            branch_index = problems[i].getBranchIndex(node.getCurrentBoundSolution());
                            if(branch_index >= 0){
                                branch(node, problems[i], branch_index);
                            }
                            else{
                                node = node.transferBoundSolutionToSolution();
                                if(node.upperbound > incumbent){
                                    incumbent = node.upperbound;
                                    best = node;
                                }
                            }
                        }
                        else{
                            branch_index = problems[i].getBranchIndex(node.getCurrentBoundSolution());
                            if(branch_index >= 0){
                                branch(node, problems[i], branch_index);
                            }
                            else{
                                node = node.transferBoundSolutionToSolution();
                                if(node.lowerbound < incumbent){
                                    incumbent = node.lowerbound;
                                    best = node;
                                }
                            }
                        }
                    }
                    else{
                        branch(node, problems[i], -1);
                    }
                }
                node = null;
            }
            final long end_time = System.currentTimeMillis();

            Result tmp = new Result(total_nodes, (end_time - start_time)/1000.0, dataset.size(), problems[i].strategy,problems[i].getP_name());
            tmp.setObjectiveValue(best.getObjectiveValue(dataset));

            Node curr = best;
            while (curr.depth > -1){
                tmp.setSolution(curr.index, (curr.included?1:0));
                curr = curr.getParent();
            }

            for(int v: tmp.getSolution()){
                System.out.format("%d ",v);
            }
            System.out.println();

            results[i] = tmp;
            total_nodes = 0;
            resetIncumbent(problems[i].type);
            best = null;
            if(init == null){
                resultsSolved.setValue(i + 1);
            }
            System.gc();
        }
    }

    public IntegerProperty getResultsSolvedProperty(){
        return resultsSolved;
    }

    private void resetIncumbent(ProblemType type){
        switch (type){
            case MAXIMIZATION:
                incumbent = Double.NEGATIVE_INFINITY;
                break;
            case MINIMIZATION:
                incumbent = Double.POSITIVE_INFINITY;
                break;
        }
    }

    public static Comparator<Node> getComparator(Problem problem,NodeStrategy strategy){
        switch (strategy){
            case BEST_FIRST:
                return (o1, o2) -> {
                    if(problem.type.equals(MAXIMIZATION)){
                        return -Double.compare(o1.upperbound,o2.upperbound);
                    }
                    else{
                        return Double.compare(o1.lowerbound,o2.lowerbound);
                    }
                };
            case DEPTH_FIRST:
                return (o1,o2) -> -Integer.compare(o1.depth,o2.depth);
            case BREADTH_FIRST:
                return Comparator.comparingInt(o -> o.depth);
        }
        return null;
    }

    private void branch(Node node, Problem problem, int nextValIndex){
        z++;
        int nextVal = (nextValIndex >= 0)?nextValIndex:node.depth + 1;
        boolean new_node_allowed = true;

        boolean feasible = true;
        for(Constraint c: problem.getConstraints()) {
            Pair<Boolean,Boolean> tmp = c.checkConstraint(node,nextVal);
            if(c.getcT() == ConstraintType.EQUALS){
                if(tmp.getKey()){
                    new_node_allowed &= true;
                }
                else{
                    if(tmp.getValue()){
                        new_node_allowed &= true;
                    }
                    else{
                        new_node_allowed &= false;
                    }
                }
                feasible &= tmp.getKey() && tmp.getValue();
            }
            else{
                feasible &= tmp.getKey();
                new_node_allowed &= tmp.getKey() || tmp.getValue();
            }
        }

        if (new_node_allowed) {
            Node is_included = new Node(node,true, nextVal);
            is_included.feasible = feasible;
            problem.Lowerbound(is_included,dataset);
            problem.Upperbound(is_included,dataset);
            nodepool.add(is_included);
            total_nodes++;
        }

        Node not_included = new Node(node,false, nextVal);
        not_included.feasible = node.feasible;
        problem.Lowerbound(not_included,dataset);
        problem.Upperbound(not_included,dataset);
        nodepool.add(not_included);
        total_nodes++;
    }

    public Result[] getResults() {
        return results;
    }
}
