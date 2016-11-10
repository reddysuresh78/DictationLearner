package com.ri.dictationlearner.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.ri.dictationlearner.R;
import com.ri.dictationlearner.domain.Word;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Suresh on 29-09-2016.
 */

public final class Utils {

    public static final String PREFS_NAME = "DictationLearnerPref";

    private Utils(){}

    public static String getFormattedDate(Date date) {
        if(date == null) {
            return "";
        }
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yy");
        return format.format(date);
    }



    public static ArrayList<Word> getWordList(String dictationName) {

        // Construct the data source
        final ArrayList<Word> words = new ArrayList<Word>();

        if(dictationName.equalsIgnoreCase("ANIMALS")){
            words.add(new Word().setWord("Cat").setImageResourceId(R.drawable.cat));
            words.add(new Word().setWord("Cow").setImageResourceId(R.drawable.cow));
            words.add(new Word().setWord("Dog").setImageResourceId(R.drawable.dog));
            words.add(new Word().setWord("Frog").setImageResourceId(R.drawable.frog));
            words.add(new Word().setWord("Hen").setImageResourceId(R.drawable.hen));
            words.add(new Word().setWord("Horse").setImageResourceId(R.drawable.horse));
            words.add(new Word().setWord("Pig").setImageResourceId(R.drawable.pig));
            words.add(new Word().setWord("Sheep").setImageResourceId(R.drawable.sheep));

        }else {

            words.add(new Word().setWord("Sunday").setImageResourceId(R.drawable.weekdays));
            words.add(new Word().setWord("Monday").setImageResourceId(R.drawable.weekdays));
            words.add(new Word().setWord("Tuesday").setImageResourceId(R.drawable.weekdays));
            words.add(new Word().setWord("Wednesday").setImageResourceId(R.drawable.weekdays));
            words.add(new Word().setWord("Thursday").setImageResourceId(R.drawable.weekdays));
            words.add(new Word().setWord("Friday").setImageResourceId(R.drawable.weekdays));
            words.add(new Word().setWord("Saturday").setImageResourceId(R.drawable.weekdays));
        }

        return words;

    }

    // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public static Bitmap resizeBitmap(String photoPath, int targetW, int targetH) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        }

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true; //Deprecated API 21

        return BitmapFactory.decodeFile(photoPath, bmOptions);
    }

}
