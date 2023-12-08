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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class NewTask extends AppCompatActivity {
    NewtaskBinding binding;
    FirebaseUser fUser;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID, taskID;

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
                String inputData = binding.list.getText().toString();
                String inputTitle = binding.editTitle.getText().toString();

                saveData(inputTitle, inputData);
                startActivity(new Intent(getApplicationContext(), Home.class));
                finish();
            }
        });
    }
    private void saveData(String title, String taskData){
        Map<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("taskData", taskData);

        CollectionReference collectionReference = fStore.collection("user").document(userID).collection("task");

        fStore.collection("users").document(userID).collection("task")
                .add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("task UserID", "title : "+ title);
                        Log.d("ADD DATA", "Document Snapshot written with ID: " + documentReference.getId());
                        Log.d("ADD DATA", "Document Snapshot written with Parent: " + documentReference.getParent());
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
