package bb_framework.exceptions;

public class IncorrectCoefficientTypeException extends Exception {
    public IncorrectCoefficientTypeException(String expected, String received){
        super(String.format("Wrong Coefficient type received, expected: %s, received: %s", expected, received));
    }
}
