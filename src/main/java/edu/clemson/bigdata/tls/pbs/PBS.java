package edu.clemson.bigdata.tls.pbs;

import io.vertx.core.json.JsonObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteStreamHandler;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.lang.StringUtils;

import edu.clemson.bigdata.tls.pbs.model.Job;
import edu.clemson.bigdata.tls.pbs.model.Node;
import edu.clemson.bigdata.tls.pbs.model.Queue;
import edu.clemson.bigdata.tls.pbs.parser.NodeXmlParser;
import edu.clemson.bigdata.tls.pbs.parser.ParseException;
import edu.clemson.bigdata.tls.pbs.parser.QstatJobsParser;
import edu.clemson.bigdata.tls.pbs.parser.QstatQueuesParser;
import edu.clemson.bigdata.tls.pbs.utils.CommandOutput;
import edu.clemson.bigdata.tls.pbs.utils.PBSException;

/**
 * PBS Java API.
 *
 */
public class PBS {

//    /**
//     * PBS getNodesSnapshot command.
//     * <p>
//     * Get information about the cluster nodes.
//     *
//     * @return list of nodes
//     * @throws PBSException if an error communicating with the PBS occurs
//     */
//    public static List<Node> getNodesSnapshot() {
//        return getNodesSnapshot(null);
//    }

    // pbsnodes snapshot
    static JsonObject pbsNodesInfo;


    /**
     * Get PBS nodes information.
     * <p>
     * Get information about the cluster nodes.
     *
     * @throws IOException if an error communicating with the PBS occurs
     */
    public static void getNodesSnapshot() throws IOException, InterruptedException {
        final List<Node> nodes;

        // construct query command: pbsnodes -a -F json
        final CommandLine cmdLine = new CommandLine(COMMAND_QNODES);
        cmdLine.addArgument("-a");
        cmdLine.addArgument("-F");
        cmdLine.addArgument("json");

        // execute command pbsnodes
        final OutputStream out = new ByteArrayOutputStream();
        final OutputStream err = new ByteArrayOutputStream();
        DefaultExecuteResultHandler resultHandler;
        resultHandler = execute(cmdLine, null, out, err);
        resultHandler.waitFor(DEFAULT_TIMEOUT);

        final int exitValue = resultHandler.getExitValue();
        LOGGER.info("getNodesSnapshot exit value: " + exitValue);

        // update pbsnodes snapshot
        pbsNodesInfo = new JsonObject(out.toString());
    }

    public List<String> getJobNodes(String jobID) {
        return null;
    }

    /**
     * PBS qstat command.
     * <p>
     * Equivalent to qstat -Q -f [name]
     *
     * @return list of queues
     */
    public static List<Queue> qstatQueues() {
        return qstatQueues(null);
    }

    /**
     * PBS qstat command.
     * <p>
     * Equivalent to qstat -Q -f [name]
     *
     * @param name queue name
     * @return list of queues
     */
    public static List<Queue> qstatQueues(String name) {
        final CommandLine cmdLine = new CommandLine(COMMAND_QSTAT);
        cmdLine.addArgument(PARAMETER_FULL_STATUS);
        cmdLine.addArgument(PARAMETER_QUEUE);
        if (StringUtils.isNotBlank(name)) {
            cmdLine.addArgument(name);
        }

        final OutputStream out = new ByteArrayOutputStream();
        final OutputStream err = new ByteArrayOutputStream();

        DefaultExecuteResultHandler resultHandler;
        try {
            resultHandler = execute(cmdLine, null, out, err);
            resultHandler.waitFor(DEFAULT_TIMEOUT);
        } catch (ExecuteException e) {
            throw new PBSException("Failed to execute qstat command: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new PBSException("Failed to execute qstat command: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            throw new PBSException("Failed to execute qstat command: " + e.getMessage(), e);
        }

        final int exitValue = resultHandler.getExitValue();
        LOGGER.info("qstat exit value: " + exitValue);

        final List<Queue> queues;
        try {
            queues = QSTAT_QUEUES_PARSER.parse(out.toString());
        } catch (ParseException pe) {
            throw new PBSException("Failed to parse qstat queues output: " + pe.getMessage(), pe);
        }

        return (queues == null ? new ArrayList<Queue>(0) : queues);
    }

    /**
     * PBS qstat command.
     * <p>
     * Equivalent to qstat -f
     *
     * @return list of jobs
     */
    public static List<Job> qstat() {
        return qstat((String) null);
    }

    /**
     * PBS qstat command.
     * <p>
     * Equivalent to qstat -f [queue_name]
     *
     * @param queue PBS {@link Queue}
     * @return list of jobs
     */
    public static List<Job> qstat(Queue queue) {
        return qstat(queue.getName());
    }

    /**
     * PBS qstat command.
     * <p>
     * Equivalent to qstat -f [job_name]
     *
     * @param job the PBS Job
     * @return list of jobs
     */
    public static List<Job> qstat(Job job) {
        return qstat(job.getName());
    }

    /**
     * PBS qstat command.
     * <p>
     * Equivalent to qstat -f [param]
     *
     * @param name job name
     * @return list of jobs
     */
    public static List<Job> qstat(String name) {
        final CommandLine cmdLine = new CommandLine(COMMAND_QSTAT);
        cmdLine.addArgument(PARAMETER_FULL_STATUS);
        if (StringUtils.isNotBlank(name)) {
            cmdLine.addArgument(name);
        }

        final OutputStream out = new ByteArrayOutputStream();
        final OutputStream err = new ByteArrayOutputStream();

        DefaultExecuteResultHandler resultHandler;
        try {
            resultHandler = execute(cmdLine, null, out, err);
            resultHandler.waitFor(DEFAULT_TIMEOUT);
        } catch (ExecuteException e) {
            throw new PBSException("Failed to execute qstat command: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new PBSException("Failed to execute qstat command: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            throw new PBSException("Failed to execute qstat command: " + e.getMessage(), e);
        }

        final int exitValue = resultHandler.getExitValue();
        LOGGER.info("qstat exit value: " + exitValue);

        final List<Job> jobs;
        try {
            jobs = QSTAT_JOBS_PARSER.parse(out.toString());
        } catch (ParseException pe) {
            throw new PBSException("Failed to parse qstat jobs output: " + pe.getMessage(), pe);
        }

        return (jobs == null ? new ArrayList<Job>(0) : jobs);
    }

    /**
     * PBS qstat command for Array Jobs
     * <p>
     * Equivalent to qstat -f -t [param]
     *
     * @param name job name
     * @return list of jobs
     */
    public static List<Job> qstatArrayJob(String name) {
        final CommandLine cmdLine = new CommandLine(COMMAND_QSTAT);
        cmdLine.addArgument(PARAMETER_FULL_STATUS);
        cmdLine.addArgument(PARAMETER_ARRAY_JOB_STATUS);
        if (StringUtils.isNotBlank(name)) {
            cmdLine.addArgument(name);
        }

        final OutputStream out = new ByteArrayOutputStream();
        final OutputStream err = new ByteArrayOutputStream();

        DefaultExecuteResultHandler resultHandler;
        try {
            resultHandler = execute(cmdLine, out, err);
            resultHandler.waitFor(DEFAULT_TIMEOUT);
        } catch (ExecuteException e) {
            throw new PBSException("Failed to execute qstat command: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new PBSException("Failed to execute qstat command: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            throw new PBSException("Failed to execute qstat command: " + e.getMessage(), e);
        }

        final int exitValue = resultHandler.getExitValue();
        LOGGER.info("qstat exit value: " + exitValue);

        final List<Job> jobs;
        try {
            jobs = QSTAT_JOBS_PARSER.parse(out.toString());
        } catch (ParseException pe) {
            throw new PBSException("Failed to parse qstat jobs output: " + pe.getMessage(), pe);
        }

        return (jobs == null ? new ArrayList<Job>(0) : jobs);
    }

    /**
     * PBS qdel command.
     * <p>
     * Equivalent to qdel [param]
     *
     * @param jobId job id
     */
    public static void qdel(String jobId) {
        final CommandLine cmdLine = new CommandLine(COMMAND_QDEL);
        cmdLine.addArgument(jobId);

        final OutputStream out = new ByteArrayOutputStream();
        final OutputStream err = new ByteArrayOutputStream();

        DefaultExecuteResultHandler resultHandler;
        try {
            resultHandler = execute(cmdLine, null, out, err);
            resultHandler.waitFor(DEFAULT_TIMEOUT);
        } catch (ExecuteException e) {
            throw new PBSException("Failed to execute qdel command: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new PBSException("Failed to execute qdel command: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            throw new PBSException("Failed to execute qdel command: " + e.getMessage(), e);
        }

        final int exitValue = resultHandler.getExitValue();
        LOGGER.info("qdel exit value: " + exitValue);

        if (exitValue != 0)
            throw new PBSException("Failed to delete job " + jobId + ". Error output: " + err.toString());
    }

    /**
     * PBS qsub command.
     * <p>
     * Equivalent to qsub [param]
     *
     * @param input job input file
     * @return job id
     */
    public static String qsub(String input) {
        final CommandLine cmdLine = new CommandLine(COMMAND_QSUB);
        cmdLine.addArgument(input);

        final OutputStream out = new ByteArrayOutputStream();
        final OutputStream err = new ByteArrayOutputStream();

        DefaultExecuteResultHandler resultHandler;
        try {
            resultHandler = execute(cmdLine, null, out, err);
            resultHandler.waitFor(DEFAULT_TIMEOUT);
        } catch (ExecuteException e) {
            throw new PBSException("Failed to execute qsub command: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new PBSException("Failed to execute qsub command: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            throw new PBSException("Failed to execute qsub command: " + e.getMessage(), e);
        }

        final int exitValue = resultHandler.getExitValue();
        LOGGER.info("qsub exit value: " + exitValue);
        LOGGER.fine("qsub output: " + out.toString());

        if (exitValue != 0)
            throw new PBSException("Failed to submit job script " + input + ". Error output: " + err.toString());

        String jobId = out.toString();
        return jobId.trim();
    }

    /**
     * <p>
     * PBS qsub command with arguments resource overrides
     * </p>
     *
     * <p>
     * Equivalent to qsub [param] -l [resource_name=value,resource_name=value]]
     * </p>
     *
     * @param input job input file
     * @param resourceOverrides variable number of resources to override
     * @return job id
     */
    public static String qsub(String input, String... resourceOverrides) {
        final CommandLine cmdLine = new CommandLine(COMMAND_QSUB);
        cmdLine.addArgument(PARAMETER_RESOURCE_OVERRIDE_STATUS);
        String resourceOverrideArgument = StringUtils.join(resourceOverrides, ",");
        cmdLine.addArgument(resourceOverrideArgument);
        cmdLine.addArgument(input);

        final OutputStream out = new ByteArrayOutputStream();
        final OutputStream err = new ByteArrayOutputStream();

        DefaultExecuteResultHandler resultHandler;
        try {
            resultHandler = execute(cmdLine, out, err);
            resultHandler.waitFor(DEFAULT_TIMEOUT);
        } catch (ExecuteException e) {
            throw new PBSException("Failed to execute qsub command: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new PBSException("Failed to execute qsub command: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            throw new PBSException("Failed to execute qsub command: " + e.getMessage(), e);
        }

        final int exitValue = resultHandler.getExitValue();
        LOGGER.info("qsub exit value: " + exitValue);
        LOGGER.fine("qsub output: " + out.toString());

        if (exitValue != 0)
            throw new PBSException("Failed to submit job script " + input + ". Error output: " + err.toString());

        String jobId = out.toString();
        return jobId.trim();
    }

    /**
     * PBS qsub command for an Array Job with Specific PBS_ARRAY_IDs to submit
     * <p>
     * Equivalent to qsub -t 1,2,3 [param]
     *
     * @param input job input file
     * @param pbsArrayIDs list of specified PBS indices
     * @return job id of array job
     */
    public static String qsubArrayJob(String input, List<Integer> pbsArrayIDs) {
        final CommandLine cmdLine = new CommandLine(COMMAND_QSUB);
        cmdLine.addArgument(PARAMETER_ARRAY_JOB_STATUS);
        String listArgument = StringUtils.join(pbsArrayIDs, ",");
        cmdLine.addArgument(listArgument);
        cmdLine.addArgument(input);

        final OutputStream out = new ByteArrayOutputStream();
        final OutputStream err = new ByteArrayOutputStream();

        DefaultExecuteResultHandler resultHandler;
        try {
            resultHandler = execute(cmdLine, out, err);
            resultHandler.waitFor(DEFAULT_TIMEOUT);
        } catch (ExecuteException e) {
            throw new PBSException("Failed to execute qsub command: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new PBSException("Failed to execute qsub command: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            throw new PBSException("Failed to execute qsub command: " + e.getMessage(), e);
        }

        final int exitValue = resultHandler.getExitValue();
        LOGGER.info("qsub exit value: " + exitValue);
        LOGGER.fine("qsub output: " + out.toString());

        if (exitValue != 0)
            throw new PBSException("Failed to submit job script " + input + ". Error output: " + err.toString());

        String jobId = out.toString();
        return jobId.trim();
    }

    /**
     * PBS qsub command.
     * <p>
     * Equivalent to qsub [param]
     *
     * @param inputs job input file
     * @param environment environment variables
     * @return job id
     */
    public static String qsub(String[] inputs, Map<String, String> environment) {
        final CommandLine cmdLine = new CommandLine(COMMAND_QSUB);
        for (int i = 0; i < inputs.length; ++i) {
            cmdLine.addArgument(inputs[i]);
        }

        final OutputStream out = new ByteArrayOutputStream();
        final OutputStream err = new ByteArrayOutputStream();

        DefaultExecuteResultHandler resultHandler;
        try {
            resultHandler = execute(cmdLine, environment, out, err);
            resultHandler.waitFor(DEFAULT_TIMEOUT);
        } catch (ExecuteException e) {
            throw new PBSException("Failed to execute qsub command: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new PBSException("Failed to execute qsub command: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            throw new PBSException("Failed to execute qsub command: " + e.getMessage(), e);
        }

        final int exitValue = resultHandler.getExitValue();
        LOGGER.info("qsub exit value: " + exitValue);
        LOGGER.fine("qsub output: " + out.toString());

        if (exitValue != 0) {
            throw new PBSException("Failed to submit job script with command line '" + cmdLine.toString()
            + "'. Error output: " + err.toString());
        }

        String jobId = out.toString();
        return jobId.trim();
    }

    /**
     * PBS qsub command for an Array Job with Specific PBS_ARRAY_IDs to submit, and resource overrides
     * <p>
     * Equivalent to qsub -t 1,2,3 -l [resource_name=value,resource_name=value] [param]
     *
     * @param input job input file
     * @param pbsArrayIDs of specified PBS indices
     * @param resourceOverrides list of resource overrides
     * @return job id of array job
     */
    public static String qsubArrayJob(String input, List<Integer> pbsArrayIDs, String... resourceOverrides) {
        final CommandLine cmdLine = new CommandLine(COMMAND_QSUB);
        cmdLine.addArgument(PARAMETER_ARRAY_JOB_STATUS);
        String listArgument = StringUtils.join(pbsArrayIDs, ",");
        cmdLine.addArgument(listArgument);
        cmdLine.addArgument(PARAMETER_RESOURCE_OVERRIDE_STATUS);
        String resourceOverrideArgument = StringUtils.join(resourceOverrides, ",");
        cmdLine.addArgument(resourceOverrideArgument);
        cmdLine.addArgument(input);

        final OutputStream out = new ByteArrayOutputStream();
        final OutputStream err = new ByteArrayOutputStream();

        DefaultExecuteResultHandler resultHandler;
        try {
            resultHandler = execute(cmdLine, out, err);
            resultHandler.waitFor(DEFAULT_TIMEOUT);
        } catch (ExecuteException e) {
            throw new PBSException("Failed to execute qsub command: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new PBSException("Failed to execute qsub command: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            throw new PBSException("Failed to execute qsub command: " + e.getMessage(), e);
        }

        final int exitValue = resultHandler.getExitValue();
        LOGGER.info("qsub exit value: " + exitValue);
        LOGGER.fine("qsub output: " + out.toString());

        if (exitValue != 0)
            throw new PBSException("Failed to submit job script " + input + ". Error output: " + err.toString());

        String jobId = out.toString();
        return jobId.trim();
    }

    /**
     * PBS qsub command for an Array Job with Specific PBS_ARRAY_IDs to submit
     * <p>
     * Equivalent to qsub -t 5-20 [param]
     *
     * @param input job input file
     * @param beginIndex beginning of index range
     * @param endIndex end of index range
     * @return job id of array job
     */
    public static String qsubArrayJob(String input, int beginIndex, int endIndex) {
        final CommandLine cmdLine = new CommandLine(COMMAND_QSUB);
        cmdLine.addArgument(PARAMETER_ARRAY_JOB_STATUS);
        String rangeArgument = beginIndex + "-" + endIndex;
        cmdLine.addArgument(rangeArgument);
        cmdLine.addArgument(input);

        final OutputStream out = new ByteArrayOutputStream();
        final OutputStream err = new ByteArrayOutputStream();

        DefaultExecuteResultHandler resultHandler;
        try {
            resultHandler = execute(cmdLine, out, err);
            resultHandler.waitFor(DEFAULT_TIMEOUT);
        } catch (ExecuteException e) {
            throw new PBSException("Failed to execute qsub command: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new PBSException("Failed to execute qsub command: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            throw new PBSException("Failed to execute qsub command: " + e.getMessage(), e);
        }

        final int exitValue = resultHandler.getExitValue();
        LOGGER.info("qsub exit value: " + exitValue);
        LOGGER.fine("qsub output: " + out.toString());

        if (exitValue != 0)
            throw new PBSException("Failed to submit job script " + input + ". Error output: " + err.toString());

        String jobId = out.toString();
        return jobId.trim();
    }

    /**
     * PBS qsub command for an Array Job with Specific PBS_ARRAY_IDs to submit AND a range to submit
     * <p>
     * Equivalent to qsub -t 1,2,3,5-20 [param]
     *
     * @param input job input file
     * @param pbsArrayIDs list of specified indices
     * @param beginIndex beginning of index range
     * @param endIndex end of index range
     * @return job id of array job
     */
    public static String qsubArrayJob(String input, List<Integer> pbsArrayIDs, int beginIndex, int endIndex) {
        final CommandLine cmdLine = new CommandLine(COMMAND_QSUB);
        cmdLine.addArgument(PARAMETER_ARRAY_JOB_STATUS);
        String rangeArgument = beginIndex + "-" + endIndex;
        String listArgument = StringUtils.join(pbsArrayIDs, ",");
        String combinedArgument = listArgument + "," + rangeArgument;
        cmdLine.addArgument(combinedArgument);
        cmdLine.addArgument(input);

        final OutputStream out = new ByteArrayOutputStream();
        final OutputStream err = new ByteArrayOutputStream();

        DefaultExecuteResultHandler resultHandler;
        try {
            resultHandler = execute(cmdLine, out, err);
            resultHandler.waitFor(DEFAULT_TIMEOUT);
        } catch (ExecuteException e) {
            throw new PBSException("Failed to execute qsub command: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new PBSException("Failed to execute qsub command: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            throw new PBSException("Failed to execute qsub command: " + e.getMessage(), e);
        }

        final int exitValue = resultHandler.getExitValue();
        LOGGER.info("qsub exit value: " + exitValue);
        LOGGER.fine("qsub output: " + out.toString());

        if (exitValue != 0)
            throw new PBSException("Failed to submit job script " + input + ". Error output: " + err.toString());

        String jobId = out.toString();
        return jobId.trim();
    }

    /**
     * <p>
     * PBS tracejob command.
     * </p>
     * <p>
     * Equivalent to tracejob -n [numberOfDays] [jobId]
     * </p>
     *
     * @param jobId job id
     * @param numberOfDays number of days to look for the job
     * @return tracejob output
     */
    public static CommandOutput traceJob(String jobId, int numberOfDays) {
        return traceJob(jobId, numberOfDays, true /* quiet */);
    }

    /**
     * PBS tracejob command.
     * <p>
     * Equivalent to tracejob -n [numberOfDays] [jobId]
     *
     * @param jobId job id
     * @param numberOfDays number of days to look for the job
     * @param quiet quiet mode flag
     * @return tracejob output
     */
    public static CommandOutput traceJob(String jobId, int numberOfDays, boolean quiet) {
        final CommandLine cmdLine = new CommandLine(COMMAND_TRACEJOB);
        cmdLine.addArgument(PARAMETER_NUMBER_OF_DAYS);
        cmdLine.addArgument(Integer.toString(numberOfDays));
        if (quiet) {
            cmdLine.addArgument(PARAMETER_QUIET_MODE);
        }
        cmdLine.addArgument(jobId);

        final OutputStream out = new ByteArrayOutputStream();
        final OutputStream err = new ByteArrayOutputStream();

        DefaultExecuteResultHandler resultHandler;
        try {
            resultHandler = execute(cmdLine, null, out, err);
            resultHandler.waitFor(DEFAULT_TIMEOUT);
        } catch (ExecuteException e) {
            throw new PBSException("Failed to execute tracejob command: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new PBSException("Failed to execute tracejob command: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            throw new PBSException("Failed to execute tracejob command: " + e.getMessage(), e);
        }

        final int exitValue = resultHandler.getExitValue();
        LOGGER.info("tracejob exit value: " + exitValue);
        LOGGER.fine("tracejob output: " + out.toString());

        return new CommandOutput(out.toString(), err.toString());
    }

    /*
     * ------------------------------ Utility methods ------------------------------
     */
    /**
     * Executes a PBS command.
     *
     * @param cmdLine command
     * @param out output stream
     * @param err err stream
     * @return execute handler
     * @throws ExecuteException
     * @throws IOException
     */
    static DefaultExecuteResultHandler execute(CommandLine cmdLine, OutputStream out, OutputStream err)
            throws ExecuteException, IOException {
        return execute(cmdLine, Collections.<String, String> emptyMap(), out, err);
    }

    /**
     * Executes a PBS command.
     *
     * @param cmdLine command
     * @param environment env vars
     * @param out output stream
     * @param err err stream
     * @return execute handler
     * @throws ExecuteException if there is an error executing a command
     * @throws IOException in case of an IO problem
     */
    static DefaultExecuteResultHandler execute(CommandLine cmdLine, Map<String, String> environment, OutputStream out,
            OutputStream err) throws ExecuteException, IOException {
        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
        ExecuteStreamHandler streamHandler = new PumpStreamHandler(out, err);
        DefaultExecutor executor = new DefaultExecutor();
        executor.setExitValue(0);
        executor.setStreamHandler(streamHandler);
        if (environment != null) {
            executor.execute(cmdLine, environment, resultHandler);
        } else {
            executor.execute(cmdLine, resultHandler);
        }
        return resultHandler;
    }

    private static final Logger LOGGER = Logger.getLogger(PBS.class.getName());

    private static final String COMMAND_QNODES = "getNodesSnapshot";
    private static final String COMMAND_QSTAT = "qstat";
    private static final String COMMAND_QDEL = "qdel";
    private static final String COMMAND_QSUB = "qsub";
    private static final String COMMAND_TRACEJOB = "tracejob";
    // qstat
    private static final String PARAMETER_FULL_STATUS = "-f";
    private static final String PARAMETER_ARRAY_JOB_STATUS = "-t";
    private static final String PARAMETER_RESOURCE_OVERRIDE_STATUS = "-l";
    private static final String PARAMETER_QUEUE = "-Q";
    // tracejob
    private static final String PARAMETER_NUMBER_OF_DAYS = "-n";
    private static final String PARAMETER_QUIET_MODE = "-q";

    private static final NodeXmlParser NODE_XML_PARSER = new NodeXmlParser();
    private static final QstatQueuesParser QSTAT_QUEUES_PARSER = new QstatQueuesParser();
    private static final QstatJobsParser QSTAT_JOBS_PARSER = new QstatJobsParser();

    /**
     * Default time-out for process execution.
     */
    private static final int DEFAULT_TIMEOUT = 60000;

}
