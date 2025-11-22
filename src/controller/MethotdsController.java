package controller;

import model.Graph;
import model.Methods;
import model.Edge;
import java.util.List;

/**
 * Controlador que expone los algoritmos (Methods) para la UI.
 */
public class MethotdsController {

    public static List<Edge> kruskal(Graph g) {
        return Methods.kruskal(g);
    }

    public static List<Edge> prim(Graph g) {
        return Methods.prim(g);
    }

    public static List<Edge> dfs(Graph g, int startVertex) {
        return Methods.dfs(g, startVertex);
    }
}
