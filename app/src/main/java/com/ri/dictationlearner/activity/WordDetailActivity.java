package com.ri.dictationlearner.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ri.dictationlearner.R;
import com.ri.dictationlearner.activity.db.DatabaseHelper;
import com.ri.dictationlearner.domain.GlobalState;
import com.ri.dictationlearner.domain.Word;
import com.ri.dictationlearner.utils.DatabaseUtils;
import com.ri.dictationlearner.utils.Utils;

import java.util.Locale;

//TODO: Home button from this activity makes the word list activity loose its title.
public class WordDetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = "WordDetailActivity";

    private TextToSpeech textToSpeech;

    private String mCurrentMode;

//    private List<Word> mAllWords = null;
    private int mCurrentWordIndex = -1;
    private EditText edWord;
    private TextView tvWordIndex;
    private ImageView ivWordImage;
    private ImageButton ibPrevButton;
    private ImageButton ibNextButton;
    private ImageButton ibStartButton;
    private ImageButton ibEndButton;
    private ImageButton ibPlaySoundButton;
    private ImageButton ibToggleWordButton;
    private ImageButton ibCheckSpellingButton;
    private DatabaseHelper dbHelper;

    private boolean isTestMode = false;

    private Cursor cursor;

    private Word word;

    private long testId;

    private String dictationName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras != null) {
                mCurrentWordIndex =  extras.getInt("CURRENT_WORD_INDEX");
//                mAllWords = (List<Word>) extras.get("ALL_WORDS");
                mCurrentMode = extras.getString("CURRENT_MODE");
                word =(Word) extras.get("WORD");
                dictationName =   extras.getString("NAME");
            }
        }else{

            Log.d(LOG_TAG , "savedInstanceState  " + savedInstanceState);

            mCurrentWordIndex =  savedInstanceState.getInt("CURRENT_WORD_INDEX");
            mCurrentMode = savedInstanceState.getString("CURRENT_MODE");
            word =(Word) savedInstanceState.get("WORD");
            dictationName =   savedInstanceState.getString("NAME");

            Log.d(LOG_TAG , "Dictataion name " + dictationName);
        }
        dbHelper = new DatabaseHelper(this);

        isTestMode = false;

        if(mCurrentMode.equalsIgnoreCase("TEST")) {
            isTestMode = true;
            testId = createNewTest();
        }

        cursor = dbHelper.getWordList(word.getDictationId());

        edWord = (EditText) findViewById(R.id.etWord);
//        edWord.setFocusable(false);

        tvWordIndex = (TextView) findViewById(R.id.tvWordIndex);

        ivWordImage = (ImageView) findViewById(R.id.ivWordImage);

        ibPrevButton = (ImageButton) findViewById(R.id.ibGoToPrevious);
        ibPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCurrentWordIndex >0) {
                    mCurrentWordIndex --;
                    showWord();
                }
                adjustControls();
            }
        });

        ibNextButton = (ImageButton) findViewById(R.id.ibGoToNext);
        ibNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isTestMode) {
                    saveWord();

                    String currentTag = (String) ibNextButton.getTag();

                    if("DONE".equalsIgnoreCase(currentTag)) {
                        Toast.makeText(WordDetailActivity.this, "You are done with the test", Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }


                }

                if(mCurrentWordIndex < cursor.getCount() -1) {
                    mCurrentWordIndex ++;
                    showWord();
                }
                adjustControls();
            }
        });

        ibStartButton = (ImageButton) findViewById(R.id.ibGoToStart);
        ibStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentWordIndex = 0;
                showWord();
                adjustControls();
            }
        });

        ibEndButton = (ImageButton) findViewById(R.id.ibGoToEnd);
        ibEndButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentWordIndex = cursor.getCount() -1;
                showWord();
                adjustControls();
            }
        });

        ibPlaySoundButton = (ImageButton) findViewById(R.id.ibPlaySoundDetail);
        ibPlaySoundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playSound();
            }
        });

        ibToggleWordButton = (ImageButton) findViewById(R.id.ibToggleWord);
        ibToggleWordButton.setTag("Showing");
        ibToggleWordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String curState  = (String) ibToggleWordButton.getTag();
                if(curState != null && curState.equalsIgnoreCase("Showing")) {
                    edWord.setText("");
                    edWord.setFocusable(true);
                    edWord.setFocusableInTouchMode(true);
                    edWord.requestFocus();
                    ibToggleWordButton.setTag("NotShowing");
                    ibToggleWordButton.setImageResource(R.drawable.ic_visibility_black_24dp);
                }else{
                    ibToggleWordButton.setImageResource(R.drawable.ic_visibility_off_black_24dp);
                    Word word = DatabaseUtils.getWord(cursor);
                    edWord.setText(word.getWord());
                    edWord.setFocusable(false);
                    ibToggleWordButton.setTag("Showing");
//                    edWord.requestFocus();
                }
            }
        });

        ibCheckSpellingButton= (ImageButton) findViewById(R.id.ibCheckSpelling);
        ibCheckSpellingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String curState  = (String) ibToggleWordButton.getTag();
                if(curState != null && curState.equalsIgnoreCase("Showing")) {
                    return;
                }
                String entered = edWord.getText().toString();
                if(entered == null || entered.isEmpty()) {
                    Toast.makeText(WordDetailActivity.this, "Please type the word", Toast.LENGTH_SHORT).show();
                    return;
                }
                Word word = DatabaseUtils.getWord(cursor);
                if(entered.equalsIgnoreCase(word.getWord())){
                    Toast.makeText(WordDetailActivity.this, "You are right", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(WordDetailActivity.this, "You are wrong. Try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if(mCurrentMode.equalsIgnoreCase("PRACTICE")){
            setTitle("Practice: " + dictationName);
        }else if(mCurrentMode.equalsIgnoreCase("TEST")){
            isTestMode = true;
            setTitle("Test: " + dictationName);
            ibCheckSpellingButton.setVisibility(View.INVISIBLE);
            ibToggleWordButton.setVisibility(View.INVISIBLE);
            ivWordImage.setVisibility( !GlobalState.isShowImagesEnabled() ?View.GONE: View.VISIBLE);
            ibStartButton.setVisibility(View.GONE);
            ibEndButton.setVisibility(View.GONE);
            ibPrevButton.setVisibility(View.GONE);
         }else{
            setTitle("View: " + dictationName);
            ibCheckSpellingButton.setVisibility(View.INVISIBLE);
            ibToggleWordButton.setVisibility(View.INVISIBLE);
        }

        showWord();
        adjustControls();

    }

    private void saveWord(){
        Log.d(LOG_TAG,"Record test result for current word");
        String entered = edWord.getText().toString();
        Word word = DatabaseUtils.getWord(cursor);
        String actual = word.getWord();

        Integer attempted = new Integer(entered.isEmpty() ? 0: 1 );
        Integer correct = new Integer( actual.equalsIgnoreCase(entered) ? 1:0 );
        Integer wrong = new Integer( correct > 0? 0:1);

        dbHelper.addTestWord(testId, actual, entered, actual.equalsIgnoreCase(entered) ? 1:0 );
        dbHelper.updateTest(testId,attempted, correct, wrong);
    }

    private long createNewTest() {
        return dbHelper.addTest(word.getDictationId());
    }

    private void initializeSpeech() {
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.UK);
                    textToSpeech.setSpeechRate(0.5f);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
//                if (getParentActivityIntent() == null) {
//                    Log.i(TAG, "You have forgotten to specify the parentActivityName in the AndroidManifest!");
//                    onBackPressed();
//                } else {
                    NavUtils.navigateUpFromSameTask(this);
//                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void playSound() {

        if(textToSpeech.isSpeaking()) {
            textToSpeech.stop();
        }
        cursor.moveToPosition(mCurrentWordIndex);
        String toSpeak =  cursor.getString(2);

        textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
    }


    public void onPause(){
        if(textToSpeech !=null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        if(cursor!=null){
            cursor.close();
        }
        super.onPause();
    }

    public void onResume(){
        super.onResume();
        initializeSpeech();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        if(textToSpeech !=null){
            textToSpeech.stop();
            textToSpeech.shutdown();

        }
    }

    private void showWord() {

        cursor.moveToPosition(mCurrentWordIndex);

        Word word = DatabaseUtils.getWord(cursor);

        Log.d(LOG_TAG, "Current mode is " + mCurrentMode);

        if(mCurrentMode.equalsIgnoreCase("TEST")) {
            edWord.setText("");
//            edWord.setFocusable(true);
//            if(edWord.requestFocus()) {
//                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//            }
//            edWord.requestFocus();
        }else{
            edWord.setText(word.getWord());
            edWord.setFocusable(false);
        }

        tvWordIndex.setText((mCurrentWordIndex + 1) + "/" + cursor.getCount());
        ivWordImage.setImageResource(word.getImageResourceId());
        ivWordImage.setColorFilter(R.color.colorPrimary);


        if(word.getImage() != null) {
            ivWordImage.setImageBitmap(Utils.getImage(word.getImage()));
        }

//        setTitle(mAllWords.get(mCurrentWordIndex).getWord());
    }

    private void adjustControls(){

        ibNextButton.setEnabled(mCurrentWordIndex < cursor.getCount()  -1);

        if(isTestMode) {
            ibPrevButton.setEnabled(false);
            ibEndButton.setEnabled(false);
            ibStartButton.setEnabled(false);

            if(mCurrentWordIndex == cursor.getCount() - 1){
                ibNextButton.setImageResource(R.drawable.ic_done_all_black_24dp);
                ibNextButton.setTag("DONE");
            }

            ibNextButton.setEnabled(mCurrentWordIndex < cursor.getCount());

        }else{
            ibStartButton.setEnabled(mCurrentWordIndex > 0);
            ibPrevButton.setEnabled(mCurrentWordIndex > 0);
            ibEndButton.setEnabled(mCurrentWordIndex < cursor.getCount()  -1 );

        }
        if(ibToggleWordButton.getVisibility() == View.VISIBLE) {
            ibToggleWordButton.setImageResource(R.drawable.ic_visibility_off_black_24dp);
            edWord.setFocusable(false);
            ibToggleWordButton.setTag("Showing");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        Log.d(LOG_TAG,"State is being saved for " + dictationName);

        savedInstanceState.putString("CURRENT_MODE", mCurrentMode);
        savedInstanceState.putString("NAME", dictationName);
        savedInstanceState.putParcelable("WORD", word);
        savedInstanceState.putInt("CURRENT_WORD_INDEX", mCurrentWordIndex);
        super.onSaveInstanceState(savedInstanceState);

        Log.d(LOG_TAG,"State has been saved for " + dictationName);

    }


}
