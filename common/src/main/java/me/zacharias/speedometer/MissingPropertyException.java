package me.zacharias.speedometer;

public class MissingPropertyException extends Exception {
    public MissingPropertyException(String field) {
        super("Missing Speedometer config field: " + field);
    }
}
