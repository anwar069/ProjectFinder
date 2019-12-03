package com.persistent.medicalmcq;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.persistent.medicalmcq.adapter.ResultDetailsListAdapter;
import com.persistent.medicalmcq.data.impl.DatabaseHandler;
import com.persistent.medicalmcq.model.Answer;
import com.persistent.medicalmcq.model.Question;

import java.util.ArrayList;
import java.util.HashMap;

public class ResultDetailsActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private NavigationDrawerFragment mNavigationDrawerFragment;
    boolean onLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_details);
        final String subTopicName = getIntent().getStringExtra("subTopicName");
        final int topicID = getIntent().getIntExtra("subTopicID", -1);
        android.support.v7.app.ActionBar supportActionBar = getSupportActionBar();

        supportActionBar.setTitle(subTopicName);
        final int color = getIntent().getIntExtra("Color", 0);
        supportActionBar.setBackgroundDrawable(new ColorDrawable(color));

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(getApplicationContext(),
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        mNavigationDrawerFragment.uncheckAllItem();

        ArrayList<Question> questionList = new ArrayList<Question>();
        HashMap<Question, ArrayList<Answer>> attemptedQuestions = new HashMap<Question, ArrayList<Answer>>();
        final DatabaseHandler databaseHandler = DatabaseHandler.getInstance(getApplicationContext());
        attemptedQuestions = databaseHandler.getResultDetailsByTopicID(topicID);

        ((ListView) findViewById(R.id.lvResDetails)).setAdapter(new ResultDetailsListAdapter(this, attemptedQuestions));

        ((Button)findViewById(R.id.btnGoToSubtopics)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iSubTopics = new Intent(ResultDetailsActivity.this, SubTopicActivity.class);
                iSubTopics.putExtra("FromResultDetails",true);
                iSubTopics.putExtra("btnColor", color);
                iSubTopics.putExtra("TOPIC",databaseHandler.getTopicByID(databaseHandler.getTopicByID(topicID).getParentTopicID()));
                startActivity(iSubTopics);
            }
        });

    }

    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_result_details, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, ResultActivity.PlaceholderFragment.newInstance(position + 1))
                .commit();

//        Toast.makeText(this, position + " clicked", Toast.LENGTH_SHORT).show();
        if (onLoad) {
            onLoad = false;
        } else {
            Intent iMCQHome = new Intent(this, MCQHomeActivity.class);
            iMCQHome.putExtra("FromResult", true);
            iMCQHome.putExtra("Position", position);
            iMCQHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(iMCQHome);
        }


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
        }
    }

}

