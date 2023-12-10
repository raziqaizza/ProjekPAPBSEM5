package com.example.projek;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projek.databinding.HomeBinding;
import com.example.projek.databinding.LayoutItemBinding;


import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;
    ArrayList<Tugas> tugasArrayList;

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
    }

    @Override
    public int getItemCount() {
        Log.d("ItemCount", "Jumlah data" + tugasArrayList.size());
        return tugasArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        LayoutItemBinding binding;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = LayoutItemBinding.bind(itemView);
            title = binding.idList;
        }
    }
}
