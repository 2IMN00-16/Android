package nl.tue.san.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Spinner;

import nl.tue.san.sanseminar.R;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements Navigatable {


    private ListView lightsListView;
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
        View inflated = inflater.inflate(R.layout.fragment_home, container, false);

        this.lightsListView = (ListView)inflated.findViewById(R.id.lights);
        this.scheduler = (Spinner)inflated.findViewById(R.id.scheduler_spinner);
        this.taskSet = (Spinner)inflated.findViewById(R.id.task_set_spinner);



        return inflated;
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
                                                                .build();

}
