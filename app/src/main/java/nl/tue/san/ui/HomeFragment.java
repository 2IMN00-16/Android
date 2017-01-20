package nl.tue.san.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import nl.tue.san.net.Callback;
import nl.tue.san.net.Server;
import nl.tue.san.sanseminar.R;
import nl.tue.san.tasks.TaskSetIO;
import nl.tue.san.tasks.TaskSetManager;
import nl.tue.san.visualization.VisualizationIO;
import nl.tue.san.visualization.VisualizationManager;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends ProgressableFragment implements Navigatable {

    private VisualizationManager manager;
    private TaskSetManager taskSetManager;

    private Spinner scheduler, taskSet;


    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        this.manager = VisualizationManager.getInstance(this.getContext());
        this.taskSetManager = TaskSetManager.getInstance(this.getContext());

        View inflated = inflater.inflate(R.layout.fragment_home, container, false);

        this.scheduler = (Spinner)inflated.findViewById(R.id.scheduler_spinner);
        this.taskSet = (Spinner)inflated.findViewById(R.id.task_set_spinner);

        super.setProgressBar((ProgressBar) inflated.findViewById(R.id.progress));

        // Listen to changes to the selected scheduler, and report them to the visualization.
        this.scheduler.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                manager.getVisualization().setScheduler((String) parent.getAdapter().getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        display();

        return inflated;
    }

    private void display(){

        /*
         * Update the TaskSet spinner
         */
        {
            // First get all available tasks and sort them
            List<String> taskSets = new LinkedList<>(this.taskSetManager.stored());
            Collections.sort(taskSets);
            this.taskSet.setEnabled(taskSets.size() > 0);

            // Then create basic adapter
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_spinner_item, taskSets);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // Then apply
            this.taskSet.setAdapter(spinnerArrayAdapter);
        }


        /*
         * Update the Scheduler
         */
        {
            final List<String> schedulers = new LinkedList<>();
            // Then create basic adapter
            final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_spinner_item, schedulers);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // Then apply
            this.scheduler.setAdapter(spinnerArrayAdapter);

            // Now try to get new data, and update the view.
            Server.GET("schedulers", new Callback() {
                @Override
                public void onSuccess(String data) {

                    try {
                        JSONArray array = new JSONArray(data);
                        for(int i = 0; i < array.length(); ++i)
                            schedulers.add(array.getString(i));
                        Collections.sort(schedulers);
                        spinnerArrayAdapter.notifyDataSetChanged();

                        if(schedulers.contains(manager.getVisualization().getScheduler()))
                            manager.getVisualization().setScheduler(schedulers.get(0));

                        scheduler.setSelection(schedulers.indexOf(manager.getVisualization().getScheduler()));
                        scheduler.setEnabled(true);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        onFailure();
                    }


                }

                @Override
                public void onFailure() {
                    schedulers.clear();
                    scheduler.setEnabled(false);
                    Toast.makeText(getContext(), "Failed to load schedulers from server", Toast.LENGTH_LONG).show();
                }
            });
        }
    }


    /**
     * Call to start the visualization
     */
    private void startVisualization() {

        final int steps = 5;

        try {
            this.showProgress(0, steps);
            final String taskSet = TaskSetIO.toJSON(this.taskSetManager.get((String) this.taskSet.getSelectedItem())).toString();
            this.showProgress(1, steps);
            final String visualization = VisualizationIO.toJSON(this.manager.getVisualization()).toString();
            this.showProgress(2, steps);
            // This is a nest of 3 requests. Due to the async nature of these requests, I could
            // either try to sync them, or just only send the next of the previous reaches
            // "onSuccess". The latter is easier, but looks uglier. Did that anyway.
            Server.PUT("taskset", new Callback() {
                @Override
                public void onSuccess(String data) {
                    showProgress(3, steps);
                    Server.PUT("settings", new Callback() {
                        @Override
                        public void onSuccess(String data) {
                            showProgress(4, steps);
                            Server.PATCH("restart", new Callback() {
                                @Override
                                public void onSuccess(String data) {
                                    showProgress(steps, steps);
                                    progressCompleted();
                                    Toast.makeText(getContext(), "Visualization was started", Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onFailure() {
                                    progressCompleted();
                                    Toast.makeText(getContext(), "Couldn't get server to start visualization", Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                        @Override
                        public void onFailure() {
                            progressCompleted();
                            Toast.makeText(getContext(), "Couldn't communicate visualization settings to server", Toast.LENGTH_LONG).show();
                        }
                    }, visualization);
                }

                @Override
                public void onFailure() {
                    progressCompleted();
                    Toast.makeText(getContext(), "Couldn't communicate task set to server", Toast.LENGTH_LONG).show();
                }
            }, taskSet);

        } catch (JSONException e) {
            Toast.makeText(this.getContext(), "Couldn't translate objects", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            this.progressCompleted();
        }
    }

    /**
     * Gets the properties for navigation purposes.
     */
    //@Override
    public Navigatable.Properties getProperties() {
        return this.properties;
    }

    private final Navigatable.Properties properties = new Navigatable.Properties.Builder()
                                                                .useTitle(R.string.nav_title_home)
                                                                .useFabIcon(R.drawable.ic_play_arrow)
                                                                .useFabHandler(new View.OnClickListener() {
                @Override
                public void onClick(View v) { startVisualization(); }
            })
                                                                .build();

}
