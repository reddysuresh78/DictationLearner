package com.ri.dictationlearner.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;

import com.ri.dictationlearner.R;
import com.ri.dictationlearner.activity.db.AndroidDatabaseManager;
import com.ri.dictationlearner.activity.db.DatabaseHelper;
import com.ri.dictationlearner.adapters.DictationsCursorAdapter;
import com.ri.dictationlearner.domain.Dictation;
import com.ri.dictationlearner.domain.GlobalState;
import com.ri.dictationlearner.domain.Word;
import com.ri.dictationlearner.utils.DatabaseUtils;

public class DictationListActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {

    private static final String LOG_TAG = "DictationListActivity";

    private DatabaseHelper dbHelper;

    DictationsCursorAdapter cursorAdapter = null;

    ListView listView = null;

    Cursor  cursor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictation_list_container);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Log.i(LOG_TAG,"OnCreate called");

        GlobalState.init(this);

        if(cursor != null){
            cursor.close();
        }

        dbHelper = new DatabaseHelper(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("MAIN","Trying to create new dictation");
                Intent intent = new Intent(DictationListActivity.this, AddDictationActivity.class);
                startActivity(intent);
            }
        });

        fab.setVisibility(GlobalState.isParentMode() ? View.VISIBLE: View.GONE );


        cursor = dbHelper.getDictationList();

        cursorAdapter = new DictationsCursorAdapter(this, cursor, 0  );
        cursorAdapter.setReadOnlyMode(!GlobalState.isParentMode());

// Attach the adapter to a ListView
        listView = (ListView) findViewById(R.id.lv_dictations);
        listView.setAdapter(cursorAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                cursor.moveToPosition(position);
                Dictation dictation = DatabaseUtils.getDictation(cursor);  //(Dictation) listView.getItemAtPosition(position);
                String title = dictation.getName();
                Intent intent = new Intent(DictationListActivity.this, WordsListActivity.class);
                intent.putExtra("DICTATION", dictation);
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

        setTitle("Dictation List");

        NavigationView nav = (NavigationView)findViewById(R.id.nav_view);
        MenuItem switchItem = nav.getMenu().findItem(R.id.nav_parent_mode);
        CompoundButton switchView = (CompoundButton) MenuItemCompat.getActionView(switchItem);
        switchView.setChecked(GlobalState.isParentMode());
        switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                Log.d(LOG_TAG, "Status changed now " + isChecked);
                GlobalState.savePreference(getString(R.string.pref_parent_mode), isChecked);
                Toast.makeText(DictationListActivity.this,"Please restart app to apply " + (isChecked ? "Parent Mode": "Child Mode") ,Toast.LENGTH_LONG ).show();
            }
        });

        MenuItem switchItemImages = nav.getMenu().findItem(R.id.nav_test_show_images);
        CompoundButton switchViewImages = (CompoundButton) MenuItemCompat.getActionView(switchItemImages);
        switchViewImages.setChecked(GlobalState.isShowImagesEnabled());
        switchViewImages.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                Log.d(LOG_TAG, "Status changed now " + isChecked);
                GlobalState.savePreference(getString(R.string.pref_test_show_images), isChecked);
                Toast.makeText(DictationListActivity.this,"Please restart app to apply the changes" ,Toast.LENGTH_LONG ).show();
            }
        });

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
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void editDictationOnClickHandler(View v) {
        Dictation dictation = (Dictation) v.getTag();

        Log.d("MAIN","Trying to edit " + dictation.getName());

        Intent intent = new Intent(DictationListActivity.this, AddDictationActivity.class);

        intent.putExtra("DICTATION", dictation);

        startActivity(intent);

    }

    public void deleteDictationOnClickHandler(View v) {
        showDialog(v);
    }

    private void deleteDictation(View v){
        Dictation dictation = (Dictation) v.getTag();

        Log.d("MAIN","Trying to delete " + dictation.getName());

        dbHelper.deleteDictation(dictation.getId());

        cursor.close();

        cursor = dbHelper.getDictationList();

        cursorAdapter.changeCursor(cursor);

        Toast.makeText(this,"Dictation Deleted " + dictation.getId(),Toast.LENGTH_LONG ).show();

    }

    public void takeTestOnClickHandler(View v) {
        Dictation dictation = (Dictation) v.getTag();

        if(dictation.getWordCount() == 0) {
            Toast.makeText(this,"There are no words in this dictation", Toast.LENGTH_LONG).show();
            return;
        }

        Log.d("MAIN","Trying to test " + dictation.getName());

        Word word = new Word().setDictationId(dictation.getId());

        Intent intent = new Intent(DictationListActivity.this, WordDetailActivity.class);
        intent.putExtra("CURRENT_WORD_INDEX", 0);

        intent.putExtra("WORD", word);

        intent.putExtra("NAME", dictation.getName());
        intent.putExtra("CURRENT_MODE", "TEST");

        startActivity(intent);
   }
    public void practiceDictationOnClickHandler(View v) {
        Dictation dictation = (Dictation) v.getTag();

        if(dictation.getWordCount() == 0) {
            Toast.makeText(this,"There are no words in this dictation", Toast.LENGTH_LONG).show();
            return;
        }

        Log.d("MAIN","Trying to practice " + dictation.getName());

        Word word = new Word().setDictationId(dictation.getId());

        Intent intent = new Intent(DictationListActivity.this, WordDetailActivity.class);
        intent.putExtra("CURRENT_WORD_INDEX", 0);

        intent.putExtra("WORD", word);

        intent.putExtra("NAME", dictation.getName());
        intent.putExtra("CURRENT_MODE", "PRACTICE");

        startActivity(intent);

    }


    public void onResume(){
        super.onResume();

        Log.d(LOG_TAG, "OnResume is called");


        cursor = dbHelper.getDictationList();

        cursorAdapter.changeCursor(cursor);

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_dictation_list) {
            Intent intent = new Intent(DictationListActivity.this, DictationListActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_tests_list) {
            Intent intent = new Intent(DictationListActivity.this, TestResultsSummaryActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
//            Intent intent = new Intent(DictationListActivity.this, SettingsActivity.class);
//            startActivity(intent);

            Intent dbmanager = new Intent(DictationListActivity.this,AndroidDatabaseManager.class);
            startActivity(dbmanager);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void showDialog(final View v) {
        Dictation dictation  = (Dictation) v.getTag();

        new AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("Do you really want to delete dictation " + dictation.getName() + "?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        deleteDictation(v);
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }
    public void onPause(){

        if(cursor!=null){
            cursor.close();
        }
        super.onPause();
    }




}
