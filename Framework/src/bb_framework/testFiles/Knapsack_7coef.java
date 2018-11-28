package bb_framework.testFiles;

import bb_framework.enums.ConstraintType;
import bb_framework.enums.NodeStrategy;
import bb_framework.enums.ProblemType;
import bb_framework.interfaces.Bound;
import bb_framework.utils.Constraint;
import bb_framework.utils.DisjointSet;
import bb_framework.utils.Node;
import bb_framework.utils.Problem;

public class Knapsack_7coef extends Problem {
    final static double[] leftHandSide = new double[] {41,50,49,59,55,57,60};
    final static double[] dataset      = new double[] {442,525,511,593,546,564,617};

    private Knapsack_7coef(Constraint[] constraints, Bound bound, NodeStrategy strategy, ProblemType type, boolean lp) {
        super("",constraints, bound, strategy, type, lp, 0.5);
    }

    @SuppressWarnings("Duplicates")
    private static class knapsackBounds implements Bound {

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
                if(csSet != ds.Find(i)){
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
            return 0;
        }
    }

    public static Knapsack_7coef CreateNew(){
        Constraint[] constraints = new Constraint[] {new Constraint(leftHandSide,170,ConstraintType.LEQ, false)};
        return new Knapsack_7coef(constraints, new knapsackBounds(),NodeStrategy.BEST_FIRST, ProblemType.MAXIMIZATION, true);
        
    }

    public double[] getDataset() {
        return dataset;
    }

    public Bound testBound(){
        return new knapsackBounds();
    }
}
