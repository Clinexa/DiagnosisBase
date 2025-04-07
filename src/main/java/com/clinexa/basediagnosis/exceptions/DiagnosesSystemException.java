package com.clinexa.basediagnosis.exceptions;

public class DiagnosesSystemException extends RuntimeException {
    public DiagnosesSystemException(Exception e) {
        super(e);
    }

    public DiagnosesSystemException(String message) {
        super(message);
    }
}
