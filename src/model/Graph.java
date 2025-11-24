package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Graph {
    private String label;
    private int [] [] matrizAd;
    private String [] listaVertices;
    private int numNodos;
    
    public Graph (){
        
    }
    
    public Graph(String label, int[][] matrizAd, String[] listaVertices) {
        this.label = label;
        this.matrizAd = matrizAd;
        this.listaVertices = listaVertices;
        
        // AGREGA ESTO PARA QUE LOS ALGORITMOS RECONOZCAN EL TAMAÑO AL INSTANTE
        if (listaVertices != null) {
            this.numNodos = listaVertices.length;
        }
    }
    
    public void addEdge(int start, int end, int weight) {
        matrizAd[start-1][end-1] = weight;
        //matrizAd[end-1][start-1] = weight;
    }
    
    
    public String getLabel() {
        return label;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }
    
    public int[][] getMatrizAd() {
        return matrizAd;
    }
    
    public void setMatrizAd(int[][] matrizAd) {
        this.matrizAd = matrizAd;
    }
    
    public String[] getListaVertices() {
        return listaVertices;
    }
    
    public void setListaVertices(String[] listaVertices) {
        this.listaVertices = listaVertices;
        this.numNodos = listaVertices.length;
    }
    
    public int getNumNodos() {
        return numNodos;
    }
    
    public void setNumNodos(int numNodos) {
        this.numNodos = numNodos;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Label: ").append(label == null ? "null" : label).append(System.lineSeparator());
        sb.append("Num Nodos: ").append(numNodos).append(System.lineSeparator());
        sb.append("Lista Vertices: ");
        if (listaVertices != null) {
            sb.append(Arrays.toString(listaVertices));
        } else {
            sb.append("[]");
        }
        sb.append(System.lineSeparator());
        sb.append("Matriz Adyacencia:").append(System.lineSeparator());
        if (matrizAd != null) {
            for (int i = 0; i < matrizAd.length; i++) {
                sb.append("[");
                for (int j = 0; j < matrizAd[i].length; j++) {
                    sb.append(matrizAd[i][j]);
                    if (j < matrizAd[i].length - 1) sb.append(", ");
                }
                sb.append("]").append(System.lineSeparator());
            }
        } else {
            sb.append("null").append(System.lineSeparator());
        }
        return sb.toString();
    }
    
    public List<Edge> getEdges() {
        List<Edge> list = new ArrayList<>();
        for (int i = 0; i < numNodos; i++) {
            for (int j = i+1; j < numNodos; j++) {
                if (matrizAd[i][j] > 0) list.add(new Edge(i, j, matrizAd[i][j]));
            }
        }
        return list;
    }
    
    public void printGraph() {
        System.out.print(this.toString());
    }
}
