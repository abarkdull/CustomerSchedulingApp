package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class used for establishing and managing mySQL database connectivity.
 */
public class DBConnection {

    /**
     * Database connection protocol.
     */
    private static final String protocol = "jdbc";

    /**
     * Database vendor name.
     */
    private static final String vendorName = ":mysql:";

    /**
     * Database ip address.
     */
    private static final String ipAddress = "//wgudb.ucertify.com/";

    /**
     * Database name.
     */
    private static final String dbName = "WJ08oNc";

    /**
     * Database connection URL.
     */
    private static final String jdbcURL = protocol + vendorName + ipAddress + dbName;

    /**
     * JDBC driver.
     */
    private static final String MYSQLJDBCDrive = "com.mysql.jdbc.Driver";

    /**
     * Connection reference variable.
     */
    private static Connection connection = null;

    /**
     * Databse connection username.
     */
    private static final String username = "U08oNc";

    /**
     * Database connection password.
     */
    private static final String password = "53689350415";

    /**
     * Initializes connection to SQL database.
     * @return reference to connection
     */
    public static Connection startConnection() {
        try {
            Class.forName(MYSQLJDBCDrive);
            connection = DriverManager.getConnection(jdbcURL, username, password);
            System.out.println("Connection successful");
        }
        catch (ClassNotFoundException | SQLException e) {
            System.out.println(e.getMessage());
        }

        return connection;
    }

    /**
     * Returns connection reference.
     */
    public static Connection getConnection() {
        return connection;
    }

    /**
     * Closes out of database connection.
     */
    public static void closeConnection() {
        try {
            connection.close();
        }
        catch (Exception e) {

        }
    }

}
