package bb_framework.utils;

public class DisjointSet {
    int count;

    int[] rank;
    int[] parent;

    public DisjointSet(int c){
        count = c;
        rank = new int[c];
        parent = new int[c];
        for (int i = 0; i < c; i++){
            rank[i] = 0;
            parent[i] = i;
        }
    }

    public void Union(int x, int y){
        int xParent;
        int yParent;

        xParent = Find(x);
        yParent = Find(y);

        if (xParent == yParent) return;

        if (rank[x] < rank[y]) parent[xParent] = yParent;
        else if (rank[y] < rank[x]) parent[yParent] = xParent;
        else
        {
            parent[xParent] = yParent;
            rank[yParent]++;
        }
    }

    public int Find(int x)
    {
        if (parent[x] == x) return x;

        int res = Find(parent[x]);

        parent[x] = res;

        return res;
    }

    public void InitializeWithCurrentSolution(Node current){
        int prev = -1;
        Node curr = current;
        while (curr.depth > -1){
            if(prev != -1){
                this.Union(prev, curr.index);
            }
            prev = curr.index;
            curr = curr.getParent();
        }
    }

}
