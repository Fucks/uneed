package com.project.uneed.activities;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.project.uneed.R;
import com.project.uneed.adapter.DrawerAdapter;
import com.project.uneed.fragments.AnnouncesFragment;
import com.project.uneed.fragments.ConfigFragment;
import com.project.uneed.fragments.CreateAnnouncesFragment;
import com.project.uneed.fragments.MyAnnouncesFragment;
import com.project.uneed.fragments.MyProposalsFragment;
import com.project.uneed.fragments.ProfileFragment;
import com.project.uneed.util.SessionUtil;


public class MainActivity extends ActionBarActivity {

    //First We Declare Titles And Icons For Our Navigation Drawer List View
    //This Icons And Titles Are holded in an Array as you can see

    String TITLES[] = {};
    int ICONS[] = {R.drawable.ic_home, R.drawable.ic_add_circle, R.drawable.ic_message, R.drawable.ic_message, R.drawable.ic_message, R.drawable.ic_person, R.drawable.ic_settings};

    //Similarly we Create a String Resource for the name and email in the header view
    //And we also create a int resource for profile picture in the header view

    private Toolbar toolbar;                              // Declaring the Toolbar Object

    RecyclerView mRecyclerView;                           // Declaring RecyclerView
    RecyclerView.Adapter mAdapter;                        // Declaring Adapter For Recycler View
    RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager
    DrawerLayout Drawer;                                  // Declaring DrawerLayout

    ActionBarDrawerToggle mDrawerToggle;                  // Declaring Action Bar Drawer Toggle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Assinging the toolbar object ot the view
        and setting the the Action bar to our toolbar
         */
        TITLES = new String[]{getString(R.string.menu_home), getString(R.string.menu_new__anuncio), getString(R.string.menu_my__anuncio), getString(R.string.menu_my__proposal), getString(R.string.menu_message), getString(R.string.menu_profile), getString(R.string.menu_config)};

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView); // Assigning the RecyclerView Object to the xml View

        mRecyclerView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size

        mAdapter = new DrawerAdapter(TITLES, ICONS, SessionUtil.currentUser, this);       // Creating the Adapter of DrawerAdapter class(which we are going to see in a bit)
        // And passing the titles,icons,header view name, header view email,
        // and header view profile picture

        mRecyclerView.setAdapter(mAdapter);                              // Setting the adapter to RecyclerView

        mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager

        mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager
        final GestureDetector mGestureDetector = new GestureDetector(MainActivity.this, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });

        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                if (child != null && mGestureDetector.onTouchEvent(motionEvent)) {
                    Drawer.closeDrawers();

                    int position = recyclerView.getChildAdapterPosition(child);

                    if (position > 0) {
                        ((DrawerAdapter) mAdapter).setSelectedItem(position);
                    } else {
                        ((DrawerAdapter) mAdapter).setSelectedItem(6);
                    }

                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

                    switch (position) {
                        case 0:
                            fragmentTransaction.replace(R.id.main_fragment_container, new ProfileFragment());
                            break;
                        case 1:
                            fragmentTransaction.replace(R.id.main_fragment_container, new AnnouncesFragment());
                            break;
                        case 2:
                            fragmentTransaction.replace(R.id.main_fragment_container, new CreateAnnouncesFragment());
                            break;
                        case 3:
                            fragmentTransaction.replace(R.id.main_fragment_container, new MyAnnouncesFragment());
                            break;
                        case 4:
                            fragmentTransaction.replace(R.id.main_fragment_container, new MyProposalsFragment());
                            break;
                        case 5:
                            fragmentTransaction.replace(R.id.main_fragment_container, new MyProposalsFragment());
                            break;
                        case 6:
                            fragmentTransaction.replace(R.id.main_fragment_container, new ProfileFragment());
                            break;
                        case 7:
                            fragmentTransaction.replace(R.id.main_fragment_container, new ConfigFragment());
                            break;

                        default:
//                    ((MainActivity)context).getFragmentManager().beginTransaction().replace(R.id.main_fragment_container, new MyAnnouncesFragment());
                            break;
                    }

                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();

                    mAdapter.notifyDataSetChanged();
                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }
        });

        Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);        // Drawer object Assigned to the view
        mDrawerToggle = new ActionBarDrawerToggle(this, Drawer, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // code here will execute once the drawer is opened( As I dont want anything happened whe drawer is
                // open I am not going to put anything here)
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Code here will execute once drawer is closed
            }


        }; // Drawer Toggle Object Made
        Drawer.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle
        mDrawerToggle.syncState();               // Finally we set the drawer toggle sync State

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment_container, new AnnouncesFragment());
        fragmentTransaction.commit();
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
}