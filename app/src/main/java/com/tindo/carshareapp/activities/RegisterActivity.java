package com.tindo.carshareapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.hbb20.CountryCodePicker;
import com.tindo.carshareapp.Constants;
import com.tindo.carshareapp.R;

import java.util.Calendar;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {
    EditText  usernameEditText,registerVehiclePlateNumberEditText;
    Button backBtn, nextBtn;
    RadioGroup roleGroup;
    RadioButton driverRadioBtn, customerRadioBtn;
    RadioGroup transportationTypeGroup;
    RadioButton registerCarRadioBtn, registerBikeRadioBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });



        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        linkViewElements();

        setRoleGroupBtnActionHandler();
        setBackBtnAction();
        setNextBtnAction();


    }

    //Get View variables from xml id
    private void linkViewElements() {

        usernameEditText = (EditText) findViewById(R.id.registerUsernameEditText);
        backBtn = (Button) findViewById(R.id.registerBackBtn);
        nextBtn = (Button) findViewById(R.id.registerFinalRegisterBtn);
        roleGroup = (RadioGroup) findViewById(R.id.roleGroup);
        customerRadioBtn = (RadioButton) findViewById(R.id.registerCustomerRadioBtn);
        driverRadioBtn = (RadioButton) findViewById(R.id.registerDriverRadioBtn);
        transportationTypeGroup = (RadioGroup) findViewById(R.id.transportationTypeGroup);
        registerCarRadioBtn = (RadioButton) findViewById(R.id.registerCarRadioBtn);
        registerBikeRadioBtn = (RadioButton) findViewById(R.id.registerBikeRadioBtn);
        registerVehiclePlateNumberEditText = (EditText) findViewById(R.id.registerVehiclePlateNumberEditText);
    }

    private void setRoleGroupBtnActionHandler() {
        roleGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId)
                {
                    case R.id.registerCustomerRadioBtn:
                        transportationTypeGroup.setVisibility(View.INVISIBLE);
                        registerVehiclePlateNumberEditText.setVisibility(View.INVISIBLE);
                        break;

                    case R.id.registerDriverRadioBtn:
                        transportationTypeGroup.setVisibility(View.VISIBLE);
                        registerVehiclePlateNumberEditText.setVisibility(View.VISIBLE);
                        break;

                }
            }
        });
    }

    //Move back to startActivity when pressing 'back' button
    private void setBackBtnAction() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegisterActivity.this, OtpActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    //Move to RegisterFinalActivity when pressing 'next', also passing inputted data of user
    private void setNextBtnAction() {
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String role = customerRadioBtn.isChecked() ? "Customer" : "Driver";
                String transportationType = "";
                if (driverRadioBtn.isChecked()) {
                    transportationType = registerCarRadioBtn.isChecked() ? "car" : "bike";
                }
                String vehiclePlateNumber = registerVehiclePlateNumberEditText.getText().toString();

                //Check empty input

                if (checkEmptyInput(username, role)) {
                    Toast.makeText(RegisterActivity.this, Constants.ToastMessage.emptyInputError, Toast.LENGTH_SHORT).show();

                } else {
                    //Transfer data
                    Intent i = new Intent(RegisterActivity.this, RegisterFinalActivity.class);
                    i.putExtra(Constants.FSUser.usernameField, username);
                    i.putExtra(Constants.FSUser.roleField, role);
                    i.putExtra(Constants.FSUser.transportationType, transportationType);
                    i.putExtra(Constants.FSUser.vehiclePlateNumber, vehiclePlateNumber);
                    startActivity(i);
                    finish();
                }
            }
        });
    }

    //Check if one of the input is empty
    private boolean checkEmptyInput(String username, String phone) {
        return username.isEmpty() || phone.isEmpty();
    }

    //date picker dialog for birthday




}
