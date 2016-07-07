package edu.clemson.bigdata.tls.pbs.model;

/**
 * Queue state. Valid values are:
 * <ul>
 * <li>free</li>
 * <li>offline</li>
 * <li>down</li>
 * <li>reserve</li>
 * <li>job-exclusive</li>
 * <li>job-sharing</li>
 * <li>busy</li>
 * <li>time-shared</li>
 * <li>state-unknown</li>
 * <li>unknown</li>
 * </ul>
 *
 */
public enum QueueState {

    FREE("free"), OFFLINE("offline"), DOWN("down"), RESERVE("reserve"), JOB_EXCLUSIVE("job-exclusive"), JOB_SHARING(
            "job-sharing"), BUSY("busy"), TIME_SHARED("time-shared"), STATE_UNKNOWN("state-unknown"), UNKNOWN(
                    "unknown");

    private final String state;

    QueueState(String state) {
        this.state = state;
    }

    public String getState() {
        return this.state;
    }

    public static QueueState fromString(String state) {
        if ("free".equals(state))
            return FREE;
        if ("down".equals(state))
            return DOWN;
        return UNKNOWN;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return this.state;
    }

}
