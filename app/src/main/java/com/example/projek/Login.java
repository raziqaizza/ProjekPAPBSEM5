package com.example.projek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.projek.databinding.LoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    private FirebaseAuth mAuth;
    LoginBinding binding;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent home = new Intent(Login.this, Home.class);
            home.putExtra("email",binding.idEmail.getText().toString());
            startActivity(home);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        binding = LoginBinding.inflate(getLayoutInflater());
        mAuth = FirebaseAuth.getInstance();
        View view = binding.getRoot();
        setContentView(view);


        //Login Button
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email, password;
                email = binding.idEmail.getText().toString();
                password = binding.idPassword.getText().toString();
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(Login.this, "Email dan Password tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Toast.makeText(Login.this, "Login successful.",
                                            Toast.LENGTH_SHORT).show();
                                    Intent home = new Intent(Login.this, Home.class);
                                    home.putExtra("email",binding.idEmail.getText().toString());
                                    startActivity(home);
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(Login.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            }
        });

        //Register Button
        binding.btnCreateAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent register = new Intent(Login.this, Register.class);
                startActivity(register);
            }
        });
    }
}