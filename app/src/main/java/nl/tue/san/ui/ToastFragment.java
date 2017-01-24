package nl.tue.san.ui;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.Toast;

/**
 * Created by Maurice on 24-1-2017.
 */

public class ToastFragment extends Fragment {

    protected final void showMessage(String message){
        Context context = this.getContext();
        if(context != null)
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
