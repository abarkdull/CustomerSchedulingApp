package Controller;

import Model.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;

/** Class used for controlling events on the appointments page.*/
public class AppointmentsController implements Initializable {

    @FXML
    private Label customersLbl;

    @FXML
    private TableView<Appointment> apptsTableView;

    @FXML
    private TableColumn<Appointment, Integer> apptIdCol;

    @FXML
    private TableColumn<Appointment, String> titleCol;

    @FXML
    private TableColumn<Appointment, String> descriptionCol;

    @FXML
    private TableColumn<Appointment, String> locationCol;

    @FXML
    private TableColumn<Appointment, Integer> contactCol;

    @FXML
    private TableColumn<Appointment, String> typeCol;

    @FXML
    private TableColumn<Appointment, LocalDateTime> startDateCol;

    @FXML
    private TableColumn<Appointment, LocalDateTime> endDateCol;

    @FXML
    private TableColumn<Appointment, Integer> customerIdCol;

    @FXML
    private HBox buttonHbox;

    @FXML
    private Label addModifyApptLbl;

    @FXML
    private GridPane addModifyApptGridPane;

    @FXML
    private ComboBox<Customer> customerComboBox;

    @FXML
    private ComboBox<Contact> contactComboBox;

    @FXML
    private ComboBox<User> userComboBox;

    @FXML
    private TextField apptIdTF;

    @FXML
    private TextField titleTF;

    @FXML
    private TextField descriptionTF;

    @FXML
    private TextField locationTF;

    @FXML
    private TextField typeTF;

    @FXML
    private DatePicker startDate;

    @FXML
    private ComboBox<LocalTime> startTime;

    @FXML
    private DatePicker endDate;

    @FXML
    private Label exceptionsLbl;

    @FXML
    private ComboBox<LocalTime> endTime;

    @FXML
    private RadioButton filterByMonthRB;

    @FXML
    private RadioButton filterByWeekRB;

    private boolean modify = false;

    /** Initializes appt form and populates the appointments table view.*/
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        apptsTableView.setItems(Appointment.getAllAppointments());

        apptIdCol.setCellValueFactory(new PropertyValueFactory<>("ApptId"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("Title"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("Description"));
        locationCol.setCellValueFactory(new PropertyValueFactory<>("Location"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("Type"));
        startDateCol.setCellValueFactory(new PropertyValueFactory<>("Start"));
        endDateCol.setCellValueFactory(new PropertyValueFactory<>("End"));
        customerIdCol.setCellValueFactory(new PropertyValueFactory<>("CustomerId"));
        contactCol.setCellValueFactory(new PropertyValueFactory<>("ContactNameById"));


    }

    /**
     * Sets form to allow for addition of a user.
     * @param actionEvent add clicked
     */
    public void onAdd(ActionEvent actionEvent) {

        modify = false;
        addModifyApptLbl.setText("--Add Appointment--");
        addModifyApptGridPane.setDisable(false);
        apptsTableView.setDisable(true);
        buttonHbox.setDisable(true);
        customersLbl.setDisable(true);
        addModifyApptLbl.setDisable(false);
        apptIdTF.setText(null);
        titleTF.setText(null);
        descriptionTF.setText(null);
        locationTF.setText(null);
        typeTF.setText(null);
        customerComboBox.getSelectionModel().select(null);
        contactComboBox.getSelectionModel().select(null);
        userComboBox.getSelectionModel().select(null);
        userComboBox.setItems(User.getAllUsers());
        customerComboBox.setItems(Customer.getAllCustomers());
        contactComboBox.setItems(Contact.getAllContacts());
        startTime.setItems(Appointment.getComboBoxStartTimes(LocalTime.of(8, 00)));

    }

    /**
     * Sets form back to view mode (enables table view, disables add/update section).
     */
    public void viewMode() {
        customersLbl.setDisable(false);
        apptsTableView.setDisable(false);
        buttonHbox.setDisable(false);
        addModifyApptLbl.setDisable(true);
        addModifyApptGridPane.setDisable(true);
        titleTF.setText(null);
        descriptionTF.setText(null);
        locationTF.setText(null);
        typeTF.setText(null);
        startDate.setValue(null);
        startTime.getSelectionModel().select(null);
        endDate.setValue(null);
        endTime.getSelectionModel().select(null);
        customerComboBox.getSelectionModel().select(null);
        contactComboBox.getSelectionModel().select(null);
        userComboBox.getSelectionModel().select(null);
    }

    /**
     * Sets form for updating an appointment.
     * @param actionEvent update button clicked
     */
    public void onUpdate(ActionEvent actionEvent) {

        if (apptsTableView.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please select an appointment to update.");
            alert.showAndWait();
            return;
        }
        Appointment selectedAppt = apptsTableView.getSelectionModel().getSelectedItem();

        modify = true;
        addModifyApptLbl.setText("--Update Appointment--");
        addModifyApptGridPane.setDisable(false);
        apptsTableView.setDisable(true);
        buttonHbox.setDisable(true);
        customersLbl.setDisable(true);
        addModifyApptLbl.setDisable(false);

        apptIdTF.setText(String.valueOf(selectedAppt.getApptId()));
        titleTF.setText(selectedAppt.getTitle());
        descriptionTF.setText(selectedAppt.getDescription());
        locationTF.setText(selectedAppt.getLocation());
        typeTF.setText(selectedAppt.getType());

        startDate.setValue(selectedAppt.getStart().toLocalDate());
        startTime.setItems(Appointment.getComboBoxStartTimes(LocalTime.of(8, 00)));
        startTime.getSelectionModel().select(selectedAppt.getStart().toLocalTime());
        endDate.setValue(selectedAppt.getEnd().toLocalDate());
        endTime.setItems(Appointment.getComboBoxEndTimes(startTime.getSelectionModel().getSelectedItem()));
        endTime.getSelectionModel().select(selectedAppt.getEnd().toLocalTime());

        customerComboBox.setItems(Customer.getAllCustomers());
        customerComboBox.getSelectionModel().select(Customer.lookupCustomer(selectedAppt.getCustomerId()));
        userComboBox.setItems(User.getAllUsers());
        userComboBox.getSelectionModel().select(User.lookupUser(selectedAppt.getUserId()));
        contactComboBox.setItems(Contact.getAllContacts());
        contactComboBox.getSelectionModel().select(Contact.lookupContact(selectedAppt.getUserId()));
    }

    /**
     * Deletes selected appointment from database.
     * @param actionEvent delete button clicked
     */
    public void onDelete(ActionEvent actionEvent) throws SQLException {
        Appointment apptToBeDeleted = apptsTableView.getSelectionModel().getSelectedItem();

        if (apptToBeDeleted == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please select an appointment to delete.");
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Appointment " + apptToBeDeleted.getApptId() + " (Type: " + apptToBeDeleted.getType() + ") has been deleted.");
        Appointment.deleteAppointment(apptToBeDeleted);
        alert.showAndWait();
    }

    /**
     * Adds/Updates appointment in database.
     * @param actionEvent save button clicked
     */
    public void onSave(ActionEvent actionEvent) throws SQLException {
        exceptionsLbl.setText(null);
        int apptId = 0;
        try {
            apptId = Integer.parseInt(apptIdTF.getText());
        } catch (NumberFormatException e) { }

        try {
            String title = titleTF.getText();
            String description = descriptionTF.getText();
            String location = locationTF.getText();
            String type = typeTF.getText();

            LocalDate startingDate = startDate.getValue();
            LocalTime startingTime = startTime.getSelectionModel().getSelectedItem();
            LocalDate endingDate = startingDate;
            LocalTime endingTime = endTime.getSelectionModel().getSelectedItem();

            LocalDateTime localStartDateTime = LocalDateTime.of(startingDate, startingTime);
            LocalDateTime localEndDateTime = LocalDateTime.of(endingDate, endingTime);

            int customerId = customerComboBox.getSelectionModel().getSelectedItem().getCustomerId();
            int userId = userComboBox.getSelectionModel().getSelectedItem().getUserId();
            int contactId = contactComboBox.getSelectionModel().getSelectedItem().getContactId();

            Appointment newAppt = new Appointment(title, description, location, type, localStartDateTime, localEndDateTime, customerId, userId, contactId);
            if (modify) {
                if (title.isEmpty() || description.isEmpty() || location.isEmpty() || type.isEmpty()) {
                    throw new NullPointerException();
                }
                newAppt.setApptId(apptId);
                if (!Appointment.isOverlapping(newAppt)) {
                    Appointment.updateAppointment(newAppt);
                    viewMode();
                } else {
                    exceptionsLbl.setText("Overlapping appointments");
                }
            } else {
                if (!Appointment.isOverlapping(newAppt)) {
                    Appointment.addAppointment(newAppt);
                    viewMode();
                } else {
                    exceptionsLbl.setText("Overlapping appointments");
                }
            }
        }
        catch (NullPointerException f) {
            exceptionsLbl.setText("Missing required data fields");
        }
    }

    /**
     * Cancels add/update and returns to view mode.
     * @param actionEvent cancel button clicked
     */
    public void onCancel(ActionEvent actionEvent) {
        exceptionsLbl.setText(null);
        viewMode();
    }

    /**
     * Sets end date to same value as start date.
     * @param actionEvent date selected from datepicker
     */
    public void onDateSelected(ActionEvent actionEvent) {
        endDate.setValue(startDate.getValue());
    }

    /**
     * Sets end time combo box options based on selected start time.
     * @param actionEvent time selected from combo box
     */
    public void onStartTimeSelected(ActionEvent actionEvent) {
        try {
            LocalTime beginTime = startTime.getSelectionModel().getSelectedItem();
            endTime.setItems(Appointment.getComboBoxEndTimes(beginTime));
        } catch (NullPointerException e) { }
    }

    /**
     * Goes to reports page.
     * @param actionEvent reports button selected
     */
    public void onReportsSelected(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        Parent scene = FXMLLoader.load(getClass().getResource("/View/Reports.fxml"));

        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * Goes to customers page
     * @param actionEvent customers button clicked
     */
    public void onCustomerTableSelected(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        Parent scene = FXMLLoader.load(getClass().getResource("/View/Customers.fxml"));

        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * Filters appointment table view by current month
     * @param actionEvent filter by month button selected
     */
    public void onFilterByMonth(ActionEvent actionEvent) {
        ObservableList<Appointment> filteredMonths = Appointment.filterByMonth(LocalDateTime.now().getMonth().toString());
        apptsTableView.setItems(filteredMonths);
    }

    /**
     * Filters appointment table view by current week.
     * @param actionEvent filter by week button selected.
     */
    public void onFilterByWeek(ActionEvent actionEvent) {
        ObservableList<Appointment> filteredWeeks = Appointment.filterByWeek(LocalDateTime.now());
        apptsTableView.setItems(filteredWeeks);
    }

    /**
     * Displays all appointments in table view
     * @param actionEvent reset button clicked
     */
    public void onReset(ActionEvent actionEvent) {
        apptsTableView.setItems(Appointment.getAllAppointments());
        filterByMonthRB.setSelected(false);
        filterByWeekRB.setSelected(false);
    }
}