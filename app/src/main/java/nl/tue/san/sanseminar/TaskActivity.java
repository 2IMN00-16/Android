package nl.tue.san.sanseminar;

import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.Locale;

import nl.tue.san.sanseminar.components.Task;
import nl.tue.san.sanseminar.components.TaskSet;
import nl.tue.san.sanseminar.components.TaskSetManager;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class TaskActivity extends AppCompatActivity {

    private TaskSetManager manager;

    /**
     * The name that should be given to the task. The task may already exist, in which case the task property of this activity won't be null.
     */
    private String intendedTaskName;

    /**
     *
     */
    private Task task;

    /**
     * The TaskSet to which the Task belongs.
     */
    private TaskSet taskSet;



    /*
         UI COMPONENTS
     */
    /*
        UI COMPONENTS - editors
     */
    private CheckBox defineThresholdCheckBox;
    private ColorPickerView taskColorEditor;
    private EditText taskPeriodEditor;
    private EditText taskPriorityEditor;
    private EditText taskPriorityThresholdEditor;
    private EditText taskOffsetEditor;
    private EditText taskComputationEditor;
    private EditText taskDeadlineEditor;
    private EditText taskNameEditor;
    private Spinner taskTaskSet;

    /*
        UI COMPONENTS - containers
     */
    private ViewGroup taskPriorityThresholdContainer;
    private ViewGroup taskNameContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout();



        this.manager = TaskSetManager.getInstance(this);

        Bundle intentExtras = this.getIntent().getExtras();



        this.intendedTaskName = intentExtras.getString("task");

        // Find the task set
        this.taskSet = getTaskSet(intentExtras.getString("taskset"));

        // Find whether the task already exists
        if(intendedTaskName != null)
            this.task = this.taskSet.get(intendedTaskName);

        this.display();
    }

    /**
     * Defines the content view, and finds all important views within that inflated view.
     */
    private void initLayout(){

        setContentView(R.layout.activity_task);

        // EditTexts / editors
        this.taskColorEditor = ((ColorPickerView)this.findViewById(R.id.task_color));
        this.taskColorEditor.provideFragmentManager(this.getFragmentManager());
        this.taskColorEditor.setColorOptions(getResources().getIntArray(R.array.default_rainbow));

        this.taskPriorityEditor = ((EditText)this.findViewById(R.id.task_priority));
        this.taskPeriodEditor = ((EditText)this.findViewById(R.id.task_period));
        this.taskOffsetEditor = ((EditText)this.findViewById(R.id.task_offset));
        this.taskPriorityThresholdEditor = ((EditText)this.findViewById(R.id.task_priority_threshold));
        this.taskComputationEditor = ((EditText)this.findViewById(R.id.task_computation));
        this.taskDeadlineEditor = ((EditText)this.findViewById(R.id.task_deadline));
        this.taskNameEditor = ((EditText)this.findViewById(R.id.task_name));
        this.taskTaskSet = ((Spinner)this.findViewById(R.id.task_taskset));

        // Checkbox indicating whether a Priority Threshold is used.
        this.defineThresholdCheckBox = ((CheckBox)this.findViewById(R.id.task_define_threshold));

        // Containers for properties that may disappear.
        this.taskPriorityThresholdContainer = (ViewGroup)findViewById(R.id.task_priority_threshold_container);
        this.taskNameContainer = (ViewGroup)findViewById(R.id.task_name_container);

        this.defineThresholdCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    taskPriorityThresholdContainer.setVisibility(isChecked ? VISIBLE : GONE);
                if(getTextAsInteger(taskPriorityThresholdEditor) < 0)
                    setIntegerAsText(taskPriorityThresholdEditor, Math.max(getTextAsInteger(taskPriorityEditor) + 1, 0));
            }
        });
    }


    /**
     * Updates the properties of all fields in the layout to show the required text based on the task that is being created or modified.
     */
    private void display(){

        /*
         First do general stuff
         */
        this.getSupportActionBar().setTitle(this.task == null ? "Create Task" : this.task.getName());
        this.taskNameEditor.setText(this.intendedTaskName);
        this.taskTaskSet.setAdapter(new TaskSetSpinnerAdapter());


        /*
         Then do stuff specific to if a task is set.
         */

        if(task != null) {

            // Set the value of all views
            setIntegerAsText(this.taskPriorityEditor, this.task.getPriority());
            setIntegerAsText(this.taskPriorityThresholdEditor, this.task.getThreshold());
            setIntegerAsText(this.taskPeriodEditor, this.task.getPeriod());
            setIntegerAsText(this.taskOffsetEditor, this.task.getOffset());
            setIntegerAsText(this.taskComputationEditor, this.task.getComputation());
            setIntegerAsText(this.taskDeadlineEditor, this.task.getDeadline());


            // Update the spinner
            this.taskTaskSet.setSelection(this.manager.indexOf(taskSet));

            // Show the correct color
            this.taskColorEditor.setColor(this.task.getColor());

            // Show the preemption threshold EditText, and update the checkbox.
            this.defineThresholdCheckBox.setChecked(this.task.hasPreemptionThreshold());
            if(this.task.hasPreemptionThreshold())
                setIntegerAsText(this.taskPriorityThresholdEditor, this.task.getThreshold());
            else
                this.taskPriorityThresholdContainer.setVisibility(GONE);


            // Don't show the task name, as it can't be modified.
            this.taskNameContainer.setVisibility(GONE);

        }

    }


    /**
     * Fills an integer property as the text for the given EditText. This will format the integer value following the default locale.
     * @param editor
     * @param value
     */
    private void setIntegerAsText(EditText editor, int value){
        editor.setText(String.format(Locale.getDefault(), "%d", value));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.task_action_save:
                trySave();
                return true;
            case R.id.task_action_revert:
                revert();
                return true;
            case R.id.task_action_delete:
                tryDelete();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    private void trySave() {
        try {
            this.save();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void tryDelete(){
        try {
            this.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * If a Task is being modified the modified task will be removed from its TaskSet. This change is then committed to the manager, after which the activity is terminated.
     * If we're currently in the process of creating a new task, the progress is discarded.
     * @throws Exception
     */
    private void delete() throws Exception {
        if(this.task != null)
            this.taskSet.remove(task);
        this.manager.write();
        this.finish();
    }

    /**
     * Save the current state. This will validate the input fields. If the input is not valid, or the changes couldn't be made persistent, an exception is thrown.
     *
     */
    private void save() throws Exception {


        this.validateInput();
        this.writeToTask();
        this.manager.write();
        this.finish();
    }

    private void validateInput() {
        if(this.defineThresholdCheckBox.isChecked() && this.getTextAsInteger(taskPriorityThresholdEditor) <= this.getTextAsInteger(taskPriorityEditor)){
            throw new IllegalTaskStateException("Priority Threshold must be strictly larger than the priority", this.taskPriorityEditor, this.taskPriorityThresholdEditor);
        }
        TaskSet selected = ((TaskSet)this.taskTaskSet.getSelectedItem());
        if(this.taskSet != selected && selected.get(this.taskNameEditor.getText().toString()) != null )
            throw new IllegalTaskStateException("A Task with the given name already exists in the intended Task Set. ", this.taskTaskSet, this.taskNameEditor);
    }

    /**
     * Reverts the GUI back to the initial state. This is achieved by calling display.
     */
    private void revert(){
        this.display();
    }


    private int getTextAsInteger(EditText editText){
        String text = editText.getText().toString();
        return text.length() == 0 ? 0 : Integer.parseInt(editText.getText().toString());
    }

    /**
     * Writes all values to a task. If the task doesn't exist yet, it is created. Otherwise the
     * properties of the existing task are updated.
     *
     * If a new task is created it is important to note that the name is no longer allowed to change. To achieve this either finish the Activity or call {@link #display()}.
     * The latter will result in the Task being visualized again as an existing task, thereby removing the option to change the title.
     * @return Whether or not a task had to be created.
     */
    private boolean writeToTask() {

        final int priority = getTextAsInteger(taskPriorityEditor);
        final int priorityThreshold = this.defineThresholdCheckBox.isChecked() ? getTextAsInteger(taskPriorityThresholdEditor) : Task.NO_PREEMPTION_THRESHOLD;
        final int offset = getTextAsInteger(taskOffsetEditor);
        final int period = getTextAsInteger(taskPeriodEditor);
        final int computation = getTextAsInteger(taskComputationEditor);
        final int deadline = getTextAsInteger(taskDeadlineEditor);
        final int color = this.taskColorEditor.getColorInt();
        final TaskSet taskSet = (TaskSet) this.taskTaskSet.getSelectedItem();

        if(task == null){
            // Create a new task. This requires the name as an additional property.
            final String name = taskNameEditor.getText().toString();
            task = new Task(name, color, offset, period, deadline, computation, priority, priorityThreshold);
            taskSet.put(task);
            return true;
        }
        else
        {
            // Update the existing task
            this.task.setPriority(priority);
            this.task.setThreshold(priorityThreshold);
            this.task.setOffset(offset);
            this.task.setPeriod(period);
            this.task.setComputation(computation);
            this.task.setDeadline(deadline);
            this.task.setColor(color);

            // If the owning taskset change, unregister the current taskset, and register the new one
            if(this.taskSet != taskSet){
                this.taskSet.remove(task);
                taskSet.put(task);
                this.taskSet = taskSet;
            }

            return false;
        }

    }


    /**
     * Obtain a TaskSet based on the given name. If the given name is null then a default TaskSet is
     * obtained.
     * @param taskSetName The name of the TaskSet, or null if you want to obtain a default TaskSet.
     * @return The TaskSet found for the given name
     * @throws IllegalStateException If the TaskSetManager doesn't manage any task sets.
     * @throws IllegalArgumentException If the TaskSetManager doesn't manage any task set with the given non null name.
     */
    private TaskSet getTaskSet(String taskSetName) {
        if(this.manager.size() == 0)
            throw new IllegalStateException("Can't manage a state without any task sets");

        if(taskSetName == null)
            return this.manager.get(0);
        else {
            taskSet = manager.get(taskSetName);
            if (taskSet == null)
                throw new IllegalArgumentException("Can't find task set \"" + taskSetName + "\"");
            return taskSet;
        }
    }

    /**
     * SpinnerAdapter that uses the available TaskSets as an underlying thingy.
     */
    private class TaskSetSpinnerAdapter implements SpinnerAdapter {
        /**
         * Gets a {@link View} that displays in the drop down popup
         * the data at the specified position in the data set.
         *
         * @param position    index of the item whose view we want.
         * @param convertView the old view to reuse, if possible. Note: You should
         *                    check that this view is non-null and of an appropriate type before
         *                    using. If it is not possible to convert this view to display the
         *                    correct data, this method can create a new view.
         * @param parent      the parent that this view will eventually be attached to
         * @return a {@link View} corresponding to the data at the
         * specified position.
         */
        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getView(position, convertView, parent);
        }

        /**
         * Register an observer that is called when changes happen to the data used by this adapter.
         *
         * @param observer the object that gets notified when the data set changes.
         */
        @Override
        public void registerDataSetObserver(DataSetObserver observer) {

        }

        /**
         * Unregister an observer that has previously been registered with this
         * adapter via {@link #registerDataSetObserver}.
         *
         * @param observer the object to unregister.
         */
        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {

        }

        /**
         * How many items are in the data set represented by this Adapter.
         *
         * @return Count of items.
         */
        @Override
        public int getCount() {
            return TaskActivity.this.manager.size();
        }

        /**
         * Get the data item associated with the specified position in the data set.
         *
         * @param position Position of the item whose data we want within the adapter's
         *                 data set.
         * @return The data at the specified position.
         */
        @Override
        public Object getItem(int position) {
            return TaskActivity.this.manager.get(position);
        }

        /**
         * Get the row id associated with the specified position in the list.
         *
         * @param position The position of the item within the adapter's data set whose row id we want.
         * @return The id of the item at the specified position.
         */
        @Override
        public long getItemId(int position) {
            return ((TaskSet)this.getItem(position)).getName().hashCode();
        }

        /**
         * Indicates whether the item ids are stable across changes to the
         * underlying data.
         *
         * @return True if the same id always refers to the same object.
         */
        @Override
        public boolean hasStableIds() {
            return true;
        }

        /**
         * Get a View that displays the data at the specified position in the data set. You can either
         * create a View manually or inflate it from an XML layout file. When the View is inflated, the
         * parent View (GridView, ListView...) will apply default layout parameters unless you use
         * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
         * to specify a root view and to prevent attachment to the root.
         *
         * @param position    The position of the item within the adapter's data set of the item whose view
         *                    we want.
         * @param convertView The old view to reuse, if possible. Note: You should check that this view
         *                    is non-null and of an appropriate type before using. If it is not possible to convert
         *                    this view to display the correct data, this method can create a new view.
         *                    Heterogeneous lists can specify their number of view types, so that this View is
         *                    always of the right type (see {@link #getViewTypeCount()} and
         *                    {@link #getItemViewType(int)}).
         * @param parent      The parent that this view will eventually be attached to
         * @return A View corresponding to the data at the specified position.
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView)convertView;
            if(view == null)
                view = new TextView(TaskActivity.this);

            view.setText(((TaskSet)this.getItem(position)).getName());
            view.setTextSize(18);
            view.setTextColor(Color.BLACK);
            view.setPadding(20,20,20,20);
            return view;
        }

        /**
         * Get the type of View that will be created by {@link #getView} for the specified item.
         *
         * @param position The position of the item within the adapter's data set whose view type we
         *                 want.
         * @return An integer representing the type of View. Two views should share the same type if one
         * can be converted to the other in {@link #getView}. Note: Integers must be in the
         * range 0 to {@link #getViewTypeCount} - 1. {@link #IGNORE_ITEM_VIEW_TYPE} can
         * also be returned.
         * @see #IGNORE_ITEM_VIEW_TYPE
         */
        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        /**
         * <p>
         * Returns the number of types of Views that will be created by
         * {@link #getView}. Each type represents a set of views that can be
         * converted in {@link #getView}. If the adapter always returns the same
         * type of View for all items, this method should return 1.
         * </p>
         * <p>
         * This method will only be called when the adapter is set on the {@link AdapterView}.
         * </p>
         *
         * @return The number of types of Views that will be created by this adapter
         */
        @Override
        public int getViewTypeCount() {
            return 1;
        }

        /**
         * @return true if this adapter doesn't contain any data.  This is used to determine
         * whether the empty view should be displayed.  A typical implementation will return
         * getCount() == 0 but since getCount() includes the headers and footers, specialized
         * adapters might want a different behavior.
         */
        @Override
        public boolean isEmpty() {
            return this.getCount() == 0;
        }
    }


    /**
     * Indicates that a TaskProperty was invalid
     */
    private class IllegalTaskStateException extends IllegalStateException{

        final View[] invalid;

        private IllegalTaskStateException(String message, View ...invalid) {
            super(message);
            this.invalid = invalid;
        }
    }
}
