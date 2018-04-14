package com.mal.framework;

import com.mal.framework.enums.NodeStrategy;
import com.mal.framework.enums.ProblemType;
import com.mal.framework.interfaces.Constraint;
import com.mal.framework.abstract_classes.Dataset;
import com.mal.framework.utils.BranchValue;
import com.mal.framework.utils.Node;
import com.mal.framework.utils.Problem;
import com.mal.framework.utils.Result;

import java.util.Comparator;
import java.util.PriorityQueue;

public class BranchAndBound {
    private Problem[] problems;
    private double[] dataset;
    private Result[] results;
    int total_nodes = 0;

    private PriorityQueue<Node> nodepool;

    double incumbent;
    Node best;

    public BranchAndBound(Problem[] instances, double[] set){
        this.problems = instances;
        this.dataset = set;
        this.results = new Result[instances.length];
    }

    public void Solve(){
        for(int i = 0; i < problems.length; i++){
            final long start_time = System.currentTimeMillis();

            int extreme = (problems[i].type==ProblemType.MAXIMIZATION)?Integer.MIN_VALUE:Integer.MAX_VALUE;
            Node root = new Node(null, extreme,false, dataset.length);
            root.lowerbound = Double.POSITIVE_INFINITY;
            root.upperbound = Double.NEGATIVE_INFINITY;
            nodepool = new PriorityQueue<>(getComparator(problems[i], problems[i].strategy));
            nodepool.add(root);
            best = root;

            while(!nodepool.isEmpty()) {
                Node node = nodepool.poll();
                node.lowerbound = problems[i].Lowerbound(node, dataset);
                node.upperbound = problems[i].Upperbound(node, dataset);
                boolean pruned = false;
                switch (problems[i].type){
                    case MINIMIZATION:
                    case MAXIMIZATION:
                }
                if (!pruned){
                    Branch(node, problems[i].getConstraints());
                }
            }
            final long end_time = System.currentTimeMillis();

            Result tmp = new Result(total_nodes, end_time - start_time);
            while (best != null){
                tmp.addToSolution(best.value);
                best = best.parent;
            }
            results[i] = tmp;
            total_nodes = 0;
            resetIncumbent(problems[i].type);
        }
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

    private Comparator<Node> getComparator(Problem problem,NodeStrategy strategy){
        switch (strategy){
            case BEST_FIRST:
                return (o1, o2) -> {
                    if(problem.type == ProblemType.MAXIMIZATION){
                        return Double.compare(o1.upperbound,o2.upperbound);
                    }
                    else{
                        return Double.compare(o1.lowerbound,o2.lowerbound);
                    }
                };
            case DEPTH_FIRST:
                return (o1, o2) -> o1.depth-o2.depth;
            case BREADTH_FIRST:
                return (o1,o2) -> o2.depth-o1.depth;
        }
        return null;
    }

    private void Branch(Node node, Constraint[] constraints){
        double nextVal = dataset[node.depth + 1];
        boolean new_node_allowed = true;

        for(Constraint c: constraints) {
            if (!c.CheckConstraint(new Node(node, nextVal, true, dataset.length), dataset)) { new_node_allowed = false; }
        }

        if (new_node_allowed) {
            Node is_included = new Node(node,nextVal, true, dataset.length);
            nodepool.add(is_included);
            total_nodes++;
        }
        nodepool.add(new Node(node, nextVal, false, dataset.length));
        total_nodes++;
    }

    public Result[] getResults() {
        return results;
    }
}
