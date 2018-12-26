package bb_framework.testFiles;

import bb_framework.interfaces.Bound;
import bb_framework.interfaces.Dataset;
import bb_framework.utils.DisjointSet;
import bb_framework.utils.Node;
import bb_framework.utils.Problem;

@SuppressWarnings("Duplicates")
public class KnapsackBound implements Bound{
        @Override
        public double Lowerbound(Node node, Dataset set, Problem problem) {
            float sum = 0;
            float weight = 0;
            DisjointSet ds = new DisjointSet(set.size());

            int prev = -1;
            Node curr = node;
            while (curr.depth > -1){
                if(prev != -1){
                    ds.Union(prev, curr.index);
                }
                prev = curr.index;
                sum += curr.included?(Double) set.get(curr.index).getVal():0;
                weight += curr.included?problem.getConstraints()[0].getD_lhs()[curr.index]:0;
                curr = curr.getParent();
            }

            int csSet = prev==-1?prev:ds.Find(prev);
            for (int i = 0; i < set.size(); i++){
                if(csSet == -1 || ds.Find(csSet) != ds.Find(i)){
                    if(weight + problem.getConstraints()[0].getD_lhs()[i] <= problem.getConstraints()[0].getRhs()){
                        sum += (Double) set.get(i).getVal();
                        weight += problem.getConstraints()[0].getD_lhs()[i];
                    }
                }
            }
            return sum;
        }

        @Override
        public double Upperbound(Node node, Dataset set, Problem problem) {
            return 30000000;
        }

}
