package view;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import model.AppointmentsPerMonth;
import model.ConsultantSchedule;
import model.SchedulingDAO;

public class ReportingLayoutController implements Initializable {

    @FXML
    private Button queryAppointmentsPerMonthButton;

    @FXML
    private Button queryConsultantScheduleButton;

    @FXML
    private Button queryCustomersPerCountryButton;

    @FXML
    private ScrollPane reportingScrollPane;

    @FXML
    private TextFlow reportingTextFlow;

    @FXML
    private Button reportingCloseButton;

    private SchedulingDAO dao;

    @FXML
    void handleReportClose(ActionEvent event) throws IOException {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Cancel Reporting");
        alert.setContentText("Are you sure you want to cancel?");
        
        alert.showAndWait().filter(response -> response == ButtonType.OK)
                .ifPresent(response -> {
                    Stage stage = (Stage) reportingCloseButton.getScene().getWindow();
                    stage.close();
                });
    }

    @FXML
    void handleReportConsultantSchedule(ActionEvent event) {

        reportingTextFlow.getChildren().clear();

        Text initText = new Text("Report generated:\n*************************\n");
        reportingTextFlow.getChildren().add(initText);

        // Returns number of appointment types per month
        ArrayList<ConsultantSchedule> allApm = dao.schedulePerConsultant();

        //reportingTextFlow = new TextFlow();
        String currentConsultant = "";

        for (ConsultantSchedule cs : allApm) {

            if (cs.getConsultant().equals(currentConsultant)) {

                // add new type under the current month
                String strAppointment = "\n" + cs.getTitle() + "\nStarts: " + cs.getStart() + "\nEnds: " + cs.getEnd() + "\n";
                Text typeText = new Text(strAppointment);
                reportingTextFlow.getChildren().add(typeText);
            } else {
                // Add new month to Textflow
                // Then add this type
                currentConsultant = cs.getConsultant();
                Text consultantText = new Text(currentConsultant + "\n");
                consultantText.setFont(Font.font("Helvetica", 30));
                consultantText.setUnderline(true);
                reportingTextFlow.getChildren().add(consultantText);
                String strAppointment = cs.getTitle() + "\nStarts: " + cs.getStart() + "\nEnds: " + cs.getEnd() + "\n";
                Text typeText = new Text(strAppointment);
                reportingTextFlow.getChildren().add(typeText);
            }

        }

    }

    @FXML
    void handleReportCustomersPerCountry(ActionEvent event) {

        reportingTextFlow.getChildren().clear();
        
        Text initText = new Text("Report generated:\n*************************\n");
        reportingTextFlow.getChildren().add(initText);

        String currentCountry = "";

        // Returns number of appointment types per month
        HashMap<String, ArrayList<String>> allCusts = dao.customersPerCountry();

        for (Map.Entry<String, ArrayList<String>> entry : allCusts.entrySet()) {

            // Add current Country
            currentCountry = entry.getKey();
            Text countryText = new Text(currentCountry + "\n");
            countryText.setFont(Font.font("Helvetica", 30));
            countryText.setUnderline(true);
            reportingTextFlow.getChildren().add(countryText);

            for (String str : entry.getValue()) {

                Text custText = new Text(str + "\n");
                reportingTextFlow.getChildren().add(custText);
            }

        }

    }

    @FXML
    void handleReportNumAppointmentTypes(ActionEvent event) {

        reportingTextFlow.getChildren().clear();

        Text initText = new Text("Report generated:\n*************************\n");
        reportingTextFlow.getChildren().add(initText);

        // Returns number of appointment types per month
        ArrayList<AppointmentsPerMonth> allApm = dao.numAppointmentsPerMonth();

        //reportingTextFlow = new TextFlow();
        int currentMonth = -1;

        for (AppointmentsPerMonth apm : allApm) {

            if (apm.getMonth() == currentMonth) {

                // add new type under the current month
                String strType = apm.getNumberOfTypes() + "\t - " + apm.getType() + "\n";
                Text typeText = new Text(strType);
                reportingTextFlow.getChildren().add(typeText);
            } else {
                // Add new month to Textflow
                // Then add this type
                currentMonth = apm.getMonth();
                String strMonth = lookupMonth(currentMonth) + "\n";
                Text monText = new Text(strMonth);
                monText.setFont(Font.font("Helvetica", 30));
                monText.setUnderline(true);
                reportingTextFlow.getChildren().add(monText);
                String strType = apm.getNumberOfTypes() + "\t - " + apm.getType() + "\n";
                Text typeText = new Text(strType);
                reportingTextFlow.getChildren().add(typeText);
            }

        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dao = new SchedulingDAO();
    }

    private String lookupMonth(int month) {
        switch (month) {
            case 1:
                return "January";
            case 2:
                return "February";
            case 3:
                return "March";
            case 4:
                return "April";
            case 5:
                return "May";
            case 6:
                return "June";
            case 7:
                return "July";
            case 8:
                return "August";
            case 9:
                return "September";
            case 10:
                return "October";
            case 11:
                return "November";
            case 12:
                return "December";
            default:
                return null;
        }
    }

}
