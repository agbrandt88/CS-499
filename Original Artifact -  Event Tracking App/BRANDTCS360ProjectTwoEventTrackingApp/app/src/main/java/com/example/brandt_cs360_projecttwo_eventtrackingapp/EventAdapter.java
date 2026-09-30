package com.example.brandt_cs360_projecttwo_eventtrackingapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// Pulls information from database and displays on cards
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    // References: https://developer.android.com/develop/ui/views/layout/recyclerview#java
    // References: https://abhiandroid.com/ui/adapter#gsc.tab=0
    // References: https://learn.zybooks.com/zybook/CS-360-13011.202616-1/chapter/4/section/6

    // Event list variable
    private List<EventTable> localDataSet;
    private Context context;
    private int userId;

    public EventAdapter(List<EventTable> dataSet, Context context, int userId) {
        this.localDataSet = dataSet;
        this.context = context;
        this.userId = userId;
    }

    // Connecting card data to xml display
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView textCardTitle;
        private final TextView textCardDate;
        private final TextView textCardTime;
        private final Button buttonCardDelete;

        public ViewHolder(View view) {
            super(view);
            textCardTitle = view.findViewById(R.id.textCardTitle);
            textCardDate = view.findViewById(R.id.textCardDate);
            textCardTime = view.findViewById(R.id.textCardTime);
            buttonCardDelete = view.findViewById(R.id.buttonCardDelete);
        }

        public TextView getTextCardTitle() {
            return textCardTitle;
        }

        public TextView getTextCardDate() {
            return textCardDate;
        }

        public TextView getTextCardTime() {
            return textCardTime;
        }

        public Button getButtonCardDelete() {
            return buttonCardDelete;
        }

    }

    // Generates event card
    @NonNull
    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_event_cards, viewGroup, false);

        return new ViewHolder(view);

    }

    // Pulls information into event card
    @Override
    public void onBindViewHolder(EventAdapter.ViewHolder viewHolder, final int position) {

        EventTable event = localDataSet.get(position);

        viewHolder.getTextCardTitle().setText(event.eventName);
        viewHolder.getTextCardDate().setText(event.eventDate);
        viewHolder.getTextCardTime().setText(event.eventTime);

        viewHolder.getButtonCardDelete().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventRepository.getInstance(context).deleteEvent(event);
                localDataSet.remove(event);
                notifyDataSetChanged();
            }
        });

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, AddEventActivity.class);
                i.putExtra("pickEvent", event.id);
                i.putExtra("userId", userId);
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
