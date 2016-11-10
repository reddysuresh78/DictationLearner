package com.ri.dictationlearner.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ri.dictationlearner.R;
import com.ri.dictationlearner.domain.Dictation;

import java.util.ArrayList;

public class DictationsAdapter extends ArrayAdapter<Dictation> {

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



    public DictationsAdapter(Context context, ArrayList<Dictation> dictations) {
        super(context, R.layout.dictation_list_item, dictations);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Dictation dictation = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.dictation_list_item, parent, false);
            viewHolder.name = (TextView) convertView.findViewById(R.id.tvName);
            viewHolder.testDate = (TextView) convertView.findViewById(R.id.tvTestDate);
            viewHolder.wordCount = (TextView) convertView.findViewById(R.id.tvWordCount);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.ivIcon);
            viewHolder.editDictation = (ImageButton) convertView.findViewById(R.id.ibEditDictation);
            viewHolder.editDictation.setTag(dictation);
            viewHolder.deleteDictation= (ImageButton) convertView.findViewById(R.id.ibDeleteDictation);
            viewHolder.deleteDictation.setTag(dictation);
            viewHolder.takeTest = (ImageButton) convertView.findViewById(R.id.ibTakeTest);
            viewHolder.takeTest.setTag(dictation);
            viewHolder.practiceDictation= (ImageButton) convertView.findViewById(R.id.ibPracticeDictation);
            viewHolder.practiceDictation.setTag(dictation);
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data into the template view using the data object
        viewHolder.name.setText(dictation.getName());
//        viewHolder.testDate.setText(Utils.getFormattedDate(dictation.getTestDate()));
        viewHolder.wordCount.setText("" +dictation.getWordCount());
        viewHolder.icon.setImageResource(dictation.getImageResourceId());

        viewHolder.editDictation.setEnabled(!mReadOnlyMode);
        viewHolder.deleteDictation.setEnabled(!mReadOnlyMode);
        viewHolder.deleteDictation.setVisibility(mReadOnlyMode ?View.GONE: View.VISIBLE);
        viewHolder.editDictation.setVisibility(mReadOnlyMode ?View.GONE: View.VISIBLE);
        viewHolder.takeTest.setVisibility(!mReadOnlyMode ?View.GONE: View.VISIBLE);
        viewHolder.practiceDictation.setVisibility(!mReadOnlyMode ?View.GONE: View.VISIBLE);


        return convertView;
    }


    public void setReadOnlyMode(boolean readOnlyMode) {
        this.mReadOnlyMode = readOnlyMode;
    }

}