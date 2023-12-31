package com.example.projek;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projek.databinding.HomeBinding;

import com.google.android.gms.tasks.OnCompleteListener;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Objects;

public class Home extends AppCompatActivity{
    FirebaseAuth fAuth;
    FirebaseUser user;
    HomeBinding binding;
    TextView result;
    String userID;
    RecyclerView recyclerView;
    ArrayList<Task> taskArrayList = new ArrayList<Task>();;
    MyAdapter myAdapter;
    FirebaseFirestore fStore;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = HomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();
        userID = fAuth.getCurrentUser().getUid();

        recyclerView = binding.idRecyclerView;
        recyclerView.setHasFixedSize(true);

        loadData();

        myAdapter = new MyAdapter(Home.this, taskArrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myAdapter);

        result = binding.idUser;

        DocumentReference documentReference = fStore.collection("users").document(userID);

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<DocumentSnapshot> task) {
                DocumentSnapshot data = task.getResult();
                result.setText("Hi, " + data.getString("name"));
                Log.d("Data Nama", "Nama : "+ data.getString("name"));
            }
        });

        //Cek apabila sudah ada yang login atau belum
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }

        //Logout button
        binding.idLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        //New Task Button
        binding.btnNewTask.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NewTask.class));
                finish();
            }
        });
    }

    //Load data dari database
    private void loadData() {
        userID = fAuth.getCurrentUser().getUid();
        fStore.collection("users").document(userID).collection("task")
                .orderBy("title", Query.Direction.ASCENDING).addSnapshotListener(((value, error) -> {
                    if (error != null){
                        Toast.makeText(this, "Chapter Loaded", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    for (int i = 0;  i< Objects.requireNonNull(value).getDocumentChanges().size(); i++){
                        DocumentChange dc = Objects.requireNonNull(value).getDocumentChanges().get(i);
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            taskArrayList.add(dc.getDocument().toObject(Task.class));
                            myAdapter.getItemCount();
                            for (int j = 0; j < taskArrayList.size(); j++) {
                                Log.d("Data", "Data : "  + j + taskArrayList.get(j).getTitle());
                            }
                            Log.d("Fetch", "loadData: " + dc.getDocument());
                            myAdapter.notifyDataSetChanged();
                        }
                    }
                }));
    }
}