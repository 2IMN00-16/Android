package nl.tue.san.sanseminar.components.tasks;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Maurice on 5-1-2017.
 */

public class TaskIO {

    /**
     * Converts a JSONObject to a Task. This expects that the following properties are set.
     * <ul>
     *      <li>
     *          <strong>Name</strong>
     *          <em>(String)</em>
     *      </li>
     *      <li>
     *          <strong>Priority</strong>
     *          <em>(Integer >= 0)</em>
     *      </li>
     *      <li>
     *          <strong>Computation</strong>
     *          <em>(Integer > 0, the computation time required for each job)</em>
     *      </li>
     *      <li>
     *          <strong>Period</strong>
     *          <em>(Integer > 0, indicates how much time exists between two job instances of the task)</em>
     *      </li>
     *      <li>
     *          <strong>Deadline</strong>
     *          <em>(Integer > 0, indicates how much time a job instance has to complete)</em>
     *      </li>
     *
     *      <li>
     *          <strong>Color</strong>
     *          <em>(Color (#RRGGBB), the color to use to represent this task)</em>
     *      </li>
     * </ul>
     *
     * Additionally, the following properties may be set as well. If they are not set then the
     * indicated default values are used.
     *
     * <ul>
     *     <li>
     *          <strong>Threshold</strong>
     *          <em>(Integer >= 0, indicates the priority threshold for interruptions in case of FPTS)</em>
     *          <br><strong>default:</strong> {@code priority + 1}.
     *      </li>
     *      <li>
     *          <strong>Offset</strong>
     *          <em>(Integer >= 0, indicates the offset in the arrival of the first job of this task, opposed to the global starting time)</em>
     *          <br><strong>default:</strong> {@code 0}.
     *      </li>
     * </ul>
     *
     * If any of the required properties is missing, a JSONException is thrown. If any of the properties has an illegal value, IllegalArgumentExceptions are thrown, as specified
     *
     * @param object
     * @return
     */
    public static Task fromJSON(JSONObject object) throws JSONException {

        // First all required properties
        String name = object.getString(NAME);
        int period = object.getInt(PERIOD);
        int deadline = object.getInt(DEADLINE);
        int computation = object.getInt(COMPUTATION);
        int priority = object.getInt(PRIORITY);
        int color = Integer.valueOf(object.getString(COLOR).substring(1), 16);


        // Then all optional properties
        int offset = object.optInt(OFFSET, 0);
        int threshold = object.optInt(THRESHOLD, Task.NO_PREEMPTION_THRESHOLD);

        return new Task(name, color,offset,period,deadline,computation,priority,threshold);
    }

    /**
     * Converts a Task to a JSONObject. The returned JSONObject contains the following properties,
     * in no particular order:
     * <ul>
     *      <li>
     *          <strong>Name</strong>
     *          <em>(String)</em>
     *      </li>
     *      <li>
     *          <strong>Priority</strong>
     *          <em>(Integer >= 0)</em>
     *      </li>
     *      <li>
     *          <strong>Threshold</strong>
     *          <em>(Integer >= 0, indicates the priority threshold for interruptions in case of FPTS)</em>
     *      </li>
     *      <li>
     *          <strong>Computation</strong>
     *          <em>(Integer > 0, the computation time required for each job)</em>
     *      </li>
     *      <li>
     *          <strong>Period</strong>
     *          <em>(Integer > 0, indicates how much time exists between two job instances of the task)</em>
     *      </li>
     *      <li>
     *          <strong>Deadline</strong>
     *          <em>(Integer > 0, indicates how much time a job instance has to complete)</em>
     *      </li>
     *      <li>
     *          <strong>Offset</strong>
     *          <em>(Integer >= 0, indicates the offset in the arrival of the first job of this task, opposed to the global starting time)</em>
     *      </li>
     *      <li>
     *          <strong>Color</strong>
     *          <em>(Color (#RRGGBB), the color to use to represent this task)</em>
     *      </li>
     * </ul>
     * @param task The Task to convert to JSON
     * @return A JSONObject representing the given Task.
     * @throws JSONException If writing to the JSONObject failed.
     */
    public static JSONObject toJSON(Task task) throws JSONException {
        return new JSONObject()
                .put(NAME, task.getName())
                .put(PRIORITY,task.getPriority())
                .put(THRESHOLD,task.getThreshold())
                .put(COMPUTATION,task.getComputation())
                .put(PERIOD,task.getPeriod())
                .put(DEADLINE,task.getDeadline())
                .put(OFFSET,task.getOffset())
                .put(COLOR,'#'+task.getHexColor());
    }

    private static final String NAME = "Name";
    private static final String PRIORITY = "Priority";
    private static final String THRESHOLD = "Threshold";
    private static final String COMPUTATION = "Computation";
    private static final String PERIOD = "Period";
    private static final String DEADLINE = "Deadline";
    private static final String OFFSET = "Offset";
    private static final String COLOR = "Color";

}
