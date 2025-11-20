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
        
        // Borde redondeado visual (Simulado con panel dentro de panel o custom painting, aquí simple)
        
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
        
        int cantidadGrafos = GestorArchivo.getGrafos().size();
        for (int i = 1; i <= cantidadGrafos ; i++) {
            gridPanel.add(createSquareButton("G" + i));
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
        
        navPanel.add(createNavButton("Kruskal", false));
        navPanel.add(createNavButton("Prim", true)); // Activo
        navPanel.add(createNavButton("DFS", false));
        navPanel.add(createNavButton("Comparar", false));
        
        centerPanel.add(navPanel, BorderLayout.NORTH);

        // --- Center: Visualización del Grafo ---
        var graphCanvas = new GraphCanvas();
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

    private JButton createSquareButton(String text) {
        var btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btn.setForeground(TEXT_GRAY);
        btn.setBackground(new Color(241, 245, 249)); // Slate-100
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createNavButton(String text, boolean isActive) {
        var btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
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
        btn.setForeground(isActive ? Color.WHITE : TEXT_DARK);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(100, 35));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
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

    // Clase interna para dibujar el Grafo
    class GraphCanvas extends JPanel {
        // Nodos "Dummy" para el ejemplo visual
        record Node(String id, int x, int y) {}
        record Edge(Node n1, Node n2) {}

        private final List<Node> nodes = new ArrayList<>();
        private final List<Edge> edges = new ArrayList<>();

        public GraphCanvas() {
            setBackground(GRAPH_BG);
            // Crear datos de ejemplo similares a la imagen
            var nA = new Node("A", 300, 250);
            var nB = new Node("B", 500, 300);
            var nE = new Node("E", 650, 200);
            
            // Nodos extra para simular las líneas que bajan
            var nAd = new Node("", 300, 400);
            var nBd = new Node("", 500, 450);
            var nEd = new Node("", 650, 400);

            nodes.add(nA);
            nodes.add(nB);
            nodes.add(nE);

            edges.add(new Edge(nA, nB));
            edges.add(new Edge(nB, nE));
            // Líneas verticales decorativas (estilo proyección)
            edges.add(new Edge(nA, nAd));
            edges.add(new Edge(nB, nBd));
            edges.add(new Edge(nE, nEd));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Dibujar Aristas
            g2.setStroke(new BasicStroke(2));
            g2.setColor(new Color(255, 255, 255, 100)); // Blanco semi-transparente
            for (var edge : edges) {
                g2.drawLine(edge.n1.x, edge.n1.y, edge.n2.x, edge.n2.y);
            }

            // Dibujar Nodos
            for (var node : nodes) {
                if (node.id.isEmpty()) continue; // Ignorar nodos invisibles de proyección
                int r = 40; // Radio
                int x = node.x - r/2;
                int y = node.y - r/2;

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