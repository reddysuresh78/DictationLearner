package com.ri.dictationlearner.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ri.dictationlearner.R;
import com.ri.dictationlearner.activity.db.DatabaseHelper;
import com.ri.dictationlearner.adapters.DictationWordListAdapter;
import com.ri.dictationlearner.domain.Dictation;
import com.ri.dictationlearner.domain.GlobalState;
import com.ri.dictationlearner.domain.Word;
import com.ri.dictationlearner.utils.DatabaseUtils;
import com.ri.dictationlearner.utils.ImageUtils;

import java.util.Locale;

public class WordsListActivity extends AppCompatActivity {

    private static final String LOG_TAG = "WordsListActivity";

    private TextToSpeech textToSpeech;

    private Dictation dictation;

    private DatabaseHelper dbHelper;

    private Cursor cursor = null;

    private DictationWordListAdapter cursorAdapter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_words_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String dictationName = "";
        if (savedInstanceState == null) {
            Log.d(LOG_TAG , "savedInstanceState  " + savedInstanceState);

            Bundle extras = getIntent().getExtras();
            if(extras != null) {
                dictation  = (Dictation) extras.get("DICTATION");
                dictationName = dictation.getName();
            }

        }else{

            Log.d(LOG_TAG , "savedInstanceState  " + savedInstanceState);

            dictationName = savedInstanceState.getString("title");
            dictation  = (Dictation) savedInstanceState.get("DICTATION");

            Log.d(LOG_TAG , "Dictataion name " + dictationName);
        }

        Log.d(LOG_TAG, "Received dictation "  + dictation);

        setTitle("Dictation: " + dictationName);

        dbHelper = new DatabaseHelper(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add_dictation_word);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(LOG_TAG,"Trying to create new word");

                Intent intent = new Intent(WordsListActivity.this, AddWordActivity.class);
                Word word = new Word().setDictationId(dictation.getId()).setSerialNo(1); //We need to update this serialno correctly
                intent.putExtra("WORD", word);

                Log.d(LOG_TAG,"Trying to create new word " + word);

                startActivity(intent);

            }
        });
        fab.setVisibility(GlobalState.isParentMode() ? View.VISIBLE: View.GONE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cursor = dbHelper.getWordList(dictation.getId());

        cursorAdapter = new DictationWordListAdapter(this, cursor, 0  );
        cursorAdapter.setReadOnlyMode(!GlobalState.isParentMode());


// Attach the adapter to a ListView
        final ListView listView = (ListView) findViewById(R.id.lv_dictationwords);
        listView.setAdapter(cursorAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                cursor.moveToPosition(position);
                Word word = DatabaseUtils.getWord(cursor);

                Intent intent = new Intent(WordsListActivity.this, WordDetailActivity.class);
                intent.putExtra("CURRENT_WORD_INDEX", position);
                intent.putExtra("WORD", word);
                intent.putExtra("NAME", dictation.getName());
                intent.putExtra("CURRENT_MODE", "SHOW");
                startActivity(intent);
            }
        });
        initializeSpeech();
    }

    public void deleteWordOnClickHandler(View v) {
        showDialog(v);
    }

    public void showDialog(final View v) {
        Word word = (Word) v.getTag();

        new AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("Do you really want to delete word " + word.getWord() + "?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        deleteWord(v);
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void deleteWord(View v){
        Word word = (Word) v.getTag();

        Log.d(LOG_TAG,"Trying to delete " + word.getWord());

        dbHelper.deleteWord( dictation.getId(),  word.getWordId());

        cursor.close();

        cursor = dbHelper.getWordList(dictation.getId());

        cursorAdapter.changeCursor(cursor);

        Toast.makeText(this,"Word Deleted",Toast.LENGTH_LONG ).show();

    }


    public void playSoundOnClickHandler(View v) {
        Word word = (Word)v.getTag();

        Log.d(LOG_TAG,"Trying to speak " + word.getWord());

        if(textToSpeech.isSpeaking()) {
            textToSpeech.stop();
        }
        String toSpeak = word.getWord();
        textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
    }

    public void editWordOnClickHandler(View v) {
        Word word = (Word) v.getTag();

        Log.d(LOG_TAG,"Trying to edit " + word.getWord());

        Intent intent = new Intent(WordsListActivity.this, AddWordActivity.class);

        intent.putExtra("WORD", word);

        startActivity(intent);

    }

    public void onResume(){
        super.onResume();

        Log.d(LOG_TAG, "OnResume is called");

        if(cursor!=null){
            cursor.close();
        }

        cursor = dbHelper.getWordList(dictation.getId());

        cursorAdapter.changeCursor(cursor);

        initializeSpeech();
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

    public void showImageClickHandler(View v) {
        Word word = (Word) v.getTag();

        Log.d(LOG_TAG,"ShowImage called "  + v.getId()  + "/" + word);
        Dialog builder = new Dialog(this);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //nothing;
            }
        });

        Log.d(LOG_TAG,"Fetching image for "  + word.getDictationId()  + "/" + word.getWordId());
        Cursor cursor = dbHelper.getWordImage(word.getDictationId(), word.getWordId());

        boolean imagePresent = false;

        if (null != cursor && cursor.getCount() > 0) {

            cursor.moveToFirst();

            byte[] image = cursor.getBlob(0);

            if(image!=null && image.length >0) {

                ImageView imageView = new ImageView(this);
                imageView.setImageBitmap(ImageUtils.getImage(image));

                imagePresent = true;
                builder.addContentView(imageView, new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                builder.show();
            }
        }

        if(!imagePresent){
            Toast.makeText(this,"No picture present for this word", Toast.LENGTH_SHORT).show();
        }

    }

    private void initializeSpeech() {

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.UK);
                    textToSpeech.setSpeechRate(0.5f);
                    textToSpeech.setPitch(0.6f);

                    AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    int amCurrentVolume = am.getStreamVolume(am.STREAM_MUSIC);
                    am.setStreamVolume(am.STREAM_MUSIC, 10, 0);

                }
            }
        });
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if(textToSpeech !=null){
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        Log.d(LOG_TAG,"State has been saved for " + dictation);
        savedInstanceState.putString("title", this.getTitle().toString());
        savedInstanceState.putParcelable("DICTATION", dictation);
        super.onSaveInstanceState(savedInstanceState);

        Log.d(LOG_TAG,"State has been saved");

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String title = savedInstanceState.getString("title");
        setTitle(title);
    }

}
