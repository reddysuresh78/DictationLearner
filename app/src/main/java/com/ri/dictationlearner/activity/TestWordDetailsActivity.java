package com.ri.dictationlearner.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.ri.dictationlearner.R;
import com.ri.dictationlearner.activity.db.DatabaseHelper;
import com.ri.dictationlearner.adapters.TestResultsWordsAdapter;
import com.ri.dictationlearner.domain.TestHistoryItem;
import com.ri.dictationlearner.domain.TestHistoryWordDetails;
import com.ri.dictationlearner.utils.DatabaseUtils;

import java.util.ArrayList;

public class TestWordDetailsActivity extends AppCompatActivity {

    private static final String LOG_TAG = "TestWordDetailsActivity";

    private DatabaseHelper mDBHelper;

    private TestHistoryItem mHistoryItem;

    private ProgressDialog mProgressDialog;

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_word_details);

        String dictationName = "";
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                dictationName = extras.getString("DICTATION_NAME");
                mHistoryItem = (TestHistoryItem) extras.get("TEST");
            }
        } else {
            dictationName = savedInstanceState.getString("DICTATION_NAME");
            mHistoryItem = (TestHistoryItem) savedInstanceState.get("TEST");
        }

        setTitle(dictationName + " on " + mHistoryItem.getTestDate());

        mDBHelper = new DatabaseHelper(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ArrayList<TestHistoryWordDetails> historyItems = new ArrayList<>();


// Create the adapter to convert the array to views
        TestResultsWordsAdapter adapter = new TestResultsWordsAdapter(this, historyItems);

// Attach the adapter to a ListView
        mListView = (ListView) findViewById(R.id.lv_test_word_details);
        mListView.setAdapter(adapter);

    }
    private ArrayList<TestHistoryWordDetails> populateHistoryWordItems() {
        ArrayList<TestHistoryWordDetails> testItems = new ArrayList<>();
        testItems.add(new TestHistoryWordDetails( ).setActualWord("Actual").setEnteredWord("Entered").setCorrectIndicator(true));
        Cursor cursor = mDBHelper.getTestHistoryWordDetails(mHistoryItem.getTestId());
        //_id, dict_id, total_count, attempt_count, correct_count, wrong_count
        if(cursor != null && cursor.getCount() > 0 ) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                testItems.add(DatabaseUtils.getTestHistoryWordDetails(cursor));
                cursor.moveToNext();
            }
        }
        cursor.close();

        return testItems;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("title", this.getTitle().toString());
        savedInstanceState.putParcelable( "TEST", mHistoryItem);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String title = savedInstanceState.getString("title");
        mHistoryItem = savedInstanceState.getParcelable("TEST");
        setTitle(title);
    }

    public void onResume(){
        super.onResume();

        Log.d(LOG_TAG, "OnResume is called");

        GetTestWordDetailsAsyncTask dbTask = new GetTestWordDetailsAsyncTask(this);
        dbTask.execute();

    }


    public class GetTestWordDetailsAsyncTask extends AsyncTask<String, Void, ArrayList<TestHistoryWordDetails>> {
        private Context mContext;

        public GetTestWordDetailsAsyncTask(Context context ) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(TestWordDetailsActivity.this,ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
            //android.R.style.Theme_DeviceDefault_Dialog_Alert);
            mProgressDialog.setTitle("Please wait");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setMessage("Retrieving...");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setInverseBackgroundForced(true);
            mProgressDialog.show();
        }

        @Override
        protected ArrayList<TestHistoryWordDetails> doInBackground(String... strings) {
            return populateHistoryWordItems();
        }

        @Override
        protected void onPostExecute(ArrayList<TestHistoryWordDetails> items) {
            // Attach the adapter to a ListView
            TestResultsWordsAdapter adapter = new TestResultsWordsAdapter(mContext, items);
            mListView.setAdapter(adapter);
            mProgressDialog.dismiss();
        }

    }

}
