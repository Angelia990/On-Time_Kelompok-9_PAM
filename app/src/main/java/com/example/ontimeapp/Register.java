package com.example.ontimeapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.ref.Reference;
import java.util.Timer;
import java.util.TimerTask;

public class Register extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseDatabase db;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText fullNameEditText = findViewById(R.id.fullName);
        EditText passwordEditText = findViewById(R.id.password);
        EditText emailEditText = findViewById(R.id.email);
        EditText pwconfirmEditText = findViewById(R.id.pw_confirm);
        TextView haveAcc = findViewById(R.id.haveAcc);
        Button startRegister = findViewById(R.id.startRegister);
        ProgressBar progressBar = findViewById(R.id.progressBar);

        haveAcc.setOnClickListener(v -> {
            Intent intent = new Intent(Register.this, Login.class);
            startActivity(intent);
        });

        startRegister.setOnClickListener(v -> {
            db = FirebaseDatabase.getInstance("https://tugaskelompokpapb-default-rtdb.asia-southeast1.firebasedatabase.app");
            reference = db.getReference("Users");
            progressBar.setVisibility(View.VISIBLE);
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String fullName = fullNameEditText.getText().toString().trim();
            String pwconfirm = pwconfirmEditText.getText().toString().trim();

            HelperClass helperClass = new HelperClass(fullName, email);

            // Hide the ProgressBar after a short delay
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(() -> progressBar.setVisibility(View.GONE));
                }
            }, 1000);

            // Validate inputs
            if (TextUtils.isEmpty(fullName)) {
                Toast.makeText(Register.this, "Masukkan Nama", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(Register.this, "Masukkan Email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(Register.this, "Masukkan Kata Sandi", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isPasswordValid(password)) {
                return;
            }

            if (!password.equals(pwconfirm)) {
                Toast.makeText(Register.this, "Kata Sandi Tidak Sama", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(Register.this, "Masukkan Alamat Email yang Valid", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Get the UID of the newly created user
                                String userId = task.getResult().getUser().getUid();

                                // Save user data to Firebase Realtime Database
                                reference.child(userId).setValue(helperClass)
                                        .addOnCompleteListener(dbTask -> {
                                            if (dbTask.isSuccessful()) {
                                                Toast.makeText(Register.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.GONE);
                                                Intent intent = new Intent(Register.this, Login.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        });
                            } else {
                                Toast.makeText(Register.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
        });
    }

    // Method to validate password
    private boolean isPasswordValid(String password) {
        // Check for minimum length
        if (password.length() < 6) {
            Toast.makeText(this, "Kata Sandi harus terdiri dari minimal 6 karakter", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check for at least one letter and one digit
        boolean hasLetter = false;
        boolean hasDigit = false;

        for (char ch : password.toCharArray()) {
            if (Character.isLetter(ch)) {
                hasLetter = true;
            } else if (Character.isDigit(ch)) {
                hasDigit = true;
            }
        }

        if (!hasLetter) {
            Toast.makeText(this, "Kata Sandi harus memiliki setidaknya satu huruf", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!hasDigit) {
            Toast.makeText(this, "Kata Sandi harus memiliki setidaknya satu angka", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true; // Password is valid
    }
}