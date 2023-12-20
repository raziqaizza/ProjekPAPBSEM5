package com.example.projek;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projek.databinding.RegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    public static final String TAG = "TAG";
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    RegisterBinding binding;
    String userID;
    FirebaseFirestore fStore;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent home = new Intent(Register.this, Home.class);
            home.putExtra("email",binding.idEmail.getText().toString());
            startActivity(home);
            finish();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = RegisterBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //Auth
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, password, name;
                email = binding.idEmail.getText().toString();
                password = binding.idPassword.getText().toString();
                name = binding.idNama.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(Register.this, "Email dan Password tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Toast.makeText(Register.this, "Account created.",
                                            Toast.LENGTH_SHORT).show();
                                    userID = mAuth.getUid();
                                    DocumentReference documentReference = fStore.collection("users").document(userID);
                                    Map<String,Object> user = new HashMap<>();
                                    user.put("name", name);
                                    user.put("email", email);
                                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d(TAG, "onSuccess: user Profile is created for " + userID);
                                        }
                                    });
                                } else {
                                    // If sign in fails, display a message to the user.

                                    Toast.makeText(Register.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login = new Intent(Register.this, Login.class);
                startActivity(login);
            }
        });
    }
}