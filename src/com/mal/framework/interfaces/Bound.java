package com.mal.framework.interfaces;

import com.mal.framework.utils.Constraint;
import com.mal.framework.utils.Node;
import com.mal.framework.abstract_classes.Dataset;

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
    float Lowerbound(HashMap<Integer,Float> currentSolution, double[] set, Constraint[] constraints);

    /**
     * @param currentSolution used to get current state.
     * @param set can be used if necessary by user
     * @return upperbound Double value for current node state
     * */
    float Upperbound(HashMap<Integer,Float> currentSolution, double[] set, Constraint[] constraints);
}
