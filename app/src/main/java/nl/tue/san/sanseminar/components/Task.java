package nl.tue.san.sanseminar.components;

/**
 * Created by Maurice on 5-1-2017.
 */

public class Task {


    private int offset;
    private int period;
    private int deadline;
    private int computation;
    private int priority;
    private int threshold;
    private int color;

    private String name;


    /**
     * @param name The name of the task.
     * @param color The color with which to represent this job. Only the red, green and blue channels of this color are used.
     * @param offset The timestamp at which the first job instance of the task starts.
     * @param period The amount of time between two subsequent job instances of the task.
     * @param deadline The deadline of a job relative to the starting point of that job.
     * @param computation The computation time required to fulfil the job.
     * @param priority The priority given to jobs of this task.
     * @param threshold The preemption threshold for this job.
     */
    private Task(String name, int color, int offset, int period, int deadline, int computation, int priority, int threshold) {
        this.setName(name);
        this.setColor(color);
        this.setOffset(offset);
        this.setPeriod(period);
        this.setDeadline(deadline);
        this.setComputation(computation);
        this.setPriority(priority);
        this.setThreshold(threshold);

    }
    /**
     * Create a Task whose first occurrence is at time 0.
     * @param name The name of the task.
     * @param color The color with which to represent this job. Only the red, green and blue channels of this color are used.
     * @param period The amount of time between two subsequent job instances of the task.
     * @param deadline The deadline of a job relative to the starting point of that job.
     * @param computation The computation time required to fulfil the job.
     * @param priority The priority given to jobs of this task.
     * @param threshold The preemption threshold for this job.
     * @return The created task
     */
    // "static" constructor
    public static Task createImmediateTask(String name, int color, int period, int deadline, int computation, int priority, int threshold){
        return new Task(name, color, 0, period, deadline, computation, priority, threshold);
    }

    /**
     * Create a Task whose first occurrence is at time 0 that has no preemption threshold. This means
     * that only tasks with a higher priority than the given priority may preempt this task in the
     * case of preemptive scheduling.
     * @param name The name of the task.
     * @param color The color with which to represent this job. Only the red, green and blue channels of this color are used.
     * @param period The amount of time between two subsequent job instances of the task.
     * @param deadline The deadline of a job relative to the starting point of that job.
     * @param computation The computation time required to fulfil the job.
     * @param priority The priority given to jobs of this task.
     * @return The created task
     */
    // "static" constructor
    public static Task createImmediateTaskWithoutThreshold(String name, int color, int period, int deadline, int computation, int priority){
        return new Task(name, color, 0, period, deadline, computation, priority, NO_PREEMPTION_THRESHOLD);

    }

    /**
     * Create a Task without a preemption threshold. This means that only tasks with a higher
     * priority than the given priority may preempt this task in the case of preemptive scheduling.
     * @param name The name of the task.
     * @param color The color with which to represent this job. Only the red, green and blue channels of this color are used.
     * @param offset The timestamp at which the first job instance of the task starts.
     * @param period The amount of time between two subsequent job instances of the task.
     * @param deadline The deadline of a job relative to the starting point of that job.
     * @param computation The computation time required to fulfil the job.
     * @param priority The priority given to jobs of this task.
     * @return The created task
     */
    // "static" constructor
    public static Task createTaskWthoutThreshold(String name, int color, int period, int deadline, int computation, int priority, int offset){
        return new Task(name, color, offset, period, deadline, computation, priority, NO_PREEMPTION_THRESHOLD);
    }

    /**
     * Value for the preemption threshold that indicates that no preemption threshold is specified.
     * In case the threshold is given this value, the minimal interrupt priority is equal to
     * {@code (this.priority + 1)}.
     */
    private static final int NO_PREEMPTION_THRESHOLD = -1;

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public int getDeadline() {
        return deadline;
    }

    public void setDeadline(int deadline) {
        this.deadline = deadline;
    }

    public int getComputation() {
        return computation;
    }

    public void setComputation(int computation) {
        this.computation = computation;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
