package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;

public class GestorArchivo {
    // archivo puede ser externo (al lado del jar) o copiado desde el recurso embebido
    private static File archivo;
    private static ArrayList<Graph> grafos;

    static {
        initArchivo();
        cargarGrafos();
    }

    public static ArrayList<Graph> getGrafos() {
        return grafos;
    }

    public static void setGrafos(ArrayList<Graph> grafos) {
        GestorArchivo.grafos = grafos;
    }

    /**
     * Inicializa 'archivo' buscando en varias ubicaciones:
     * 1) misma carpeta del jar (si se detecta)
     * 2) directorio de trabajo (user.dir)
     * 3) ./resources/grafos.csv
     *
     * Si no se encuentra ninguno, intenta copiar el recurso embebido "/resources/grafos.csv"
     * desde el classpath al primer destino disponible. Si no existe el recurso, crea un
     * archivo vacío en el directorio de trabajo.
     */
    private static void initArchivo() {
        // posibles rutas externas
        String userDir = System.getProperty("user.dir");
        File fUser = new File(userDir, "grafos.csv");
        File fResourcesRel = new File("resources/grafos.csv");
        File fResourcesSrc = new File("src/resources/grafos.csv");

        // intentar obtener carpeta del jar (si aplica)
        File fJarDir = null;
        try {
            var codeSource = GestorArchivo.class.getProtectionDomain().getCodeSource();
            if (codeSource != null && codeSource.getLocation() != null) {
                File code = new File(codeSource.getLocation().toURI());
                if (code.isFile()) { // es un jar
                    fJarDir = code.getParentFile();
                } else if (code.isDirectory()) { // ejecutando desde IDE
                    fJarDir = code;
                }
            }
        } catch (Exception ignored) {}

        File fJarCandidate = fJarDir != null ? new File(fJarDir, "grafos.csv") : null;

        // prioridad: jar dir -> user.dir -> resources/ -> src/resources/
        if (fJarCandidate != null && fJarCandidate.exists()) {
            archivo = fJarCandidate;
            return;
        }
        if (fUser.exists()) {
            archivo = fUser;
            return;
        }
        if (fResourcesRel.exists()) {
            archivo = fResourcesRel;
            return;
        }
        if (fResourcesSrc.exists()) {
            archivo = fResourcesSrc;
            return;
        }

        // Si no existe externamente, intentar extraer recurso embebido desde el classpath
        InputStream in = GestorArchivo.class.getResourceAsStream("/resources/grafos.csv");
        if (in == null) {
            // intentar sin carpeta
            in = GestorArchivo.class.getResourceAsStream("/grafos.csv");
        }

        try {
            File target = (fJarDir != null) ? new File(fJarDir, "grafos.csv") : fUser;
            if (in != null) {
                // copiar recurso embebido al filesystem
                Files.createDirectories(target.getParentFile().toPath());
                try (FileOutputStream out = new FileOutputStream(target)) {
                    byte[] buffer = new byte[8192];
                    int read;
                    while ((read = in.read(buffer)) != -1) {
                        out.write(buffer, 0, read);
                    }
                }
                archivo = target;
                return;
            } else {
                // recurso embebido no existe: crear archivo vacío en user dir
                Files.createDirectories(fUser.getParentFile() == null ? new File(".").toPath() : fUser.getParentFile().toPath());
                if (!fUser.exists()) fUser.createNewFile();
                archivo = fUser;
            }
        } catch (Exception e) {
            // en caso de fallo, fallback a un archivo en memoria (temporal)
            archivo = new File("grafos.csv");
            try { if (!archivo.exists()) archivo.createNewFile(); } catch (Exception ignored) {}
        } finally {
            try { if (in != null) in.close(); } catch (Exception ignored) {}
        }
    }

    public static void cargarGrafos (){
        if (archivo == null) {
            grafos = new ArrayList<>();
            return;
        }
        try {
            GestorArchivo.grafos = Lector.leerArchivo(archivo);
            if (GestorArchivo.grafos == null) GestorArchivo.grafos = new ArrayList<>();
        } catch (Exception e) {
            // en error, inicializar lista vacía
            GestorArchivo.grafos = new ArrayList<>();
        }
    }

    public static void eliminarGrafo (Graph g){
        if (grafos != null) grafos.remove(g);
    }

    public static void actualizarGrafo (Graph g,Graph g2){
        int indice =  grafos.indexOf(g);
        grafos.add(new Graph(g.getLabel(),g2.getMatrizAd(),grafos.get(0).getListaVertices()));
        grafos.remove(indice);
    }

    public static void insertarGrafo (Graph g){
        grafos.add(new Graph("Grafo " + (grafos.size()+1),g.getMatrizAd(),g.getListaVertices()));
        guardarCambios();
    }

    public static void guardarCambios (){
        if (archivo == null) return;
        try {
            File parent = archivo.getParentFile();
            if (parent != null && !parent.exists()) parent.mkdirs();
            try (FileWriter fw = new FileWriter(archivo)) {
                int contador = 1;
                for (Graph g : grafos){
                    fw.append("Lista Vertices").append(System.lineSeparator());
                    fw.append(listaVertices(g));
                    fw.append("Matriz Adyacencia - ").append(String.valueOf(contador++)).append(System.lineSeparator());
                    fw.append(matrizAdyacenciaString(g));
                    fw.append("---------------").append(System.lineSeparator());
                }
            }
        }  catch (FileNotFoundException e) {
            System.out.println("ARCHIVO NO ENCONTRADO");
        } catch (Exception e) {
            System.out.print("ERROR INESPERADO: " + e.getMessage());
        }
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
