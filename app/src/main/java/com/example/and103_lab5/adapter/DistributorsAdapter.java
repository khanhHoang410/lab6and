package com.example.and103_lab5.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.and103_lab5.R;
import com.example.and103_lab5.models.Distributor;

import java.util.List;

public class DistributorsAdapter extends RecyclerView.Adapter<DistributorsAdapter.DistributorViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(String id);
        void updateItem(String id, String name);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.click = listener;
    }

    private OnItemClickListener click;

    List<Distributor> list;

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<Distributor> ls) {
        this.list = ls;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public DistributorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.distributor_item, parent, false);
        return new DistributorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DistributorViewHolder holder, int position) {
        Distributor dis = list.get(position);
        if (dis == null) {
            return;
        }
        holder.tvID.setText(String.valueOf(position + 1));
        holder.tvName.setText(dis.getName());
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String id = list.get(holder.getAdapterPosition()).getId();
                String name = list.get(holder.getAdapterPosition()).getName();
                click.updateItem(id, name);

            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = list.get(holder.getAdapterPosition()).getId();
                click.onItemClick(id);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    public static class DistributorViewHolder extends RecyclerView.ViewHolder {
        TextView tvID, tvName;
        ImageButton btnEdit, btnDelete;

        public DistributorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvID = itemView.findViewById(R.id.tvID);
            tvName = itemView.findViewById(R.id.tvName);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
