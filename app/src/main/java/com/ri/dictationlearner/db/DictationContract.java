package com.ri.dictationlearner.db;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Suresh on 16-11-2016.
 */

public class DictationContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.ri.dictationlearner";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.example.android.sunshine.app/weather/ is a valid path for
    // looking at weather data. content://com.example.android.sunshine.app/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    // At least, let's hope not.  Don't be that dev, reader.  Don't be that dev.
    public static final String PATH_WORDS = "words";


    /* Inner class that defines the table contents of the location table */
    public static final class WordsEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_WORDS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WORDS;


        // Table name
        public static final String TABLE_NAME = "WORDS";

        public static final String KEY_ID = "_id";
        public static final String KEY_WORD = "word";
        public static final String KEY_WORD_IMAGE = "word_image";
        public static final String KEY_DICTATION_ID = "dict_id";
        public static final String KEY_WORD_ORDER = "word_order";


        public static Uri buildWordUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getDictationIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }


}
