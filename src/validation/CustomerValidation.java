package validation;

public class CustomerValidation extends Exception{
    
    public CustomerValidation(){}
    
    public CustomerValidation(String message){
        super(message);
    }
    
}
