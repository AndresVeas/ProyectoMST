import javax.swing.SwingUtilities;

import view.BienvenidaApp;

public class Launcher {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BienvenidaApp b = new BienvenidaApp();
            b.setLocationRelativeTo(null);
            b.setVisible(true);
        });
    }
}
