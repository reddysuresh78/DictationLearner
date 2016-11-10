package com.ri.dictationlearner.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

    private DatabaseHelper dbHelper;

    private static final String LOG_TAG = "TestResultsSummary";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_results);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        dbHelper = new DatabaseHelper(this);

        // Construct the data source
        ArrayList<TestResultSummaryItem> summaryItems = populateSummaryItems();


        // Attach the adapter to a ListView

        TestResultsSummaryAdapter adapter = new TestResultsSummaryAdapter(this, summaryItems );

        final ListView listView = (ListView) findViewById(R.id.lv_test_dictation_summary);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                TestResultSummaryItem testResultSummaryItem = (TestResultSummaryItem) listView.getItemAtPosition(position);
                String title = testResultSummaryItem.getName();
                Intent intent = new Intent(TestResultsSummaryActivity.this, TestResultsActivity.class);
                intent.putExtra(EXTRA_MESSAGE, title);
                intent.putExtra("TEST", testResultSummaryItem);
                startActivity(intent);
            }
        });

    }

    private ArrayList<TestResultSummaryItem> populateSummaryItems() {
        ArrayList<TestResultSummaryItem> summaryItems = new ArrayList<>();
        Cursor cursor = dbHelper.getDictationList();

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
        cursor = dbHelper.getAllTestsList();
        if(cursor != null && cursor.getCount() > 0 ) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                TestResultSummaryItem summaryItem = DatabaseUtils.getTestSummary(cursor);
                Dictation dictation = dictations.get(summaryItem.getDictationId());
                summaryItem.setImage(dictation.getImage());
                summaryItem.setName(dictation.getName());
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

}
