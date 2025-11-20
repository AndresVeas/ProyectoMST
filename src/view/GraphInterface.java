package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import model.GestorArchivo;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

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

    public GraphInterface() {
        setTitle("ÁRBOL DE EXPANSIÓN MINIMA (MST)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_COLOR);

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

        // 2. Panel Central (Algoritmos + Visualización + Info)
        mainContainer.add(createCentralPanel(), BorderLayout.CENTER);
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
        var gridPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        gridPanel.setBackground(PANEL_BG);
        gridPanel.setMaximumSize(new Dimension(140, 250));
        
        int cantidadGrafos = Math.max(1, GestorArchivo.getGrafos().size());
        for (int i = 1; i <= cantidadGrafos ; i++) {
            var btn = createSquareButton("G" + i, i - 1);
            gridPanel.add(btn);
            graphButtons.add(btn);
        }

        sidebar.add(gridPanel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));

        // Botón Agregar (+)
        var addButton = new CircleButton("+");
        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(addButton);

        addButton.addActionListener(e -> {
                    AgregarVerticesApp a = new AgregarVerticesApp();
                    a.setLocationRelativeTo(null);
                    a.setVisible(true);
                    this.dispose();
        });

        var addLabel = new JLabel("Agregar grafo");
        addLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        addLabel.setForeground(TEXT_GRAY);
        addLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebar.add(addLabel);

        return sidebar;
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
        
        centerPanel.add(navPanel, BorderLayout.NORTH);

        // --- Center: Visualización del Grafo ---
        graphCanvas = new GraphCanvas();
        centerPanel.add(graphCanvas, BorderLayout.CENTER);

        // --- Bottom: Información ---
        var infoPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        infoPanel.setOpaque(false);
        infoPanel.setPreferredSize(new Dimension(0, 100));

        infoPanel.add(createInfoBox("Camino mínimo", "A → B. E E → D (Cost: 15)"));
        infoPanel.add(createInfoBox("Coste computacional", "O(E log V)"));

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
        btn.addActionListener(e -> selectGraph(index));
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

    private JPanel createInfoBox(String title, String value) {
        var panel = new JPanel(new GridLayout(2, 1));
        panel.setBackground(PANEL_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1), // Borde sutil
            new EmptyBorder(15, 15, 15, 15)
        ));

        var titleLbl = new JLabel(title, SwingConstants.CENTER);
        titleLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        titleLbl.setForeground(TEXT_GRAY);

        var valueLbl = new JLabel(value, SwingConstants.CENTER);
        valueLbl.setFont(new Font("SansSerif", Font.BOLD, 16));
        valueLbl.setForeground(TEXT_DARK);

        panel.add(titleLbl);
        panel.add(valueLbl);
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

    // Selecciona un grafo de la barra lateral
    private void selectGraph(int index) {
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
    }

    // Selecciona algoritmo en la barra superior (permanece marcado)
    private void selectAlgorithm(String name) {
        selectedAlgorithm = name;
        for (JButton b : navButtons) {
            if (b.getText().equals(name)) {
                b.setForeground(Color.WHITE);
            } else {
                b.setForeground(TEXT_DARK);
            }
        }
        // Si necesitas que la selección del algoritmo cambie la visualización,
        // aquí puedes notificar al graphCanvas para que dibuje resultados del algoritmo.
        repaint();
    }

    // Clase interna para dibujar el Grafo
    class GraphCanvas extends JPanel {
        record Node(String id, int x, int y) {}
        record Edge(Node n1, Node n2, int w) {}

        private final List<Node> nodes = new ArrayList<>();
        private final List<Edge> edges = new ArrayList<>();
        private int currentGraphIndex = -1;

        public GraphCanvas() {
            setBackground(GRAPH_BG);
        }

        public void setGraphIndex(int idx) {
            currentGraphIndex = idx;
            rebuildGraphForIndex(idx);
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

            // Crear aristas a partir de la matriz de adyacencia (considera grafo no dirigido)
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    int peso = 0;
                    if (i < mat.length && j < mat[i].length) peso = mat[i][j];
                    if (peso > 0) {
                        edges.add(new Edge(nodes.get(i), nodes.get(j), peso));
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

            // Dibujar aristas
            g2.setStroke(new BasicStroke(2));
            g2.setColor(new Color(255, 255, 255, 160));
            for (var edge : edges) {
                int x1 = edge.n1.x;
                int y1 = edge.n1.y;
                int x2 = edge.n2.x;
                int y2 = edge.n2.y;
                g2.drawLine(x1, y1, x2, y2);
            }

            // Etiquetas de peso
            g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            for (var edge : edges) {
                int x1 = edge.n1.x;
                int y1 = edge.n1.y;
                int x2 = edge.n2.x;
                int y2 = edge.n2.y;
                String wstr = String.valueOf(edge.w);

                double mx = (x1 + x2) / 2.0;
                double my = (y1 + y2) / 2.0;

                double dx = x2 - x1;
                double dy = y2 - y1;
                double len = Math.max(1.0, Math.hypot(dx, dy));
                double nx = -dy / len;
                double ny = dx / len;
                double offset = 12;
                double tx = mx + nx * offset;
                double ty = my + ny * offset;

                FontMetrics fm = g2.getFontMetrics();
                int sw = fm.stringWidth(wstr);
                int sh = fm.getHeight();
                int pad = 6;
                g2.setColor(new Color(255, 255, 255, 220));
                g2.fillRoundRect((int)(tx - sw/2.0) - pad/2, (int)(ty - sh/2.0) - pad/2, sw + pad, sh + pad/2, 8, 8);
                g2.setColor(TEXT_DARK);
                g2.drawString(wstr, (int)(tx - sw/2.0), (int)(ty + fm.getAscent()/2.0) - 2);
            }

            // Dibujar nodos
            for (var node : nodes) {
                int r = 40;
                int x = node.x - r / 2;
                int y = node.y - r / 2;

                g2.setColor(NODE_COLOR);
                g2.fillOval(x, y, r, r);

                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                int textX = x + (r - fm.stringWidth(node.id)) / 2;
                int textY = y + ((r - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(node.id, textX, textY);
            }
        }
    }

}