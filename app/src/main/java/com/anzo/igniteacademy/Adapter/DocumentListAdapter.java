package com.anzo.igniteacademy.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anzo.igniteacademy.Interface.RecyclerClickInterface;
import com.anzo.igniteacademy.Model.DocumentListModel;
import com.anzo.igniteacademy.R;
import com.anzo.igniteacademy.databinding.RecyclerDocumentBinding;
import com.bumptech.glide.Glide;

import java.util.List;

public class DocumentListAdapter extends RecyclerView.Adapter<DocumentListAdapter.DocumentListHolder> {

    Context context;
    List<DocumentListModel> models;
    RecyclerClickInterface recyclerClickInterface;

    public DocumentListAdapter(Context context, List<DocumentListModel> models) {
        this.context = context;
        this.models = models;
    }

    public void setOnDeleteDocument(RecyclerClickInterface recyclerClickInterface) {
        this.recyclerClickInterface = recyclerClickInterface;
    }


    @NonNull
    @Override
    public DocumentListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerDocumentBinding binding = RecyclerDocumentBinding.inflate(LayoutInflater.from(context), parent, false);
        return new DocumentListHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentListHolder holder, int position) {
        DocumentListModel item = models.get(position);
        if (item.document.endsWith(".pdf")) {
            Glide.with(holder.itemView)
                    .load(context.getResources().getDrawable(R.drawable.ic_pdf_2))
                    .into(holder.binding.ivDocument);
        } else if(item.document.endsWith(".jpg") || item.document.endsWith(".png")) {
            Glide.with(holder.itemView)
                    .load(item.document)
                    .into(holder.binding.ivDocument);
        }else {
            Glide.with(holder.itemView)
                    .load(context.getResources().getDrawable(R.drawable.ic_docx))
                    .into(holder.binding.ivDocument);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recyclerClickInterface != null) {
                    recyclerClickInterface.onItemClick(holder.getAdapterPosition());
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class DocumentListHolder extends RecyclerView.ViewHolder {
        RecyclerDocumentBinding binding;

        public DocumentListHolder(@NonNull RecyclerDocumentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
