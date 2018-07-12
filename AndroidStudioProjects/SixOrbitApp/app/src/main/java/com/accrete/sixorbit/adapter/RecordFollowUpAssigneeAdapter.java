package com.accrete.sixorbit.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.model.ChatContacts;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by agt on 22/9/17.
 */

public class RecordFollowUpAssigneeAdapter extends BaseAdapter implements Filterable {
    private Activity activity;
    private List<ChatContacts> contacts;
    private List<ChatContacts> suggestions = new ArrayList<>();
    private Filter filter = new CustomFilter();

    public RecordFollowUpAssigneeAdapter(Activity activity, List<ChatContacts> contacts) {
        this.activity = activity;
        this.contacts = contacts;

    }

    public int getCount() {
        // TODO Auto-generated method stub
        return suggestions.size();
    }

    @Override
    public Object getItem(int position) {
        return suggestions.get(position).getName();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(activity);

        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.simple_spinner_item, parent, false);
            holder = new ViewHolder();
            holder.autoText = (TextView) convertView.findViewById(android.R.id.text1);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.autoText.setText(suggestions.get(position).getName());

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private static class ViewHolder {
        TextView autoText;
    }

    /**
     * Our Custom Filter Class.
     */
    private class CustomFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            suggestions.clear();

            if (contacts != null && constraint != null) { // Check if the Original List and Constraint aren't null.
                for (int i = 0; i < contacts.size(); i++) {
                    if (contacts.get(i).getName().toLowerCase().contains(constraint)) { // Compare item in original list if it contains constraints.
                        suggestions.add(contacts.get(i)); // If TRUE add item in Suggestions.
                    }
                }
            }
            FilterResults results = new FilterResults(); // Create new Filter Results and return this to publishResults;
            results.values = suggestions;
            results.count = suggestions.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }

}

