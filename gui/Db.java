import java.sql.*;

public class Db {
    String databaseName;
    String databaseUser;
    String databasePassword;

    Connection conn;

    static final String databaseURLFormat = "jdbc:postgresql://csce-315-db.engr.tamu.edu/%s";

    public Db() {
        databaseName = "team_61_db";
        databaseUser = "team_61";
        databasePassword = "6161";

        connect();
    }

    public Db(String databaseName, String databaseUser, String databasePassword) {
        this.databaseName = databaseName;
        this.databaseUser = databaseUser;
        this.databasePassword = databasePassword;

        connect();
    }

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

    private void connect() {
        String databaseURL = String.format(databaseURLFormat, databaseName);

        try {
            Class.forName("org.postgresql.Driver"); // Load the PostgreSQL JDBC driver
            conn = DriverManager.getConnection(databaseURL, databaseUser, databasePassword);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(1);
        }
        System.out.println("DB Connection Established Successfully!");
    }
}
