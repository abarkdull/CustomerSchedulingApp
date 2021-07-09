package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Utility class used for querying mySQL database.
 */
public class DBQuery {

    /**
     * Prepared statement reference variable.
     */
    private static PreparedStatement prepStatement;

    /**
     * Initializes a prepared statement object
     * @param c database connection reference
     * @param sql sql query
     */
    public static void setPreparedStatement(Connection c, String sql) throws SQLException {
        prepStatement = c.prepareStatement(sql);
    }

    /**
     * Returns prepared statement object reference.
     */
    public static PreparedStatement getPreparedStatement() {
        return prepStatement;
    }
}
