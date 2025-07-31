import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PantallaInicio {

    public void Inicio (String[] args) {
        // Crear la ventana principal
        JFrame ventana = new JFrame("JUEGO GRUPO 4");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setSize(1024, 768);
        ventana.setLayout(new BorderLayout());
        ventana.getContentPane().setBackground(Color.BLACK);

        // Panel central para los componentes
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.BLACK);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Título del juego
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

        // Acción del botón "INICIAR" (abre un JOptionPane para ingresar nombre)
        iniciarBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nombre = JOptionPane.showInputDialog(
                    ventana,
                    "Ingresa tu nombre:",
                    "Bienvenido/a",
                    JOptionPane.PLAIN_MESSAGE
                );
                if (nombre != null && !nombre.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(
                        ventana,
                        "¡Hola, " + nombre + "! El juego comenzará pronto.",
                        "Mensaje",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    // Aquí iría la lógica para iniciar el juego
                }
            }
        });

        // Acción del botón "SALIR" (cierra el juego)
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

        // Añadir panel a la ventana
        ventana.add(panel, BorderLayout.CENTER);
        ventana.setLocationRelativeTo(null); // Centrar ventana
        ventana.setVisible(true);
    }
}