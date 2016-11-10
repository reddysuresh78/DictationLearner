package com.ri.dictationlearner.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ri.dictationlearner.R;
import com.ri.dictationlearner.domain.Dictation;
import com.ri.dictationlearner.utils.DatabaseUtils;
import com.ri.dictationlearner.utils.Utils;

public class DictationsCursorAdapter extends CursorAdapter {

    private boolean mReadOnlyMode = true;

    // View lookup cache
    private static class ViewHolder {
        ImageView icon;
        TextView name;
        TextView testDate;
        TextView wordCount;
        ImageButton editDictation;
        ImageButton deleteDictation;
        ImageButton takeTest;
        ImageButton practiceDictation;
    }


    // Default constructor
    public DictationsCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
//        View convertView = ((LayoutInflater) context
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.dictation_list_item, viewGroup, false);

        ViewHolder viewHolder = new ViewHolder();
        LayoutInflater inflater = LayoutInflater.from(context);
        View convertView = inflater.inflate(R.layout.dictation_list_item, viewGroup, false);

        viewHolder.name = (TextView) convertView.findViewById(R.id.tvName);
        viewHolder.testDate = (TextView) convertView.findViewById(R.id.tvTestDate);
        viewHolder.wordCount = (TextView) convertView.findViewById(R.id.tvWordCount);
        viewHolder.icon = (ImageView) convertView.findViewById(R.id.ivIcon);
        viewHolder.editDictation = (ImageButton) convertView.findViewById(R.id.ibEditDictation);

        viewHolder.deleteDictation= (ImageButton) convertView.findViewById(R.id.ibDeleteDictation);

        viewHolder.takeTest = (ImageButton) convertView.findViewById(R.id.ibTakeTest);

        viewHolder.practiceDictation= (ImageButton) convertView.findViewById(R.id.ibPracticeDictation);

        // Cache the viewHolder object inside the fresh view
        convertView.setTag(viewHolder);

        return convertView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        Dictation dictation = DatabaseUtils.getDictation(cursor);

        viewHolder.name.setText(dictation.getName());
//        viewHolder.testDate.setText(Utils.getFormattedDate(dictation.getTestDate()));
        viewHolder.wordCount.setText("" +dictation.getWordCount());


        if(dictation.getImage() != null ) {
            viewHolder.icon.setImageBitmap(Utils.getImage(dictation.getImage()));
        }else{
            viewHolder.icon.setImageResource(dictation.getImageResourceId());
        }

        viewHolder.editDictation.setEnabled(!mReadOnlyMode);
        viewHolder.deleteDictation.setEnabled(!mReadOnlyMode);
        viewHolder.deleteDictation.setVisibility(mReadOnlyMode ?View.GONE: View.VISIBLE);
        viewHolder.editDictation.setVisibility(mReadOnlyMode ?View.GONE: View.VISIBLE);
        viewHolder.takeTest.setVisibility(!mReadOnlyMode ?View.GONE: View.VISIBLE);
        viewHolder.practiceDictation.setVisibility(!mReadOnlyMode ?View.GONE: View.VISIBLE);

        viewHolder.editDictation.setTag(dictation);
        viewHolder.deleteDictation.setTag(dictation);
        viewHolder.takeTest.setTag(dictation);
        viewHolder.practiceDictation.setTag(dictation);

    }


    public void setReadOnlyMode(boolean readOnlyMode) {
        this.mReadOnlyMode = readOnlyMode;
    }

}