package Model;

import Database.DBConnection;
import Database.DBQuery;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Class used for creating and managing first-level-divisions both locally and in mySQL database.
 */
public class FirstLevelDivision {

    /**
     * Division id.
     */
    private int divisionId;

    /**
     * Name of division.
     */
    private String division;

    /**
     * Division create date.
     */
    private LocalDateTime createDate;

    /**
     * User who created division.
     */
    private String createdBy;

    /**
     * Last update of division.
     */
    private Timestamp lastUpdate;

    /**
     * User who last updated division.
     */
    private String lastUpdatedBy;

    /**
     * Country id of division.
     */
    private int countryId;

    /**
     * Creates a first level division object.
     */
    public FirstLevelDivision(int divisionId, String division, LocalDateTime createDate, String createdBy, Timestamp lastUpdate, String lastUpdatedBy, int countryId) {
        this.divisionId = divisionId;
        this.division = division;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdatedBy = lastUpdatedBy;
        this.countryId = countryId;
    }

    /**
     * Filters first level divisions based on country id.
     * @param countryId country id to filter FLDs
     * @return returns list of filtered FLDs
     */
    public static ObservableList<FirstLevelDivision> filteredFLD(int countryId) throws SQLException {

        ObservableList<FirstLevelDivision> filteredFLDs = FXCollections.observableArrayList();

        for (FirstLevelDivision fld : getAllFirstLevelDivs()) {
            if (fld.getCountryId() == countryId) {
                filteredFLDs.add(fld);
            }
        }

        return filteredFLDs;
    }

    /**
     * Returns country id of a first level division.
     */
    public static int lookupCountryId(int divisionId) throws SQLException {
        for (FirstLevelDivision fld : getAllFirstLevelDivs()) {
            if (fld.getDivisionId() == divisionId) {
                return fld.getCountryId();
            }
        }
        return -1;
    }

    /**
     * Returns firstleveldivision object of a division id.
     */
    public static FirstLevelDivision lookupFLD(int divisionId) throws SQLException {
        for (FirstLevelDivision fld : getAllFirstLevelDivs()) {
            if (fld.getDivisionId() == divisionId) {
                return fld;
            }
        }
        return null;
    }

    /**
     * Overrides Object toString() for better portrayal of FLD data in combo boxes.
     */
    @Override
    public String toString() {
        return this.getDivision();
    }

    /**
     * Returns list of all first level divisions.
     */
    public static ObservableList<FirstLevelDivision> getAllFirstLevelDivs() throws SQLException {
        ObservableList<FirstLevelDivision> allFirstDivs = FXCollections.observableArrayList();
        String sql = "SELECT * FROM first_level_divisions";
        DBQuery.setPreparedStatement(DBConnection.getConnection(), sql);
        PreparedStatement ps = DBQuery.getPreparedStatement();
        ps.execute();

        ResultSet rs = ps.getResultSet();
        while (rs.next()) {
            int divisionId = rs.getInt("Division_ID");
            String division = rs.getString("Division");

            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime createDate = LocalDateTime.parse(rs.getString("Create_Date"), df);

            String createdBy = rs.getString("Created_By");
            Timestamp lastUpdate = rs.getTimestamp("Last_Update");
            String lastUpdatedBy = rs.getString("Last_Updated_By");
            int countryId = rs.getInt("Country_ID");

            allFirstDivs.add(new FirstLevelDivision(divisionId, division, createDate, createdBy, lastUpdate, lastUpdatedBy, countryId));

        }

        return allFirstDivs;
    }

    /**
     * Returns division id.
     * @return
     */
    public int getDivisionId() {
        return divisionId;
    }

    /**
     * Sets new division id.
     * @param divisionId new division id
     */
    public void setDivisionId(int divisionId) {
        this.divisionId = divisionId;
    }

    /**
     * Returns name of division.
     * @return
     */
    public String getDivision() {
        return division;
    }

    /**
     * Sets new name of division.
     * @param division new division name
     */
    public void setDivision(String division) {
        this.division = division;
    }

    /**
     * Gets created date of division.
     * @return
     */
    public LocalDateTime getCreateDate() {
        return createDate;
    }

    /**
     * Sets new create date for division.
     * @param createDate new create date.
     */
    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    /**
     * Gets user who created division.
     * @return
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Sets new created by for division.
     * @param createdBy new created by.
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Gets last update of division.
     * @return
     */
    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    /**
     * Sets new last update for division.
     * @param lastUpdate new last update
     */
    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    /**
     * Gets user who last updated division.
     * @return
     */
    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    /**
     * Sets new last updated by for division.
     * @param lastUpdatedBy new last updated by
     */
    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    /**
     * Gets country id of division.
     * @return
     */
    public int getCountryId() {
        return countryId;
    }

    /**
     * Sets new country id for division.
     * @param countryId new country id
     */
    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }
}
