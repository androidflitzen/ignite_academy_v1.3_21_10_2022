package com.anzo.igniteacademy.Fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.anzo.igniteacademy.Adapter.CountryListAdapter;
import com.anzo.igniteacademy.Adapter.CourseListAdapter;
import com.anzo.igniteacademy.Api.WebService;
import com.anzo.igniteacademy.BaseActivity;
import com.anzo.igniteacademy.Helper.Helper;
import com.anzo.igniteacademy.Helper.UserPreferences;
import com.anzo.igniteacademy.Interface.RecyclerClickInterface;
import com.anzo.igniteacademy.Model.CountryListModel;
import com.anzo.igniteacademy.Model.CourseListModel;
import com.anzo.igniteacademy.R;
import com.anzo.igniteacademy.databinding.FragmentAddNewEnquiryBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class AddNewEnquiryFragment extends Fragment {

    FragmentAddNewEnquiryBinding binding;
    private BaseActivity baseActivity;

    CourseListAdapter courseListAdapter;
    List<CourseListModel> courseListModels = new ArrayList<>();

    CountryListAdapter countryListAdapter;
    List<CountryListModel> countryListModels = new ArrayList<>();

    public static String COURSE_ID = "";
    public static int ENQUIRY_ID = 0;
    Dialog dialog;
    public static String COURSE_NAME = "", GENDER = "", COURSE_FEE = "";
    public static boolean EDIT_MODE = false;
    int BUNDLE_COURSE_ID = -1;
    TextView tvEnquiry, tvHome, tvNotification, tvLogout;
    ImageView ivEnquiry, ivHome, ivNotification, ivLogout;
    LinearLayout btnEnquiry;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddNewEnquiryBinding.inflate(getLayoutInflater(), container, false);
        baseActivity = (BaseActivity) getActivity();
        Bundle bundle = getArguments();
        if (bundle != null) {
            binding.text.setText("Edit Enquiry");
            EDIT_MODE = true;
            ENQUIRY_ID = bundle.getInt("ID");
        }else{
            EDIT_MODE =false;
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (EDIT_MODE) {
            getEnquiryDetails(String.valueOf(ENQUIRY_ID));
        }else {
              getEnquiryDetails(String.valueOf(false));
        }

        binding.rvCourseList.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        courseListAdapter = new CourseListAdapter(getContext(), courseListModels);
        binding.rvCourseList.setAdapter(courseListAdapter);

        courseListAdapter.setRecyclerClickInterface(new RecyclerClickInterface() {
            @Override
            public void onItemClick(int position) {
                COURSE_ID = courseListModels.get(position).getCourseId();
                COURSE_FEE = courseListModels.get(position).getCourseFee();
                baseActivity.userPreference.setData(UserPreferences.COURSE_FEE, COURSE_FEE);
            }
        });

        binding.edtCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCountryDialog();
            }
        });

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.edtName.getText().toString().isEmpty()) {
                    binding.edtName.setError("Name Required");
                    return;
                }
                if (binding.edtPhone.getText().toString().length() < 10) {
                    binding.edtPhone.setError("Enter Valid Phone Number");
                    return;
                }
                if (binding.edtPhone.getText().toString().isEmpty()) {
                    binding.edtPhone.setError("Enter Phone Number");
                    return;
                }
                if (EDIT_MODE) {
                    addEnquiry(true);
                }
                else {
                    addEnquiry(false);
                }

            }
        });

        binding.edtDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickDob();
            }
        });
        getCourseList();
        getCountry();
    }

    private void getEnquiryDetails(String enquiryId) {

        Map<String, String> reqParams = new HashMap<>();
        reqParams.put("api_key", WebService.API_KEY);
        reqParams.put("user_id", baseActivity.userPreference.getUserId());
        reqParams.put("enquiry_id", String.valueOf(enquiryId));

        baseActivity.requestApi(reqParams, Request.Method.POST, WebService.ENQUIRY_DETAILS, new BaseActivity.RequestApiInterface() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt(WebService.Params.STATUS) == WebService.SUCCESS) {
                        JSONArray dataJsonArray = jsonObject.getJSONArray(WebService.Params.DATA);
                        for (int i = 0; i < dataJsonArray.length(); i++) {
                            JSONObject dataJsonObject = dataJsonArray.getJSONObject(i);
                            binding.edtName.setText(dataJsonObject.getString("name"));
                            binding.edtPhone.setText(dataJsonObject.getString("phone_number"));
                            binding.edtEmail.setText(dataJsonObject.getString("email"));
                            binding.edtCity.setText(dataJsonObject.getString("city"));
                            BUNDLE_COURSE_ID = dataJsonObject.getInt("course_id") - 1;
                            GENDER = dataJsonObject.getString("gender");
                            binding.edtCountry.setText(dataJsonObject.getString("country"));
                            binding.edtDob.setText(Helper.changeDateFormat(dataJsonObject.getString("birth_date"), Helper.defaultDateFormat, "dd MMM yyyy"));
                            binding.edtAddress.setText(dataJsonObject.getString("address"));
                            binding.edtRemark.setText(dataJsonObject.getString("remarks"));
                            COURSE_ID = String.valueOf(BUNDLE_COURSE_ID) + 1;
                            if (GENDER.equals(binding.rdbMale.getText().toString().toLowerCase(Locale.ROOT))) {
                                binding.rdbMale.setChecked(true);
                            } else if (GENDER.equals(binding.rdbFemale.getText().toString().toLowerCase(Locale.ROOT))) {
                                binding.rdbFemale.setChecked(true);
                            } else {

                            }
                            if (BUNDLE_COURSE_ID != -1)
                                courseListAdapter.setSelectedPosition(BUNDLE_COURSE_ID);
                        }
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

    public void pickDob() {
        int cYear = Integer.parseInt(Helper.getCurrentDateTime("yyyy"));
        int cMonth = Integer.parseInt(Helper.getCurrentDateTime("MM")) - 1;
        int cDay = Integer.parseInt(Helper.getCurrentDateTime("dd"));

        try {
            String date = binding.edtDob.getTag().toString();
            cYear = Integer.parseInt(Helper.changeDateFormat(date, Helper.defaultDateFormat, "yyyy"));
            cMonth = Integer.parseInt(Helper.changeDateFormat(date, Helper.defaultDateFormat, "MM")) - 1;
            cDay = Integer.parseInt(Helper.changeDateFormat(date, Helper.defaultDateFormat, "dd"));
        } catch (Exception e) {
          //  Toast.makeText(getContext(), ""+e, Toast.LENGTH_SHORT).show();
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                String date = year + "-" + (month + 1) + "-" + dayOfMonth;
                binding.edtDob.setTag(date);
                binding.edtDob.setText(Helper.changeDateFormat(date, Helper.defaultDateFormat, "dd MMM yyyy"));
            }
        }, cYear, cMonth, cDay);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.setTitle("");
        datePickerDialog.show();
    }

    private void addEnquiry(boolean editMode) {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();

        Map<String, String> reqParams = new HashMap<String, String>();

        String gender = "";
        if(binding.rdbMale.isChecked()){
            gender = "male";
        }else if(binding.rdbFemale.isChecked()){
            gender = "female";
        }
//        String dob ="1970-01-01";
//        if (binding.edtDob.getText().toString().equals(dob)){
//            Toast.makeText(getContext(), "if", Toast.LENGTH_SHORT).show();
//            binding.edtDob.setText("");
//        }else {
//            binding.edtDob.getText().toString();
//            Toast.makeText(getContext(), "else", Toast.LENGTH_SHORT).show();
//        }


        if (editMode) {
            reqParams.put("api_key", WebService.API_KEY);
            reqParams.put("enquiry_id", String.valueOf(ENQUIRY_ID));
            reqParams.put("user_id", baseActivity.userPreference.getUserId());
            reqParams.put("name", binding.edtName.getText().toString());
            reqParams.put("phone_number", binding.edtPhone.getText().toString());
            reqParams.put("email", binding.edtEmail.getText().toString());
            reqParams.put("city", binding.edtCity.getText().toString());
            reqParams.put("country", binding.edtCountry.getText().toString());
            reqParams.put("course_id", COURSE_ID);
            reqParams.put("birth_date", binding.edtDob.getText().toString());
          //  reqParams.put("birth_date", dob);
            reqParams.put("address", binding.edtAddress.getText().toString());
            reqParams.put("remarks", binding.edtRemark.getText().toString());
            reqParams.put("gender", gender);

        } else  {
            reqParams.put("api_key", WebService.API_KEY);
            reqParams.put("user_id", baseActivity.userPreference.getUserId().trim());
            reqParams.put("name", binding.edtName.getText().toString());
            reqParams.put("phone_number", binding.edtPhone.getText().toString());
            reqParams.put("email", binding.edtEmail.getText().toString());
            reqParams.put("city", binding.edtCity.getText().toString());
            reqParams.put("country", binding.edtCountry.getText().toString());
            reqParams.put("course_id", COURSE_ID);
            reqParams.put("birth_date", binding.edtDob.getText().toString().isEmpty() ? "" : binding.edtDob.getTag().toString());
             // reqParams.put("birth_date", dob);
            reqParams.put("address", binding.edtAddress.getText().toString());
            reqParams.put("remarks", binding.edtRemark.getText().toString());
            reqParams.put("gender", gender);
        }

        baseActivity.requestApi(reqParams, Request.Method.POST, WebService.ADD_ENQUIRY, new BaseActivity.RequestApiInterface() {
            @Override
            public void onResponse(String response) {
                progressDialog.cancel();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Toast.makeText(getContext(), jsonObject.getString(WebService.Params.MESSAGE), Toast.LENGTH_SHORT).show();
                    if (jsonObject.getInt(WebService.Params.STATUS) == WebService.SUCCESS) {
                        /*Bundle bundle = new Bundle();
                        bundle.putString("Course Fee",COURSE_FEE);
                        EnquiryFragment enquiryFragment = new EnquiryFragment();
                        enquiryFragment.setArguments(bundle);*/
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new EnquiryFragment()).commit();
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

    private void showCountryDialog() {
        if (countryListModels.size() == 0) {
            Toast.makeText(getContext(), "Country list not found!", Toast.LENGTH_SHORT).show();
            return;
        }
        dialog = new Dialog(getContext());
        dialog.requestWindowFeature(1);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

        RecyclerView rvCountryList = (RecyclerView) dialog.findViewById(R.id.rvCountryList);
        rvCountryList.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        countryListAdapter = new CountryListAdapter(getContext(), countryListModels);
        rvCountryList.setAdapter(countryListAdapter);
        countryListAdapter.setRecyclerClickInterface(new RecyclerClickInterface() {
            @Override
            public void onItemClick(int position) {
                binding.edtCountry.setText(countryListModels.get(position).getCountryName());
                dialog.dismiss();
            }
        });

        dialog.show();
        /*Window window = dialog.getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.color_primary));
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);*/
    }

    private void getCountry() {
        countryListModels.clear();
        Map<String, String> reqParams = new HashMap<String, String>();
        reqParams.put("api_key", WebService.API_KEY);

        baseActivity.requestApi(reqParams, Request.Method.POST, WebService.GET_COUNTRY, new BaseActivity.RequestApiInterface() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt(WebService.Params.STATUS) == WebService.SUCCESS) {
                        JSONArray dataJsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < dataJsonArray.length(); i++) {
                            JSONObject dataJsonObject = dataJsonArray.getJSONObject(i);
                            CountryListModel item = new CountryListModel();
                            item.countryName = dataJsonObject.getString("country_of_interest");
                            countryListModels.add(item);
                        }
                    } else {
                        Toast.makeText(getContext(), jsonObject.getString(WebService.Params.MESSAGE), Toast.LENGTH_SHORT).show();
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

    private void getCourseList() {

        Map<String, String> reqParams = new HashMap<String, String>();
        reqParams.put("api_key", WebService.API_KEY);

        baseActivity.requestApi(reqParams, Request.Method.POST, WebService.GET_COURSE, new BaseActivity.RequestApiInterface() {
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
                        Toast.makeText(getContext(), jsonObject.getString(WebService.Params.MESSAGE), Toast.LENGTH_SHORT).show();
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

}