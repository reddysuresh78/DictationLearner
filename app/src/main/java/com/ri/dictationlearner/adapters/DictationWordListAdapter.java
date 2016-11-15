package com.ri.dictationlearner.adapters;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ri.dictationlearner.R;
import com.ri.dictationlearner.domain.GlobalState;
import com.ri.dictationlearner.domain.Word;
import com.ri.dictationlearner.utils.DatabaseUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DictationWordListAdapter extends CursorAdapter {


    private static final String LOG_TAG = "DictationWordsAdapter";

    private boolean mReadOnlyMode = true;

    // View lookup cache
    static class ViewHolder {


        @InjectView(R.id.tvWord)
        TextView word;
        @InjectView(R.id.ibPlaySound)
        ImageButton playSound;
        @InjectView(R.id.ibEditWord)
        ImageButton editWord;
        @InjectView(R.id.ibDeleteWord)
        ImageButton deleteWord;
        @InjectView(R.id.ibShowWordImage)
        ImageButton showImage;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

    }

    // Default constructor
    public DictationWordListAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View convertView = inflater.inflate(R.layout.word_list_item, viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(convertView);

//        viewHolder.image = (ImageView) convertView.findViewById(R.id.ibShowWordImage);
//        viewHolder.word = (TextView) convertView.findViewById(R.id.tvWord);
//        viewHolder.playSound = (ImageButton) convertView.findViewById(R.id.ibPlaySound);
//        viewHolder.editWord = (ImageButton) convertView.findViewById(R.id.ibEditWord);
//        viewHolder.deleteWord = (ImageButton) convertView.findViewById(R.id.ibDeleteWord);
//        viewHolder.showImage = (ImageButton) convertView.findViewById(R.id.ibShowWordImage);

        if(!GlobalState.isParentMode()) {

            Log.d(LOG_TAG, "Entered layout resetting");
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewHolder.showImage.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            viewHolder.showImage.setLayoutParams(params);


        }

        convertView.setTag(viewHolder);

        return convertView;

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        DictationWordListAdapter.ViewHolder viewHolder = (DictationWordListAdapter.ViewHolder) view.getTag();

        Word word = DatabaseUtils.getWord(cursor);

        viewHolder.playSound.setTag(word);
        viewHolder.editWord.setTag(word);
        viewHolder.deleteWord.setTag(word);
        viewHolder.showImage.setTag(word);

        // Populate the data into the template view using the data object
        viewHolder.word.setText(word.getWord());

        viewHolder.deleteWord.setVisibility(mReadOnlyMode ?View.GONE: View.VISIBLE);
        viewHolder.editWord.setVisibility(mReadOnlyMode ?View.GONE: View.VISIBLE);
    }

    public void setReadOnlyMode(boolean readOnlyMode) {
        this.mReadOnlyMode = readOnlyMode;
    }
}