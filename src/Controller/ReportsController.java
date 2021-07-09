package Controller;

import Model.Appointment;
import Model.Contact;
import Model.Customer;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Class used for controlling events on the reports page.
 */
public class ReportsController implements Initializable {

    @FXML
    private TextArea reportTextArea;

    /**
     * Initializes the reports page.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) { }

    /**
     * Generates customer appointment report by month and type
     * @param actionEvent customer report button clicked
     */
    public void onCustomerReport(ActionEvent actionEvent) throws SQLException {
        reportTextArea.setText(null);
        StringBuilder report = Appointment.generateReport();
        reportTextArea.setText(report.toString());
    }

    /**
     * Generates contact report w/ info on appointments per contact.
     * @param actionEvent contact report button clicked
     */
    public void onContactsReport(ActionEvent actionEvent) {
        reportTextArea.setText(null);
        StringBuilder report = Contact.generateReport();
        reportTextArea.setText(report.toString());
    }

    /**
     * Generates report containing info for recently updated customers and/or appointments.
     * @param actionEvent recently updated items button clicked
     */
    public void onRecentlyUpdated(ActionEvent actionEvent) {
        reportTextArea.setText(null);
        ObservableList<Appointment> appts = Appointment.getRecentlyUpdatedAppointments();
        ObservableList<Customer> customers = Customer.getRecentlyUpdatedCustomer();

        StringBuilder report = new StringBuilder();

        try {
            report.append("Recently updated appointments:\n");
            for (Appointment a : appts) {
                report.append("Appointment ID: " + a.getApptId() + " was updated at " + a.getLastUpdated() + " by " + a.getLastUpdatedBy() + "\n");
            }
            report.append("\nRecently updated customers:\n");
            for (Customer c : customers) {
                report.append("Customer ID: " + c.getCustomerId() + " was updated at " + c.getLastUpdate() + " by " + c.getLastUpdatedBy() + "\n");
            }
            reportTextArea.setText(report.toString());
        }
        catch (NullPointerException e) {
            reportTextArea.setText("No recent changes!");
        }
    }

    /**
     * Returns to customers page.
     * @param actionEvent customers button clicked
     */
    public void backToCustomers(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        Parent scene = FXMLLoader.load(getClass().getResource("/View/Customers.fxml"));

        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * Returns to appointments page.
     * @param actionEvent appointments button clicked
     */
    public void backToAppts(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        Parent scene = FXMLLoader.load(getClass().getResource("/View/Appointments.fxml"));

        stage.setScene(new Scene(scene));
        stage.show();
    }
}