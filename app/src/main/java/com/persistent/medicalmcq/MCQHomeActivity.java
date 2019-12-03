package com.persistent.medicalmcq;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.persistent.medicalmcq.adapter.GridAdapter;
import com.persistent.medicalmcq.adapter.GridResultAdapter;
import com.persistent.medicalmcq.data.impl.DatabaseHandler;
import com.persistent.medicalmcq.data.impl.FileDataLoaderImpl;


public class MCQHomeActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, SearchView.OnQueryTextListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    public static final String DATABASE_CREATED = "DATABASE_CREATED";
    public static final String FILE_NAME = "demo.csv";
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    GridView gridview;
    String prefFileName = "MedicalMCQPrefs";
    SharedPreferences preferences;
    final int DB_VERSION = 13;
    private LinearLayout lLayoutInfoPage;
    private RelativeLayout relLayoutGridPage;
    private GridAdapter gridViewadapter;
    private Boolean isHome = true, firstLoad = true;
    private TextView mStatusView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getSharedPreferences(prefFileName, MODE_PRIVATE);
        // gridview.setAdapter(new GridAdapter(this));
        gridViewadapter = new GridAdapter(this);
        Boolean isDBExists = preferences.getBoolean(DATABASE_CREATED, false);
        int prevDBVersion = preferences.getInt("DB_VERSION", -1);
//        Toast.makeText(getApplicationContext(),"DB CREATED : "+isDBExists, Toast.LENGTH_LONG).show();

        if (!isDBExists || prevDBVersion < DB_VERSION) {
            LoadDataIntoDB();
        }

        setContentView(R.layout.activity_mcqhome);
        gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(gridViewadapter);

        lLayoutInfoPage = (LinearLayout) findViewById(R.id.linLayout_Info);
        relLayoutGridPage = (RelativeLayout) findViewById(R.id.relLayout_Topic);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(getApplicationContext(),
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        boolean fromResultAtivity = getIntent().getBooleanExtra("FromResult", false);
        if(fromResultAtivity){
            int position = getIntent().getIntExtra("Position", 0);
            mNavigationDrawerFragment.selectItem(position);

            onSectionAttached(position+1);
            invalidateOptionsMenu();
        }



    }

    private void LoadDataIntoDB() {
        FileDataLoaderImpl csvLoader = new FileDataLoaderImpl();
        csvLoader.loadCSVToBeans(FILE_NAME, getApplicationContext());

        DatabaseHandler databaseHandler = DatabaseHandler.getInstance(this);
        databaseHandler.truncateAndRecreateDB(DB_VERSION);
        databaseHandler.saveTopics(FileDataLoaderImpl.topics);
        databaseHandler.saveQuestions(FileDataLoaderImpl.questions);

        SharedPreferences.Editor editor = getSharedPreferences(prefFileName, MODE_PRIVATE).edit();
        editor.putBoolean(DATABASE_CREATED, true);
        editor.putInt("DB_VERSION", DB_VERSION);
        editor.commit();

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();

//        Toast.makeText(this, position + " clicked", Toast.LENGTH_SHORT).show();


        switch (position) {
            case 0:
                isHome = true;
                medicalMCQClicked();
                break;

            case 1:
                isHome = false;
                resultsClicked();
                break;

            case 2:
                isHome = false;
                infoClicked();
                break;

        }
    }


    @Override
    public void onBackPressed() {

        if (isHome) {
            super.onBackPressed();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
        } else {
            mNavigationDrawerFragment.selectItem(0);
            onSectionAttached(1);
            invalidateOptionsMenu();
//            this.onCreateOptionsMenu();
        }
    }

    private void medicalMCQClicked() {
        try {
            lLayoutInfoPage.setVisibility(View.GONE);
            relLayoutGridPage.setVisibility(View.VISIBLE);
            gridViewadapter = new GridAdapter(this);
            gridview.setAdapter(gridViewadapter);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    private void resultsClicked() {

        lLayoutInfoPage.setVisibility(View.GONE);
        relLayoutGridPage.setVisibility(View.VISIBLE);
        gridview.setAdapter(new GridResultAdapter(this));
    }

    private void infoClicked() {
        relLayoutGridPage.setVisibility(View.GONE);
        lLayoutInfoPage.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DatabaseHandler.getInstance(this).close();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        gridview.setNumColumns(getResources().getInteger(R.integer.grid_rows));

    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            restoreActionBar();
            if (isHome) {
                getMenuInflater().inflate(R.menu.mcqhome, menu);
                // Associate searchable configuration with the SearchView
//                if(firstLoad) {
                SearchManager searchManager =
                        (SearchManager) getSystemService(this.SEARCH_SERVICE);
                SearchView searchView =
                        (SearchView) menu.findItem(R.id.menu_search).getActionView();
                searchView.setSearchableInfo(
                        searchManager.getSearchableInfo(getComponentName()));
                searchView.setOnQueryTextListener(this);
//                    firstLoad=false;
//                }
            }
            return true;

        }
        return super.onCreateOptionsMenu(menu);
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


    private boolean isAlwaysExpanded() {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
//        Toast.makeText(this,newText,Toast.LENGTH_SHORT).show();
        gridViewadapter.getFilter().filter(newText);
        return false;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_mcqhome, container, false);
            return rootView;
        }


        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MCQHomeActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
