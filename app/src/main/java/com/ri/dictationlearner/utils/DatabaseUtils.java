package com.ri.dictationlearner.utils;

import android.database.Cursor;

import com.ri.dictationlearner.R;
import com.ri.dictationlearner.domain.Dictation;
import com.ri.dictationlearner.domain.TestHistoryItem;
import com.ri.dictationlearner.domain.TestHistoryWordDetails;
import com.ri.dictationlearner.domain.TestResultSummaryItem;
import com.ri.dictationlearner.domain.Word;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Suresh on 19-10-2016.
 */

public class DatabaseUtils {

    private DatabaseUtils(){}

    public static Dictation getDictation(Cursor cursor) {

        return new Dictation()
                .setId(cursor.getInt(0))
                .setName(cursor.getString(1))
                .setWordCount(cursor.getInt(2))
                .setImage(cursor.getBlob(3));
    }

    public static Word getWord(Cursor cursor) {

        return new Word()
                .setDictationId(cursor.getInt(1))
                .setImageResourceId(R.drawable.ic_broken_image_white_48dp)
                .setImage(cursor.getBlob(4))
                .setWord(cursor.getString(2))
                .setSerialNo(cursor.getInt(3))
                .setWordId(cursor.getInt(0));

    }

    public static TestResultSummaryItem getTestSummary(Cursor cursor){
        //_id, dict_id, total_count, correct_count
        return new TestResultSummaryItem()
                .setTestId(cursor.getInt(0))
                .setDictationId(cursor.getInt(1))
                .setTotalCount(cursor.getInt(2))
                .setLatestScore(cursor.getInt(3));
    }
//_id, dict_id, total_count, attempt_count, correct_count, wrong_count
    public static TestHistoryItem getTestDetails(Cursor cursor){

        String milliSeconds = cursor.getString(2);

        Date d = new Date ( Long.valueOf(milliSeconds));

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        String testDate  = sdf.format(d);

        return new TestHistoryItem()
                .setTestId(cursor.getInt(0))
                .setDictationId(cursor.getInt(1))
                .setTestDate(testDate)
                .setTotalCount(cursor.getInt(3))
                .setAttemptedCount(cursor.getInt(4))
                .setCorrectCount(cursor.getInt(5))
                .setWrongCount(cursor.getInt(6));
    }

    public static TestHistoryWordDetails getTestHistoryWordDetails(Cursor cursor){
    //actual_word,entered_word, status
        return new TestHistoryWordDetails()
                .setActualWord(cursor.getString(0))
                .setEnteredWord(cursor.getString(1))
                .setCorrectIndicator(cursor.getInt(2) == 0? false: true);

    }

}
