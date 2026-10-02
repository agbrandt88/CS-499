package com.example.brandt_cs360_projecttwo_eventtrackingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

// Display user event page, communicates user clicks to LoadEventViewModel (delete) or AddEventActivity
// (event click).
public class HomeActivity extends AppCompatActivity {

    // References: https://www.geeksforgeeks.org/android/user-login-in-android-using-back4app/
    // References: https://developer.android.com/develop/ui/views/layout/recyclerview
    // References: https://abhiandroid.com/ui/adapter#gsc.tab=0

    private RecyclerView recyclerEventGrid;
    private FloatingActionButton fabCreateEvent;
    private EventAdapter eventAdapter;
    private LoadEventViewModel loadEventViewModel;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        // Supports loading of events specific to user.
        userId = getIntent().getIntExtra("userId", -1);

        recyclerEventGrid = findViewById(R.id.recyclerEventGrid);

        // Breaks down cards into grid with two columns
        recyclerEventGrid.setLayoutManager(new GridLayoutManager(this, 2));

        fabCreateEvent = findViewById(R.id.fabCreateEvent);

        loadEventViewModel = new ViewModelProvider(this).get(LoadEventViewModel.class);

        // Supports card display & communicates user clicks.
        eventAdapter = new EventAdapter(new EventAdapter.EventClickListener() {

            @Override
            public void userEventClick(EventTable event) {

                Intent i = new Intent(HomeActivity.this, AddEventActivity.class);
                i.putExtra("pickEvent", event.id);
                i.putExtra("userId", userId);
                startActivity(i);
            }

            // User delete clicks communicated to ViewModel vice being handled by HomeActivity.
            @Override
            public void userDeleteClick(EventTable event) {

                loadEventViewModel.deleteEventData(event.id, userId);

            }
        });

        recyclerEventGrid.setAdapter(eventAdapter);

        // Pulls and displays event list in order from ViewModel.
        loadEventViewModel.getEventData().observe(this, events -> eventAdapter.setEvents(events));
        loadEventViewModel.getErrorData().observe(this, message -> {
            if (message == null) {
                return;
            }

            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        });

        fabCreateEvent.setOnClickListener(view ->  {
            Intent i = new Intent(HomeActivity.this, AddEventActivity.class);
            i.putExtra("userId", userId);
            startActivity(i);
        });

    }

    // Event display on event page updated after closing add/edit event card.
    @Override
    protected void onResume() {
        super.onResume();
        if (loadEventViewModel != null) {
            loadEventViewModel.loadEventData(userId);
        }
    }

}
