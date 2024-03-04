package com.tindo.carshareapp.ui.user_profile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.tindo.carshareapp.model.User;
import com.tindo.carshareapp.Constants;
import com.tindo.carshareapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UserProfileFragment extends Fragment {

    private UserProfileViewModel mViewModel;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private StorageReference mStorageRef;
    User currentUserObject = null;


    public static UserProfileFragment newInstance() {
        return new UserProfileFragment();
    }

    private ImageView profileImgView;
    private EditText nameEditText;
    private EditText emailEditText;
    private Button updateBtn;
    private Button pickerBtn;
    private Button changePassBtn;



    private void linkViewElements(View rootView) {
        profileImgView = rootView.findViewById(R.id.image_userAva);
        nameEditText = rootView.findViewById(R.id.editText_name);
        emailEditText = rootView.findViewById(R.id.editText_email);
        updateBtn = rootView.findViewById(R.id.btn_updateProfle);
        pickerBtn = rootView.findViewById(R.id.btn_pickDate);
        changePassBtn = rootView.findViewById(R.id.btn_changePass);
    }

    //date picker dialog for birthday





            public void afterTextChanged(Editable s) {
            }



    /**
     * Send intent to open photo gallery
     */
    private void handleProfileImageClick() {
        profileImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if(resultCode == Activity.RESULT_OK) {
                Uri imgUri = data.getData();
                profileImgView.setImageURI(imgUri);
                uploadImageFirebase(imgUri);
            }
        }
    }

    /**
     * Get profile image from firebase
     */
    private void setImageFromFirebase() {
        StorageReference fref = mStorageRef.child("profileImages").child(currentUserObject.getDocId()+".jpeg");

        fref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImgView);            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });
    }

    /**
     * Upload profile image
     * @param uri
     */
    private void uploadImageFirebase(Uri uri) {
        StorageReference fref = mStorageRef.child("profileImages").child(currentUserObject.getDocId()+".jpeg");
        fref.putFile(uri).addOnSuccessListener(
                new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("USER PROFILE", "onSuccess: upload success");
                Toast.makeText(getActivity(), "Image Uploaded", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("USER PROFILE", "onFalure: upload failed" + e.getMessage());

                    }
                }
        );
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        currentUser = mAuth.getCurrentUser();

        linkViewElements(view);
        return view;
    }

    /**
     * Render user info to view
     */
    private void renderUserDetails() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");

        nameEditText.setText(currentUserObject.getUsername());
        emailEditText.setText(currentUserObject.getEmail());

    }


    /**
     * Set change password button event listener
     */
    private void setChangePasswordBtnHandler() {
        changePassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.sendPasswordResetEmail(Objects.requireNonNull(currentUser.getEmail()))
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Please check your email to receive further instruction", Toast.LENGTH_LONG).show();
                                    Log.d("USER PROFILE", "Email sent.");
                                }
                            }
                        });
            }
        });
    }

    /**
     * set update button event handler
     */
    private void setUpdateBtnHandler() {
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Map<String, Object> userData = new HashMap<>();
                userData.put(Constants.FSUser.usernameField, nameEditText.getText().toString());


                db.collection(Constants.FSUser.userCollection).document(currentUserObject.getDocId())
                        .update(userData)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    Toast.makeText(getActivity().getApplicationContext(),
                                            "User profile updates",
                                            Toast.LENGTH_LONG).show();
                                } else {

                                }
                            }
                        });
            }
        });

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        setImageFromFirebase();
        handleProfileImageClick();
        setChangePasswordBtnHandler();
        setUpdateBtnHandler();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(requireActivity()).get(UserProfileViewModel.class);
        mViewModel.getCurrentUserObject().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                currentUserObject = user;
                renderUserDetails();
                setImageFromFirebase();
            }
        });
    }
}
