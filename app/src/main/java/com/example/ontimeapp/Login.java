package com.example.ontimeapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Timer;
import java.util.TimerTask;

public class Login extends AppCompatActivity {

    EditText emailEditText, passwordEditText;
    Button loginButton;
    ProgressBar progressBar;
    TextView noHaveAcc;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        mAuth=FirebaseAuth.getInstance();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        passwordEditText = findViewById(R.id.password);
        emailEditText = findViewById(R.id.email);
        noHaveAcc = findViewById(R.id.noHaveAcc);
        progressBar = findViewById(R.id.progressBar);
        loginButton = findViewById(R.id.login);

        noHaveAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to navigate to the Login activity
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
                return;
            }
        });

        emailEditText.setOnFocusChangeListener((View v, boolean hasFocus) -> {
            if (hasFocus) {
                emailEditText.setHint(""); // Clear hint when focused
            } else if (emailEditText.getText().toString().isEmpty()) {
                emailEditText.setHint("Masukkan email"); // Restore hint if no text is entered
            }
        });

        passwordEditText.setOnFocusChangeListener((View v, boolean hasFocus) -> {
            if (hasFocus) {
                passwordEditText.setHint(""); // Clear hint when focused
            } else if (passwordEditText.getText().toString().isEmpty()) {
                passwordEditText.setHint("Masukkan kata sandi"); // Restore hint if no text is entered
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String email, password;
                email = String.valueOf(emailEditText.getText());
                password = String.valueOf(passwordEditText.getText());

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        // Hide the ProgressBar
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }
                }, 1000);

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(Login.this, "Masukkan Email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)){
                    Toast.makeText(Login.this, "Masukkan Kata Sandi", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(Login.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                return;
            }
        });

    }
}