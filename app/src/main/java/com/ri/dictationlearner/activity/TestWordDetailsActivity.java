package com.ri.dictationlearner.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.ri.dictationlearner.R;
import com.ri.dictationlearner.activity.db.DatabaseHelper;
import com.ri.dictationlearner.adapters.TestResultsWordsAdapter;
import com.ri.dictationlearner.domain.TestHistoryItem;
import com.ri.dictationlearner.domain.TestHistoryWordDetails;
import com.ri.dictationlearner.utils.DatabaseUtils;

import java.util.ArrayList;

public class TestWordDetailsActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    private TestHistoryItem mHistoryItem;


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

        dbHelper = new DatabaseHelper(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ArrayList<TestHistoryWordDetails> historyItems = populateHistoryWordItems();


// Create the adapter to convert the array to views
        TestResultsWordsAdapter adapter = new TestResultsWordsAdapter(this, historyItems);

// Attach the adapter to a ListView
        final ListView listView = (ListView) findViewById(R.id.lv_test_word_details);
        listView.setAdapter(adapter);

    }
    private ArrayList<TestHistoryWordDetails> populateHistoryWordItems() {
        ArrayList<TestHistoryWordDetails> testItems = new ArrayList<>();
        testItems.add(new TestHistoryWordDetails( ).setActualWord("Actual").setEnteredWord("Entered").setCorrectIndicator(true));
        Cursor cursor = dbHelper.getTestHistoryWordDetails(mHistoryItem.getTestId());
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

}
