package com.tindo.carshareapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;
import com.tindo.carshareapp.Constants;
import com.tindo.carshareapp.R;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class RegisterFinalActivity extends AppCompatActivity {

    Button backBtn, registerBtn;
    EditText emailEditText, passwordEditText;

    private String username, role, transportationType, vehiclePlateNumber;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_final);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        TextView termsPrivacyTextView = findViewById(R.id.termsPrivacyTextView);




        termsPrivacyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Open privacy policy link
                Intent privacyIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.freeprivacypolicy.com/live/ddc902b0-e3eb-4109-af72-91c6b738b3c4"));
                startActivity(privacyIntent);
            }
        });


        linkViewElements();
        getPreviousRegisterFormInfo();
        setBackBtnAction();
        setRegisterBtnAction();


        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });


        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    //Get View variables from xml id
    private void linkViewElements() {
        backBtn = (Button) findViewById(R.id.registerFinalBackBtn);
        registerBtn = (Button) findViewById(R.id.registerFinalRegisterBtn);
        emailEditText = (EditText) findViewById(R.id.registerFinalEmailEditText);
        passwordEditText = (EditText) findViewById(R.id.registerFinalPasswordEditText);

    }

    //Go back to RegisterActivity when pressing 'back' button


    private void setBackBtnAction() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegisterFinalActivity.this, RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    //Get user input data from previous register activity
    private void getPreviousRegisterFormInfo() {
        Intent intent = getIntent();
        username = (String) intent.getExtras().get(Constants.FSUser.usernameField);
        role = (String) intent.getExtras().get(Constants.FSUser.roleField);
        transportationType = (String) intent.getExtras().get(Constants.FSUser.transportationType);
        vehiclePlateNumber = (String) intent.getExtras().get(Constants.FSUser.vehiclePlateNumber);
    }

    //Save user data to 'users' collection on firebase
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveUserInfo() throws ParseException {


        //Create data hashmap to push to FireStore db
        Map<String, Object> data = new HashMap<>();
        data.put(Constants.FSUser.usernameField, username);
        data.put(Constants.FSUser.emailField, emailEditText.getText().toString());
        data.put(Constants.FSUser.roleField, role);
        data.put(Constants.FSUser.transportationType, transportationType);
        data.put(Constants.FSUser.vehiclePlateNumber, vehiclePlateNumber);
        data.put(Constants.FSUser.currentPositionLatitude, 0.0);
        data.put(Constants.FSUser.currentPositionLongitude, 0.0);
//        data.put(Constants.FSUser.rating, 5.0);
        ArrayList<Integer> rating = new ArrayList<>();
        rating.add(5);
        data.put(Constants.FSUser.rating, rating);

        db.collection(Constants.FSUser.userCollection).add(data);
    }

    //Redirect to LoginActivity
    private void moveToLoginActivity() {
        Intent i = new Intent(RegisterFinalActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }




    //
    private void setRegisterBtnAction() {
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                //Check if username and password fields are empty

                //Call FireStoreAuth for authentication process
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterFinalActivity.this, new OnCompleteListener<AuthResult>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) { //If successful
                            Toast.makeText(RegisterFinalActivity.this, Constants.ToastMessage.registerSuccess, Toast.LENGTH_SHORT).show();
                            try {
                                saveUserInfo();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            moveToLoginActivity(); // go to login activity
                        } else {
                            Toast.makeText(RegisterFinalActivity.this, Constants.ToastMessage.registerFailure,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

}