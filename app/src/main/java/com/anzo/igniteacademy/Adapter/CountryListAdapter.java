package com.anzo.igniteacademy.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anzo.igniteacademy.Interface.RecyclerClickInterface;
import com.anzo.igniteacademy.Model.CountryListModel;
import com.anzo.igniteacademy.databinding.RecyclerCountryListBinding;

import java.util.List;

public class CountryListAdapter extends RecyclerView.Adapter<CountryListAdapter.CountryListHolder> {

    Context context;
    List<CountryListModel> models;
    private RecyclerClickInterface recyclerClickInterface;

    public CountryListAdapter(Context context, List<CountryListModel> models) {
        this.context = context;
        this.models = models;
    }

    public void setRecyclerClickInterface(RecyclerClickInterface recyclerClickInterface) {
        this.recyclerClickInterface = recyclerClickInterface;
    }

    @NonNull
    @Override
    public CountryListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerCountryListBinding binding = RecyclerCountryListBinding.inflate(LayoutInflater.from(context), parent, false);
        return new CountryListHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CountryListHolder holder, int position) {
        CountryListModel item = models.get(position);
        holder.binding.tvCountryName.setText(item.countryName);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recyclerClickInterface != null) {
                    recyclerClickInterface.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class CountryListHolder extends RecyclerView.ViewHolder {

        RecyclerCountryListBinding binding;

        public CountryListHolder(@NonNull RecyclerCountryListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
