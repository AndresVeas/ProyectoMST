package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Lector {
    
    static File file;
    
    public static File getFile() {
        return file;
    }
    
    
    public static void setFile(File file) {
        Lector.file = file;
    }
    
    
    public static ArrayList <Graph> leerArchivo (File ruta){
        file = ruta;
        ArrayList <Graph> grafosGuardados = new ArrayList<>();
        try (Scanner sc = new Scanner(file)) {
            int i = 0;
            while (sc.hasNextLine()){
                String linea = sc.nextLine().trim();
                if (!linea.equalsIgnoreCase("Lista Vertices")) {
                    continue;
                }

                
                String[] vertices = leerVertices(sc);
                if (vertices.length == 0) continue;

                Graph g = new Graph();
                g.setListaVertices(vertices);

                g.setLabel("Grafo " + (++i));
                g.setMatrizAd(new int[vertices.length][vertices.length]);
                leerMatriz(sc, g);

                

                grafosGuardados.add(g);
            }
        } catch (FileNotFoundException e) {
            System.out.println("ARCHIVO NO ENCONTRADO");
        } catch (Exception e) {
            System.out.print("ERROR INESPERADO: " + e.getMessage());
        }
        return grafosGuardados;
    }
    
    private static String [] leerVertices (Scanner sc){
        ArrayList <String> vertice = new ArrayList<>();
        while (sc.hasNextLine()){
            String linea = sc.nextLine();
            if (linea.isEmpty() || linea.contains("Matriz Adyacencia")){
                break;
            } 

            String [] vertices = linea.split(",");
            for (String v : vertices){
                vertice.add(v);
            }
            
        }
        return vertice.toArray(new String [0]);
    }
    
    private static void leerMatriz (Scanner sc, Graph g){
        while (sc.hasNextLine()) {
            String linea = sc.nextLine().trim();
            if (linea.isEmpty()) break;

            if (linea.contains("-") || linea.equalsIgnoreCase("Matriz Adyacencia")) {
                break;
            }

            String[] parts = linea.split(",");
            if (parts.length != 3) {
                System.out.println("Cantidad de parametros incorrectos");
                continue;
            }
       
            g.addEdge(Integer.parseInt(parts[0]),Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
        }      
    }
    
}
