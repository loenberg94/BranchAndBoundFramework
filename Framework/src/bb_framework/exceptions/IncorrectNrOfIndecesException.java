package bb_framework.exceptions;

public class IncorrectNrOfIndecesException extends Exception {
    public IncorrectNrOfIndecesException(int expectedNumber, int receivedNumber, String type){
        super(String.format("%s expects %d indices but received %d", type, expectedNumber, receivedNumber));
    }
}
