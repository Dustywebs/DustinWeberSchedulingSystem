package model;

public class AppointmentsPerMonth {
    
    private int numberOfTypes;
    private String type;
    private int month;
    
    public AppointmentsPerMonth(int num, String type, int month){
        this.numberOfTypes = num;
        this.type = type;
        this.month = month;
    }

    public int getNumberOfTypes() {
        return numberOfTypes;
    }

    public void setNumberOfTypes(int numberOfTypes) {
        this.numberOfTypes = numberOfTypes;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }
    
    
    
}
