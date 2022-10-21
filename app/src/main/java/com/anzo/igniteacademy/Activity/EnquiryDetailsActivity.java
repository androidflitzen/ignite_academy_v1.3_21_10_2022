package com.anzo.igniteacademy.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.anzo.igniteacademy.Adapter.CourseListAdapter;
import com.anzo.igniteacademy.Adapter.FeeReceiptAdapter;
import com.anzo.igniteacademy.Adapter.FollowUpListAdapter;
import com.anzo.igniteacademy.Api.WebService;
import com.anzo.igniteacademy.BaseActivity;
import com.anzo.igniteacademy.Helper.Helper;
import com.anzo.igniteacademy.Helper.IntentHelper;
import com.anzo.igniteacademy.Interface.RecyclerClickInterface;
import com.anzo.igniteacademy.Model.CourseListModel;
import com.anzo.igniteacademy.Model.EnquiryListModel;
import com.anzo.igniteacademy.Model.FeeReceiptModel;
import com.anzo.igniteacademy.Model.FollowUpListModel;
import com.anzo.igniteacademy.R;
import com.anzo.igniteacademy.databinding.ActivityEnquiryDetailsBinding;
import com.anzo.igniteacademy.databinding.DialogAddEditFollowupBinding;
import com.anzo.igniteacademy.databinding.DialogAddEnrollmentBinding;
import com.anzo.igniteacademy.databinding.DialogFeePaymentBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnquiryDetailsActivity extends BaseActivity {

    private ActivityEnquiryDetailsBinding binding;

    private int enquiryId = 0;

    private ArrayList<FollowUpListModel> arrayList = new ArrayList<>();

    private ArrayList<FeeReceiptModel> arrayListFee = new ArrayList<>();


    private FollowUpListAdapter mAdapterFollowUp;

    public String FeePlus;
    private FeeReceiptAdapter mAdapterFeeReceipt;

    private ArrayList<String> arrayListBatchTime = new ArrayList<>();
    private EnquiryListModel enquiryDetails;

    CourseListAdapter courseListAdapter;
    List<CourseListModel> courseListModels = new ArrayList<>();
    public static String COURSE_ID = "", COURSE_FEE = "";
    public static int SELECTED_COURSE_ID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEnquiryDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        enquiryId = getIntent().getIntExtra("ID", 0);

        binding.recyclerviewFollowups.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerviewFollowups.setNestedScrollingEnabled(false);
        mAdapterFollowUp = new FollowUpListAdapter(this, arrayList);
        binding.recyclerviewFollowups.setAdapter(mAdapterFollowUp);
        mAdapterFollowUp.setOnEditFollowUp(new FollowUpListAdapter.setOnEditFollowUp() {
            @Override
            public void onEditFollowUp(int position) {
                addFollowUp(true, position);
            }
        });

        binding.rvPdf.setLayoutManager(new LinearLayoutManager(this));
        binding.rvPdf.setNestedScrollingEnabled(false);
        mAdapterFeeReceipt = new FeeReceiptAdapter(this, arrayListFee);
        binding.rvPdf.setAdapter(mAdapterFeeReceipt);
        mAdapterFeeReceipt.setOnEditFee(new FeeReceiptAdapter.setOnEditFee() {
            @Override
            public void onEditFee(int position) {
                showFeeDialog(true, position);
            }
        });

        //  Toast.makeText(EnquiryDetailsActivity.this, String.valueOf(enquiryId), Toast.LENGTH_SHORT).show();
        getBatchTime();
        getEnquiryDetails();
        getFeeDetails();

        binding.btnAddNewFollowUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFollowUp(false, 0);
            }
        });

        mAdapterFollowUp.setClickInterface(new RecyclerClickInterface() {
            @Override
            public void onItemClick(int position) {
                new AlertDialog.Builder(EnquiryDetailsActivity.this)
                        .setTitle("Confirmation")
                        .setMessage("Are you sure you want to delete follow up")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteFollowUp(position);
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
                /*String[] options = new String[]{"Edit follow up", "Delete follow up"};
                new AlertDialog.Builder(EnquiryDetailsActivity.this)
                        .setTitle("Select Option")
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 0) {
                                    //addFollowUp(true, position);
                                } else if (i == 1) {
                                    new AlertDialog.Builder(EnquiryDetailsActivity.this)
                                            .setTitle("Confirmation")
                                            .setMessage("Are you sure you want to delete this follow up?")
                                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    deleteFollowUp(position);
                                                }
                                            }).setNegativeButton("Cancel", null).show();
                                }
                            }
                        }).show();*/
            }
        });
        binding.btnEnroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEnrollment();
            }
        });

        binding.btnAddFee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFeeDialog(false, 0);
            }
        });

        binding.btnEnrollDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EnquiryDetailsActivity.this, EnrollmentDetailActivity.class);
                intent.putExtra("EnquiryId", enquiryId);
                startActivity(intent);
            }
        });
        binding.btnEditEnquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EnquiryDetailsActivity.this, MainActivity.class);
                intent.putExtra("enquiryId", enquiryDetails.id);
                intent.putExtra(IntentHelper.EDIT_ENQUIRY_FROM_ENQUIRY_DETAILS, true);
                startActivity(intent);
                finish();
            }
        });
    }

//    private void showFeeDialog(int adapterPosition) {
//        showFeeDialog();
//    }

    public void getEnquiryDetails() {
        Map<String, String> reqParams = new HashMap<>();
        reqParams.put("api_key", WebService.API_KEY);
        reqParams.put("user_id", userPreference.getUserId());
        reqParams.put("enquiry_id", String.valueOf(enquiryId));

        requestApi(reqParams, Request.Method.POST, WebService.ENQUIRY_DETAILS, new BaseActivity.RequestApiInterface() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt(WebService.Params.STATUS) == WebService.SUCCESS) {
                        enquiryDetails = new Gson()
                                .fromJson(jsonObject.getJSONArray(WebService.Params.DATA).getJSONObject(0).toString(), EnquiryListModel.class);

                        SELECTED_COURSE_ID = enquiryDetails.course_id - 1;
                        binding.tvStudentName.setText(enquiryDetails.name);
                        binding.tvPhoneNumber.setText(enquiryDetails.phone_number);
                        binding.tvAddress.setText(enquiryDetails.address);
                        //  binding.tvDob.setText(Helper.isNullOrEmpty(enquiryDetails.birth_date) ? "" : Helper.changeDateFormat(enquiryDetails.birth_date, Helper.defaultDateFormat, "dd MMM yyyy"));
                        binding.tvGender.setText(enquiryDetails.gender);
                        binding.tvCourse.setText(enquiryDetails.course_name);
                        binding.tvCountry.setText(enquiryDetails.country);

                        if (!binding.tvDob.equals("1970-01-01")) {
                            binding.tvDob.setText(enquiryDetails.birth_date);
                        } else {
                            binding.tvDob.setText("-");
                        }

                        if (enquiryDetails.status == 1) {
                            binding.btnAddFee.setVisibility(View.GONE);
                            binding.btnEnroll.setVisibility(View.VISIBLE);
                            binding.btnEnrollDetail.setVisibility(View.GONE);
                        } else if (enquiryDetails.status == 2) {

                            if (enquiryDetails.course_fees == enquiryDetails.total_paid_fees) {
                                binding.btnEnroll.setVisibility(View.GONE);
                                binding.btnEnrollDetail.setVisibility(View.VISIBLE);
                                binding.btnAddFee.setVisibility(View.GONE);
                            } else {
                                binding.btnEnroll.setVisibility(View.GONE);
                                binding.btnEnrollDetail.setVisibility(View.VISIBLE);
                                binding.btnAddFee.setVisibility(View.VISIBLE);
                            }
                        }

                        getFollowUpList();

                    } else {
                        Toast.makeText(EnquiryDetailsActivity.this, jsonObject.getString(WebService.Params.MESSAGE), Toast.LENGTH_SHORT).show();
                        binding.btnEditEnquiry.setVisibility(View.INVISIBLE);
                        binding.btnEnroll.setVisibility(View.INVISIBLE);
                        binding.btnAddNewFollowUp.setVisibility(View.INVISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error, int errorCode) {

            }
        }, null);
    }

    public void getFollowUpList() {
        Map<String, String> reqParams = new HashMap<>();
        reqParams.put("api_key", WebService.API_KEY);
        reqParams.put("user_id", userPreference.getUserId());
        reqParams.put("enquiry_id", String.valueOf(enquiryId));

        requestApi(reqParams, Request.Method.POST, WebService.FOLLOW_UP_LIST, new BaseActivity.RequestApiInterface() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt(WebService.Params.STATUS) == WebService.SUCCESS) {
                        Type listType = new TypeToken<List<FollowUpListModel>>() {
                        }.getType();

                        arrayList.clear();
                        arrayList.addAll(new Gson().fromJson(jsonObject.getJSONArray(WebService.Params.DATA).toString(), listType));
                        mAdapterFollowUp.notifyDataSetChanged();

                        if (arrayList.size() == 0) {
                            binding.viewEmptyState.setVisibility(View.VISIBLE);
                        } else {
                            binding.viewEmptyState.setVisibility(View.GONE);
                        }
                    } else {
                        binding.viewEmptyState.setVisibility(View.VISIBLE);
                        //Toast.makeText(EnquiryDetailsActivity.this, jsonObject.getString(WebService.Params.MESSAGE), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error, int errorCode) {

            }
        }, null);
    }

    private void showFeeDialog(boolean isEdit, int position) {
        LayoutInflater localView = LayoutInflater.from(this);
        DialogFeePaymentBinding dialogBinding = DialogFeePaymentBinding.inflate(localView);


        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setView(dialogBinding.getRoot());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);

        if (isEdit) {
            dialogBinding.txtFeePaymentTitle.setText("Update Fee");
            dialogBinding.edtFee.setText(arrayListFee.get(position).amount);
            // dialogBinding.btnAdd.setText("Update follow up");
            // dialogBinding.edtRemark.setText(arrayList.get(position).remarks);

        }
        int pendingFee = enquiryDetails.course_fees - enquiryDetails.total_paid_fees;

        dialogBinding.tvTotalFeeAmount.setText("Total Fee Amount : " + pendingFee);

        dialogBinding.btnPayFee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogBinding.edtFee.getText().toString().isEmpty()) {
                    dialogBinding.edtFee.setError("Enter Fee Amount");
                    return;
                }
                FeePlus = dialogBinding.edtFee.getText().toString();
                ProgressDialog progressDialog = new ProgressDialog(EnquiryDetailsActivity.this);
                progressDialog.setMessage("Please wait...");
                progressDialog.show();

                Map<String, String> reqParams = new HashMap<>();
                reqParams.put("api_key", WebService.API_KEY);
                reqParams.put("enquiry_id", String.valueOf(enquiryId));
                reqParams.put("user_id", userPreference.getUserId());
                reqParams.put("pay_amount", dialogBinding.edtFee.getText().toString());
                if (isEdit) {
//                     reqParams.put("fees_payment_id", arrayListFee.get(position).id);
                    reqParams.put("receipt_id", String.valueOf(arrayListFee.get(position).id));
                }

                requestApi(reqParams, Request.Method.POST, WebService.FEES_PAYMENT, new RequestApiInterface() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.cancel();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getInt(WebService.Params.STATUS) == WebService.SUCCESS) {
                                alertDialog.dismiss();
                                getFeeDetails();
                                getEnquiryDetails();

                                Toast.makeText(EnquiryDetailsActivity.this, jsonObject.getString(WebService.Params.MESSAGE), Toast.LENGTH_SHORT).show();
                            } else {
                                progressDialog.cancel();
                                Toast.makeText(EnquiryDetailsActivity.this, jsonObject.getString(WebService.Params.MESSAGE), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(VolleyError error, int errorCode) {
                        progressDialog.cancel();
                    }
                }, null);
            }
        });
        dialogBinding.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void getFeeDetails() {

        Map<String, String> reqParams = new HashMap<>();
        reqParams.put("api_key", WebService.API_KEY);
        reqParams.put("user_id", userPreference.getUserId());
        reqParams.put("enquiry_id", String.valueOf(enquiryId));
        // reqParams.put("pay_amount", feePlus);

        Log.d("TAG", "api key : " + WebService.API_KEY);
        Log.d("TAG", "uer id : " + userPreference.getUserId());
        Log.d("TAG", "enquiry id : " + String.valueOf(enquiryId));

        requestApi(reqParams, Request.Method.POST, WebService.Fee_Receipt, new BaseActivity.RequestApiInterface() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt(WebService.Params.STATUS) == WebService.SUCCESS) {
                        Type listType = new TypeToken<List<FeeReceiptModel>>() {
                        }.getType();

                        arrayListFee.clear();
                        arrayListFee.addAll(new Gson().fromJson(jsonObject.getJSONArray(WebService.Params.DATA).toString(), listType));
                        mAdapterFeeReceipt.notifyDataSetChanged();

                        if (arrayListFee.size() == 0) {
                            binding.viewEmptyStates.setVisibility(View.VISIBLE);
                        } else {
                            binding.viewEmptyStates.setVisibility(View.GONE);
                        }
                    } else {
                        binding.viewEmptyStates.setVisibility(View.VISIBLE);
                        //  Toast.makeText(EnquiryDetailsActivity.this, jsonObject.getString(WebService.Params.MESSAGE), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error, int errorCode) {

            }
        }, null);
    }

    public void addFollowUp(boolean isEdit, int position) {
        LayoutInflater localView = LayoutInflater.from(this);
        DialogAddEditFollowupBinding dialogBinding = DialogAddEditFollowupBinding.inflate(localView);

        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setView(dialogBinding.getRoot());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);

        if (isEdit) {
            dialogBinding.txtFollowUpTitle.setText("Edit Follow Up");
            dialogBinding.btnAdd.setText("Update follow up");
            dialogBinding.edtRemark.setText(arrayList.get(position).remarks);
        }

        dialogBinding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialogBinding.edtRemark.getText().toString().trim().isEmpty()) {
                    dialogBinding.edtRemark.setError("Please enter follow up remarks");
                    return;
                }

                ProgressDialog progressDialog = new ProgressDialog(EnquiryDetailsActivity.this);
                progressDialog.setMessage("Please wait...");
                progressDialog.show();

                Map<String, String> reqParams = new HashMap<>();
                reqParams.put("api_key", WebService.API_KEY);
                reqParams.put("user_id", userPreference.getUserId());
                reqParams.put("enquiry_id", String.valueOf(enquiryId));
                reqParams.put("remarks", dialogBinding.edtRemark.getText().toString().trim());
                if (isEdit)
                    reqParams.put("follow_up_id", String.valueOf(arrayList.get(position).follow_up_id));

                requestApi(reqParams, Request.Method.POST, WebService.ADD_FOLLOW_UP, new RequestApiInterface() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.hide();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getInt(WebService.Params.STATUS) == WebService.SUCCESS) {
                                alertDialog.dismiss();
                                getFollowUpList();
                            } else {
                                Toast.makeText(EnquiryDetailsActivity.this, jsonObject.getString(WebService.Params.MESSAGE), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(VolleyError error, int errorCode) {
                        progressDialog.hide();
                        Toast.makeText(EnquiryDetailsActivity.this, "Failed to add follow up", Toast.LENGTH_SHORT).show();
                    }
                }, null);
            }
        });

        dialogBinding.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    public void deleteFollowUp(int position) {
        ProgressDialog progressDialog = new ProgressDialog(EnquiryDetailsActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        Map<String, String> reqParams = new HashMap<>();
        reqParams.put("api_key", WebService.API_KEY);
        reqParams.put("user_id", userPreference.getUserId());
        reqParams.put("follow_up_id", String.valueOf(arrayList.get(position).follow_up_id));

        requestApi(reqParams, Request.Method.POST, WebService.DELETE_FOLLOW_UP, new RequestApiInterface() {
            @Override
            public void onResponse(String response) {
                progressDialog.hide();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Toast.makeText(EnquiryDetailsActivity.this, jsonObject.getString(WebService.Params.MESSAGE), Toast.LENGTH_SHORT).show();
                    if (jsonObject.getInt(WebService.Params.STATUS) == WebService.SUCCESS) {
                        arrayList.remove(position);
                        mAdapterFollowUp.notifyItemRemoved(position);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error, int errorCode) {
                progressDialog.hide();
            }
        }, null);
    }

    public void getBatchTime() {

        ProgressDialog progressDialog = new ProgressDialog(EnquiryDetailsActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        Map<String, String> reqParams = new HashMap<>();
        reqParams.put("api_key", WebService.API_KEY);

        requestApi(reqParams, Request.Method.POST, WebService.GET_BATCH_TIME, new RequestApiInterface() {
            @Override
            public void onResponse(String response) {
                progressDialog.hide();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt(WebService.Params.STATUS) == WebService.SUCCESS) {
                        JSONArray jsonArray = jsonObject.getJSONArray(WebService.Params.DATA);
                        Type listType = new TypeToken<List<String>>() {
                        }.getType();
                        arrayListBatchTime.clear();
                        arrayListBatchTime.add("Select Batch Time");
                        arrayListBatchTime.addAll(new Gson().fromJson(jsonArray.toString(), listType));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error, int errorCode) {
                progressDialog.hide();
            }
        }, null);
    }

    public void addEnrollment() {
        if (courseListModels.size() > 0) {
            courseListModels.clear();
        }
        LayoutInflater localView = LayoutInflater.from(this);
        DialogAddEnrollmentBinding dialogBinding = DialogAddEnrollmentBinding.inflate(localView);

        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setView(dialogBinding.getRoot());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);


        dialogBinding.rvDialogCourseList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        courseListAdapter = new CourseListAdapter(this, courseListModels);
        dialogBinding.rvDialogCourseList.setAdapter(courseListAdapter);

        dialogBinding.edtCourseAmount.setText(userPreference.getCourseFee());

        if (SELECTED_COURSE_ID != -1) {
            COURSE_ID = String.valueOf(SELECTED_COURSE_ID + 1);
            courseListAdapter.setSelectedPosition(SELECTED_COURSE_ID);
        }

        courseListAdapter.setRecyclerClickInterface(new RecyclerClickInterface() {
            @Override
            public void onItemClick(int position) {
                COURSE_ID = courseListModels.get(position).getCourseId();
                COURSE_FEE = courseListModels.get(position).getCourseFee();
                dialogBinding.edtCourseAmount.setText(COURSE_FEE);
            }
        });

        getCourse();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.spn_batch_time_textview, arrayListBatchTime);

        dialogBinding.spnBatchTime.setAdapter(adapter);

        dialogBinding.edtFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int cYear = Integer.parseInt(Helper.getCurrentDateTime("yyyy"));
                int cMonth = Integer.parseInt(Helper.getCurrentDateTime("MM")) - 1;
                int cDay = Integer.parseInt(Helper.getCurrentDateTime("dd"));

                try {
                    String date = dialogBinding.edtFromDate.getTag().toString();
                    cYear = Integer.parseInt(Helper.changeDateFormat(date, Helper.defaultDateFormat, "yyyy"));
                    cMonth = Integer.parseInt(Helper.changeDateFormat(date, Helper.defaultDateFormat, "MM")) - 1;
                    cDay = Integer.parseInt(Helper.changeDateFormat(date, Helper.defaultDateFormat, "dd"));
                } catch (Exception e) {

                }

                DatePickerDialog datePickerDialog = new DatePickerDialog(EnquiryDetailsActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        String date = year + "-" + (month + 1) + "-" + dayOfMonth;
                        // Toast.makeText(EnquiryDetailsActivity.this, "Please enter From date", Toast.LENGTH_SHORT).show();

                        dialogBinding.edtFromDate.setTag(date);
                        // Toast.makeText(EnquiryDetailsActivity.this, "Please enter From date", Toast.LENGTH_SHORT).show();

                        dialogBinding.edtFromDate.setText(Helper.changeDateFormat(date, Helper.defaultDateFormat, "dd MMM yyyy"));
                    }
                }, cYear, cMonth, cDay);

                //datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.setTitle("");
                datePickerDialog.show();
            }
        });

        dialogBinding.edtToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int cYear = Integer.parseInt(Helper.getCurrentDateTime("yyyy"));
                int cMonth = Integer.parseInt(Helper.getCurrentDateTime("MM")) - 1;
                int cDay = Integer.parseInt(Helper.getCurrentDateTime("dd"));

                try {
                    String date = dialogBinding.edtToDate.getTag().toString();
                    cYear = Integer.parseInt(Helper.changeDateFormat(date, Helper.defaultDateFormat, "yyyy"));
                    cMonth = Integer.parseInt(Helper.changeDateFormat(date, Helper.defaultDateFormat, "MM")) - 1;
                    cDay = Integer.parseInt(Helper.changeDateFormat(date, Helper.defaultDateFormat, "dd"));
                } catch (Exception e) {

                }

                DatePickerDialog datePickerDialog = new DatePickerDialog(EnquiryDetailsActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        String date = year + "-" + (month + 1) + "-" + dayOfMonth;
                        dialogBinding.edtToDate.setTag(date);
                        dialogBinding.edtToDate.setText(Helper.changeDateFormat(date, Helper.defaultDateFormat, "dd MMM yyyy"));
                    }
                }, cYear, cMonth, cDay);

                //datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.setTitle("");
                datePickerDialog.show();
            }
        });

        dialogBinding.rdbPartPayment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    dialogBinding.viewPayAmount.setVisibility(View.VISIBLE);
                else dialogBinding.viewPayAmount.setVisibility(View.GONE);
            }
        });

        dialogBinding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialogBinding.edtFromDate.getText().toString().isEmpty()) {
                    //  Toast.makeText(EnquiryDetailsActivity.this, "Please select from date", Toast.LENGTH_SHORT).show();
                    dialogBinding.edtFromDate.setError("Select from date");
                    return;
                }
                if (dialogBinding.edtToDate.getText().toString().isEmpty()) {
                    dialogBinding.edtToDate.setError("Select to date");
                    //  Toast.makeText(EnquiryDetailsActivity.this, "Please select to date", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (dialogBinding.spnBatchTime.getSelectedItemPosition() == 0) {
                    Toast.makeText(EnquiryDetailsActivity.this, "Select valid batch time", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (dialogBinding.edtCourseAmount.getText().toString().isEmpty()) {
                    //   dialogBinding.edtCourseAmount.setError("Enter course amount");
                    Toast.makeText(EnquiryDetailsActivity.this, "Enter course amount", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (COURSE_ID.isEmpty()) {
                    Toast.makeText(EnquiryDetailsActivity.this, "Select Course", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!dialogBinding.rdbPartPayment.isChecked() && !dialogBinding.rdbFullPayment.isChecked()) {
                    Toast.makeText(EnquiryDetailsActivity.this, "Select fees option", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (dialogBinding.rdbPartPayment.isChecked()) {
                    if (dialogBinding.edtPayAmount.getText().toString().isEmpty()) {
                        dialogBinding.edtPayAmount.setError("Enter pay amount");
                        return;
                    }
                    int courseValue = Integer.parseInt(dialogBinding.edtCourseAmount.getText().toString().trim());
                    int value = Integer.parseInt(dialogBinding.edtPayAmount.getText().toString().trim());
                    if (value > courseValue) {
                        dialogBinding.edtPayAmount.setError("Invalid pay amount");
                        return;
                    }
                }

                ProgressDialog progressDialog = new ProgressDialog(EnquiryDetailsActivity.this);
                progressDialog.setMessage("Please wait...");
                progressDialog.show();

                Map<String, String> reqParams = new HashMap<>();
                reqParams.put("api_key", WebService.API_KEY);
                reqParams.put("enquiry_id", String.valueOf(enquiryId));
                reqParams.put("user_id", userPreference.getUserId());
                reqParams.put("start_date", dialogBinding.edtFromDate.getTag().toString());
                reqParams.put("end_date", dialogBinding.edtToDate.getTag().toString());
                reqParams.put("batch_time", dialogBinding.spnBatchTime.getSelectedItem().toString());
                reqParams.put("fees_type", dialogBinding.rdbFullPayment.isChecked() ? "0" : "1");
                reqParams.put("pay_amount", dialogBinding.edtPayAmount.getText().toString().trim());
                reqParams.put("course_fees", dialogBinding.edtCourseAmount.getText().toString().trim());
                reqParams.put("course_id", COURSE_ID);

                requestApi(reqParams, Request.Method.POST, WebService.ENQUIRY_ENROLLMENT, new RequestApiInterface() {
                    @Override
                    public void onResponse(String response) {
                        alertDialog.dismiss();
                        progressDialog.dismiss();
                        getEnquiryDetails();
                        getFeeDetails();
                    }

                    @Override
                    public void onError(VolleyError error, int errorCode) {
                        progressDialog.dismiss();
                    }
                }, null);
            }
        });

        dialogBinding.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void getCourse() {
        Map<String, String> reqParams = new HashMap<String, String>();
        reqParams.put("api_key", WebService.API_KEY);

        requestApi(reqParams, Request.Method.POST, WebService.GET_COURSE, new BaseActivity.RequestApiInterface() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt(WebService.Params.STATUS) == WebService.SUCCESS) {
                        JSONArray dataJsonArray = jsonObject.getJSONArray(WebService.Params.DATA);
                        for (int i = 0; i < dataJsonArray.length(); i++) {
                            JSONObject dataJsonObject = dataJsonArray.getJSONObject(i);
                            CourseListModel item = new CourseListModel();

                            item.courseId = dataJsonObject.getString("course_id");
                            item.courseName = dataJsonObject.getString("course_name");
                            item.courseFee = dataJsonObject.getString("fees");
                            courseListModels.add(item);
                        }
                        courseListAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(EnquiryDetailsActivity.this, jsonObject.getString(WebService.Params.MESSAGE), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(VolleyError error, int errorCode) {

            }
        }, null);

    }

    public void onCloseClick(View view) {
        finish();
    }
}