package Model;

import Database.DBConnection;
import Database.DBQuery;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Class used for creating and managing countries both locally and in mySQL database.
 */
public class Country {

    /**
     * Country id.
     */
    private int id;

    /**
     * Country name.
     */
    private String country;

    /**
     * Country create date.
     */
    private LocalDateTime createDate;

    /**
     * User who created country.
     */
    private String createdBy;

    /**
     * TIme of last update of country.
     */
    private Timestamp lastUpdate;

    /**
     * User who last updated country.
     */
    private String lastUpdatedBy;

    /**
     * Instantiates a country object.
     */
    public Country(int id, String country, LocalDateTime createDate, String createdBy, Timestamp lastUpdate, String lastUpdatedBy) {
        this.id = id;
        this.country = country;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdatedBy = lastUpdatedBy;
    }

    /**
     * Returns list of all countries in database.
     */
    public static ObservableList<Country> getAllCountries() throws SQLException {
        ObservableList<Country> countries = FXCollections.observableArrayList();
        String sql = "SELECT * FROM countries";
        DBQuery.setPreparedStatement(DBConnection.getConnection(), sql);
        PreparedStatement ps = DBQuery.getPreparedStatement();
        ps.execute();

        ResultSet rs = ps.getResultSet();
        while(rs.next()) {
            int countryId = rs.getInt("Country_ID");
            String country = rs.getString("Country");

            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime createDate = LocalDateTime.parse(rs.getString("Create_Date"), df);

            String createdBy = rs.getString("Created_By");
            Timestamp lastUpdate = rs.getTimestamp("Last_Update");
            String lastUpdatedBy = rs.getString("Last_Updated_By");

            countries.add(new Country(countryId, country, createDate, createdBy, lastUpdate, lastUpdatedBy));

        }
        return countries;
    }

    /**
     * Returns country object by id.
     * @param countryId country id to be found.
     */
    public static Country lookupCountry(int countryId) throws SQLException {
        for (Country country : getAllCountries()) {
            if (country.getId() == countryId) {
                return country;
            }
        }
        return null;
    }

    /**
     * Overrides Object toString() method to better portray coutry data in combo boxes.
     */
    @Override
    public String toString() {
        return this.getCountry();
    }

    /**
     * Returns country id.
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * Sets new country id.
     * @param id new country id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns name of country.
     * @return
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets new name for country.
     * @param country new name
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Gets create date of country.
     * @return
     */
    public LocalDateTime getCreateDate() {
        return createDate;
    }

    /**
     * Sets new created date for country.
     * @param createDate new create date
     */
    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    /**
     * Gets created by for country
     * @return
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Sets new created by for country.
     * @param createdBy new created by
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Returns last update of country.
     * @return
     */
    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    /**
     * Sets new last update for country.
     * @param lastUpdate new last updatead
     */
    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    /**
     * Gets last updated by for country
     * @return
     */
    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    /**
     * Sets new last updated by for country.
     * @param lastUpdatedBy new last updated by
     */
    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }
}
