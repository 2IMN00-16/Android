package nl.tue.san.tasks;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Set;

import nl.tue.san.net.Callback;
import nl.tue.san.net.Server;
import nl.tue.san.util.Manager;
import nl.tue.san.util.ReadWriteSafeObject.Operation;

/**
 * Created by Maurice on 6-1-2017.
 */

public class TaskSetManager extends Manager<LinkedHashMap<String, TaskSet>> {

    /**
     * By default there is no instance. On the first call to getInstance the instance will be created.
     */
    private static TaskSetManager instance;

    /**
     * Constant describing the extension of TaskSet files. The extension includes the dot.
     */
    private static final String TASK_SETS_FILENAME = "root.tasksets";

    private final Set<OnTaskSetsChangedListener> listeners = new HashSet<>();

    /**
     * Add the given {@link OnTaskSetsChangedListener} as a listener on this manager.
     * @param listener The Listener to add
     */
    public void addOnTaskSetsChangedListener (OnTaskSetsChangedListener listener){
        this.listeners.add(listener);
    }

    /**
     * Remove the given {@link OnTaskSetsChangedListener} as a listener on this manager.
     * @param listener The Listener to remove
     */
    public void removeOnTaskSetsChangedListener (OnTaskSetsChangedListener listener){
        this.listeners.remove(listener);
    }

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
     * Create a new TaskSetManager that uses the given File as a directory to store TaskSets in.
     *
     * @param context The Context in which the Manager operates.
     */
    private TaskSetManager(Context context) {
        super(new File(context.getFilesDir(), TASK_SETS_FILENAME));
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
                return managed().get(name);
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
                return new ArrayList<>(managed().values()).get(index);
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
                return managed().size();
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
                return new HashSet<>(managed().keySet());
            }
        });

    }


    /**
     * Convert the managed object to a String. This method and {@link #unmarshall(String)} must be
     * defined in such a way that {@code unmarshall(marshall(managed)).equals(managed)}. If this
     * does not hold, the marshalling is useless.
     *
     * @param managed The managed object to marshall.
     * @return A marshalling of the managed object that can be unmarshalled to recreate the managed
     * object.
     * @throws Exception If anything went wrong during marshalling.
     */
    @Override
    protected String marshall(LinkedHashMap<String, TaskSet> managed) throws Exception {
        JSONArray array = new JSONArray();
        for (TaskSet taskSet : managed.values())
            array.put(TaskSetIO.toJSON(taskSet));
        return array.toString();
    }

    /**
     * Convert a String back to the managed object. This method and {@link #marshall(Object)} must
     * be defined in such a way that {@code unmarshall(marshall(managed)).equals(managed)}. If this
     * does not hold, the unmarshalling is useless.
     *
     * @param content A String that was the result of calling marshall on the managed object.
     * @return The object that was described by the given string, which should be managed.
     * @throws Exception If anything went wrong during unmarshalling.
     */
    @Override
    protected LinkedHashMap<String, TaskSet> unmarshall(String content) throws Exception {
        LinkedHashMap<String, TaskSet> map = new LinkedHashMap<>();
        JSONArray array = new JSONArray(new JSONTokener(content));
        for (int i = 0; i < array.length(); ++i){
            TaskSet taskSet = TaskSetIO.fromJSON(array.getJSONObject(i));
            map.put(taskSet.getName(), taskSet);
        }

        return map;
    }

    @Override
    protected LinkedHashMap<String, TaskSet> initialObject() {
        return new LinkedHashMap<>();
    }

    /**
     * Register the given TaskSet under this manager.
     *
     * @param taskSet
     */
    public void register(final TaskSet taskSet) {
        this.writeOp(new Operation<Void>() {
            @Override
            public Void perform() {
                TaskSet contained = managed().get(taskSet.getName());

                if(taskSet.equals(contained))
                    return null;

                if(contained != null)
                    remove(managed().get(taskSet.getName()));


                managed().put(taskSet.getName(), taskSet);
                for(OnTaskSetsChangedListener listener : listeners)
                    listener.onTaskSetAdded(taskSet);

                return null;
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
                if (managed().containsKey(taskSet.getName()) && managed().get(taskSet.getName()).equals(taskSet)) {
                    managed().remove(taskSet.getName());
                    for(OnTaskSetsChangedListener listener : listeners)
                        listener.onTaskSetRemoved(taskSet);
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
                LinkedList<TaskSet> values = new LinkedList<>(managed().values());
                managed().clear();
                for(TaskSet removed : values)
                    for(OnTaskSetsChangedListener listener : listeners)
                        listener.onTaskSetRemoved(removed);
                return null;
            }
        });
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
     * Indicates the position at which the given taskset is stored.
     *
     * @param taskSet
     * @return
     */
    public int indexOf(TaskSet taskSet) {
        return new LinkedList<>(this.managed().values()).indexOf(taskSet);
    }

    public interface OnTaskSetsChangedListener {
        void onTaskSetAdded(TaskSet taskSet);

        void onTaskSetRemoved(TaskSet taskSet);
    }
}
