import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class VentanaPuntajes extends JFrame {
    public VentanaPuntajes() {
        setTitle("Puntajes Guardados");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        String[] columnas = {"ID", "Puntaje"};
        DefaultTableModel model = new DefaultTableModel(columnas, 0);
        JTable tabla = new JTable(model);

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:mi_juego.db");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM scores ORDER BY puntaje DESC")) {

            while (rs.next()) {
                int id = rs.getInt("id");
                int puntaje = rs.getInt("puntaje");
                model.addRow(new Object[]{id, puntaje});
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        JScrollPane scrollPane = new JScrollPane(tabla);
        add(scrollPane, BorderLayout.CENTER);
    }
}

