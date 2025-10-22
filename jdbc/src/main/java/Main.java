import io.github.cdimascio.dotenv.Dotenv;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        String url = dotenv.get("CONNECTION_STRING");

        if (url == null || url.isBlank()) {
            logger.severe("CONNECTION_STRING not found in environment variables.");
            return;
        }

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter search term: ");
            String name = scanner.nextLine().trim();

            if (name.isEmpty()) {
                logger.warning("Invalid input: search term is empty.");
                return;
            }

            List<Country> countries = fetchCountriesByName(url, name);
            if (countries.isEmpty()) {
                logger.info("No countries found matching: " + name);
            } else {
                countries.forEach(System.out::println);
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error occurred", e);
        }
    }

    private static List<Country> fetchCountriesByName(String url, String name) {
        String query = "SELECT country_id, country_name, language_code FROM country WHERE country_name = ?";
        List<Country> results = new ArrayList<>();

        try (
                Connection connection = DriverManager.getConnection(url);
                PreparedStatement preparedStatement = connection.prepareStatement(query)
        ) {
            preparedStatement.setString(1, name);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("country_id");
                    String countryName = resultSet.getString("country_name");
                    String languageCode = resultSet.getString("language_code");
                    results.add(new Country(id, countryName, languageCode));
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database query failed", e);
        }

        return results;
    }

    public record Country(int countryId, String countryName, String languageCode) {
        @Override
        public String toString() {
            return countryName + " (" + languageCode + ")";
        }
    }
}
