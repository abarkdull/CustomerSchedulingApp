package Model;

import Database.DBConnection;
import Database.DBQuery;
import Interface.FilterComboBoxTimes;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * Class used for creating and managing appointments both locally and in mySQL database.
 */
public class Appointment {

    /**
     * Appointment ID.
     */
    private int apptId;

    /**
     * Appointment title.
     */
    private String title;

    /**
     * Appointment description.
     */
    private String description;

    /**
     * Appointment location
     */
    private String location;

    /**
     * Appointment type.
     */
    private String type;

    /**
     * Appointment start time.
     */
    private LocalDateTime start;

    /**
     * Appointment end time.
     */
    private LocalDateTime end;

    /**
     * Appointment create date.
     */
    private LocalDateTime createDate;

    /**
     * User who created appointment.
     */
    private String createdBy;

    /**
     * Timestamp of last update.
     */
    private LocalDateTime lastUpdated;

    /**
     * User who last updated the appointment.
     */
    private String lastUpdatedBy;

    /**
     * Customer ID of appointment
     */
    private int customerId;

    /**
     * User ID of appointment.
     */
    private int userId;

    /**
     * Contact ID of appointment.
     */
    private int contactId;

    /**
     * All appointments from database
     */
    private static ObservableList<Appointment> allAppointments;

    /**
     * Appointments updated by current user int the current session.
     */
    private static ObservableList<Appointment> recentlyUpdatedAppointments = FXCollections.observableArrayList();

    static {
        try {
            allAppointments = setAllAppts();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Overloaded constructor used for instantiating pre-existing appointments from database.
     */
    public Appointment(int apptId, String title, String description, String location, String type, LocalDateTime start, LocalDateTime end, LocalDateTime createDate, String createdBy, LocalDateTime lastUpdated, String lastUpdatedBy, int customerId, int userId, int contactId) {
        this.apptId = apptId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.start = start;
        this.end = end;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdated = lastUpdated;
        this.lastUpdatedBy = lastUpdatedBy;
        this.customerId = customerId;
        this.userId = userId;
        this.contactId = contactId;
    }

    /**
     * Overloaded constructor used for instantiating new appointments.
     */
    public Appointment(String title, String description, String location, String type, LocalDateTime start, LocalDateTime end, int customerId, int userId, int contactId) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.start = start;
        this.end = end;
        this.createDate = LocalDateTime.now();
        this.createdBy = User.getCurrentUser().toString();
        this.lastUpdatedBy = User.getCurrentUser().toString();
        this.customerId = customerId;
        this.userId = userId;
        this.contactId = contactId;
    }

    /**
     * Generates report of appointments by type and month.
     * @return returns the report as a stringbuilder object.
     */
    public static StringBuilder generateReport() throws SQLException {
        StringBuilder report = new StringBuilder();
        report.append(String.format("%-12s%-20s%s\n", "Month", "Type", "Total Appt(s)"));
        report.append(String.format("%-12s%-20s%s\n", "-----", "----", "-------------"));

        String selectReportView = "SELECT * FROM Report";
        DBQuery.setPreparedStatement(DBConnection.getConnection(), selectReportView);
        PreparedStatement ps = DBQuery.getPreparedStatement();
        ps.execute();
        ResultSet rs = ps.getResultSet();

        while (rs.next()) {
            String month = Month.of(rs.getInt("Month")).name();
            String type = rs.getString("Type");
            String total = String.valueOf(rs.getInt("Total"));

            String r = String.format("%-12s%-20s%s\n", month, type, total);
            report.append(r);
        }

        return report;
    }

    /**
     * Returns all appointments for contact.
     * DISCUSSION OF LAMBDA: This lambda allowed me to
     * more efficiently loop through the appointments to find
     * appointments with matching contact IDs. It also allowed
     * me to greatly reduce the amount of lines of code required
     * to accomplish the task.
     * @param contId
     * @return
     */
    public static ObservableList<Appointment> getApptsByContactId(int contId) {

        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        allAppointments.forEach( appointment -> {
            if (appointment.getContactId() == contId) appointments.add(appointment);
            });
        return appointments;
    }

    /**
     * Adds a new appointment to allAppointments and to the database.
     * @param a appointment to be added.
     */
    public static void addAppointment(Appointment a) throws SQLException {
        String insertStatement = "INSERT INTO appointments(Title, Description, Location, Type, Start, End, Create_Date, Created_By, Last_Updated_By, Customer_ID, User_ID, Contact_ID) " +
                                " VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";

        DBQuery.setPreparedStatement(DBConnection.getConnection(), insertStatement);
        PreparedStatement ps = DBQuery.getPreparedStatement();

        ps.setString(1, a.getTitle());
        ps.setString(2, a.getDescription());
        ps.setString(3, a.getLocation());
        ps.setString(4, a.getType());

        ps.setTimestamp(5, Timestamp.valueOf(a.getStart()));
        ps.setTimestamp(6, Timestamp.valueOf(a.getEnd()));
        ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));

        ps.setString(8, a.createdBy);
        ps.setString(9, a.lastUpdatedBy);
        ps.setInt(10, a.customerId);
        ps.setInt(11, a.userId);
        ps.setInt(12, a.contactId);

        ps.execute();

        String getNextAutoIncrIndex = "SELECT AUTO_INCREMENT FROM information_schema.tables WHERE table_name = 'appointments' AND table_schema = DATABASE( ) ;";
        DBQuery.setPreparedStatement(DBConnection.getConnection(), getNextAutoIncrIndex);
        ps = DBQuery.getPreparedStatement();
        ps.execute();

        ResultSet rs1 = ps.getResultSet();
        int nextAutoIncrement = 0;
        while (rs1.next()) {
            nextAutoIncrement = rs1.getInt("Auto_Increment");
        }

        a.apptId = nextAutoIncrement - 1;
        allAppointments.add(a);
    }

    /**
     * Used for populating contact info in the appointments table view.
     * @return returns the contacts name.
     */
    public String getContactNameById() {
        return Contact.lookupContact(this.contactId).getContactName();
    }

    /**
     * Used for populating customer info in the appointments table view.
     * @return returns the customers name.
     */
    public String getCustomerNameById() {
        return Customer.lookupCustomer(this.customerId).getCustomerName();
    }

    /**
     * Filters appointments by the current month.
     * @param month month to be filtered by.
     * @return returns list of appointments in current month
     */
    public static ObservableList<Appointment> filterByMonth(String month) {

        ObservableList<Appointment> filteredList = FXCollections.observableArrayList();
        for (Appointment appt : allAppointments) {
            if (appt.getStart().getMonth().toString().toLowerCase().equals(month.toLowerCase())) {
                filteredList.add(appt);
            }
        }
        return filteredList;
    }

    /**
     * Filters appointments by the current week.
     * @param currentLDT current date and time.
     * @return returns list of appointments in current week.
     */
    public static ObservableList<Appointment> filterByWeek(LocalDateTime currentLDT) {

        ObservableList<Appointment> filteredList = FXCollections.observableArrayList();
        LocalDate ld = currentLDT.toLocalDate();
        while (true) {
            String dayOfWeek = ld.getDayOfWeek().toString();
            if (ld.getDayOfWeek().toString().equals("MONDAY")) {
                break;
            }
            ld = ld.minusDays(1);
        }

        LocalDate monday = ld;
        LocalDate nextMonday = monday.plusDays(7);

        for (Appointment a : allAppointments) {
            if (a.start.toLocalDate().equals(monday)) {
                filteredList.add(a);
            }
            else if (a.start.toLocalDate().isAfter(monday) && a.start.toLocalDate().isBefore(nextMonday)) {
                filteredList.add(a);
            }
        }

        return filteredList;
    }

    /**
     * Returns appointment object with matching ID.
     * @param apptId appointment ID to be searched.
     */
    public static Appointment lookupAppt(int apptId) {

        for (Appointment a : allAppointments) {
            if (a.getApptId() == apptId) {
                return a;
            }
        }
        return null;
    }


    /**
     * Updates appointment in allAppointments and in the database.
     * @param appt appointment to be updated.
     */
    public static void updateAppointment(Appointment appt) throws SQLException {
        String updateStatement = "UPDATE appointments " +
                                "SET Title = ?, " +
                                "Description = ?, " +
                                "Location = ?, " +
                                "Type = ?, " +
                                "Start = ?, " +
                                "End = ?, " +
                                "Last_Updated_By = ?, " +
                                "Last_Update = NOW()," +
                                "Customer_ID = ?, " +
                                "User_ID = ?, " +
                                "Contact_ID = ? " +
                                "WHERE Appointment_ID = ?";

        DBQuery.setPreparedStatement(DBConnection.getConnection(), updateStatement);
        PreparedStatement ps = DBQuery.getPreparedStatement();

        ps.setString(1, appt.title);
        ps.setString(2, appt.description);
        ps.setString(3, appt.location);
        ps.setString(4, appt.type);
        ps.setTimestamp(5, Timestamp.valueOf(appt.start));
        ps.setTimestamp(6, Timestamp.valueOf(appt.end));
        ps.setString(7, User.getCurrentUser().getUsername());
        ps.setInt(8, appt.customerId);
        ps.setInt(9, appt.userId);
        ps.setInt(10, appt.contactId);
        ps.setInt(11, appt.apptId);

        ps.execute();
        appt.setLastUpdated(LocalDateTime.now());
        appt.addToRecentlyUpdatedAppt();
        allAppointments.set(allAppointments.indexOf(Appointment.lookupAppt(appt.apptId)), appt);
    }

    /**
     * Adds appointment to recently updated list.
     */
    public void addToRecentlyUpdatedAppt() {
        recentlyUpdatedAppointments.add(this);
    }

    /**
     * Returns all recently updated appointments.
     */
    public static ObservableList<Appointment> getRecentlyUpdatedAppointments() {
        return recentlyUpdatedAppointments;
    }

    /**
     * Returns true if there is an appointment in allAppointments which overlaps appointment a.
     * @param a new appointment
     */
    public static boolean isOverlapping(Appointment a) {

        boolean res = false;
        boolean current;
        for (Appointment appt : allAppointments) {
            if (a.start.isAfter(appt.start) && a.start.isBefore(appt.end)) {
                res = true;
            }
            else if (a.start.isEqual(appt.start) || a.end.isEqual(appt.end)) {
                res = true;
            }
            else if (a.end.isAfter(appt.start) && a.end.isBefore(appt.end)) {
                res = true;
            }
            else if(a.start.isBefore(appt.start) && a.end.isAfter(appt.end)) {
                res = true;
            }

            if (res) {
                boolean isSameCustomer = a.customerId == appt.customerId;
                boolean isSameAppt = a.apptId == appt.apptId;
                if (isSameCustomer && !isSameAppt) {
                    return true;
                }
                else {
                    res = false;
                }
            }

        }
        return false;

    }

    /**
     * Deletes appointment from allAppointments and from database.
     * @param a appointment to be deleted.
     */
    public static void deleteAppointment(Appointment a) throws SQLException {
        String deleteStatement = "DELETE FROM appointments WHERE Appointment_ID = ?";
        DBQuery.setPreparedStatement(DBConnection.getConnection(), deleteStatement);
        PreparedStatement ps = DBQuery.getPreparedStatement();

        ps.setInt(1, a.apptId);
        ps.execute();
        allAppointments.remove(a);
    }

    /**
     * Returns combo box start times based on 8 am -> 10 pm EST in users local time.
     * DISCUSSION OF LAMBDA: This lambda allowed me to reduce the amount of code required
     * to complete the task. This lambda will also be helpful in future enhancement updates as
     * the interface can be utilized again in other areas of the code (e.g. the getComboBoxEndTimes
     * method).
     */
    public static ObservableList<LocalTime> getComboBoxStartTimes(LocalTime beginTime) {

        FilterComboBoxTimes comboBoxSet = (begin) -> {
            LocalDate utilityDate = LocalDate.of(1997, 11, 25);
            LocalDateTime utilityBeginTime = LocalDateTime.of(utilityDate, begin);
            ZonedDateTime easternZDT = utilityBeginTime.atZone(ZoneId.of("US/Eastern"));
            ZonedDateTime easternToLocal = easternZDT.withZoneSameInstant(ZoneId.of(ZoneId.systemDefault().toString()));
            begin = easternToLocal.toLocalTime();

            ObservableList<LocalTime> cbTimes = FXCollections.observableArrayList();
            LocalTime endTime = begin.plusHours(14);
            while (begin.isBefore(endTime.plusSeconds(1))) {
                cbTimes.add(begin);
                begin = begin.plusMinutes(30);
            }
            return cbTimes;
        };
        return comboBoxSet.getComboBoxTimes(beginTime);

    }

    /**
     * Returns combo box end times based on begin time -> 10 pm EST in users local time.
     */
    public static ObservableList<LocalTime> getComboBoxEndTimes(LocalTime beginTime) {
        ObservableList<LocalTime> cbTimes = FXCollections.observableArrayList();


        LocalDate utilityDate = LocalDate.of(1997, 11, 25);
        LocalDateTime utilityEndTime = LocalDateTime.of(utilityDate, LocalTime.of(22, 00));
        ZonedDateTime easternZDT = utilityEndTime.atZone(ZoneId.of("US/Eastern"));
        ZonedDateTime easternToLocal = easternZDT.withZoneSameInstant(ZoneId.of(ZoneId.systemDefault().toString()));
        LocalTime endTime = easternToLocal.toLocalTime();

        while (beginTime.isBefore(endTime.plusSeconds(1))) {
            cbTimes.add(beginTime);
            beginTime = beginTime.plusMinutes(30);
        }

        return cbTimes;

    }

    /**
     * Returns allAppointments.
     */
    public static ObservableList<Appointment> getAllAppts() {
        return allAppointments;
    }

    /**
     * Adds all pre-existing appointments in database to allAppointments list.
     * @return returns list of appointments from database.
     */
    public static ObservableList<Appointment> setAllAppts() throws SQLException {
        try {
            allAppointments.removeAll();
        } catch (NullPointerException e) {}

        ObservableList<Appointment> res = FXCollections.observableArrayList();

        String sqlQuery = "SELECT * FROM appointments";
        DBQuery.setPreparedStatement(DBConnection.getConnection(), sqlQuery);
        PreparedStatement ps = DBQuery.getPreparedStatement();
        ps.execute();

        ResultSet results = ps.getResultSet();
        while (results.next()) {
            int apptId = results.getInt("Appointment_ID");
            String title = results.getString("Title");
            String description = results.getString("Description");
            String location = results.getString("Location");
            String type = results.getString("Type");

            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            LocalDateTime start = LocalDateTime.parse(results.getString("Start"), df);
            ZonedDateTime utcZDT = start.atZone(ZoneId.of("UTC"));
            ZonedDateTime utcToLocalZDT = utcZDT.withZoneSameInstant(ZoneId.of(ZoneId.systemDefault().toString()));
            start = utcToLocalZDT.toLocalDateTime();

            LocalDateTime end = LocalDateTime.parse(results.getString("End"), df);
            utcZDT = end.atZone(ZoneId.of("UTC"));
            utcToLocalZDT = utcZDT.withZoneSameInstant(ZoneId.of(ZoneId.systemDefault().toString()));
            end = utcToLocalZDT.toLocalDateTime();

            LocalDateTime createDate = LocalDateTime.parse(results.getString("Create_Date"), df);
            String createdBy = results.getString("Created_By");
            LocalDateTime lastUpdate = results.getTimestamp("Last_Update").toLocalDateTime();
            String lastUpdatedBy = results.getString("Last_Updated_By");
            int customerId = results.getInt("Customer_ID");
            int userId = results.getInt("User_ID");
            int contactId = results.getInt("Contact_ID");
            res.add(new Appointment(apptId, title, description, location, type, start, end, createDate, createdBy, lastUpdate, lastUpdatedBy, customerId, userId, contactId));
        }

        return res;
    }

    /**
     * Returns appointment ID.
     * @return
     */
    public int getApptId() {
        return apptId;
    }

    /**
     * Sets new appointment id.
     * @param apptId new appointment id.
     */
    public void setApptId(int apptId) {
        this.apptId = apptId;
    }

    /**
     * Returns title of appointment.
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets new appointment title.
     * @param title new title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns appointment description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets new appointment description.
     * @param description new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns appointment location.
     * @return
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets new appointment location.
     * @param location new location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Returns appointment type.
     * @return
     */
    public String getType() {
        return type;
    }

    /**
     * Sets new appointment type.
     * @param type new type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Returns start time of appointment.
     * @return
     */
    public LocalDateTime getStart() {
        return start;
    }

    /**
     * Sets new appointment start time.
     * @param start new start time
     */
    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    /**
     * Returns appointment end time.
     * @return
     */
    public LocalDateTime getEnd() {
        return end;
    }

    /**
     * Sets new appointment end time.
     * @param end new appointment end time
     */
    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    /**
     * Returns appointment create date.
     * @return
     */
    public LocalDateTime getCreateDate() {
        return createDate;
    }

    /**
     * Sets new appointment create date.
     * @param createDate new create date
     */
    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    /**
     * Returns appointment created by
     * @return
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Sets new appointment created by.
     * @param createdBy new created by
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Returns appointment last update time.
     * @return
     */
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    /**
     * Sets new last update time.
     * @param lastUpdated new update time.
     */
    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    /**
     * Returns appointment last update by
     * @return
     */
    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    /**
     * Sets new appointment last update by.
     * @param lastUpdatedBy
     */
    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    /**
     * Return customer id of appointment
     * @return
     */
    public int getCustomerId() {
        return customerId;
    }

    /**
     * Sets new customer id of appointment
     * @param customerId new customer id
     */
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    /**
     * Returns user id of appointment
     * @return
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Sets new user id of appointment
     * @param userId new user id
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Returns contact id of appointment
     * @return
     */
    public int getContactId() {
        return contactId;
    }

    /**
     * Sets new contact id of appointment
     * @param contactId new contact id
     */
    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    /**
     * Returns all appointments list
     * @return
     */
    public static ObservableList<Appointment> getAllAppointments() {
        return allAppointments;
    }

}
