package org.example;

import java.sql.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() throws SQLException {
        String connUrl = "jdbc:mysql://localhost:3306/sakila";

        try (Connection connection = DriverManager.getConnection(
                connUrl,
                System.getenv("DATABASE_USER"),
                System.getenv("DATABASE_PASSWORD"))) {

            String query = "select * from actor where actor_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, 10);
                ResultSet result = statement.executeQuery();
                while (result.next()) {
                    System.out.println(result.getString(2) +
                            " " +
                            result.getString(3));
                }
                result.close();
            }

            String insert = "insert into actor(first_name,last_name) value(?,?)";
            try(PreparedStatement stmt = connection.prepareStatement(insert)){
                stmt.setString(1,"Bob");
                stmt.setString(2,"#");

                int rows = stmt.executeUpdate();
                System.out.println( rows + " rows inserted.");
            }



        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
