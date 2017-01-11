package nl.tue.san.sanseminar.components.tasks;

import android.graphics.Color;

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

    private final String name;


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
    public Task(String name, int color, int offset, int period, int deadline, int computation, int priority, int threshold) {
        this.name = name;
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
    public static Task createTaskWithoutThreshold(String name, int color, int period, int deadline, int computation, int priority, int offset){
        return new Task(name, color, offset, period, deadline, computation, priority, NO_PREEMPTION_THRESHOLD);
    }

    /**
     * Value for the preemption threshold that indicates that no preemption threshold is specified.
     * In case the threshold is given this value, the minimal interrupt priority is equal to
     * {@code (this.priority + 1)}.
     */
    public static final int NO_PREEMPTION_THRESHOLD = -1;

    /**
     * Indicates whether a Preemption threshold has been specified for this task.
     * @return Whether a preemption threshold has been specified for this task.
     */
    public boolean hasPreemptionThreshold(){
        return this.threshold >= 0;
    }

    /**
     * Get the minimal priority a task must have (inclusive) to be able to interrupt the execution of this task.
     * @return The minimal priority a task must have (inclusive) to be able to interrupt this task.
     */
    public int getMinimalPreemptionPriority(){
        return hasPreemptionThreshold() ? threshold : (priority + 1);
    }

    public int getOffset() {
        return offset;
    }

    /**
     * Set the timestamp at which the first job instance of the task starts.
     * @param offset The timestamp at which the first job instance of the task starts. Must be at least 0.
     * @throws IllegalArgumentException If the condition on the argument is not met.
     */
    public void setOffset(int offset) {
        assertPositive(offset, "Offset");
        this.offset = offset;
    }

    public int getPeriod() {
        return period;
    }

    /**
     * Set the amount of time between two subsequent job instances of the task.
     * @param period The amount of time between two subsequent job instances of the task. Must be
     *               strictly greater than 0.
     * @throws IllegalArgumentException If the condition on the argument is not met.
     */
    public void setPeriod(int period) {
        assertStrictlyPositive(period, "Period");
        this.period = period;
    }

    public int getDeadline() {
        return deadline;
    }

    /**
     * Set the deadline of a job relative to the starting point of that job.
     * @param deadline The deadline of a job relative to the starting point of that job. Must be
     *                 strictly greater than 0.
     * @throws IllegalArgumentException If the condition on the argument is not met.
     */
    public void setDeadline(int deadline) {
        assertStrictlyPositive(deadline, "Relative deadline");
        this.deadline = deadline;
    }

    public int getComputation() {
        return computation;
    }

    /**
     * Set the computation time required to fulfil the job.
     * @param computation The computation time required to fulfil the job. Must be
     *                 strictly greater than 0.
     * @throws IllegalArgumentException If the condition on the argument is not met.
     */
    public void setComputation(int computation) {
        assertStrictlyPositive(computation, "Computation time");
        this.computation = computation;
    }

    public int getPriority() {
        return priority;
    }

    /**
     * Set the priority given to jobs of this task.
     * @param priority The priority given to jobs of this task. Must be at least 0.
     * @throws IllegalArgumentException If the condition on the argument is not met.
     */
    public void setPriority(int priority) {
        assertPositive(priority, "Priority");
        this.priority = priority;
    }

    public int getThreshold() {
        return threshold;
    }

    /**
     * Set the preemption threshold for this job.
     * @param threshold The preemption threshold for this job. Must either have a minimal value of
     *                  0, or it must be equal to one of the following flags:
     *                  <ul>
     *                      <li>{@code NO_PREEMPTION_THRESHOLD}</li>
     *                  </ul>
     * @throws IllegalArgumentException If the condition on the argument is not met.
     * @see Task#NO_PREEMPTION_THRESHOLD
     */
    public void setThreshold(int threshold) {
        if(threshold != NO_PREEMPTION_THRESHOLD)
            assertPositive(threshold, "Preemption threshold");
        this.threshold = threshold;
    }

    /**
     * Asserts that the given value is positive. If the value is not positive, an
     * {@code IllegalArgumentException} is thrown with the given subject. If the value is positive
     * the method terminates normally.
     * @param value The value to assert to be positive, meaning at least 0.
     * @param subject The subject to include in the Exception if the value is negative.
     */
    private void assertPositive(int value, String subject) throws IllegalArgumentException{
        if(value < 0)
            throw new IllegalArgumentException(subject+" must be at least 0.");
    }

    /**
     * Asserts that the given value is strictly larger than 0. If this does not hold, an
     * {@code IllegalArgumentException} is thrown with the given subject. If the value is positive
     * the method terminates normally.
     * @param value The value to assert to be larger than 0.
     * @param subject The subject to include in the Exception if the value is negative.
     */
    private void assertStrictlyPositive(int value, String subject) throws IllegalArgumentException{
        if(value <= 0)
            throw new IllegalArgumentException(subject+" must be strictly greater than 0.");
    }

    /**
     * Get the integer representation of the color that is currently set. The color is a 4 byte
     * representation following the ARGB specification. The alpha component of the color is always
     * equal to 255. Thus the following holds at all times: {@code color & 0xFF000000 == 0xFF000000}.
     * @return The integer representation of the color that is currently set.
     */
    public int getColor() {
        return color;
    }

    /**
     * Get a hexadecimal representation of the color. The representation always contains 6
     * uppercase characters following the pattern RRGGBB.
     * @return A hexadecimal representation of the color that is currently set.
     */
    public String getHexColor(){
        String hex = Integer.toHexString(this.color);
        if(hex.length() != 8)
            throw new IllegalStateException("Expected the 4 byte color to be converted to an 8 character string.");
        return hex.substring(2);
    }

    /**
     * Set the color with which this task is represented. The alpha channel of the given color is
     * ignored and is instead always set to 255.
     * @param color The 4 byte ARGB representation of the color.
     */
    public void setColor(int color) {
        this.color =  0xFF000000 + (0x00FFFFFF & color);
    }

    /**
     * Set the color based on RGB values. The underlying code is
     * {@code android.graphics.Color#rgb(int, int, int)}. Restrictions that apply to that method
     * also apply here.
     * @see android.graphics.Color#rgb(int, int, int)
     * @param red The red component of the color
     * @param green The green component of the color
     * @param blue The blue component of the color
     */
    public void setColor(int red, int green, int blue){
        this.color = Color.rgb(red,green,blue);
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        if (offset != task.offset) return false;
        if (period != task.period) return false;
        if (deadline != task.deadline) return false;
        if (computation != task.computation) return false;
        if (priority != task.priority) return false;
        if (threshold != task.threshold) return false;
        if (color != task.color) return false;
        return name.equals(task.name);

    }

    @Override
    public int hashCode() {
        int result = offset;
        result = 31 * result + period;
        result = 31 * result + deadline;
        result = 31 * result + computation;
        result = 31 * result + priority;
        result = 31 * result + threshold;
        result = 31 * result + color;
        result = 31 * result + name.hashCode();
        return result;
    }
}
