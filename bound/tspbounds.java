

import bb_framework.interfaces.Bound;
import bb_framework.utils.Constraint;
import bb_framework.utils.Cplex;
import bb_framework.utils.Node;
import bb_framework.utils.Problem;
import bb_framework.BranchAndBound;
import java.util.HashMap;

import ilog.concert.IloException;
import javafx.util.Pair;

public class tspbounds implements Bound {
    @Override
    public double Lowerbound(Node node, double[] set, Problem problem) {
        try {
            Cplex cplex = new Cplex();
            Constraint[] constraints = new Constraint[6];
            constraints[0] = problem.getConstraints()[0];
            constraints[1] = problem.getConstraints()[1];
            constraints[2] = problem.getConstraints()[2];
            constraints[3] = problem.getConstraints()[3];
            constraints[4] = problem.getConstraints()[4];
            constraints[5] = problem.getConstraints()[5];

            //return cplex.ip_solve(set,node.getCurrentSolution(),constraints,problem.type);

        } catch (IloException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public double Upperbound(Node node, double[] set, Problem problem) {
        return Double.MAX_VALUE;
    }
}
