package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class BienvenidaApp extends JFrame {

    // Paleta de Colores extraída de tu imagen
    private static final Color COLOR_FONDO_APP = new Color(224, 231, 255); // Lavanda suave
    private static final Color COLOR_CARD_BG = Color.WHITE;
    private static final Color COLOR_TEXTO_TITULO = new Color(31, 41, 55); // Gris oscuro
    private static final Color COLOR_FONDO_SUBTITULO = new Color(219, 234, 254); // Azul muy pálido
    private static final Color COLOR_TEXTO_SUBTITULO = new Color(30, 58, 138); // Azul oscuro
    private static final Color COLOR_TEXTO_DESC = new Color(75, 85, 99);   // Gris medio
    private static final Color COLOR_BOTON = new Color(59, 130, 246);      // Azul Royal (El de la imagen)
    private static final Color COLOR_BOTON_HOVER = new Color(37, 99, 235); // Azul un poco más oscuro para hover

    // Fuentes modernas
    private static final Font FONT_TITULO = new Font("SansSerif", Font.BOLD, 28);
    private static final Font FONT_SUBTITULO = new Font("SansSerif", Font.BOLD, 16);
    private static final Font FONT_DESC = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font FONT_BOTON = new Font("SansSerif", Font.BOLD, 14);

    public BienvenidaApp() {
        setTitle("Planificador de Redes");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        setLocationRelativeTo(null); // Centrar en pantalla
        
        // Panel principal (Fondo general)
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(COLOR_FONDO_APP);
        add(mainPanel);

        // --- TARJETA BLANCA CENTRAL ---
        RoundedPanel cardPanel = new RoundedPanel(30, COLOR_CARD_BG);
        cardPanel.setLayout(new GridBagLayout());
        cardPanel.setBorder(new EmptyBorder(40, 40, 40, 40)); // Padding interno de la tarjeta
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(0, 0, 20, 0); // Espacio vertical entre elementos
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        // 1. Título "Bienvenido!!"
        JLabel titleLabel = new JLabel("Bienvenido!!", SwingConstants.CENTER);
        titleLabel.setFont(FONT_TITULO);
        titleLabel.setForeground(COLOR_TEXTO_TITULO);
        cardPanel.add(titleLabel, gbc);

        // 2. Subtítulo con fondo (Caja azul claro)
        // Usamos un panel redondeado pequeño para el fondo del subtítulo
        RoundedPanel subtitlePanel = new RoundedPanel(15, COLOR_FONDO_SUBTITULO);
        subtitlePanel.setLayout(new BorderLayout());
        subtitlePanel.setBorder(new EmptyBorder(10, 20, 10, 20)); // Padding dentro de la caja azul
        
        JLabel subtitleText = new JLabel("<html><center>Planificador de redes de<br>comunicación</center></html>", SwingConstants.CENTER);
        subtitleText.setFont(FONT_SUBTITULO);
        subtitleText.setForeground(COLOR_TEXTO_SUBTITULO);
        
        subtitlePanel.add(subtitleText, BorderLayout.CENTER);
        
        // Ajustar insets para el subtítulo
        GridBagConstraints gbcSub = (GridBagConstraints) gbc.clone();
        gbcSub.insets = new Insets(10, 10, 25, 10); // 10 px de margen lateral dentro de la tarjeta
        gbcSub.fill = GridBagConstraints.HORIZONTAL;
        gbcSub.weightx = 1.0; // permitir que el subtítulo se expanda al ancho disponible
        cardPanel.add(subtitlePanel, gbcSub);

        // 3. Descripción (Texto centrado y ajustado)
        JTextPane descText = new JTextPane();
        descText.setText("Este programa aplica algoritmos de Árbol de Expansión Mínima para encontrar una red óptima con el camino más corto.\n\nCada nodo representa un centro de comunicación y cada enlace un posible costo entre ellos.");
        descText.setFont(FONT_DESC);
        descText.setForeground(COLOR_TEXTO_DESC);
        descText.setEditable(false);
        descText.setOpaque(false); // Fondo transparente
        descText.setFocusable(false);
        
        // Centrar texto dentro del JTextPane
        StyledDocument doc = descText.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);

        GridBagConstraints gbcDesc = (GridBagConstraints) gbc.clone();
        gbcDesc.insets = new Insets(0, 10, 30, 10); // Márgenes laterales para que el texto no toque los bordes
        cardPanel.add(descText, gbcDesc);

        // 4. Botón "Empezar"
        ModernButton startButton = new ModernButton("Empezar");
        startButton.addActionListener(e -> {
                    GraphInterface g = new GraphInterface();
                    g.setLocationRelativeTo(null);
                    g.setVisible(true);
                    this.setVisible(false);
        });

        
        
        GridBagConstraints gbcBtn = (GridBagConstraints) gbc.clone();
        gbcBtn.fill = GridBagConstraints.NONE; // No estirar el botón a todo el ancho
        gbcBtn.ipadx = 100; // Hacer el botón más ancho horizontalmente
        gbcBtn.ipady = 10;  // Un poco más alto
        gbcBtn.insets = new Insets(10, 0, 0, 0);
        cardPanel.add(startButton, gbcBtn);

        // Forzar que la tarjeta sea cuadrada y responsiva al tamaño de la ventana
        int initialSize = 420; // tamaño inicial de la tarjeta (cuadrado)
        cardPanel.setPreferredSize(new Dimension(initialSize, initialSize));
        cardPanel.setMinimumSize(new Dimension(300, 300));
        cardPanel.setMaximumSize(new Dimension(800, 800));

        // Mantener cuadrado al redimensionar la ventana
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                int s = Math.min(getWidth(), getHeight()) - 120; // dejar margen
                s = Math.max(300, Math.min(800, s)); // límites
                cardPanel.setPreferredSize(new Dimension(s, s));
                cardPanel.revalidate();
            }
        });

        // Añadir la tarjeta al panel principal (centrada)
        GridBagConstraints gbcMain = new GridBagConstraints();
        gbcMain.gridx = 0;
        gbcMain.gridy = 0;
        gbcMain.anchor = GridBagConstraints.CENTER;
        mainPanel.add(cardPanel, gbcMain);
    }

    // --- CLASES PERSONALIZADAS PARA ESTILO VISUAL ---

    /**
     * Panel con esquinas redondeadas y antialiasing.
     */
    static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color backgroundColor;

        public RoundedPanel(int radius, Color bgColor) {
            this.radius = radius;
            this.backgroundColor = bgColor;
            setOpaque(false); // Importante para que se vean las esquinas transparentes
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            // Activar suavizado de bordes (Antialiasing)
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2.setColor(backgroundColor);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), radius, radius));
            
            g2.dispose();
            super.paintComponent(g); // Pintar hijos
        }
    }

    /**
     * Botón moderno estilo "Pill" (Pastilla) con efecto hover.
     */
    static class ModernButton extends JButton {
        private boolean isHovered = false;

        public ModernButton(String text) {
            super(text);
            setFont(FONT_BOTON);
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false); // Pintaremos nosotros el fondo
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            // Efecto Hover
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Cambiar color si el mouse está encima
            if (isHovered) {
                g2.setColor(COLOR_BOTON_HOVER);
            } else {
                g2.setColor(COLOR_BOTON);
            }

            // Dibujar fondo redondeado (radio alto para efecto "pill")
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 15, 15));

            // Dibujar texto centrado
            FontMetrics fm = g2.getFontMetrics();
            Rectangle stringBounds = fm.getStringBounds(this.getText(), g2).getBounds();
            int textX = (getWidth() - stringBounds.width) / 2;
            int textY = (getHeight() - stringBounds.height) / 2 + fm.getAscent();

            g2.setColor(getForeground());
            g2.setFont(getFont());
            g2.drawString(getText(), textX, textY);

            g2.dispose();
        }
    }

}