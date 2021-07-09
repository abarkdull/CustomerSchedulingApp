package Controller;

import Model.Appointment;
import Model.Customer;
import Model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Class used for controlling events on the login page.
 */
public class LoginController implements Initializable {

    @FXML
    private Button loginButtonLbl;

    @FXML
    private Label usernameLbl;

    @FXML
    private Label passwordLbl;

    @FXML
    private TextField usernameTF;

    @FXML
    private TextField passwordTF;

    @FXML
    private Label systemLanguageLbl;

    @FXML
    private Label loginLbl;

    @FXML
    private Button cancelLbl;

    @FXML
    private Label exceptionsLbl;

    /**
     * Initializes login page in user's default system language.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        ResourceBundle rb = ResourceBundle.getBundle("rbBase", Locale.getDefault());
        String localZoneId = ZoneId.systemDefault().toString();


        if (Locale.getDefault().getLanguage().equals("fr")) {
            systemLanguageLbl.setText(rb.getString("Timezone") + ": " + localZoneId);
            passwordLbl.setText((rb.getString("password")));
            cancelLbl.setText(rb.getString("cancel"));
            loginLbl.setText(rb.getString("login"));
            usernameLbl.setText(rb.getString("username"));
            loginButtonLbl.setText(rb.getString("login"));
        }
        else {
            systemLanguageLbl.setText("Timezone: " + localZoneId);
        }
    }

    /**
     * Exits the program.
     * @param actionEvent cancel button clicked
     */
    public void onCancel(ActionEvent actionEvent) {
        User.closePrintWriter();
        System.exit(0);
    }

    /**
     * Checks if username/password are valid and goes to appointments page.
     * All login attempts are also logged with this method. An alert is displayed
     * if a user has appointments within 15 minutes of logging in.
     * @param actionEvent login button clicked
     */
    public void onLogin(ActionEvent actionEvent) throws SQLException, IOException {

        StringBuilder exceptions = new StringBuilder();
        String username = usernameTF.getText();
        String password = passwordTF.getText();
        ResourceBundle rb = ResourceBundle.getBundle("rbBase", Locale.getDefault());

        if (username.isEmpty()) {
            if (Locale.getDefault().getLanguage().equals("en")) exceptions.append("Username field is empty\n");
            else exceptions.append(rb.getString("userEmpty") + "\n");
        }
        if (password.isEmpty()) {
            if (Locale.getDefault().getLanguage().equals("en")) exceptions.append("Password field is empty\n");
            else exceptions.append(rb.getString("passwordEmpty") + "\n");
        }

        // If user is found login
        if (User.lookupUser(username, password)) {
            Locale.setDefault(new Locale("en"));
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Parent scene = FXMLLoader.load(getClass().getResource("/View/Appointments.fxml"));

            stage.setScene(new Scene(scene));
            stage.show();

            LocalDateTime logonTime = User.getCurrentUser().getLastLogon();
            for (Appointment a : Appointment.getAllAppointments()) {
                LocalDateTime startTime = a.getStart();
                long timeDifference = ChronoUnit.MINUTES.between(logonTime, startTime);
                int apptUserId = a.getUserId();
                if (Math.abs(timeDifference) <= 15 && timeDifference >= 0 && apptUserId == User.getCurrentUser().getUserId()) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("You (" + User.getCurrentUser().getUsername() + ") have an appointment with " + Customer.lookupCustomer(a.getCustomerId()).getCustomerName() + " in " + Math.abs(timeDifference) + " minutes!");
                    alert.showAndWait();
                    return;
                }
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("You (" + User.getCurrentUser().getUsername() + ") have no upcoming appointments.");
            alert.showAndWait();
        }
        else {
            if (Locale.getDefault().getLanguage().equals("en")) {
                exceptions.append("Username and/or password not valid.");
            }
            else {
                exceptions.append(rb.getString("error"));
            }
        }
        exceptionsLbl.setText(String.valueOf(exceptions));
        return;
    }
}
