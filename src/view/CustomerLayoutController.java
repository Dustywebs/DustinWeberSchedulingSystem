package view;

import java.io.IOException;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Customer;
import model.SchedulingDAO;
import validation.CustomerValidation;

public class CustomerLayoutController {

    @FXML
    private Label customerLayoutTitle;

    @FXML
    private Label customerLayoutCustomerNameLabel;

    @FXML
    private Label customerLayoutAddress1Label;

    @FXML
    private Label customerLayoutAddress2Label;

    @FXML
    private Label customerLayoutCityLabel;

    @FXML
    private Label customerLayoutZipLabel;

    @FXML
    private Label customerLayoutCountryLabel;

    @FXML
    private Label customerLayoutPhoneLabel;

    @FXML
    private TextField customerLayoutCustomerNameInput;

    @FXML
    private TextField customerLayoutAddress1Input;

    @FXML
    private TextField customerLayoutAddress2Input;

    @FXML
    private TextField customerLayoutCityInput;

    @FXML
    private TextField customerLayoutZipInput;

    @FXML
    private TextField customerLayoutCountryInput;

    @FXML
    private TextField customerLayoutPhoneInput;

    @FXML
    private CheckBox customerLayoutActiveCheckbox;

    @FXML
    private Label customerLayoutActiveLabel;

    @FXML
    private Button customerLayoutSaveButton;

    @FXML
    private Button customerLayoutCancelButton;

    @FXML
    private ObservableList<Customer> dataCustomer;

    private Customer modifyCustomer;

    public void setCustomerData(Customer cust) {

        modifyCustomer = cust;

        customerLayoutTitle.setText("Modify Customer");
        customerLayoutSaveButton.setText("Update");

        // Get all of the input field data
        customerLayoutCustomerNameInput.setText(cust.getCustomerName());
        customerLayoutAddress1Input.setText(cust.getAddress1());
        customerLayoutAddress2Input.setText(cust.getAddress2());
        customerLayoutCityInput.setText(cust.getCity());
        customerLayoutZipInput.setText(cust.getZipCode());
        customerLayoutCountryInput.setText(cust.getCountry());
        customerLayoutPhoneInput.setText(cust.getPhone());

        if (cust.getActive() == 1) {
            customerLayoutActiveCheckbox.setSelected(true);
        } else {
            customerLayoutActiveCheckbox.setSelected(false);
        }

    }

    public void setCustomerTableData(ObservableList<Customer> data) {
        this.dataCustomer = data;

    }

    public void handleButtonCancel(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Cancel Customer");
        alert.setContentText("Are you sure you want to cancel?");

        alert.showAndWait()
                .filter(response -> response == ButtonType.OK)
                .ifPresent(response -> {
                    Stage stage = (Stage) customerLayoutCancelButton.getScene().getWindow();
                    stage.close();
                });
    }

    @FXML
    void addCustomer(ActionEvent event) {

        // Get all of the input field data
        String customerName = customerLayoutCustomerNameInput.getText();
        String customerAddress1 = customerLayoutAddress1Input.getText();
        String customerAddress2 = customerLayoutAddress2Input.getText();
        String customerCity = customerLayoutCityInput.getText();
        String customerZip = customerLayoutZipInput.getText();
        String customerCountry = customerLayoutCountryInput.getText();
        String customerPhone = customerLayoutPhoneInput.getText();

        int customerActive;

        if (customerLayoutActiveCheckbox.isSelected()) {
            customerActive = 1;
        } else {
            customerActive = 0;
        }

        try {
            validate(customerName, customerAddress1, customerAddress2, customerCity, customerZip, customerCountry, customerPhone);

            // If we are not modifying a customer, add a new customer
            if (modifyCustomer == null) {

                Customer addedCustomer = new Customer(customerName,
                        customerAddress1,
                        customerAddress2,
                        customerCity,
                        customerZip,
                        customerCountry,
                        customerPhone,
                        customerActive);

                SchedulingDAO dao = new SchedulingDAO();

                // This needs to return two ints, one for CustomerID, and one for AddressID
                int[] custIDs = dao.createCustomer(addedCustomer);
                addedCustomer.setCustomerID(custIDs[0]);
                addedCustomer.setAddressID(custIDs[1]);

                dataCustomer.add(addedCustomer);

                Node node = (Node) event.getSource();
                Stage stage = (Stage) node.getScene().getWindow();
                stage.close();

            } else {

                Customer addedCustomer = new Customer(modifyCustomer.getCustomerID(),
                        modifyCustomer.getAddressID(),
                        customerName,
                        customerAddress1,
                        customerAddress2,
                        customerCity,
                        customerZip,
                        customerCountry,
                        customerPhone,
                        customerActive);

                // Updating Customers
                SchedulingDAO dao = new SchedulingDAO();
                Boolean successfulUpdate = dao.updateCustomer(addedCustomer);

                if (successfulUpdate) {
                    // Removing originial customers
                    // Adding new customer
                    dataCustomer.remove(modifyCustomer);
                    dataCustomer.add(addedCustomer);

                    Node node = (Node) event.getSource();
                    Stage stage = (Stage) node.getScene().getWindow();
                    stage.close();
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Update Error");
                    alert.setHeaderText("Update Failed");
                    alert.setContentText("Something went wrong during update. Please check database connectivity.");

                    alert.showAndWait();
                }
            }

        } catch (CustomerValidation cv) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Validation Error");
            alert.setHeaderText("Missing Information");
            alert.setContentText(cv.getMessage());

            alert.showAndWait();
        }

    }

    // Validates inputted data to check for not null
    private void validate(String customerName,
            String customerAddress1,
            String customerAddress2,
            String customerCity,
            String customerZip,
            String customerCountry,
            String customerPhone) throws CustomerValidation {

        if (customerName.isEmpty()) {
            throw new CustomerValidation("Customer's Name cannot be empty");
        }
        if (customerAddress1.isEmpty()) {
            throw new CustomerValidation("Customer's Address cannot be empty");
        }
        if (customerAddress2.isEmpty()) {
            throw new CustomerValidation("Customer's Address2 cannot be empty");
        }
        if (customerCity.isEmpty()) {
            throw new CustomerValidation("Customer's City cannot be empty");
        }
        if (customerZip.isEmpty()) {
            throw new CustomerValidation("Customer's Zip Code cannot be empty");
        }
        if (customerCountry.isEmpty()) {
            throw new CustomerValidation("Customer's Country cannot be empty");
        }
        if (customerPhone.isEmpty()) {
            throw new CustomerValidation("Customer's Phone Number cannot be empty");
        }

    }

    @FXML
    public void initialize() {
        
    }

}
