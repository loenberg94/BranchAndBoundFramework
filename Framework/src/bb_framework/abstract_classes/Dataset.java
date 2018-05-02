package bb_framework.abstract_classes;

import bb_framework.utils.Node;
import bb_framework.utils.Subset;

import java.util.HashMap;
import java.util.Map;

/**
 * Interface for the user to specify the problem specific dataset
 */
public abstract class Dataset {
    Map<String, Subset> set = new HashMap<>();
    Map<String, Subset> constraint_set = new HashMap<>();

    /**
     *
     * @param s_name
     * @param s_set
     */
    public void addSubset(String s_name, Subset s_set){
        this.set.put(s_name, s_set);
    }

    /**
     *
     * @param cs_name
     * @param cs_set
     */
    public void addConstraintSubset(String cs_name,Subset cs_set){
        this.constraint_set.put(cs_name,cs_set);
    }

    /**
     * This method is for the user to specify what the next value to branch on is
     * @param <T> problem specific type, used to branch on
     * @param node to get state of bb
     * @return returns value to be evaluated
     */
    public abstract <T> T nextValue(Node node);
}
