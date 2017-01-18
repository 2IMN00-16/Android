package nl.tue.san.ui;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import nl.tue.san.sanseminar.R;
import nl.tue.san.tasks.Task;
import nl.tue.san.tasks.TaskSet;
import nl.tue.san.tasks.TaskSetManager;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TaskSetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TaskSetFragment extends Fragment implements Navigatable {


    private TaskSetManager taskSetManager = TaskSetManager.getInstance(this.getContext());
    private ViewPager viewPager;
    private TaskSetAdapter adapter;

    public TaskSetFragment() {
        // Required empty public constructor
    }

    public static TaskSetFragment newInstance() {
        TaskSetFragment fragment = new TaskSetFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.viewPager = (ViewPager) inflater.inflate(R.layout.fragment_task_set, container, false);

        this.adapter = new TaskSetAdapter();
        this.viewPager.setAdapter(this.adapter);
        this.taskSetManager.addOnTaskSetsChangedListener(this.adapter);

        return viewPager;
    }

    public void onDestroyView(){
        super.onDestroyView();
        // remove the listener to prevent it from staying alive.
        this.taskSetManager.removeOnTaskSetsChangedListener(this.adapter);
        this.adapter = null;
        this.viewPager = null;
    }
    /**
     * Gets the properties for navigation purposes.
     */
    //@Override
    public Properties getProperties() {
        return this.properties;
    }

    private final Properties properties = new Properties.Builder()
                                                                .useTitle(R.string.nav_title_task_set)
                                                                .useFabIcon(R.drawable.ic_plus)
                                                                .useFabHandler(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        createNewTask();
                                                                    }
                                                                })
                                                                .useMenu(R.menu.menu_task_set)
                                                                .build();

    /**
     * Get the current TaskSet.
     * @return The TaskSet that is currently displaying.
     */
    private TaskSet current(){
        return taskSetManager.get(this.viewPager.getCurrentItem());
    }

    /**
     * Create a new TaskSet under the given name.
     */
    private void createNewTaskSet(String taskSetName){
        TaskSet taskSet = new TaskSet(taskSetName);
        this.taskSetManager.register(taskSet);
    }

    /**
     * Prompt the user to create a new task set
     */
    private void requestCreateNewTaskSet(){
        createNewTaskSet(""+System.currentTimeMillis());
    }

    /**
     * Remove the current taskSet.
     */
    private void deleteCurrentTaskset(){
        this.taskSetManager.remove(this.current());
    }

    /**
     * Creates a new Task in the currently displaying TaskSet.
     */
    private void createNewTask() {
        this.createOrModify(current(), null);
    }


    private void createOrModify(TaskSet taskSet, String taskName){
        Intent intent = new Intent(TaskSetFragment.this.getActivity(), TaskActivity.class);
        intent.putExtra(TaskActivity.INTENT_EXTRA_TASK, taskName);
        intent.putExtra(TaskActivity.INTENT_EXTRA_TASKSET, taskSet.getName());
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.menu_task_set_create:
                requestCreateNewTaskSet(); return true;
            case R.id.menu_task_set_delete:
                deleteCurrentTaskset(); return true;
            case R.id.menu_task_set_download:
                this.taskSetManager.loadFromServer(); return true;
        }
        return false;

    }

    /**
     * PagerAdapter that creates a page for each TaskSet.
     */
    private final class TaskSetAdapter extends PagerAdapter implements TaskSetManager.OnTaskSetsChangedListener {

        private int count = taskSetManager.size();
        @Override
        public int getCount() {
            synchronized (this){
                return count;
            }
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return object == view;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return taskSetManager.get(position).getName();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
            ((ListView)object).setAdapter(null);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ListView listView = new ListView(getContext());
            listView.setAdapter(new TaskAdapter(taskSetManager.get(position)));
            container.addView(listView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            return listView;
        }

        @Override
        public int getItemPosition(Object object) {
            TaskSet displayed = ((TaskAdapter) ((ListView) object).getAdapter()).taskSet;

            int index = taskSetManager.indexOf(displayed);
            return index >= 0 ? index : POSITION_NONE;
        }

        @Override
        public void onTaskSetAdded(TaskSet taskSet) {
            synchronized (this) {
                this.count = taskSetManager.size();
                this.notifyDataSetChanged();
            }
        }

        @Override
        public void onTaskSetRemoved(TaskSet taskSet) {
            synchronized (this){
                this.count = taskSetManager.size();
                this.notifyDataSetChanged();
            }
        }
    }

    /**
     * Adapter for lists that displays all tasks.
     */
    private class TaskAdapter implements ListAdapter {

        private TaskSet taskSet;
        private TaskViewClickListener taskViewClickListener;

        private TaskAdapter(TaskSet taskSet){

            this.taskSet = taskSet;
            this.taskViewClickListener = new TaskViewClickListener(taskSet);
        }

        /**
         * Indicates whether all the items in this adapter are enabled. If the
         * value returned by this method changes over time, there is no guarantee
         * it will take effect.  If true, it means all items are selectable and
         * clickable (there is no separator.)
         *
         * @return True if all items are enabled, false otherwise.
         * @see #isEnabled(int)
         */
        @Override
        public boolean areAllItemsEnabled() {
            return true;
        }

        /**
         * Returns true if the item at the specified position is not a separator.
         * (A separator is a non-selectable, non-clickable item).
         * <p>
         * The result is unspecified if position is invalid. An {@link ArrayIndexOutOfBoundsException}
         * should be thrown in that case for fast failure.
         *
         * @param position Index of the item
         * @return True if the item is not a separator
         * @see #areAllItemsEnabled()
         */
        @Override
        public boolean isEnabled(int position) {
            return true;
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
            return this.taskSet.size();
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
            return this.taskSet.get(position);
        }

        /**
         * Get the row id associated with the specified position in the list.
         *
         * @param position The position of the item within the adapter's data set whose row id we want.
         * @return The id of the item at the specified position.
         */
        @Override
        public long getItemId(int position) {
            return this.taskSet.get(position).hashCode();
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

            TaskView view = (TaskView) convertView;
            if(view == null) {
                view = new TaskView(getContext());
                view.setOnClickListener(taskViewClickListener);
            }

            view.setTask((Task) this.getItem(position));

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
            return getCount() == 0;
        }
    }

    private class TaskViewClickListener implements View.OnClickListener{

        private TaskSet taskSet;

        private TaskViewClickListener(TaskSet taskSet) {
            this.taskSet = taskSet;
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            if(v instanceof TaskView) {
                createOrModify(taskSet, ((TaskView)v).getTask().getName());
            }
        }
    }
}
