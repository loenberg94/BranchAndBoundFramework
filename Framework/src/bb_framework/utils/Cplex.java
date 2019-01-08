package bb_framework.utils;

import bb_framework.enums.ProblemType;
import bb_framework.exceptions.IncorrectCoefficientTypeException;
import bb_framework.interfaces.Dataset;
import bb_framework.types.Coefficient;
import bb_framework.types.Index;
import bb_framework.utils.Constraint;
import ilog.concert.*;
import ilog.cplex.*;

import java.util.HashMap;

@SuppressWarnings("Duplicates")
public class Cplex {
    static IloCplex cplex;

    static {
        try {
            cplex = new IloCplex();
        } catch (IloException e) {
            e.printStackTrace();
        }
    }

    public static double[] lp_relaxation(Dataset coefficients, Node currentSolution, Constraint[] constraints, ProblemType type) throws Exception {
        IloNumVar[] x = cplex.numVarArray(coefficients.size(),0.0, 1.0);
        cplex.setOut(null);

        DisjointSet ds = new DisjointSet(coefficients.size());

        // Objective function
        IloLinearNumExpr obj = cplex.linearNumExpr();

        int prev = -1;
        Node curr = currentSolution;
        while (curr.depth > -1){
            if(prev != -1){
                ds.Union(curr.index,prev);
            }
            prev = curr.index;

            int val = curr.included?1:0;
            x[curr.index].setLB(val);
            x[curr.index].setUB(val);
            Coefficient tmp = coefficients.get(curr.index);
            if(tmp.isIndex()){
                obj.addTerm(1, x[(int) tmp.getVal()]);
            }
            else{
                obj.addTerm((Double) coefficients.get(curr.index).getVal(), x[curr.index]);
            }
            curr = curr.getParent();
        }

        int csSet = prev==-1?prev:ds.Find(prev);
        for(int i = 0; i < coefficients.size(); i++){
            if(csSet != ds.Find(i)){
                obj.addTerm((Double) coefficients.get(i).getVal(), x[i]);
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

            for(int i = 0; i < cons.getLhs().length; i++){
                Coefficient constraintCoeff = cons.getLhs()[i];
                if(cons.isIndexConstraint()){
                    if(constraintCoeff.isIndex()){
                        int index = (int) constraintCoeff.getVal();
                        exprs.addTerm(1, x[index]);
                    }
                    else{
                        throw new IncorrectCoefficientTypeException("Index", constraintCoeff.toString());
                    }
                }
                else{
                    if(constraintCoeff.isValue()){
                        exprs.addTerm((Double) cons.getLhs()[i].getVal(), x[i]);
                    }
                    else{
                        throw new IncorrectCoefficientTypeException("Value", constraintCoeff.toString());
                    }
                }
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

        // Clean-up
        cplex.clearModel();
        x = null;
        obj.clear();
        obj = null;

        return retArr;
    }

    public static double[] ip_solve(Dataset coefficients, HashMap<Integer,Double> currentSolution, Constraint[] constraints, ProblemType type) throws Exception {
        IloNumVar[] x = cplex.boolVarArray(coefficients.size());
        cplex.setOut(null);

        // Objective function
        IloLinearNumExpr obj = cplex.linearNumExpr();

        for(int i = 0; i < coefficients.size(); i++){
            if (currentSolution.containsKey(i)){
                x[i].setLB(currentSolution.get(i));
                x[i].setUB(currentSolution.get(i));
                obj.addTerm((Double) coefficients.get(i).getVal(), x[i]);
            }
            else{
                obj.addTerm((Double) coefficients.get(i).getVal(), x[i]);
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
            for(int i = 0; i < cons.getLhs().length; i++){
                exprs.addTerm((Double)cons.getLhs()[i].getVal(), x[i]);
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

        double[] retarr = null;
        if (status.equals(IloCplex.Status.Optimal) || status.equals(IloCplex.Status.Feasible)){
            retarr = cplex.getValues(x);
        }
        else {
            throw new Exception("No optimal solution found");
        }

        // Clean-up
        cplex.clearModel();
        x = null;
        obj.clear();
        obj = null;

        return retarr;
    }
}
