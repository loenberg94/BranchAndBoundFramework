package bb_framework.test;

import bb_framework.interfaces.Dataset;
import bb_framework.types.Coefficient;
import bb_framework.types.Value;
import bb_framework.types.Vector;
import bb_framework.utils.DisjointSet;
import bb_framework.utils.Node;
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

public class NodeTest {
    int NR_OF_TESTS = 100;
    int MAX_SIZE_SOLUTION = 100;
    int MAX_VAL_DATASET = 490;

    Random rand = new Random();


    private void testTransfer(Node solution, double[] boundSolution){
        double[] s = new double[boundSolution.length];
        Node curr = solution;
        while (curr.depth > -1){
            s[curr.index] = curr.included?1:0;
            curr = curr.getParent();
        }

        for(int i = 0; i < s.length; i++){
            System.out.println();
            Assert.assertTrue(boundSolution[i] == s[i]);
        }
    }

    private Node buildNodeSolution(double[] bSolution, int elements){
        Node root = new Node(null,false);
        DisjointSet ds = new DisjointSet(bSolution.length);

        int csSet = -1;
        while (elements > 0){
            int i = rand.nextInt(bSolution.length);
            if(csSet == -1){
                csSet = ds.Find(i);
                root = new Node(root,bSolution[i] == 1? true:false,i);
                elements--;
            }
            else {
                if(ds.Find(i) != ds.Find(csSet)){
                    root = new Node(root,bSolution[i] == 1? true:false,i);
                    ds.Union(csSet,i);
                    elements--;
                }
            }
        }
        return root;
    }

    private double[] buildBoundSolution(int size){
        double[] tmp = new double[size];
        for(int i = 0; i < size; i++){
            tmp[i] = rand.nextInt(100) < 50? 0:1;
        }
        return tmp;
    }

    private Dataset buildDataset(int size){
        Coefficient[] tmp = new Coefficient[size];
        for(int i = 0; i < size; i++){
            tmp[i] = new Value(Double.valueOf(rand.nextInt(MAX_VAL_DATASET) + 10));
        }
        return new Vector(tmp,tmp.length);
    }

    @Test
    public void transferBoundSolutionToSolution() {
        for(int i = 0; i < NR_OF_TESTS; i++){
            int size = rand.nextInt(MAX_SIZE_SOLUTION) + 5;
            double[] boundSolution = buildBoundSolution(size);
            Node n = buildNodeSolution(boundSolution, rand.nextInt(size));
            n.setCurrentBoundSolution(boundSolution);
            n = n.transferBoundSolutionToSolution();
            testTransfer(n,boundSolution);
        }
    }

    private int calculateValue(Dataset data, double[] solution){
        int sum = 0;
        for (int i = 0; i < data.size(); i++){
            sum += (Double) data.get(i).getVal() * solution[i];
        }
        return sum;
    }

    @Test
    public void getObjectiveValue() {
        for(int i = 0; i < NR_OF_TESTS; i++){
            int size = rand.nextInt(MAX_SIZE_SOLUTION);
            double[] bSolution = buildBoundSolution(size);
            Dataset dataset = buildDataset(size);
            Node n = buildNodeSolution(bSolution, size);

            int expectedObjectiveValue = calculateValue(dataset, bSolution);
            int actualObjectiveValue = (int) n.getObjectiveValue(dataset);

            Assert.assertTrue(expectedObjectiveValue == actualObjectiveValue);
        }
    }

    @Test
    public void free(){
        Node root = new Node(null,false);
        Node n1 = new Node(root,true);
        Node n2 = new Node(n1,true);
        Node stop = new Node(n2,true);
        Node n3 = new Node(n2,true);
        Node n4 = new Node(n3,true);
        Node n5 = new Node(n4,true);
        n5.free();
        Assert.assertTrue(n5.getParent() == null && n4.getParent() == null);
        Assert.assertTrue(stop.getParent() != null && n2.getParent() != null && n1.getParent() != null);
    }
}