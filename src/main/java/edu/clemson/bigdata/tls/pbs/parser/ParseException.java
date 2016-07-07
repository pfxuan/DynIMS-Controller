package edu.clemson.bigdata.tls.pbs.parser;

/**
 * Exception threw by PBS parsers.
 *
 */
public class ParseException extends Exception {

    private static final long serialVersionUID = 1832085151390299726L;

    /**
     * Default constructor.
     */
    public ParseException() {
    }

    /**
     * Constructor with message.
     *
     * @param message exception message
     */
    public ParseException(String message) {
        super(message);
    }

    /**
     * Constructor with case.
     *
     * @param cause cause
     */
    public ParseException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor with message and cause.
     *
     * @param message message
     * @param cause cause
     */
    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }

}
