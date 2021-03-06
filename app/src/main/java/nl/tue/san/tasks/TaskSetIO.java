package nl.tue.san.tasks;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Maurice on 6-1-2017.
 */

public class TaskSetIO {


    private static final String TASKS = "Tasks";
    private static final String NAME = "Name";

    /**
     * Converts a JSONObject to a TaskSet. This expects that the following properties are set.
     * <ul>
     *      <li>
     *          <strong>Name</strong>
     *          <em>(String)</em>
     *      </li>
     *      <li>
     *          <strong>Tasks</strong>
     *          <em>(Array. The items in the array are expected to match the requirements set by
     *          {@link TaskIO#fromJSON(JSONObject)} )</em>
     *      </li>
     * </ul>
     *
     * If any of the required properties is missing, a JSONException is thrown.
     *
     * @param object The JSONObject representing a TaskSet.
     * @return The created TaskSet
     * @see TaskIO#fromJSON(JSONObject)
     */
    public static TaskSet fromJSON(JSONObject object) throws JSONException {
        return fromJSON(object.getString(NAME), object);
    }

    /**
     * Converts a JSONObject to a TaskSet, but uses the given name rather than the contained name.
     * This expects that the following properties are set.
     * <ul>
     *      <li>
     *          <em>(String)</em>
     *      </li>
     *      <li>
     *          <strong>Tasks</strong>
     *          <em>(Array. The items in the array are expected to match the requirements set by
     *          {@link TaskIO#fromJSON(JSONObject)} )</em>
     *      </li>
     * </ul>
     *
     * If any of the required properties is missing, a JSONException is thrown.
     *
     * @param object The JSONObject representing a TaskSet.
     * @return The created TaskSet
     * @see TaskIO#fromJSON(JSONObject)
     */
    public static TaskSet fromJSON(String name, JSONObject object) throws JSONException {
        List<Task> tasks = new LinkedList<>();

        JSONArray taskArray = object.getJSONArray(TASKS);

        for(int i = 0; i < taskArray.length(); ++i)
            tasks.add(TaskIO.fromJSON(taskArray.getJSONObject(i)));


        return new TaskSet(name, tasks);
    }


    /**
     * Converts a TaskSet to a JSONObject. The returned JSONObject contains the following properties,
     * in no particular order:
     * <ul>
     *      <li>
     *          <strong>Name</strong>
     *          <em>(String)</em>
     *      </li>
     *      <li>
     *          <strong>Tasks</strong>
     *          <em>(Array. The items in the array are match the requirements set by
     *          {@link TaskIO#toJSON(Task)} )</em>
     *      </li>
     * </ul>
     * @param taskSet The TaskSet to convert to JSON
     * @return A JSONObject representing the given TaskSet.
     * @throws JSONException If writing to the JSONObject failed.
     */
    public static JSONObject toJSON(TaskSet taskSet) throws JSONException {
        return TaskSetIO.toJSON(taskSet, true);
    }

    /**
     * Converts a TaskSet to a JSONObject. The returned JSONObject contains the following properties,
     * in no particular order:
     * <ul>
     *      <li>
     *          <strong>Name</strong>
     *          <em>(String)</em>
     *      </li>
     *      <li>
     *          <strong>Tasks</strong>
     *          <em>(Array. The items in the array are match the requirements set by
     *          {@link TaskIO#toJSON(Task)} )</em>
     *      </li>
     * </ul>
     * @param taskSet The TaskSet to convert to JSON
     * @param allowThresholdFlags Whether the threshold should be absolute
     *                            ({@code false}) or whether flag values are also allowed
     *                            ({@code true}).
     * @return A JSONObject representing the given TaskSet.
     * @throws JSONException If writing to the JSONObject failed.
     */
    public static JSONObject toJSON(TaskSet taskSet, boolean allowThresholdFlags) throws JSONException {

        JSONArray tasks = new JSONArray();
        for(Task task : taskSet.getOrderedTasks())
            tasks.put(TaskIO.toJSON(task, allowThresholdFlags));

        return new JSONObject()
                .put(TASKS,tasks)
                .put(NAME,taskSet.getName())
                ;

    }

}
