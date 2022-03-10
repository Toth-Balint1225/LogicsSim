package hu.uni_pannon.sim;


public class InvalidParamException extends Exception {

    private String param;

    public InvalidParamException(String param) {
        this.param = param;
    }

    public String getMessage() {
        return "Invalid parameter: " + param;
    }

    public void printStackTrace() {
        System.err.println(getMessage());
        super.printStackTrace();
    }
}
