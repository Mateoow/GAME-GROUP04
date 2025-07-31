import javax.swing.*;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PantallaInicio pantalla = new PantallaInicio();
            pantalla.setVisible(true);
        });
    }
}