package bb_framework.testFiles;

import bb_framework.interfaces.Bound;
import bb_framework.interfaces.Dataset;
import bb_framework.utils.Constraint;
import bb_framework.utils.Node;
import bb_framework.utils.Problem;
import bb_framework.BranchAndBound;



public class tspbounds implements Bound {

    private class bnd implements Bound{

        @Override
        public double Lowerbound(Node node, Dataset set, Problem problem) {
            return 0;
        }

        @Override
        public double Upperbound(Node node, Dataset set, Problem problem) {
            return Double.POSITIVE_INFINITY;
        }
    }

    @Override
    public double Lowerbound(Node node, Dataset set, Problem problem) {
        Constraint[] tmp = problem.getConstraints();
        Constraint[] constraints = new Constraint[] {tmp[0],tmp[1],tmp[2],tmp[3],tmp[4]};
        Problem newProb = problem.copy();
        newProb.updateConstraints(constraints);
        newProb.updateBound(new bnd());
        BranchAndBound bnb = new BranchAndBound(new Problem[] { newProb }, set);
        bnb.Solve(node);
        return bnb.getResults()[0].getObjectiveValue();
    }

    @Override
    public double Upperbound(Node node, Dataset set, Problem problem) {
        return Double.MAX_VALUE;
    }
}
