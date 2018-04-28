package com.mal.utils.cplex;

import com.mal.framework.enums.ProblemType;
import com.mal.framework.utils.Constraint;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

public class Cplex_org {
    public static double[] lp_relaxation(double[] coefficients, int[] currentSolution, int cS_index, Constraint[] constraints, ProblemType type) throws Exception {
        IloCplex cplex = new IloCplex();
        IloNumVar[] x = cplex.numVarArray(coefficients.length,0.0, 1.0);

        // Objective function
        IloLinearNumExpr obj = cplex.linearNumExpr();
        for(int i = 0; i < coefficients.length; i++){
            if (i < cS_index){
                x[i].setLB(currentSolution[i]);
                x[i].setUB(currentSolution[i]);
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
        if (status.equals(IloCplex.Status.Optimal)){
            return cplex.getValues(x);
        }
        else {
            throw new Exception("No optimal solution found");
        }
    }
}
