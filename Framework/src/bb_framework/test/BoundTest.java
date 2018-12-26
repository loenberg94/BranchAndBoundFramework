package bb_framework.test;

import bb_framework.interfaces.Bound;
import bb_framework.testFiles.Knapsack;
import bb_framework.testFiles.KnapsackBound;
import bb_framework.utils.DisjointSet;
import bb_framework.utils.Node;
import bb_framework.utils.Problem;
import org.junit.Test;

import static org.junit.Assert.*;

public class BoundTest {

    @Test
    public void lowerbound() {
        Knapsack pb = Knapsack.CreateNew();

        // Node solution
        Node root = new Node(null,false);
        Node n0 = new Node(root,true,1);
        Node n1 = new Node(n0,true,2);
        Node n2 = new Node(n1,false,3);
        Node n3 = new Node(n2,false,4);
        Node n4 = new Node(n3,false,5);
        Node n5 = new Node(n4,false,6);
        Node n6 = new Node(n5,false,7);
        Node n7 = new Node(n6,true,8);
        Node n8 = new Node(n7,true,9);
        Node n9 = new Node(n8,false,11);
        Node n10 = new Node(n9,true,12);
        Node n11 = new Node(n10,true,13);
        Node n12 = new Node(n11,true,14);

        Bound lb = new KnapsackBound();
        //double p_val = n12.getObjectiveValue(pb.getDataset());

        //double tmp = lb.Lowerbound(n12,pb.getDataset(),pb);
        //System.out.println(tmp);
    }
}