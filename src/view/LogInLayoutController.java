package view;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.SchedulingDAO;

public class LogInLayoutController {

    @FXML
    private Label loginTitle;

    @FXML
    private Label loginUserNameLabel;

    @FXML
    private TextField loginUsernameInput;

    @FXML
    private Label loginPasswordLabel;

    @FXML
    private PasswordField loginPasswordInput;

    @FXML
    private Label loginValidationField;

    @FXML
    private Button loginLoginButton;

    @FXML
    private Button loginExitApplication;

    private SchedulingDAO dao;
    private ResourceBundle message;

    @FXML
    void handleExit(ActionEvent event) {

        Stage stage;

        stage = (Stage) loginExitApplication.getScene().getWindow();
        stage.close();

    }

    @FXML
    void handleLogin(ActionEvent event) throws IOException {

        String un = loginUsernameInput.getText();
        String pw = loginPasswordInput.getText();

        // Validates username and password
        if (dao.login(un, pw)) {
            // Write login data to text file
            logFile(un);

            // Set logged in person into dao static fields
            dao.setLoggedInUser(un);

            Stage stage;
            Parent root;
    
            stage = (Stage) loginLoginButton.getScene().getWindow();

            // Opens main application
            FXMLLoader fxmlLoader;
            fxmlLoader = new FXMLLoader(getClass().getResource("MainLayout.fxml"));
            root = (Parent) fxmlLoader.load();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } else {
            // Display incorrect validation
            // In the required language / locale
            loginValidationField.setText(message.getString("validationMessage"));
        }

    }

    public void initialize() {

        dao = new SchedulingDAO();

        loginValidationField.setText("");

        // Set Zone - into the dao file
        ZoneId zid = ZoneId.systemDefault();
        dao.setzLocal(zid);

        // Set Locale to french for different language
        //Locale.setDefault(new Locale("fr", "FR"));
        
        // Depending on locale, get different properties file
        message = ResourceBundle.getBundle("locales/Message");

        // Sets all fields based on locale
        loginLoginButton.setText(message.getString("login"));
        loginExitApplication.setText(message.getString("exit"));
        loginUserNameLabel.setText(message.getString("unLabel"));
        loginPasswordLabel.setText(message.getString("pwLabel"));
        loginTitle.setText(message.getString("title"));

    }

    // Method for logging users entry
    private void logFile(String un) {
        
        File f = new File("c:\\DustinWeberSchedulingSystem\\log.txt");

        // Check if file exists
        if (f.exists()) {
            System.out.println("File existed");
        } else {
            System.out.println("File not found!");
            try {
                f.getParentFile().mkdirs();
                f.createNewFile();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // Add new entry to it
        FileWriter fw;
        BufferedWriter bw;
        PrintWriter out;
        try {
            fw = new FileWriter(f, true);
            bw = new BufferedWriter(fw);
            out = new PrintWriter(bw);
            
            Instant now = Instant.now();
            LocalDateTime ldtNow = LocalDateTime.ofInstant(now, dao.getzLocal());
            
            out.println("User: " + un + " Logged in at: " + ldtNow.toString());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
