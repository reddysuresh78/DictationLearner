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

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.ri.dictationlearner.R;
import com.ri.dictationlearner.activity.db.DatabaseHelper;
import com.ri.dictationlearner.domain.GlobalState;
import com.ri.dictationlearner.domain.Word;
import com.ri.dictationlearner.utils.DatabaseUtils;
import com.ri.dictationlearner.utils.ImageUtils;

import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class WordDetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = "WordDetailActivity";

    private TextToSpeech textToSpeech;

    private String mCurrentMode;

    private int mCurrentWordIndex = -1;

    @InjectView(R.id.etWord)
    EditText edWord;
    @InjectView(R.id.tvWordIndex)
    TextView tvWordIndex;
    @InjectView(R.id.ivWordImage)
    ImageView ivWordImage;
    @InjectView(R.id.ibGoToPrevious)
    ImageButton ibPrevButton;
    @InjectView(R.id.ibGoToNext)
    ImageButton ibNextButton;
    @InjectView(R.id.ibGoToStart)
    ImageButton ibStartButton;
    @InjectView(R.id.ibGoToEnd)
    ImageButton ibEndButton;
    @InjectView(R.id.ibPlaySoundDetail)
    ImageButton ibPlaySoundButton;
    @InjectView(R.id.ibToggleWord)
    ImageButton ibToggleWordButton;
    @InjectView(R.id.ibCheckSpelling)
    ImageButton ibCheckSpellingButton;

    private DatabaseHelper dbHelper;

    private boolean isTestMode = false;

    private Cursor cursor;

    private Word word;

    private long testId;

    private String dictationName;

    @InjectView(R.id.adView)
    AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_detail);

        ButterKnife.inject(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras != null) {
                mCurrentWordIndex =  extras.getInt("CURRENT_WORD_INDEX");
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

//        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("9FE9315EC5961D7BCA2C8E3569A4C3F8")
                .build();
        mAdView.loadAd(adRequest);

        isTestMode = false;

        if(mCurrentMode.equalsIgnoreCase("TEST")) {
            isTestMode = true;
            testId = createNewTest();
        }

        cursor = dbHelper.getWordList(word.getDictationId());
//
//        edWord = (EditText) findViewById(R.id.etWord);
//
//        tvWordIndex = (TextView) findViewById(R.id.tvWordIndex);
//
//        ivWordImage = (ImageView) findViewById(R.id.ivWordImage);
//
//        ibPrevButton = (ImageButton) findViewById(R.id.ibGoToPrevious);
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

//        ibNextButton = (ImageButton) findViewById(R.id.ibGoToNext);
        ibNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isTestMode) {
                    saveWord();

                    String currentTag = (String) ibNextButton.getTag();

                    if("DONE".equalsIgnoreCase(currentTag)) {
                        Toast.makeText(WordDetailActivity.this, R.string.test_done_confirm, Toast.LENGTH_LONG).show();
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

//        ibStartButton = (ImageButton) findViewById(R.id.ibGoToStart);
        ibStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentWordIndex = 0;
                showWord();
                adjustControls();
            }
        });

//        ibEndButton = (ImageButton) findViewById(R.id.ibGoToEnd);
        ibEndButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentWordIndex = cursor.getCount() -1;
                showWord();
                adjustControls();
            }
        });

//        ibPlaySoundButton = (ImageButton) findViewById(R.id.ibPlaySoundDetail);
        ibPlaySoundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playSound();
            }
        });

//        ibToggleWordButton = (ImageButton) findViewById(R.id.ibToggleWord);
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
                }
            }
        });

//        ibCheckSpellingButton= (ImageButton) findViewById(R.id.ibCheckSpelling);
        ibCheckSpellingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String curState  = (String) ibToggleWordButton.getTag();
                if(curState != null && curState.equalsIgnoreCase("Showing")) {
                    return;
                }
                String entered = edWord.getText().toString();
                if(entered == null || entered.isEmpty()) {
                    Toast.makeText(WordDetailActivity.this, R.string.type_word, Toast.LENGTH_SHORT).show();
                    return;
                }
                Word word = DatabaseUtils.getWord(cursor);
                if(entered.equalsIgnoreCase(word.getWord())){
                    Toast.makeText(WordDetailActivity.this, R.string.correct_confirm, Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(WordDetailActivity.this, R.string.wrong_confirm, Toast.LENGTH_SHORT).show();
                }
            }
        });

        if(mCurrentMode.equalsIgnoreCase("PRACTICE")){
            setTitle(getString(R.string.header_practice) + dictationName);
        }else if(mCurrentMode.equalsIgnoreCase("TEST")){
            isTestMode = true;
            setTitle(getString(R.string.header_Test) + dictationName);
            ibCheckSpellingButton.setVisibility(View.INVISIBLE);
            ibToggleWordButton.setVisibility(View.INVISIBLE);
            ivWordImage.setVisibility( !GlobalState.isShowImagesEnabled() ?View.GONE: View.VISIBLE);
            ibStartButton.setVisibility(View.GONE);
            ibEndButton.setVisibility(View.GONE);
            ibPrevButton.setVisibility(View.GONE);
         }else{
            setTitle(getString(R.string.header_view) + dictationName);
            ibCheckSpellingButton.setVisibility(View.INVISIBLE);
            ibToggleWordButton.setVisibility(View.INVISIBLE);
        }

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
//                Toast.makeText(getApplicationContext(), "Ad is loaded!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClosed() {
//                Toast.makeText(getApplicationContext(), "Ad is closed!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
//                Toast.makeText(getApplicationContext(), "Ad failed to load! error code: " + errorCode, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLeftApplication() {
//                Toast.makeText(getApplicationContext(), "Ad left application!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
//                Toast.makeText(getApplicationContext(), "Ad is opened!", Toast.LENGTH_SHORT).show();
            }
        });

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
                NavUtils.navigateUpFromSameTask(this);
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

        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    public void onResume(){
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
        initializeSpeech();

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        if (mAdView != null) {
            mAdView.destroy();
        }

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
        }else{
            edWord.setText(word.getWord());
            edWord.setFocusable(false);
        }

        tvWordIndex.setText((mCurrentWordIndex + 1) + "/" + cursor.getCount());
        ivWordImage.setImageResource(word.getImageResourceId());
        ivWordImage.setColorFilter(R.color.colorPrimary);


        if(word.getImage() != null) {
            ivWordImage.setImageBitmap(ImageUtils.getImage(word.getImage()));
        }
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
