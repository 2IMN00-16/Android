package nl.tue.san.sanseminar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VisualizationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VisualizationFragment extends Fragment implements Navigatable {


    public VisualizationFragment() {
        // Required empty public constructor
    }

    public static VisualizationFragment newInstance() {
        VisualizationFragment fragment = new VisualizationFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_visualization, container, false);
    }

    /**
     * Gets the properties for navigation purposes.
     */
    //@Override
    public Properties getProperties() {
        return this.properties;
    }

    private final Properties properties = new Properties.Builder()
                                                                .useTitle(R.string.nav_title_visualization)
                                                                .build(this);
}
