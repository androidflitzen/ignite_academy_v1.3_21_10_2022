package com.anzo.igniteacademy.Fragment;

import android.app.ProgressDialog;
import android.app.VoiceInteractor;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.anzo.igniteacademy.Activity.EnquiryDetailsActivity;
import com.anzo.igniteacademy.Activity.EnrollmentDetailActivity;
import com.anzo.igniteacademy.Adapter.NotificationAdapter;
import com.anzo.igniteacademy.Api.WebService;
import com.anzo.igniteacademy.BaseActivity;
import com.anzo.igniteacademy.Interface.RecyclerClickInterface;
import com.anzo.igniteacademy.Model.NotificationModel;
import com.anzo.igniteacademy.R;
import com.anzo.igniteacademy.databinding.FragmentNotificationBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationFragment extends Fragment {

    FragmentNotificationBinding binding;
    private BaseActivity baseActivity;

    private NotificationAdapter notificationAdapter;
    private List<NotificationModel> notificationModel = new ArrayList<>();
    private int pageNo = 1;
    private boolean isMoreDataAvailable = true;
    private boolean isApiRunning = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentNotificationBinding.inflate(getLayoutInflater(), container, false);
        baseActivity = (BaseActivity) getActivity();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.rvNotificationList.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        notificationAdapter = new NotificationAdapter(getContext(), notificationModel);
        binding.rvNotificationList.setAdapter(notificationAdapter);

        notificationAdapter.setRecyclerClickInterface(new RecyclerClickInterface() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getContext(), EnquiryDetailsActivity.class);
                intent.putExtra("ID", notificationModel.get(position).enquiryId);
                startActivity(intent);
            }
        });
        getNotification();

        binding.swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageNo = 1;
                isMoreDataAvailable = true;
                getNotification();
            }
        });
        binding.nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (v.getChildAt(v.getChildCount() - 1) != null) {
                    int parentHeight = (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight());
                    //Helper.LOG("Scrolling", scrollY + " / " + (parentHeight - 500) + " / " + oldScrollY);
                    if ((scrollY >= (parentHeight - 500)) &&
                            scrollY > oldScrollY) {

                        int visibleItemCount = binding.rvNotificationList.getLayoutManager().getChildCount();
                        int totalItemCount = binding.rvNotificationList.getLayoutManager().getItemCount();
                        int pastVisiblesItems = ((LinearLayoutManager) binding.rvNotificationList.getLayoutManager()).findFirstVisibleItemPosition();

                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            if (isMoreDataAvailable && !isApiRunning) {
                                pageNo++;
                                getNotification();
                            }
                            //Helper.LOG("Scroll", "Reached");
                        }
                    }
                }
            }
        });
    }

    private void getNotification() {
        isApiRunning = true;
        binding.swipeToRefresh.setRefreshing(true);
        Map<String, String> reqParams = new HashMap<>();
        reqParams.put("api_key", WebService.API_KEY);
        reqParams.put("page_no", String.valueOf(pageNo));

        baseActivity.requestApi(reqParams, Request.Method.POST, WebService.GET_NOTIFICATION, new BaseActivity.RequestApiInterface() {
            @Override
            public void onResponse(String response) {
                isApiRunning = false;
                if (!isAdded())
                    return;
                binding.swipeToRefresh.setRefreshing(false);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt(WebService.Params.STATUS) == WebService.SUCCESS) {
                        JSONArray dataJsonArray = jsonObject.getJSONArray(WebService.Params.DATA);
                        for (int i = 0; i < dataJsonArray.length(); i++) {
                            JSONObject dataJsonObject = dataJsonArray.getJSONObject(i);
                            NotificationModel item = new NotificationModel();
                            item.notificationMessage = dataJsonObject.getString("message");
                            item.enquiryId = dataJsonObject.getInt("enquiry_id");
                            item.webOrApp = dataJsonObject.getInt("web_or_app");
                            item.createdAt = dataJsonObject.getString("created_at");
                            item.isNotificationDeleted = dataJsonObject.getString("is_deleted");
                            notificationModel.add(item);
                        }
                        notificationAdapter.notifyDataSetChanged();
                    } else {
                        isMoreDataAvailable = false;
                        Toast.makeText(getContext(), jsonObject.getString(WebService.Params.MESSAGE), Toast.LENGTH_SHORT).show();
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
}