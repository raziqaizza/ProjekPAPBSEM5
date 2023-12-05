package com.example.projek;


import android.app.ProgressDialog;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.projek.databinding.TasklistBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;

public class Home extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseUser user;
    TasklistBinding binding;
    TextView result;
    String userID;
    RecyclerView recyclerView;
    ArrayList<com.example.projek.Task> taskArrayList;
    MyAdapter myAdapter;
    FirebaseFirestore fStore;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = TasklistBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());

        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();
        userID = auth.getCurrentUser().getUid();

        taskArrayList = new ArrayList<com.example.projek.Task>();
        myAdapter = new MyAdapter(Home.this, taskArrayList);

        EventChangeListener();

        recyclerView = binding.recyclerView;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myAdapter);

        result = binding.user;

        DocumentReference documentReference = fStore.collection("users").document(userID);

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot data = task.getResult();
                result.setText("Hi, " + data.getString("name"));
            }
        });

        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }
//        Intent dataIntent = getIntent();
//        String data = dataIntent.getStringExtra("email");
//        result = binding.user;
//        result.setText(data);

        binding.idLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void EventChangeListener() {
        userID = auth.getCurrentUser().getUid();
        fStore.collection("users").document(userID).collection("task").orderBy("title", Query.Direction.ASCENDING).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            taskArrayList.add(document.toObject(com.example.projek.Task.class));
                        }
                        myAdapter.notifyDataSetChanged();
                    } else{
                        Log.e("Firestore Error", "Error getting documents", task.getException());
                    }
                });
    }


}
