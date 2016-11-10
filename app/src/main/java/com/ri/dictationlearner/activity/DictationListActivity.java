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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;

import com.ri.dictationlearner.R;
import com.ri.dictationlearner.activity.db.DatabaseHelper;
import com.ri.dictationlearner.adapters.DictationListAdapter;
import com.ri.dictationlearner.domain.Dictation;
import com.ri.dictationlearner.domain.GlobalState;
import com.ri.dictationlearner.domain.Word;
import com.ri.dictationlearner.utils.DatabaseUtils;

public class DictationListActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {

    private static final String LOG_TAG = "DictationListActivity";

    private DatabaseHelper mDBHelper;

    private DictationListAdapter mDictationListAdapter = null;

    private ListView mListView = null;

    private Cursor mCursor = null;

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictation_list_container);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (findViewById(R.id.dictation_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w820dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        Log.i(LOG_TAG,"OnCreate called");

        GlobalState.init(this);

        if(mCursor != null){
            mCursor.close();
        }

        mDBHelper = new DatabaseHelper(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("MAIN","Trying to create new dictation");
                Intent intent = new Intent(DictationListActivity.this, DictationDetailActivity.class);
                intent.putExtra("OPERATION", "NEW");
                startActivity(intent);
            }
        });

        fab.setVisibility(GlobalState.isParentMode() ? View.VISIBLE: View.GONE );

        mCursor = mDBHelper.getDictationList();

        mDictationListAdapter = new DictationListAdapter(this, mCursor, 0  );
        mDictationListAdapter.setReadOnlyMode(!GlobalState.isParentMode());

// Attach the adapter to a ListView
        mListView = (ListView) findViewById(R.id.lv_dictations);
        mListView.setAdapter(mDictationListAdapter);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                mCursor.moveToPosition(position);
                Dictation dictation = DatabaseUtils.getDictation(mCursor);
                Log.d("MAIN","Trying to view " + dictation.getName());

                if (mTwoPane) {
                    mListView.setSelection(position);

                    Bundle arguments = new Bundle();
                    arguments.putParcelable (DictationDetailFragment.ARG_DICTATION, dictation);
                    arguments.putString (DictationDetailFragment.ARG_CUR_OPERATION, "VIEW");

                    DictationDetailFragment fragment = new DictationDetailFragment();
                    fragment.setArguments(arguments);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.dictation_detail_container, fragment)
                            .commit();

                 }else {

                    Intent intent = new Intent(DictationListActivity.this, DictationDetailActivity.class);

                    intent.putExtra("DICTATION", dictation);
                    intent.putExtra("OPERATION", "VIEW");

                    startActivity(intent);
                }
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

        if(mTwoPane) {
            if (mListView.getCount() > 0) {
                mListView.performItemClick(mListView, 0, mListView.getItemIdAtPosition(0));
            }
        }


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
 

    public void editDictationOnClickHandler(View v) {
        Dictation dictation = (Dictation) v.getTag();

        Log.d("MAIN","Trying to edit " + dictation.getName());

        Intent intent = new Intent(DictationListActivity.this, DictationDetailActivity.class);

        intent.putExtra("DICTATION", dictation);
        intent.putExtra("OPERATION", "EDIT");

        startActivity(intent);

    }

    public void showWordListOnClickHandler(View v) {
        Dictation dictation = (Dictation) v.getTag();
        String title = dictation.getName();
        Intent intent = new Intent(DictationListActivity.this, WordsListActivity.class);
        intent.putExtra("DICTATION", dictation);
        startActivity(intent);
    }



    public void deleteDictationOnClickHandler(View v) {
        showDialog(v);
    }

    private void deleteDictation(View v){
        Dictation dictation = (Dictation) v.getTag();

        Log.d("MAIN","Trying to delete " + dictation.getName());

        mDBHelper.deleteDictation(dictation.getId());

        mCursor.close();

        mCursor = mDBHelper.getDictationList();

        mDictationListAdapter.changeCursor(mCursor);

        Toast.makeText(this,"Dictation Deleted" ,Toast.LENGTH_LONG ).show();

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


        mCursor = mDBHelper.getDictationList();

        mDictationListAdapter.changeCursor(mCursor);

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
        } /* else if (id == R.id.nav_internal_db) {
            Intent dbmanager = new Intent(DictationListActivity.this,AndroidDatabaseManager.class);
            startActivity(dbmanager);
        }*/

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

        if(mCursor !=null){
            mCursor.close();
        }
        super.onPause();
    }




}
