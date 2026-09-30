package com.example.brandt_cs360_projecttwo_eventtrackingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

// Supports app home screen and FAB button functionality
public class HomeActivity extends AppCompatActivity {

    // References: https://www.geeksforgeeks.org/android/user-login-in-android-using-back4app/
    // References: https://developer.android.com/develop/ui/views/layout/recyclerview
    // References: https://abhiandroid.com/ui/adapter#gsc.tab=0

    private RecyclerView recyclerEventGrid;
    private FloatingActionButton fabCreateEvent;
    private EventRepository eventRepository;
    private EventAdapter eventAdapter;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        // Added userId to ensure account events are unique to each user login
        userId = getIntent().getIntExtra("userId", -1);

        eventRepository = EventRepository.getInstance(this);
        recyclerEventGrid = findViewById(R.id.recyclerEventGrid);

        // Breaks down cards into grid with two columns
        recyclerEventGrid.setLayoutManager(new GridLayoutManager(this, 2));

        fabCreateEvent = findViewById(R.id.fabCreateEvent);

        eventAdapter = new EventAdapter(new ArrayList<>(), this, userId);
        recyclerEventGrid.setAdapter(eventAdapter);

        // Allows for FAB functionality and opens event creation display
        fabCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, AddEventActivity.class);
                i.putExtra("userId", userId);
                startActivity(i);
            }
        });

    }

    // Shows updated home screen post FAB engagement
    @Override
    protected void onResume() {
        super.onResume();
        EventAdapter eventAdapter = new EventAdapter(eventRepository.getEvents(userId), this, userId);
        recyclerEventGrid.setAdapter(eventAdapter);
    }

}
