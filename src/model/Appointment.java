package model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class Appointment {
    
    private final IntegerProperty appointmentID;
    private final IntegerProperty customerID;
    private final StringProperty title;
    private final StringProperty description;
    private final StringProperty location;
    private final StringProperty contact;
    private final StringProperty start;
    private final StringProperty end;
    
    
    public Appointment(int customerID, String title, String description, String location, String contact, String start, String end){
        
        this.appointmentID = new SimpleIntegerProperty(-1);
        this.customerID = new SimpleIntegerProperty(customerID);
        this.title = new SimpleStringProperty(title);
        this.description = new SimpleStringProperty(description);
        this.location = new SimpleStringProperty(location);
        this.contact = new SimpleStringProperty(contact);
        this.start = new SimpleStringProperty(start);
        this.end = new SimpleStringProperty(end);
        
    }
    
    public Appointment(int appointmentID, int customerID, String title, String description, String location, String contact, String start, String end){
        
        this.appointmentID = new SimpleIntegerProperty(appointmentID);
        this.customerID = new SimpleIntegerProperty(customerID);
        this.title = new SimpleStringProperty(title);
        this.description = new SimpleStringProperty(description);
        this.location = new SimpleStringProperty(location);
        this.contact = new SimpleStringProperty(contact);
        this.start = new SimpleStringProperty(start);
        this.end = new SimpleStringProperty(end);
        
    }

    /*
        Appointment ID Method
    */
    public IntegerProperty appointmentID() {
        return appointmentID;
    }
    
    public int getAppointmentID() {
        return appointmentID.get();
    }

    public void setAppointmentID(int appointmentID) {
        this.appointmentID.set(appointmentID);
    }

    /*
        Customer ID Methods
    */
    public IntegerProperty customerID() {
        return customerID;
    }
    
    public int getCustomerID() {
        return customerID.get();
    }

    public void setCustomerID(int customerID) {
        this.customerID.set(customerID);
    }

    /*
        Title Methods
    */
    public StringProperty title() {
        return title;
    }
    
    public String getTitle() {
        return title.get();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    /*
        Description Methods
    */
    public StringProperty description() {
        return description;
    }
    
    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    /*
        Location Methods
    */
    public StringProperty location() {
        return location;
    }
    
    public String getLocation() {
        return location.get();
    }

    public void setLocation(String location) {
        this.location.set(location);
    }

    /*
        Contact Methods
    */
    public StringProperty contact() {
        return contact;
    }
    
    public String getContact() {
        return contact.get();
    }

    public void setContact(String contact) {
        this.contact.set(contact);
    }

    /*
        Start Date methods
    */
    public StringProperty start() {
        return start;
    }
    
    public String getStart() {
        return start.get();
    }

    public void setStart(String start) {
        this.start.set(start);
    }

    /*
        End Date methods
    */
    public StringProperty end() {
        return end;
    }
    
    public String getEnd() {
        return end.get();
    }

    public void setEnd(String end) {
        this.end.set(end);
    }
    
}
