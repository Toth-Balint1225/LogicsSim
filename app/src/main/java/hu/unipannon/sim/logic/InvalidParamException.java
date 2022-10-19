package hu.unipannon.sim.logic;


/**
 * Custom exception that is meant to signal when an invalid string id is 
 * supplied to a function.
 * @author Tóth Bálint
 */
public class InvalidParamException extends Exception {

    
    private String param;

    public InvalidParamException(String param) {
        this.param = param;
    }

    /** 
     * Overriden Exception method for a broader use.
     * @return the message with the invalid argument
     */
    @Override 
    public String getMessage() {
        return "Invalid parameter: " + param;
    }

    /**
     * Stack trace printing util function.
     */
    @Override
    public void printStackTrace() {
        System.err.println(getMessage());
        super.printStackTrace();
    }
}
