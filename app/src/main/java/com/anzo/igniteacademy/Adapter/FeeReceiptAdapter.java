package com.anzo.igniteacademy.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anzo.igniteacademy.Interface.RecyclerClickInterface;
import com.anzo.igniteacademy.Model.FeeReceiptModel;
import com.anzo.igniteacademy.databinding.RecyclerFeeReceiptListBinding;

import java.util.ArrayList;

public class FeeReceiptAdapter extends RecyclerView.Adapter<FeeReceiptAdapter.EnquiryHolder> {
    Context context;
    ArrayList<FeeReceiptModel> models;
    RecyclerClickInterface clickInterface;
    setOnEditFee onEditFee;

    public FeeReceiptAdapter(Context context, ArrayList<FeeReceiptModel> models) {
        this.context = context;
        this.models = models;
    }

    public void setClickInterface(RecyclerClickInterface clickInterface) {
        this.clickInterface = clickInterface;
    }

    public void setOnEditFee(setOnEditFee onEditFee) {
        this.onEditFee = onEditFee;
    }

    @NonNull
    @Override
    public EnquiryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerFeeReceiptListBinding binding = RecyclerFeeReceiptListBinding.inflate(LayoutInflater.from(context), parent, false);
        return new EnquiryHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EnquiryHolder holder, int position) {
        FeeReceiptModel items = models.get(position);
        holder.binding.txtDate.setText(items.created_at.trim());
      //  holder.binding.ivImagePdf.setImageURI(Uri.parse(items.receipt_url.trim()));
        holder.binding.txtCreatedBy.setText("Created by " + items.created_by.trim());
        holder.binding.txtMoney.setText(items.amount.trim());

//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setData(Uri.parse(items.receipt_url));
//        start(intent);

        holder.binding.ivImagePdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //   Log.d("TAG", "onClick: URL : "+items.receipt_url);
//                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(items.receipt_url.trim()));
//                context.startActivity(browserIntent);
//
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setDataAndType(Uri.parse( "http://docs.google.com/viewer?url=" + items.receipt_url.trim()), "text/html");
//                context.startActivity(intent);

               context.startActivity(new Intent(Intent.ACTION_VIEW).setDataAndType(Uri.parse(items.receipt_url.trim()), "application/pdf"));


//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse(items.receipt_url));
//                context.startActivity(intent);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickInterface != null)
                    clickInterface.onItemClick(holder.getAdapterPosition());
            }
        });

        holder.binding.ivEditFee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onEditFee != null) {
                    onEditFee.onEditFee(holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class EnquiryHolder extends RecyclerView.ViewHolder {
        RecyclerFeeReceiptListBinding binding;

        public EnquiryHolder(@NonNull RecyclerFeeReceiptListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface setOnEditFee {
        void onEditFee(int position);
    }

}
