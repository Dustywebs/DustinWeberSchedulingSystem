package model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class Customer {
    
    private final IntegerProperty customerID;
    private final StringProperty customerName;
    private final IntegerProperty addressID;
    private final StringProperty address1;
    private final StringProperty address2;
    private final StringProperty city;
    private final StringProperty zipCode;
    private final StringProperty country;
    private final StringProperty phone;
    private final IntegerProperty active;
    
    public Customer(int customerID, int addressID, String customerName, String address1, String address2, String city, String zipCode, String country, String phone, int active){
        
        this.customerID = new SimpleIntegerProperty(customerID);
        this.addressID = new SimpleIntegerProperty(addressID);
        this.customerName = new SimpleStringProperty(customerName);
        this.address1 = new SimpleStringProperty(address1);
        this.address2 = new SimpleStringProperty(address2);
        this.city = new SimpleStringProperty(city);
        this.zipCode = new SimpleStringProperty(zipCode);
        this.country = new SimpleStringProperty(country);
        this.phone = new SimpleStringProperty(phone);
        this.active = new SimpleIntegerProperty(active);
    }
    
    public Customer(String customerName, String address1, String address2, String city, String zipCode, String country, String phone, int active){
        
        this.customerID = new SimpleIntegerProperty();
        this.addressID = new SimpleIntegerProperty();
        this.customerName = new SimpleStringProperty(customerName);
        this.address1 = new SimpleStringProperty(address1);
        this.address2 = new SimpleStringProperty(address2);
        this.city = new SimpleStringProperty(city);
        this.zipCode = new SimpleStringProperty(zipCode);
        this.country = new SimpleStringProperty(country);
        this.phone = new SimpleStringProperty(phone);
        this.active = new SimpleIntegerProperty(active);
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
        Customer ID Methods
    */
    public IntegerProperty addressID() {
        return addressID;
    }
    
    public int getAddressID() {
        return addressID.get();
    }

    public void setAddressID(int addressID) {
        this.addressID.set(addressID);
    }

    /*
        Customer Name Methods
    */
    public StringProperty customerName() {
        return customerName;
    }
    
    public String getCustomerName() {
        return customerName.get();
    }

    public void setCustomerName(String customerName) {
        this.customerName.set(customerName);
    }

    /*
        Customer Address1 Methods
    */
    public StringProperty address1() {
        return address1;
    }
    
    public String getAddress1() {
        return address1.get();
    }

    public void setAddress1(String address1) {
        this.address1.set(address1);
    }

    /*
        Customer Address2 Methods
    */
    public StringProperty address2() {
        return address2;
    }
    
    public String getAddress2() {
        return address2.get();
    }

    public void setAddress2(String address2) {
        this.address2.set(address2);
    }

    /*
        Customer City Methods
    */
    public StringProperty city() {
        return city;
    }
    
    public String getCity() {
        return city.get();
    }

    public void setCity(String city) {
        this.city.set(city);
    }

    /*
        Customer ZipCode Methods
    */
    public StringProperty zipCode() {
        return zipCode;
    }
    
    public String getZipCode() {
        return zipCode.get();
    }

    public void setZipCode(String zipCode) {
        this.zipCode.set(zipCode);
    }

    /*
        Customer Country Methods
    */
    public StringProperty country() {
        return country;
    }
    
    public String getCountry() {
        return country.get();
    }

    public void setCountry(String country) {
        this.country.set(country);
    }

    /*
        Customer Phone Methods
    */
    public StringProperty phone() {
        return phone;
    }
    
    public String getPhone() {
        return phone.get();
    }

    public void setPhone(String phone) {
        this.phone.set(phone);
    }

    /*
        Customer Active Methods
    */
    public IntegerProperty active() {
        return active;
    }
    
    public int getActive() {
        return active.get();
    }

    public void setActive(int active) {
        this.active.set(active);
    }
    
    
}
