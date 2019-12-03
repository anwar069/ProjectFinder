package com.persistent.medicalmcq;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.persistent.medicalmcq.adapter.SubTopicResultListAdapter;
import com.persistent.medicalmcq.data.impl.DatabaseHandler;
import com.persistent.medicalmcq.model.Topic;
import com.persistent.medicalmcq.util.ScoreManager;
import com.persistent.medicalmcq.util.UiUtils;

import java.util.ArrayList;


public class SubTopicResultsActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    int color;
    private static LayoutInflater inflater = null;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    boolean onLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_topic);

        ViewHolder holder = new ViewHolder();
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(R.color.full_transparent));
        getSupportActionBar().setTitle("");
        color = getIntent().getIntExtra("btnColor", 0);

        ArrayList<ArrayList<Integer>> results = (ArrayList<ArrayList<Integer>>) getIntent().getSerializableExtra("RESULTS");

        final View rootView;
        rootView = inflater.inflate(R.layout.layout_result_grid_item, null);
        rootView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        holder.txtViewTopicName = (TextView) rootView.findViewById(R.id.txtViewTopicName);
        holder.txtViewSubtopicCount = (TextView) rootView.findViewById(R.id.txtViewSubTopicCount);
        holder.txtViewTopicScore = (TextView) rootView.findViewById(R.id.txtViewTopicScore);
        holder.progressBarScore = (ProgressBar) rootView.findViewById(R.id.progressBarScore);


        holder.txtViewTopicName.setText(getIntent().getStringExtra("topicName"));
        holder.txtViewSubtopicCount.setText(getIntent().getIntExtra("count", -1) + "");
        holder.txtViewTopicScore.setText(getIntent().getIntExtra("totalScore", -1) + " out of " + getIntent().getIntExtra("outof", -1));
        rootView.setBackgroundColor(color);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(getApplicationContext(),
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        mNavigationDrawerFragment.uncheckAllItem();

        ((LinearLayout) findViewById(R.id.llHeader)).addView(rootView);

        ((ListView) findViewById(R.id.lvSubTopics)).setAdapter(new SubTopicResultListAdapter(this, results, color));

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_sub_topic_results, menu);
//        return true;
//    }

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

    class ViewHolder {
        private TextView txtViewTopicName;
        private TextView txtViewSubtopicCount;
        private TextView txtViewTopicScore;
        private ProgressBar progressBarScore;
    }
}
