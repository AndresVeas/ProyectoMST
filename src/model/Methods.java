package model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Methods {
    
    //ALGORITMO DE KRUSKAL
    public static List<Edge> kruskal (Graph G){
        // Asegurar que consideramos el grafo como no dirigido:
        // para cada par {u,v} tomar la arista de menor peso (si existen u->v y v->u)
        java.util.Map<Long, Edge> best = new java.util.HashMap<>();
        for (Edge e : G.getEdges()) {
            int u = Math.min(e.getU(), e.getV());
            int v = Math.max(e.getU(), e.getV());
            long key = (((long) u) << 32) | (v & 0xffffffffL);
            Edge prev = best.get(key);
            if (prev == null || e.getWeight() < prev.getWeight()) {
                best.put(key, new Edge(u, v, e.getWeight()));
            }
        }
        List<Edge> edges = new ArrayList<>(best.values());
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
    public static List<Edge> prim(Graph G) {
        int n = G.getNumNodos();
        List<Edge> mst = new ArrayList<>();
        if (n == 0) return mst;

        // construir matriz de pesos desde G.getEdges()
        int[][] w = new int[n][n];
        for (Edge e : G.getEdges()) {
            int u = e.getU();
            int v = e.getV();
            int wt = e.getWeight();
            // si ya hay una arista entre u-v, conservar el mínimo
            if (w[u][v] == 0) {
                w[u][v] = wt;
                w[v][u] = wt;
            } else {
                int m = Math.min(w[u][v], wt);
                w[u][v] = m;
                w[v][u] = m;
            }
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

    // ALGORITMO DE DFS (usa matriz de adyacencia) - devuelve aristas del árbol/selva DFS
    public  static List<Edge> dfs(Graph G, int start) {
        int n = G.getNumNodos();
        List<Edge> tree = new ArrayList<>();
        if (n == 0) return tree;

        // construir matriz de adyacencia (peso > 0 indica arista)
        int[][] w = new int[n][n];
        for (Edge e : G.getEdges()) {
            int u = e.getU();
            int v = e.getV();
            int wt = e.getWeight();
            if (w[u][v] == 0) {
                w[u][v] = wt;
                w[v][u] = wt;
            } else {
                int m = Math.min(w[u][v], wt);
                w[u][v] = m;
                w[v][u] = m;
            }
        }

        boolean[] visited = new boolean[n];

        // recorrer desde el vértice inicial
        if (start < 0 || start >= n) start = 0;
        dfsVisit(start, -1, visited, w, tree);

        // si el grafo es desconectado, completar para los demás componentes
        for (int v = 0; v < n; v++) {
            if (!visited[v]) dfsVisit(v, -1, visited, w, tree);
        }

        return tree;
    }

    private static void dfsVisit(int u, int parent, boolean[] visited, int[][] w, List<Edge> tree) {
        if (visited[u]) return;
        visited[u] = true;
        if (parent != -1) {
            tree.add(new Edge(parent, u, w[parent][u]));
        }
        for (int v = 0; v < w.length; v++) {
            if (!visited[v] && w[u][v] > 0) {
                dfsVisit(v, u, visited, w, tree);
            }
        }
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
