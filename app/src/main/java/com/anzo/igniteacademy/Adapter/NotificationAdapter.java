package com.anzo.igniteacademy.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anzo.igniteacademy.Helper.Helper;
import com.anzo.igniteacademy.Interface.RecyclerClickInterface;
import com.anzo.igniteacademy.Model.NotificationModel;
import com.anzo.igniteacademy.databinding.RecyclerNotificationBinding;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationHolder> {

    Context context;
    List<NotificationModel> models;
    RecyclerClickInterface recyclerClickInterface;

    public NotificationAdapter(Context context, List<NotificationModel> models) {
        this.context = context;
        this.models = models;
    }

    public void setRecyclerClickInterface(RecyclerClickInterface recyclerClickInterface) {
        this.recyclerClickInterface = recyclerClickInterface;
    }

    @NonNull
    @Override
    public NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerNotificationBinding binding = RecyclerNotificationBinding.inflate(LayoutInflater.from(context), parent, false);
        return new NotificationHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationHolder holder, int position) {
        NotificationModel item = models.get(position);
        holder.binding.tvNotificationText.setText(item.notificationMessage);
        holder.binding.tvNotificationDate.setText(Helper.isNullOrEmpty(item.createdAt) ? "" : Helper.changeDateFormat(item.createdAt, Helper.defaultDateFormat, "dd MMM yyyy hh:mm"));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.binding.ivViewEnquiry.isShown()) {
                    if (recyclerClickInterface != null)
                        recyclerClickInterface.onItemClick(holder.getAdapterPosition());
                } else {
                    Toast.makeText(context, "Enquiry has been deleted", Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (item.isNotificationDeleted.equals("no")) {
            holder.binding.ivViewEnquiry.setVisibility(View.VISIBLE);
        } else {
            holder.binding.ivViewEnquiry.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class NotificationHolder extends RecyclerView.ViewHolder {

        RecyclerNotificationBinding binding;

        public NotificationHolder(@NonNull RecyclerNotificationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
