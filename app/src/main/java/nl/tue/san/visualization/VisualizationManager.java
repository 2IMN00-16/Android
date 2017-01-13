package nl.tue.san.visualization;

import android.content.Context;

import org.json.JSONException;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
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


    @Override
    protected String marshall(Visualization object) throws JSONException {
        return null;//VisualizationIO.toJSON(object).toString();
    }

    @Override
    protected Visualization unmarshall(String content) throws JSONException {
        return null;//VisualizationIO.fromJSON(new JSONObject(new JSONTokener(content)));
    }
}
