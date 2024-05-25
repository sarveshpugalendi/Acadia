package com.example.acadia;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<ThingSpeakFeed> feeds;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView entryIdView;
        public TextView createdAtView;
        public TextView field1View;
        public TextView field2View;
        public TextView field3View;


        public ViewHolder(View itemView) {
            super(itemView);
            field1View = itemView.findViewById(R.id.field1);
            field2View = itemView.findViewById(R.id.field2);
            field3View = itemView.findViewById(R.id.field3);
        }
    }

    public MyAdapter(List<ThingSpeakFeed> feeds) {
        this.feeds = feeds;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ThingSpeakFeed feed = feeds.get(position);

        holder.field1View.setText(String.valueOf(feed.field1));
        holder.field2View.setText(String.valueOf(feed.field3));
        holder.field3View.setText(String.valueOf(feed.field4));
    }

    @Override
    public int getItemCount() {
        return feeds.size();
    }
}

