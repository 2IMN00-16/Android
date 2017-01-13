package nl.tue.san.tasks;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Set;

import nl.tue.san.net.*;
import nl.tue.san.util.ReadWriteSafeObject;

/**
 * Created by Maurice on 6-1-2017.
 */

public class TaskSetManager extends ReadWriteSafeObject {

    /**
     * By default there is no instance. On the first call to getInstance the instance will be created.
     */
    private static TaskSetManager instance;

    /**
     * Constant describing the extension of TaskSet files. The extension includes the dot.
     */
    private static final String TASK_SETS_FILENAME = "root.tasksets";

    /**
     * Obtain an instance of the TaskSetManager. On the first call to this method the manager is
     * created. Therefore that first call may take more time than subsequent calls.
     *
     * @param context The context in which the manager is used. This is required to be able to write
     *                files to the internal storage.
     * @return The only instance of the TaskSetManager
     */
    public static TaskSetManager getInstance(Context context) {

        if (instance == null)
            instance = new TaskSetManager(context);
        return instance;
    }

    /**
     * The location of tasksets within
     */
    private final LinkedHashMap<String, TaskSet> taskSets = new LinkedHashMap<>();

    /**
     * The directory in which we can save all TaskSets.
     */
    private final File root;

    /**
     * Create a new TaskSetManager that uses the given File as a directory to store TaskSets in.
     *
     * @param context The Context in which the Manager operates.
     */
    private TaskSetManager(Context context) {
        this.root = new File(context.getFilesDir(), TASK_SETS_FILENAME);
        try {
            this.reload();
        } catch (Exception e) {
            Log.d("TaskSetManager", "Couldn't load tasksets from file " + this.root.getAbsolutePath() +
                    " due to " + e.getClass().getSimpleName() + ":  " + e.getMessage() + "." +
                    " Call reload on TaskSetManager to get full exception.");
        }
    }

    /**
     * Get the TaskSet with the given name.
     *
     * @param name The name of the TaskSet.
     * @return The TaskSet with the given name, or null if there is no such TaskSet.
     */
    public TaskSet get(final String name) {
        return this.readOp(new Operation<TaskSet>() {
            @Override
            public TaskSet perform() {
                return taskSets.get(name);
            }
        });
    }

    /**
     * Get the TaskSet located at index 0. The given index is expected to lie within the range [0,size).
     *
     * @param index The index of the TaskSet to obtain
     * @return The TaskSet that was located at the given index.
     * @throws IndexOutOfBoundsException If the given index is out of bounds.
     */
    public TaskSet get(final int index) {

        return this.readOp(new Operation<TaskSet>() {
            @Override
            public TaskSet perform() {
                return new ArrayList<>(taskSets.values()).get(index);
            }
        });
    }

    /**
     * Get the number of TaskSets managed by this TaskSetManager.
     *
     * @return The number of TaskSets managed by this TaskSetManager. The returned value is never negative.
     */
    public int size() {
        return this.readOp(new Operation<Integer>() {
            @Override
            public Integer perform() {
                return taskSets.size();
            }
        });
    }

    /**
     * Get the names of all TaskSets that are available.
     *
     * @return A set containing the names of all available TaskSets. The returned Set may contain 0
     * elements but is never {@code null}.
     */
    public Set<String> stored() {
        return this.readOp(new Operation<Set<String>>() {
            @Override
            public Set<String> perform() {
                return new HashSet<>(taskSets.keySet());
            }
        });

    }


    /**
     * Asserts that access has been given to the required directory. If access was not given, an
     * {@link IllegalStateException} is thrown. If the assertion is met, the method terminates
     * normally.
     */
    private void assertAccess() {
        if (root == null)
            throw new IllegalStateException("Not allowed to access directory");
    }

    /**
     * Register the given TaskSet under this manager.
     *
     * @param taskSet
     */
    public void register(final TaskSet taskSet) {
        this.writeOp(new Operation<TaskSet>() {
            @Override
            public TaskSet perform() {
                return taskSets.put(taskSet.getName(), taskSet);
            }
        });
    }

    /**
     * Discard the given TaskSet.
     *
     * @param taskSet
     * @return
     */
    public boolean remove(final TaskSet taskSet) {
        return this.writeOp(new Operation<Boolean>() {
            @Override
            public Boolean perform() {
                if (taskSets.containsKey(taskSet.getName()) && taskSets.get(taskSet.getName()).equals(taskSet)) {
                    taskSets.remove(taskSet.getName());
                    return true;
                } else
                    return false;
            }
        });
    }

    /**
     * Remove all TaskSets.
     */
    public void removeAll() {
        this.writeOp(new Operation<Void>() {
            @Override
            public Void perform() {
                taskSets.clear();
                return null;
            }
        });
    }

    /**
     * Perform a reload of all TaskSets.
     */
    public void reload() throws Exception {
        assertAccess();

        Exception exception = writeOp(new Operation<Exception>() {
            @Override
            public Exception perform() {
                try {
                    unsafeReload();
                    return null;
                } catch (JSONException | IOException e) {
                    return e;
                }
            }
        });

        if (exception != null)
            this.loadFromServer();
//            throw exception;
    }

    private void loadFromServer() {

        final TaskSetManager that = this;

        Server.GET("taskset", new Callback() {
            @Override
            public void onSuccess(String data) {
                Log.i("Network", "Retrieved data: " + data);
                try {
                    JSONObject obj = new JSONObject(new JSONTokener(data));

                    // Then convert each entry in the JSONArray to a TaskSet.
                    that.register(TaskSetIO.fromJSON(obj));

                } catch (Exception e) {
                    Log.e("TaskSetManager", "Loading of taskset Failed inner", e);
                    this.onFailure();
                }
            }

            @Override
            public void onFailure() {
                Log.e("TaskSetManager", "Loading of taskset Failed through callback");
            }
        });
    }

    /**
     * Performs a reload without synchronization.
     *
     * @throws JSONException
     * @throws IOException
     */
    private void unsafeReload() throws JSONException, IOException {

        // Convert the root file into a JSONArray
        StringBuilder builder = new StringBuilder();
        try (Reader reader = new InputStreamReader(new FileInputStream(this.root))) {
            while (reader.ready())
                builder.append((char) reader.read());
        }
        JSONArray array = new JSONArray(new JSONTokener(builder.toString()));

        // Then convert each entry in the JSONArray to a TaskSet.
        for (int i = 0; i < array.length(); ++i)
            this.register(TaskSetIO.fromJSON(array.getJSONObject(i)));
    }

    /**
     * Writes all TaskSets as a JSONArray to the root file. This does not provide any
     * synchronization. When calling, this should use external synchronization allowing it to read.
     * This method does not change any properties on the TaskSetManager.
     */
    private void unsafeWrite() throws JSONException, IOException {
        JSONArray array = new JSONArray();
        for (TaskSet taskSet : taskSets.values())
            array.put(TaskSetIO.toJSON(taskSet));

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.root)))) {
            writer.write(array.toString());
        }

    }

    /**
     * Write all TaskSets. This provides synchronization.
     */
    public void write() throws Exception {
        assertAccess();

        // To perform a write, we don't update this object. Instead we only read the properties of
        // this object. Therefore we use a readOp and not a writeOp.
        Exception exception = this.readOp(new Operation<Exception>() {
            @Override
            public Exception perform() {
                try {
                    unsafeWrite();
                    return null;
                } catch (JSONException | IOException e) {
                    return e;
                }
            }
        });

        if (exception != null)
            throw exception;
    }

    /**
     * Indicates the position at which the given taskset is stored.
     *
     * @param taskSet
     * @return
     */
    public int indexOf(TaskSet taskSet) {
        return new LinkedList<>(this.taskSets.values()).indexOf(taskSet);
    }
}