package com.ri.dictationlearner.activity.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG_TAG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    private static final String ON_DELETE_CASCADE = " ON DELETE CASCADE";

    // Database Name
    private static final String DATABASE_NAME = "DictationLearner";

    // Table Names
    private static final String TABLE_DICTATIONS = "DICTATIONS";
    private static final String TABLE_WORDS = "WORDS";
    private static final String TABLE_TESTS = "TESTS";
    private static final String TABLE_TEST_WORDS = "TEST_WORDS";

    // column names for DICTATIONS
    private static final String KEY_ID = "_id";
    private static final String KEY_DICTATION_NAME = "dict_name";
    private static final String KEY_DICTATION_IMAGE = "dict_image";
    private static final String KEY_DICTATION_WORD_COUNT= "dict_word_count";
    private static final String KEY_DICTATION_MODIFIED_DATE = "dict_modified_date";
    private static final String KEY_DICTATION_CREATED_DATE = "dict_created_date";

    // column names for WORDS
    private static final String KEY_WORD = "word";
    private static final String KEY_WORD_IMAGE = "word_image";
    private static final String KEY_DICTATION_ID = "dict_id";
    private static final String KEY_WORD_ORDER = "word_order";

    // column names for TESTS
    private static final String KEY_TEST_DATE = "test_date";
    private static final String KEY_WORD_TOTAL_COUNT = "total_count";
    private static final String KEY_WORD_ATTEMPT_COUNT = "attempt_count";
    private static final String KEY_WORD_CORRECT_COUNT = "correct_count";
    private static final String KEY_WORD_WRONG_COUNT = "wrong_count";

    // column names for TEST_WORDS
    private static final String KEY_TEST_ID = "test_id";
    private static final String KEY_WORD_ACTUAL = "actual_word";
    private static final String KEY_WORD_ENTERED = "entered_word";
    private static final String KEY_WORD_STATUS = "status";

    // Table Create Statements
    // DICTATIONS table create statement
    private static final String CREATE_TABLE_DICTATIONS = "CREATE TABLE "
            + TABLE_DICTATIONS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_DICTATION_NAME + " TEXT,"
            + KEY_DICTATION_WORD_COUNT + " INTEGER,"
            + KEY_DICTATION_CREATED_DATE + " INTEGER,"
            + KEY_DICTATION_MODIFIED_DATE + " INTEGER,"
            + KEY_DICTATION_IMAGE + " BLOB)";


    // WORDS table create statement
    private static final String CREATE_TABLE_WORDS = "CREATE TABLE "
            + TABLE_WORDS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_DICTATION_ID + " INTEGER,"
            + KEY_WORD + " TEXT,"
            + KEY_WORD_ORDER + " INTEGER,"
            + KEY_WORD_IMAGE + " BLOB,"
            + " FOREIGN KEY (" + KEY_DICTATION_ID + ") REFERENCES " +  TABLE_DICTATIONS + "("+ KEY_ID + ")"
            + ON_DELETE_CASCADE
            + ")";

    // TESTS table create statement
    private static final String CREATE_TABLE_TESTS = "CREATE TABLE "
            + TABLE_TESTS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_DICTATION_ID + " INTEGER,"
            + KEY_TEST_DATE + " INTEGER,"
            + KEY_WORD_TOTAL_COUNT + " INTEGER,"
            + KEY_WORD_ATTEMPT_COUNT + " INTEGER,"
            + KEY_WORD_CORRECT_COUNT + " INTEGER,"
            + KEY_WORD_WRONG_COUNT + " INTEGER,"
            + " FOREIGN KEY (" + KEY_DICTATION_ID + ") REFERENCES " +  TABLE_DICTATIONS + "("+ KEY_ID + ")"
            + ON_DELETE_CASCADE
            + ")";

    // TEST_WORDS table create statement
    private static final String CREATE_TABLE_TEST_WORDS = "CREATE TABLE "
            + TABLE_TEST_WORDS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_TEST_ID + " INTEGER,"
            + KEY_WORD_ACTUAL + " TEXT,"
            + KEY_WORD_ENTERED + " TEXT,"
            + KEY_WORD_STATUS + " INTEGER,"
            + " FOREIGN KEY (" + KEY_TEST_ID + ") REFERENCES " +  TABLE_TESTS + "("+ KEY_ID + ")"
            + ON_DELETE_CASCADE
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating required tables
        db.execSQL(CREATE_TABLE_DICTATIONS);
        db.execSQL(CREATE_TABLE_WORDS);
        db.execSQL(CREATE_TABLE_TESTS);
        db.execSQL(CREATE_TABLE_TEST_WORDS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


    }

    public long addDictation(String name, byte[] image ) {

        Log.d(LOG_TAG, "Adding Dictation record for dict  " + name);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(KEY_DICTATION_NAME,name);
        contentValues.put(KEY_DICTATION_CREATED_DATE, new java.util.Date().getTime());

        contentValues.put(KEY_DICTATION_WORD_COUNT, 0);

        if(image!=null) {
            contentValues.put(KEY_DICTATION_IMAGE, image);
        }

        long id = db.insert(TABLE_DICTATIONS, null, contentValues);
        db.close();
        return id;
    }

    public long addTest(final int dictationId) {

        long totalCount = getTotalWordCount(dictationId);

        Log.d(LOG_TAG, "Adding Test record for dict/count " + dictationId + "/" + totalCount);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(KEY_DICTATION_ID,dictationId);
        contentValues.put(KEY_TEST_DATE,new java.util.Date().getTime());
        contentValues.put(KEY_WORD_TOTAL_COUNT, totalCount);
        contentValues.put(KEY_WORD_ATTEMPT_COUNT, 0);
        contentValues.put(KEY_WORD_CORRECT_COUNT, 0);
        contentValues.put(KEY_WORD_WRONG_COUNT, 0);

        long id = db.insert(TABLE_TESTS, null, contentValues);
        db.close();
        return id;
    }

    public long addTestWord(final long testId, String actualWord, String enteredWord, int status  ) {

        Log.d(LOG_TAG, "Adding Test word record for testId/actual/entered " + testId + "/" + actualWord + "/" + enteredWord);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(KEY_TEST_ID,testId);
        contentValues.put(KEY_WORD_ACTUAL, actualWord);
        contentValues.put(KEY_WORD_ENTERED, enteredWord);
        contentValues.put(KEY_WORD_STATUS, status);

        long id = db.insert(TABLE_TEST_WORDS, null, contentValues);
        db.close();
        return id;
    }

    public void deleteAllTestWords(int testId) {
        Log.d(LOG_TAG,"Deleting all test words for testId " + testId);
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TEST_WORDS, "test_id = ?", new String[] { String.valueOf(testId) });
        db.close();
    }


    public void updateTest(long id, Integer attemptCount, Integer correctCount, Integer wrongCount) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        if(attemptCount != null) {
            contentValues.put(KEY_WORD_ATTEMPT_COUNT, attemptCount);
        }
        if(correctCount !=null) {
            contentValues.put(KEY_WORD_CORRECT_COUNT, correctCount);
        }
        if(wrongCount !=null) {
            contentValues.put(KEY_WORD_WRONG_COUNT, wrongCount);
        }

        db.execSQL("update TESTS set attempt_count = attempt_count + ?, "+
                "correct_count = correct_count + ?, wrong_count = wrong_count+ ? where _id= ?",
                new String[] { String.valueOf(attemptCount),
                        String.valueOf(correctCount),
                        String.valueOf(wrongCount),
                        String.valueOf(id)
                });

        db.close();
    }


    public long getTotalWordCount(final int dictationID) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor =  db.rawQuery("SELECT dict_word_count FROM DICTATIONS WHERE _id = ?", new String[] { String.valueOf( dictationID)});

        long count = 0;
        if (null != cursor && cursor.getCount() > 0) {
            cursor.moveToFirst();
            count = cursor.getLong(0);
            cursor.close();
        }

        return count;
    }

    public void updateDictation(int id, String name, Integer wordCount,  byte[] image ) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        if(name != null) {
            contentValues.put(KEY_DICTATION_NAME, name);
        }
        if(image != null) {
            contentValues.put(KEY_DICTATION_IMAGE, image);
        }
        if(wordCount != null) {
            contentValues.put(KEY_DICTATION_WORD_COUNT, wordCount);
        }

        contentValues.put(KEY_DICTATION_MODIFIED_DATE, new java.util.Date().getTime());

        db.update(TABLE_DICTATIONS, contentValues,"_id= ?", new String[] { String.valueOf(id) });

        db.close();
    }

    public void deleteDictation(int id) {
         //Delete all words
        deleteAllWords(id);

        SQLiteDatabase db = this.getWritableDatabase();
        //Now delete dictation
        db.delete(TABLE_DICTATIONS, "_id= ?", new String[] { String.valueOf(id) });
        db.close();
    }

    public Cursor getDictationList() {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery("SELECT _id, dict_name,dict_word_count,dict_image FROM dictations d", null);

    }

    public Cursor getTestHistoryWordDetails(final int testID) {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery("SELECT actual_word,entered_word, status FROM test_words WHERE test_id = ?",
                new String[] { String.valueOf( testID ) });
    }

    public Cursor getWordList(final int dictationID) {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery("SELECT _id,dict_id, word, word_order,word_image FROM words WHERE dict_id = ?",
                new String[] { String.valueOf( dictationID) });

    }

    public Cursor getTestsForDictation(final long dictationID) {

        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("select _id, dict_id, test_date,  total_count, attempt_count, correct_count, wrong_count from tests where dict_id = ? " , new String[] { String.valueOf( dictationID)} );
    }


    public Cursor getAllTestsList() {

        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("select _id, dict_id, total_count, correct_count from tests where _id in (SELECT max(_id) FROM tests group by dict_id)" , null);
   }

    public Cursor getWordImage(final int dictationID, final int wordId) {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery("SELECT word_image FROM words WHERE dict_id = ? and _id = ?",
                new String[] { String.valueOf( dictationID), String.valueOf(wordId) });

    }

    public long addWord(int dictationId, String word, int order,  byte[] image ) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(KEY_DICTATION_ID,dictationId);
        contentValues.put(KEY_WORD, word);
        contentValues.put(KEY_WORD_ORDER, order);

        if(image!=null) {
            contentValues.put(KEY_WORD_IMAGE , image);
        }

        long id = db.insert(TABLE_WORDS, null, contentValues);

        //Increment word count from dictation
        db.execSQL("update " + TABLE_DICTATIONS + " set " + KEY_DICTATION_WORD_COUNT + " = " + KEY_DICTATION_WORD_COUNT + " + 1 where _id= ?", new String[] {  String.valueOf(dictationId) });

        db.close();
        return id;
    }

    public boolean updateWord(int dictationId, int wordId, String word, Integer order,  byte[] image ) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        if(word != null) {
            contentValues.put(KEY_WORD, word);
        }
        if(order != null) {
            contentValues.put(KEY_WORD_ORDER, order);
        }
        if(image != null) {
            contentValues.put(KEY_WORD_IMAGE, image);
        }

        int recCount = db.update(TABLE_WORDS, contentValues,"_id= ? and dict_id = ?", new String[] { String.valueOf(wordId), String.valueOf(dictationId) });

        db.close();

        return recCount >0;
    }

    public void deleteWord(int dictationId, int wordId) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WORDS, "_id= ? and dict_id = ?", new String[] { String.valueOf(wordId), String.valueOf(dictationId) });
        //Decrement word count from dictation
        db.execSQL("update " + TABLE_DICTATIONS + " set " + KEY_DICTATION_WORD_COUNT + " = " + KEY_DICTATION_WORD_COUNT + " - 1 where _id= ?", new String[] {  String.valueOf(dictationId) });

        db.close();
    }

    public void deleteAllWords(int dictationId) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WORDS, "dict_id = ?", new String[] { String.valueOf(dictationId) });
        //Reset word count to zero from dictation
        db.execSQL("update " + TABLE_DICTATIONS + " set " + KEY_DICTATION_WORD_COUNT + " = 0 where _id= ?", new String[] {  String.valueOf(dictationId) });

        db.close();
    }

    public ArrayList<Cursor> getData(String Query){
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[] { "mesage" };
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2= new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try{
            String maxQuery = Query ;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);


            //add value to cursor2
            Cursor2.addRow(new Object[] { "Success" });

            alc.set(1,Cursor2);
            if (null != c && c.getCount() > 0) {


                alc.set(0,c);
                c.moveToFirst();

                return alc ;
            }
            return alc;
        } catch(SQLException sqlEx){
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        } catch(Exception ex){

            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }


    }

}