package bb_framework.testFiles;

import bb_framework.enums.ConstraintType;
import bb_framework.enums.NodeStrategy;
import bb_framework.enums.ProblemType;
import bb_framework.interfaces.Bound;
import bb_framework.interfaces.Dataset;
import bb_framework.types.Coefficient;
import bb_framework.types.Value;
import bb_framework.types.Vector;
import bb_framework.utils.Constraint;
import bb_framework.utils.DisjointSet;
import bb_framework.utils.Node;
import bb_framework.utils.Problem;

public class Knapsack extends Problem {

    //final static double[] leftHandSide = new double[] {70,73,77,80,82,87,90,94,98,106,110,113,115,118,120};
    public final static double[] leftHandSide = new double[] {120,118,115,113,110,106,98,94,90,87,82,80,77,73,70};
    //final static double[] dataset      = new double[] {135,139,149,150,156,163,173,184,192,201,210,214,221,229,240};
    final static double[] dataset      = new double[] {240,229,221,214,210,201,192,184,173,163,156,150,149,139,135};
    final static Coefficient[] cs = new Coefficient[] {new Value(240.),new Value(229.),new Value(221.),
            new Value(214.),new Value(210.),new Value(201.),new Value(192.),new Value(184.),new Value(173.),
            new Value(163.),new Value(156.),new Value(150.),new Value(149.),new Value(139.),new Value(135.)};
    public final static Dataset ds = new Vector(cs,15);

    private Knapsack(Constraint[] constraints, Bound bound, NodeStrategy strategy, ProblemType type, boolean lp) {
        super("",constraints, bound, strategy, type, lp, 0.5);
    }

    public static Knapsack CreateNew(){
        Constraint[] constraints = new Constraint[] {new Constraint(leftHandSide,750,ConstraintType.LEQ,false)};
        return new Knapsack(constraints, new KnapsackBound(),NodeStrategy.BEST_FIRST, ProblemType.MAXIMIZATION, true);
    }

    public Dataset getDataset() {
        return ds;
    }

    public Bound getBound(){
        return new KnapsackBound();
    }
}
