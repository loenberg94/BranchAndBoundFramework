package bb_framework.utils;

import bb_framework.enums.ConstraintType;
import javafx.util.Pair;

import java.util.Map;

public class Constraint {
    boolean indexConstraint;
    String[] s_lhs;
    double[] d_lhs;
    ConstraintType cT;
    double rhs;

    public Pair<Boolean,Boolean> checkConstraint(Map<Integer,Double> currentSolution, int nextIndex){
        double sum = 0;
        if(indexConstraint){
            boolean relevant = false;
            for(String s:s_lhs){ relevant |= (s.equals(String.valueOf(nextIndex)) || currentSolution.containsKey(Integer.valueOf(s))); }
            if(relevant){
                for(String s:s_lhs){
                    int tmp = Integer.valueOf(s);
                    if(tmp == nextIndex){
                        sum += 1;
                    }
                    else{
                        sum += currentSolution.getOrDefault(tmp,0.0);
                    }
                }
            }
            else{
                return new Pair<>(false,true);
            }
        }
        else{
            for(int i = 0; i < d_lhs.length; i++){
                if(currentSolution.containsKey(i)){
                    sum += currentSolution.get(i) * d_lhs[i];
                }
                else if(i == nextIndex){
                    sum += d_lhs[i];
                }
            }
        }

        switch (cT){
            case LEQ:
                return new Pair(sum <= rhs,false);
            case LT:
                return new Pair(sum < rhs,false);
            case EQUALS:
                if(sum==rhs){
                    return new Pair(true,true);
                }
                else if(sum < rhs){
                    return new Pair(false,true);
                }
                else{
                    return new Pair(false,false);
                }
            case GEQ:
                return new Pair(sum >= rhs,false);
            case GT:
                return new Pair(sum > rhs,false);
            default:
                return new Pair(true,false);
        }
    }

    public Constraint(double[] leftHandSide, double rightHandSide, ConstraintType type, boolean i_constraint){
        d_lhs = leftHandSide;
        rhs = rightHandSide;
        cT = type;
        indexConstraint = i_constraint;
    }

    public Constraint(String[] leftHandSide, double rightHandSide, ConstraintType type, boolean i_constraint){
        s_lhs = leftHandSide;
        rhs = rightHandSide;
        cT = type;
        indexConstraint = i_constraint;
    }

    public double getRhs() {
        return rhs;
    }

    public double[] getD_lhs() {
        return d_lhs;
    }

    public ConstraintType getcT() {
        return cT;
    }
}
