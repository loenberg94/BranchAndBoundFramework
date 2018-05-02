package bb_framework;

import bb_framework.enums.NodeStrategy;
import bb_framework.enums.ProblemType;
import bb_framework.utils.Constraint;
import bb_framework.utils.Node;
import bb_framework.utils.Problem;
import bb_framework.utils.Result;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.Comparator;
import java.util.PriorityQueue;

import static bb_framework.enums.ProblemType.MAXIMIZATION;

public class BranchAndBound {
    private Problem[] problems;
    private double[] dataset;
    private Result[] results;
    int total_nodes = 0;

    IntegerProperty resultsSolved;

    private PriorityQueue<Node> nodepool;

    double incumbent;
    Node best;

    public BranchAndBound(Problem[] instances, double[] set){
        this.problems = instances;
        this.dataset = set;
        this.results = new Result[instances.length];
        this.resultsSolved = new SimpleIntegerProperty();
    }

    public void Solve(){
        resultsSolved.setValue(0);
        for(int i = 0; i < problems.length; i++){
            final long start_time = System.currentTimeMillis();

            int extreme = (problems[i].type==MAXIMIZATION)?Integer.MIN_VALUE:Integer.MAX_VALUE;
            Node root = new Node(null, extreme,false);
            problems[i].Upperbound(root,dataset);
            problems[i].Lowerbound(root,dataset);

            Comparator<Node> comparator = getComparator(problems[i],problems[i].strategy);
            nodepool = new PriorityQueue<>(comparator);
            nodepool.add(root);
            best = root;

            while(!nodepool.isEmpty()) {
                Node node = nodepool.poll();

                boolean pruned = false;
                switch (problems[i].type){
                    case MINIMIZATION:
                        if(node.upperbound < best.lowerbound){
                            pruned = true;
                        }
                        else if (node.lowerbound == node.upperbound) {
                            pruned = true;
                            double s_val = node.getObjectiveValue(dataset);
                            if(s_val < incumbent){
                                incumbent = s_val;
                                best = node;
                            }
                        }
                        break;
                    case MAXIMIZATION:
                        if(node.depth == dataset.length - 1){
                            pruned = true;
                            double val = node.getObjectiveValue(dataset);
                            if (val > incumbent) {
                                incumbent = val;
                                best = node;
                            }
                        }
                        else if(best.lowerbound > node.upperbound){
                            pruned = true;
                        } else if (node.lowerbound == node.upperbound) {
                            pruned = true;
                            double s_val = node.upperbound;
                            if(s_val > incumbent){
                                node.transferBoundSolutionToSolution();
                                incumbent = s_val;
                                best = node;
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
                                node.transferBoundSolutionToSolution();
                                if(node.upperbound > incumbent){
                                    incumbent = node.upperbound;
                                    best = node;
                                }
                            }
                        }
                        else{
                            branch_index = problems[i].getBranchIndex(node.getCurrentBoundSolution());
                        }
                    }
                    else{
                        branch(node, problems[i], -1);
                    }
                }
                node = null;
            }
            final long end_time = System.currentTimeMillis();

            Result tmp = new Result(total_nodes, (end_time - start_time)/1000.0, dataset.length, problems[i].strategy,problems[i].getP_name());
            tmp.setObjectiveValue(best.getObjectiveValue(dataset));
            for(int j:best.getCurrentSolution().keySet()){
                tmp.setSolution(j,best.getCurrentSolution().get(j));
            }
            results[i] = tmp;
            total_nodes = 0;
            resetIncumbent(problems[i].type);
            best = null;
            resultsSolved.setValue(i + 1);
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
        double nextVal = (nextValIndex >= 0)?nextValIndex:dataset[node.depth + 1];
        boolean new_node_allowed = true;

        for(Constraint c: problem.getConstraints()) {
            //if (!c.CheckConstraint(new Node(node, nextVal, true, dataset.length), dataset)) { new_node_allowed = false; }
            double[] lhs = c.getLhs();
            double val = 0;
            for(int i = 0; i < lhs.length; i++){
                if(node.getCurrentSolution().containsKey(i)){
                    val += node.getCurrentSolution().get(i) * lhs[i];
                }
                else if(i == nextValIndex){
                    val += lhs[i];
                }
            }
            switch (c.getcT()){
                case LEQ:
                    new_node_allowed = new_node_allowed && (val <= c.getRhs());
                    break;
                case EQUALS:
                    new_node_allowed = new_node_allowed && (val == c.getRhs());
                    break;
                case GEQ:
                    new_node_allowed = new_node_allowed && (val >= c.getRhs());
            }
        }

        if (new_node_allowed) {
            Node is_included = (problem.isLpRelaxation())?new Node(node,nextVal, true, nextValIndex):new Node(node,nextVal, true);
            problem.Lowerbound(is_included,dataset);
            problem.Upperbound(is_included,dataset);
            nodepool.add(is_included);
            total_nodes++;
            is_included = null;
        }

        Node not_included = (problem.isLpRelaxation())?new Node(node,nextVal, false, nextValIndex):new Node(node,nextVal, false);
        problem.Lowerbound(not_included,dataset);
        problem.Upperbound(not_included,dataset);
        nodepool.add(not_included);
        total_nodes++;
        not_included = null;
        node.free();
    }

    public Result[] getResults() {
        return results;
    }
}