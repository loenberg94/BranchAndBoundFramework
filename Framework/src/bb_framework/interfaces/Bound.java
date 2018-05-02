package bb_framework.interfaces;

import bb_framework.utils.Constraint;

import java.util.HashMap;

/**
 *  Interface for user to specify lower- and upperbound funcions
 */
public interface Bound {

    /**
     * @param currentSolution used to get current state.
     * @param set can be used if necessary by user
     * @return lowerbound Double value for current node state
     * */
    double Lowerbound(HashMap<Integer,Double> currentSolution, double[] set, Constraint[] constraints);

    /**
     * @param currentSolution used to get current state.
     * @param set can be used if necessary by user
     * @return upperbound Double value for current node state
     * */
    double Upperbound(HashMap<Integer,Double> currentSolution, double[] set, Constraint[] constraints);
}
