package bb_framework.utils;

import bb_framework.enums.ConstraintType;
import bb_framework.types.Coefficient;
import bb_framework.types.Index;
import bb_framework.types.Value;
import javafx.util.Pair;

public class Constraint {
    private boolean indexConstraint;
    private Coefficient[] lhs;
    private ConstraintType cT;
    private double rhs;

    /*public boolean containsIndex(int sIndex){
        int i = lhs.length / 2;
        double stepSize = i;
        while(true){
            int cIndex = (Integer) lhs[i].getVal();
            if(cIndex == sIndex) return true;
            if(stepSize == 0.5) break;
            stepSize /= 2;
            if(cIndex < sIndex){
                i += (int) Math.ceil(stepSize);
            }
            else{
                i -= (int) Math.ceil(stepSize);
            }
        }
        return false;
    }*/

    public boolean containsIndex(int sIndex){
        for(Coefficient i : lhs){
            if((int)i.getVal() == sIndex){
                return true;
            }
        }
        return false;
    }

    public Pair<Boolean,Boolean> checkConstraint(Node node, int nextIndex){
        double sum = 0;
        Node curr = node;
        if(indexConstraint){
            boolean relevant = containsIndex(nextIndex);

            while (curr.depth > -1){
                relevant |= containsIndex(curr.index);
                curr = curr.getParent();
            }

            if(relevant){
                if(containsIndex(nextIndex)) sum += 1;

                curr = node;
                while (curr.depth > -1){
                    if (containsIndex(curr.index)) sum += curr.included ? 1:0;
                    curr = curr.getParent();
                }
            }
            else{
                return new Pair<>(false,true);
            }
        }
        else{
            while (curr.depth > -1){
                int i = curr.included? 1:0;
                sum +=  i * (Double)lhs[curr.index].getVal();
                curr = curr.getParent();
            }
            sum += (Double)lhs[nextIndex].getVal();
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

    public Constraint(Value[] leftHandSide, double rightHandSide, ConstraintType type){
        lhs = leftHandSide;
        rhs = rightHandSide;
        cT = type;
        indexConstraint = false;
    }

    public Constraint(Index[] leftHandSide, double rightHandSide, ConstraintType type){
        lhs = leftHandSide;
        rhs = rightHandSide;
        cT = type;
        indexConstraint = true;
    }

    //TODO: ONLY TEMPORARY FOR DEBUGGING PURPOSE
    public Constraint(double[] leftHandSide, double rightHandSide, ConstraintType type){
        Value[] tmp = new Value[leftHandSide.length];
        for(int i = 0; i < leftHandSide.length; i++) tmp[i] = new Value(leftHandSide[i]);
        lhs = tmp;
        rhs = rightHandSide;
        cT = type;
        indexConstraint = false;
    }

    public double getRhs() {
        return rhs;
    }

    public Coefficient[] getLhs() {
        return lhs;
    }

    public ConstraintType getcT() {
        return cT;
    }

    public boolean isIndexConstraint() {
        return indexConstraint;
    }
}
