package model;

public class ConsultantSchedule {
    
    private int id;
    private String title;
    private String customerName;
    private String start;
    private String end;
    private String consultant;
    
    public ConsultantSchedule(int id, String title, String customerName, String start, String end, String consultant){
        this.id = id;
        this.title = title;
        this.customerName = customerName;
        this.start = start;
        this.end = end;
        this.consultant = consultant;
        
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getConsultant() {
        return consultant;
    }

    public void setConsultant(String consultant) {
        this.consultant = consultant;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    
    
}
