package main;
import javax.swing.*;

public class App {
    public static void main(String[] args) {
        ScoreDAO.crearTabla(); // Crear tabla al iniciar
        new PantallaInicio();  // Abrir ventana inicial
        SwingUtilities.invokeLater(() -> {
            PantallaInicio pantalla = new PantallaInicio();
            pantalla.setVisible(true);
        });      
    }
}