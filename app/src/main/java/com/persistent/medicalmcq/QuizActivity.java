package com.persistent.medicalmcq;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.persistent.medicalmcq.data.impl.DatabaseHandler;
import com.persistent.medicalmcq.model.Answer;
import com.persistent.medicalmcq.model.Question;
import com.persistent.medicalmcq.util.ScoreManager;
import com.persistent.medicalmcq.util.UiUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class QuizActivity extends ActionBarActivity {

    LinearLayout selectorLayout,llQuestions;
    private Button prevButton;
    boolean isFirst = true;
    ArrayList<Question> questionList = new ArrayList<Question>();
    TextView tvQuestion, tvDescription;
    RadioButton rbOption1, rbOption2, rbOption3, rbOption4;
    Button btnNext, btnPrev, btnCheck,btnSubmit;
    int CurrentQuestionIndex = 0;
    ArrayList<Button> buttonList = null;
    HashMap<Question, ArrayList<Answer>> attemptedQuestions= new HashMap<Question,ArrayList<Answer>>();
    static boolean isClearCalled=false;
    RadioGroup rgOptions;



    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
//        final int subTopicID = getIntent().getIntExtra("subTopicID", -1);
        final int topicID = getIntent().getIntExtra("TopicID", -1);
        final String subTopicName = getIntent().getStringExtra("subTopicName");

        android.support.v7.app.ActionBar supportActionBar = getSupportActionBar();

        supportActionBar.setTitle(subTopicName);
        final int color = getIntent().getIntExtra("Color", 0);
        supportActionBar.setBackgroundDrawable(new ColorDrawable(color));

        questionList = DatabaseHandler.getInstance(getBaseContext()).getQuestionsByTopicID(topicID);
        setUpVariables();
        selectorLayout = (LinearLayout) findViewById(R.id.selectors);
        ContextThemeWrapper newContext = new ContextThemeWrapper(getBaseContext(), R.style.ScrollButton);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                UiUtils.getDIP(getBaseContext(), 60), UiUtils.getDIP(getBaseContext(), 40));
        params.setMargins(20, 0, 0, 0);

        buttonList = new ArrayList<Button>();

        for (int i = 0; i < questionList.size(); i++) {

            final Button scrollBarButton = new Button(newContext);
            scrollBarButton.setBackgroundResource(R.drawable.custom_btn_scroll);
            scrollBarButton.setText(String.valueOf(i + 1));
            scrollBarButton.setLayoutParams(params);
            scrollBarButton.setPadding(5,5,5,5);
            selectorLayout.addView(scrollBarButton);
            buttonList.add(scrollBarButton);
            final int finalI = i;
            scrollBarButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    prevButton.setBackgroundResource(R.drawable.custom_btn_answered);
                    prevButton = (Button) v;
                    ((Button) v).setBackgroundResource(R.drawable.custom_btn_selected);
                    CurrentQuestionIndex = finalI;
                    displayQuestion(finalI);
                }
            });
        }

        //Display first question on startup

        displayQuestion(0);
        buttonList.get(0).setBackgroundResource(R.drawable.custom_btn_selected);
        prevButton = buttonList.get(0);

//        btnCheck.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ArrayList<Answer> selectedAnswerList=new ArrayList<Answer>();
//                int index=getSelectedOptiosIndex();
//                if (index!=-1){
////                    tvDescription.setVisibility(View.VISIBLE);
////                    llQuestions.setBackgroundColor(Color.argb(100,0,0,0));
//                    UiUtils.disableEnableControls(false, llQuestions);
//                    questionList.get(CurrentQuestionIndex).setIsAnswered(true);
////                    buttonList.get(CurrentQuestionIndex).setBackgroundResource(R.drawable.custom_btn_answered);
//                    if(questionList.get(CurrentQuestionIndex).getAnswerList().get(getSelectedOptiosIndex()).isCorrect()){
//                        rgOptions.findViewById(rgOptions.getCheckedRadioButtonId()).setBackgroundColor(getResources().getColor(R.color.darkgreen));
//                    }else{
//                        rgOptions.findViewById(rgOptions.getCheckedRadioButtonId()).setBackgroundColor(getResources().getColor(R.color.red));
//                    }
//                }else {
//                    Toast.makeText(getApplicationContext(), "Please select an option before checking", Toast.LENGTH_LONG).show();
//                }
//            }
//        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CurrentQuestionIndex > 0) {
                    buttonList.get(CurrentQuestionIndex).setBackgroundResource(R.drawable.custom_btn_answered);
                    buttonList.get(CurrentQuestionIndex - 1).setBackgroundResource(R.drawable.custom_btn_selected);
                    prevButton = buttonList.get(CurrentQuestionIndex - 1);
                    CurrentQuestionIndex--;
                    displayQuestion(CurrentQuestionIndex);
                }

            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CurrentQuestionIndex < questionList.size() - 1) {
                    buttonList.get(CurrentQuestionIndex).setBackgroundResource(R.drawable.custom_btn_answered);
                    buttonList.get(CurrentQuestionIndex + 1).setBackgroundResource(R.drawable.custom_btn_selected);
                    prevButton = buttonList.get(CurrentQuestionIndex + 1);
                    CurrentQuestionIndex++;
                    displayQuestion(CurrentQuestionIndex);
                }
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScoreManager scoreManager = new ScoreManager(attemptedQuestions, questionList);
                int marksObtained = scoreManager.calculateScore();
                int outOfScore = scoreManager.getOutOfScore();

//                Toast.makeText(getApplicationContext(), "Your Score is " + marksObtained +
//                        " Out of " + outOfScore, Toast.LENGTH_LONG).show();

                DatabaseHandler instance = DatabaseHandler.getInstance(getBaseContext());

                instance.saveResult(topicID, marksObtained, outOfScore);
                instance.saveResultDetails(attemptedQuestions,questionList);
                Intent iResults = new Intent(QuizActivity.this, ResultActivity.class);
                iResults.putExtra("SCORE", marksObtained);
                iResults.putExtra("OUTOF", outOfScore);
                iResults.putExtra("subTopicID", topicID);
                iResults.putExtra("subTopicName", subTopicName);
                iResults.putExtra("Color", color);
                startActivity(iResults);
                finish();
            }
        });


    }

    private int getSelectedOptiosIndex() {
        if(rgOptions.getCheckedRadioButtonId()!=-1){
            int id= rgOptions.getCheckedRadioButtonId();
            View radioButton = rgOptions.findViewById(id);
            int radioId = rgOptions.indexOfChild(radioButton);
            return radioId;
        }
        return -1;
    }

    private void displayQuestion(int finalI) {
        ArrayList<Answer> markedAnswerArrayList = attemptedQuestions.get(questionList.get(finalI));
        Question currentQuestion = questionList.get(finalI);

        if (finalI==questionList.size()-1)
        {
            btnSubmit.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.GONE);
        }else {
            btnNext.setVisibility(View.VISIBLE);
            btnSubmit.setVisibility(View.GONE);
        }




        // Check whether Multiple Correct answers or not
        if(currentQuestion.getCorrectAnswers().size()>1)
        {
            displayCheckBoxOptions(currentQuestion.getAnswerList());
        }else {
            if (markedAnswerArrayList!=null){
                int markedAnswerIndex = currentQuestion.getAnswerList().indexOf(markedAnswerArrayList.get(0));
                displayRadioButtonOptions(currentQuestion.getAnswerList(),markedAnswerIndex);
            }else{
                displayRadioButtonOptions(currentQuestion.getAnswerList(),-1);
            }

        }

        if (currentQuestion.isAnswered()){
//                llQuestions.setBackgroundColor(Color.argb(100,0,0,0));
            UiUtils.disableEnableControls(false, llQuestions);
//                tvDescription.setVisibility(View.VISIBLE);
            if(questionList.get(CurrentQuestionIndex).getAnswerList().get(getSelectedOptiosIndex()).isCorrect()){
                rgOptions.findViewById(rgOptions.getCheckedRadioButtonId()).setBackgroundColor(getResources().getColor(R.color.lightgreen));
            }else{
                rgOptions.findViewById(rgOptions.getCheckedRadioButtonId()).setBackgroundColor(getResources().getColor(R.color.pink));
            }
        }else {
//            llQuestions.setBackgroundColor(Color.argb(0,255,255,255));
            UiUtils.disableEnableControls(true, llQuestions);
//            tvDescription.setVisibility(View.GONE);
        }

        tvQuestion.setText(questionList.get(finalI).getQuestionText());
        tvDescription.setText(questionList.get(finalI).getAdditionalInfo());
    }

    private void displayCheckBoxOptions(List<Answer> answerList) {
       // TODO: Display Checkboxes for multiple contact

    }

    private void displayRadioButtonOptions(List<Answer> answerList, int markedIndex) {
        rgOptions = new RadioGroup(this);
        RadioButton radiobutton;
        LinearLayout optionsLayout = (LinearLayout) findViewById(R.id.llOptions);
        optionsLayout.removeAllViewsInLayout();

        RadioGroup.LayoutParams radioGroupParams = new RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.MATCH_PARENT,
                RadioGroup.LayoutParams.WRAP_CONTENT);

        RadioGroup.LayoutParams radioButtonParams = new RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.MATCH_PARENT,
                RadioGroup.LayoutParams.WRAP_CONTENT);

        radioButtonParams.setMargins(UiUtils.getDIP(this,20),0,0,0);

        rgOptions.setLayoutParams(radioGroupParams);
        rgOptions.setOrientation(RadioGroup.VERTICAL);

        int i=0;
        for (Answer answer:answerList){
            radiobutton = new RadioButton(this);
            radiobutton.setLayoutParams(radioButtonParams);
            radiobutton.setTextColor(Color.BLACK);
            radiobutton.setText(" " + answer.getAnswerText());
            radiobutton.setId(i++ + 500);
            radiobutton.setButtonDrawable(R.color.full_transparent);
//            radiobutton.setBackground(null);
            radiobutton.setCompoundDrawablesWithIntrinsicBounds(0,0,android.R.drawable.btn_radio,0);
            rgOptions.addView(radiobutton);
        }

        if(markedIndex!=-1){
            int id= ((RadioButton)rgOptions.getChildAt(markedIndex)).getId();
            rgOptions.check(id);
        }

        optionsLayout.addView(rgOptions);

        rgOptions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int index = getSelectedOptiosIndex();
                if (!isClearCalled && index != -1) {
                    ArrayList<Answer> selectedAnswerList = new ArrayList<Answer>();
                    selectedAnswerList.add(questionList.get(CurrentQuestionIndex).getAnswerList().get(index));
                    attemptedQuestions.put(questionList.get(CurrentQuestionIndex), selectedAnswerList);
                }
                isClearCalled = false;
            }
        });

    }

    private void setUpVariables() {
        llQuestions= (LinearLayout) findViewById(R.id.llQuestions);
        tvQuestion = (TextView) findViewById(R.id.tvQuestion);
        tvDescription = (TextView) findViewById(R.id.tvDescription);
        btnNext = (Button) findViewById(R.id.btnNext);
        btnPrev = (Button) findViewById(R.id.btnPrev);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
//        btnCheck = (Button) findViewById(R.id.btnCheck);
    }



//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_quiz, menu);
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
}
