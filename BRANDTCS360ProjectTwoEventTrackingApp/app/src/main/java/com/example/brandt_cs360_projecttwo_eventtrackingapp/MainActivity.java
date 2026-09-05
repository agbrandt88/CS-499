package com.example.brandt_cs360_projecttwo_eventtrackingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// Primary functionality for app login
public class MainActivity extends AppCompatActivity {

    // References: https://www.geeksforgeeks.org/android/user-login-in-android-using-back4app/

    private EditText editUsername;
    private EditText editPassword;
    private EditText editPasswordTwo;
    private Button buttonLogin;
    private Button buttonCreateAccount;
    private EventRepository eventRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Allows for database connectivity
        eventRepository = EventRepository.getInstance(this);

        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        editPasswordTwo = findViewById(R.id.editPasswordTwo);

        buttonLogin = findViewById(R.id.buttonLogin);
        buttonCreateAccount = findViewById(R.id.buttonCreateAccount);

        // Username & password authentication upon button click
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = editUsername.getText().toString();
                String password = editPassword.getText().toString();

                UserTable user = eventRepository.getUsername(username);

                // Checks for username entry and password match. If valid, transitions to home display
                if (user == null) {
                    Toast.makeText(MainActivity.this, "Invalid username", Toast.LENGTH_SHORT).show();
                } else if (!user.password.equals(password)) {
                    Toast.makeText(MainActivity.this, "Invalid password", Toast.LENGTH_SHORT).show();
                } else {
                    Intent i = new Intent(MainActivity.this, HomeActivity.class);
                    i.putExtra("username", username);
                    i.putExtra("userId", user.id);
                    startActivity(i);
                }
            }
        });

        // Allows for account creation upon button engagement
        buttonCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = editUsername.getText().toString();
                String password = editPassword.getText().toString();
                String reenterPassword = editPasswordTwo.getText().toString();

                UserTable currentUser = eventRepository.getUsername(username);

                // Verifies matching password entries
                if (!password.equals(reenterPassword)) {
                    Toast.makeText(MainActivity.this, "Passwords must match", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Authenticates new username then adds to database. Finally, transition to home screen.
                if (currentUser != null) {
                    Toast.makeText(MainActivity.this, "Username taken", Toast.LENGTH_SHORT).show();
                } else {
                    UserTable newUser = new UserTable();
                    newUser.username = username;
                    newUser.password = password;


                    Intent i = new Intent(MainActivity.this, HomeActivity.class);
                    i.putExtra("username", username);
                    i.putExtra("userId", (int) eventRepository.insertUsername(newUser));
                    startActivity(i);

                }

            }
        });

    }
}