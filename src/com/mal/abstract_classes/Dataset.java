package com.mal.abstract_classes;

import com.mal.Node;
import com.mal.utils.Subset;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

/**
 * Interface for the user to specify the problem specific dataset
 */
public abstract class Dataset {

    Map<String, Subset> set = new HashMap<>();

    public void addSubset(String s_name, Subset s_set){
        this.set.put(s_name, s_set);
    }

    /**
     * This method is for the user to specify what the next value to branch on is
     * @param <T> problem specific type, used to branch on
     * @param node to get state of bb
     * @return returns value to be evaluated
     */
    public abstract <T> T nextValue(Node node);
}
