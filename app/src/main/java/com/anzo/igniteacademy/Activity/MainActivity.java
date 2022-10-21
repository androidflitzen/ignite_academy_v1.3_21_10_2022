package com.anzo.igniteacademy.Activity;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.anzo.igniteacademy.BaseActivity;
import com.anzo.igniteacademy.Fragment.AddNewEnquiryFragment;
import com.anzo.igniteacademy.Fragment.DashboardFragment;
import com.anzo.igniteacademy.Fragment.EnquiryFragment;
import com.anzo.igniteacademy.Fragment.NotificationFragment;
import com.anzo.igniteacademy.Helper.IntentHelper;
import com.anzo.igniteacademy.R;
import com.anzo.igniteacademy.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivity {

    ActivityMainBinding binding;
    Dialog dialog;
    boolean doubleBackToExitPressedOnce = false;
    int enquiryId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new DashboardFragment());
        binding.ivHome.setColorFilter(getResources().getColor(R.color.secondary_primary));
        binding.tvHome.setTextColor(getResources().getColor(R.color.secondary_primary));

        if (getIntent().hasExtra(IntentHelper.EDIT_ENQUIRY_FROM_ENQUIRY_DETAILS)) {

            enquiryId = getIntent().getIntExtra("enquiryId", 0);
            // IntentHelper.EditID = "enquiryID";
            Bundle bundle = new Bundle();
            bundle.putInt("ID", enquiryId);
            binding.ivHome.setColorFilter(getResources().getColor(R.color.white));
            binding.tvHome.setTextColor(getResources().getColor(R.color.white));

            binding.ivNotification.setColorFilter(getResources().getColor(R.color.white));
            binding.tvNotification.setTextColor(getResources().getColor(R.color.white));

            binding.ivLogout.setColorFilter(getResources().getColor(R.color.white));
            binding.tvLogout.setTextColor(getResources().getColor(R.color.white));

            binding.ivEnquiry.setColorFilter(getResources().getColor(R.color.secondary_primary));
            binding.tvEnquiry.setTextColor(getResources().getColor(R.color.secondary_primary));

            binding.btnEnquiry.setActivated(false);

            IntentHelper.IS_EDIT_CLICK = true;

            AddNewEnquiryFragment addNewEnquiryFragment = new AddNewEnquiryFragment();
            addNewEnquiryFragment.setArguments(bundle);
            // replaceFragment(addNewEnquiryFragment);
            MainActivity.this.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameLayout, addNewEnquiryFragment)
                    .commit();

        }


        binding.btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.ivNotification.setColorFilter(getResources().getColor(R.color.white));
                binding.tvNotification.setTextColor(getResources().getColor(R.color.white));

                binding.ivLogout.setColorFilter(getResources().getColor(R.color.white));
                binding.tvLogout.setTextColor(getResources().getColor(R.color.white));

                binding.ivEnquiry.setColorFilter(getResources().getColor(R.color.white));
                binding.tvEnquiry.setTextColor(getResources().getColor(R.color.white));

                binding.ivHome.setColorFilter(getResources().getColor(R.color.secondary_primary));
                binding.tvHome.setTextColor(getResources().getColor(R.color.secondary_primary));

                replaceFragment(new DashboardFragment());
            }
        });

        binding.btnEnquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.ivHome.setColorFilter(getResources().getColor(R.color.white));
                binding.tvHome.setTextColor(getResources().getColor(R.color.white));

                binding.ivNotification.setColorFilter(getResources().getColor(R.color.white));
                binding.tvNotification.setTextColor(getResources().getColor(R.color.white));

                binding.ivLogout.setColorFilter(getResources().getColor(R.color.white));
                binding.tvLogout.setTextColor(getResources().getColor(R.color.white));

                binding.ivEnquiry.setColorFilter(getResources().getColor(R.color.secondary_primary));
                binding.tvEnquiry.setTextColor(getResources().getColor(R.color.secondary_primary));

                binding.btnEnquiry.setActivated(true);

                IntentHelper.INTENT_ENQUIRY_STATUS = "live_enquiries";
                IntentHelper.INTENT_SET_NAME = "Live Enquiry";
                replaceFragment(new EnquiryFragment());
            }
        });

        binding.btnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.ivLogout.setColorFilter(getResources().getColor(R.color.white));
                binding.tvLogout.setTextColor(getResources().getColor(R.color.white));

                binding.ivEnquiry.setColorFilter(getResources().getColor(R.color.white));
                binding.tvEnquiry.setTextColor(getResources().getColor(R.color.white));

                binding.ivHome.setColorFilter(getResources().getColor(R.color.white));
                binding.tvHome.setTextColor(getResources().getColor(R.color.white));

                binding.ivNotification.setColorFilter(getResources().getColor(R.color.secondary_primary));
                binding.tvNotification.setTextColor(getResources().getColor(R.color.secondary_primary));

                binding.btnNotification.setActivated(true);

                replaceFragment(new NotificationFragment());
            }
        });

        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showLogoutDialog();
            }
        });
    }

    private void showLogoutDialog() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(1);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.delete_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

        CardView btnYes = (CardView) dialog.findViewById(R.id.cardPositive);
        CardView btnNo = (CardView) dialog.findViewById(R.id.cardNegative);
        TextView tvMessage = (TextView) dialog.findViewById(R.id.tvMessage);

        tvMessage.setText("Logout \n Sure You Want To Logout?");
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPreference.clear();
                userPreference.setLogin(false);
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                dialog.dismiss();
            }
        });
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void replaceFragment(Fragment selectedFragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, selectedFragment).commit();
    }

    @Override
    public void onBackPressed() {
        if (binding.btnEnquiry.isActivated() || binding.btnNotification.isActivated()) {

            binding.btnEnquiry.setActivated(false);
            binding.btnNotification.setActivated(false);

            replaceFragment(new DashboardFragment());

            binding.ivNotification.setColorFilter(getResources().getColor(R.color.white));
            binding.tvNotification.setTextColor(getResources().getColor(R.color.white));

            binding.ivLogout.setColorFilter(getResources().getColor(R.color.white));
            binding.tvLogout.setTextColor(getResources().getColor(R.color.white));

            binding.ivEnquiry.setColorFilter(getResources().getColor(R.color.white));
            binding.tvEnquiry.setTextColor(getResources().getColor(R.color.white));

            binding.ivHome.setColorFilter(getResources().getColor(R.color.secondary_primary));
            binding.tvHome.setTextColor(getResources().getColor(R.color.secondary_primary));
        } else if (IntentHelper.IS_EDIT_CLICK) {
            IntentHelper.IS_EDIT_CLICK = false;
            replaceFragment(new EnquiryFragment());
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(MainActivity.this, "Press back again to exit", Toast.LENGTH_SHORT).show();

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 1000);
        }

    }

}