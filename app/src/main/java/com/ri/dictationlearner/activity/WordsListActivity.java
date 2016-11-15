package com.ri.dictationlearner.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.AsyncTask;
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

import butterknife.ButterKnife;
import butterknife.InjectView;

public class WordsListActivity extends AppCompatActivity {

    private static final String LOG_TAG = "WordsListActivity";

    private TextToSpeech mTextToSpeech;

    private Dictation mDictation;

    private DatabaseHelper mDBHelper;

    private Cursor mCursor = null;

    private DictationWordListAdapter mCursorAdapter = null;

    private ProgressDialog mProgressDialog;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.fab_add_dictation_word)
    FloatingActionButton fab;

    @InjectView(R.id.lv_dictationwords)
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_words_list);

        ButterKnife.inject(this);

        setSupportActionBar(toolbar);

        String dictationName = "";
        if (savedInstanceState == null) {
            Log.d(LOG_TAG , "savedInstanceState  " + savedInstanceState);

            Bundle extras = getIntent().getExtras();
            if(extras != null) {
                mDictation = (Dictation) extras.get("DICTATION");
                dictationName = mDictation.getName();
            }

        }else{

            Log.d(LOG_TAG , "savedInstanceState  " + savedInstanceState);

            dictationName = savedInstanceState.getString("title");
            mDictation = (Dictation) savedInstanceState.get("DICTATION");

            Log.d(LOG_TAG , "Dictation name " + dictationName);
        }

        Log.d(LOG_TAG, "Received Dictation "  + mDictation);

        setTitle(getString(R.string.header_dictation) + dictationName);

        mDBHelper = new DatabaseHelper(this);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(LOG_TAG,"Trying to create new word");

                Intent intent = new Intent(WordsListActivity.this, AddWordActivity.class);
                Word word = new Word().setDictationId(mDictation.getId()).setSerialNo(1); //We need to update this serialno correctly
                intent.putExtra("WORD", word);

                Log.d(LOG_TAG,"Trying to create new word " + word);

                startActivity(intent);

            }
        });
        fab.setVisibility(GlobalState.isParentMode() ? View.VISIBLE: View.GONE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCursorAdapter = new DictationWordListAdapter(this, mCursor, 0  );
        mCursorAdapter.setReadOnlyMode(!GlobalState.isParentMode());


// Attach the adapter to a ListView

        listView.setAdapter(mCursorAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                mCursor.moveToPosition(position);
                Word word = DatabaseUtils.getWord(mCursor);

                Intent intent = new Intent(WordsListActivity.this, WordDetailActivity.class);
                intent.putExtra("CURRENT_WORD_INDEX", position);
                intent.putExtra("WORD", word);
                intent.putExtra("NAME", mDictation.getName());
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
                .setTitle(getString(R.string.header_dialog_confirm))
                .setMessage(getString(R.string.message_delete_confirm) + word.getWord() + "?")
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

        mDBHelper.deleteWord( mDictation.getId(),  word.getWordId());

        GetWordsAsyncTask dbTask = new GetWordsAsyncTask(this);
        dbTask.execute(mDictation.getId() );

        Toast.makeText(this, R.string.delete_word_confirm,Toast.LENGTH_LONG ).show();

    }


    public void playSoundOnClickHandler(View v) {
        Word word = (Word)v.getTag();

        Log.d(LOG_TAG,"Trying to speak " + word.getWord());

        if(mTextToSpeech.isSpeaking()) {
            mTextToSpeech.stop();
        }
        String toSpeak = word.getWord();
        mTextToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
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

        GetWordsAsyncTask dbTask = new GetWordsAsyncTask(this);
        dbTask.execute(mDictation.getId());

        initializeSpeech();
    }

    public void onPause(){
        if(mTextToSpeech !=null){
            mTextToSpeech.stop();
            mTextToSpeech.shutdown();

        }
        Log.d(LOG_TAG, "OnPause is called");
        if(mCursor !=null){
            mCursor.close();
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
        Cursor cursor = mDBHelper.getWordImage(word.getDictationId(), word.getWordId());

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
            Toast.makeText(this, R.string.no_pic_warning, Toast.LENGTH_SHORT).show();
        }

    }

    private void initializeSpeech() {

        mTextToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    mTextToSpeech.setLanguage(Locale.UK);
                    mTextToSpeech.setSpeechRate(0.5f);
                    mTextToSpeech.setPitch(0.6f);

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

        if(mTextToSpeech !=null){
            mTextToSpeech.stop();
            mTextToSpeech.shutdown();
            mTextToSpeech = null;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        Log.d(LOG_TAG,"State has been saved for " + mDictation);
        savedInstanceState.putString("title", this.getTitle().toString());
        savedInstanceState.putParcelable("DICTATION", mDictation);
        super.onSaveInstanceState(savedInstanceState);

        Log.d(LOG_TAG,"State has been saved");

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String title = savedInstanceState.getString("title");
        setTitle(title);
    }

    public class GetWordsAsyncTask extends AsyncTask<Integer, Void, Cursor> {
        private Context mContext;

        public GetWordsAsyncTask(Context context ) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(WordsListActivity.this,ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
            //android.R.style.Theme_DeviceDefault_Dialog_Alert);
            mProgressDialog.setTitle(getString(R.string.header_please_wait));
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setMessage(getString(R.string.message_retrieving));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setInverseBackgroundForced(true);
            mProgressDialog.show();
        }

        @Override
        protected Cursor doInBackground(Integer... dictationId) {
            if(mCursor !=null){
                mCursor.close();
            }
            return mDBHelper.getWordList(dictationId[0]);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            mCursor = cursor;
            mCursorAdapter.changeCursor(mCursor);
            mProgressDialog.dismiss();

        }

    }

}
