package com.mal;

import bb_framework.BranchAndBound;
import bb_framework.enums.NodeStrategy;
import bb_framework.enums.ProblemType;
import bb_framework.interfaces.Bound;
import bb_framework.utils.*;
import bb_framework.testFiles.Knapsack;
import com.mal.UI.utils.*;
import utils.MemoryCompiler;

import java.io.*;
import java.util.HashMap;

@SuppressWarnings("Duplicates")
public class Main {

    static String teststr =
            "package extraClasses;\n" +
                    "\n" +
                    "import bb_framework.interfaces.Bound;\n" +
                    "import bb_framework.interfaces.Dataset;\n" +
                    "import bb_framework.utils.DisjointSet;\n" +
                    "import bb_framework.utils.Node;\n" +
                    "import bb_framework.utils.Problem;\n" +
                    "\n" +
                    "@SuppressWarnings(\"Duplicates\")\n" +
                    "public class knapsack_bound implements Bound{\n" +
                    "        @Override\n" +
                    "        public double Lowerbound(Node node, Dataset set, Problem problem) {\n" +
                    "            float sum = 0;\n" +
                    "            float weight = 0;\n" +
                    "            DisjointSet ds = new DisjointSet(set.size());\n" +
                    "\n" +
                    "            int prev = -1;\n" +
                    "            Node curr = node;\n" +
                    "            while (curr.depth > -1){\n" +
                    "                if(prev != -1){\n" +
                    "                    ds.Union(prev, curr.index);\n" +
                    "                }\n" +
                    "                prev = curr.index;\n" +
                    "                sum += curr.included?(Double) set.get(curr.index).getVal():0;\n" +
                    "                weight += curr.included?problem.getConstraints()[0].getD_lhs()[curr.index]:0;\n" +
                    "                curr = curr.getParent();\n" +
                    "            }\n" +
                    "\n" +
                    "            int csSet = prev==-1?prev:ds.Find(prev);\n" +
                    "            for (int i = 0; i < set.size(); i++){\n" +
                    "                if(csSet == -1 || ds.Find(csSet) != ds.Find(i)){\n" +
                    "                    if(weight + problem.getConstraints()[0].getD_lhs()[i] <= problem.getConstraints()[0].getRhs()){\n" +
                    "                        sum += (Double) set.get(i).getVal();\n" +
                    "                        weight += problem.getConstraints()[0].getD_lhs()[i];\n" +
                    "                    }\n" +
                    "                }\n" +
                    "            }\n" +
                    "            return sum;\n" +
                    "        }\n" +
                    "\n" +
                    "        @Override\n" +
                    "        public double Upperbound(Node node, Dataset set, Problem problem) {\n" +
                    "            return 30000000;\n" +
                    "        }\n" +
                    "}";


    private static void testCurrentError() {
        MemoryCompiler mc = MemoryCompiler.newInstance();
        Class<?> cmpClass = null;
        try {
            cmpClass = mc.compile("knapsack_bound",teststr);
            Bound test = (Bound) cmpClass.getDeclaredConstructor().newInstance();

            test.Lowerbound(null,null,null);
            test.Upperbound(null,null,null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static JavaFile getFile(String path){
        JavaFile ret = null;
        try {
            BufferedReader stream = new BufferedReader(new FileReader(path));
            File tmp = new File(path);

            StringBuilder st = new StringBuilder();
            String line;
            while ((line = stream.readLine()) != null){
                st.append(line);
            }
            ret = new JavaFile(tmp.getName(),st.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static void testCompile(){
        JavaFile file = getFile("C:\\Users\\loenb\\Desktop\\test files\\knapsack_bounds.java");
        JavaFile file2 = getFile("C:\\Users\\loenb\\Desktop\\test files\\tspbounds.java");
        HashMap<Integer, ProblemInstance> test = new HashMap<>();
        test.put(0, new ProblemInstance("test"));
        test.put(1, new ProblemInstance("test"));
        test.put(2, new ProblemInstance("test"));
        MultiThreadCompiler threadCompiler = new MultiThreadCompiler(test);
        try {
            System.out.println("Waiting 2 sec");
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        /*threadCompiler.compile(0,file);
        threadCompiler.compile(1,file2);
        threadCompiler.compile(2,file);*/
        threadCompiler.addFile(0,file);
        threadCompiler.addFile(1,file2);
        threadCompiler.addFile(2,file);
        threadCompiler.compileAll();
        while(true){
            System.out.format("pi1: %b\n", (test.get(0).getBound()!= null));
            System.out.format("pi2: %b\n", (test.get(1).getBound() != null));
            System.out.format("pi3: %b\n", (test.get(2).getBound() != null));
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void testFramework(){
        Knapsack knapsack_bst  = Knapsack.CreateNew();
        Knapsack knapsack_dpth = Knapsack.CreateNew();
        knapsack_dpth.strategy = NodeStrategy.DEPTH_FIRST;
        Knapsack knapsack_brth = Knapsack.CreateNew();
        knapsack_brth.strategy = NodeStrategy.BREADTH_FIRST;
        try {
            Cplex cplex = new Cplex();
            /*double[] resArr = cplex.ip_solve(knapsack_bst.getDataset(),new HashMap<>(),knapsack_bst.getConstraints(),knapsack_bst.type);
            for(double res:resArr){
                System.out.printf("%d ", (int)res);
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        BranchAndBound bnb = new BranchAndBound(new Problem[] {knapsack_bst},knapsack_bst.ds);
        bnb.Solve(null);
        Result[] res = bnb.getResults();
        for(Result r:res){
            System.out.printf("\n\nStrategy: %s\n",r.getStrategy().toString());
            System.out.printf("\nObjective value: %f\n",r.getObjectiveValue());
            System.out.printf("time: %f seconds\n",r.getRuntime());
            System.out.printf("nodes: %d\n",r.getNr_of_nodes());
            System.out.printf("solution: ");
            for(int s:r.getSolution()){
                System.out.printf("%d ",s);
            }
        }
    }

    private static void testBnbFile() {
        ProblemInstance[] instances = new ProblemInstance[]{
                new ProblemInstance("BestFist",NodeStrategy.BEST_FIRST,true,"C:\\Users\\loenb\\Desktop\\test files\\tspbounds.java"),
                new ProblemInstance("DepthFirst", NodeStrategy.DEPTH_FIRST, true, "C:\\Users\\loenb\\Desktop\\test files\\tspbounds.java"),
                new ProblemInstance("BreadthFirst", NodeStrategy.BREADTH_FIRST, false, "C:\\Users\\loenb\\Desktop\\test files\\knapsack_bounds.java")
        };
        Settings settings = new Settings("5 3 2 5 6 7 8 5 3","3 4 5 6 7 8 2 3 <= 45", ProblemType.MAXIMIZATION,instances);

        BnbFile testFile = new BnbFile("test");
        try {
            testFile.write(settings);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //testCompile();
        //testBnbFile();
        //testFramework();
        testCurrentError();
    }
}
