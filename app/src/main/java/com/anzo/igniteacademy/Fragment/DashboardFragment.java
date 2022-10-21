package com.anzo.igniteacademy.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.anzo.igniteacademy.Api.WebService;
import com.anzo.igniteacademy.BaseActivity;
import com.anzo.igniteacademy.Helper.IntentHelper;
import com.anzo.igniteacademy.R;
import com.anzo.igniteacademy.databinding.FragmentDashboardBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class DashboardFragment extends Fragment {

    FragmentDashboardBinding binding;
    private BaseActivity baseActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDashboardBinding.inflate(getLayoutInflater(), container, false);
        baseActivity = (BaseActivity) getActivity();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.cardEnquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Bundle bundle = new Bundle();
//                bundle.putString("enquiry_status", "all_enquiries");
//                bundle.putString("enquiry", "All Student");

                IntentHelper.INTENT_ENQUIRY_STATUS = "all_enquiries";
                IntentHelper.INTENT_SET_NAME = "All Student";

                EnquiryFragment fragment = new EnquiryFragment();
//                fragment.setArguments(bundle);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.frameLayout, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        binding.cardEnrollment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Bundle bundle = new Bundle();
//                bundle.putString("enquiry_status", "enrollments");
//                bundle.putString("enquiry", "Enrollments");

                IntentHelper.INTENT_ENQUIRY_STATUS = "enrollments";
                IntentHelper.INTENT_SET_NAME = "Enrollments";

                EnquiryFragment fragment = new EnquiryFragment();
//                fragment.setArguments(bundle);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.frameLayout, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        binding.cardPastEnrollment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Bundle bundle = new Bundle();
//                bundle.putString("enquiry_status", "past_enrollments");
//                bundle.putString("enquiry", " Past Enrollments ");

                IntentHelper.INTENT_ENQUIRY_STATUS = "past_enrollments";
                IntentHelper.INTENT_SET_NAME = " Past Enrollments ";

                EnquiryFragment fragment = new EnquiryFragment();
//                fragment.setArguments(bundle);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.frameLayout, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        binding.cardCloseEnquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Bundle bundle = new Bundle();
//                bundle.putString("enquiry_status", "canceled_enquiries");
//                bundle.putString("enquiry", " Canceled Enrollments ");

                IntentHelper.INTENT_ENQUIRY_STATUS = "canceled_enquiries";
                IntentHelper.INTENT_SET_NAME = " Canceled Enrollments ";

                EnquiryFragment fragment = new EnquiryFragment();
//                fragment.setArguments(bundle);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.frameLayout, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        binding.cardLiveEnquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Bundle bundle = new Bundle();
//                bundle.putString("enquiry_status", "live_enquiries");
//                EnquiryFragment fragment = new EnquiryFragment();
//                fragment.setArguments(bundle);
//                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                FragmentTransaction transaction = fragmentManager.beginTransaction();
//                transaction.replace(R.id.frameLayout, fragment);
//                transaction.addToBackStack(null);
//                transaction.commit();

//                Bundle bundle = new Bundle();
//                bundle.putString("enquiry_status", "live_enquiries");
//                bundle.putString("enquiry", " Live Enquiry ");

                IntentHelper.INTENT_ENQUIRY_STATUS = "live_enquiries";
                IntentHelper.INTENT_SET_NAME = "Live Enquiry";

                EnquiryFragment fragment = new EnquiryFragment();
//                fragment.setArguments(bundle);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });

        getTotalRecord();
    }
    private void getTotalRecord() {
        Map<String, String> reqParams = new HashMap<String, String>();
        reqParams.put("api_key", WebService.API_KEY);
        reqParams.put("user_id", baseActivity.userPreference.getUserId());

        baseActivity.requestApi(reqParams, Request.Method.POST, WebService.TOTAL_RECORD, new BaseActivity.RequestApiInterface() {
            @Override
            public void onResponse(String response) {
                if (!isAdded())
                    return;
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt(WebService.Params.STATUS) == WebService.SUCCESS) {
                        JSONObject dataJsonObject = jsonObject.getJSONObject(WebService.Params.DATA);

                        binding.tvTotalEnquiryCount.setText(dataJsonObject.getString("total_enquiry"));
                        binding.tvTotalEnrollment.setText(dataJsonObject.getString("total_enrollment"));
                        binding.tvTotalPastEnrollment.setText(dataJsonObject.getString("total_past_enrollment"));
                        binding.tvTotalCloseEnquiry.setText(dataJsonObject.getString("total_cancel_enquiry"));
                        binding.tvTotalLiveEnquiry.setText(dataJsonObject.getString("total_live_enquiry"));
                    } else {
                        Toast.makeText(getContext(), jsonObject.getString(WebService.Params.MESSAGE), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error, int errorCode) {
                if (!isAdded())
                    return;
            }
        }, null);

    }
}