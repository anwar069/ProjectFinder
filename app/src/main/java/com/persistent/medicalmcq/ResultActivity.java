package com.persistent.medicalmcq;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.persistent.medicalmcq.adapter.GridAdapter;
import com.persistent.medicalmcq.adapter.GridResultAdapter;
import com.persistent.medicalmcq.util.UiUtils;


public class ResultActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    Button btnHome,btnProgress;
    private TextView tvScore;
    DonutProgress donutProgress;
    private Handler handler = new Handler();
    private NavigationDrawerFragment mNavigationDrawerFragment;

    float value=0;
    int count=1;
    float progressBarVal=0;
    int progressAminSecs=1000;
    int progressAminTick=10;
    boolean onLoad=true;



    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
      /* do what you need to do */
            value+=progressBarVal/100;
            donutProgress.setProgress((int)value);

      /* and here comes the "trick" */
            if (count++<progressAminSecs/10)
                    handler.postDelayed(this, progressAminTick);
            else
                donutProgress.setProgress((int)progressBarVal);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        int score=getIntent().getIntExtra("SCORE",-1);
        int outOf=getIntent().getIntExtra("OUTOF",-1);
        final int topicID=getIntent().getIntExtra("subTopicID",-1);


        final String subTopicName = getIntent().getStringExtra("subTopicName");

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

        float weigth=(float)score/outOf;

        Display display = getWindowManager().getDefaultDisplay();
        int llWidth=display.getWidth();


//        int btnWidth = (int)(llWidth * weigth);

//        ResizeWidthAnimation anim = new ResizeWidthAnimation(btnProgress, btnWidth - UiUtils.getDIP(getBaseContext(),15));
        ((TextView)findViewById(R.id.tvScore)).setText( score + "/" + outOf);
        donutProgress=(DonutProgress)findViewById(R.id.donut_progress);


//        donutProgress.setProgress(100*score/outOf);
        progressBarVal=100*score/outOf;

        handler.postDelayed(runnable, progressAminTick);



//        anim.setDuration(1500);
//        btnProgress.startAnimation(anim);


//        String weightage=String.format("%.2f", weigth*100)+"%";
//        tvScore.setText(weightage);
//        Animation animScore;
//                Toast.makeText(mContext, "Topic " + button.getText(), Toast.LENGTH_LONG).show();

//        animScore = AnimationUtils.loadAnimation(getBaseContext(),
//                R.anim.score_anim);
//        tvScore.startAnimation(animScore);

        ((Button) findViewById(R.id.btnHome)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iResultDeatils = new Intent(ResultActivity.this, ResultDetailsActivity.class);
                iResultDeatils.putExtra("subTopicName", subTopicName);
                iResultDeatils.putExtra("Color", color);
                iResultDeatils.putExtra("subTopicID", topicID);
                startActivity(iResultDeatils);
                finish();
            }
        });
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, ResultActivity.PlaceholderFragment.newInstance(position + 1))
                .commit();

//        Toast.makeText(this, position + " clicked", Toast.LENGTH_SHORT).show();
        if(onLoad){
            onLoad=false;
        }else {
            Intent iMCQHome=new Intent(this,MCQHomeActivity.class);
            iMCQHome.putExtra("FromResult",true);
            iMCQHome.putExtra("Position",position);
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
