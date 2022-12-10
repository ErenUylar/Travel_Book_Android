package com.erenuylar.example;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.erenuylar.example.databinding.RecyleRowBinding;

import java.util.ArrayList;

public class RecyleAdapter extends RecyclerView.Adapter<RecyleAdapter.recyleHolder> {

    ArrayList<Travel> travelArrayList;

    public RecyleAdapter(ArrayList<Travel> travelArrayList) {
        this.travelArrayList = travelArrayList;
    }

    @NonNull
    @Override
    public recyleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyleRowBinding recyleRowBinding = RecyleRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new recyleHolder(recyleRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull recyleHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.recyleRowBinding.textView.setText(travelArrayList.get(position).name);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.itemView.getContext(), DetailsActivity.class);
                intent.putExtra("info", "old");
                intent.putExtra("id", travelArrayList.get(position).id);
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return travelArrayList.size();
    }

    public class recyleHolder extends RecyclerView.ViewHolder {
        private RecyleRowBinding recyleRowBinding;

        public recyleHolder(RecyleRowBinding recyleRowBinding) {
            super(recyleRowBinding.getRoot());
            this.recyleRowBinding = recyleRowBinding;
        }
    }
}
