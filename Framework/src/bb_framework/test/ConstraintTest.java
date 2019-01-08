package bb_framework.test;

import bb_framework.enums.ConstraintType;
import bb_framework.types.Index;
import bb_framework.utils.Constraint;
import bb_framework.utils.Node;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class ConstraintTest {

    private Node buildNodeSolution(HashMap<Integer,Boolean> hmSolution){
        Node root = new Node(null,false);
        for(int key: hmSolution.keySet()){
            root = new Node(root,hmSolution.get(key),key);
        }
        return root;
    }



    @Test
    public void checkConstraint() {
        HashMap<Integer, Boolean> testset = new HashMap<>();
        testset.put(0,true);
        testset.put(2,true);
        testset.put(3,true);
        Node solution = buildNodeSolution(testset);

        Index[] indices = new Index[]{new Index(0),new Index(1),new Index(4)};
        Constraint constraint = new Constraint(indices,1,ConstraintType.EQUALS);
        System.out.println(constraint.checkConstraint(solution,4));
    }

    @Test
    public void containsIndex(){
        Index[] indeces = new Index[]{new Index(1), new Index(2), new Index(3), new Index(4), new Index(5)};
        Constraint tmp = new Constraint(indeces,5,ConstraintType.EQUALS);
        System.out.println(tmp.containsIndex(5));
    }

}