import io.github.cdimascio.dotenv.Dotenv;
import java.sql.*;
import java.time.LocalDateTime;
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

        // --- 1. Demonstrera ursprunglig SELECT (Hämta länder) ---
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter search term (e.g., Sweden): ");
            String name = scanner.nextLine().trim();

            if (name.isEmpty()) {
                logger.warning("Invalid input: search term is empty. Skipping SELECT demo.");
            } else {
                List<Country> countries = fetchCountriesByName(url, name);
                if (countries.isEmpty()) {
                    logger.info("No countries found matching: " + name);
                } else {
                    System.out.println("\n--- Countries found matching '" + name + "' (select with timestamp) ---");
                    countries.forEach(System.out::println);
                }
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error occurred during initial search", e);
        }

        // --- 2. Demonstrera COUNT (Aggregerad fråga) ---
        System.out.println("\n--- Demonstrating count (aggregate query) ---");
        long count = fetchCountryCount(url);
        System.out.println("Total number of countries in the table: " + count);

        // --- 3. Demonstrera DML-operationer (insert, update, delete) ---
        System.out.println("\n--- Demonstrating DML Operations (insert, update, delete) ---");
        String newCountryName = "ZzTestCountry";
        String updatedName = "ZzTestCountry (Updated)";

        // insert
        System.out.println("Attempting to insert country: " + newCountryName);
        int insertId = insertCountry(url, newCountryName, "ZZ");
        if (insertId > 0) {
            System.out.println("insert successful. New ID: " + insertId);

            // update
            System.out.println("Attempting to update country ID " + insertId);
            int updatedRows = updateCountry(url, insertId, updatedName);
            System.out.println("update successful. Rows updated: " + updatedRows);

            // delete
            System.out.println("Attempting to delete country ID " + insertId);
            int deletedRows = deleteCountry(url, insertId);
            System.out.println("delete successful. Rows deleted: " + deletedRows);
        } else {
            System.out.println("insert failed, skipping update/delete demo.");
        }

        // --- 4. Demonstrera BATCH UPDATES ---
        System.out.println("\n--- Demonstrating Batch Updates (High-Performance Insert) ---");
        performBatchInsertExample(url, 3);

        // --- 5. Demonstrera TRANSAKTIONER (commit/rollback) ---
        System.out.println("\n--- Demonstrating Transaction Management (commit/rollback) ---");
        performTransactionExample(url);
    }

    /**
     * Hämtar alla länder som matchar ett givet namn, inklusive den nya tidsstämpel-kolumnen.
     */
    private static List<Country> fetchCountriesByName(String url, String name) {
        // SQL har nu den extra kolumnen last_updated
        String query = "select country_id, country_name, language_code, last_updated from country where country_name like ?";
        List<Country> results = new ArrayList<>();

        try (
                Connection connection = DriverManager.getConnection(url);
                PreparedStatement preparedStatement = connection.prepareStatement(query)
        ) {
            preparedStatement.setString(1, "%" + name + "%");

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("country_id");
                    String countryName = resultSet.getString("country_name");
                    String languageCode = resultSet.getString("language_code");

                    // Använder getObject() för att få modern LocalDateTime
                    LocalDateTime lastUpdated = resultSet.getObject("last_updated", LocalDateTime.class);

                    results.add(new Country(id, countryName, languageCode, lastUpdated));
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database select query failed", e);
        }

        return results;
    }

    /**
     * Hämtar antalet rader i 'country'-tabellen (count).
     */
    private static long fetchCountryCount(String url) {
        String query = "select count(*) from country";
        long count = -1;

        try (
                Connection connection = DriverManager.getConnection(url);
                Statement statement = connection.createStatement(); // Statement OK eftersom det inte finns några variabler
                ResultSet resultSet = statement.executeQuery(query)
        ) {
            if (resultSet.next()) {
                count = resultSet.getLong(1);
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database count query failed", e);
        }

        return count;
    }

    /**
     * Lägger till en ny rad i 'country'-tabellen (insert) och sätter tidsstämpel.
     */
    private static int insertCountry(String url, String name, String code) {
        // SQL har nu den extra kolumnen last_updated
        String query = "insert into country (country_name, language_code, last_updated) values (?, ?, ?)";
        int newId = -1;
        LocalDateTime now = LocalDateTime.now(); // Hämta aktuell tid

        try (
                Connection connection = DriverManager.getConnection(url);
                PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)
        ) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, code);
            // Använder setObject() för att skicka modern LocalDateTime
            preparedStatement.setObject(3, now);

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        newId = generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database insert failed", e);
        }
        return newId;
    }

    /**
     * Uppdaterar ett land med ett givet ID (update) och uppdaterar tidsstämpel.
     */
    private static int updateCountry(String url, int id, String newName) {
        // SQL uppdaterar både namn och tidsstämpel
        String query = "update country set country_name = ?, last_updated = ? where country_id = ?";
        int updatedRows = 0;
        LocalDateTime now = LocalDateTime.now();

        try (
                Connection connection = DriverManager.getConnection(url);
                PreparedStatement preparedStatement = connection.prepareStatement(query)
        ) {
            preparedStatement.setString(1, newName);
            preparedStatement.setObject(2, now); // Sätter ny tidsstämpel
            preparedStatement.setInt(3, id);

            updatedRows = preparedStatement.executeUpdate();

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database update failed", e);
        }
        return updatedRows;
    }

    /**
     * Tar bort ett land med ett givet ID (delete).
     */
    private static int deleteCountry(String url, int id) {
        String query = "delete from country where country_id = ?";
        int deletedRows = 0;

        try (
                Connection connection = DriverManager.getConnection(url);
                PreparedStatement preparedStatement = connection.prepareStatement(query)
        ) {
            preparedStatement.setInt(1, id);
            deletedRows = preparedStatement.executeUpdate();

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database delete failed", e);
        }
        return deletedRows;
    }

    /**
     * Demonstrerar transaktionshantering (commit/rollback).
     */
    private static void performTransactionExample(String url) {
        Connection connection = null;
        String tempName = "TxTestCountry";
        String tempCode = "TX";

        try {
            connection = DriverManager.getConnection(url);
            connection.setAutoCommit(false);
            //connection.createStatement().execute("start transaction");
            System.out.println("Transaction started (AutoCommit set to false).");

            String insertQuery = "insert into country (country_name, language_code, last_updated) values (?, ?, ?)";
            try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                insertStmt.setString(1, tempName);
                insertStmt.setString(2, tempCode);
                insertStmt.setObject(3, LocalDateTime.now());
                insertStmt.executeUpdate();
                System.out.println("Step 1: Successfully inserted temporary country: " + tempName);
            }

            // --- Steg 2: Simulera en felaktig uppdatering som skulle orsaka ett fel ---
            String badUpdateQuery = "update non_existent_table set name = ?";
            try (PreparedStatement updateStmt = connection.prepareStatement(badUpdateQuery)) {
                updateStmt.setString(1, "Shouldn't run");
                updateStmt.executeUpdate();

                connection.commit();
                System.out.println("commit executed (shouldn't happen in this example).");
            }

        } catch (SQLException e) {
            logger.warning("Transaction failed due to: " + e.getMessage());
            if (connection != null) {
                try {
                    System.out.println("Executing rollback...");
                    connection.rollback();
                    System.out.println("rollback successful. insert operation was undone.");
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, "Rollback failed", ex);
                }
            }
        } finally {
            if (connection != null) {
                try {
                    //connection.commit();
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, "Error closing connection in transaction demo", ex);
                }
            }

            // Verifiering av rollback (använder en förenklad fetch)
            if (fetchCountriesByName(url, tempName).isEmpty()) {
                System.out.println("Verification: The temporary country (" + tempName + ") was successfully rolled back and is not in the database.");
            } else {
                System.out.println("Verification: Error! The temporary country (" + tempName + ") was NOT rolled back.");
            }
        }
    }

    /**
     * Demonstrerar hur man infogar flera rader effektivt med batch-uppdateringar.
     */
    private static void performBatchInsertExample(String url, int count) {
        // SQL har nu den extra kolumnen last_updated
        String query = "insert into country (country_name, language_code, last_updated) values (?, ?, ?)";

        try (
                Connection connection = DriverManager.getConnection(url);
                PreparedStatement preparedStatement = connection.prepareStatement(query)
        ) {
            // Förbättring: Stäng av autoCommit under batch-körningen för transaktionskontroll
            connection.setAutoCommit(false);

            for (int i = 1; i <= count; i++) {
                String name = "BatchCountry-" + i;
                String code = "BC" + i;

                preparedStatement.setString(1, name);
                preparedStatement.setString(2, code);
                preparedStatement.setObject(3, LocalDateTime.now());

                // Lägg till satsen i batchen istället för att exekvera direkt
                preparedStatement.addBatch();
                System.out.println("Added to batch: " + name);
            }

            // Exekvera alla satser i ett enda anrop till databasen
            int[] updateCounts = preparedStatement.executeBatch();

            // Gör ändringarna permanenta
            connection.commit();

            int totalInserted = 0;
            for (int c : updateCounts) {
                totalInserted += c;
            }

            System.out.printf("Batch successful! Executed %d inserts. Total affected rows: %d.\n", updateCounts.length, totalInserted);

            // Städa upp de infogade testerna
            cleanUpBatchInserts(url);

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Batch insert failed", e);
            // Om det misslyckades, se till att rulla tillbaka (rollback)
            // (Mer robust hantering skulle kräva att man fångar felet och hanterar connection separat)
        } finally {
            // Återställ autoCommit
            try (Connection connection = DriverManager.getConnection(url)) {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error resetting autoCommit", e);
            }
        }
    }

    /**
     * Hjälpmetod för att städa upp de länder som skapades under batch-testet.
     */
    private static void cleanUpBatchInserts(String url) {
        String query = "delete from country where country_name like 'BatchCountry-%'";
        try (
                Connection connection = DriverManager.getConnection(url);
                Statement statement = connection.createStatement()
        ) {
            int deleted = statement.executeUpdate(query);
            System.out.printf("(Cleanup: Deleted %d batch-test entries.)\n", deleted);
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Cleanup failed", e);
        }
    }

    // --- Datastruktur och toString för utskrift ---
    // Record är uppdaterad med LocalDateTime
    public record Country(int countryId, String countryName, String languageCode, LocalDateTime lastUpdated) {
        @Override
        public String toString() {
            // Formatera utskriften för att visa tidsstämpeln
            return String.format("ID: %d | %s (%s) | Last Updated: %s",
                    countryId, countryName, languageCode, lastUpdated.toString());
        }
    }
}