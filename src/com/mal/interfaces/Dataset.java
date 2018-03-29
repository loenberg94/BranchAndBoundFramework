package com.bb.interfaces;

import com.bb.Node;

/**
 * Interface for the user to specify the problem specific dataset
 */
public interface Dataset {
    /**
     * This method is for the user to specify what the next value to branch on is
     * @param <T> problem specific type, used to branch on
     * @param node to get state of bb
     * @return returns value to be evaluated
     */
    <T> T nextValue(Node node);
}
