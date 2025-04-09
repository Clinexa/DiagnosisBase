package com.clinexa.basediagnosis.exceptions;

/**
 * This exception is an unchecked exception, which should be
 * used by all diagnoses systems (classes which implement
 * {@link com.clinexa.basediagnosis.DiagnosesSystem} interface).
 *
 * @since 0.1-dev.1
 * @author Nikita S.
 */
public class DiagnosesSystemException extends RuntimeException {

    /**
     * Constructs a new exception with the specified cause.
     *
     * @param e exception which will be stored as a cause for this exception or
     *          (undesirable) null for unknown cause.
     * @see <a href="https://docs.oracle.com/en/java/javase/23/docs/api/java.base/java/lang/RuntimeException.html#constructor-summary">java.lang.RuntimeException constructors</a>
     */
    public DiagnosesSystemException(Exception e) {
        super(e);
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message.
     * @see <a href="https://docs.oracle.com/en/java/javase/23/docs/api/java.base/java/lang/RuntimeException.html#constructor-summary">java.lang.RuntimeException constructors</a>
     */
    public DiagnosesSystemException(String message) {
        super(message);
    }
}
