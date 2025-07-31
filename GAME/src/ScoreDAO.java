import java.sql.*;

public class ScoreDAO {
    private static final String URL = "jdbc:sqlite:mi_juego.db";

    public static void crearTabla() {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {

            String sql = "CREATE TABLE IF NOT EXISTS scores (" +
                         "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                         "puntaje INTEGER NOT NULL)";
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void guardarScore(int puntaje) {
        String sql = "INSERT INTO scores(puntaje) VALUES (?)";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, puntaje);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

