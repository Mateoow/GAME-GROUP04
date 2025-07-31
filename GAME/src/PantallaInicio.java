import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PantallaInicio {

    public void Inicio(String[] args) {
        // Crear la ventana principal
        JFrame ventana = new JFrame("JUEGO GRUPO 4");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setSize(1024, 768);
        ventana.setLayout(new BorderLayout());
        ventana.getContentPane().setBackground(Color.BLACK);

        // Panel central
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.BLACK);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Título
        JLabel titulo = new JLabel("JUEGO GRUPO 4", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 30));
        titulo.setForeground(Color.GREEN);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Botón "INICIAR"
        JButton iniciarBtn = new JButton("INICIAR");
        iniciarBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        iniciarBtn.setFont(new Font("Arial", Font.PLAIN, 18));
        iniciarBtn.setBackground(Color.DARK_GRAY);
        iniciarBtn.setForeground(Color.WHITE);
        iniciarBtn.setFocusPainted(false);

        // Botón "SALIR"
        JButton salirBtn = new JButton("SALIR");
        salirBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        salirBtn.setFont(new Font("Arial", Font.PLAIN, 18));
        salirBtn.setBackground(Color.DARK_GRAY);
        salirBtn.setForeground(Color.WHITE);
        salirBtn.setFocusPainted(false);

        // Acción del botón "INICIAR"
        iniciarBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Paso 1: Pedir el nombre
                String nombre = JOptionPane.showInputDialog(
                    ventana,
                    "Ingresa tu nombre:",
                    "Bienvenido/a",
                    JOptionPane.PLAIN_MESSAGE
                );

                if (nombre != null && !nombre.trim().isEmpty()) {
                    // Paso 2: Seleccionar escenario (DÍA/NOCHE)
                    String[] opciones = {"DÍA", "NOCHE"};
                    int eleccion = JOptionPane.showOptionDialog(
                        ventana,
                        "Selecciona el escenario:",
                        "Configuración del Juego",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        opciones,
                        opciones[0]
                    );

                    // Paso 3: Guardar la selección usando tu clase SeleccionEscenario
                    SeleccionEscenario seleccion = new SeleccionEscenario();
                    if (eleccion == 0) {
                        seleccion.setStageSeleccionado(SeleccionEscenario.Stage.DIA);
                    } else {
                        seleccion.setStageSeleccionado(SeleccionEscenario.Stage.NOCHE);
                    }

                    // Paso 4: Mensaje de confirmación (opcional)
                    JOptionPane.showMessageDialog(
                        ventana,
                        "¡Listo, " + nombre + "! Jugando en modo: " + seleccion.getStageSeleccionado(),
                        "Iniciando Juego",
                        JOptionPane.INFORMATION_MESSAGE
                    );

                    // Aquí puedes iniciar el juego con los parámetros:
                    // - nombre: String
                    // - escenario: SeleccionEscenario.Stage
                    // Ejemplo: iniciarJuego(nombre, seleccion.getStageSeleccionado());
                }
            }
        });

        // Acción del botón "SALIR"
        salirBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirmacion = JOptionPane.showConfirmDialog(
                    ventana,
                    "¿Estás seguro de que quieres salir?",
                    "Salir",
                    JOptionPane.YES_NO_OPTION
                );
                if (confirmacion == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });

        // Añadir componentes al panel
        panel.add(Box.createVerticalGlue());
        panel.add(titulo);
        panel.add(Box.createVerticalStrut(30));
        panel.add(iniciarBtn);
        panel.add(Box.createVerticalStrut(10));
        panel.add(salirBtn);
        panel.add(Box.createVerticalGlue());

        // Mostrar ventana
        ventana.add(panel, BorderLayout.CENTER);
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }
}