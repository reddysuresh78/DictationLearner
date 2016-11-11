package com.ri.dictationlearner.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ri.dictationlearner.R;
import com.ri.dictationlearner.domain.TestHistoryItem;

import java.util.ArrayList;

public class TestResultsAdapter extends ArrayAdapter<TestHistoryItem> {

    private static final String LOG_TAG = "TestResultsAdapter";


    // View lookup cache
    private static class ViewHolder {

        TextView testDate;
        TextView totalWordCount;
        TextView attemptedWordCount;
        TextView correctWordCount;
        TextView wrongWordCount;
    }

    public TestResultsAdapter(Context context, ArrayList<TestHistoryItem> items) {
        super(context, R.layout.test_details_list_item, items);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final TestHistoryItem testHistoryItem = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.test_details_list_item, parent, false);

            viewHolder.testDate = (TextView) convertView.findViewById(R.id.tvTestDate);
            viewHolder.totalWordCount = (TextView) convertView.findViewById(R.id.tvTotalWordCount);
            viewHolder.attemptedWordCount = (TextView) convertView.findViewById(R.id.tvAttemptedWordCount);
            viewHolder.correctWordCount = (TextView) convertView.findViewById(R.id.tvCorrectWordCount);
            viewHolder.wrongWordCount= (TextView) convertView.findViewById(R.id.tvWrongWordCount);
 
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data into the template view using the data object
        viewHolder.testDate.setText(testHistoryItem.getTestDate());
        viewHolder.totalWordCount.setText("" + testHistoryItem.getTotalCount());
        viewHolder.attemptedWordCount.setText("" + testHistoryItem.getAttemptedCount());
        viewHolder.correctWordCount.setText("" + testHistoryItem.getCorrectCount());
        viewHolder.wrongWordCount.setText("" + testHistoryItem.getWrongCount());

        return convertView;
    }

}