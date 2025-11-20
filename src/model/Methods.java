package model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Methods {
    
    //ALGORITMO DE KRUSKAL
    public List<Edge> kruskal (Graph G){
        List<Edge> edges = G.getEdges();
        edges.sort(Comparator.comparingInt(Edge::getWeight));
        int n = G.getNumNodos();
        UnionFind uf = new UnionFind(n);
        List<Edge> mst = new ArrayList<>();

        for (Edge e : edges) {
            int u = e.getU();
            int v = e.getV();
            if (uf.find(u) != uf.find(v)) {
                uf.union(u, v);
                mst.add(e);
                if (mst.size() == n - 1) break;
            }
        }

        return mst;
    }

    // ALGORITMO DE PRIM (versión O(V^2) para matriz de adyacencia)
    public List<Edge> prim(Graph G) {
        int n = G.getNumNodos();
        List<Edge> mst = new ArrayList<>();
        if (n == 0) return mst;

        // construir matriz de pesos desde G.getEdges()
        int[][] w = new int[n][n];
        for (Edge e : G.getEdges()) {
            w[e.getU()][e.getV()] = e.getWeight();
            w[e.getV()][e.getU()] = e.getWeight();
        }

        boolean[] used = new boolean[n];
        int[] dist = new int[n];
        int[] parent = new int[n];
        for (int i = 0; i < n; i++) {
            dist[i] = Integer.MAX_VALUE;
            parent[i] = -1;
        }

        dist[0] = 0; // arrancar desde el vértice 0

        for (int iter = 0; iter < n; iter++) {
            // seleccionar vértice no usado con distancia mínima
            int u = -1;
            int best = Integer.MAX_VALUE;
            for (int v = 0; v < n; v++) {
                if (!used[v] && dist[v] < best) {
                    best = dist[v];
                    u = v;
                }
            }
            if (u == -1) break; // resto de componentes desconectadas

            used[u] = true;
            if (parent[u] != -1) {
                mst.add(new Edge(parent[u], u, dist[u]));
            }

            // relajar aristas incidentes a u
            for (int v = 0; v < n; v++) {
                if (!used[v] && w[u][v] > 0 && w[u][v] < dist[v]) {
                    dist[v] = w[u][v];
                    parent[v] = u;
                }
            }
        }

        return mst;
    }

    // Unión-find (disjoint set)
    private static class UnionFind {
        private final int[] parent;
        private final int[] rank;

        UnionFind(int n) {
            parent = new int[n];
            rank = new int[n];
            for (int i = 0; i < n; i++) parent[i] = i;
        }

        int find(int x) {
            if (parent[x] != x) parent[x] = find(parent[x]);
            return parent[x];
        }

        void union(int x, int y) {
            int rx = find(x), ry = find(y);
            if (rx == ry) return;
            if (rank[rx] < rank[ry]) parent[rx] = ry;
            else if (rank[ry] < rank[rx]) parent[ry] = rx;
            else {
                parent[ry] = rx;
                rank[rx]++;
            }
        }
    }

}
