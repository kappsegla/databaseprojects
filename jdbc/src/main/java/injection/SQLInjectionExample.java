package injection;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLInjectionExample {

    private static final Logger logger = Logger.getLogger(SQLInjectionExample.class.getName());

    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/test";//?allowMultiQueries=true";
        String user = "root";
        String password = System.getenv("password");
        String countryName = "Sweden' OR '1'='1";

        try (
                Connection conn = DriverManager.getConnection(url, user, password);
                Statement stmt = conn.createStatement()
        ) {
            String query = "SELECT * FROM country WHERE country_name = '" + countryName + "'";
            logger.info("Executing query: " + query);

            try (ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    String name = rs.getString("country_name");
                    String code = rs.getString("language_code");
                    System.out.println("Result: " + name + " " + code);
                }
            }

            // Demonstrating destructive injection (for demo only)
            countryName = "Sweden'; DROP TABLE country; -- ";
            query = "SELECT * FROM country WHERE country_name = '" + countryName + "'";
            logger.warning("Executing potentially destructive query: " + query);
            stmt.execute(query);

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error occurred", e);
        }
    }
}
