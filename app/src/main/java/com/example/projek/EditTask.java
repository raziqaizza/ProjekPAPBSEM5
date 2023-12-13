package com.example.projek;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.projek.databinding.NewtaskBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EditTask extends AppCompatActivity {
    NewtaskBinding binding;
    FirebaseUser fUser;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID, pUID, pTitle, pDesc;
    MyAdapter myAdapter;
    ArrayList<Task> taskArrayList = new ArrayList<Task>();;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = NewtaskBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();
        setContentView(v);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userID = fAuth.getCurrentUser().getUid();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            pUID = bundle.getString("pUID");
            pTitle = bundle.getString("pTitle");
            pDesc = bundle.getString("pDesc");

            binding.idEditTitle.setText(pTitle);
            binding.idEditDescription.setText(pDesc);
        }


        com.google.android.gms.tasks.Task task = (com.google.android.gms.tasks.Task) getIntent().getSerializableExtra("task");

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

//        if (image != null){
//            data.put("imageURL", image);
//        }

        data.put("uid", pUID);
        data.put("title", title);
        data.put("desc", desc);

        DocumentReference dr = fStore.collection("users").document(userID).collection("task").document(pUID);

        dr.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                        Log.d("task UserID", "title : "+ title);
                        Log.d("ADD DATA", "Document Snapshot written with ID: " + dr.getId());
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
