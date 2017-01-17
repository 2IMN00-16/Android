package nl.tue.san.visualization;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import nl.tue.san.util.Manager;
import nl.tue.san.util.ReadWriteSafeObject.Operation;


/**
 * Created by Maurice on 13-1-2017.
 */

public class VisualizationManager extends Manager<Visualization> {

    /**
     * Constant describing the extension of TaskSet files. The extension includes the dot.
     */
    private static final String VISUALIZATION_FILENAME = "visualization.json";

    private static VisualizationManager instance;

    /**
     * Set containing all available lights.
     */
    private Set<String> lights;

    /**
     * Set containing all possible visualizations that can be applied to a light.
     */
    private Set<String> visualizations;

    /**
     * Indicates the end of the most recent request to identify the lights. This indicates the time
     * in milliseconds at which the identification is stopped. If this lies in the future, then the
     * identification is ongoing. If it lies in the past then it has stopped. Along with this
     * identification the mapping specified in {@link #mappingOfRecentIdentification} was used.
     * @see #mappingOfRecentIdentification
     */
    private long endOfRecentIdentification = 0;

    /**
     * Indicates how lights were mapped to colors on the most recent request to identify the lights.
     * If the associated end time, represented by {@link #endOfRecentIdentification}, lies in the
     * future, then this mapping should be used. If it lies in the past then it has stopped.
     * Additionally, if the associated end time lies in the past, then this value may be null.
     * As long as the end time is still in the future, this value will not be null.
     * @see #endOfRecentIdentification
     */
    private Map<String, Integer> mappingOfRecentIdentification;

    public Set<String> getLights() {
        return this.readOp(new Operation<Set<String>>() {
            @Override
            public Set<String> perform() {
                return new HashSet<>(VisualizationManager.instance.lights);
            }
        });
    }

    public Set<String> getVisualizations() {
        return this.readOp(new Operation<Set<String>>() {
            @Override
            public Set<String> perform() {
                return new HashSet<>(VisualizationManager.instance.visualizations);
            }
        });

    }

    public boolean hasLight(final String light){
        return this.readOp(new Operation<Boolean>() {
            @Override
            public Boolean perform() {
                return VisualizationManager.instance.lights.contains(light);
            }
        });

    }

    public boolean hasVisualization(final String visualization){
        return this.readOp(new Operation<Boolean>() {
            @Override
            public Boolean perform() {
                return VisualizationManager.instance.visualizations.contains(visualization);
            }
        });

    }


    /**
     * Create a new TaskSetManager that uses the given File as a directory to store TaskSets in.
     * @param context The Context in which the Manager operates.
     */
    private VisualizationManager(Context context) {
        super(new File(context.getFilesDir(), VISUALIZATION_FILENAME));
    }

    /**
     * Synchronize the values contained in this manager with the server.
     * This synchronizes both lights and visualizations.
     */
    public void synchronize(){

        this.synchronizeLights();
        this.synchronizeVisualizations();
    }

    /**
     * Synchronize the lights with the server. This makes sure that we locally know what lights are
     * available on the server.
     */
    public void synchronizeLights(){

        final Collection<String> lights = new HashSet<>();

        // Do the following writeOp call async
        this.writeOp(new Operation<Void>() {
            @Override
            public Void perform() {
                VisualizationManager.instance.lights.clear();
                VisualizationManager.instance.lights.addAll(lights);
                return null;
            }
        });
    }
    /**
     * Synchronize the visualizations with the server. This makes sure that we locally know what
     * visualizations are available on the server.
     */
    public void synchronizeVisualizations(){
        final Collection<String> visualizations = new HashSet<>();

        // Do the following writeOp call async
        this.writeOp(new Operation<Void>() {
            @Override
            public Void perform() {
                VisualizationManager.instance.visualizations.clear();
                VisualizationManager.instance.visualizations.addAll(visualizations);
                return null;
            }
        });
    }

    /**
     * Obtain the VisualizationManager. This expects that an instance is already created.
     * @return The existing VisualizationManager.
     * @throws IllegalStateException If the VisualizationManager wasn't instantiated.
     */
    public static VisualizationManager getInstance(){
        if(instance == null)
            throw new IllegalStateException("Can't get instance. Instance must be created first");
        else
            return instance;
    }

    /**
     * Create the instance of the VisualizationManager. This expects that an instance does not yet
     * exist.
     * @return The created VisualizationManager.
     * @throws IllegalStateException If the VisualizationManager was already created.
     */
    public static VisualizationManager createInstance(Context context){

        if(VisualizationManager.instance != null)
            throw new IllegalStateException("Can't create instance. Instance already exists");

        VisualizationManager.instance = new VisualizationManager(context);
        return VisualizationManager.instance;
    }

    /**
     * Get the instance of the VisualizationManager, or create it if necessary. Use this method if
     * you don't know whether an instance already exists, but you have access to the Context to
     * create it.
     *
     * @param context The Context from which to create the VisualizationManager.
     * @return The instance of the VisualizationManager.
     */
    public static VisualizationManager getInstance(Context context){
        if(instance == null)
            createInstance(context);
        return getInstance();
    }

    public Visualization getVisualization(){
        return this.managed();
    }


    @Override
    protected String marshall(Visualization object) throws JSONException {
        return VisualizationIO.toJSON(object).toString();
    }

    @Override
    protected Visualization unmarshall(String content) throws JSONException {
        return VisualizationIO.fromJSON(new JSONObject(new JSONTokener(content)));
    }

    public long getEndOfRecentIdentification() {
        return endOfRecentIdentification;
    }

    public Map<String, Integer> getMappingOfRecentIdentification() {
        return mappingOfRecentIdentification;
    }

    /**
     * Start a new identification.
     * @param lightToColors THe mapping to use to identify lights
     * @param duration The amount of time for which this identification will be on going. Must
     *                 be positive.
     */
    public void startIdentification(Map<String, Integer> lightToColors, long duration) {
        if(lightToColors == null)
            throw new IllegalArgumentException("Mapping is required");
        if(duration < 0)
            throw new IllegalArgumentException("Duration must be positive");

        this.endOfRecentIdentification = System.currentTimeMillis() + duration;
        this.mappingOfRecentIdentification = lightToColors;
    }
}
