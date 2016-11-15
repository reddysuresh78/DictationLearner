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
import com.ri.dictationlearner.utils.ImageUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DictationListAdapter extends CursorAdapter {

    private static final String LOG_TAG = "DictationListAdapter";

    private boolean mReadOnlyMode = true;

    // View lookup cache
    static class ViewHolder {

        @InjectView(R.id.ivIcon)
        ImageView icon;
        @InjectView(R.id.tvName)
        TextView name;

        @InjectView(R.id.tvWordCount)
        TextView wordCount;
        @InjectView(R.id.tvLblWordCount)
        TextView wordCountLabel;
        @InjectView(R.id.ibEditDictation)
        ImageButton editDictation;
        @InjectView(R.id.ibDeleteDictation)
        ImageButton deleteDictation;
        @InjectView(R.id.ibTakeTest)
        ImageButton takeTest;
        @InjectView(R.id.ibPracticeDictation)
        ImageButton practiceDictation;
        @InjectView(R.id.ibShowWordList)
        ImageButton showWordsList;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    // Default constructor
    public DictationListAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View convertView = inflater.inflate(R.layout.dictation_list_item, viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(convertView);

//        viewHolder.name = (TextView) convertView.findViewById(R.id.tvName);
//        viewHolder.testDate = (TextView) convertView.findViewById(R.id.tvTestDate);
//        viewHolder.wordCount = (TextView) convertView.findViewById(R.id.tvWordCount);
//        viewHolder.wordCountLabel = (TextView) convertView.findViewById(R.id.tvLblWordCount);
//        viewHolder.icon = (ImageView) convertView.findViewById(R.id.ivIcon);
//        viewHolder.editDictation = (ImageButton) convertView.findViewById(R.id.ibEditDictation);
//
//        viewHolder.deleteDictation= (ImageButton) convertView.findViewById(R.id.ibDeleteDictation);
//
//        viewHolder.takeTest = (ImageButton) convertView.findViewById(R.id.ibTakeTest);
//
//        viewHolder.practiceDictation= (ImageButton) convertView.findViewById(R.id.ibPracticeDictation);
//
//        viewHolder.showWordsList= (ImageButton) convertView.findViewById(R.id.ibShowWordList);

        // Cache the viewHolder object inside the fresh view
        convertView.setTag(viewHolder);

        return convertView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        Dictation dictation = DatabaseUtils.getDictation(cursor);

        viewHolder.name.setText(dictation.getName());
        viewHolder.wordCount.setText("" +dictation.getWordCount());

        if(dictation.getWordCount() == 1) {
            viewHolder.wordCountLabel.setText("word");
        }else{
            viewHolder.wordCountLabel.setText("words");
        }

        if(dictation.getImage() != null ) {
            viewHolder.icon.setImageBitmap(ImageUtils.getImage(dictation.getImage()));
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
        viewHolder.showWordsList.setTag(dictation);

    }


    public void setReadOnlyMode(boolean readOnlyMode) {
        this.mReadOnlyMode = readOnlyMode;
    }

}