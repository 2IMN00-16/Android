package nl.tue.san.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import nl.tue.san.sanseminar.R;
import nl.tue.san.visualization.Visualization;
import nl.tue.san.visualization.VisualizationManager;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VisualizationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VisualizationFragment extends Fragment implements Navigatable {

    private VisualizationManager manager;

    private EditText timeScale, cycleRate;
    private LinearLayout lightsContainer;

    private List<LightVisualizationView> lightVisualizationViews;



    public VisualizationFragment() {
        // Required empty public constructor
    }

    public static VisualizationFragment newInstance() {
        return new VisualizationFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        this.manager = VisualizationManager.getInstance(this.getContext());

        View inflated = inflater.inflate(R.layout.fragment_visualization, container, false);

        this.lightsContainer = (LinearLayout)inflated.findViewById(R.id.lights_container);
        this.timeScale = (EditText)inflated.findViewById(R.id.time_scale);
        this.cycleRate = (EditText)inflated.findViewById(R.id.cycle_rate);

        this.lightVisualizationViews = new LinkedList<>();


        display();

        return inflated;
    }

    private void display(){

        final Visualization visualization = manager.getVisualization();


        /*
         * Update the lights
         */
        {
            List<String> lights = new LinkedList<>(VisualizationManager.getInstance().getLights());
            Collections.sort(lights);

            List<String> visualizationOptions = new LinkedList<>(VisualizationManager.getInstance().getVisualizations());
            Collections.sort(visualizationOptions);



            for(String light : lights) {
                LightVisualizationView view = new LightVisualizationView(this.getContext());
                view.setName(light);
                view.showVisualizationOptions(visualization.getMapping(light), visualizationOptions);
                this.lightsContainer.addView(view);
                this.lightVisualizationViews.add(view);
            }
        }

        this.cycleRate.setText(String.format(Locale.getDefault(), "%d",visualization.getCycleRate()));
        this.cycleRate.setHint(String.format(Locale.getDefault(), "%d",Visualization.DEFAULT_CYCLE_RATE));

        this.timeScale.setText(String.format(Locale.getDefault(), "%d",visualization.getTimeScale()));
        this.timeScale.setHint(String.format(Locale.getDefault(), "%d",Visualization.DEFAULT_TIME_SCALE));
    }

    private long getCycleRateInput(){

        if(cycleRate.getText().length() > 0)
            return Long.parseLong(cycleRate.getText().toString());
        else
            return Visualization.DEFAULT_CYCLE_RATE;
    }

    /**
     * Get the time scale that was given by the user, or the default if the user didn't input anything.
     * @return The time scale that the user has indicated to use.
     */
    private long getTimeScaleInput(){
        if(timeScale.getText().length() > 0)
            return Long.parseLong(timeScale.getText().toString());
        else
            return Visualization.DEFAULT_TIME_SCALE;
    }

    @Override
    public void onPause() {
        super.onPause();

        try {
            // update visualization
            Visualization visualization = this.manager.getVisualization();

            visualization.setCycleRate(getCycleRateInput());
            visualization.setTimeScale(getTimeScaleInput());

            for(LightVisualizationView view : lightVisualizationViews)
                visualization.set(view.getLight(), view.getSelectedVisualization());

            // Finally save everything
            this.manager.write();
        } catch (Exception e) {
            e.printStackTrace();
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
            .useTitle(R.string.nav_title_visualization)
            .build();

}
