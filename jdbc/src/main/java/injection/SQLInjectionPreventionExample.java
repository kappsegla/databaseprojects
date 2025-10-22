package injection;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
Prepared statements prevent SQL injection by separating the SQL code from the data. When you use a prepared statement, the SQL query is precompiled, and the data is sent separately. This means that the database treats the data as literal values, not executable code. Here's a breakdown of how it works:

Precompilation: The SQL query is precompiled by the database. This means the structure of the query is fixed, and the database knows what to expect.

Parameter Binding: The data is sent separately and bound to the precompiled query. The database treats this data as literal values, not part of the SQL code.

No Code Execution: Since the data is treated as literal values, any malicious input is not executed as SQL code. This prevents attackers from injecting harmful SQL commands.
 */

public class SQLInjectionPreventionExample {

    private static final Logger logger = Logger.getLogger(SQLInjectionPreventionExample.class.getName());

    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/test";
        String user = "root";
        String password = System.getenv("password");
        String countryName = "Sweden"; // Try: "Sweden' OR '1'='1" to test injection resistance

        String query = "SELECT country_name, language_code FROM country WHERE country_name = ?";

        try (
                Connection conn = DriverManager.getConnection(url, user, password);
                PreparedStatement pstmt = conn.prepareStatement(query)
        ) {
            pstmt.setString(1, countryName);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("country_name");
                    String code = rs.getString("language_code");
                    System.out.println(name + " " + code);
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error occurred", e);
        }
    }
}
