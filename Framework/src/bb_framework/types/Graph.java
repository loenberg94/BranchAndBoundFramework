package bb_framework.types;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Graph {

    private final double[][] edgeWeights;
    public final List<Edge> edges;
    public final List<Edge>[] adjecentEdges;

    Graph(int n){
        this.edgeWeights = new double[n][n];
        this.edges = new ArrayList<>();
        this.adjecentEdges = new List[n];
        for(int i = 0; i < n; i++) adjecentEdges[i] = new LinkedList<>();
        for(int i = 0; i < n; i++) for(int j = 0; j < n; j++) edgeWeights[i][j] = Double.POSITIVE_INFINITY;
    }

    public void addEdge(int u, int v, double weight, boolean undirected){
        if (edgeWeights[u][v] < Double.POSITIVE_INFINITY) return;
        edgeWeights[u][v] = weight;
        Edge e = new Edge(u,v);
        if(undirected) {
            adjecentEdges[v].add(e);
            edgeWeights[v][u] = weight;
        }
        adjecentEdges[u].add(e);
        edges.add(e);
    }

    public double getEdgeWeight(Edge e) {
        return edgeWeights[e.u][e.v];
    }
}
