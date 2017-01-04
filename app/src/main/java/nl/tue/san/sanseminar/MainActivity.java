package nl.tue.san.sanseminar;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        this.navigationView = (NavigationView) findViewById(R.id.nav_view);
        this.navigationView.setNavigationItemSelectedListener(this);

        // Set the default item to be selected
        this.setContentType(DEFAULT_CONTENT_TYPE);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Given a resource id for a button in the navigation drawer, this method determines which
     * ContentType should be displayed. If
     * @param id The resource id.
     * @return The ContentType instance. This is never null.
     * @throws UnknownNavigationResourceException If the given resource id can't be translated to a
     * ContentType.
     */
    private static ContentType contentTypeForNavigationResource(int id){
        for(ContentType type : ContentType.values())
            if(type.navigationResource == id)
                return type;
        throw new UnknownNavigationResourceException(id);
    }
    /**
     * Translate a ContentType into a corresponding Fragment instance.
     * @param contentType The ContentType for which to construct a fragment. Can't be null.
     * @return A Fragment instance that corresponds to the given content type.
     */
    private static Fragment constructFragmentForContentType(ContentType contentType){

        if(contentType == null)
            throw new IllegalArgumentException("ContentType can't be null");

        switch(contentType){

            case HOME:
                return HomeFragment.newInstance();
            case TASK_SET:
                return TaskSetFragment.newInstance();
            default:
                throw new IllegalStateException("Unknown ContentType "+contentType);
        }



    }


    /**
     * Indicates the ContentType that is currently being displayed
     */
    private ContentType currentContentType;

    private static final ContentType DEFAULT_CONTENT_TYPE = ContentType.HOME;
    /**
     * Updates the current content type that is being displayed. If the given content type differs
     * from the currently displayed content type, then the fragment that is currently displayed is
     * replaced.
     * @param contentType The content type to display. Can't be null. If it is equal to the current
     *                    content type then calling this method will have no effect.
     */
    private synchronized void setContentType(ContentType contentType) {

        // Check for null
        if (contentType == null)
            throw new IllegalArgumentException("Given ContentType can't be null");

        // Check whether we're changing
        if (this.currentContentType == contentType)
            return;

        // update the fragment
        Fragment fragment = constructFragmentForContentType(contentType);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_main, fragment)
                .commit();

        // Start to update all other properties
        final ActionBar actionBar = this.getSupportActionBar();
        final Navigatable.Properties properties = fragment instanceof Navigatable ? ((Navigatable) fragment).getProperties() : null;
        final FloatingActionButton fab = (FloatingActionButton) this.findViewById(R.id.fab);


        // Apply the title properties to the action bar, if applicable
        if (actionBar != null){
            if (properties != null && properties.usesTitle())
                actionBar.setTitle(properties.getTitle(this));
            else
                actionBar.setTitle("SAN Seminar");
        }

        // Show and handle the Floating Action Button, if applicable
        if(properties != null && properties.usesFloatingActionButton()){
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(properties.getFabClickListener());
            fab.setImageResource(properties.getFabIconResource());
        } else {
            fab.setVisibility(View.GONE);
            fab.setOnClickListener(null);
        }

        // Update the current content type, and reflect this in the navigation drawer.
        this.currentContentType = contentType;
        this.navigationView.setCheckedItem(this.currentContentType.navigationResource);

    }







    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        try {
            this.setContentType(contentTypeForNavigationResource(item.getItemId()));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Unknown navigation item", Toast.LENGTH_LONG).show();
            return true;
        }

        // Fragment is updated, close drawer and indicate that it is handled.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Enumeration indicating the three different types of content that can be shown on the screen:
     * <ul>
     *     <li>HOME, indicating the home screen of the app</li>
     *     <li>TASK_SET, indicating that an overview of all tasks is shown</li>
     *     <li>VISUALIZATION, indicating that an overview of all visualization rules regarding a
     *     schedule are shown.</li>
     * </ul>
     */
    private enum ContentType {
        HOME(R.id.nav_home),
        TASK_SET(R.id.nav_task_set),
        VISUALIZATION(R.id.nav_visualization);

        /**
         * The identifier of the button in the navigation drawer that leads to a ContentType being
         * selected.
         */
        public final int navigationResource;

        ContentType(int navigationResource) {
            this.navigationResource = navigationResource;
        }
    }

    /**
     * Exception class to indicate that for the given resource we do not know how to navigate elsewhere
     */
    private static final class UnknownNavigationResourceException extends RuntimeException{
        UnknownNavigationResourceException(int resourceId){
            super("The resource with id 0x"+Integer.toHexString(resourceId)+"("+resourceId+") is not known as a navigation resource");
        }
    }
}
