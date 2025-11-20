package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.regex.Pattern;

public class AgregarAristasApp extends JFrame {

    // --- PALETA DE COLORES ---
    private static final Color COLOR_FONDO_APP = new Color(240, 242, 245);
    private static final Color COLOR_CARD_BG = Color.WHITE;
    private static final Color COLOR_TEXTO_TITULO = new Color(31, 41, 55);
    private static final Color COLOR_TEXTO_DESC = new Color(107, 114, 128); // Gris subtítulo
    private static final Color COLOR_PRIMARY = new Color(59, 130, 246); // Azul Royal
    private static final Color COLOR_PRIMARY_HOVER = new Color(37, 99, 235);
    private static final Color COLOR_INPUT_BORDER = new Color(209, 213, 219);
    private static final Color COLOR_PLUS_BG = new Color(219, 234, 254); // Azul muy clarito para el círculo +
    private static final Color COLOR_PLUS_ICON = new Color(59, 130, 246); // El color del signo +

    // Fuentes
    private static final Font FONT_TITULO = new Font("SansSerif", Font.BOLD, 26);
    private static final Font FONT_DESC = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font FONT_NUMERO = new Font("SansSerif", Font.BOLD, 14);
    private static final Font FONT_INPUT = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font FONT_BOTON = new Font("SansSerif", Font.BOLD, 16);

    // Componentes lógicos
    private JPanel edgesContainer;
    private JScrollPane scrollPane;
    private int edgeCount = 0;

    public AgregarAristasApp() {
        setTitle("Agregar Aristas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        setLocationRelativeTo(null);

        // Panel Principal
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // separación de 10px respecto al borde de la ventana
        mainPanel.setBackground(COLOR_FONDO_APP);
        add(mainPanel);

        // --- TARJETA CENTRAL ---
        RoundedPanel cardPanel = new RoundedPanel(30, COLOR_CARD_BG);
        cardPanel.setLayout(new BorderLayout());
        // reducir un poco los paddings internos para que la tarjeta quepa bien en 600x600
        cardPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        cardPanel.setPreferredSize(new Dimension(520, 520));

        // 1. HEADER (Título e Instrucciones)
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Agregar aristas");
        titleLabel.setFont(FONT_TITULO);
        titleLabel.setForeground(COLOR_TEXTO_TITULO);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea descLabel = new JTextArea("Ingrese los datos siguiendo el orden:Nodo Inicio, Nodo Destino, Peso.");
        descLabel.setFont(FONT_DESC);
        descLabel.setForeground(COLOR_TEXTO_DESC);
        descLabel.setWrapStyleWord(true);
        descLabel.setLineWrap(true);
        descLabel.setEditable(false);
        descLabel.setOpaque(false);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Centrar texto dentro del Area
        descLabel.setBorder(new EmptyBorder(10, 20, 10, 20)); 

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(descLabel);
        cardPanel.add(headerPanel, BorderLayout.NORTH);

        // 2. LISTA DE ARISTAS (ScrollPane)
        edgesContainer = new JPanel();
        edgesContainer.setLayout(new BoxLayout(edgesContainer, BoxLayout.Y_AXIS));
        edgesContainer.setOpaque(false);

        // Agregar campos iniciales (según imagen: 1 con ejemplo, 2 y 3 con formato)
        addEdgeField("Ej: 1, 2, 15");
        addEdgeField("Inicio, Destino, Peso");
        addEdgeField("Inicio, Destino, Peso");

        scrollPane = new JScrollPane(edgesContainer);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        // Evitar scroll horizontal (los campos se ajustan para caber)
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        cardPanel.add(scrollPane, BorderLayout.CENTER);

        // 3. FOOTER (Botón + y Botón Ver Grafo)
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));
        footerPanel.setOpaque(false);
        footerPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        // A. Botón "+" Circular (Centrado)
        JPanel plusContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        plusContainer.setOpaque(false);
        CirclePlusButton btnPlus = new CirclePlusButton();
        btnPlus.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                addEdgeField("[Inicio, Destino, Peso]");
                // Auto-scroll al final
                SwingUtilities.invokeLater(() -> {
                    JScrollBar vertical = scrollPane.getVerticalScrollBar();
                    vertical.setValue(vertical.getMaximum());
                });
            }
        });
        plusContainer.add(btnPlus);
        footerPanel.add(plusContainer);

        footerPanel.add(Box.createVerticalStrut(20)); // Espacio

        // B. Botón "Ver Grafo" con Icono
        GraphButton btnVerGrafo = new GraphButton("Ver Grafo");
        btnVerGrafo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        btnVerGrafo.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Procesando aristas y generando grafo...");
            // Aquí conectas con tu lógica principal
        });
        
        footerPanel.add(btnVerGrafo);
        cardPanel.add(footerPanel, BorderLayout.SOUTH);

        mainPanel.add(cardPanel);
    }

    // Método para agregar fila (modificado para 3 campos: inicio, destino, peso)
    private void addEdgeField(String examplePlaceholder) {
        edgeCount++;

        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        row.setBorder(new EmptyBorder(0, 0, 15, 0));

        // Número pequeño arriba a la izquierda
        JLabel lblNum = new JLabel(edgeCount + "");
        lblNum.setFont(FONT_NUMERO);
        lblNum.setForeground(COLOR_TEXTO_TITULO);
        lblNum.setPreferredSize(new Dimension(28, 45));
        lblNum.setVerticalAlignment(SwingConstants.TOP);
        lblNum.setBorder(new EmptyBorder(12, 0, 0, 0));

        // Panel que contiene los 3 campos
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setOpaque(false);
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.X_AXIS));
        fieldsPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        // Campo Inicio (ANCHO REDUCIDO)
        RoundedTextField txtInicio = new RoundedTextField("Inicio (Ej: 1)");
        txtInicio.setMaximumSize(new Dimension(110, 45));
        txtInicio.setPreferredSize(new Dimension(110, 45));

        // Campo Destino (ANCHO REDUCIDO)
        RoundedTextField txtDestino = new RoundedTextField("Destino (Ej: 2)");
        txtDestino.setMaximumSize(new Dimension(110, 45));
        txtDestino.setPreferredSize(new Dimension(110, 45));
        txtDestino.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Campo Peso (solo enteros) (ANCHO REDUCIDO)
        RoundedNumberField txtPeso = new RoundedNumberField("Peso (15)");
        txtPeso.setMaximumSize(new Dimension(60, 45));
        txtPeso.setPreferredSize(new Dimension(60, 45));
        txtPeso.setHorizontalAlignment(SwingConstants.CENTER);

        // Separadores visuales entre campos
        fieldsPanel.add(txtInicio);
        fieldsPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        fieldsPanel.add(txtDestino);
        fieldsPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        fieldsPanel.add(txtPeso);

        row.add(lblNum, BorderLayout.WEST);
        row.add(fieldsPanel, BorderLayout.CENTER);

        edgesContainer.add(row);
        edgesContainer.revalidate();
        edgesContainer.repaint();
    }

    // ==========================================
    // COMPONENTES PERSONALIZADOS
    // ==========================================

    /** 1. Tarjeta con fondo blanco y bordes redondeados */
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
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** 2. Campo de Texto Redondeado */
    static class RoundedTextField extends JTextField implements FocusListener {
        private String placeholder;
        protected boolean showingPlaceholder;

        public RoundedTextField(String placeholder) {
            this.placeholder = placeholder;
            this.showingPlaceholder = true;
            setOpaque(false);
            setBorder(new EmptyBorder(12, 15, 12, 15));
            setFont(FONT_INPUT);
            setForeground(Color.GRAY);
            setText(placeholder);
            addFocusListener(this);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);

            g2.setColor(COLOR_INPUT_BORDER);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);

            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        public void focusGained(FocusEvent e) {
            if (showingPlaceholder) {
                setText("");
                setForeground(COLOR_TEXTO_TITULO);
                showingPlaceholder = false;
            }
        }
        @Override
        public void focusLost(FocusEvent e) {
            if (getText().isEmpty()) {
                setText(placeholder);
                setForeground(Color.GRAY);
                showingPlaceholder = true;
            }
        }
    }

    // Campo numérico que solo acepta enteros (visualmente similar)
    static class RoundedNumberField extends JTextField implements FocusListener {
        private String placeholder;
        private boolean showingPlaceholder;

        public RoundedNumberField(String placeholder) {
            this.placeholder = placeholder;
            this.showingPlaceholder = true;
            setOpaque(false);
            setBorder(new EmptyBorder(12, 12, 12, 12));
            setFont(FONT_INPUT);
            setForeground(Color.GRAY);
            setText(placeholder);
            addFocusListener(this);

            // Añadir filtro que solo permita dígitos, pero permite placeholder cuando esté activo
            ((AbstractDocument) getDocument()).setDocumentFilter(new IntDocumentFilter(this));
        }

        // Getter para que el filtro pueda comprobar el estado del placeholder
        public boolean isShowingPlaceholder() {
            return showingPlaceholder;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);

            g2.setColor(COLOR_INPUT_BORDER);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);

            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        public void focusGained(FocusEvent e) {
            if (showingPlaceholder) {
                setText("");
                setForeground(COLOR_TEXTO_TITULO);
                showingPlaceholder = false;
            }
        }
        @Override
        public void focusLost(FocusEvent e) {
            if (getText().isEmpty()) {
                setText(placeholder);
                setForeground(Color.GRAY);
                showingPlaceholder = true;
            }
        }
    }

    // Filtro que permite solo dígitos cuando el campo no está mostrando placeholder
    static class IntDocumentFilter extends DocumentFilter {
        private static final Pattern DIGITS = Pattern.compile("\\d*");
        private final RoundedNumberField owner;

        public IntDocumentFilter(RoundedNumberField owner) {
            this.owner = owner;
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            String current = fb.getDocument().getText(0, fb.getDocument().getLength());
            String before = current.substring(0, offset);
            String after = current.substring(offset);
            String resultado = before + (string == null ? "" : string) + after;
            if (allowChange(resultado)) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            String current = fb.getDocument().getText(0, fb.getDocument().getLength());
            String before = current.substring(0, offset);
            String after = current.substring(Math.min(current.length(), offset + length));
            String resultado = before + (text == null ? "" : text) + after;
            if (allowChange(resultado)) {
                super.replace(fb, offset, length, text, attrs);
            }
        }

        @Override
        public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
            // Permitir borrado siempre (resultado puede quedar vacío)
            super.remove(fb, offset, length);
        }

        private boolean allowChange(String proposedFullText) {
            // Si el campo está en modo placeholder, permitir cualquier texto (para que se muestre)
            if (owner.isShowingPlaceholder()) return true;
            // Validar que el resultado sea solo dígitos (o vacío)
            return DIGITS.matcher(proposedFullText).matches();
        }
    }

    /** 3. Botón Circular "+" (Azul claro con signo azul) */
    static class CirclePlusButton extends JComponent {
        private boolean isHovered = false;

        public CirclePlusButton() {
            setPreferredSize(new Dimension(40, 40)); // Tamaño del círculo
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

            // Fondo círculo
            g2.setColor(isHovered ? COLOR_PLUS_BG.darker() : COLOR_PLUS_BG);
            g2.fillOval(0, 0, getWidth(), getHeight());

            // Signo "+"
            g2.setColor(COLOR_PLUS_ICON);
            g2.setStroke(new BasicStroke(2.5f));
            int cx = getWidth() / 2;
            int cy = getHeight() / 2;
            int size = 7; 

            g2.drawLine(cx - size, cy, cx + size, cy); // Horizontal
            g2.drawLine(cx, cy - size, cx, cy + size); // Vertical

            g2.dispose();
        }
    }

    /** 4. Botón Grande "Ver Grafo" con Icono dibujado */
    static class GraphButton extends JButton {
        private boolean isHovered = false;

        public GraphButton(String text) {
            super(text);
            setFont(FONT_BOTON);
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

            // -- DIBUJAR EL ICONO DEL GRAFO (3 nodos conectados) --
            // Calculamos posición para centrar contenido (Icono + Texto)
            FontMetrics fm = g2.getFontMetrics();
            int textW = fm.stringWidth(getText());
            int iconSize = 20;
            int gap = 10;
            int contentW = iconSize + gap + textW;
            
            int startX = (getWidth() - contentW) / 2;
            int centerY = getHeight() / 2;

            // Dibujar Icono (coordenadas relativas a startX)
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(2f));
            
            // Nodos (círculos pequeños)
            int nodeR = 3; // radio visual
            // Nodo arriba
            g2.fillOval(startX + 10 - nodeR, centerY - 8 - nodeR, nodeR*2, nodeR*2);
            // Nodo abajo izq
            g2.fillOval(startX - nodeR, centerY + 6 - nodeR, nodeR*2, nodeR*2);
            // Nodo abajo der
            g2.fillOval(startX + 20 - nodeR, centerY + 6 - nodeR, nodeR*2, nodeR*2);
            
            // Líneas conectando
            g2.drawLine(startX + 10, centerY - 8, startX, centerY + 6);
            g2.drawLine(startX + 10, centerY - 8, startX + 20, centerY + 6);
            g2.drawLine(startX, centerY + 6, startX + 20, centerY + 6);

            // -- DIBUJAR TEXTO --
            g2.drawString(getText(), startX + iconSize + gap, centerY + (fm.getAscent()/2) - 2);

            g2.dispose();
        }
    }

}
