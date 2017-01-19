package nl.tue.san.visualization;

import java.util.HashMap;

/**
 * Created by Maurice on 13-1-2017.
 */

public class Visualization {

    /**
     * Mapping that maps a light to a visualization.
     */
    private final HashMap<String, String> lightVisualization = new HashMap<>();

    /**
     * Indicates how much milliseconds a single timestep on the server should take. This maps the
     * unit-less times in a TaskSet to an amount of time in milliseconds.
     */
    private long timeScale;

    /**
     * Indicates the cycle rate of the server when it is cycling through all tasks that should be
     * visualized at that point. The value indicates how long a single task is visible on a light if
     * multiple tasks are made visible on the same light. The unit is milliseconds.
     */
    private long cycleRate;

    /**
     * The Scheduler to use
     */
    private String scheduler;

    public Visualization(){
        this.timeScale = DEFAULT_TIME_SCALE;
        this.cycleRate = DEFAULT_CYCLE_RATE;
    }

    /**
     * Map the given light to the given visualization.
     * @param light The light for which to set the visualization. This light must be a known by the
     *              {@link VisualizationManager} and may not be null.
     * @param visualization The visualization to use. May be null to indicate that the given light
     *                      is not used. If it is not null, then the visualization must be a known
     *                      by the {@link VisualizationManager}.
     */
    public void set(String light, String visualization){
        if(light == null)
            throw new IllegalArgumentException("Light can't be null");

        this.lightVisualization.put(light, visualization);
    }


    /**
     * Clears all mappings of lights to visualizations. As a result of calling this method nothing
     * is visualized.
     */
    public void clearMapping(){
        this.lightVisualization.clear();
    }

    /**
     * Get the mapping from lights to the visualization they should show.
     * @return The mapping from lights to the visualization the light must show. Values may be
     * mapped to null. A Stored value of null has the same meaning as an absent key, namely that the
     * light shouldn't show anything.
     */
    public HashMap<String, String> getMapping() {
        return new HashMap<>(this.lightVisualization);
    }
    /**
     * Indicates how much time must pass to represent a time increment of "1" in a schedule. The
     * value is expressed as milliseconds.
     * @return The amount of time in milliseconds that must pass for the time in the schedule to
     * increase by 1. The returned value is always strictly larger than 0.
     */
    public long getTimeScale() {
        return timeScale;
    }

    /**
     * Indicate how much time must pass to represent a time increment of "1" in a schedule.
     * @param timeScale The amount of time in milliseconds that must pass for the time in the
     *                  schedule to increase by 1. This value must be strictly larger than 0.
     * @throws IllegalArgumentException If the given {@code timeScale} is not strictly larger than
     * 0.
     */
    public void setTimeScale(long timeScale) {
        if(timeScale <= 0)
            throw new IllegalArgumentException("The time scale must be strictly larger than 0");
        this.timeScale = timeScale;
    }

    /**
     * Get the cycle rate in milliseconds. This indicates the amount of time the server will show a
     * single task on a light if multiple tasks must be shown on a single light.
     * @return The cycle rate in milliseconds. Is always larger than 0.
     */
    public long getCycleRate() {
        return cycleRate;
    }


    /**
     * Define the rate, in milliseconds, at which the server cycles through tasks that are
     * visualized on the same light.
     * @param cycleRate The amount of time a single task must be visible, in milliseconds, if the
     *                  server has to show multiple tasks on the same light.
     */
    public void setCycleRate(long cycleRate) {
        if(cycleRate <= 0)
            throw new IllegalArgumentException("Must be at least 1");
        this.cycleRate = cycleRate;
    }

    /**
     * The default amount of time a single task must be visible, in milliseconds, if the server has
     * to show multiple tasks on the same light.
     */
    public static final long DEFAULT_CYCLE_RATE = 500;


    /**
     * The default amount of time in milliseconds that must pass for the time in the schedule to
     * increase by 1.
     */
    public static final long DEFAULT_TIME_SCALE = 50;


    /**
     * Get the mapping for the specified light
     * @param light The light for which to get the mapping
     * @return The visualization that should be used for the given light. May return null, which
     * indicates that it should display nothing.
     */
    public String getMapping(String light) {
        return this.lightVisualization.get(light);
    }

    public String getScheduler() {
        return scheduler;
    }

    public void setScheduler(String scheduler) {
        this.scheduler = scheduler;
    }
}
