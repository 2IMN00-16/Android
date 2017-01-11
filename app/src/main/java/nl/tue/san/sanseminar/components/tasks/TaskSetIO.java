package nl.tue.san.sanseminar.components.tasks;

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

        String name = object.getString(NAME);

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

        JSONArray tasks = new JSONArray();
        for(Task task : taskSet.getOrderedTasks())
            tasks.put(TaskIO.toJSON(task));

        return new JSONObject()
                .put(TASKS,tasks)
                .put(NAME,taskSet.getName())
                ;

    }


}
