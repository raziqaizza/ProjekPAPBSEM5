package com.example.projek;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projek.databinding.HomeBinding;
import com.example.projek.databinding.LayoutItemBinding;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;
    ArrayList<Tugas> tugasArrayList;
    ClickEvent clickEvent;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

    private static ClickListener clickListener;

    public MyAdapter(Context context, ArrayList<Tugas> tugasArrayList) {
        this.context = context;
        this.tugasArrayList = tugasArrayList;
    }

    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.layout_item, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {
        Tugas tugas = tugasArrayList.get(position);
        holder.title.setText(tugas.getTitle());
        Log.d("Onbind" , "data ke : " + position);
        Log.d("OnBind", "title " + tugas.title);

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth = FirebaseAuth.getInstance();
                fStore = FirebaseFirestore.getInstance();
                String userID = fAuth.getCurrentUser().getUid();

                fStore.collection("users").document(userID).collection("task")
                        .document(tugasArrayList.get(holder.getAdapterPosition()).getUid()).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(context.getApplicationContext(), "SLEBEW", Toast.LENGTH_SHORT).show();
                                tugasArrayList.remove(holder.getAdapterPosition());
                                notifyItemRemoved(holder.getAdapterPosition());
                            }
                        });
            }
        });
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth = FirebaseAuth.getInstance();
                fStore = FirebaseFirestore.getInstance();
                String userID = fAuth.getCurrentUser().getUid();
                context.startActivity(new Intent(context, EditTask.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d("ItemCount", "Jumlah data" + tugasArrayList.size());
        return tugasArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        LinearLayout layout;
        LayoutItemBinding binding;
        ImageView delete;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = LayoutItemBinding.bind(itemView);
            layout = binding.contactLayout;
            title = binding.idList;
            delete = binding.delete;
        }
    }

    public void setOnItemClickListener(MyAdapter.ClickListener clickListener) {
        MyAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }
}
