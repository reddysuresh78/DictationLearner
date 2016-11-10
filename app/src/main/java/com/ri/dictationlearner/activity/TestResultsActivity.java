package com.ri.dictationlearner.activity;

import android.content.Intent;
import android.database.Cursor;
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

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class TestResultsActivity extends AppCompatActivity {

    private static final String LOG_TAG = "TestResultsActivity";

    private TestResultSummaryItem mSummaryItem;

    private DatabaseHelper dbHelper;

    private String mDictationName = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_details);


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

        setTitle("Test Details for " + mDictationName);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = new DatabaseHelper(this);

        // Construct the data source
        final ArrayList<TestHistoryItem> historyItems = populateHistoryItems();

//        historyItems.add(new TestHistoryItem().setDate("12-Sep-2016").setTotal(8).setAttempted(8).setCorrect(7));
//        historyItems.add(new TestHistoryItem().setDate("11-Sep-2016").setTotal(8).setAttempted(6).setCorrect(6));
//        historyItems.add(new TestHistoryItem().setDate("09-Sep-2016").setTotal(8).setAttempted(4).setCorrect(2));

// Create the adapter to convert the array to views
        TestResultsAdapter adapter = new TestResultsAdapter(this, historyItems);

// Attach the adapter to a ListView
        final ListView listView = (ListView) findViewById(R.id.lv_dictation_test_history);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                TestHistoryItem testHistoryItem= (TestHistoryItem) listView.getItemAtPosition(position);
                Intent intent = new Intent(TestResultsActivity.this, TestWordDetailsActivity.class);
                intent.putExtra("DICTATION_NAME", mDictationName);
                intent.putExtra("TEST", testHistoryItem);
                startActivity(intent);
            }
        });
    }
    private ArrayList<TestHistoryItem> populateHistoryItems() {
        ArrayList<TestHistoryItem> testItems = new ArrayList<>();
        Cursor cursor = dbHelper.getTestsForDictation(mSummaryItem.getDictationId());
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

}
