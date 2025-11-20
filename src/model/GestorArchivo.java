package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;

public class GestorArchivo {
    private static final File archivo = new File ("src/resources/grafos.csv");
    private static ArrayList <Graph> grafos;

    static {
        cargarGrafos();
    }
    
    public static ArrayList<Graph> getGrafos() {
        return grafos;
    }

    public static void setGrafos(ArrayList<Graph> grafos) {
        GestorArchivo.grafos = grafos;
    }

    public static void cargarGrafos (){
        GestorArchivo.grafos = Lector.leerArchivo(archivo);
    }
    
    public static void eliminarGrafo (Graph g){
        grafos.remove(g);
    }
    
    public static void actualizarGrafo (Graph g,Graph g2){
        int indice =  grafos.indexOf(g);
        grafos.add(new Graph(g.getLabel(),g2.getMatrizAd(),grafos.get(0).getListaVertices()));
        grafos.remove(indice);
    }
    
    public static void insertarGrafo (String[] listaVertices, int [] [] matrizAd){
        grafos.add(new Graph("Grafo " + (grafos.size()+1),matrizAd,listaVertices));
    } 

    public static void guardarCambios (){
        try (FileWriter fw = new FileWriter(archivo)) {
            int contador = 1;
            for (Graph g : grafos){
                fw.append("Lista Vertices").append(System.lineSeparator());
                fw.append(listaVertices(g));
                fw.append("Matriz Adyacencia - ").append(String.valueOf(contador++)).append(System.lineSeparator());
                fw.append(matrizAdyacenciaString(g));
                fw.append("---------------").append(System.lineSeparator());
            }
        }  catch (FileNotFoundException e) {
            System.out.println("ARCHIVO NO ENCONTRADO");
        } catch (Exception e) {
            System.out.print("ERROR INESPERADO: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
 
    }

    private static String listaVertices (Graph g){
        if (g == null || g.getListaVertices() == null) return System.lineSeparator();
        String[] v = g.getListaVertices();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < v.length; i++) {
            if (i > 0) {
                if (i % 5 == 0) {
                    sb.append(System.lineSeparator());
                } else {
                    sb.append(",");
                }
            }
            sb.append(v[i]);
        }
        sb.append(System.lineSeparator());
        return sb.toString();
    }

    private static String matrizAdyacenciaString(Graph g){
        if (g == null || g.getMatrizAd() == null) return System.lineSeparator();
        int[][] m = g.getMatrizAd();
        int n = m.length;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) { // recorrer todas las columnas para grafo dirigido
                int peso = m[i][j];
                if (peso != 0) {
                    sb.append(i + 1).append(",").append(j + 1).append(",").append(peso).append(System.lineSeparator());
                }
            }
        }
        return sb.toString();
    }
}
