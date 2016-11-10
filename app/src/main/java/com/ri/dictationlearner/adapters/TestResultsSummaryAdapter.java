package com.ri.dictationlearner.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ri.dictationlearner.R;
import com.ri.dictationlearner.domain.TestResultSummaryItem;
import com.ri.dictationlearner.utils.Utils;

import java.util.ArrayList;

public class TestResultsSummaryAdapter extends ArrayAdapter<TestResultSummaryItem> {

    // View lookup cache
    private static class ViewHolder {
        ImageView icon;
        TextView name;
        TextView latestScore;

    }
    public TestResultsSummaryAdapter(Context context, ArrayList<TestResultSummaryItem> items) {
        super(context, R.layout.test_results_summary_list_item, items);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final TestResultSummaryItem testResultSummaryItem = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.test_results_summary_list_item, parent, false);

            viewHolder.name = (TextView) convertView.findViewById(R.id.tvName);
            viewHolder.latestScore = (TextView) convertView.findViewById(R.id.tvLblLatestScoreValue);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.ivIcon);

            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.name.setText(testResultSummaryItem.getName());
        viewHolder.latestScore.setText("" +testResultSummaryItem.getLatestScore() + "/" + testResultSummaryItem.getTotalCount());

        if(testResultSummaryItem.getImage() != null ) {
            viewHolder.icon.setImageBitmap(Utils.getImage(testResultSummaryItem.getImage()));
        }else{
            viewHolder.icon.setImageResource(R.drawable.ic_broken_image_white_48dp);
        }

        return convertView;
    }

}