package com.example.hide.lasd5;

import android.graphics.Color;
import android.graphics.Typeface;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.hide.lasd5.lasd5.dict.Dictionary;
import com.example.hide.lasd5.lasd5.dict.Headword;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private static final String TAG = MainActivity.class.getSimpleName();

    private Dictionary dic;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        HistoryAdapter adapter;
        public TextView mTextView;

        public ViewHolder(View v, HistoryAdapter adp) {
            super(v);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    Log.d(TAG, "onClick:" + mTextView.getText() + " pos:" + pos);
                    adapter.onClick(pos);
                }
            });
            adapter = adp;
            mTextView = (TextView) v.findViewById(R.id.textView);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public HistoryAdapter(Dictionary d) {
        dic = d;
    }

    public void onClick(int pos) {
        Headword hw = dic.getHistoryHW(pos);

        if (hw == null) {
            ; // do nothing
        } else {
            dic.setWord(hw);
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_row_item, parent, false);

        ViewHolder vh = new ViewHolder(v, this);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Headword hw = dic.getHistoryHW(position);

        if (hw == null) {
            holder.mTextView.setText("");
        } else {
            holder.mTextView.setText(hw.toString());

            if (hw.isActive()) {
                holder.mTextView.setTextColor(Color.parseColor("#ff33ff"));
                holder.mTextView.setTypeface(null, Typeface.BOLD);
            } else {
                holder.mTextView.setTextColor(Color.parseColor("#000000"));
                holder.mTextView.setTypeface(null, Typeface.NORMAL);
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (dic != null) {
            return dic.getHistorySize();
        } else {
            return 0;
        }
    }

}
