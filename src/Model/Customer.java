package Model;

import Database.DBConnection;
import Database.DBQuery;
import javafx.beans.binding.StringBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Class used for creating and managing customers both locally and in mySQL database.
 */
public class Customer {

    /**
     * Customer id.
     */
    private int customerId;

    /**
     * Name of customer.
     */
    private String customerName;

    /**
     * Customer address.
     */
    private String address;

    /**
     * Customer postal code.
     */
    private String postalCode;

    /**
     * Customer phone number.
     */
    private String phone;

    /**
     * Customer create date.
     */
    private LocalDateTime createDate;

    /**
     * User who created customer.
     */
    private String createdBy;

    /**
     * Date and time of last update of customer.
     */
    private LocalDateTime lastUpdate;

    /**
     * User who last updated customer.
     */
    private String lastUpdatedBy;

    /**
     * Division id of customer.
     */
    private int divisionId;

    /**
     * List of all customers.
     */
    private static ObservableList<Customer> allCustomers;

    /**
     * List of recently updated customers.
     */
    private static ObservableList<Customer> recentlyUpdatedCustomers = FXCollections.observableArrayList();

    static {
        try {
            allCustomers = setAllCustomers();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Overloaded constructor for creating new customers
     */
    public Customer(String customerName, String address, String postalCode, String phone,  int divisionId) throws SQLException {
        this.customerName = customerName;
        this.address = address;
        this.postalCode = postalCode;
        this.phone = phone;
        this.divisionId = divisionId;
        this.createdBy = User.getCurrentUser().toString();
        this.createDate = LocalDateTime.now();
        this.lastUpdate = LocalDateTime.now();
        this.lastUpdatedBy = User.getCurrentUser().toString();
    }

    /**
     * Overloaded constructor for creating pre-existing customers from database.
     */
    public Customer(int customerId, String customerName, String address, String postalCode, String phone,  LocalDateTime createDate, String createdBy, LocalDateTime lastUpdate, String lastUpdatedBy, int divisionId) throws SQLException {
        this.customerId = customerId;
        this.customerName = customerName;
        this.address = address;
        this.postalCode = postalCode;
        this.phone = phone;
        this.divisionId = divisionId;
        this.createdBy = createdBy;
        this.createDate = createDate;
        this.lastUpdatedBy = lastUpdatedBy;
        this.lastUpdate = lastUpdate;
    }

    /**
     * Adds customer to recently updated customer list.
     */
    public void addToRecentlyUpdatedCustomer() {
        recentlyUpdatedCustomers.add(this);
    }

    /**
     * Returns recently updated customer list.
     */
    public static ObservableList<Customer> getRecentlyUpdatedCustomer() {
        return recentlyUpdatedCustomers;
    }

    /**
     * Overrides Object toString() method for better portraying customer data in combo boxes.
     */
    @Override
    public String toString() {
        return "[" + this.getCustomerId() + "] " + this.getCustomerName();
    }

    /**
     * Returns list of all customers.
     */
    public static ObservableList<Customer> getAllCustomers() {
        return allCustomers;
    }

    /**
     * Deletes customer from allCustomers and from database.
     */
    public void deleteCustomer() throws SQLException {
        String deleteStatement = "DELETE FROM customers WHERE customer_id = ?";
        DBQuery.setPreparedStatement(DBConnection.getConnection(), deleteStatement);
        PreparedStatement ps = DBQuery.getPreparedStatement();

        ps.setInt(1, customerId);
        ps.execute();
        allCustomers.remove(this);
    }

    /**
     * Returns customer object of customer id.
     * @param customerId id of customer to be returned.
     */
    public static Customer lookupCustomer(int customerId) {
        for (Customer c : allCustomers) {
            if (c.getCustomerId() == customerId) {
                return c;
            }
        }
        return null;
    }

    /**
     * Returns name of first level division.
     */
    public String getDivisionName() throws SQLException {
        FirstLevelDivision fld = FirstLevelDivision.lookupFLD(this.divisionId);
        return fld.getDivision();
    }

    /**
     * Updates customer in allCustomers and in database.
     * @param indexOfCustomer index of customer in database
     */
    public void updateCustomer(int indexOfCustomer) throws SQLException {
        String updateStatement = "UPDATE customers " +
                                 "SET Customer_Name = ?, " +
                                      "Address = ?, " +
                                    "Postal_Code = ?, " +
                                    "Phone = ?, " +
                                    "Last_Updated_By = ?, " +
                                    "Last_Update = NOW()," +
                                    "Division_ID = ? " +
                                "WHERE Customer_ID = ?";

        DBQuery.setPreparedStatement(DBConnection.getConnection(), updateStatement);
        PreparedStatement ps = DBQuery.getPreparedStatement();

        ps.setString(1, this.getCustomerName());

        ps.setString(2, this.address);
        ps.setString(3, this.postalCode);
        ps.setString(4, this.phone);
        ps.setString(5, this.lastUpdatedBy);
        ps.setInt(6, this.divisionId);
        ps.setInt(7, indexOfCustomer);

        ps.execute();
        this.customerId = indexOfCustomer;
        this.lastUpdate = LocalDateTime.now();
        this.addToRecentlyUpdatedCustomer();
        allCustomers.set(allCustomers.indexOf(Customer.lookupCustomer(indexOfCustomer)), this);
    }

    /**
     * Adds all pre-existing customers in database to allCustomers list.
     * @return returns list of customers in database.
     */
    public static ObservableList<Customer> setAllCustomers() throws SQLException {

        try {
            allCustomers.removeAll();
        } catch (NullPointerException e) {}

        ObservableList<Customer> res = FXCollections.observableArrayList();

        String sqlQuery = "SELECT * FROM customers";
        DBQuery.setPreparedStatement(DBConnection.getConnection(), sqlQuery);
        PreparedStatement ps = DBQuery.getPreparedStatement();
        ps.execute();

        ResultSet results = ps.getResultSet();
        while (results.next()) {
            int customerId = results.getInt("Customer_ID");
            String customerName = results.getString("Customer_Name");
            String address = results.getString("Address");
            String postalCode = results.getString("Postal_Code");
            String phone = results.getString("Phone");

            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime createDate = LocalDateTime.parse(results.getString("Create_Date"), df);

            String createdBy = results.getString("Created_By");
            LocalDateTime lastUpdate = results.getTimestamp("Last_Update").toLocalDateTime();
            String lastUpdatedBy = results.getString("Last_Updated_By");
            int divisionId = results.getInt("Division_ID");
            res.add(new Customer(customerId, customerName, address, postalCode, phone, createDate, createdBy, lastUpdate, lastUpdatedBy, divisionId));
        }

        return res;
    }

    /**
     * Adds customer to allCustomers and to database.
     * @param c customer to be added.
     * @throws SQLException
     */
    public static void addCustomer(Customer c) throws SQLException {
        String insertStatement = "INSERT INTO customers(Customer_Name, Address, Postal_Code, Phone, Created_By,Last_Updated_By, Division_ID)"
                + " VALUES(?,?,?,?,?,?,?)";

        DBQuery.setPreparedStatement(DBConnection.getConnection(), insertStatement);
        PreparedStatement ps = DBQuery.getPreparedStatement();

        ps.setString(1, c.getCustomerName());
        ps.setString(2, c.getAddress());
        ps.setString(3, c.getPostalCode());
        ps.setString(4, c.getPhone());
        ps.setString(5, c.getCreatedBy());
        ps.setString(6, c.getLastUpdatedBy());
        ps.setInt(7, c.getDivisionId());

        ps.execute();

        String getNextAutoIncrIndex = "SELECT AUTO_INCREMENT FROM information_schema.tables WHERE table_name = 'customers' AND table_schema = DATABASE( ) ;";
        DBQuery.setPreparedStatement(DBConnection.getConnection(), getNextAutoIncrIndex);
        ps = DBQuery.getPreparedStatement();
        ps.execute();

        ResultSet rs1 = ps.getResultSet();
        int nextAutoIncrement = 0;
        while (rs1.next()) {
            nextAutoIncrement = rs1.getInt("Auto_Increment");
        }

        c.customerId = nextAutoIncrement - 1;
        allCustomers.add(c);
    }

    /**
     * Returns customer id.
     * @return
     */
    public int getCustomerId() {
        return customerId;
    }

    /**
     * Sets new customer id.
     * @param customerId new customer id
     */
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    /**
     * Returns name of customer.
     * @return
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Sets new customer name.
     * @param customerName new customer name.
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * Returns customer address.
     * @return
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets new address for customer.
     * @param address new address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Returns postal code of customer.
     * @return
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Sets new postal code for customer.
     * @param postalCode new postal code
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * Returns customer phone number.
     * @return
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets new phone number for customer.
     * @param phone new phone number
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Gets create date of customer.
     * @return
     */
    public LocalDateTime getCreateDate() {
        return createDate;
    }

    /**
     * Sets new create date for customer.
     * @param createDate new create date.
     */
    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    /**
     * Returns user who created customer.
     * @return
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Sets new created by user.
     * @param createdBy new created by
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Gets last update of customer.
     * @return
     */
    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    /**
     * Sets new last update of customer.
     * @param lastUpdate new last update
     */
    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    /**
     * Gets user who last updated customer.
     * @return
     */
    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    /**
     * Sets new last updated by user.
     * @param lastUpdatedBy new last updated by
     */
    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    /**
     * Gets division id of customer.
     * @return
     */
    public int getDivisionId() {
        return divisionId;
    }

    /**
     * Sets new division id for customer.
     * @param divisionId new division id
     */
    public void setDivisionId(int divisionId) {
        this.divisionId = divisionId;
    }
}
