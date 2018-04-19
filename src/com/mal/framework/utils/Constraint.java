package com.mal.framework.utils;

import com.mal.framework.enums.ConstraintType;

public class Constraint {
    double[] lhs;
    ConstraintType cT;
    double rhs;

    public boolean checkConstraint(int[] currentSolution){
        double sum = 0;
        for(int i = 0; i < currentSolution.length; i++){
            sum += lhs[i] * currentSolution[i];
        }

        switch (cT){
            case LEQ:
                return sum <= rhs;
            case EQUALS:
                return sum == rhs;
            case GEQ:
                return sum >= rhs;
            default:
                return false;
        }
    }

    public Constraint(double[] leftHandSide, double rightHandSide, ConstraintType type){
        lhs = leftHandSide;
        rhs = rightHandSide;
        cT = type;
    }

    public double getRhs() {
        return rhs;
    }

    public double[] getLhs() {
        return lhs;
    }

    public ConstraintType getcT() {
        return cT;
    }
}
