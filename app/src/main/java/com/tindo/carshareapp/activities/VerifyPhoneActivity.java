package com.tindo.carshareapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tindo.carshareapp.R;
import com.tindo.carshareapp.model.User;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class VerifyPhoneActivity extends AppCompatActivity {

    //These are the objects needed
    //It is the verification id that will be sent to the user
    private String mVerificationId;

    //The edittext to input the code
    private PinView pinview;

    //firebase auth object
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);


        //initializing objects
        mAuth = FirebaseAuth.getInstance();
        pinview = findViewById(R.id.pinview);


        //getting mobile number from the previous activity
        //and sending the verification code to the number
        Intent intent = getIntent();
        String mobile = intent.getStringExtra("mobile");


        sendVerificationCode(mobile);


        //if the automatic sms detection did not work, user can also enter the code manually
        //so adding a click listener to the button
        findViewById(R.id.buttonSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = pinview.getText().toString().trim();
                if (code.isEmpty() || code.length() < 6) {
                    pinview.setError("Enter valid code");
                    pinview.requestFocus();
                    return;
                }

                //verifying the code entered manually
                verifyVerificationCode(code);
            }
        });

    }



    private void sendVerificationCode(String mobile ) {

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+237" + mobile)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);


    }


    //the callback to detect the verification status
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
                pinview.setText(code);
                //verifying the code
                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(VerifyPhoneActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            //storing the verification id that is sent to the user
            mVerificationId = s;
        }
    };


    private void verifyVerificationCode(String code) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }



    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential)

    {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)

                    {
                        if (task.isSuccessful()) {
                            // The user has been successfully signed in.
                            Log.d("VerifyPhoneActivity", "User signed in successfully.");

                            // Get the current user
                            FirebaseUser currentUser = mAuth.getCurrentUser();

                            // Retrieve user information from Cloud Firestore
                            if (currentUser != null) {
                                String uid = currentUser.getUid();


                                // Read user data from Cloud Firestore
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                DocumentReference userRef = db.collection("users").document(uid);
                                userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task)

                                    {
                                        if (task.isSuccessful() && task.getResult().exists()) {

                                            Toast.makeText(VerifyPhoneActivity.this, "Verification successful!", Toast.LENGTH_SHORT).show();

                                            // Get user data from the DocumentSnapshot
                                            DocumentSnapshot snapshot = task.getResult();
                                            User currentUserObject = snapshot.toObject(User.class);

                                            // Display user information
                                            if (currentUserObject != null) {
                                                Log.d("VerifyPhoneActivity", "User data retrieved: " + currentUserObject);

                                                // Use the retrieved user data to display user information or perform other actions
                                            }
                                        } else {
                                            Log.e("VerifyPhoneActivity", "Failed to retrieve user data");
                                        }
                                    }
                                });
                            }

                            // Go to the main activity.
                            startActivity(new Intent(VerifyPhoneActivity.this, RegisterActivity.class));
                        } else {
                            // The sign in failed.
                            Log.e("VerifyPhoneActivity", "Sign in failed: ", task.getException());
                            Toast.makeText(VerifyPhoneActivity.this, "Sign in failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



}