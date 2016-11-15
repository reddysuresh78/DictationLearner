package com.ri.dictationlearner.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ri.dictationlearner.R;
import com.ri.dictationlearner.activity.db.DatabaseHelper;
import com.ri.dictationlearner.adapters.TestResultsAdapter;
import com.ri.dictationlearner.domain.TestHistoryItem;
import com.ri.dictationlearner.domain.TestResultSummaryItem;
import com.ri.dictationlearner.utils.DatabaseUtils;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class TestResultsActivity extends AppCompatActivity {

    private static final String LOG_TAG = "TestResultsActivity";

    private ProgressDialog mProgressDialog;

    private TestResultSummaryItem mSummaryItem;

    private DatabaseHelper mDBHelper;

    private String mDictationName = "";

    @InjectView(R.id.lv_dictation_test_history)
    ListView mListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_details);

        ButterKnife.inject(this);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                mDictationName = extras.getString(EXTRA_MESSAGE);
                mSummaryItem = (TestResultSummaryItem) extras.get("TEST");
            }

        } else {
            mDictationName = savedInstanceState.getString("title");
            mSummaryItem = (TestResultSummaryItem) savedInstanceState.get("TEST");
            Log.d(LOG_TAG, "Dictation name " + mDictationName);

        }

        setTitle(getString(R.string.header_test_details) + mDictationName);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDBHelper = new DatabaseHelper(this);

        // Construct the data source
        final ArrayList<TestHistoryItem> historyItems = new ArrayList<>();

// Create the adapter to convert the array to views
        TestResultsAdapter adapter = new TestResultsAdapter(this, historyItems);

// Attach the adapter to a ListView
//        mListView = (ListView) findViewById(R.id.lv_dictation_test_history);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                TestHistoryItem testHistoryItem= (TestHistoryItem) mListView.getItemAtPosition(position);
                Intent intent = new Intent(TestResultsActivity.this, TestWordDetailsActivity.class);
                intent.putExtra("DICTATION_NAME", mDictationName);
                intent.putExtra("TEST", testHistoryItem);
                startActivity(intent);
            }
        });
    }
    private ArrayList<TestHistoryItem> populateHistoryItems() {
        ArrayList<TestHistoryItem> testItems = new ArrayList<>();
        Cursor cursor = mDBHelper.getTestsForDictation(mSummaryItem.getDictationId());
        //_id, dict_id, total_count, attempt_count, correct_count, wrong_count
        if(cursor != null && cursor.getCount() > 0 ) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                testItems.add(DatabaseUtils.getTestDetails(cursor));
                cursor.moveToNext();
            }
        }
        cursor.close();

        return testItems;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("title", this.getTitle().toString());
        savedInstanceState.putParcelable( "TEST", mSummaryItem);

        super.onSaveInstanceState(savedInstanceState);

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String title = savedInstanceState.getString("title");
        mSummaryItem = savedInstanceState.getParcelable("TEST");
        setTitle(title);
    }

    public void onResume(){
        super.onResume();

        Log.d(LOG_TAG, "OnResume is called");

        GetTestResultsAsyncTask dbTask = new GetTestResultsAsyncTask(this);
        dbTask.execute();

    }

    public class GetTestResultsAsyncTask extends AsyncTask<String, Void, ArrayList<TestHistoryItem>> {
        private Context mContext;

        public GetTestResultsAsyncTask(Context context ) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(TestResultsActivity.this,ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
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
        protected ArrayList<TestHistoryItem> doInBackground(String... strings) {
            return populateHistoryItems();
        }

        @Override
        protected void onPostExecute(ArrayList<TestHistoryItem> items) {
            // Attach the adapter to a ListView
            TestResultsAdapter adapter = new TestResultsAdapter(mContext, items);
            mListView.setAdapter(adapter);
            mProgressDialog.dismiss();
        }

    }
}
