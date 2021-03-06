package nl.tue.san.visualization;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Maurice on 13-1-2017.
 */

public class VisualizationIO {

    public static Visualization fromJSON(JSONObject jsonObject) throws JSONException {

        Visualization visualization = new Visualization();

        // Extract simple properties
        visualization.setTimeScale(jsonObject.getLong(TIME_SCALE));
        visualization.setCycleRate(jsonObject.getLong(CYCLE_RATE));
        visualization.setScheduler(jsonObject.getString(SCHEDULER));
        visualization.setAnimationDuration(jsonObject.getLong(ANIMATION_DURATION));

        // Extract lights
        JSONArray lights = jsonObject.getJSONArray(LIGHTS);

        for(int i = 0 ; i < lights.length(); ++i) {
            visualization.set(lights.getJSONObject(i).getString(NAME), lights.getJSONObject(i).getString(VALUE));
        }

        //Done
        return visualization;
    }


    public static JSONObject toJSON(Visualization visualization) throws JSONException {
        JSONObject object = new JSONObject();

        // Insert simple properties
        object.put(CYCLE_RATE, visualization.getCycleRate());
        object.put(TIME_SCALE, visualization.getTimeScale());
        object.put(SCHEDULER, visualization.getScheduler());
        object.put(ANIMATION_DURATION, visualization.getAnimationDuration());

        // Insert lights
        JSONArray lights = new JSONArray();
        for(Map.Entry<String, String> entry : visualization.getMapping().entrySet())
            lights.put(new JSONObject().put(NAME, entry.getKey()).put(VALUE, entry.getValue()));
        object.put(LIGHTS, lights);

        // Done
        return object;
    }


    private static final String VALUE = "Value";
    private static final String NAME = "Name";
    private static final String ANIMATION_DURATION = "Duration";

    private static final String SCHEDULER = "Scheduler";
    private static final String LIGHTS = "Lights";
    private static final String CYCLE_RATE = "CycleRate";
    private static final String TIME_SCALE = "TimeFactor";





}
