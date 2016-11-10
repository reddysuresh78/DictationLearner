package com.ri.dictationlearner.activity.db;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

public class Global {
    private static Global instance = null;
    private Context context;

    public Global( Context context ) {
        this.context = context.getApplicationContext();
    }

    public static Global getInstance( Context context ) {
        if( instance == null )
            instance = new Global( context );
        return instance;
    }

    public SQLiteOpenHelper getDatabase() {
        return new DatabaseHelper(context);
    }
}