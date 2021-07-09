package Controller;

import Model.Appointment;
import Model.Country;
import Model.Customer;
import Model.FirstLevelDivision;
import javafx.collections.FXCollections;
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
import java.util.ResourceBundle;

/**
 * Class used for controlling events on the customers page.
 */
public class CustomersController implements Initializable {

    @FXML
    private TableView<Customer> customersTblView;

    @FXML
    private TableColumn<Customer, Integer> customerId;

    @FXML
    private TableColumn<Customer, String> customerName;

    @FXML
    private TableColumn<Customer, String> addresss;

    @FXML
    private TableColumn<Customer, String> postalCode;

    @FXML
    private TableColumn<Customer, String> phone;

    @FXML
    private TableColumn<Customer, String> divisionId;

    @FXML
    private HBox buttonHbox;

    @FXML
    private Label addModifyCustomerLbl;

    @FXML
    private GridPane addModifyCustGridPane;

    @FXML
    private Label customersLbl;

    @FXML
    private ComboBox<Country> countryComboBox;

    @FXML
    private ComboBox<FirstLevelDivision> fldComboBox;

    @FXML
    private TextField customerNameTF;

    @FXML
    private TextField addressTF;

    @FXML
    private TextField postalCodeTF;

    @FXML
    private TextField phoneTF;

    @FXML
    private Label exceptionsLbl;

    @FXML
    private TextField customerIdTF;

    private static boolean modify = false;


    /**
     * Initializes customers page and populates customers table view.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        customersTblView.setItems(Customer.getAllCustomers());

        customerId.setCellValueFactory(new PropertyValueFactory<>("CustomerId"));
        customerName.setCellValueFactory(new PropertyValueFactory<>("CustomerName"));
        addresss.setCellValueFactory(new PropertyValueFactory<>("Address"));
        postalCode.setCellValueFactory(new PropertyValueFactory<>("PostalCode"));
        phone.setCellValueFactory(new PropertyValueFactory<>("Phone"));
        divisionId.setCellValueFactory(new PropertyValueFactory<>("DivisionName"));

    }


    /**
     * Sets customers page to allow for addition of new customer.
     * @param actionEvent add button clicked
     */
    public void onAdd(ActionEvent actionEvent) throws SQLException {
        modify = false;
        customersLbl.setDisable(true);
        customersTblView.setDisable(true);
        buttonHbox.setDisable(true);
        addModifyCustomerLbl.setDisable(false);
        addModifyCustGridPane.setDisable(false);
        countryComboBox.setItems(Country.getAllCountries());
        countryComboBox.getSelectionModel().select(null);
        fldComboBox.setItems(null);
        fldComboBox.getSelectionModel().select(null);
        addModifyCustomerLbl.setText("--Add Customer--");
        customerIdTF.setText("Disabled Auto-Generated");
        customerNameTF.setText(null);
        addressTF.setText(null);
        postalCodeTF.setText(null);
        phoneTF.setText(null);

    }

    /**
     * Sets page to view mode (add/update section disabled, table view enabled).
     */
    public void viewMode() {
        customersLbl.setDisable(false);
        customersTblView.setDisable(false);
        buttonHbox.setDisable(false);
        addModifyCustomerLbl.setDisable(true);
        addModifyCustGridPane.setDisable(true);
        customersTblView.refresh();
    }

    /**
     * Sets up customers page for updating an existing customer.
     * @param actionEvent update button selected
     */
    public void onUpdate(ActionEvent actionEvent) throws SQLException {

        if (customersTblView.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please select a customer to update.");
            alert.showAndWait();
            return;
        }
        modify = true;
        addModifyCustomerLbl.setText("--Update Customer--");
        customersLbl.setDisable(true);
        customersTblView.setDisable(true);
        buttonHbox.setDisable(true);
        addModifyCustomerLbl.setDisable(false);
        addModifyCustGridPane.setDisable(false);
        countryComboBox.setItems(Country.getAllCountries());

        Customer selectedCustomer = customersTblView.getSelectionModel().getSelectedItem();

        Country country = Country.lookupCountry(FirstLevelDivision.lookupCountryId(selectedCustomer.getDivisionId()));
        fldComboBox.setItems(FirstLevelDivision.filteredFLD(country.getId()));


        customerIdTF.setText(String.valueOf(selectedCustomer.getCustomerId()));
        customerNameTF.setText(selectedCustomer.getCustomerName());
        addressTF.setText(selectedCustomer.getAddress());
        postalCodeTF.setText(selectedCustomer.getPostalCode());
        phoneTF.setText(selectedCustomer.getPhone());
        countryComboBox.getSelectionModel().select(country);
        fldComboBox.getSelectionModel().select(FirstLevelDivision.lookupFLD(selectedCustomer.getDivisionId()));

    }

    /**
     * Deletes customer from database.
     * @param actionEvent delete button clicked
     */
    public void onDelete(ActionEvent actionEvent) throws SQLException {
        try {
            Customer customerToBeDeleted = customersTblView.getSelectionModel().getSelectedItem();

            for (Appointment a : Appointment.getAllAppointments()) {
                if (a.getCustomerId() == customerToBeDeleted.getCustomerId()) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setContentText("All appointments for " + customerToBeDeleted.getCustomerName() + " must be deleted prior to deleting customer record.");
                    alert.showAndWait();
                    return;
                }
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Customer: " + customerToBeDeleted.getCustomerName() + " has been deleted.");
            customerToBeDeleted.deleteCustomer();
            alert.showAndWait();
        }
        catch (NullPointerException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please select a customer to delete.");
            alert.showAndWait();
            return;
        }
    }

    /**
     * Adds/updates customer in database.
     * @param actionEvent save button clicked.
     */
    public void onSave(ActionEvent actionEvent) throws SQLException, InterruptedException {
        exceptionsLbl.setText(null);
        int customerId = 0;
        try {
            customerId = Integer.parseInt(customerIdTF.getText());
        } catch (NumberFormatException f) { }

        try {
            String customerName = customerNameTF.getText();
            String address = addressTF.getText();
            String postalCode = postalCodeTF.getText();
            String phone = phoneTF.getText();
            int divisionId = fldComboBox.getSelectionModel().getSelectedItem().getDivisionId();

            if (modify) {
                if (customerName.isEmpty() || address.isEmpty() || postalCode.isEmpty() || phone.isEmpty()) {
                    throw new NullPointerException();
                }
                Customer customerToBeUpdated = new Customer(customerName, address, postalCode, phone, divisionId);
                customerToBeUpdated.updateCustomer(customerId);
            } else {
                Customer.addCustomer(new Customer(customerName, address, postalCode, phone, divisionId));
            }
        }
        catch (NullPointerException e) {
            exceptionsLbl.setText("Missing required fields");
            return;
        }

        viewMode();
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
     * Filters first-level-divisions combo box based on country selected.
     * @param actionEvent country selected from combo box
     */
    public void onCountrySelected(ActionEvent actionEvent) throws SQLException {

        try {
            fldComboBox.getSelectionModel().select(null);
            Country selectedCountry = countryComboBox.getSelectionModel().getSelectedItem();
            fldComboBox.setItems(FirstLevelDivision.filteredFLD(selectedCountry.getId()));
        }
        catch (NullPointerException e) { }

    }

    /**
     * Goes to reports page.
     * @param actionEvent reports button clicked
     */
    public void onReportsSelected(ActionEvent actionEvent) throws IOException {

        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        Parent scene = FXMLLoader.load(getClass().getResource("/View/Reports.fxml"));

        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * Goes to appointments page.
     * @param actionEvent appointments button clicked
     */
    public void onAppointmentSelected(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        Parent scene = FXMLLoader.load(getClass().getResource("/View/Appointments.fxml"));

        stage.setScene(new Scene(scene));
        stage.show();
    }
}
