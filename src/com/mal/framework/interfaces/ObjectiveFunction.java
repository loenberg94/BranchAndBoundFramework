package com.mal.framework.interfaces;

import com.mal.framework.utils.Node;

/**
 * Interface for user to specify the objective function for the problem
 */
public interface ObjectiveFunction {
    /**
     * This method calculates the value of the solution from the objective function
     * @param node represents solution
     * @return returns value of the given solution
     */
    double calculate(Node node);
}
