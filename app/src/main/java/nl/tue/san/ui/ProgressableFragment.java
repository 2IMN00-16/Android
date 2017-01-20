package nl.tue.san.ui;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ProgressBar;

/**
 * Created by Maurice on 20-1-2017.
 */

public class ProgressableFragment extends Fragment {


    private ProgressBar progressBar;

    protected final void setProgressBar(ProgressBar progressBar){
        this.progressBar = progressBar;
    }

    /**
     * Show the given amount of process
     * @param step The progress
     * @param total The maximally achievable progress.
     */
    protected final void showProgress(final int step, final int total){
        this.progressBar.post(new Runnable() {
            @Override
            public void run() {
                progressBar.setIndeterminate(false);
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setMax(total);
                progressBar.setProgress(step);
            }
        });
    }

    protected final void showIndeterminate(){
        this.progressBar.post(new Runnable() {
            @Override
            public void run() {
                progressBar.setIndeterminate(true);
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Show that progress is complete. This hides the progressbar.
     */
    protected final void progressCompleted(){
        this.progressBar.post(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
}
