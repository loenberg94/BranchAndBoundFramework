

import bb_framework.interfaces.Bound;
import bb_framework.utils.Constraint;
import bb_framework.utils.DisjointSet;
import bb_framework.utils.Node;
import bb_framework.utils.Problem;
import bb_framework.BranchAndBound;

import java.util.HashMap;

public class knapsack_bounds implements Bound {
        @Override
        public double Lowerbound(Node node, double[] set, Problem problem) {
            float sum = 0;
            float weight = 0;
            DisjointSet ds = new DisjointSet(set.length);

            int prev = -1;
            Node curr = node;
            while (curr.depth > -1){
                if(prev != -1){
                    ds.Union(prev, curr.index);
                }
                prev = curr.index;
                int in = node.included?1:0;
                sum += in * set[curr.index];
                weight += in * problem.getConstraints()[0].getD_lhs()[curr.index];
                curr = curr.getParent();
            }
            
            int csSet = prev==-1?prev:ds.Find(prev);
            for (int i = 0; i < set.length; i++){
                if(csSet == -1 || ds.Find(csSet) != ds.Find(i)){
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
