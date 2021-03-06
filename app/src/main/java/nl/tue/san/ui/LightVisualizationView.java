package nl.tue.san.ui;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import nl.tue.san.sanseminar.R;

/**
 * Created by Maurice on 13-1-2017.
 */

public class LightVisualizationView extends LinearLayout {

    private ImageView color;
    private Spinner visualization;
    private TextView name;

    public LightVisualizationView(Context context) {
        super(context);
        this.initLayout();
    }

    public LightVisualizationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initLayout();
    }

    public LightVisualizationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initLayout();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LightVisualizationView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initLayout();
    }


    /**
     * Initializes the layout. After this call the
     */
    private void initLayout(){
        LayoutInflater.from(this.getContext()).inflate(R.layout.home_light_row, this);
        this.color = (ImageView) this.findViewById(R.id.light_color);
        this.visualization = (Spinner) this.findViewById(R.id.light_visualization);
        this.name = (TextView) this.findViewById(R.id.light_name);
    }

    /**
     * Specify which visualization options there are.
     * @param options The options that the user can pick from for this light.
     */
    public void showVisualizationOptions(String selected, List<String> options){
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_spinner_item, options);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.visualization.setAdapter(spinnerArrayAdapter);
        this.visualization.setSelection(options.indexOf(selected));
    }

    /**
     * Shows an identifying color for this light. The color is modified to be completely opaque.
     * @param color The color to display.
     */
    public void showIdentifyingColor(int color){
        this.color.getDrawable().mutate().setColorFilter(0x50000000 | (0x00FFFFFF & color), PorterDuff.Mode.SRC_ATOP);
        this.color.setVisibility(VISIBLE);
    }

    /**
     * Disables or hides the identfying color for this light.
     */
    public void hideIdentifyingColor(){
        this.color.setVisibility(INVISIBLE);
    }

    /**
     * Set the name of the light
     * @param name The name to use
     */
    public void setName(String name){
        this.name.setText(name);
    }
    
    public String getSelectedVisualization(){
        return (String) this.visualization.getSelectedItem();
    }

    public String getLight() {
        return this.name.getText().toString();
    }
}
