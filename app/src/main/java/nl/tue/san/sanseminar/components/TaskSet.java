package nl.tue.san.sanseminar.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import nl.tue.san.sanseminar.concurrent.ReadWriteSafeObject;

/**
 * Created by Maurice on 5-1-2017.
 *
 * Contains Tasks such that the tasks are obtainable by name and by index. It is thread safe in the
 * sense that no other operations can occur while new elements are being added to the TaskSet. For
 * more information, see {@link nl.tue.san.sanseminar.concurrent.ReadWriteSafeObject}.
 * @see nl.tue.san.sanseminar.concurrent.ReadWriteSafeObject
 */

public class TaskSet extends ReadWriteSafeObject {

    private final String name;
    private final ArrayList<String> order;
    private final HashMap<String, Task> tasks;

    /**
     * Creates a new TaskSet with the given name, containing the given tasks. The tasks are inserted in the order that they are given in.
     * @param name
     * @param tasks
     */
    public TaskSet(String name, Task...tasks) {
        this(name, Arrays.asList(tasks));
    }

    /**
     * Creates a new TaskSet with the given name, containing the given tasks. The tasks are inserted in the order that they are given in.
     * @param name
     * @param tasks
     */
    public TaskSet(String name, List<Task> tasks) {
        this(name, tasks.size());

        // Now write all tasks. It is safe to call insert directly as there can be only one call
        // occurring on this object right now, and that is the call to this constructor.
        for(Task task : tasks)
            this.unsafeInsert(task);
    }

    /**
     * Creates a new TaskSet that has the given name. It contains no tasks, but expects to contain
     * the given number of tasks. A correct size estimation allows for a more efficient storage
     * usage.
     * @param name The name of the TaskSet.
     * @param size The expected number of tasks in this taskset.
     */
    public TaskSet(String name, int size){
        this.name = name;
        this.tasks = new HashMap<>();
        this.order = new ArrayList<>(size);
    }

    /**
     * Creates a new TaskSet that contains no tasks, and has the given name.
     * @param name The name of the TaskSet.
     */
    public TaskSet(String name){
        this(name, 10);
    }


    /**
     * Get the name of this TaskSet.
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Get the task at the given index.
     * @param index
     * @return
     */
    public Task get(final int index){
        return this.readOp(new Operation<Task>() {
            @Override
            public Task perform() {
                return TaskSet.this.get(order.get(index));
            }
        });
    }

    /**
     * Get the task with the given name
     * @param name
     * @return
     */
    public Task get(final String name){
        return this.readOp(new Operation<Task>() {
            @Override
            public Task perform() {
                return TaskSet.this.tasks.get(name);
            }
        });
    }

    /**
     * Indicates whether a Task with the given name is contained in this TaskSet.
     * @param name
     * @return
     */
    public boolean contains(final String name){

        return readOp(new Operation<Boolean>() {
            @Override
            public Boolean perform() {
                return name != null && TaskSet.this.tasks.containsKey(name);
            }
        });
    }

    /**
     * Indicates whether a task equal to the given task is contained in this TaskSet.
     * @param task
     * @return
     */
    public boolean contains(final Task task) {

        return readOp(new Operation<Boolean>() {
            @Override
            public Boolean perform() {
                return task != null && TaskSet.this.contains(task.getName()) && TaskSet.this.get(task.getName()).equals(task);
            }
        });
    }

    /**
     * Indicates the number of tasks in this TaskSet.
     * @return
     */
    public int size(){
        return readOp(new Operation<Integer>() {
            @Override
            public Integer perform() {
                return TaskSet.this.tasks.size();
            }
        });
    }


    /**
     * Get the names of all tasks contained. Changes on the returned set are not reflected on the
     * TaskSet. Similarly, changes on this TaskSet that cccur after this call are not reflected in
     * the returned Set either.
     * @return A set of the names of all tasks contained in this TaskSet.
     */
    public Set<String> getTaskNames(){
        return readOp(new Operation<Set<String>>() {
            @Override
            public Set<String> perform() {
                return new HashSet<>(TaskSet.this.tasks.keySet());
            }
        });
    }

    /**
     * Get all tasks contained. The tasks are in arbitrary order. Changes on the returned set are not
     * reflected on the TaskSet nor are changes on the TaskSet after this call reflected in the
     * returned set. Changes on the Tasks themselves are reflected.
     * @return A set of the names of all tasks contained in this TaskSet.
     */
    public Set<Task> getTasks(){
        return readOp(new Operation<HashSet<Task>>() {
            @Override
            public HashSet<Task> perform() {
                return new HashSet<>(TaskSet.this.tasks.values());
            }
        });
    }

    /**
     * Get all tasks contained, in order. Changes on the returned list are not reflected on the
     * TaskSet nor are changes on the TaskSet after this call reflected in the returned set. Changes
     * on the Tasks themselves are reflected.
     * @return A set of the names of all tasks contained in this TaskSet.
     */
    public List<Task> getOrderedTasks(){
        return readOp(new Operation<List<Task>>() {
            @Override
            public List<Task> perform() {

                List<Task> tasksInOrder = new LinkedList<>();
                for(String name : TaskSet.this.order)
                    tasksInOrder.add(TaskSet.this.tasks.get(name));
                return tasksInOrder;
            }
        });
    }

    /**
     * Insert the given task. This will replace any existing task with the same name. The given task
     * will be at the end of the order, regardless of whether a task with the same name was already contained.
     * @param task The task to insert.
     * @return The Task that was evicted by adding the given task. If no task is evicted then null
     *          is returned.
     */
    public Task put(final Task task){
        return this.writeOp(new Operation<Task>() {
            @Override
            public Task perform() {
                return TaskSet.this.unsafeInsert(task);
            }
        });
    }


    /**
     * Insert the given task into the TaskSet. This method performs no synchronization. Therefore it
     * should <strong>not</strong> be publicly available. Furthermore, it should not be called
     * without external synchronization.
     * @param task The task to insert
     * @return The Task that was evicted by adding the given task. If no task is evicted then null
     *          is returned.
     */
    private Task unsafeInsert(Task task){
        // Ensure that the taskname only occurs at the end of the order
        order.remove(task.getName());
        order.add(task.getName());

        // Then insert it into mapping
        return tasks.put(task.getName(), task);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaskSet taskSet = (TaskSet) o;

        if (!name.equals(taskSet.name)) return false;
        if (!order.equals(taskSet.order)) return false;
        return tasks.equals(taskSet.tasks);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + order.hashCode();
        result = 31 * result + tasks.hashCode();
        return result;
    }
}
