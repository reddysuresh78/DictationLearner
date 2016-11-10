package com.ri.dictationlearner.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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

public class TestResultsSummaryActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_results_container);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_dictation_list) {
            Intent intent = new Intent(TestResultsSummaryActivity.this, DictationListActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_tests_list) {
            Intent intent = new Intent(TestResultsSummaryActivity.this, TestResultsSummaryActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
