package nl.tue.san.sanseminar.components;

import java.util.HashSet;
import java.util.Set;

import nl.tue.san.sanseminar.concurrent.ReadWriteSafeObject;

/**
 * Created by Maurice on 6-1-2017.
 */
public class TaskSetManager extends ReadWriteSafeObject{

    private TaskSetManager() {
    }

    /**
     * Save the given TaskSet. This writes the TaskSet to a file. Any existing TaskSet with the same
     * name will be overwritten.
     * @param taskSet The TaskSet to save.
     */
    public static boolean save(TaskSet taskSet){
        return false;
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
