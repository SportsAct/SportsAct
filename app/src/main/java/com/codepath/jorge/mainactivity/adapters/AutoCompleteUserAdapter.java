package com.codepath.jorge.mainactivity.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.codepath.jorge.mainactivity.R;
import com.codepath.jorge.mainactivity.UserItem;

import java.util.ArrayList;
import java.util.List;


public class AutoCompleteUserAdapter extends ArrayAdapter<UserItem> {
    private  List<UserItem> userListFull;

    public AutoCompleteUserAdapter(@NonNull Context context, @NonNull List<UserItem> userList) {
        super(context, 0, userList);
        userListFull = new ArrayList<>(userList);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return userFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.user_autocomplete_row, parent, false);
        }

        TextView textViewName = convertView.findViewById(R.id.text_view_name);
        ImageView imageViewProfilePic = convertView.findViewById(R.id.profilePic);

        UserItem userItem = getItem(position);

        if (userItem == null) {
            textViewName.setText((CharSequence) userItem.getUsername());
            imageViewProfilePic.setImageResource(userItem.getProfileImage());
        }

        return convertView;
    }

    private Filter userFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<UserItem> suggestions = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                suggestions.addAll(userListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (UserItem item : userListFull) {
                    //TODO: .toLowerCase does not work
                    if (item.getUsername().contains(filterPattern));
                    suggestions.add(item);
                }
            }

            results.values = suggestions;
            results.count = suggestions.size();

            return results;
        }


        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            addAll((List) results.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return (CharSequence) ((UserItem) resultValue).getUsername();
        }
    };
}
