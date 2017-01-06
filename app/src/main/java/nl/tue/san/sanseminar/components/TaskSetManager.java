package nl.tue.san.sanseminar.components;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.Set;

import nl.tue.san.sanseminar.concurrent.ReadWriteSafeObject;

/**
 * Created by Maurice on 6-1-2017.
 */
public final class TaskSetManager extends ReadWriteSafeObject{

    private final static TaskSetManager instance = new TaskSetManager();

    private static final File root = null;

    private TaskSetManager() { }


    /**
     * Writes the given TaskSet to a file within the root directory as a JSONObject. It uses the
     * filename as specified by {@link TaskSetManager#fileNameFor(TaskSet)}.
     * @param taskSet The TaskSet to write
     * @return Whether the file was successfully written to.
     * @throws JSONException If the given TaskSet could not be translated to JSON.
     */
    private void writeToFile(final TaskSet taskSet) throws IOException, JSONException {

        if(root == null)
            throw new IOException("Not allowed to access directory");

        final JSONObject translation = TaskSetIO.toJSON(taskSet);

        // Ensure that we have the write lock to write to the directory.
        IOException exception = this.writeOp(new Operation<IOException>() {
            @Override
            public IOException perform() {
                try {
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(root,fileNameFor(taskSet)))));
                    writer.write(translation.toString());
                    writer.close();
                    return null;
                } catch (IOException e) {
                    return e;
                }
            }
        });

        // Throw the returned IOException
        if(exception != null)
            throw exception;
    }

    /**
     * Get the name of the file that should be used to store a TaskSet that has the given name.
     * The name of the file is the name of the TaskSet followed by the extension "taskset".
     * A shorthand method exists to get the file name for a given TaskSet as
     * {@link #fileNameFor(TaskSet)}.
     * @param taskSetName The name of the TaskSet to store.
     * @return The filename that should be used to store a TaskSet.
     * @see #fileNameFor(TaskSet)
     */
    private static String fileNameFor(String taskSetName){
        return taskSetName+".taskset";
    }

    /**
     * Get the name of the file that should be used to store the given TaskSet. This equals calling
     * {@link #fileNameFor(String)} with as argument {@code taskSet.getName()}.
     * @param taskSet The TaskSet to store.
     * @return The filename that should be used to store a TaskSet.
     * @see TaskSetManager#fileNameFor(String)
     */
    private static String fileNameFor(TaskSet taskSet){
        return fileNameFor(taskSet.getName());
    }

    /**
     * Save the given TaskSet. This writes the TaskSet to a file. Any existing TaskSet with the same
     * name will be overwritten.
     * @param taskSet The TaskSet to save.
     */
    public static boolean save(TaskSet taskSet){

        try {
            instance.writeToFile(taskSet);
            return true;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Load the TaskSet with the given name.
     * @param name The name of the TaskSet to load.
     * @return The loaded TaskSet, or null if the TaskSet didn't exist.
     */
    public static TaskSet load(String name){
        return null;
    }

    /**
     * Load all TaskSets.
     * @return A Set containing all TaskSets that are available. Modifications on this set are not
     * reflected in the storage. The returned Set may contain 0 elements but is never {@code null}.
     */
    public static Set<TaskSet> loadAll(){
        return new HashSet<>();
    }

    /**
     * Get the names of all TaskSets that are available.
     * @return A set containing the names of all available TaskSets. The returned Set may contain 0
     * elements but is never {@code null}.
     */
    public static Set<String> stored() {
        return new HashSet<>();
    }

}
