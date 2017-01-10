package nl.tue.san.sanseminar;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Locale;

import nl.tue.san.sanseminar.components.Task;

/**
 * Created by Maurice on 8-1-2017.
 */

public class TaskView extends RelativeLayout {

    private TextView priority, name;
    private Drawable color;

    private Task task;

    public TaskView(Context context) {
        super(context);
        inflate(context);
    }

    public TaskView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context);
    }

    public TaskView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TaskView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        inflate(context);
    }

    private void inflate(Context context){
        LayoutInflater.from(context).inflate(R.layout.task, this);
        this.name = (TextView)this.findViewById(R.id.task_name);
        this.priority = (TextView)this.findViewById(R.id.task_priority);

        this.color = ((ImageView)this.findViewById(R.id.task_color)).getDrawable();
    }

    public void setTask(Task task){
        this.task = task;
        this.syncView();
    }

    /**
     * Synchronize the view with the current Task.
     */
    private void syncView(){
        this.name.setText(this.task.getName());
        this.priority.setText(String.format(Locale.getDefault(), "%d", this.task.getPriority()));
        this.color.mutate().setColorFilter(this.task.getColor(), PorterDuff.Mode.ADD);
    }

}
