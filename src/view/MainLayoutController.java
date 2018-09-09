package view;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Appointment;
import model.ConsultantSchedule;
import model.Customer;
import model.SchedulingDAO;

public class MainLayoutController {

    @FXML
    private Label mainTitle;

    @FXML
    private Label mainCustomersTableTitle;

    @FXML
    private Label mainFilterLabel;

    @FXML
    private TextField mainCustomerFilterTextInput;

    @FXML
    private TableView<Customer> mainCustomersTable;

    @FXML
    private TableColumn<?, ?> mainCustomersTableName;

    @FXML
    private TableColumn<?, ?> mainCustomersTablePhone;

    @FXML
    private TableColumn<?, ?> mainCustomersTableCity;

    @FXML
    private TableColumn<?, ?> mainCustomersTableCountry;

    @FXML
    private Button mainCustomersTableAddButton;

    @FXML
    private Button mainCustomersTableModifyButton;

    @FXML
    private Label mainTimeLabel;

    @FXML
    private Button mainAppointmentsTableAllButton;

    @FXML
    private Button mainAppointmentsTable30DayButton;

    @FXML
    private Button mainAppointmentsTable7DayButton;

    @FXML
    private TableView<Appointment> mainAppointmentsTable;

    @FXML
    private TableColumn<?, ?> mainAppointmentsTableCustomer;

    @FXML
    private TableColumn<?, ?> mainAppointmentsTableTitle;

    @FXML
    private TableColumn<?, ?> mainAppointmentsTableLocation;

    @FXML
    private TableColumn<?, ?> mainAppointmentsTableContact;

    @FXML
    private TableColumn<?, ?> mainAppointmentsTableStart;

    @FXML
    private TableColumn<?, ?> mainAppointmentsTableEnd;

    @FXML
    private Button mainCustomersScheduleAppointmentButton;

    @FXML
    private Button mainCustomersModifyAppointmentButton;

    @FXML
    private Button mainCustomersDeleteAppointmentButton;

    @FXML
    private Button mainLayoutExitButton;

    @FXML
    private Button mainLayoutReportingButton;

    @FXML
    public ObservableList<Customer> dataCustomers = FXCollections.observableArrayList();
    @FXML
    public ObservableList<Appointment> dataAppointments = FXCollections.observableArrayList();

    private SchedulingDAO dao;
    private DateTimeFormatter showFormat = DateTimeFormatter.ofPattern("MM-dd-yyyy kk:mm");

    // Initial Load of Data from the Database
    public MainLayoutController() {

        dao = new SchedulingDAO();

        ArrayList<Customer> initialCustomersPopulation = dao.retrieveAllCustomers();

        for (Customer cust : initialCustomersPopulation) {
            dataCustomers.add(cust);
        }

        ArrayList<Appointment> initialAppointmentsPopulation = dao.retrieveAllAppointments();

        for (Appointment appointment : initialAppointmentsPopulation) {
            dataAppointments.add(appointment);
        }

    }

    @FXML
    public void handleButtonAddCustomer(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        Parent root;

        FXMLLoader fxmlLoader;
        fxmlLoader = new FXMLLoader(getClass().getResource("CustomerLayout.fxml"));
        root = (Parent) fxmlLoader.load();

        CustomerLayoutController custController = fxmlLoader.getController();
        custController.setCustomerTableData(dataCustomers);

        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(mainCustomersTableAddButton.getScene().getWindow());
        stage.showAndWait();

    }

    @FXML
    void handleButtonReporting(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        Parent root;

        FXMLLoader fxmlLoader;
        fxmlLoader = new FXMLLoader(getClass().getResource("ReportingLayout.fxml"));
        root = (Parent) fxmlLoader.load();

        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(mainLayoutReportingButton.getScene().getWindow());
        stage.showAndWait();
    }

    @FXML
    public void handleButtonModifyCustomer(ActionEvent event) throws IOException {

        // Get table selection
        Customer selectedCustomer = mainCustomersTable.getSelectionModel().getSelectedItem();

        // If something is selected
        if (selectedCustomer != null) {

            Stage stage;
            Parent root;

            stage = new Stage();
            FXMLLoader fxmlLoader;

            fxmlLoader = new FXMLLoader(getClass().getResource("CustomerLayout.fxml"));
            root = (Parent) fxmlLoader.load();

            // Pass the customers table, and the customer to modify
            CustomerLayoutController customerLayoutController = fxmlLoader.getController();
            customerLayoutController.setCustomerTableData(dataCustomers);
            customerLayoutController.setCustomerData(selectedCustomer);

            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(mainCustomersTableModifyButton.getScene().getWindow());
            stage.showAndWait();

            mainCustomersTable.getSelectionModel().clearSelection();

        } else {
            // If nothing selected.
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Customer Selected");
            alert.setContentText("Please select a customer in the customers table to modify a customer.");

            alert.showAndWait();
        }

    }

    @FXML
    public void initialize() {

        // Customer Table Columns
        mainCustomersTableName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        mainCustomersTablePhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        mainCustomersTableCity.setCellValueFactory(new PropertyValueFactory<>("city"));
        mainCustomersTableCountry.setCellValueFactory(new PropertyValueFactory<>("country"));

        // Set Table searching
        setCustomersTableSearch();

        // Appointment Table Columns
        mainAppointmentsTableTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        mainAppointmentsTableLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        mainAppointmentsTableStart.setCellValueFactory(new PropertyValueFactory<>("start"));
        mainAppointmentsTableEnd.setCellValueFactory(new PropertyValueFactory<>("end"));

        // Set items to the appointments table
        mainAppointmentsTable.setItems(dataAppointments);

        // Create lambda appointment actions
        createButtonActions();

        // Start local clock
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            Calendar cal = Calendar.getInstance();
            int second = cal.get(Calendar.SECOND);
            int minute = cal.get(Calendar.MINUTE);
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            mainTimeLabel.setText("Local Time: " + hour + ":" + (minute) + ":" + second + " (" + dao.getzLocal().toString() + ")");
        }),
                new KeyFrame(Duration.seconds(1))
        );
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();

        // Check for appointsments in the next 15 minutes
        // Alert users to them
        appointmentAlerts();
    }

    private void appointmentAlerts() {

        // Get all appointments from the current user
        ArrayList<ConsultantSchedule> allCs = dao.consultantsSchedule();

        Instant now = Instant.now();
        Instant now15 = now.plus(15, ChronoUnit.MINUTES);

        LocalDateTime ldtNow = LocalDateTime.ofInstant(now, dao.getzLocal());
        LocalDateTime ldtNow15 = LocalDateTime.ofInstant(now15, dao.getzLocal());

        // Iterate through them, and then alert the user
        for (ConsultantSchedule cs : allCs) {
            // Time set is in local time
            // Convert this to a LocalDateTime
            LocalDateTime ldtStart = LocalDateTime.parse(cs.getStart(), showFormat);

            // If now < start time < now15
            // Start time is between now and 15minutes in the future
            if (ldtStart.isAfter(ldtNow) && ldtStart.isBefore(ldtNow15)) {

                String[] startDateTime = cs.getStart().split(" ", 2);

                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Appointment Reminder");
                alert.setContentText(cs.getTitle() + " with " + cs.getCustomerName() + " starts at " + startDateTime[1]);

                alert.showAndWait().filter(response -> response == ButtonType.OK);

            }
        }

    }

    // Method to enable filtering of the customers table
    private void setCustomersTableSearch() {

        FilteredList<Customer> filteredData = new FilteredList<>(dataCustomers, p -> true);

        // Set the filter Predicate whenever the filter changes.
        mainCustomerFilterTextInput.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(cust -> {
                // If filter text is empty, display all customers.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Change text to lowercase, to match Strings
                String lowerCaseFilter = newValue.toLowerCase();

                // If the Customer name matches, return true
                if (cust.getCustomerName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                // If nothing matched, return false
                return false;
            });
        });

        // Wrap the FilteredList in a SortedList. 
        SortedList<Customer> sortedData = new SortedList<>(filteredData);

        // Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(mainCustomersTable.comparatorProperty());

        // Add sorted (and filtered) data to the table.
        mainCustomersTable.setItems(sortedData);

    }

    // Lambda expressions for handing scheduling appointments
    private void createButtonActions() {

        mainCustomersScheduleAppointmentButton.setOnAction((event) -> {
            Stage stage = new Stage();
            Parent root;

            FXMLLoader fxmlLoader;

            // Get table selection
            Customer selectedCustomer = mainCustomersTable.getSelectionModel().getSelectedItem();

            // If something is selected
            if (selectedCustomer != null) {

                try {

                    fxmlLoader = new FXMLLoader(getClass().getResource("AppointmentLayout.fxml"));
                    root = (Parent) fxmlLoader.load();

                    AppointmentLayoutController appointmentController = fxmlLoader.getController();
                    appointmentController.setAppointmentTableData(dataAppointments);
                    appointmentController.setCustomerData(selectedCustomer);

                    stage.setScene(new Scene(root));
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.initOwner(mainCustomersScheduleAppointmentButton.getScene().getWindow());
                    stage.showAndWait();

                    mainCustomersTable.getSelectionModel().clearSelection();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            } else {
                // If nothing selected.
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("No Selection");
                alert.setHeaderText("No Customer Selected");
                alert.setContentText("Please select a customer in the customers table to create an appointment");

                alert.showAndWait();
            }

        });

        mainCustomersModifyAppointmentButton.setOnAction((event) -> {

            // Get table selection
            Appointment selectedAppointment = mainAppointmentsTable.getSelectionModel().getSelectedItem();

            // If something is selected
            if (selectedAppointment != null) {

                Stage stage;
                Parent root;

                stage = new Stage();
                FXMLLoader fxmlLoader;

                try {

                    fxmlLoader = new FXMLLoader(getClass().getResource("AppointmentLayout.fxml"));
                    root = (Parent) fxmlLoader.load();

                    // Pass the customers table, and the customer to modify
                    AppointmentLayoutController appointmentController = fxmlLoader.getController();
                    appointmentController.setAppointmentTableData(dataAppointments);
                    appointmentController.setAppointmentData(selectedAppointment);

                    stage.setScene(new Scene(root));
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.initOwner(mainCustomersTableModifyButton.getScene().getWindow());
                    stage.showAndWait();

                    mainAppointmentsTable.getSelectionModel().clearSelection();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            } else {
                // If nothing selected.
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("No Selection");
                alert.setHeaderText("No Appointment Selected");
                alert.setContentText("Please select a appointment in the appointments table to modify an appointment.");

                alert.showAndWait();
            }

        });

        mainCustomersDeleteAppointmentButton.setOnAction((event) -> {

            // Get table selection
            Appointment selectedAppointment = mainAppointmentsTable.getSelectionModel().getSelectedItem();

            // If something is selected
            if (selectedAppointment != null) {

                Boolean deleteAppointment = dao.deleteAppointment(selectedAppointment);

                if (deleteAppointment) {
                    dataAppointments.remove(selectedAppointment);
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Delete Failure");
                    alert.setHeaderText("Unable to Delete Appointment");
                    alert.setContentText("Sorry, we were unable to delete appointment: " + selectedAppointment.getTitle());

                    alert.showAndWait();
                }

            } else {
                // If nothing selected.
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("No Selection");
                alert.setHeaderText("No Appointment Selected");
                alert.setContentText("Please select a appointment in the appointments table to modify an appointment.");

                alert.showAndWait();
            }

        });
    }

    // Global variable for lambda in search
    private LocalDate searchFilter;
    private LocalDate currentDate;

    @FXML
    public void handleFilterAppointments(ActionEvent event) throws IOException {

        // Formatter for date selectors
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy kk:mm");

        searchFilter = LocalDate.now();
        currentDate = LocalDate.now();

        if (event.getSource() == mainAppointmentsTable30DayButton) {
            // Filter Appointmentss to 30 days
            searchFilter = searchFilter.plusMonths(1);
        }

        if (event.getSource() == mainAppointmentsTable7DayButton) {
            // Filter Appointmentss to 7 days
            searchFilter = searchFilter.plusWeeks(1);
        }

        FilteredList<Appointment> filteredData = new FilteredList<>(dataAppointments, p -> true);

        // Set the filter Predicate whenever the filter changes.
        filteredData.setPredicate(appointment -> {

            if (event.getSource() == mainAppointmentsTableAllButton) {
                // Reset table back to All Appointments
                return true;
            }

            // If filter text is empty, display all customers.
            LocalDate startDate = LocalDate.parse(appointment.getStart(), formatter);

            if (startDate.isEqual(currentDate)) {
                return true;
            } else {
                // Return true or false, depending on filter
                return (startDate.isBefore(searchFilter) && startDate.isAfter(currentDate));
            }

        });

        // Wrap the FilteredList in a SortedList. 
        SortedList<Appointment> sortedData = new SortedList<>(filteredData);

        // Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(mainAppointmentsTable.comparatorProperty());

        // Add sorted (and filtered) data to the table.
        mainAppointmentsTable.setItems(sortedData);

    }

    @FXML
    public void handleButtonExitApplication(ActionEvent event) throws IOException {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Exit Application");
        alert.setContentText("Are you sure you want to leave?");

        alert.showAndWait().filter(response -> response == ButtonType.OK)
                .ifPresent(response -> {
                    Stage stage = (Stage) mainLayoutExitButton.getScene().getWindow();
                    stage.close();
                });

    }

}
