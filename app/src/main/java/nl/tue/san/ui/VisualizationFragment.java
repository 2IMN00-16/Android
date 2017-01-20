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
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import nl.tue.san.sanseminar.R;
import nl.tue.san.visualization.Visualization;
import nl.tue.san.visualization.VisualizationManager;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VisualizationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VisualizationFragment extends Fragment implements Navigatable, VisualizationManager.OnVisualizationPropertiesChangeListener {

    private VisualizationManager manager;

    private EditText timeScale, cycleRate, animationDuration;
    private LinearLayout lightsContainer;

    private View noLightsMessage;

    private List<LightVisualizationView> lightVisualizationViews = new LinkedList<>();

    /**
     * Timer that will hide the identifying color on each LightVisualizationView once it's task is
     * completed.
     */
    private Timer hideTimer;


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
        this.animationDuration = (EditText)inflated.findViewById(R.id.animation_duration);
        this.noLightsMessage = inflated.findViewById(R.id.message_no_lights);

        display();

        this.manager.addListener(this);

        return inflated;
    }

    private void display(){

        final Visualization visualization = manager.getVisualization();


        /*
         * Update the lights
         */
        this.lightVisualizationViews.clear();
        this.lightsContainer.removeAllViews();

        List<String> lights = new LinkedList<>(VisualizationManager.getInstance().getLights());
        Collections.sort(lights);

        List<String> visualizationOptions = new LinkedList<>(VisualizationManager.getInstance().getVisualizations());
        Collections.sort(visualizationOptions);

        this.lightsContainer.setVisibility(lights.size() == 0 ? View.GONE : View.VISIBLE);
        this.noLightsMessage.setVisibility(lights.size() > 0 ? View.GONE : View.VISIBLE);

        for(String light : lights) {
            LightVisualizationView view = new LightVisualizationView(this.getContext());
            view.setName(light);
            view.showVisualizationOptions(visualization.getMapping(light), visualizationOptions);
            this.lightsContainer.addView(view);
            this.lightVisualizationViews.add(view);
        }

        /*
         * Update the cycle rate
         */
        this.cycleRate.setText(String.format(Locale.getDefault(), "%d",visualization.getCycleRate()));
        this.cycleRate.setHint(String.format(Locale.getDefault(), "%d",Visualization.DEFAULT_CYCLE_RATE));

        /*
         * Update the time scale
         */
        this.timeScale.setText(String.format(Locale.getDefault(), "%d",visualization.getTimeScale()));
        this.timeScale.setHint(String.format(Locale.getDefault(), "%d",Visualization.DEFAULT_TIME_SCALE));

         /*
         * Update the time scale
         */
        this.animationDuration.setText(String.format(Locale.getDefault(), "%d",visualization.getAnimationDuration()));
        this.animationDuration.setHint(String.format(Locale.getDefault(), "%d",Visualization.DEFAULT_ANIMATION_DURATION));

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


    private long getAnimationDurationInput(){
        if(animationDuration.getText().length() > 0)
            return Long.parseLong(animationDuration.getText().toString());
        else
            return Visualization.DEFAULT_ANIMATION_DURATION;
    }


    @Override
    public void onPause() {
        super.onPause();
        try {
           this.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        this.manager.removeListener(this);
    }

    /**
     * Save the current state
     * @throws Exception
     */
    private void save() throws Exception {
        // update visualization
        Visualization visualization = this.manager.getVisualization();

        visualization.setCycleRate(getCycleRateInput());
        visualization.setTimeScale(getTimeScaleInput());
        visualization.setAnimationDuration(getAnimationDurationInput());

        for(LightVisualizationView view : lightVisualizationViews)
            visualization.set(view.getLight(), view.getSelectedVisualization());

        // Finally save everything
        this.manager.write();
    }


    /**
     * Send out a request to the server to identify all lights.
     */
    private void requestIdentifyLights() {
        this.manager.requestIdentification(5000);
    }

    /**
     * Identify the lights by mapping each light to a color, and displaying that color for the given
     * amount of time.
     * @param lightsToColor A mapping that maps the name of a light to a color integer to use as the
     *                      color to display the light.
     * @param time The amount of time for which to display the color on each light before hidin it
     *             again.
     */
    public void identify(final Map<String, Integer> lightsToColor, long time){

        // cancel the current "hide" timer
        synchronized (this) {
            if (this.hideTimer != null)
                this.hideTimer.cancel();

            for(final LightVisualizationView lightVisualizationView : this.lightVisualizationViews){
                String light = lightVisualizationView.getLight();
                if(lightsToColor.containsKey(light)){
                    lightVisualizationView.post(new Runnable() {
                        @Override
                        public void run() {
                            lightVisualizationView.showIdentifyingColor(lightsToColor.get(lightVisualizationView.getLight()));
                        }
                    });
                }
            }

            // start the new hide timer
            this.hideTimer = new Timer();
            this.hideTimer.schedule(new TimerTask(){

                /**
                 * The action to be performed by this timer task.
                 */
                @Override
                public void run() {

                    for(final LightVisualizationView lightVisualizationView : VisualizationFragment.this.lightVisualizationViews) {
                        lightVisualizationView.post(new Runnable() {
                            @Override
                            public void run() {
                                lightVisualizationView.hideIdentifyingColor();
                            }
                        });
                    }
                    synchronized (VisualizationFragment.this) {
                        VisualizationFragment.this.hideTimer = null;
                    }
                }
            }, time);
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
            .useFabIcon(R.drawable.ic_lightbulb)
            .useFabHandler(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VisualizationFragment.this.requestIdentifyLights();
                }
            })
            .build();

    @Override
    public void onAvailableLightsChange() {
        this.display();
    }

    @Override
    public void onAvailableVisualizationsChange() {
        this.display();
    }

    @Override
    public void onIdentificationStarted(Map<String, Integer> lightToColors, long duration) {
        this.identify(lightToColors, duration);
    }
}
