package bb_framework.utils;

import bb_framework.enums.ProblemType;
import bb_framework.utils.Constraint;
import ilog.concert.*;
import ilog.cplex.*;

import java.util.HashMap;

public class Cplex {
    IloCplex cplex;
    IloNumVar[] x;
    IloLinearNumExpr obj;

    public Cplex() throws IloException {
        cplex = new IloCplex();
    }

    public double[] lp_relaxation(double[] coefficients, HashMap<Integer,Double> currentSolution, Constraint[] constraints, ProblemType type) throws Exception {
        x = cplex.numVarArray(coefficients.length,0.0, 1.0);
        cplex.setOut(null);
        // Objective function
        obj = cplex.linearNumExpr();
        for(int i = 0; i < coefficients.length; i++){
            if (currentSolution.containsKey(i)){
                x[i].setLB(currentSolution.get(i));
                x[i].setUB(currentSolution.get(i));
                obj.addTerm(coefficients[i], x[i]);
            }
            else{
                obj.addTerm(coefficients[i], x[i]);
            }
        }

        if (type == ProblemType.MAXIMIZATION){
            cplex.addMaximize(obj);
        }
        else{
            cplex.addMinimize(obj);
        }

        // Constraints
        for(Constraint cons:constraints){
            IloLinearNumExpr exprs = cplex.linearNumExpr();
            for(int i = 0; i < cons.getD_lhs().length; i++){
                exprs.addTerm(cons.getD_lhs()[i], x[i]);
            }
            switch (cons.getcT()){
                case LEQ:
                    cplex.addLe(exprs,cons.getRhs());
                    break;
                case EQUALS:
                    cplex.addGe(exprs,cons.getRhs());
                    break;
                case GEQ:
                    cplex.addEq(exprs,cons.getRhs());
                    break;
            }
        }

        cplex.solve();

        IloCplex.Status status = cplex.getStatus();
        double[] retArr = null;
        if(status == IloCplex.Status.Optimal || status == IloCplex.Status.Feasible){
            retArr = cplex.getValues(x);
        }
        return retArr;
    }

    public void release(){
        try {
            cplex.clearModel();
            for(int i = 0; i < x.length; i++){
                x[i] = null;
            }
            x = null;
            obj.clear();
            obj = null;
        } catch (IloException e) {
            e.printStackTrace();
        }
    }

    public double[] ip_solve(double[] coefficients, Constraint[] constraints, ProblemType type) throws Exception {
        IloCplex cplex = new IloCplex();
        IloNumVar[] x = cplex.boolVarArray(coefficients.length);

        // Objective function
        IloLinearNumExpr obj = cplex.linearNumExpr();
        for(int i = 0; i < coefficients.length; i++){
            obj.addTerm(coefficients[i], x[i]);
        }

        if (type == ProblemType.MAXIMIZATION){
            cplex.addMaximize(obj);
        }
        else{
            cplex.addMinimize(obj);
        }

        // Constraints
        for(Constraint cons:constraints){
            IloLinearNumExpr exprs = cplex.linearNumExpr();
            for(int i = 0; i < cons.getD_lhs().length; i++){
                exprs.addTerm(cons.getD_lhs()[i], x[i]);
            }
            switch (cons.getcT()){
                case LEQ:
                    cplex.addLe(exprs,cons.getRhs());
                    break;
                case EQUALS:
                    cplex.addGe(exprs,cons.getRhs());
                    break;
                case GEQ:
                    cplex.addEq(exprs,cons.getRhs());
            }
        }

        cplex.solve();

        IloCplex.Status status = cplex.getStatus();
        if (status.equals(IloCplex.Status.Optimal)){
            System.out.printf("\n\n%f\n",cplex.getObjValue());
            double[] retarr = cplex.getValues(x);
            /*x = null;
            cplex = null;
            obj = null;*/
            return retarr;
        }
        else {
            throw new Exception("No optimal solution found");
        }
    }
}
