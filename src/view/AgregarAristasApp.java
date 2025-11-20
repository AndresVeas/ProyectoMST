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

    private String [] listadoVertices;
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
    private static final Font FONT_TITULO = new Font("SansSerif", Font.BOLD, 24);
    private static final Font FONT_TITULO_RESUMEN = new Font("SansSerif", Font.BOLD, 18);
    private static final Font FONT_DESC = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font FONT_NUMERO = new Font("SansSerif", Font.BOLD, 14);
    private static final Font FONT_INPUT = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font FONT_BOTON = new Font("SansSerif", Font.BOLD, 16);
    private static final Font FONT_RESUMEN_ITEM = new Font("SansSerif", Font.PLAIN, 15);

    // Componentes lógicos
    private JPanel edgesContainer;
    private JScrollPane scrollPaneForm;
    private int edgeCount = 0;

    // Constructor
    public AgregarAristasApp(String [] listadoVertices) {
        this.listadoVertices = listadoVertices;

        System.out.println(listadoVertices);
        setTitle("Agregar Aristas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // 1. Modificación de tamaño solicitado
        setSize(1000, 700);
        setLocationRelativeTo(null);

        // Panel Principal con BorderLayout para dividir Izquierda/Centro
        JPanel mainPanel = new JPanel(new BorderLayout(20, 0)); // Separación horizontal de 20px
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        mainPanel.setBackground(COLOR_FONDO_APP);
        setContentPane(mainPanel);

        // =================================================================
        // PARTE IZQUIERDA: PANEL DE RESUMEN DE NODOS
        // =================================================================
        RoundedPanel summaryCard = new RoundedPanel(25, Color.WHITE);
        summaryCard.setLayout(new BorderLayout());
        summaryCard.setPreferredSize(new Dimension(280, 0)); // Ancho fijo para el resumen
        summaryCard.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header del Resumen
        JLabel lblResumenTitulo = new JLabel("Resumen de Nodos");
        lblResumenTitulo.setFont(FONT_TITULO_RESUMEN);
        lblResumenTitulo.setForeground(COLOR_TEXTO_TITULO);
        lblResumenTitulo.setBorder(new EmptyBorder(0, 0, 15, 0));
        summaryCard.add(lblResumenTitulo, BorderLayout.NORTH);

        // Lista del Resumen (Dentro de un ScrollPane)
        JPanel summaryListContainer = new JPanel();
        summaryListContainer.setLayout(new BoxLayout(summaryListContainer, BoxLayout.Y_AXIS));
        summaryListContainer.setOpaque(false);

        // --- AQUÍ CARGAMOS LOS DATOS ---
        cargarResumenNodosDummy(summaryListContainer);
        // -------------------------------

        JScrollPane scrollResumen = new JScrollPane(summaryListContainer);
        scrollResumen.setOpaque(false);
        scrollResumen.getViewport().setOpaque(false);
        scrollResumen.setBorder(null);
        summaryCard.add(scrollResumen, BorderLayout.CENTER);

        // Agregar panel izquierdo al layout principal
        mainPanel.add(summaryCard, BorderLayout.WEST);


        // =================================================================
        // PARTE CENTRAL: FORMULARIO DE ARISTAS (Tu código anterior)
        // =================================================================
        RoundedPanel formCardPanel = new RoundedPanel(30, COLOR_CARD_BG);
        formCardPanel.setLayout(new BorderLayout());
        // Ajustamos padding interno
        formCardPanel.setBorder(new EmptyBorder(25, 40, 25, 40));

        // 1. HEADER (Título e Instrucciones)
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Agregar aristas");
        titleLabel.setFont(FONT_TITULO);
        titleLabel.setForeground(COLOR_TEXTO_TITULO);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea descLabel = new JTextArea("Utilice los números del resumen para indicar Origen y Destino.\nFormato: Origen, Destino, Peso.");
        descLabel.setFont(FONT_DESC);
        descLabel.setForeground(COLOR_TEXTO_DESC);
        descLabel.setWrapStyleWord(true);
        descLabel.setLineWrap(true);
        descLabel.setEditable(false);
        descLabel.setOpaque(false);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        descLabel.setBorder(new EmptyBorder(10, 20, 10, 20));

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(descLabel);
        formCardPanel.add(headerPanel, BorderLayout.NORTH);

        // 2. LISTA DE ARISTAS (ScrollPane)
        edgesContainer = new JPanel();
        edgesContainer.setLayout(new BoxLayout(edgesContainer, BoxLayout.Y_AXIS));
        edgesContainer.setOpaque(false);

        // Agregar campos iniciales
        addEdgeField(true); // Primer campo con ejemplo visual

        scrollPaneForm = new JScrollPane(edgesContainer);
        scrollPaneForm.setBorder(null);
        scrollPaneForm.setOpaque(false);
        scrollPaneForm.getViewport().setOpaque(false);
        scrollPaneForm.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPaneForm.getVerticalScrollBar().setUnitIncrement(16);

        formCardPanel.add(scrollPaneForm, BorderLayout.CENTER);

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
                addEdgeField(false);
                // Auto-scroll al final
                SwingUtilities.invokeLater(() -> {
                    JScrollBar vertical = scrollPaneForm.getVerticalScrollBar();
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
            // AQUÍ RECOGERÍAS LOS DATOS DE LOS CAMPOS
            JOptionPane.showMessageDialog(this, "Procesando aristas y generando grafo...");
        });

        footerPanel.add(btnVerGrafo);
        formCardPanel.add(footerPanel, BorderLayout.SOUTH);

        // Agregar panel central al layout principal
        mainPanel.add(formCardPanel, BorderLayout.CENTER);
    }


    /**
     * MÉTODO SIMULADO: Carga una lista de nodos de ejemplo en el panel izquierdo.
     * IMPORTANTE: Debes reemplazar esto con tu lógica real que obtiene los nodos creados en la ventana anterior.
     */
    private void cargarResumenNodosDummy(JPanel container) {
        int i = 0;
        for (String g : listadoVertices) {
            JLabel itemLabel = new JLabel("•  " + (++i) + " ---->" + g);
            itemLabel.setFont(FONT_RESUMEN_ITEM);
            itemLabel.setForeground(COLOR_TEXTO_TITULO);
            itemLabel.setBorder(new EmptyBorder(5, 0, 5, 0)); // Espacio entre items
            itemLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            container.add(itemLabel);
        }
    }


    // Método para agregar fila (con opción de mostrar ejemplo en el primero)
    private void addEdgeField(boolean isFirstExample) {
        edgeCount++;

        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        row.setBorder(new EmptyBorder(0, 0, 15, 0));

        // Número pequeño arriba a la izquierda
        JLabel lblNum = new JLabel(edgeCount + ".");
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

        // Placeholders (Si es el primero, muestra ejemplo, si no, genérico)
        String phInicio = isFirstExample ? "Origen (Ej: 0)" : "Origen ID";
        String phDestino = isFirstExample ? "Destino (Ej: 2)" : "Destino ID";
        String phPeso = isFirstExample ? "Peso (Ej: 15)" : "Peso";

        // Campo Inicio
        RoundedNumberField txtInicio = new RoundedNumberField(phInicio);
        txtInicio.setMaximumSize(new Dimension(200, 45)); // Usar espacio disponible

        // Campo Destino
        RoundedNumberField txtDestino = new RoundedNumberField(phDestino);
        txtDestino.setMaximumSize(new Dimension(200, 45));

        // Campo Peso
        RoundedNumberField txtPeso = new RoundedNumberField(phPeso);
        txtPeso.setMaximumSize(new Dimension(200, 45)); // Peso un poco más pequeño
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
    // COMPONENTES PERSONALIZADOS (Sin cambios respecto al anterior)
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

    /** 2. Campo Numérico Redondeado (Con filtro para solo enteros) */
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
            ((AbstractDocument) getDocument()).setDocumentFilter(new IntDocumentFilter(this));
        }

        public boolean isShowingPlaceholder() { return showingPlaceholder; }

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

    // Filtro para solo dígitos
    static class IntDocumentFilter extends DocumentFilter {
        private static final Pattern DIGITS = Pattern.compile("\\d*");
        private final RoundedNumberField owner;
        public IntDocumentFilter(RoundedNumberField owner) { this.owner = owner; }
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (allowChange(fb.getDocument().getText(0, fb.getDocument().getLength()), offset, string)) super.insertString(fb, offset, string, attr);
        }
        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (allowChange(fb.getDocument().getText(0, fb.getDocument().getLength()), offset, length, text)) super.replace(fb, offset, length, text, attrs);
        }
        private boolean allowChange(String current, int offset, String text) { return allowChange(current, offset, 0, text); }
        private boolean allowChange(String current, int offset, int length, String text) {
            if (owner.isShowingPlaceholder()) return true;
            String before = current.substring(0, offset);
            String after = current.substring(Math.min(current.length(), offset + length));
            String resultado = before + (text == null ? "" : text) + after;
            return DIGITS.matcher(resultado).matches();
        }
    }

    /** 3. Botón Circular "+" */
    static class CirclePlusButton extends JComponent {
        private boolean isHovered = false;
        public CirclePlusButton() {
            setPreferredSize(new Dimension(40, 40));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
            });
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(isHovered ? COLOR_PLUS_BG.darker() : COLOR_PLUS_BG);
            g2.fillOval(0, 0, getWidth(), getHeight());
            g2.setColor(COLOR_PLUS_ICON);
            g2.setStroke(new BasicStroke(2.5f));
            int cx = getWidth() / 2, cy = getHeight() / 2, size = 7;
            g2.drawLine(cx - size, cy, cx + size, cy);
            g2.drawLine(cx, cy - size, cx, cy + size);
            g2.dispose();
        }
    }

    /** 4. Botón Grande "Ver Grafo" */
    static class GraphButton extends JButton {
        private boolean isHovered = false;
        public GraphButton(String text) {
            super(text);
            setFont(FONT_BOTON);
            setForeground(Color.WHITE);
            setFocusPainted(false); setBorderPainted(false); setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
            });
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(isHovered ? COLOR_PRIMARY_HOVER : COLOR_PRIMARY);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            FontMetrics fm = g2.getFontMetrics();
            int iconSize = 20, gap = 10;
            int startX = (getWidth() - (iconSize + gap + fm.stringWidth(getText()))) / 2;
            int centerY = getHeight() / 2;
            g2.setColor(Color.WHITE); g2.setStroke(new BasicStroke(2f));
            int nodeR = 3;
            g2.fillOval(startX + 10 - nodeR, centerY - 8 - nodeR, nodeR*2, nodeR*2);
            g2.fillOval(startX - nodeR, centerY + 6 - nodeR, nodeR*2, nodeR*2);
            g2.fillOval(startX + 20 - nodeR, centerY + 6 - nodeR, nodeR*2, nodeR*2);
            g2.drawLine(startX + 10, centerY - 8, startX, centerY + 6);
            g2.drawLine(startX + 10, centerY - 8, startX + 20, centerY + 6);
            g2.drawLine(startX, centerY + 6, startX + 20, centerY + 6);
            g2.drawString(getText(), startX + iconSize + gap, centerY + (fm.getAscent()/2) - 2);
            g2.dispose();
        }
    }

}