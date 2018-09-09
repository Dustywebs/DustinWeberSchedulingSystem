package view;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Appointment;
import model.ConsultantSchedule;
import model.Customer;
import model.SchedulingDAO;
import validation.AppointmentValidation;

public class AppointmentLayoutController {

    @FXML
    private Label scheduleAppointmentTitle;

    @FXML
    private Label scheduleAppointmentCustomerLabel;

    @FXML
    private Label scheduleAppointmentCustomerValueDisplay;

    @FXML
    private Label scheduleAppointmentTitleLabel;

    @FXML
    private Label scheduleAppointmentDescriptionLabel;

    @FXML
    private Label scheduleAppointmentLocationLabel;

    @FXML
    private Label scheduleAppointmentContactLabel;

    @FXML
    private Label scheduleAppointmentStartDateLabel;

    @FXML
    private Label scheduleAppointmentEndTimeLabel;

    @FXML
    private Label scheduleAppointmentEndDateLabel;

    @FXML
    private TextField scheduleAppointmentTitleInput;

    @FXML
    private TextField scheduleAppointmentDescriptionInput;

    @FXML
    private TextField scheduleAppointmentLocationInput;

    @FXML
    private TextField scheduleAppointmentContactInput;

    @FXML
    private DatePicker scheduleAppointmentStartDateSelector;

    @FXML
    private TextField scheduleAppointmentStartTimeInput;

    @FXML
    private DatePicker scheduleAppointmentEndDateSelector;

    @FXML
    private TextField scheduleAppointmentEndTimeInput;

    @FXML
    private Button appointmentAddButton;

    @FXML
    private Button appointmentExitButton;

    @FXML
    private ObservableList<Appointment> dataAppointments;

    private Appointment modifyAppointment;
    private Customer selectedCustomer;

    // Formatter for date selectors
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
    DateTimeFormatter formTime = DateTimeFormatter.ofPattern("MM-dd-yyyy kk:mm");

    public void setAppointmentTableData(ObservableList<Appointment> data) {
        this.dataAppointments = data;

    }

    public void setAppointmentData(Appointment appointment) {

        modifyAppointment = appointment;

        scheduleAppointmentTitle.setText("Modify Appointment");
        appointmentAddButton.setText("Update");

        SchedulingDAO dao = new SchedulingDAO();
        Customer cust = dao.retrieveCustomer(appointment.getCustomerID());
        setCustomerData(cust);

        // Get all of the input field data
        scheduleAppointmentTitleInput.setText(appointment.getTitle());
        scheduleAppointmentDescriptionInput.setText(appointment.getDescription());
        scheduleAppointmentLocationInput.setText(appointment.getLocation());
        scheduleAppointmentContactInput.setText(appointment.getContact());

        String[] startDateTime = appointment.getStart().split(" ", 2);
        scheduleAppointmentStartTimeInput.setText(startDateTime[1]);
        scheduleAppointmentStartDateSelector.setValue(LocalDate.parse(startDateTime[0], formatter));

        String[] endDateTime = appointment.getEnd().split(" ", 2);
        scheduleAppointmentEndTimeInput.setText(endDateTime[1]);
        scheduleAppointmentEndDateSelector.setValue(LocalDate.parse(endDateTime[0], formatter));

    }

    public void setCustomerData(Customer cust) {
        selectedCustomer = cust;
        scheduleAppointmentCustomerValueDisplay.setText(selectedCustomer.getCustomerName());
    }

    @FXML
    public void initialize() {

        appointmentAddButton.setOnAction((event) -> {
            // Get all of the inputed field data
            String appointmentTitle = scheduleAppointmentTitleInput.getText();
            String appointmentDescription = scheduleAppointmentDescriptionInput.getText();
            String appointmentLocation = scheduleAppointmentLocationInput.getText();
            String appointmentContact = scheduleAppointmentContactInput.getText();

            String appointmentStartDate;
            String appointmentEndDate;

            try {
                appointmentStartDate = scheduleAppointmentStartDateSelector.getValue().format(formatter);
                String appointmentStartTime = scheduleAppointmentStartTimeInput.getText();
                appointmentEndDate = scheduleAppointmentEndDateSelector.getValue().format(formatter);
                String appointmentEndTime = scheduleAppointmentEndTimeInput.getText();

                validateValues(appointmentTitle, appointmentDescription, appointmentLocation, appointmentContact, appointmentStartTime, appointmentEndTime);

                String startCombined = appointmentStartDate + " " + appointmentStartTime;

                String endCombined = appointmentEndDate + " " + appointmentEndTime;
                validateTimes(startCombined, endCombined);

                // If we are not modifying a appointment, add a new appointment
                if (modifyAppointment == null) {

                    Appointment addedAppointment = new Appointment(selectedCustomer.getCustomerID(),
                            appointmentTitle,
                            appointmentDescription,
                            appointmentLocation,
                            appointmentContact,
                            startCombined,
                            endCombined);

                    SchedulingDAO dao = new SchedulingDAO();

                    int appointmentID = dao.createAppointment(addedAppointment);
                    addedAppointment.setAppointmentID(appointmentID);

                    dataAppointments.add(addedAppointment);

                    Node node = (Node) event.getSource();
                    Stage stage = (Stage) node.getScene().getWindow();
                    stage.close();

                } else {

                    Appointment addedAppointment = new Appointment(modifyAppointment.getAppointmentID(),
                            selectedCustomer.getCustomerID(),
                            appointmentTitle,
                            appointmentDescription,
                            appointmentLocation,
                            appointmentContact,
                            startCombined,
                            endCombined);

                    // Updating Appointments
                    SchedulingDAO dao = new SchedulingDAO();
                    Boolean successfulUpdate = dao.updateAppointment(addedAppointment);

                    if (successfulUpdate) {
                        System.out.println("Passed Update");
                        // Removing originial appointment
                        // Adding the new one
                        dataAppointments.remove(modifyAppointment);
                        dataAppointments.add(addedAppointment);

                        Node node = (Node) event.getSource();
                        Stage stage = (Stage) node.getScene().getWindow();
                        stage.close();

                    } else {
                        System.out.println("");
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Update Error");
                        alert.setHeaderText("Update Failed");
                        alert.setContentText("Something went wrong during update. Please check database connectivity.");
                        alert.showAndWait();
                    }

                }

            } catch (AppointmentValidation av) {
                System.out.println(av.getMessage());
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Validation Error");
                alert.setHeaderText("Incorrect Information");
                alert.setContentText(av.getMessage());

                alert.showAndWait();
            } catch (NullPointerException npe) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Validation Error");
                alert.setHeaderText("Incorrect Information");
                alert.setContentText("Start Date / End Date cannot be null");

                alert.showAndWait();
            }

        });

        appointmentExitButton.setOnAction((event) -> {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("Cancel Appointment");
            alert.setContentText("Are you sure you want to cancel?");

            alert.showAndWait()
                    .filter(response -> response == ButtonType.OK)
                    .ifPresent(response -> {
                        Stage stage = (Stage) appointmentExitButton.getScene().getWindow();
                        stage.close();
                    });

        });

    }

    // Validates inputted values to check for empty fields
    private void validateValues(String appointmentTitle,
            String appointmentDescription,
            String appointmentLocation,
            String appointmentContact,
            String appointmentStartTime,
            String appointmentEndTime) throws AppointmentValidation {

        if (appointmentTitle.isEmpty()) {
            throw new AppointmentValidation("Appointment's title cannot be empty");
        }
        if (appointmentDescription.isEmpty()) {
            throw new AppointmentValidation("Appointment's description cannot be empty");
        }
        if (appointmentLocation.isEmpty()) {
            throw new AppointmentValidation("Appointment's location cannot be empty");
        }
        if (appointmentContact.isEmpty()) {
            throw new AppointmentValidation("Appointment's contact cannot be empty");
        }
        if (appointmentStartTime.isEmpty()) {
            throw new AppointmentValidation("Appointment's start time cannot be empty");
        }
        if (appointmentEndTime.isEmpty()) {
            throw new AppointmentValidation("Appointment's end time cannot be empty");
        }
    }

    // Validates the times
    // Time must be within buisness hours
    // End date can't be before start date
    // Check to ensure no conflicting appointments
    private void validateTimes(String start, String end) throws AppointmentValidation {

        SchedulingDAO dao = new SchedulingDAO();

        // Get Start and End dates, and convert them to LocalDateTime
        LocalDateTime ldtStart = LocalDateTime.parse(start, formTime);
        LocalDateTime ldtEnd = LocalDateTime.parse(end, formTime);
        //System.out.println("Start Hour: " + ldtStart.getHour());
        //System.out.println("End Hour: " + ldtEnd.getHour());

        // Assume 9-5 is buisness hours
        if (ldtStart.getHour() >= 9 && ldtStart.getHour() <= 17) {
            //System.out.println("ldtStart is between 9 to 5: " + ldtStart.getHour());
        } else {
            throw new AppointmentValidation("Incorrect start date, appointments must be during business hours of 9:00-17:00 local time. You entered: " + start);
        }

        if (ldtEnd.getHour() >= 9 && ldtEnd.getHour() <= 17) {
            //System.out.println("ldtEnd is between 9 to 5: " + ldtEnd.getHour());
        } else {
            throw new AppointmentValidation("Incorrect end date, appointments must be during business hours of 9:00-17:00 local time. You entered: " + end);
        }

        if (ldtEnd.isBefore(ldtStart)) {
            throw new AppointmentValidation("End date cannot be before start date!");
        }

        // Check if start and end are conflicts with another appointment
        ArrayList<ConsultantSchedule> allCs = dao.consultantsSchedule();

        int checkForModify = -1;

        if (modifyAppointment != null) {
            checkForModify = modifyAppointment.getAppointmentID();
        }

        for (ConsultantSchedule cs : allCs) {

            if (cs.getId() == checkForModify) {
                // Do nothing, as this is the record we are modifying
            } else {
                // Check is new start date is between start and end of this appointment
                LocalDateTime csStart = LocalDateTime.parse(cs.getStart(), formTime);
                LocalDateTime csEnd = LocalDateTime.parse(cs.getEnd(), formTime);
                System.out.println("csStart: " + cs.getStart());
                System.out.println("csEnd: " + cs.getEnd());
                if(ldtStart.isEqual(csStart)){
                    throw new AppointmentValidation("Scheduling Conflict: " + cs.getTitle() + " Starts at " + cs.getStart() + " til " + cs.getEnd());
                }
                
                if ( ldtStart.isAfter(csStart) && ldtStart.isBefore(csEnd)) {
                    throw new AppointmentValidation("Scheduling Conflict: " + cs.getTitle() + " Starts at " + cs.getStart() + " til " + cs.getEnd());
                }
            }

        }

    }

}
