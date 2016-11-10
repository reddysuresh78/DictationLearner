package com.ri.dictationlearner.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ri.dictationlearner.R;
import com.ri.dictationlearner.domain.TestHistoryWordDetails;

import java.util.ArrayList;

public class TestResultsWordsAdapter extends ArrayAdapter<TestHistoryWordDetails> {


    // View lookup cache
    private static class ViewHolder {

        TextView actualWord;
        TextView enteredWord;
        ImageView correctIndicator;
        ImageView wrongIndicator;
     }

    public TestResultsWordsAdapter(Context context, ArrayList<TestHistoryWordDetails> items) {
        super(context, R.layout.test_details_word_list_item , items);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final TestHistoryWordDetails testHistoryItem = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.test_details_word_list_item, parent, false);

            viewHolder.actualWord = (TextView) convertView.findViewById(R.id.tvActualWord);
            viewHolder.enteredWord = (TextView) convertView.findViewById(R.id.tvEnteredWord);
            viewHolder.correctIndicator = (ImageView) convertView.findViewById(R.id.ivCorrectStatus);
            viewHolder.wrongIndicator = (ImageView) convertView.findViewById(R.id.ivWrongStatus);
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data into the template view using the data object
        viewHolder.actualWord.setText(testHistoryItem.getActualWord());
        viewHolder.enteredWord.setText(testHistoryItem.getEnteredWord());
        viewHolder.wrongIndicator.setVisibility(testHistoryItem.isCorrectIndicator()?View.GONE: View.VISIBLE);
        viewHolder.correctIndicator.setVisibility(!testHistoryItem.isCorrectIndicator()?View.GONE: View.VISIBLE);


        return convertView;
    }

}