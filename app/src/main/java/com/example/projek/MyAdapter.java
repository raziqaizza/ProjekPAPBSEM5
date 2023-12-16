package com.example.projek;

import android.annotation.SuppressLint;
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

import com.example.projek.databinding.LayoutItemBinding;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;
    ArrayList<Task> taskArrayList;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

    public MyAdapter(Context context, ArrayList<Task> taskArrayList) {
        this.context = context;
        this.taskArrayList = taskArrayList;
    }

    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.layout_item, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Task task = taskArrayList.get(position);
        holder.title.setText(task.getTitle());


        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth = FirebaseAuth.getInstance();
                fStore = FirebaseFirestore.getInstance();
                String userID = fAuth.getCurrentUser().getUid();

                fStore.collection("users").document(userID).collection("task")
                        .document(taskArrayList.get(holder.getAdapterPosition()).getUid()).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(context.getApplicationContext(), "Data deleted", Toast.LENGTH_SHORT).show();
                                taskArrayList.remove(holder.getAdapterPosition());
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

                String uid, title, desc;
                uid = taskArrayList.get(position).getUid();
                title = taskArrayList.get(position).getTitle();
                desc = taskArrayList.get(position).getDesc();

                Intent intent = new Intent(context, EditTask.class);

                intent.putExtra("pUID", uid);
                intent.putExtra("pTitle", title);
                intent.putExtra("pDesc", desc);

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d("ItemCount", "Jumlah data" + taskArrayList.size());
        return taskArrayList.size();
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
}
