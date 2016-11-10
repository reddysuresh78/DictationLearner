package com.ri.dictationlearner.domain;

import android.content.Context;
import android.content.SharedPreferences;

import com.ri.dictationlearner.R;

/**
 * Created by Suresh on 06-11-2016.
 */

public class GlobalState {

    private static boolean isParentMode = false;
    private static boolean showImageDuringTest = false;

    private static Context context;

    public static void init(Context inContext){
        context = inContext;
        isParentMode = getPreference(context.getString(R.string.pref_parent_mode), true);
        showImageDuringTest = getPreference(context.getString(R.string.pref_test_show_images) ,true);
    }
    public static boolean isShowImagesEnabled() { return showImageDuringTest; }

    public static boolean isParentMode() {
        return isParentMode;
    }

    public static void savePreference(String name, boolean value){

        SharedPreferences sharedPref =  context.getSharedPreferences ( "DICTATION_PREF" , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(name, value);
        editor.commit();

    }

    public static boolean getPreference(String name, boolean defaultValue){

        SharedPreferences sharedPref = context.getSharedPreferences("DICTATION_PREF" , Context.MODE_PRIVATE);
        return sharedPref.getBoolean(name, defaultValue);

    }

}
