package edu.clemson.bigdata.tls.pbs.utils;

/**
 * Output of a command.
 *
 */
public final class CommandOutput {

    private final String output;
    private final String error;

    public CommandOutput(String output, String error) {
        super();
        this.output = output;
        this.error = error;
    }

    public String getOutput() {
        return output;
    }

    public String getError() {
        return error;
    }

    @Override
    public String toString() {
        return "CommandOutput [output=" + output + ", error=" + error + "]";
    }

}
