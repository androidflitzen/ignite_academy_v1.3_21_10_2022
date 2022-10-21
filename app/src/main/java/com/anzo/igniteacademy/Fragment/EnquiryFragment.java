package com.anzo.igniteacademy.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.anzo.igniteacademy.Activity.EnquiryDetailsActivity;
import com.anzo.igniteacademy.Adapter.EnquiryListAdapter;
import com.anzo.igniteacademy.Api.WebService;
import com.anzo.igniteacademy.BaseActivity;
import com.anzo.igniteacademy.Helper.IntentHelper;
import com.anzo.igniteacademy.Interface.RecyclerClickInterface;
import com.anzo.igniteacademy.Model.EnquiryListModel;
import com.anzo.igniteacademy.R;
import com.anzo.igniteacademy.databinding.FragmentEnquiryBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EnquiryFragment extends Fragment {
    private static final String TAG = "EnquiryFragment";

    FragmentEnquiryBinding binding;
    private BaseActivity baseActivity;

    private ArrayList<EnquiryListModel> arrayList = new ArrayList<>();
    private EnquiryListAdapter mAdapter;
    private int pageNo = 1;
    private boolean isMoreDataAvailable = true;
    private boolean isApiRunning = false;
    public static final int EDIT_REQUEST_CODE = 100;
    Dialog dialog;
    TextView tvEnquiry, tvHome, tvNotification, tvLogout;
    ImageView ivEnquiry, ivHome, ivNotification, ivLogout;
    LinearLayout btnEnquiry;
    public String ENQUIRY_STATUS = "live_enquiries";
    public String SET_NAME = "Live Enquiries";



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEnquiryBinding.inflate(getLayoutInflater(), container, false);
        baseActivity = (BaseActivity) getActivity();
        /*Bundle bundle = getArguments();
        if (bundle != null) {
            ENQUIRY_STATUS = bundle.getString("enquiry_status");
            SET_NAME = bundle.getString("enquiry");
        }*/
        ENQUIRY_STATUS = IntentHelper.INTENT_ENQUIRY_STATUS;
        SET_NAME = IntentHelper.INTENT_SET_NAME;

        Log.d(TAG, "onCreateView: ENQUAIRY_STATUS : "+ENQUIRY_STATUS);
        Log.d(TAG, "onCreateView: SET_NAME        : "+SET_NAME);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.tvTitle.setText(SET_NAME);

        tvEnquiry = getActivity().findViewById(R.id.tvEnquiry);
        ivEnquiry = getActivity().findViewById(R.id.ivEnquiry);
        tvHome = getActivity().findViewById(R.id.tvHome);
        ivHome = getActivity().findViewById(R.id.ivHome);
        btnEnquiry = getActivity().findViewById(R.id.btn_enquiry);

        btnEnquiry.setActivated(true);


        //  ivEnquiry.setColorFilter(getResources().getColor(R.color.secondary_primary));
        // tvEnquiry.setTextColor(getResources().getColor(R.color.secondary_primary));

        //   ivHome.setColorFilter(getResources().getColor(R.color.white));
        //  tvHome.setTextColor(getResources().getColor(R.color.white));

        binding.rvEnquiryList.setNestedScrollingEnabled(false);
        mAdapter = new EnquiryListAdapter(baseActivity, getContext(), arrayList);
        binding.rvEnquiryList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvEnquiryList.setAdapter(mAdapter);
        getEnquiryList(ENQUIRY_STATUS);

        binding.swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageNo = 1;
                isMoreDataAvailable = true;
                getEnquiryList(ENQUIRY_STATUS);
            }
        });

        binding.btnAddNewEnquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnEnquiry.setActivated(false);
                IntentHelper.IS_EDIT_CLICK = true;

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new AddNewEnquiryFragment()).commit();
            }
        });

//        binding.btnClosedEnquiry.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ENQUIRY_STATUS = "canceled_enquiries";
//                getEnquiryList(ENQUIRY_STATUS);
//                ENQUIRY_STATUS = "Canceled Enquiries";
//                binding.tvTitle.setText(ENQUIRY_STATUS);
//            }
//        });

        binding.nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (v.getChildAt(v.getChildCount() - 1) != null) {
                    int parentHeight = (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight());
                    //Helper.LOG("Scrolling", scrollY + " / " + (parentHeight - 500) + " / " + oldScrollY);
                    if ((scrollY >= (parentHeight - 500)) &&
                            scrollY > oldScrollY) {

                        int visibleItemCount = binding.rvEnquiryList.getLayoutManager().getChildCount();
                        int totalItemCount = binding.rvEnquiryList.getLayoutManager().getItemCount();
                        int pastVisiblesItems = ((LinearLayoutManager) binding.rvEnquiryList.getLayoutManager()).findFirstVisibleItemPosition();

                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            if (isMoreDataAvailable && !isApiRunning) {
                                pageNo++;
                                getEnquiryList(ENQUIRY_STATUS);
                            }
                            //Helper.LOG("Scroll", "Reached");
                        }
                    }
                }
            }
        });

        mAdapter.setClickInterface(new RecyclerClickInterface() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getActivity(), EnquiryDetailsActivity.class);
                intent.putExtra("ID", arrayList.get(position).id);
                startActivity(intent);
            }
        });
        mAdapter.setOnDeleteEnquiry(new EnquiryListAdapter.setOnDeleteEnquiry() {
            @Override
            public void onDeleteEnquiry(int position) {
                showDeleteDialog(position);
            }
        });
        mAdapter.setOnCancelEnquiry(new EnquiryListAdapter.setOnCancelEnquiry() {
            @Override
            public void onCancelEnquiry(int position) {
                showCancelDialog(position);
            }
        });

        mAdapter.setOnItemEdit(new EnquiryListAdapter.setOnItemEdit() {
            @Override
            public void onItemEdit(int position) {
                btnEnquiry.setActivated(false);
                Bundle bundle = new Bundle();
                bundle.putInt("ID", arrayList.get(position).id);
                IntentHelper.IS_EDIT_CLICK = true;
                AddNewEnquiryFragment addNewEnquiryFragment = new AddNewEnquiryFragment();
                addNewEnquiryFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameLayout, addNewEnquiryFragment)
                        .commit();
            }
        });
    }

    private void showDeleteDialog(int position) {
        dialog = new Dialog(getContext());
        dialog.requestWindowFeature(1);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.delete_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

        CardView btnDelete = (CardView) dialog.findViewById(R.id.cardPositive);
        CardView btnCancel = (CardView) dialog.findViewById(R.id.cardNegative);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteEnquiry(position);
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void deleteEnquiry(int position) {

        Map<String, String> reqParams = new HashMap<String, String>();
        reqParams.put("api_key", WebService.API_KEY);
        reqParams.put("enquiry_id", String.valueOf(arrayList.get(position).id));
        reqParams.put("user_id", baseActivity.userPreference.getUserId());

        baseActivity.requestApi(reqParams, Request.Method.POST, WebService.DELETE_ENQUIRY, new BaseActivity.RequestApiInterface() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt(WebService.Params.STATUS) == WebService.SUCCESS) {
                        arrayList.remove(position);
                        mAdapter.notifyItemRemoved(position);

                        if (arrayList.size() == 0) {
                            binding.layoutEmpty.setVisibility(View.VISIBLE);
                            binding.rvEnquiryList.setVisibility(View.GONE);
                        }
                        Toast.makeText(getContext(), jsonObject.getString(WebService.Params.MESSAGE), Toast.LENGTH_SHORT).show();
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

    private void showCancelDialog(int position) {
        dialog = new Dialog(getContext());
        dialog.requestWindowFeature(1);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.delete_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        TextView textView=(TextView) dialog.findViewById(R.id.tvMessage);
        textView.setText("Are You Sure!! You Want To Cancel Enquiry");
        CardView btnDelete = (CardView) dialog.findViewById(R.id.cardPositive);
        CardView btnCancel = (CardView) dialog.findViewById(R.id.cardNegative);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelEnquiry(position);
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void cancelEnquiry(int position) {

        Map<String, String> reqParams = new HashMap<String, String>();
        reqParams.put("api_key", WebService.API_KEY);
        reqParams.put("enquiry_id", String.valueOf(arrayList.get(position).id));
        reqParams.put("user_id", baseActivity.userPreference.getUserId());

        baseActivity.requestApi(reqParams, Request.Method.POST, WebService.CANCEL_ENQUIRY, new BaseActivity.RequestApiInterface() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt(WebService.Params.STATUS) == WebService.SUCCESS) {
                        arrayList.remove(position);
                        mAdapter.notifyItemRemoved(position);

                        if (arrayList.size() == 0) {
                            binding.layoutEmpty.setVisibility(View.VISIBLE);
                            binding.rvEnquiryList.setVisibility(View.GONE);
                        }
                        Toast.makeText(getContext(), jsonObject.getString(WebService.Params.MESSAGE), Toast.LENGTH_SHORT).show();
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

    public void getEnquiryList(String ENQUIRY_STATUS) {
        isApiRunning = true;
        binding.swipeToRefresh.setRefreshing(true);
        Map<String, String> reqParams = new HashMap<String, String>();
        reqParams.put("api_key", WebService.API_KEY);
        reqParams.put("user_id", baseActivity.userPreference.getUserId());
        reqParams.put("status", ENQUIRY_STATUS);
        reqParams.put("page_no", String.valueOf(pageNo));

        baseActivity.requestApi(reqParams, Request.Method.POST, WebService.ENQUIRY_LIST, new BaseActivity.RequestApiInterface() {
            @Override
            public void onResponse(String response) {
                isApiRunning = false;
                if (!isAdded())
                    return;
                binding.swipeToRefresh.setRefreshing(false);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt(WebService.Params.STATUS) == WebService.SUCCESS) {
                        Type listType = new TypeToken<List<EnquiryListModel>>() {
                        }.getType();

                        if (pageNo == 1)
                            arrayList.clear();
                        arrayList.addAll(new Gson().fromJson(jsonObject.getJSONArray(WebService.Params.DATA).toString(), listType));
                        mAdapter.notifyDataSetChanged();
                    } else {
                        isMoreDataAvailable = false;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error, int errorCode) {
                isApiRunning = false;
                if (!isAdded())
                    return;
                binding.swipeToRefresh.setRefreshing(false);
            }
        }, null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if (requestCode == EDIT_REQUEST_CODE && resultCode == RESULT_OK){

        }*/
    }

}