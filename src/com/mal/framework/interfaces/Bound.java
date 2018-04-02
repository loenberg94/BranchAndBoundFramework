package com.mal.framework.interfaces;

import com.mal.framework.utils.Node;
import com.mal.framework.abstract_classes.Dataset;

/**
 *  Interface for user to specify lower- and upperbound funcions
 */
public interface Bound {

    /**
     * @param node used to get current state.
     * @param set can be used if necessary by user
     * @return lowerbound value for current node state
     * */
    double Lowerbound(Node node, Dataset set);

    /**
     * @param node used to get current state.
     * @param set can be used if necessary by user
     * @return upperbound value for current node state
     * */
    double Upperbound(Node node, Dataset set);
}
