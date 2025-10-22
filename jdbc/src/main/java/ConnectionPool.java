import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionPool {

    private static final Logger logger = Logger.getLogger(ConnectionPool.class.getName());

    public static void main(String[] args) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/test");
        config.setUsername("root");
        config.setPassword(System.getenv("PASSWORD"));
        config.setMaximumPoolSize(10); // Optional: tune for demo load
        config.setPoolName("DemoPool");

        String query = "SELECT country_name, language_code FROM country WHERE country_name = ?";
        String countryName = "Sweden";

        try (HikariDataSource dataSource = new HikariDataSource(config);
             Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, countryName);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("country_name");
                    String code = rs.getString("language_code");
                    System.out.println("Result: " + name + " (" + code + ")");
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database operation failed", e);
        }
    }
}

