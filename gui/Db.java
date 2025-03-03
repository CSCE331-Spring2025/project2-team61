import java.sql.*;

/**
 * This class handles database connectivity and queries using PostgreSQL.
 * It establishes a connection to a specified database and allows querying.
 * 
 * @author Luke Conran
 * @author Kamryn Vogel
 * @author Christian Fadal
 * @author Macsen Casaus
 * @author Surada Suwansathit
 */
public class Db {
    private String databaseName;
    private String databaseUser;
    private String databasePassword;
    private Connection conn;

    private static final String databaseURLFormat = "jdbc:postgresql://csce-315-db.engr.tamu.edu/%s";

    /**
     * Default constructor for the database connection.
     * Initializes connection with predefined database credentials.
     */
    public Db() {
        databaseName = "team_61_db";
        databaseUser = "team_61";
        databasePassword = "6161";

        connect();
    }

    /**
     * Parameterized constructor for establishing a database connection.
     * 
     * @param databaseName     The name of the database.
     * @param databaseUser     The username for database authentication.
     * @param databasePassword The password for database authentication.
     */
    public Db(String databaseName, String databaseUser, String databasePassword) {
        this.databaseName = databaseName;
        this.databaseUser = databaseUser;
        this.databasePassword = databasePassword;

        connect();
    }

    /**
     * Executes a SQL query on the connected database.
     * 
     * @param fmt  The SQL query format string.
     * @param args The arguments to format into the SQL query.
     * @return A {@code ResultSet} containing the results of the query, or {@code null} if an error occurs.
     */
    public ResultSet query(String fmt, Object... args) {
        String sqlStatement = String.format(fmt, args);
        try {
            Statement stmt = conn.createStatement();
            return stmt.executeQuery(sqlStatement);
        } catch (Exception e) {
            System.out.println(e);
            System.out.printf("Error executing query:\n%s\n", sqlStatement);
            return null;
        }
    }

    /**
     * Establishes a connection to the PostgreSQL database.
     * Uses the PostgreSQL JDBC driver to connect using the provided credentials.
     */
    private void connect() {
        String databaseURL = String.format(databaseURLFormat, databaseName);

        try {
            Class.forName("org.postgresql.Driver"); // Load the PostgreSQL JDBC driver
            conn = DriverManager.getConnection(databaseURL, databaseUser, databasePassword);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(1);
        }
        // System.out.println("DB Connection Established Successfully!");
    }
}
.
