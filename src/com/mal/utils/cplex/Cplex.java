package com.mal.utils.cplex;

import com.mal.framework.enums.ProblemType;
import com.mal.framework.utils.Constraint;
import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

public class Cplex {
    public double[] lp_relaxation(double[] coefficients, int[] currentSolution, int cS_index, Constraint[] constraints, ProblemType type) throws Exception {
        IloCplex cplex = new IloCplex();
        IloNumVar[] x = cplex.numVarArray(coefficients.length,0.0, Double.MAX_VALUE);

        // Objective function
        IloLinearNumExpr obj = cplex.linearNumExpr();
        for(int i = 0; i < coefficients.length; i++){
            if (i <= cS_index){
                obj.addTerm(coefficients[i], cplex.intVar(currentSolution[i], currentSolution[i]));
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
                if(i <= cS_index){
                    exprs.addTerm(cons.getLhs()[i], cplex.intVar(currentSolution[i], currentSolution[i]));
                }
                else{
                    exprs.addTerm(cons.getLhs()[i], x[i]);
                }
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
            return cplex.getValues(x);
        }
        else {
            throw new Exception("No optimal solution found");
        }
    }
}
