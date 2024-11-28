package com.example.ontimeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LandingPage extends AppCompatActivity {

    // Declare FirebaseAuth instance
    private FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
        // Initialize FirebaseAuth and check if the user is signed in
        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Redirect to MainActivity if the user is already signed in
            Intent intent = new Intent(LandingPage.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page); // Use your actual layout file name

        // Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Setup button and click listener
        Button startButton = findViewById(R.id.startLandingPage);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Register activity
                Intent intent = new Intent(LandingPage.this, Register.class); // Replace with your Register class name
                startActivity(intent);
                finish();
                return;
            }
        });
    }
}
