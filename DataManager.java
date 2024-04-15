import java.sql.*;

public class DataManager {
    private static final String DB_URL = "jdbc:mysql://localhost:8889/my_database";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";

    public boolean login(String username, String password) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Vérifier si l'utilisateur existe dans la base de données
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE username = ?");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Si l'utilisateur existe, vérifier le mot de passe
                String storedPassword = rs.getString("password");
                if (storedPassword.equals(password)) {
                    // Mot de passe correct, connexion réussie
                    return true;
                } else {
                    // Mot de passe incorrect
                    return false;
                }
            } else {
                // L'utilisateur n'existe pas, créer un nouvel utilisateur
                PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)");
                insertStmt.setString(1, username);
                insertStmt.setString(2, password);
                insertStmt.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Gérer l'erreur de connexion à la base de données
            return false;
        }
    }
}
