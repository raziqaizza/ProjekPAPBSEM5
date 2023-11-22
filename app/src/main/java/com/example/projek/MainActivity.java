package com.example.projek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.projek.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //Auth
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if(currentUser!=null) {
            if (currentUser.isEmailVerified()) {
                Intent home = new Intent(MainActivity.this, Home.class);
                home.putExtra("email", currentUser.getEmail());
                startActivity(home);

            }
        }

        //Login Button
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signInWithEmailAndPassword(binding.idEmail.getText().toString(),binding.idPassword.getText().toString()).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {

                    //Proses autentikasi
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(user!=null) {
                                if (user.isEmailVerified()) {
                                    Intent home = new Intent(MainActivity.this, Home.class);
                                    startActivity(home);
                                } else {
                                    Toast.makeText(MainActivity.this, "Not verified", Toast.LENGTH_LONG).show();
                                }
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Authentication failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        //Register Button
        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.createUserWithEmailAndPassword(binding.idEmail.getText().toString(), binding.idPassword.getText().toString()).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user!=null) {
                                if (user.isEmailVerified()) {
                                    Intent home = new Intent(MainActivity.this, Home.class);
                                    startActivity(home);
                                } else {
                                    final String email = user.getEmail();
                                    user.sendEmailVerification().addOnCompleteListener(MainActivity.this, task1 -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(MainActivity.this, "Verification email sent to " + email, Toast.LENGTH_LONG).show();
                                        } else {
                                            Log.d("Error di verifikasi", "sendEmailVerification", task.getException());
                                            Toast.makeText(MainActivity.this, "Failed to send verification email.", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Authentication failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }
}