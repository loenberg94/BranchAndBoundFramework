package com.mal;

import com.mal.framework.BranchAndBound;
import com.mal.framework.enums.NodeStrategy;
import com.mal.framework.utils.Problem;
import com.mal.framework.utils.Result;
import com.mal.tests.Knapsack;

public class Main {

    public static void main(String[] args) {
        /*JFrame frame = new JFrame("mainform");
        frame.setSize(new Dimension(600,400));
        frame.setContentPane(new mainform().mainpanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);*/

        /*Cplex cplex = new Cplex();

        Knapsack knapsack = Knapsack.CreateNew();
        HashMap<Integer,Double> cs = new HashMap<>();
        cs.put(0,1.0);
        cs.put(1,1.0);
        cs.put(2,1.0);
        cs.put(3,1.0);

        try {
            double[] result = cplex.lp_relaxation(knapsack.getDataset(),cs, knapsack.getConstraints(), knapsack.type);
            for(double val:result){
                System.out.printf("%f\n",val);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        Knapsack knapsack_bst  = Knapsack.CreateNew();
        Knapsack knapsack_dpth = Knapsack.CreateNew();
        knapsack_dpth.strategy = NodeStrategy.DEPTH_FIRST;
        Knapsack knapsack_brth = Knapsack.CreateNew();
        knapsack_brth.strategy = NodeStrategy.BREADTH_FIRST;
        /*try {
            double[] resArr = Cplex.ip_solve(knapsack.getDataset(),knapsack.getConstraints(),knapsack.type);
            for(double res:resArr){
                System.out.printf("%f\n",res);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        BranchAndBound bnb = new BranchAndBound(new Problem[] {knapsack_bst, knapsack_dpth, knapsack_brth},knapsack_bst.getDataset());
        bnb.Solve();
        Result[] res = bnb.getResults();
        for(Result r:res){
            System.out.printf("\n\nStrategy: %s\n",r.getStrategy().toString());
            System.out.printf("\nObjective value: %f\n",r.getObjectiveValue());
            System.out.printf("time: %f seconds\n",r.getRuntime());
            System.out.printf("nodes: %d\n",r.getNr_of_nodes());
        }
    }



    /*
    * MemoryCompiler compiler = new MemoryCompiler();

        StringWriter writer = new StringWriter();
        PrintWriter out = new PrintWriter(writer);
        out.println("public class HelloWorld {");
        out.println("  public static void main(String test) {");
        out.println("    System.out.println(\"This is in another java file\");");
        out.println("  }");
        out.println("}");
        out.close();

        try {
            Method method = compiler.compileStatic("main", "HelloWorld", writer.toString());
            method.invoke(null, "");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    * */
}
