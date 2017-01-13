package nl.tue.san.visualization;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Map;

import nl.tue.san.tasks.TaskSetIO;

/**
 * Created by Maurice on 13-1-2017.
 */

public class VisualizationIO {

    public static Visualization fromJSON(JSONObject jsonObject) throws JSONException {

        Visualization visualization = new Visualization();

        // Extract simple properties
        visualization.setTimeScale(jsonObject.getLong(TIME_SCALE));
        visualization.setCycleRate(jsonObject.getLong(CYCLE_RATE));
        visualization.setTaskSet(TaskSetIO.fromJSON(jsonObject.getJSONObject(TASK_SET)));

        // Extract lights
        JSONObject lights = jsonObject.getJSONObject(LIGHTS);
        Iterator<String> lightsIter = lights.keys();
        while(lightsIter.hasNext()) {
            String light = lightsIter.next();
            visualization.set(light, lights.getString(light));
        }

        //Done
        return visualization;
    }


    public static JSONObject toJSON(Visualization visualization) throws JSONException {
        JSONObject object = new JSONObject();

        // Insert simple properties
        object.put(CYCLE_RATE, visualization.getCycleRate());
        object.put(TIME_SCALE, visualization.getTimeScale());
        if(visualization.getTaskSet() != null)
            object.put(TASK_SET, TaskSetIO.toJSON(visualization.getTaskSet()));


        // Insert lights
        JSONObject lights = new JSONObject();
        for(Map.Entry<String, String> entry : visualization.getMapping().entrySet())
            if(entry.getValue() != null)
                lights.put(entry.getKey(), entry.getValue());
        object.put(LIGHTS, lights);

        // Done
        return object;
    }


    private static final String TASK_SET = "TaskSet";
    private static final String LIGHTS = "Lights";
    private static final String CYCLE_RATE = "CycleRate";
    private static final String TIME_SCALE = "TimeScale";





}
