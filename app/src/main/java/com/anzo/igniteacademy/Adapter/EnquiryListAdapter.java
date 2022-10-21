package com.anzo.igniteacademy.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anzo.igniteacademy.BaseActivity;
import com.anzo.igniteacademy.Helper.Helper;
import com.anzo.igniteacademy.Interface.RecyclerClickInterface;
import com.anzo.igniteacademy.Model.EnquiryListModel;
import com.anzo.igniteacademy.R;
import com.anzo.igniteacademy.databinding.RecyclerEnquiryListBinding;

import java.util.ArrayList;

public class EnquiryListAdapter extends RecyclerView.Adapter<EnquiryListAdapter.EnquiryHolder> {

    Context context;
    ArrayList<EnquiryListModel> models;
    RecyclerClickInterface clickInterface;
    setOnItemEdit onItemEdit;
    BaseActivity baseActivity;
    setOnDeleteEnquiry onDeleteEnquiry;
    setOnCancelEnquiry onCancelEnquiry;

    public EnquiryListAdapter(BaseActivity baseActivity, Context context, ArrayList<EnquiryListModel> models) {
        this.context = context;
        this.models = models;
        this.baseActivity = baseActivity;
    }

    public void setOnItemEdit(setOnItemEdit onItemEdit) {
        this.onItemEdit = onItemEdit;
    }

    public void setOnDeleteEnquiry(setOnDeleteEnquiry onDeleteEnquiry) {
        this.onDeleteEnquiry = onDeleteEnquiry;
    }

    public void setOnCancelEnquiry(setOnCancelEnquiry onCancelEnquiry) {
        this.onCancelEnquiry = onCancelEnquiry;
    }

    public void setClickInterface(RecyclerClickInterface clickInterface) {
        this.clickInterface = clickInterface;
    }

    @NonNull
    @Override
    public EnquiryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerEnquiryListBinding binding = RecyclerEnquiryListBinding.inflate(LayoutInflater.from(context), parent, false);
        // binding.ivCancel.setVisibility(View.VISIBLE);

        return new EnquiryHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EnquiryHolder holder, int position) {
        EnquiryListModel item = models.get(position);
        holder.binding.tvDate.setText(Helper.changeDateFormat(item.created_at));
        holder.binding.tvStudentName.setText(item.name);
        holder.binding.tvClass.setText(item.course_name);
        holder.binding.tvPhoneNumber.setText(item.phone_number);
        holder.binding.tvStatus.setText(item.enquiry_status);

      //  Log.d("TAG","tv status :"+item.enquiry_status);

        if (item.enquiry_status.equals("enrollment")) {
            // holder.binding.tvStatus.setText("Enrollment");
            holder.binding.cardStatus.setBackgroundResource(R.drawable.enrollment_bg);
            holder.binding.tvStatus.setTextSize(14);
        } else if (item.enquiry_status.equals("live")) {
            //   holder.binding.tvStatus.setText("Live");
            holder.binding.cardStatus.setBackgroundResource(R.drawable.live_enquiry_bg);
            holder.binding.tvStatus.setTextSize(14);
        } else if (item.enquiry_status.equals("cancel")) {
            //   holder.binding.tvStatus.setText("Canceled");
            holder.binding.cardStatus.setBackgroundResource(R.drawable.canceled_enquires_bg);
            holder.binding.tvStatus.setTextSize(14);
        } else if (item.enquiry_status.equals("past enrollment")) {
            // holder.binding.tvStatus.setText("Past Enrollment");
            holder.binding.cardStatus.setBackgroundResource(R.drawable.past_enrollment_bg);
            holder.binding.tvStatus.setTextSize(12);
        } else {
//            holder.binding.cardStatus.setBackgroundResource(R.drawable.btn_background);
            //   holder.binding.cardStatus.setBackground(context.getResources().getDrawable(R.drawable.past_enrollment_bg));
            holder.binding.tvStatus.setTextSize(12);
            if (item.enquiry_status.equals("enrollment") && item.fees_type == 0) {
                holder.binding.ivCancel.setVisibility(View.VISIBLE);
            } else {
                holder.binding.ivCancel.setVisibility(View.INVISIBLE);
            }
        }

//        switch (item.enquiry_status) {
//            case "enrollments":
//                // holder.binding.tvStatus.setText("Enrollment");
//                holder.binding.cardStatus.setBackgroundResource(R.drawable.enrollment_bg);
//                holder.binding.tvStatus.setTextSize(14);
//                break;
//            case "live_enquiries":
//                //   holder.binding.tvStatus.setText("Live");
//                holder.binding.cardStatus.setBackgroundResource(R.drawable.live_enquiry_bg);
//                holder.binding.tvStatus.setTextSize(14);
//                break;
//            case "canceled_enquiries":
//                //   holder.binding.tvStatus.setText("Canceled");
//                holder.binding.cardStatus.setBackgroundResource(R.drawable.canceled_enquires_bg);
//                holder.binding.tvStatus.setTextSize(14);
//                break;
//            case "past_enrollments":
//                // holder.binding.tvStatus.setText("Past Enrollment");
//                holder.binding.cardStatus.setBackgroundResource(R.drawable.past_enrollment_bg);
//                holder.binding.tvStatus.setTextSize(12);
//                break;
//            default:
//                holder.binding.tvStatus.setText(item.enquiry_status);
////            holder.binding.cardStatus.setBackgroundResource(R.drawable.btn_background);
//                //   holder.binding.cardStatus.setBackground(context.getResources().getDrawable(R.drawable.past_enrollment_bg));
//                holder.binding.tvStatus.setTextSize(12);
//                if (item.enquiry_status.equals("enrollment") && item.fees_type == 0) {
//                    holder.binding.ivCancel.setVisibility(View.VISIBLE);
//                } else {
//                    holder.binding.ivCancel.setVisibility(View.INVISIBLE);
//                }
//                break;
//        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickInterface != null)
                    clickInterface.onItemClick(holder.getAdapterPosition());
            }
        });

        holder.binding.ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemEdit != null) {
                    onItemEdit.onItemEdit(holder.getAdapterPosition());
                }
            }
        });

        holder.binding.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDeleteEnquiry != null) {
                    onDeleteEnquiry.onDeleteEnquiry(holder.getAdapterPosition());
                }
            }
        });

        holder.binding.ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCancelEnquiry != null) {
                    onCancelEnquiry.onCancelEnquiry(holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class EnquiryHolder extends RecyclerView.ViewHolder {

        RecyclerEnquiryListBinding binding;

        public EnquiryHolder(@NonNull RecyclerEnquiryListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface setOnItemEdit {
        void onItemEdit(int position);
    }

    public interface setOnDeleteEnquiry {
        void onDeleteEnquiry(int position);
    }

    public interface setOnCancelEnquiry {
        void onCancelEnquiry(int position);
    }
}
