package bb_framework.utils;

import bb_framework.enums.ConstraintType;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.Map;

public class Constraint {
    boolean indexConstraint;
    HashMap<Integer,Boolean> s_lhs;
    double[] d_lhs;
    ConstraintType cT;
    double rhs;

    public Pair<Boolean,Boolean> checkConstraint(Node node, int nextIndex){
        double sum = 0;
        Node curr = node;
        if(indexConstraint){
            boolean relevant = s_lhs.containsKey(nextIndex);
            //for(String s:s_lhs){ relevant |= (s.equals(String.valueOf(nextIndex)) || currentSolution.containsKey(Integer.valueOf(s))); }

            while (curr.depth > -1){
                relevant |= s_lhs.containsKey(curr.index);
                curr = curr.getParent();
            }

            if(relevant){
                /*for(String s:s_lhs){
                    int tmp = Integer.valueOf(s);
                    if(tmp == nextIndex){
                        sum += 1;
                    }
                    else{
                        sum += currentSolution.getOrDefault(tmp, 0.0);
                    }
                }*/

                if(s_lhs.containsKey(nextIndex)) sum += 1;

                curr = node;
                while (curr.depth > -1){
                    if (s_lhs.containsKey(curr.index)) sum += curr.included ? 1:0;
                    curr = curr.getParent();
                }
            }
            else{
                return new Pair<>(false,true);
            }
        }
        else{
            /*for(int i = 0; i < d_lhs.length; i++){
                if(currentSolution.containsKey(i)){
                    sum += currentSolution.get(i) * d_lhs[i];
                }
                else if(i == nextIndex){
                    sum += d_lhs[i];
                }
            }*/
            while (curr.depth > -1){
                int i = curr.included? 1:0;
                sum +=  i * d_lhs[curr.index];
                curr = curr.getParent();
            }
            sum += d_lhs[nextIndex];
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
        s_lhs = new HashMap<>();
        for(String s : leftHandSide) s_lhs.put(Integer.valueOf(s),false);
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

    public boolean isIndexConstraint() {
        return indexConstraint;
    }
}
