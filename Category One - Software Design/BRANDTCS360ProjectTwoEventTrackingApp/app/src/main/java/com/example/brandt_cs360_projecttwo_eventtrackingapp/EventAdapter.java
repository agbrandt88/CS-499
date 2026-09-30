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

import java.util.ArrayList;
import java.util.List;

// Displays event cards, communicates user card & delete clicks to HomeActivity.
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    // References: https://developer.android.com/develop/ui/views/layout/recyclerview#java
    // References: https://abhiandroid.com/ui/adapter#gsc.tab=0
    // References: https://learn.zybooks.com/zybook/CS-360-13011.202616-1/chapter/4/section/6

    private List<EventTable> events = new ArrayList<>();
    private EventClickListener eventClickListener;

    // Communicates user clicks, does not directly interact with database IAW separation of concerns.
    public interface EventClickListener {

        void userEventClick(EventTable event);
        void userDeleteClick(EventTable event);
    }

    public EventAdapter(EventClickListener eventClickListener) {
        this.eventClickListener = eventClickListener;
    }

    public void setEvents(List<EventTable> events) {
        this.events = events;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textCardTitle;
        TextView textCardDate;
        TextView textCardTime;
        Button buttonCardDelete;

        public ViewHolder(View view) {
            super(view);
            textCardTitle = view.findViewById(R.id.textCardTitle);
            textCardDate = view.findViewById(R.id.textCardDate);
            textCardTime = view.findViewById(R.id.textCardTime);
            buttonCardDelete = view.findViewById(R.id.buttonCardDelete);
        }

    }

    // Generates event card display for populating.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_event_cards, viewGroup, false);

        return new ViewHolder(view);
    }

    // Populates event card data, sets up onClickListener for card & delete.
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        EventTable event = events.get(position);

        viewHolder.textCardTitle.setText(event.eventName);
        viewHolder.textCardDate.setText(event.eventDate);
        viewHolder.textCardTime.setText(event.eventTime);

        viewHolder.buttonCardDelete.setOnClickListener(view -> eventClickListener.userDeleteClick(event));
        viewHolder.itemView.setOnClickListener(view -> eventClickListener.userEventClick(event));

    }

    @Override
    public int getItemCount() {
        return events.size();
    }
}
