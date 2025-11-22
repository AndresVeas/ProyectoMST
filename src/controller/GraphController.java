package controller;

import model.Graph;

/**
 * Controlador simple que delega operaciones básicas sobre model.Graph.
 */
public class GraphController {

    /**
     * Crea un nuevo Graph, le asigna la lista de vértices y una matriz de adyacencia inicial (ceros).
     */
    public static Graph crearGrafo(String[] vertices) {
        Graph g = new Graph();
        if (vertices != null) {
            g.setListaVertices(vertices);
            int n = vertices.length;
            g.setMatrizAd(new int[n][n]);
        }
        return g;
    }

    /**
     * Inicializa la matriz de adyacencia del grafo.
     */
    public static void inicializarMatriz(Graph g, int[][] matriz) {
        if (g == null) throw new IllegalArgumentException("Graph es null");
        g.setMatrizAd(matriz);
    }

    /**
     * Establece la lista de vértices del grafo.
     */
    public static void setVertices(Graph g, String[] vertices) {
        if (g == null) throw new IllegalArgumentException("Graph es null");
        g.setListaVertices(vertices);
    }

    /**
     * Añade una arista al grafo, delega a Graph.addEdge.
     */
    public static void agregarArista(Graph g, int origen, int destino, int peso) {
        if (g == null) throw new IllegalArgumentException("Graph es null");
        g.addEdge(origen, destino, peso);
    }
}
