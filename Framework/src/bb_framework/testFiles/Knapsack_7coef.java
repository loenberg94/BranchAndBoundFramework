package bb_framework.testFiles;

import bb_framework.enums.ConstraintType;
import bb_framework.enums.NodeStrategy;
import bb_framework.enums.ProblemType;
import bb_framework.interfaces.Bound;
import bb_framework.interfaces.Dataset;
import bb_framework.types.Value;
import bb_framework.utils.Constraint;
import bb_framework.utils.DisjointSet;
import bb_framework.utils.Node;
import bb_framework.utils.Problem;

public class Knapsack_7coef extends Problem {
    final static Value[] leftHandSide = new Value[] {new Value(41.),new Value(50.),new Value(49.),
            new Value(59.),new Value(55.),new Value(57.),new Value(60.)};
    final static double[] dataset      = new double[] {442,525,511,593,546,564,617};

    private Knapsack_7coef(Constraint[] constraints, Bound bound, NodeStrategy strategy, ProblemType type, boolean lp) {
        super("",constraints, bound, strategy, type, lp, 0.5);
    }

    @SuppressWarnings("Duplicates")
    private static class knapsackBounds implements Bound {

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
                int in = node.included?1:0;
                sum += in * (Double) set.get(curr.index).getVal();
                weight += in * (Double) problem.getConstraints()[0].getLhs()[curr.index].getVal();
                curr = curr.getParent();
            }

            int csSet = prev==-1?prev:ds.Find(prev);
            for (int i = 0; i < set.size(); i++){
                if(csSet != ds.Find(i)){
                    if(weight + (Double) problem.getConstraints()[0].getLhs()[i].getVal() <= problem.getConstraints()[0].getRhs()){
                        sum += (Double) set.get(i).getVal();
                        weight += (float) problem.getConstraints()[0].getLhs()[i].getVal();
                    }
                }
            }
            return sum;
        }

        @Override
        public double Upperbound(Node node, Dataset set, Problem problem) {
            return 0;
        }
    }

    public static Knapsack_7coef CreateNew(){
        Constraint[] constraints = new Constraint[] {new Constraint(leftHandSide,170,ConstraintType.LEQ)};
        return new Knapsack_7coef(constraints, new knapsackBounds(),NodeStrategy.BEST_FIRST, ProblemType.MAXIMIZATION, true);
        
    }

    public double[] getDataset() {
        return dataset;
    }

    public Bound testBound(){
        return new knapsackBounds();
    }
}
