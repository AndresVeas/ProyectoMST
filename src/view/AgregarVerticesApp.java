package view;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

public class AgregarVerticesApp extends JFrame {

    // --- PALETA DE COLORES (Consistente con la app anterior) ---
    private static final Color COLOR_FONDO_APP = new Color(240, 242, 245);
    private static final Color COLOR_CARD_BG = Color.WHITE;
    private static final Color COLOR_TEXTO_TITULO = new Color(31, 41, 55);
    private static final Color COLOR_TEXTO_DESC = new Color(107, 114, 128);
    private static final Color COLOR_PRIMARY = new Color(59, 130, 246); // Azul Royal
    private static final Color COLOR_PRIMARY_HOVER = new Color(37, 99, 235);
    private static final Color COLOR_INPUT_BORDER = new Color(209, 213, 219); // Gris claro para bordes

    // Fuentes
    private static final Font FONT_TITULO = new Font("SansSerif", Font.BOLD, 26);
    private static final Font FONT_DESC = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font FONT_LABEL_NUM = new Font("SansSerif", Font.BOLD, 16);
    private static final Font FONT_INPUT = new Font("SansSerif", Font.PLAIN, 14);

    // Componentes dinámicos
    private JPanel verticesContainer; // Aquí se añadirán los campos
    private JScrollPane scrollPane;
    private int vertexCount = 0; // Contador de vértices

    public AgregarVerticesApp() {
        setTitle("Agregar Vértices");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 650);
        setLocationRelativeTo(null);

        // Panel Principal (Fondo)
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(COLOR_FONDO_APP);
        add(mainPanel);

        // --- TARJETA BLANCA ---
        RoundedPanel cardPanel = new RoundedPanel(25, COLOR_CARD_BG);
        cardPanel.setLayout(new BorderLayout());
        cardPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        
        // Dimensiones preferidas para la tarjeta
        cardPanel.setPreferredSize(new Dimension(420, 550));

        // 1. HEADER (Título y Descripción)
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Agregar vértices");
        titleLabel.setFont(FONT_TITULO);
        titleLabel.setForeground(COLOR_TEXTO_TITULO);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea descLabel = new JTextArea("Puede usar números, letras o lo que quiera para identificar a los nodos de su grafo.");
        descLabel.setFont(FONT_DESC);
        descLabel.setForeground(COLOR_TEXTO_DESC);
        descLabel.setWrapStyleWord(true);
        descLabel.setLineWrap(true);
        descLabel.setEditable(false);
        descLabel.setOpaque(false);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        descLabel.setBorder(new EmptyBorder(10, 0, 10, 0));

        headerPanel.add(titleLabel);
        headerPanel.add(descLabel);
        cardPanel.add(headerPanel, BorderLayout.NORTH);

        // 2. LISTA DINÁMICA (Centro con Scroll)
        verticesContainer = new JPanel();
        verticesContainer.setLayout(new BoxLayout(verticesContainer, BoxLayout.Y_AXIS));
        verticesContainer.setOpaque(false);

        // Agregar los primeros 3 campos por defecto
        addVertexField();
        addVertexField();
        addVertexField();

        // Configurar ScrollPane (invisible pero funcional)
        scrollPane = new JScrollPane(verticesContainer);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        cardPanel.add(scrollPane, BorderLayout.CENTER);

        // 3. FOOTER (Botón "Agregar más" y "Siguiente")
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));
        footerPanel.setOpaque(false);
        footerPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        // A. Panel para el botón "Agregar otro nodo"
        JPanel addMorePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        addMorePanel.setOpaque(false);
        
        // Botón Circular + Texto
        CirclePlusButton btnAddCircle = new CirclePlusButton();
        JLabel lblAddText = new JLabel(" Agregar otro nodo");
        lblAddText.setForeground(COLOR_PRIMARY);
        lblAddText.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblAddText.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Evento para agregar
        MouseAdapter addAction = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                addVertexField();
                // Scrollear hacia abajo automáticamente
                SwingUtilities.invokeLater(() -> {
                    JScrollBar vertical = scrollPane.getVerticalScrollBar();
                    vertical.setValue(vertical.getMaximum());
                });
            }
        };
        btnAddCircle.addMouseListener(addAction);
        lblAddText.addMouseListener(addAction);

        addMorePanel.add(btnAddCircle);
        addMorePanel.add(lblAddText);
        footerPanel.add(addMorePanel);
        
        footerPanel.add(Box.createVerticalStrut(25)); // Espacio

        // B. Botón "Siguiente" con Flecha
        NextButton btnNext = new NextButton("Siguiente");
        btnNext.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrado o LEFT_ALIGNMENT
        // Hacer que ocupe el ancho completo o sea grande
        btnNext.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        btnNext.addActionListener(e -> {
            // Recolectar valores válidos (se ignoran placeholders "Nombre del nodo" y cadenas vacías)
            java.util.List<String> lista = new java.util.ArrayList<>();
            Component[] rows = verticesContainer.getComponents();
            for (Component comp : rows) {
                if (comp instanceof JPanel) {
                    JPanel row = (JPanel) comp;
                    if (row.getComponentCount() > 1 && row.getComponent(1) instanceof JTextField) {
                        String txt = ((JTextField) row.getComponent(1)).getText();
                        if (txt != null) {
                            String val = txt.trim();
                            if (!val.isEmpty() && !val.equalsIgnoreCase("Nombre del nodo")) {
                                lista.add(val);
                            }
                        }
                    }
                }
            }

            String[] vertices = lista.toArray(new String[0]);
            if (vertices.length < 1) {
                JOptionPane.showMessageDialog(this, "Por favor, ingrese al menos un nodo válido.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            AgregarAristasApp g = new AgregarAristasApp(vertices);
            g.setLocationRelativeTo(null);
            g.setVisible(true);
            this.setVisible(false);
         });

        footerPanel.add(btnNext);
        cardPanel.add(footerPanel, BorderLayout.SOUTH);

        mainPanel.add(cardPanel);
    }

    // --- MÉTODO LÓGICO: Agregar un nuevo campo ---
    private void addVertexField() {
        vertexCount++;
        
        JPanel row = new JPanel();
        row.setLayout(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50)); // Altura fija por fila
        row.setBorder(new EmptyBorder(0, 0, 15, 0)); // Margen inferior entre filas

        // Número (1., 2., 3.)
        JLabel numLabel = new JLabel(vertexCount + ".");
        numLabel.setFont(FONT_LABEL_NUM);
        numLabel.setForeground(Color.GRAY);
        numLabel.setPreferredSize(new Dimension(25, 30));
        numLabel.setVerticalAlignment(SwingConstants.CENTER);

        // Campo de texto personalizado
        RoundedTextField textField = new RoundedTextField("Nombre del nodo");
        
        row.add(numLabel, BorderLayout.WEST);
        row.add(textField, BorderLayout.CENTER);

        verticesContainer.add(row);
        verticesContainer.revalidate();
        verticesContainer.repaint();
    }

    // ==========================================
    // CLASES PERSONALIZADAS PARA EL DISEÑO UI
    // ==========================================

    /** 1. Panel Tarjeta Redondeada */
    static class RoundedPanel extends JPanel {
        private int radius;
        private Color bgColor;
        public RoundedPanel(int radius, Color bgColor) {
            this.radius = radius;
            this.bgColor = bgColor;
            setOpaque(false);
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), radius, radius));
            
            // Sombra sutil (opcional)
            // g2.setColor(new Color(0,0,0,10));
            // g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, radius, radius);
            
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** 2. Input Text Redondeado con Placeholder */
    static class RoundedTextField extends JTextField implements FocusListener {
        private String placeholder;
        private boolean showingPlaceholder;

        public RoundedTextField(String placeholder) {
            this.placeholder = placeholder;
            this.showingPlaceholder = true;
            setOpaque(false); // Nosotros pintamos el fondo
            setBorder(new EmptyBorder(10, 15, 10, 15)); // Padding interno del texto
            setFont(FONT_INPUT);
            setForeground(Color.GRAY);
            setText(placeholder);
            addFocusListener(this);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Fondo blanco/redondeado
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);

            // Borde
            g2.setColor(COLOR_INPUT_BORDER);
            g2.setStroke(new BasicStroke(1));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);

            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        public void focusGained(FocusEvent e) {
            if (this.getText().equals(placeholder)) {
                this.setText("");
                this.setForeground(COLOR_TEXTO_TITULO);
                this.showingPlaceholder = false;
            }
        }

        @Override
        public void focusLost(FocusEvent e) {
            if (this.getText().isEmpty()) {
                this.setText(placeholder);
                this.setForeground(Color.GRAY);
                this.showingPlaceholder = true;
            }
        }
    }

    /** 3. Botón Circular Pequeño con "+" (Dibujado en código) */
    static class CirclePlusButton extends JComponent {
        private boolean isHovered = false;

        public CirclePlusButton() {
            setPreferredSize(new Dimension(24, 24));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
                @Override
                public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Color del círculo (solo borde o relleno suave si hover)
            if (isHovered) {
                g2.setColor(new Color(230, 240, 255)); // Fondo muy clarito en hover
                g2.fillOval(0, 0, getWidth(), getHeight());
            }
            
            // Dibujar el signo + (azul)
            g2.setColor(COLOR_PRIMARY);
            g2.setStroke(new BasicStroke(2));
            
            int cx = getWidth() / 2;
            int cy = getHeight() / 2;
            int size = 5; // Tamaño de la cruz

            // Línea horizontal
            g2.drawLine(cx - size, cy, cx + size, cy);
            // Línea vertical
            g2.drawLine(cx, cy - size, cx, cy + size);

            // Dibujar círculo (borde opcional, la imagen parece solo tener texto e icono)
            // g2.drawOval(1, 1, getWidth()-2, getHeight()-2);

            g2.dispose();
        }
    }

    /** 4. Botón "Siguiente" con Flecha (Dibujada en código) */
    static class NextButton extends JButton {
        private boolean isHovered = false;

        public NextButton(String text) {
            super(text);
            setFont(new Font("SansSerif", Font.BOLD, 16));
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
                @Override
                public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Fondo Azul
            g2.setColor(isHovered ? COLOR_PRIMARY_HOVER : COLOR_PRIMARY);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

            // Calcular posición del texto para centrarlo visualmente con la flecha
            FontMetrics fm = g2.getFontMetrics();
            String text = getText();
            int textW = fm.stringWidth(text);
            int arrowSpace = 30; // Espacio para la flecha
            int totalContentW = textW + arrowSpace;
            
            int startX = (getWidth() - totalContentW) / 2;
            int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;

            // Dibujar Texto
            g2.setColor(Color.WHITE);
            g2.drawString(text, startX, textY);

            // Dibujar Flecha (->)
            int arrowX = startX + textW + 10;
            int arrowY = getHeight() / 2;
            int arrowSize = 6;

            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            
            // Cuerpo flecha
            g2.drawLine(arrowX, arrowY, arrowX + 15, arrowY);
            // Puntas flecha
            Path2D arrowHead = new Path2D.Float();
            arrowHead.moveTo(arrowX + 15 - arrowSize, arrowY - arrowSize);
            arrowHead.lineTo(arrowX + 15, arrowY);
            arrowHead.lineTo(arrowX + 15 - arrowSize, arrowY + arrowSize);
            g2.draw(arrowHead);

            g2.dispose();
        }
    }

}