package com.anzo.igniteacademy.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anzo.igniteacademy.Helper.Helper;
import com.anzo.igniteacademy.Interface.RecyclerClickInterface;
import com.anzo.igniteacademy.Model.FollowUpListModel;
import com.anzo.igniteacademy.databinding.RecyclerFollowUpListBinding;

import java.util.ArrayList;

public class FollowUpListAdapter extends RecyclerView.Adapter<FollowUpListAdapter.EnquiryHolder> {

    Context context;
    ArrayList<FollowUpListModel> models;
    RecyclerClickInterface clickInterface;
    setOnEditFollowUp onEditFollowUp;

    public FollowUpListAdapter(Context context, ArrayList<FollowUpListModel> models) {
        this.context = context;
        this.models = models;
    }

    public void setClickInterface(RecyclerClickInterface clickInterface) {
        this.clickInterface = clickInterface;
    }

    public void setOnEditFollowUp(setOnEditFollowUp onEditFollowUp) {
        this.onEditFollowUp = onEditFollowUp;
    }

    @NonNull
    @Override
    public EnquiryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerFollowUpListBinding binding = RecyclerFollowUpListBinding.inflate(LayoutInflater.from(context), parent, false);
        return new EnquiryHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EnquiryHolder holder, int position) {
        FollowUpListModel item = models.get(position);
        holder.binding.txtFollowUpDate.setText(Helper.changeDateFormat(item.created_at));
        holder.binding.txtFollowUpRemark.setText(item.remarks.trim());
        holder.binding.txtFollowUpBy.setText("Follow up by " + item.follow_up_by);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickInterface != null)
                    clickInterface.onItemClick(holder.getAdapterPosition());
            }
        });
        holder.binding.ivEditFollowUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onEditFollowUp != null)
                    onEditFollowUp.onEditFollowUp(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class EnquiryHolder extends RecyclerView.ViewHolder {

        RecyclerFollowUpListBinding binding;

        public EnquiryHolder(@NonNull RecyclerFollowUpListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface setOnEditFollowUp {
        void onEditFollowUp(int position);
    }
}
