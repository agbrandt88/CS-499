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
import androidx.lifecycle.ViewModelProvider;

// UI portion of user login & account creation, communicates with AccountViewModel.
public class MainActivity extends AppCompatActivity {

    // References: https://www.geeksforgeeks.org/android/user-login-in-android-using-back4app/

    private EditText editUsername;
    private EditText editPassword;
    private EditText editPasswordTwo;
    private Button buttonLogin;
    private Button buttonCreateAccount;
    private AccountViewModel accountViewModel;

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


        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        editPasswordTwo = findViewById(R.id.editPasswordTwo);

        buttonLogin = findViewById(R.id.buttonLogin);
        buttonCreateAccount = findViewById(R.id.buttonCreateAccount);

        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);

        // Observes user access result without impacting logic.
        accountViewModel.getUserAccessData().observe(this, user -> {
            if (user == null) {
                return;
            }

            buttonLogin.setEnabled(true);
            buttonCreateAccount.setEnabled(true);

            Intent i = new Intent(MainActivity.this, HomeActivity.class);
            i.putExtra("username", user.username);
            i.putExtra("userId", user.id);
            startActivity(i);
        });

        accountViewModel.getErrorData().observe(this, message -> {
            if (message == null) {
                return;
            }

            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();

            buttonLogin.setEnabled(true);
            buttonCreateAccount.setEnabled(true);
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                buttonLogin.setEnabled(false);
                buttonCreateAccount.setEnabled(false);

                accountViewModel.userLogin(editUsername.getText().toString(), editPassword.getText().toString());

            }
        });

        buttonCreateAccount.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                buttonLogin.setEnabled(false);
                buttonCreateAccount.setEnabled(false);

                accountViewModel.userCreateAccount(editUsername.getText().toString(), editPassword.getText().toString(),
                        editPasswordTwo.getText().toString());

            }
        });

    }
}