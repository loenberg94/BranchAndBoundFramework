package bb_framework.test;

import bb_framework.BranchAndBound;
import bb_framework.enums.ConstraintType;
import bb_framework.enums.NodeStrategy;
import bb_framework.enums.ProblemType;
import bb_framework.interfaces.Dataset;
import bb_framework.testFiles.Knapsack;
import bb_framework.testFiles.KnapsackBound;
import bb_framework.types.Coefficient;
import bb_framework.types.Value;
import bb_framework.types.Vector;
import bb_framework.utils.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Random;

public class BranchAndBoundTest {

    int NR_OF_TESTS = 10;
    int MAX_NR_OF_ELEMENTS = 10;
    int MAX_VAL = 550;
    int MAX_CONSTRAINT_VAL = 450;
    int MULTIPLIER = 5;

    Random rand = new Random();

    private Dataset buildKnapsackDataset(int size){
        Coefficient[] tmp = new Coefficient[size];
        for (int i = 0; i < size; i++) tmp[i] = new Value((double) (rand.nextInt(MAX_VAL) + 50));
        return new Vector(tmp);
    }

    private Constraint buildKnapsackConstraint(int size){
        double[] lhs = new double[size];
        for (int i = 0; i < size; i++) lhs[i] = rand.nextInt(MAX_CONSTRAINT_VAL) + 50;
        double rhs = (rand.nextInt(MAX_CONSTRAINT_VAL) / 2) * MULTIPLIER;
        return new Constraint(lhs,rhs, ConstraintType.LEQ);
    }

    @Test
    public void solve() throws Exception {
        for (int i = 0; i < NR_OF_TESTS; i++){
            int size = rand.nextInt(MAX_NR_OF_ELEMENTS) + 5;
            Dataset dataset = buildKnapsackDataset(size);
            Constraint cs = buildKnapsackConstraint(size);
            //double[] dataset = ;
            //Constraint cs = buildKnapsackConstraint(size);

            double[] solution = Cplex.ip_solve(dataset,new HashMap<>(),new Constraint[]{cs},ProblemType.MAXIMIZATION);
            double solution_value = 0;
            for(int j = 0; j < dataset.size(); j++) solution_value += (Double) dataset.get(j).getVal() * solution[j];

            Problem bestFirst = new Problem("BestFirst", new Constraint[]{cs}, new KnapsackBound(), NodeStrategy.BEST_FIRST, ProblemType.MAXIMIZATION,true,0.5);
            Problem depthFirst = new Problem("DepthFirst", new Constraint[]{cs}, new KnapsackBound(), NodeStrategy.DEPTH_FIRST, ProblemType.MAXIMIZATION,true,0.5);
            Problem breadthFirst = new Problem("BreadthFirst", new Constraint[]{cs}, new KnapsackBound(), NodeStrategy.BREADTH_FIRST, ProblemType.MAXIMIZATION,true,0.5);
            BranchAndBound bnb = new BranchAndBound(new Problem[]{bestFirst, depthFirst, breadthFirst},dataset);
            bnb.Solve(null);

            Result[] results = bnb.getResults();
            for(int k = 0; k < results.length; k++){
                Result tmp = results[k];
                double tmp1 = tmp.getObjectiveValue();
                double tmp2 = solution_value;
                Assert.assertTrue( tmp1 == tmp2);
                for (int l = 0; l < size; l++){
                    Assert.assertTrue(tmp.getSolution()[l] == solution[l]);
                }
            }
        }
    }

}