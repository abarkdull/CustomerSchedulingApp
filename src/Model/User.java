package Model;

import Database.DBConnection;
import Database.DBQuery;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.FileHandler;

/**
 * Class used for creating and managing users both locally and in mySQL database.
 */
public class User {

    /**
     * User id.
     */
    private int userId;

    /**
     * Username.
     */
    private String username;

    /**
     * User's password.
     */
    private String password;

    /**
     * Create date of user.
     */
    private LocalDateTime createDate;

    /**
     * User who created user.
     */
    private String createdBy;

    /**
     * Time of last update of user.
     */
    private Timestamp lastUpdated;

    /**
     * User who last update user.
     */
    private String lastUpdatedBy;

    /**
     * List of all users.
     */
    private static ObservableList<User> allUsers;

    /**
     * Current logged in user.
     */
    private static User currentUser;

    /**
     * Timestamp of succesful logon by user.
     */
    private LocalDateTime lastLogon;

    /**
     * File writer for writing to login_activity.txt.
     */
    private static FileWriter logonReport;

    static {
        try {
            logonReport = new FileWriter("login_activity.txt", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            allUsers = setAllUsers();
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Print writer for writing to logonReport.
     */
    private static PrintWriter loginPW = new PrintWriter(logonReport);

    /**
     * Creates a user object.
     */
    public User(int userId, String username, String password, LocalDateTime createDate, String createdBy, Timestamp lastUpdated, String lastUpdatedBy) throws SQLException, IOException {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdated = lastUpdated;
        this.lastUpdatedBy = lastUpdatedBy;
    }

    /**
     * Returns time of last logon.
     */
    public LocalDateTime getLastLogon() {
        return lastLogon;
    }

    /**
     * Overrides Object toString() method for displaying relevant user info in combo boxes.
     */
    @Override
    public String toString() {
        return this.getUsername();
    }

    /**
     * Returns current user.
     */
    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * Returns user object of user id
     */
    public static User lookupUser(int userId) {
        for (User u : allUsers) {
            if (u.getUserId() == userId) {
                return u;
            }
        }
        return null;
    }

    /**
     * Returns user object of username
     */
    public static User lookupUser(String username) {
        for (User u : allUsers) {
            if (u.getUsername().equals(username)) {
                return u;
            }
        }
        return null;
    }

    /**
     * Returns list of all users.
     */
    public static ObservableList<User> getAllUsers() {
        return allUsers;
    }

    /**
     * Adds all users from database to allUsers list.
     * @return returns list of all users in database
     */
    public static ObservableList<User> setAllUsers() throws SQLException, IOException {
        try {
            allUsers.removeAll();
        } catch (NullPointerException e) {}

        ObservableList<User> res = FXCollections.observableArrayList();

        String sqlQuery = "SELECT * FROM users";
        DBQuery.setPreparedStatement(DBConnection.getConnection(), sqlQuery);
        PreparedStatement ps = DBQuery.getPreparedStatement();
        ps.execute();

        ResultSet results = ps.getResultSet();
        while (results.next()) {
            int userId = results.getInt("User_ID");
            String username = results.getString("User_Name");
            String password = results.getString("Password");

            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime createDate = LocalDateTime.parse(results.getString("Create_Date"), df);

            String createdBy = results.getString("Created_By");
            Timestamp lastUpdate = results.getTimestamp("Last_Update");
            String lastUpdatedBy = results.getString("Last_Updated_By");
            res.add(new User(userId, username, password, createDate, createdBy, lastUpdate, lastUpdatedBy));
        }

        return res;
    }

    /**
     * Returns user id.
     * @return
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Sets new user id.
     * @param userId new user id
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Returns user's username.
     * @return
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets new username for user.
     * @param username new username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns user's password.
     * @return
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets new password for user.
     * @param password new password.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets create date for user.
     * @return
     */
    public LocalDateTime getCreateDate() {
        return createDate;
    }

    /**
     * Sets new create date for user.
     * @param createDate new create date
     */
    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    /**
     * Gets user who created user.
     * @return
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Sets new created by for user.
     * @param createdBy new created by
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Returns last update time for user.
     * @return
     */
    public Timestamp getLastUpdated() {
        return lastUpdated;
    }

    /**
     * Sets new last updated time for user.
     * @param lastUpdated new last updated time
     */
    public void setLastUpdated(Timestamp lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    /**
     * Returns user who last updated user.
     * @return
     */
    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    /**
     * Set new last update by.
     * @param lastUpdatedBy new last updated by
     */
    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    /**
     * Returns true if user is found in allUsers and has a valid password.
     * Successful and unsuccessful logins are written to the login_activity.txt file.
     */
    public static boolean lookupUser(String username, String password) throws SQLException, IOException {

        FileWriter fl = logonReport;
        PrintWriter pw = new PrintWriter(logonReport);

        String selectAllUsers = "SELECT * FROM users";
        DBQuery.setPreparedStatement(DBConnection.getConnection(), selectAllUsers);
        PreparedStatement ps = DBQuery.getPreparedStatement();
        ps.execute();

        ResultSet rs = ps.getResultSet();
        while (rs.next()) {
            if ((rs.getString("User_Name").equals(username)) && (rs.getString("Password").equals(password))) {
                currentUser = lookupUser(rs.getString("User_Name"));
                currentUser.lastLogon = LocalDateTime.now();
                loginPW.println("Successful logon at [" + currentUser.lastLogon + "] by " + currentUser.getUsername());
                loginPW.close();
                return true;
            }
            else if ((rs.getString("User_Name").equals(username)) && !(rs.getString("Password").equals(password))) {
                loginPW.println("Unsuccessful logon at [" + LocalDateTime.now() + "] by " + (rs.getString("User_Name")));
                return false;
            }
        }

        loginPW.println("Unsuccessful logon at [" + LocalDateTime.now() + "] by unidentified user");
        return false;
    }

    /**
     * Closes print write object.
     */
    public static void closePrintWriter() {
        loginPW.close();
    }
}
