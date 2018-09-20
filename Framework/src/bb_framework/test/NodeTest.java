package bb_framework.test;

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

    private double[] buildDataset(int size){
        double[] tmp = new double[size];
        for(int i = 0; i < size; i++){
            tmp[i] = rand.nextInt(MAX_VAL_DATASET) + 10;
        }
        return tmp;
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

    private int calculateValue(double[] data, double[] solution){
        int sum = 0;
        for (int i = 0; i < data.length; i++){
            sum += data[i] * solution[i];
        }
        return sum;
    }

    @Test
    public void getObjectiveValue() {
        for(int i = 0; i < NR_OF_TESTS; i++){
            int size = rand.nextInt(MAX_SIZE_SOLUTION);
            double[] bSolution = buildBoundSolution(size);
            double[] dataset = buildDataset(size);
            Node n = buildNodeSolution(bSolution, size);

            int expectedObjectiveValue = calculateValue(dataset, bSolution);
            int actualObjectiveValue = (int) n.getObjectiveValue(dataset);

            Assert.assertTrue(expectedObjectiveValue == actualObjectiveValue);
        }
    }
}