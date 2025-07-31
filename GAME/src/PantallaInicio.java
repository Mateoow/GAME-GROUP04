package main;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PantallaInicio extends JFrame {
    
    public PantallaInicio() {
        setTitle("JUEGO GRUPO 4");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.BLACK);
        panel.setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 0));
        
        JLabel titulo = new JLabel("JUEGO GRUPO 4", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 48));
        titulo.setForeground(Color.GREEN);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JButton iniciarBtn = new JButton("INICIAR JUEGO");
        iniciarBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        iniciarBtn.setFont(new Font("Arial", Font.PLAIN, 24));
        iniciarBtn.setBackground(Color.DARK_GRAY);
        iniciarBtn.setForeground(Color.WHITE);
        
        JButton salirBtn = new JButton("SALIR");
        salirBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        salirBtn.setFont(new Font("Arial", Font.PLAIN, 24));
        salirBtn.setBackground(Color.DARK_GRAY);
        salirBtn.setForeground(Color.WHITE);
        
        iniciarBtn.addActionListener(e -> iniciarJuego());
        salirBtn.addActionListener(e -> System.exit(0));
        
        panel.add(Box.createVerticalGlue());
        panel.add(titulo);
        panel.add(Box.createVerticalStrut(50));
        panel.add(iniciarBtn);
        panel.add(Box.createVerticalStrut(20));
        panel.add(salirBtn);
        panel.add(Box.createVerticalGlue());
        
        add(panel);
    }
    
    private void iniciarJuego() {
        String[] opciones = {"DÍA", "NOCHE"};
        int eleccion = JOptionPane.showOptionDialog(
            this, 
            "Selecciona el escenario:", 
            "Configuración", 
            JOptionPane.DEFAULT_OPTION, 
            JOptionPane.QUESTION_MESSAGE, 
            null, 
            opciones, 
            opciones[0]
        );
        
        if (eleccion == JOptionPane.CLOSED_OPTION) {
            return; // El usuario cerró el diálogo
        }
        
        SeleccionEscenario.Stage escenario = (eleccion == 0) ? 
            SeleccionEscenario.Stage.DIA : SeleccionEscenario.Stage.NOCHE;
        
        // Cerrar menú actual
        this.dispose();
        
        // Iniciar juego
        SwingUtilities.invokeLater(() -> {
            JFrame gameFrame = new JFrame("Camino a la Universidad");
            gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            gameFrame.add(new CaminoUniversidad(escenario));
            gameFrame.pack();
            gameFrame.setLocationRelativeTo(null);
            gameFrame.setVisible(true);
        });
    }
}