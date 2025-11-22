package view;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.GestorArchivo;
import model.Methods;

public class GraphInterface extends JFrame {

    // Paleta de colores estilo "Tailwind" basada en tu imagen
    private static final Color BG_COLOR = new Color(243, 244, 246); // Slate-100
    private static final Color PANEL_BG = Color.WHITE;
    private static final Color PRIMARY_BLUE = new Color(59, 130, 246); // Blue-500
    private static final Color TEXT_DARK = new Color(30, 41, 59); // Slate-800
    private static final Color TEXT_GRAY = new Color(100, 116, 139); // Slate-500
    private static final Color GRAPH_BG = new Color(51, 65, 85); // Slate-700 (Fondo oscuro para el grafo)
    private static final Color NODE_COLOR = new Color(148, 163, 184); // Slate-400

    // Estado seleccionable
    private int selectedGraphIndex = -1;
    private String selectedAlgorithm = null;

    // Componentes a los que hay que acceder desde varios métodos
    private GraphCanvas graphCanvas;
    private final List<JButton> graphButtons = new ArrayList<>();
    private final List<JButton> navButtons = new ArrayList<>();

    // panel grid para botones de grafos (se usa para refrescar al eliminar)
    private JPanel graphGridPanel;

    // Panel central con CardLayout para alternar entre vista normal y comparación
    private JPanel centerCardPanel;
    private CardLayout centerCardLayout;
    private JPanel defaultCenterContent;

    // Labels para información dinámica
    private JTextArea pathTextArea;
    private JLabel timeValueLabel;
    private JLabel weightValueLabel; // nuevo: peso total

    public GraphInterface() {
        setTitle("ÁRBOL DE EXPANSIÓN MINIMA (MST)");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1280, 700);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // preferentemente abrir en pantalla completa
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_COLOR);

        // Al cerrar ventana, guardar cambios antes de salir
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    GestorArchivo.guardarCambios();
                } catch (Exception ex) {
                    // no detener cierre por error de guardado
                }
                dispose();
                System.exit(0);
            }
        });

        // --- Título Principal ---
        var titleLabel = new JLabel("ÁRBOL DE EXPANSIÓN MINIMA (MST)", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        titleLabel.setForeground(TEXT_DARK);
        titleLabel.setBorder(new EmptyBorder(20, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        // --- Contenedor Principal (con margen) ---
        var mainContainer = new JPanel(new BorderLayout(20, 0));
        mainContainer.setBackground(BG_COLOR);
        mainContainer.setBorder(new EmptyBorder(0, 30, 30, 30));
        add(mainContainer, BorderLayout.CENTER);

        // 1. Panel Izquierdo (Sidebar)
        mainContainer.add(createSidebar(), BorderLayout.WEST);

        // 2. Panel Central (usamos CardLayout para poder mostrar comparación en lugar de diálogo)
        defaultCenterContent = createCentralPanel();
        centerCardPanel = new JPanel(new CardLayout());
        centerCardLayout = (CardLayout) centerCardPanel.getLayout();
        centerCardPanel.add(defaultCenterContent, "default");
        mainContainer.add(centerCardPanel, BorderLayout.CENTER);
    }

    private JPanel createSidebar() {
        var sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(PANEL_BG);
        sidebar.setPreferredSize(new Dimension(180, 0));
        sidebar.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        var label = new JLabel("Grafos:");
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        label.setForeground(TEXT_DARK);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(label);
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));

        // Grid de botones G1, G2...
        // aumentar espacio vertical para la lista de grafos
        graphGridPanel = new JPanel(new GridLayout(6, 2, 8, 8));
        graphGridPanel.setBackground(PANEL_BG);
        graphGridPanel.setPreferredSize(new Dimension(140, 360));
        graphGridPanel.setMaximumSize(new Dimension(140, 360));
        
        refreshGraphButtons();

        sidebar.add(graphGridPanel);
        // empuja los botones hacia abajo
        sidebar.add(Box.createVerticalGlue());

        // Botón Agregar (+)
        var addButton = new CircleButton("+");
        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(addButton);
        sidebar.add(Box.createRigidArea(new Dimension(0, 6)));

        addButton.addActionListener(e -> {
                    AgregarVerticesApp a = new AgregarVerticesApp();
                    a.setLocationRelativeTo(null);
                    a.setVisible(true);
                    this.dispose();
        });

        // etiqueta del botón agregar
        var addLabel = new JLabel("Agregar grafo");
        addLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        addLabel.setForeground(TEXT_GRAY);
        addLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(addLabel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 12)));

        // Botón Eliminar (-) debajo del +
        var removeButton = new CircleMinusButton("-");
        removeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(removeButton);
        sidebar.add(Box.createRigidArea(new Dimension(0, 6)));

        removeButton.addActionListener(e -> {
            if (selectedGraphIndex < 0) {
                JOptionPane.showMessageDialog(this, "primero seleccione un grafo", "Atención", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int opt = JOptionPane.showConfirmDialog(this, "Seguro que desea eliminar?", "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (opt != JOptionPane.YES_OPTION) return;

            var grafos = GestorArchivo.getGrafos();
            if (grafos == null || selectedGraphIndex < 0 || selectedGraphIndex >= grafos.size()) {
                JOptionPane.showMessageDialog(this, "Grafo inválido", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            model.Graph toRemove = grafos.get(selectedGraphIndex);
            GestorArchivo.eliminarGrafo(toRemove);
            // limpiar selección y actualizar UI
            selectedGraphIndex = -1;
            graphCanvas.setGraphIndex(-1);
            graphCanvas.clearHighlights();
            pathTextArea.setText("N/A");
            timeValueLabel.setText("0 ns");
            weightValueLabel.setText("0");
            refreshGraphButtons();
            graphCanvas.repaint();
        });

        // etiqueta del botón eliminar
        var removeLabel = new JLabel("Eliminar grafo");
        removeLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        removeLabel.setForeground(TEXT_GRAY);
        removeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(removeLabel);

        sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
 
         return sidebar;
     }

    // Reconstruye los botones de grafos en la barra lateral
    private void refreshGraphButtons() {
        graphGridPanel.removeAll();
        graphButtons.clear();
        var grafos = GestorArchivo.getGrafos();
        int cantidadGrafos = grafos == null ? 0 : grafos.size();
        cantidadGrafos = Math.max(1, cantidadGrafos);
        for (int i = 1; i <= cantidadGrafos; i++) {
            var btn = createSquareButton("G" + i, i - 1);
            graphGridPanel.add(btn);
            graphButtons.add(btn);
            // si el grafo fue eliminado y la lista ahora es menor, los índices se ajustan al crearse botones nuevos
        }
        graphGridPanel.revalidate();
        graphGridPanel.repaint();
    }
    
    private JPanel createCentralPanel() {
        var centerPanel = new JPanel(new BorderLayout(0, 20));
        centerPanel.setOpaque(false);

        // --- Top: Algoritmos ---
        var navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        navPanel.setOpaque(false);
        
        // Crear botones de la barra de navegación y guardarlos para marcar/desmarcar
        var bKruskal = createNavButton("Kruskal");
        var bPrim = createNavButton("Prim");
        var bDFS = createNavButton("DFS");
        var bComparar = createNavButton("Comparar");

        navButtons.add(bKruskal);
        navButtons.add(bPrim);
        navButtons.add(bDFS);
        navButtons.add(bComparar);

        navPanel.add(bKruskal);
        navPanel.add(bPrim);
        navPanel.add(bDFS);
        navPanel.add(bComparar);
        
        // Agregar menú desplegable para "Comparar"
        bComparar.addActionListener(e -> {
            JPopupMenu cmpMenu = new JPopupMenu();
            JMenuItem kp = new JMenuItem("Kruskal vs Prim");
            JMenuItem kd = new JMenuItem("Kruskal vs DFS");
            JMenuItem pd = new JMenuItem("Prim vs DFS");
            kp.addActionListener(ae -> compareMethods("Kruskal", "Prim"));
            kd.addActionListener(ae -> compareMethods("Kruskal", "DFS"));
            pd.addActionListener(ae -> compareMethods("Prim", "DFS"));
            cmpMenu.add(kp); cmpMenu.add(kd); cmpMenu.add(pd);
            cmpMenu.show(bComparar, 0, bComparar.getHeight());
        });

        centerPanel.add(navPanel, BorderLayout.NORTH);

        // --- Center: Visualización del Grafo ---
        graphCanvas = new GraphCanvas();
        centerPanel.add(graphCanvas, BorderLayout.CENTER);

        // --- Bottom: Información ---
        var infoPanel = new JPanel(new GridLayout(1, 3, 20, 0)); // ahora 3 columnas: Camino | Peso total | Tiempo
        infoPanel.setOpaque(false);
        infoPanel.setPreferredSize(new Dimension(0, 80)); // tamaño reducido

        infoPanel.add(createPathInfoBox("Camino mínimo", "N/A"));
        infoPanel.add(createWeightInfoBox("Peso total del recorrido", "0"));
        infoPanel.add(createTimeInfoBox("Tiempo de ejecución", "0 ns"));

        centerPanel.add(infoPanel, BorderLayout.SOUTH);

        return centerPanel;
    }

    // --- Componentes Personalizados y Helpers ---

    private JButton createSquareButton(String text, int index) {
        var btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btn.setForeground(TEXT_GRAY);
        btn.setBackground(new Color(241, 245, 249)); // Slate-100
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Listener cambiado para permitir toggle (deselección)
        btn.addActionListener(e -> {
            if (selectedGraphIndex == index) {
                selectGraph(-1); // deseleccionar si ya estaba seleccionado
            } else {
                selectGraph(index);
            }
        });
        return btn;
    }

    private JButton createNavButton(String text) {
        var btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean isActive = text.equals(selectedAlgorithm);
                if (getModel().isRollover()) {
                    g2.setColor(isActive ? PRIMARY_BLUE.darker() : Color.WHITE);
                } else {
                    g2.setColor(isActive ? PRIMARY_BLUE : BG_COLOR);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setForeground(TEXT_DARK);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(100, 35));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> selectAlgorithm(text));
        return btn;
    }

    private JPanel createPathInfoBox(String title, String value) {
        var panel = new JPanel(new GridLayout(2, 1));
        panel.setBackground(PANEL_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1), // Borde sutil
            new EmptyBorder(15, 15, 15, 15)
        ));

        var titleLbl = new JLabel(title, SwingConstants.CENTER);
        titleLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        titleLbl.setForeground(TEXT_GRAY);

        // JTextArea dentro de JScrollPane para permitir múltiples líneas y scroll
        pathTextArea = new JTextArea(value);
        pathTextArea.setEditable(false);
        pathTextArea.setLineWrap(true);
        pathTextArea.setWrapStyleWord(true);
        pathTextArea.setFont(new Font("SansSerif", Font.BOLD, 14));
        pathTextArea.setForeground(TEXT_DARK);
        pathTextArea.setBackground(PANEL_BG);
        pathTextArea.setBorder(null);
        JScrollPane sp = new JScrollPane(pathTextArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setBorder(null);
        sp.getViewport().setBackground(PANEL_BG);

        panel.add(titleLbl);
        panel.add(sp);
        return panel;
    }

    private JPanel createWeightInfoBox(String title, String value) {
        var panel = new JPanel(new GridLayout(2, 1));
        panel.setBackground(PANEL_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            new EmptyBorder(12, 12, 12, 12)
        ));

        var titleLbl = new JLabel(title, SwingConstants.CENTER);
        titleLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        titleLbl.setForeground(TEXT_GRAY);

        weightValueLabel = new JLabel(value, SwingConstants.CENTER);
        weightValueLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        weightValueLabel.setForeground(TEXT_DARK);

        panel.add(titleLbl);
        panel.add(weightValueLabel);
        return panel;
    }

    private JPanel createTimeInfoBox(String title, String value) {
        var panel = new JPanel(new GridLayout(2, 1));
        panel.setBackground(PANEL_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1), // Borde sutil
            new EmptyBorder(10, 10, 10, 10)
        ));

        var titleLbl = new JLabel(title, SwingConstants.CENTER);
        titleLbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        titleLbl.setForeground(TEXT_GRAY);

        timeValueLabel = new JLabel(value, SwingConstants.CENTER);
        timeValueLabel.setFont(new Font("SansSerif", Font.BOLD, 13)); // tamaño reducido
        timeValueLabel.setForeground(TEXT_DARK);

        panel.add(titleLbl);
        panel.add(timeValueLabel);
        return panel;
    }

    // Clase interna para el botón circular (+)
    class CircleButton extends JButton {
        public CircleButton(String label) {
            super(label);
            setPreferredSize(new Dimension(50, 50));
            setMaximumSize(new Dimension(50, 50));
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setForeground(Color.WHITE);
            setFont(new Font("SansSerif", Font.BOLD, 24));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getModel().isPressed() ? PRIMARY_BLUE.darker() : PRIMARY_BLUE);
            g2.fillOval(0, 0, getWidth(), getHeight());
            super.paintComponent(g);
            g2.dispose();
        }
    }

    // Botón circular rojo con signo menos
    class CircleMinusButton extends JButton {
        public CircleMinusButton(String label) {
            super(label);
            setPreferredSize(new Dimension(50, 50));
            setMaximumSize(new Dimension(50, 50));
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setForeground(Color.WHITE);
            setFont(new Font("SansSerif", Font.BOLD, 28));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getModel().isPressed() ? Color.RED.darker() : Color.RED);
            g2.fillOval(0, 0, getWidth(), getHeight());
            super.paintComponent(g);
            g2.dispose();
        }
    }

    // Selecciona un grafo de la barra lateral (ahora toggle)
    private void selectGraph(int index) {
        // index == -1 significa deseleccionar
        if (index == selectedGraphIndex) {
            index = -1;
        }
        selectedGraphIndex = index;

        // Actualizar estilo de botones de grafos
        for (int i = 0; i < graphButtons.size(); i++) {
            JButton b = graphButtons.get(i);
            if (i == index) {
                b.setBackground(PRIMARY_BLUE);
                b.setForeground(Color.WHITE);
            } else {
                b.setBackground(new Color(241, 245, 249));
                b.setForeground(TEXT_GRAY);
            }
        }

        // Decidir qué grafo mostrar en el canvas
        graphCanvas.setGraphIndex(index);
        graphCanvas.repaint();

        // Si hay algoritmo seleccionado, re-ejecutarlo sobre el nuevo grafo
        if (selectedAlgorithm != null) {
            runSelectedAlgorithm();
        } else {
            // limpiar info si deseleccionan grafo
            pathTextArea.setText(index >= 0 ? "N/A" : "Seleccione un grafo");
            timeValueLabel.setText("0 ns");
            weightValueLabel.setText("0");
            graphCanvas.clearHighlights();
        }
    }

    // Selecciona algoritmo en la barra superior (permanece marcado; ahora toggle)
    private void selectAlgorithm(String name) {
        if (name != null && name.equals(selectedAlgorithm)) {
            // si se pulsa el mismo, deseleccionar
            selectedAlgorithm = null;
        } else {
            selectedAlgorithm = name;
        }

        for (JButton b : navButtons) {
            if (b.getText().equals(selectedAlgorithm)) {
                b.setForeground(Color.WHITE);
            } else {
                b.setForeground(TEXT_DARK);
            }
        }
        // repintar para que los botones nav actualicen su apariencia
        repaint();

        if (selectedAlgorithm != null) {
            runSelectedAlgorithm();
        } else {
            // limpiar resaltados e info al deseleccionar algoritmo
            pathTextArea.setText("N/A");
            timeValueLabel.setText("0 ns");
            weightValueLabel.setText("0");
            graphCanvas.clearHighlights();
            graphCanvas.repaint();
        }
    }

    // Ejecuta el algoritmo actualmente seleccionado sobre el grafo seleccionado
    private void runSelectedAlgorithm() {
        if (selectedGraphIndex < 0) {
            pathTextArea.setText("Seleccione un grafo");
            timeValueLabel.setText("0 ns");
            weightValueLabel.setText("0");
            graphCanvas.clearHighlights();
            graphCanvas.repaint();
            return;
        }

        var grafos = GestorArchivo.getGrafos();
        if (grafos == null || selectedGraphIndex < 0 || selectedGraphIndex >= grafos.size()) {
            pathTextArea.setText("Grafo inválido");
            timeValueLabel.setText("0 ns");
            weightValueLabel.setText("0");
            graphCanvas.clearHighlights();
            graphCanvas.repaint();
            return;
        }

        model.Graph g = grafos.get(selectedGraphIndex);
        if (g == null) {
            pathTextArea.setText("Grafo vacío");
            timeValueLabel.setText("0 ns");
            weightValueLabel.setText("0");
            graphCanvas.clearHighlights();
            graphCanvas.repaint();
            return;
        }

        Methods methods = new Methods();
        List<model.Edge> result = new ArrayList<>();
        long start = System.nanoTime();
        if ("Kruskal".equals(selectedAlgorithm)) {
            result = methods.kruskal(g);
        } else if ("Prim".equals(selectedAlgorithm)) {
            result = methods.prim(g);
        } else if ("DFS".equals(selectedAlgorithm)) {
            // usar vértice 0 como inicio por defecto (puede cambiarse posteriormente si desea pedir al usuario)
            result = methods.dfs(g, 0);
        } else {
            // otros métodos no esperados
            pathTextArea.setText("Método no implementado");
            timeValueLabel.setText("0 ns");
            weightValueLabel.setText("0");
            graphCanvas.clearHighlights();
            graphCanvas.repaint();
            return;
        }
        long elapsedNs = System.nanoTime() - start;

        // Actualizar info textual: formar representación del "camino" (lista de aristas) usando labels
        String[] labels = g.getListaVertices();
        if (result == null || result.isEmpty()) {
            pathTextArea.setText("Sin aristas en resultado");
            weightValueLabel.setText("0");
        } else {
            // construir partes (por arista) usando labels
            List<String> parts = new ArrayList<>();
            int totalWeight = 0;
            for (model.Edge e : result) {
                int u = e.getU();
                int v = e.getV();
                totalWeight += e.getWeight();
                String lu = (labels != null && u >= 0 && u < labels.length && labels[u] != null) ? labels[u] : String.valueOf(u);
                String lv = (labels != null && v >= 0 && v < labels.length && labels[v] != null) ? labels[v] : String.valueOf(v);
                parts.add("(" + lu + " - " + lv + ": " + e.getWeight() + ")");
            }

            // envolver en varias líneas (límite por caracteres) y usar saltos de línea para JTextArea
            int maxCharsPerLine = 60; // ajustar si se desea
            StringBuilder text = new StringBuilder();
            StringBuilder line = new StringBuilder();
            for (int i = 0; i < parts.size(); i++) {
                String p = parts.get(i);
                if (line.length() == 0) {
                    line.append(p);
                } else {
                    if (line.length() + 2 + p.length() > maxCharsPerLine) {
                        text.append(line.toString()).append("\n");
                        line.setLength(0);
                        line.append(p);
                    } else {
                        line.append(", ").append(p);
                    }
                }
            }
            if (line.length() > 0) text.append(line.toString());

            pathTextArea.setText(text.toString());
            pathTextArea.setCaretPosition(0);
            weightValueLabel.setText(String.valueOf(totalWeight));
        }
        timeValueLabel.setText(elapsedNs + " ns");

        // Indicar al canvas qué aristas/nodos resaltar
        graphCanvas.setHighlightedEdges(result);
        graphCanvas.repaint();
    }

    // Comparar dos métodos en el panel central (reemplaza la tarjeta "default" por "compare")
    private void compareMethods(String m1, String m2) {
        if (selectedGraphIndex < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un grafo para eliminar", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        var grafos = GestorArchivo.getGrafos();
        if (grafos == null || selectedGraphIndex < 0 || selectedGraphIndex >= grafos.size()) {
            JOptionPane.showMessageDialog(this, "Grafo inválido", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        model.Graph g = grafos.get(selectedGraphIndex);
        Methods methods = new Methods();

        List<model.Edge> res1 = null;
        List<model.Edge> res2 = null;
        long t1 = -1, t2 = -1;
        boolean impl1 = true, impl2 = true;

        // ejecutar m1
        if ("Kruskal".equals(m1)) {
            long s = System.nanoTime();
            res1 = methods.kruskal(g);
            t1 = System.nanoTime() - s;
        } else if ("Prim".equals(m1)) {
            long s = System.nanoTime();
            res1 = methods.prim(g);
            t1 = System.nanoTime() - s;
        } else if ("DFS".equals(m1)) {
            long s = System.nanoTime();
            res1 = methods.dfs(g, 0); // inicio por defecto = 0
            t1 = System.nanoTime() - s;
        } else {
            impl1 = false;
        }

        // ejecutar m2
        if ("Kruskal".equals(m2)) {
            long s = System.nanoTime();
            res2 = methods.kruskal(g);
            t2 = System.nanoTime() - s;
        } else if ("Prim".equals(m2)) {
            long s = System.nanoTime();
            res2 = methods.prim(g);
            t2 = System.nanoTime() - s;
        } else if ("DFS".equals(m2)) {
            long s = System.nanoTime();
            res2 = methods.dfs(g, 0); // inicio por defecto = 0
            t2 = System.nanoTime() - s;
        } else {
            impl2 = false;
        }

        // Preparar panel de comparación y mostrar en la tarjeta "compare"
        JPanel comparePanel = new JPanel(new BorderLayout(10, 10));

        // panel central con dos canvases (izq/dcho)
        JPanel center = new JPanel(new GridLayout(1, 2, 10, 0));
        GraphCanvas gcLeft = new GraphCanvas();
        GraphCanvas gcRight = new GraphCanvas();
        // inicialmente solicitar índice (reconstrucción final se forzará después)
        gcLeft.setGraphIndex(selectedGraphIndex);
        gcRight.setGraphIndex(selectedGraphIndex);
        gcLeft.setHighlightedEdges(res1);
        gcRight.setHighlightedEdges(res2);
        center.add(gcLeft);
        center.add(gcRight);
        comparePanel.add(center, BorderLayout.CENTER);

        // panel inferior con info para ambos (caminos y tiempos)
        JPanel info = new JPanel(new GridLayout(1, 2, 10, 0));
        info.setBorder(new EmptyBorder(8, 8, 8, 8));

        String[] labels = g.getListaVertices();
        // izquierdo
        JPanel leftInfo = new JPanel(new BorderLayout(6, 6));
        leftInfo.setBackground(PANEL_BG);
        JTextArea leftText = new JTextArea();
        leftText.setEditable(false);
        leftText.setLineWrap(true);
        leftText.setWrapStyleWord(true);
        leftText.setFont(new Font("SansSerif", Font.PLAIN, 13));
        leftText.setBackground(PANEL_BG);
        int totalLeft = 0;
        if (!impl1) {
            leftText.setText(m1 + " no implementado");
        } else if (res1 == null || res1.isEmpty()) {
            leftText.setText("Sin aristas en resultado");
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < res1.size(); i++) {
                model.Edge e = res1.get(i);
                int u = e.getU(), v = e.getV();
                totalLeft += e.getWeight();
                String lu = (labels != null && u >= 0 && u < labels.length && labels[u] != null) ? labels[u] : String.valueOf(u);
                String lv = (labels != null && v >= 0 && v < labels.length && labels[v] != null) ? labels[v] : String.valueOf(v);
                sb.append("(").append(lu).append(" - ").append(lv).append(": ").append(e.getWeight()).append(")");
                if (i < res1.size() - 1) sb.append(", ");
            }
            leftText.setText(sb.toString());
        }
        JLabel leftTime = new JLabel(impl1 && t1 >= 0 ? (t1 + " ns — Peso: " + totalLeft) : "N/A", SwingConstants.CENTER);
        leftTime.setFont(new Font("SansSerif", Font.BOLD, 14));
        leftInfo.add(new JLabel(m1, SwingConstants.CENTER), BorderLayout.NORTH);
        leftInfo.add(new JScrollPane(leftText, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);
        leftInfo.add(leftTime, BorderLayout.SOUTH);

        // derecho
        JPanel rightInfo = new JPanel(new BorderLayout(6, 6));
        rightInfo.setBackground(PANEL_BG);
        JTextArea rightText = new JTextArea();
        rightText.setEditable(false);
        rightText.setLineWrap(true);
        rightText.setWrapStyleWord(true);
        rightText.setFont(new Font("SansSerif", Font.PLAIN, 13));
        rightText.setBackground(PANEL_BG);
        int totalRight = 0;
        if (!impl2) {
            rightText.setText(m2 + " no implementado");
        } else if (res2 == null || res2.isEmpty()) {
            rightText.setText("Sin aristas en resultado");
        } else {
            StringBuilder sb2 = new StringBuilder();
            for (int i = 0; i < res2.size(); i++) {
                model.Edge e = res2.get(i);
                int u = e.getU(), v = e.getV();
                totalRight += e.getWeight();
                String lu = (labels != null && u >= 0 && u < labels.length && labels[u] != null) ? labels[u] : String.valueOf(u);
                String lv = (labels != null && v >= 0 && v < labels.length && labels[v] != null) ? labels[v] : String.valueOf(v);
                sb2.append("(").append(lu).append(" - ").append(lv).append(": ").append(e.getWeight()).append(")");
                if (i < res2.size() - 1) sb2.append(", ");
            }
            rightText.setText(sb2.toString());
        }
        JLabel rightTime = new JLabel(impl2 && t2 >= 0 ? (t2 + " ns — Peso: " + totalRight) : "N/A", SwingConstants.CENTER);
        rightTime.setFont(new Font("SansSerif", Font.BOLD, 14));
        rightInfo.add(new JLabel(m2, SwingConstants.CENTER), BorderLayout.NORTH);
        rightInfo.add(new JScrollPane(rightText, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);
        rightInfo.add(rightTime, BorderLayout.SOUTH);

        info.add(leftInfo);
        info.add(rightInfo);
        comparePanel.add(info, BorderLayout.SOUTH);

        // Panel superior con título y botón volver (estética igual que botones nav)
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        // botón "Volver" con estilo similar a los nav buttons
        JButton backBtn = new JButton("Volver") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean isRollover = getModel().isRollover();
                g2.setColor(isRollover ? PRIMARY_BLUE.darker() : BG_COLOR);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        backBtn.setForeground(TEXT_DARK);
        backBtn.setContentAreaFilled(false);
        backBtn.setBorderPainted(false);
        backBtn.setFocusPainted(false);
        backBtn.setPreferredSize(new Dimension(100, 35));
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(ae -> {
            centerCardLayout.show(centerCardPanel, "default");
            centerCardPanel.remove(comparePanel);
        });

        // Título centrado; añadimos un filler a la derecha con el mismo tamaño del botón
        JLabel title = new JLabel("Comparación: " + m1 + " vs " + m2, SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        Dimension btnSize = backBtn.getPreferredSize();
        Component eastFiller = Box.createRigidArea(btnSize);

        topBar.add(backBtn, BorderLayout.WEST);
        topBar.add(title, BorderLayout.CENTER);
        topBar.add(eastFiller, BorderLayout.EAST);
        comparePanel.add(topBar, BorderLayout.NORTH);

        // añadir y mostrar tarjeta "compare"
        centerCardPanel.add(comparePanel, "compare");
        centerCardLayout.show(centerCardPanel, "compare");

        // forzar reconstrucción/centrado de los canvases una vez que el layout termine
        SwingUtilities.invokeLater(() -> {
            // volver a fijar índice para que rebuild use tamaños reales
            gcLeft.setGraphIndex(selectedGraphIndex);
            gcRight.setGraphIndex(selectedGraphIndex);
            gcLeft.revalidate();
            gcLeft.repaint();
            gcRight.revalidate();
            gcRight.repaint();
        });
    }

    // Clase interna para dibujar el Grafo
    class GraphCanvas extends JPanel {
        record Node(String id, int x, int y) {}
        record Edge(Node n1, Node n2, int w, int i, int j) {}

        private final List<Node> nodes = new ArrayList<>();
        private final List<Edge> edges = new ArrayList<>();
        private int currentGraphIndex = -1;

        // conjuntos de resaltado (usar clave "u->v")
        private final Set<String> highlightedEdgeKeys = new HashSet<>();
        private final Set<Integer> highlightedNodeIdx = new HashSet<>();

        public GraphCanvas() {
            setBackground(GRAPH_BG);
        }

        public void setGraphIndex(int idx) {
            currentGraphIndex = idx;
            rebuildGraphForIndex(idx);
        }

        public void setHighlightedEdges(List<model.Edge> modelEdges) {
            highlightedEdgeKeys.clear();
            highlightedNodeIdx.clear();
            if (modelEdges == null) return;
            for (model.Edge me : modelEdges) {
                int u = me.getU();
                int v = me.getV();
                // agregar ambas direcciones para que el resaltado funcione aunque el grafo visual sea dirigido
                highlightedEdgeKeys.add(u + "->" + v);
                highlightedEdgeKeys.add(v + "->" + u);
                highlightedNodeIdx.add(u);
                highlightedNodeIdx.add(v);
            }
        }

        public void clearHighlights() {
            highlightedEdgeKeys.clear();
            highlightedNodeIdx.clear();
        }

        private void rebuildGraphForIndex(int idx) {
            nodes.clear();
            edges.clear();

            var grafos = GestorArchivo.getGrafos();
            if (grafos == null || idx < 0 || idx >= grafos.size()) {
                currentGraphIndex = -1;
                return;
            }

            model.Graph g = grafos.get(idx);
            if (g == null) {
                currentGraphIndex = -1;
                return;
            }

            String[] labels = g.getListaVertices();
            int[][] mat = g.getMatrizAd();
            if (labels == null || mat == null) {
                currentGraphIndex = -1;
                return;
            }

            int n = labels.length;
            // Coordenadas basadas en la zona disponible (si aún no tiene tamaño, usar valores por defecto)
            int w = Math.max(1, getWidth());
            int h = Math.max(1, getHeight());
            int cx = w / 2;
            int cy = h / 2;
            int radius = (int) (Math.min(w, h) * 0.35);
            if (radius < 80) radius = 80;

            // Crear nodos en círculo
            for (int i = 0; i < n; i++) {
                double angle = 2 * Math.PI * i / Math.max(1, n);
                int x = cx + (int) (radius * Math.cos(angle));
                int y = cy + (int) (radius * Math.sin(angle));
                String id = labels[i] == null ? String.valueOf(i + 1) : labels[i];
                nodes.add(new Node(id, x, y));
            }

            // Crear aristas a partir de la matriz de adyacencia (ahora dirigido: mat[i][j] representa i->j)
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                     int peso = 0;
                     if (i < mat.length && j < mat[i].length) peso = mat[i][j];
                     if (peso > 0) {
                        // permitir lazos (i == j) y aristas normales
                        edges.add(new Edge(nodes.get(i), nodes.get(j), peso, i, j));
                     }
                }
            }

            currentGraphIndex = idx;
        }

        @Override
        protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);
            Graphics2D g2 = (Graphics2D) g0;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (currentGraphIndex < 0 || nodes.isEmpty()) {
                g2.setColor(new Color(255, 255, 255, 120));
                g2.setFont(new Font("SansSerif", Font.BOLD, 20));
                String msg = "Seleccione un grafo en la barra izquierda";
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(msg)) / 2;
                int ty = getHeight() / 2;
                g2.drawString(msg, tx, ty);
                return;
            }

            // Dibujar aristas (con flechas al final indicando dirección) y etiquetas de peso
            int nodeDiameter = 40;
            for (var edge : edges) {
                int x1 = edge.n1.x;
                int y1 = edge.n1.y;
                int x2 = edge.n2.x;
                int y2 = edge.n2.y;
                String key = edge.i + "->" + edge.j;
                boolean isHighlighted = highlightedEdgeKeys.contains(key);

                g2.setStroke(new BasicStroke(isHighlighted ? 4f : 2f));
                g2.setColor(isHighlighted ? Color.ORANGE : new Color(255, 255, 255, 160));

                // Variables para la posición de la etiqueta de peso (se calculan según tipo de arista)
                double tx, ty;

                if (edge.i == edge.j) {
                    // LAZO SUPERIOR: Curva que sale y entra por el lado izquierdo/superior y derecho/superior
                    int nx = x1;
                    int ny = y1;
                    double r2 = nodeDiameter / 2.0;

                    // Puntos de inicio y fin en la parte superior del nodo
                    // Inicio (Superior-Izquierdo, aprox 20 grados)
                    double angleStart = Math.toRadians(200);
                    double sx = nx + r2 * Math.cos(angleStart);
                    double sy = ny + r2 * Math.sin(angleStart);

                    // Fin (Superior-Derecho, aprox 340 grados)
                    double angleEnd = Math.toRadians(340);
                    double ex = nx + r2 * Math.cos(angleEnd);
                    double ey = ny + r2 * Math.sin(angleEnd);

                    // Distancia vertical que se extiende el lazo por encima del nodo (arco)
                    double loopHeight = Math.max(50, nodeDiameter * 1.8);

                    // Puntos de control para la curva cúbica
                    // C1: Extendido verticalmente hacia arriba desde el punto de inicio
                    double c1x = sx;
                    double c1y = sy - loopHeight;

                    // C2: Extendido verticalmente hacia arriba desde el punto final
                    double c2x = ex;
                    double c2y = ey - loopHeight;

                    // Dibujar la curva
                    g2.draw(new java.awt.geom.CubicCurve2D.Double(sx, sy, c1x, c1y, c2x, c2y, ex, ey));

                    // --- Dibujar flecha en el punto de entrada (ex, ey) ---
                    double ax = ex;
                    double ay = ey;

                    // Para la flecha, usaremos la TANGENTE al punto de entrada.
                    // La dirección de la flecha debe apuntar *hacia la curva* (hacia el centro de la curva),
                    // no radialmente al centro del nodo.
                    
                    // Vector entre C2 y el punto de fin (ex, ey)
                    // Este es el vector de dirección de la curva en el punto final.
                    double dxCurve = ax - c2x;
                    double dyCurve = ay - c2y;
                    double dlen = Math.max(0.0001, Math.hypot(dxCurve, dyCurve));

                    // Vector unitario en la dirección de la curva (hacia el nodo)
                    double udx = dxCurve / dlen;
                    double udy = dyCurve / dlen;

                    // Dimensiones de la flecha
                    double aLen = 12.0;
                    double aHalf = 6.0;

                    // Base de la flecha (detrás de la punta)
                    // Usamos la dirección unitaria de la curva
                    double bx1 = ax - udx * aLen;
                    double by1 = ay - udy * aLen;

                    // Perpendicular para las alas (vector normal a la curva)
                    double px = -udy;
                    double py = udx;

                    int[] axs = {(int) Math.round(ax), (int) Math.round(bx1 + px * aHalf), (int) Math.round(bx1 - px * aHalf)};
                    int[] ays = {(int) Math.round(ay), (int) Math.round(by1 + py * aHalf), (int) Math.round(by1 - py * aHalf)};
                    g2.fillPolygon(axs, ays, 3);


                    // colocar etiqueta en el pico superior del lazo
                    tx = nx; // centro horizontal
                    ty = ny - loopHeight - 16; // por encima del pico
                    
                } else {
                    // arista normal: línea entre nodos (recortada en los bordes de los nodos) y flecha en el extremo
                    double angle = Math.atan2(y2 - y1, x2 - x1);
                    double sx = x1 + Math.cos(angle) * (nodeDiameter / 2.0);
                    double sy = y1 + Math.sin(angle) * (nodeDiameter / 2.0);
                    double ex = x2 - Math.cos(angle) * (nodeDiameter / 2.0);
                    double ey = y2 - Math.sin(angle) * (nodeDiameter / 2.0);

                    g2.drawLine((int) sx, (int) sy, (int) ex, (int) ey);

                    // Dibujar flecha en (ex,ey) apuntando hacia (x2,y2)
                    double arrowLen = 12;
                    double arrowWid = 8;
                    double vx = Math.cos(angle);
                    double vy = Math.sin(angle);
                    double ox = -vy;
                    double oy = vx;
                    double ax2 = ex;
                    double ay2 = ey;
                    double x2p = ax2 - vx * arrowLen + ox * (arrowWid / 2.0);
                    double y2p = ay2 - vy * arrowLen + oy * (arrowWid / 2.0);
                    double x3p = ax2 - vx * arrowLen - ox * (arrowWid / 2.0);
                    double y3p = ay2 - vy * arrowLen - oy * (arrowWid / 2.0);

                    int[] xs = {(int) Math.round(ax2), (int) Math.round(x2p), (int) Math.round(x3p)};
                    int[] ys = {(int) Math.round(ay2), (int) Math.round(y2p), (int) Math.round(y3p)};
                    g2.fillPolygon(xs, ys, 3);

                    // calcular etiqueta en el offset lateral habitual
                    double mx = (x1 + x2) / 2.0;
                    double my = (y1 + y2) / 2.0;
                    double dx = x2 - x1;
                    double dy = y2 - y1;
                    double len = Math.max(1.0, Math.hypot(dx, dy));
                    double nx_ = -dy / len;
                    double ny_ = dx / len;
                    double offset = 12;
                    tx = mx + nx_ * offset;
                    ty = my + ny_ * offset;
                }

                // Dibujar etiqueta de peso (siempre centrada en el medio de la arista)
                g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
                g2.setColor(TEXT_GRAY);
                String weightText = String.valueOf(edge.w);
                FontMetrics fm = g2.getFontMetrics();
                double tw = fm.stringWidth(weightText);
                double th = fm.getHeight();
                g2.drawString(weightText, (int) (tx - tw / 2), (int) (ty + th / 4));
            }

            // Dibujar nodos
            for (var node : nodes) {
                int x = node.x;
                int y = node.y;
                boolean isHighlighted = highlightedNodeIdx.contains(nodes.indexOf(node));

                g2.setColor(isHighlighted ? Color.ORANGE : NODE_COLOR);
                g2.fillOval(x - nodeDiameter / 2, y - nodeDiameter / 2, nodeDiameter, nodeDiameter);

                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(x - nodeDiameter / 2, y - nodeDiameter / 2, nodeDiameter, nodeDiameter);

                // Dibujar etiqueta de nodo (centrado)
                g2.setColor(TEXT_DARK);
                g2.setFont(new Font("SansSerif", Font.BOLD, 14));
                String nodeText = node.id;
                FontMetrics fm = g2.getFontMetrics();
                int tw = fm.stringWidth(nodeText);
                int th = fm.getHeight();
                g2.drawString(nodeText, x - tw / 2, y + th / 4);
            }
        }
    }
}