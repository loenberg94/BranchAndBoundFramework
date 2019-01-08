package bb_framework.test;

import bb_framework.enums.ProblemType;
import bb_framework.exceptions.IncorrectNrOfIndecesException;
import bb_framework.interfaces.Dataset;
import bb_framework.testFiles.Knapsack;
import bb_framework.types.Value;
import bb_framework.utils.Constraint;
import bb_framework.utils.Cplex;
import bb_framework.utils.DisjointSet;
import bb_framework.utils.Node;
import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Random;

import static bb_framework.enums.ConstraintType.EQUALS;
import static bb_framework.enums.ConstraintType.GEQ;
import static bb_framework.enums.ConstraintType.LEQ;

@SuppressWarnings("Duplicates")
public class CplexTest {

    Random rand = new Random();

    int NR_OF_TESTS = 100;

    int MAX_VAL = 450;
    int MAX_CONSTRAINT_VAL = 350;
    int MAX_SIZE = 20;

    int MULTIPLIER = 5;


    private HashMap<Integer,Double> buildHashMapSolution(int size, int count, int[] aSolution){
        HashMap<Integer,Double> tmp = new HashMap<>();
        while(count > 0){
            int i = rand.nextInt(size);
            if(!tmp.containsKey(i)){
                tmp.put(i, (double) aSolution[i]);
                count--;
            }
        }
        return tmp;
    }

    private Node buildNodeSolution(HashMap<Integer,Double> hmSolution){
        Node root = new Node(null,false);
        for(int key: hmSolution.keySet()){
            root = new Node(root,hmSolution.get(key) == 1,key);
        }
        return root;
    }

    private Constraint buildConstraint(int size){
        Value[] lhs = new Value[size];
        for(int i = 0; i < size; i++) lhs[i] = new Value((double) (rand.nextInt(MAX_CONSTRAINT_VAL) + 50));
        double rhs = rand.nextInt(MAX_CONSTRAINT_VAL) / 2 * MULTIPLIER;
        return new Constraint(lhs,rhs,LEQ);
    }

    private double[] oldLPRmethod(HashMap<Integer,Double> cSolution, Constraint cs, Dataset dataset) throws IloException, IncorrectNrOfIndecesException {
        IloCplex cplex = new IloCplex();
        IloNumVar[] x = cplex.numVarArray(dataset.size(),0.0, 1.0);
        cplex.setOut(null);

        DisjointSet ds = new DisjointSet(dataset.size());

        // Objective function
        IloLinearNumExpr obj = cplex.linearNumExpr();
        for(int i = 0; i < dataset.size(); i++){
            if (cSolution.containsKey(i)){
                x[i].setLB(cSolution.get(i));
                x[i].setUB(cSolution.get(i));
                obj.addTerm((Double) dataset.get(i).getVal(), x[i]);
            }
            else{
                obj.addTerm((Double) dataset.get(i).getVal(), x[i]);
            }
        }

        cplex.addMaximize(obj);

        // Constraints
        IloLinearNumExpr exprs = cplex.linearNumExpr();
        for(int i = 0; i < cs.getLhs().length; i++){
            exprs.addTerm((Double) cs.getLhs()[i].getVal(), x[i]);
        }
        switch (cs.getcT()){
            case LEQ:
                cplex.addLe(exprs,cs.getRhs());
                break;
            case EQUALS:
                cplex.addGe(exprs,cs.getRhs());
                break;
            case GEQ:
                cplex.addEq(exprs,cs.getRhs());
                break;
        }

        cplex.solve();

        IloCplex.Status status = cplex.getStatus();
        double[] retArr = null;
        if(status == IloCplex.Status.Optimal || status == IloCplex.Status.Feasible){
            retArr = cplex.getValues(x);
        }
        return retArr;
    }

    @Test
    public void lp_relaxation() {
        int[] actualSolution = new int[]{1, 1, 0, 0, 0, 0, 1, 1, 1, 0, 1, 0, 1, 0, 1};
        Knapsack problem = Knapsack.CreateNew();
        for(int i = 0; i < NR_OF_TESTS; i++){
            Dataset dataset = problem.getDataset();
            Constraint[] constraint = problem.getConstraints();
            HashMap<Integer, Double> hmSolution = buildHashMapSolution(dataset.size(),rand.nextInt((int) Math.sqrt(dataset.size())),actualSolution);
            Node nodeSolution = buildNodeSolution(hmSolution);

            try {
                double[] oldMethodResult = oldLPRmethod(hmSolution,constraint[0],dataset);
                double[] newMethodResult = Cplex.lp_relaxation(dataset,nodeSolution,constraint, ProblemType.MAXIMIZATION);

                for(int j = 0; j < dataset.size(); j++){
                    Assert.assertTrue(oldMethodResult[j] == newMethodResult[j]);
                }
            } catch (Exception e) {
                System.out.println("Exception thrown");
                e.printStackTrace();
            }
        }
    }
}