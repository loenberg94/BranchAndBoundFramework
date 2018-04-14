package com.mal.framework.interfaces;

import com.mal.framework.utils.Node;
import com.mal.framework.abstract_classes.Dataset;

/**
 *  Interface for user to specify lower- and upperbound funcions
 */
public interface Bound {

    /**
     * @param currentSolution used to get current state.
     * @param set can be used if necessary by user
     * @return lowerbound Double value for current node state
     * */
    double Lowerbound(int[] currentSolution, double[] set);

    /**
     * @param currentSolution used to get current state.
     * @param set can be used if necessary by user
     * @return upperbound Double value for current node state
     * */
    double Upperbound(int[] currentSolution, double[] set);
}
