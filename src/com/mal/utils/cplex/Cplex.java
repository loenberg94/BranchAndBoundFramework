package com.mal.utils.cplex;

import com.mal.framework.enums.ProblemType;
import com.mal.framework.utils.Constraint;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

import java.util.HashMap;

public class Cplex {
    public static double[] lp_relaxation(double[] coefficients, HashMap<Integer,Float> currentSolution, Constraint[] constraints, ProblemType type) throws Exception {
        IloCplex cplex = new IloCplex();
        IloNumVar[] x = cplex.numVarArray(coefficients.length,0.0, 1.0);

        // Objective function
        IloLinearNumExpr obj = cplex.linearNumExpr();
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
            for(int i = 0; i < cons.getLhs().length; i++){
                exprs.addTerm(cons.getLhs()[i], x[i]);
            }
            switch (cons.getcT()){
                case LEQ:
                    cplex.addLe(exprs,cons.getRhs());
                case EQUALS:
                    cplex.addGe(exprs,cons.getRhs());
                case GEQ:
                    cplex.addEq(exprs,cons.getRhs());
            }
        }

        cplex.solve();

        IloCplex.Status status = cplex.getStatus();
        return cplex.getValues(x);
    }

    public static double[] ip_solve(double[] coefficients, Constraint[] constraints, ProblemType type) throws Exception {
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
            for(int i = 0; i < cons.getLhs().length; i++){
                exprs.addTerm(cons.getLhs()[i], x[i]);
            }
            switch (cons.getcT()){
                case LEQ:
                    cplex.addLe(exprs,cons.getRhs());
                case EQUALS:
                    cplex.addGe(exprs,cons.getRhs());
                case GEQ:
                    cplex.addEq(exprs,cons.getRhs());
            }
        }

        cplex.solve();

        IloCplex.Status status = cplex.getStatus();
        if (status.equals(IloCplex.Status.Optimal)){
            System.out.printf("\n\n%f\n",cplex.getObjValue());
            return cplex.getValues(x);
        }
        else {
            throw new Exception("No optimal solution found");
        }
    }
}
