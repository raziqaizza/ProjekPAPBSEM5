package com.example.projek;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.projek.databinding.NewtaskBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NewTask extends AppCompatActivity {
    NewtaskBinding binding;
    FirebaseUser fUser;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID, taskID;
    DocumentSnapshot ds;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = NewtaskBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();
        setContentView(v);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userID = fAuth.getCurrentUser().getUid();

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Home.class));
                finish();
            }
        });

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputData = binding.idEditDescription.getText().toString();
                String inputTitle = binding.idEditTitle.getText().toString();

                saveData(inputTitle, inputData);
                startActivity(new Intent(getApplicationContext(), Home.class));
                finish();
            }
        });
    }

    //Function untuk save data
    private void saveData(String title, String desc){
        Map<String, Object> data = new HashMap<>();
        String uid = UUID.randomUUID().toString();

        data.put("uid", uid);
        data.put("title", title);
        data.put("desc", desc);

        CollectionReference collectionReference = fStore.collection("users").document(userID).collection("task");
        collectionReference.document(uid).set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("task UserID", "title : "+ title);
                        Log.d("ADD DATA", "Document Snapshot written with ID: " + collectionReference.document(uid).getId());
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("err add data", "Error adding document", e);
                    }
                });
    }
}
