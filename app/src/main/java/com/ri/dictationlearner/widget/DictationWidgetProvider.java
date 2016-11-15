package com.ri.dictationlearner.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;

import com.ri.dictationlearner.R;
import com.ri.dictationlearner.activity.db.DatabaseHelper;

public class DictationWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int count = appWidgetIds.length;

        DatabaseHelper dbHelper= new DatabaseHelper(context);

        Cursor cursor = dbHelper.getDictationList();

        int dictCount = cursor.getCount();

        if(cursor != null ) {
            dictCount = cursor.getCount();
            cursor.close();
        }

        for (int i = 0; i < count; i++) {
            int widgetId = appWidgetIds[i];
            String number = String.format("%03d", dictCount);

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.dictation_widget);
            remoteViews.setTextViewText(R.id.textView, number);

            Intent intent = new Intent(context, DictationWidgetProvider.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.actionButton, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);

        }
    }


}