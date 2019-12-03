package com.persistent.medicalmcq.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.persistent.medicalmcq.R;
import com.persistent.medicalmcq.SubTopicActivity;
import com.persistent.medicalmcq.SubTopicResultsActivity;
import com.persistent.medicalmcq.data.impl.DatabaseHandler;
import com.persistent.medicalmcq.model.Topic;
import com.persistent.medicalmcq.util.ScoreManager;
import com.persistent.medicalmcq.util.UiUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by bhushan_bhoyare on 5/13/15.
 */
public class GridResultAdapter  extends BaseAdapter{
    private Context mContext;
    TypedArray ta;
    ArrayList<Topic> topics= new ArrayList<Topic>();
    private static LayoutInflater inflater=null;
    HashMap<Integer,ArrayList<ArrayList<Integer>>> resultMap;
    ArrayList<Integer> parentIdlist;
    public GridResultAdapter(Context c) {
        mContext = c;
        ta=  c.getResources().obtainTypedArray(R.array.metroColors);
        DatabaseHandler databaseHandler =  DatabaseHandler.getInstance(c);
        topics = databaseHandler.getTopicList();
        inflater = ( LayoutInflater )c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        resultMap = DatabaseHandler.getInstance(mContext).getAllResults();
        parentIdlist = new ArrayList(resultMap.keySet());
    }
    public int getCount() {
        return resultMap.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        final View rootView;
        rootView = inflater.inflate(R.layout.layout_result_grid_item, null);
        rootView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, UiUtils.getDIP(mContext,160)));
        final ArrayList<ArrayList<Integer>> arrayLists = resultMap.get(parentIdlist.get(position));
        DatabaseHandler databaseHandler = DatabaseHandler.getInstance(mContext);
        final Topic topic = databaseHandler.getTopicByID(parentIdlist.get(position));
        holder.txtViewTopicName = (TextView) rootView.findViewById(R.id.txtViewTopicName);
        holder.txtViewSubtopicCount = (TextView) rootView.findViewById(R.id.txtViewSubTopicCount);
        holder.txtViewTopicScore = (TextView) rootView.findViewById(R.id.txtViewTopicScore);
        holder.progressBarScore = (ProgressBar) rootView.findViewById(R.id.progressBarScore);


        holder.txtViewTopicName.setText(topic.getTopicName());
        final int subTopicCount = databaseHandler.getSubTopicListByTopicID(topic.getTopicID()).size();
        holder.txtViewSubtopicCount.setText(subTopicCount + "");
        final int totalScore = ScoreManager.getCategoryTotal(arrayLists);
        final int outOf = ScoreManager.getCategoryOutOf(arrayLists);
        holder.txtViewTopicScore.setText(totalScore + " out of " + outOf);
        rootView.setBackgroundColor(ta.getColor(position % (ta.length()), 0));

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animFadeout;
                animFadeout = AnimationUtils.loadAnimation(mContext,
                        R.anim.abc_fade_out);
                animFadeout.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        Intent iSubtopics=new Intent(mContext, SubTopicResultsActivity.class);
                        iSubtopics.putExtra("btnColor", ta.getColor(position % (ta.length()), 0));
                        iSubtopics.putExtra("topicName", topic.getTopicName());
                        iSubtopics.putExtra("totalScore", totalScore);
                        iSubtopics.putExtra("count", subTopicCount);
                        iSubtopics.putExtra("outof", outOf);
                        iSubtopics.putExtra("RESULTS", arrayLists);
                        iSubtopics.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.getApplicationContext().startActivity(iSubtopics);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                rootView.startAnimation(animFadeout);
            }
        });

        return rootView;
    }

    class ViewHolder
    {
        private TextView txtViewTopicName;
        private TextView txtViewSubtopicCount;
        private TextView txtViewTopicScore;
        private ProgressBar progressBarScore;
    }
}
