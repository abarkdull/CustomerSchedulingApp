package Model;

import Database.DBConnection;
import Database.DBQuery;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class used for creating and managing contacts both locally and in mySQL database.
 */
public class Contact {

    /**
     * Contact id.
     */
    private int contactId;

    /**
     * Name of contact.
     */
    private String contactName;

    /**
     * Contacts email.
     */
    private String email;

    /**
     * List containing all contacts.
     */
    private static ObservableList<Contact> allContacts;

    static {
        try {
            allContacts = setAllContacts();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Instantiates a contact object.
     */
    public Contact(int contactId, String contactName, String email) {
        this.contactId = contactId;
        this.contactName = contactName;
        this.email = email;
    }

    /**
     * Returns report of all appointments per contact as a stringbuilder object.
     */
    public static StringBuilder generateReport() {

        StringBuilder report = new StringBuilder();
        report.append("Format: Appointment Id | Title | Type | Description | Start Date | End Date | Customer ID\n\n");
        for (Contact contact : allContacts) {
            report.append(contact.getContactName() + ":\n");
            ObservableList<Appointment> apptsForContact = Appointment.getApptsByContactId(contact.contactId);
            for (Appointment a : apptsForContact) {
                String apptId = String.valueOf(a.getApptId());
                String title = a.getTitle();
                String type = a.getType();
                String description = a.getDescription();
                String startDate = String.valueOf(a.getStart());
                String endDate = String.valueOf(a.getEnd());
                String customerId = String.valueOf(a.getCustomerId());
                String r = apptId + " | " + title + " | " + type + " | " + description + " | " + startDate + " | " + endDate + " | " + customerId;
                report.append(r + "\n");
            }
            report.append("\n");
        }
        return report;
    }

    /**
     * Returns contact object of request contact id.
     * @param contactId requested contact id
     */
    public static Contact lookupContact(int contactId) {
        for (Contact c : allContacts) {
            if (c.contactId == contactId) {
                return  c;
            }
        }
        return null;
    }

    /**
     * Returns list of allContacts.
     */
    public static ObservableList<Contact> getAllContacts() {
        return allContacts;
    }

    /**
     * Overrides Object toString method to populate more relevant data in combo boxes.
     * @return returns string containing contact name and email
     */
    @Override
    public String toString() {
        return this.contactName + " [" + this.getEmail() + "]";
    }

    /**
     * Adds all contacts from database to allContacts list.
     * @return returns list of contacts from database
     */
    public static ObservableList<Contact> setAllContacts() throws SQLException {
        try {
            allContacts.removeAll();
        } catch (NullPointerException e) {}

        ObservableList<Contact> res = FXCollections.observableArrayList();

        String sqlQuery = "SELECT * FROM contacts";
        DBQuery.setPreparedStatement(DBConnection.getConnection(), sqlQuery);
        PreparedStatement ps = DBQuery.getPreparedStatement();
        ps.execute();

        ResultSet results = ps.getResultSet();
        while (results.next()) {
            int contactId = results.getInt("Contact_ID");
            String contactName = results.getString("Contact_Name");
            String email = results.getString("Email");
            res.add(new Contact(contactId, contactName, email));
        }

        return res;
    }

    /**
     * Returns contact id.
     * @return
     */
    public int getContactId() {
        return contactId;
    }

    /**
     * Sets new contact id.
     * @param contactId new contact id
     */
    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    /**
     * Returns name of contact.
     * @return
     */
    public String getContactName() {
        return contactName;
    }

    /**
     * Sets new contact name.
     * @param contactName new contact name
     */
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    /**
     * Returns email of contact.
     * @return
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets new email for contact.
     * @param email new email
     */
    public void setEmail(String email) {
        this.email = email;
    }
}
