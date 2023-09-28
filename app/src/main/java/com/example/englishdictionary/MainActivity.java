package com.example.englishdictionary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.englishdictionary.Dialogs.ConfimationDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements ConfimationDialog.ConfirmationDialogListener {

    private Button startStopBtn, acceptBtn;

    private int userId;
    private ImageButton logOutBtn, changeWordsPerTimeBtn, acceptChangeWordsCountBtn;
    private RelativeLayout mainLayout, changeWordCountLayout;
    private ListView listWithWords;
    private TextView howManyLearned, countWordsPerTime;
    private NumberPicker changeWordsCountPicker;
    // true - startStopButton was pressed (can see list with words), false - startStopButton have default state (don't see )
    private boolean isActive = false;
    // true - can't see translate, false - can see
    private boolean isWordsHide = false;
    private byte numberOfWordsPerLesson;
    Context thisContext;
    private String[][] wordsDictionary;
    WorkWithDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new WorkWithDB(this);
        userId = db.getOnlineUser();
        thisContext = this;

        howManyLearned = findViewById(R.id.how_many_learned);
        int lastLearned = db.getLastLearnedWordNumber(userId);
        String howManyLearnedText = lastLearned + "/" + "9662";
        howManyLearned.setText(howManyLearnedText);

        countWordsPerTime = findViewById(R.id.count_words);
        int WCount = db.getCountWordsPerTime(userId);
        countWordsPerTime.setText(String.valueOf(WCount));

        changeWordCountLayout = findViewById(R.id.change_word_count_layout);
        changeWordsPerTimeBtn = findViewById(R.id.change_count);
        changeWordsPerTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isActive) {
                    Toast.makeText(thisContext, "Вы не можете сделать это во время тренеровки", Toast.LENGTH_LONG).show();
                } else {
                    changeWordCountLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        changeWordsCountPicker = findViewById(R.id.change_word_count_picker);
        changeWordsCountPicker.setMinValue(5);
        changeWordsCountPicker.setMaxValue(25);
        acceptChangeWordsCountBtn = findViewById(R.id.accept_change_count);
        acceptChangeWordsCountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.updateCountWordsPerTime(userId, changeWordsCountPicker.getValue());
                countWordsPerTime.setText(String.valueOf(changeWordsCountPicker.getValue()));
                changeWordCountLayout.setVisibility(View.INVISIBLE);
            }
        });
        acceptBtn = findViewById(R.id.accept_button);
        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainLayout.setVisibility(View.INVISIBLE);
                db.updateLastLearnedWord(userId);

                int lastLearned = db.getLastLearnedWordNumber(userId);
                String howManyLearnedText = lastLearned + "/" + "9662";
                howManyLearned.setText(howManyLearnedText);

                startStopBtn.setText(R.string.start_training);
                isActive = !isActive;
            }
        });

        startStopBtn = findViewById(R.id.start_stop_button);
        logOutBtn = findViewById(R.id.log_out);
        mainLayout = findViewById(R.id.main_layout);
        listWithWords = findViewById(R.id.list_with_words);
        startStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isActive) {
                    mainLayout.setVisibility(View.INVISIBLE);
                    startStopBtn.setText(R.string.start_training);
                } else {
                    // work with db part
                    String[][] words = db.getWords(db.getLastLearnedWordNumber(userId), db.getCountWordsPerTime(userId));

                    List<Map<String, String>> listArray = new ArrayList<>();

                    for(int i=0; i< words.length; i++)
                    {
                        Map<String, String> listItem = new HashMap<>();
                        listItem.put("eng_word", words[i][0]);
                        listItem.put("rus_word", words[i][1]);
                        listArray.add(listItem);
                    }
                    // TODO get words from db
                    SimpleAdapter simpleAdapter = new SimpleAdapter(thisContext, listArray,
                            R.layout.word_list_item,
                            new String[] {"eng_word", "rus_word" },
                            new int[] {R.id.word_on_english, R.id.word_on_russian }
                    );
                    simpleAdapter.notifyDataSetChanged();

                    listWithWords.setAdapter(simpleAdapter);

                    // show layout part
                    mainLayout.setVisibility(View.VISIBLE);
                    startStopBtn.setText(R.string.stop_with_lose_progress);
                }
                isActive = !isActive;
            }
        });

        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO create modal window with confirmation log_out
                openConfirmationDialog();
            }
        });
    }

    public void openConfirmationDialog() {
        ConfimationDialog confimationDialog = new ConfimationDialog();
        confimationDialog.show(getSupportFragmentManager(), "confirmation dialog");
    }

    public void goToAppLoadingPage() {
        Intent intent = new Intent(this, AppLoadingActivity.class);
        startActivity(intent);
    }

    @Override
    public void applyAnswer(boolean answer) {
        if (answer) {
            db.putOnlineUserToOffline();
            goToAppLoadingPage();
        }
    }
}