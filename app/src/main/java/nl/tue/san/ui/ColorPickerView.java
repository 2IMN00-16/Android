package nl.tue.san.ui;

import android.app.FragmentManager;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.xdty.preference.colorpicker.ColorPickerDialog;
import org.xdty.preference.colorpicker.ColorPickerSwatch;

import java.util.Arrays;

import nl.tue.san.sanseminar.R;

/**
 * Created by Maurice on 10-1-2017.
 */

public class ColorPickerView extends LinearLayout {

    private final ImageView indicator;
    private ColorPickerDialog dialog;
    private int color;
    private int[] colors;
    private FragmentManager fragmentManager;

    public ColorPickerView(Context context) {
        super(context);
        indicator = new ImageView(context);
        init();
    }

    public ColorPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        indicator = new ImageView(context, attrs);
        init();
    }

    public ColorPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        indicator = new ImageView(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ColorPickerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        indicator = new ImageView(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        indicator.setImageResource(R.drawable.task_color);
        this.addView(indicator, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        this.setGravity(Gravity.CENTER);

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

    }

    public void provideFragmentManager(FragmentManager fragmentManager){
        this.fragmentManager = fragmentManager;
    }


    /**
     * Set the color that ought to be displayed.
     * @param color
     */
    public void setColor(int color){
        this.color = color;
        this.indicator.getDrawable().mutate().setColorFilter(color, PorterDuff.Mode.ADD);
        if(this.dialog != null) {
            this.dialog.setSelectedColor(color);
        }
    }

    /**
     * Set the selected color based on its index in the Color Options array.
     * @param index The index in the Color Options array.
     */
    public void setColorIndex(int index){
        this.setColor(this.colors[index]);
    }

    private void openDialog() {

        final ColorPickerDialog dialog = ColorPickerDialog.newInstance(R.string.color_picker_title, makeCurrentColorSelectable(), color, 5, ColorPickerDialog.SIZE_SMALL);
        dialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {
            @Override
            public void onColorSelected(int color) {
                setColor(color);
                dialog.dismiss();
            }
        });

        if(this.fragmentManager != null)
            dialog.show(this.fragmentManager,"ColorPickerView");
        else
            Log.e("ColorPickerView", "Need access to Activity to be able to show dialog");
    }

    /**
     * Indicates whether the color that is currently selected is one of the colors that is selectable by means of this color picker.
     * @return
     */
    private boolean isCurrentColorSelectable(){
        for(int color : this.colors)
            if(color == this.color)
                return true;
        return false;
    }

    private int[] makeCurrentColorSelectable(){
        boolean addColor = !isCurrentColorSelectable();
        int[] colors = Arrays.copyOf(this.colors, this.colors.length + (addColor ? 1 : 0));
        if(addColor)
            colors[colors.length-1] = this.color;
        return colors;
    }

    /**
     * Get the selected color as a color int.
     * @return
     */
    public int getColorInt(){
        return this.color;
    }

    public void setColorOptions(int...colors){
        this.colors = colors;
    }

    public int[] getColorOptions(){
        return this.colors;
    }
}
