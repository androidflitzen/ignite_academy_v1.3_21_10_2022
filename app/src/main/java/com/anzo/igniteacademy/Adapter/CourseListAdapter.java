package com.anzo.igniteacademy.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anzo.igniteacademy.Interface.RecyclerClickInterface;
import com.anzo.igniteacademy.Model.CourseListModel;
import com.anzo.igniteacademy.R;
import com.anzo.igniteacademy.databinding.RecyclerCourseListBinding;

import java.util.List;

public class CourseListAdapter extends RecyclerView.Adapter<CourseListAdapter.CourseHolder> {

    public int selectedPosition = -1;
    Context context;
    List<CourseListModel> models;
    private RecyclerClickInterface recyclerClickInterface;

    public CourseListAdapter(Context context, List<CourseListModel> models) {
        this.context = context;
        this.models = models;
    }

    public void setRecyclerClickInterface(RecyclerClickInterface recyclerClickInterface) {
        this.recyclerClickInterface = recyclerClickInterface;
    }

    public void setSelectedPosition(int position) {
        this.selectedPosition = position;
    }

    @NonNull
    @Override
    public CourseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerCourseListBinding binding = RecyclerCourseListBinding.inflate(LayoutInflater.from(context), parent, false);
        return new CourseHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseHolder holder, @SuppressLint("RecyclerView") int position) {
        CourseListModel item = models.get(position);
        holder.binding.tvCourseName.setText(item.courseName);

        if (position == selectedPosition) {
            holder.binding.card.setCardBackgroundColor(context.getResources().getColor(R.color.color_primary));
            holder.binding.tvCourseName.setTextColor(Color.WHITE);
        } else {
            holder.binding.card.setCardBackgroundColor(Color.WHITE);
            holder.binding.tvCourseName.setTextColor(context.getResources().getColor(R.color.fontColor1));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPosition == holder.getAdapterPosition())
                    return;

                int oldPosition = selectedPosition;
                selectedPosition = holder.getAdapterPosition();

                if (recyclerClickInterface != null) recyclerClickInterface.onItemClick(position);

                notifyItemChanged(oldPosition);
                notifyItemChanged(selectedPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class CourseHolder extends RecyclerView.ViewHolder {
        RecyclerCourseListBinding binding;

        public CourseHolder(@NonNull RecyclerCourseListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
