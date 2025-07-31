import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PantallaInicio {
    public void Inicio(String[] args) {
        // Configuración inicial
        JFrame ventana = new JFrame("JUEGO GRUPO 4");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setSize(800, 600);
        ventana.setLayout(new BorderLayout());
        ventana.getContentPane().setBackground(Color.BLACK);

        // Panel de inicio
        JPanel panelInicio = new JPanel();
        panelInicio.setLayout(new BoxLayout(panelInicio, BoxLayout.Y_AXIS));
        panelInicio.setBackground(Color.BLACK);

        JLabel titulo = new JLabel("JUEGO GRUPO 4", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 30));
        titulo.setForeground(Color.GREEN);

        JButton iniciarBtn = new JButton("INICIAR");
        JButton salirBtn = new JButton("SALIR");

        // Acción del botón INICIAR
        iniciarBtn.addActionListener(e -> {
            String nombre = JOptionPane.showInputDialog(ventana, "Ingresa tu nombre:", "Bienvenido/a", JOptionPane.PLAIN_MESSAGE);
            if (nombre != null && !nombre.trim().isEmpty()) {
                // Selección de escenario (DÍA/NOCHE)
                String[] opciones = {"DÍA", "NOCHE"};
                int eleccion = JOptionPane.showOptionDialog(
                    ventana, 
                    "Selecciona el escenario:", 
                    "Configuración", 
                    JOptionPane.DEFAULT_OPTION, 
                    JOptionPane.QUESTION_MESSAGE, 
                    null, 
                    opciones, 
                    opciones[0]
                );

                SeleccionEscenario escenario = new SeleccionEscenario();
                escenario.setStageSeleccionado(eleccion == 0 ? SeleccionEscenario.Stage.DIA : SeleccionEscenario.Stage.NOCHE);

                // Iniciar el juego ChromeDinosaur con el escenario seleccionado
                ventana.dispose(); // Cierra la pantalla de inicio
                iniciarJuego(escenario.getStageSeleccionado());
            }
        });

        // Acción del botón SALIR
        salirBtn.addActionListener(e -> System.exit(0));

        // Añadir componentes al panel
        panelInicio.add(Box.createVerticalGlue());
        panelInicio.add(titulo);
        panelInicio.add(Box.createVerticalStrut(30));
        panelInicio.add(iniciarBtn);
        panelInicio.add(Box.createVerticalStrut(10));
        panelInicio.add(salirBtn);
        panelInicio.add(Box.createVerticalGlue());

        ventana.add(panelInicio, BorderLayout.CENTER);
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }

    private static void iniciarJuego(SeleccionEscenario.Stage escenario) {
        JFrame gameFrame = new JFrame("Chrome Dinosaur - Grupo 4");
        CaminoUniversidad game = new CaminoUniversidad(escenario);
        
        // Configura el fondo según el escenario
        if (escenario == SeleccionEscenario.Stage.NOCHE) {
            game.setBackground(new Color(20, 20, 40)); // Fondo oscuro para NOCHE
        } else {
            game.setBackground(Color.lightGray); // Fondo claro para DÍA
        }

        gameFrame.add(game);
        gameFrame.pack();
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setVisible(true);
    }
}