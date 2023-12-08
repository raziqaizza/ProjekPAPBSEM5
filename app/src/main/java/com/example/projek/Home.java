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

import com.example.projek.Tugas;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Home extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseUser user;
    HomeBinding binding;
    TextView result;
    String userID;
    RecyclerView recyclerView;
    ArrayList<Tugas> tugasArrayList;
    MyAdapter myAdapter;
    FirebaseFirestore fStore;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = HomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();
        userID = auth.getCurrentUser().getUid();

        tugasArrayList = new ArrayList<Tugas>();

        recyclerView = binding.recyclerView;
        recyclerView.setHasFixedSize(true);

        loadData();

        myAdapter = new MyAdapter(Home.this, tugasArrayList);

        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        result = binding.user;

        DocumentReference documentReference = fStore.collection("users").document(userID);

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot data = task.getResult();
                result.setText("Hi, " + data.getString("name"));
                Log.d("Data Nama", "Nama : "+ data.getString("name"));
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

        binding.btnNewtask.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NewTask.class));
                finish();
            }
        });
    }

    private void loadData() {
        userID = auth.getCurrentUser().getUid();


        fStore.collection("users").document(userID).collection("task")
                .orderBy("title", Query.Direction.ASCENDING).addSnapshotListener(((value, error) -> {
                    if (error != null){
                        Toast.makeText(this, "Chapter Loaded", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    for (int i = 0;  i< Objects.requireNonNull(value).getDocumentChanges().size(); i++){
                        DocumentChange dc = Objects.requireNonNull(value).getDocumentChanges().get(i);
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            tugasArrayList.add(dc.getDocument().toObject(Tugas.class));
                            myAdapter.getItemCount();
                            for (int j = 0; j < tugasArrayList.size(); j++) {
                                Log.d("Data", "Data : "  + j + tugasArrayList.get(j).getTitle());
                            }
                            Log.d("Fetch", "loadData: " + dc.getDocument());
                            myAdapter.notifyDataSetChanged();
                        }
                    }
                }));
    }


}
