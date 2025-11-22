package controller;

import model.GestorArchivo;
import model.Graph;
import java.util.List;

/**
 * Controlador que delega operaciones sobre GestorArchivo.
 */
public class GestorArchivoController {

    public static void guardarCambios() throws Exception {
        GestorArchivo.guardarCambios();
    }

    public static List<Graph> obtenerGrafos() {
        return GestorArchivo.getGrafos();
    }

    public static void insertarGrafo(Graph g) {
        GestorArchivo.insertarGrafo(g);
    }

    public static void eliminarGrafo(Graph g) {
        GestorArchivo.eliminarGrafo(g);
    }
}
