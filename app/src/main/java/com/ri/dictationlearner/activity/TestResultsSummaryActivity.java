package com.ri.dictationlearner.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ri.dictationlearner.R;
import com.ri.dictationlearner.activity.db.DatabaseHelper;
import com.ri.dictationlearner.adapters.TestResultsSummaryAdapter;
import com.ri.dictationlearner.domain.Dictation;
import com.ri.dictationlearner.domain.TestResultSummaryItem;
import com.ri.dictationlearner.utils.DatabaseUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class  TestResultsSummaryActivity extends AppCompatActivity    {

    private static final String LOG_TAG = "TestResultsSummary";

    private ProgressDialog mProgressDialog;

    private DatabaseHelper mDBHelper;

    private ListView mListView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_results);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDBHelper = new DatabaseHelper(this);

        // Construct the data source
        ArrayList<TestResultSummaryItem> summaryItems = new ArrayList<>();

        // Attach the adapter to a ListView

        TestResultsSummaryAdapter adapter = new TestResultsSummaryAdapter(this, summaryItems );

        mListView = (ListView) findViewById(R.id.lv_test_dictation_summary);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                TestResultSummaryItem testResultSummaryItem = (TestResultSummaryItem) mListView.getItemAtPosition(position);
                String title = testResultSummaryItem.getName();
                Intent intent = new Intent(TestResultsSummaryActivity.this, TestResultsActivity.class);
                intent.putExtra(EXTRA_MESSAGE, title);
                intent.putExtra("TEST", testResultSummaryItem);
                startActivity(intent);
            }
        });

    }

    public void onResume(){
        super.onResume();

        Log.d(LOG_TAG, "OnResume is called");

        GetTestSummaryAsyncTask dbTask = new GetTestSummaryAsyncTask(this);
        dbTask.execute();

    }

    private ArrayList<TestResultSummaryItem> populateSummaryItems() {
        ArrayList<TestResultSummaryItem> summaryItems = new ArrayList<>();
        Cursor cursor = mDBHelper.getDictationList();

        Map<Integer, Dictation> dictations = new HashMap <Integer, Dictation>();
        if(cursor != null && cursor.getCount() > 0 ) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Dictation dictation = DatabaseUtils.getDictation(cursor);
                dictations.put(dictation.getId(), dictation);
                cursor.moveToNext();
            }
        }
        cursor.close();
        //_id, dict_id, total_count, correct_count
        cursor = mDBHelper.getAllTestsList();
        if(cursor != null && cursor.getCount() > 0 ) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                TestResultSummaryItem summaryItem = DatabaseUtils.getTestSummary(cursor);
                Dictation dictation = dictations.get(summaryItem.getDictationId());
                if(dictation != null) {
                    summaryItem.setImage(dictation.getImage());
                    summaryItem.setName(dictation.getName());
                }
                cursor.moveToNext();
                summaryItems.add(summaryItem);
            }
        }
        cursor.close();
        return summaryItems;
    }
  
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    public class GetTestSummaryAsyncTask extends AsyncTask<String, Void, ArrayList<TestResultSummaryItem>> {
        private Context mContext;

        public GetTestSummaryAsyncTask(Context context ) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(TestResultsSummaryActivity.this,ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
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
        protected ArrayList<TestResultSummaryItem> doInBackground(String... strings) {
            return populateSummaryItems();
        }

        @Override
        protected void onPostExecute(ArrayList<TestResultSummaryItem> summaryItems) {
            // Attach the adapter to a ListView
            TestResultsSummaryAdapter adapter = new TestResultsSummaryAdapter(mContext, summaryItems );
            mListView.setAdapter(adapter);
            mProgressDialog.dismiss();
         }

    }

}
