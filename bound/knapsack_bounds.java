
import bb_framework.interfaces.Bound;
import bb_framework.utils.Constraint;
import bb_framework.utils.Node;
import bb_framework.utils.Problem;
import bb_framework.BranchAndBound;

import java.util.HashMap;

public class knapsack_bounds implements Bound {
        @Override
        public double Lowerbound(Node node, double[] set, Problem problem) {
            float sum = 0;
            float weight = 0;
            for (int cs_i:node.getCurrentSolution().keySet()){
                sum += node.getCurrentSolution().get(cs_i) * set[cs_i];
                weight += node.getCurrentSolution().get(cs_i) * problem.getConstraints()[0].getD_lhs()[cs_i];
            }
            for(int i = 0; i < set.length; i++){
                if(!node.getCurrentSolution().containsKey(i)){
                    if(weight + problem.getConstraints()[0].getD_lhs()[i] <= problem.getConstraints()[0].getRhs()){
                        sum += set[i];
                        weight += problem.getConstraints()[0].getD_lhs()[i];
                    }
                }
            }
            return sum;
        }

        @Override
        public double Upperbound(Node node, double[] set, Problem problem) {
            return 30000000;
        }
    }
