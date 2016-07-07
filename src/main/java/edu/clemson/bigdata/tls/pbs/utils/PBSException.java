package edu.clemson.bigdata.tls.pbs.utils;

/**
 * PBS Exception.
 *
 */
public class PBSException extends RuntimeException {

    private static final long serialVersionUID = -308538480475052665L;

    /**
     * Defaut constructor.
     */
    public PBSException() {
    }

    /**
     * Constructor with message.
     *
     * @param message exception message
     */
    public PBSException(String message) {
        super(message);
    }

    /**
     * Constructor with cause.
     *
     * @param cause exception cause
     */
    public PBSException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor with message and cause.
     *
     * @param message exception message
     * @param cause exception cause
     */
    public PBSException(String message, Throwable cause) {
        super(message, cause);
    }

}
