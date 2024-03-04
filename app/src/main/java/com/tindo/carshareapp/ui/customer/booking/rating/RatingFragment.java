package com.tindo.carshareapp.ui.customer.booking.rating;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.tindo.carshareapp.model.User;
import com.tindo.carshareapp.ui.customer.booking.BookingViewModel;
import com.tindo.carshareapp.Constants;
import com.tindo.carshareapp.R;

import java.util.Locale;

public class RatingFragment extends DialogFragment {

    private RatingViewModel mViewModel;
    private RatingBar mRatingBar;
    private TextView mRatingScale;
    private Button mSendFeedback;
    private EditText mFeedback;
    private ImageView profileImage;

    //Firestore instances
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private StorageReference mStorageRef;

    private User driver;


    public static RatingFragment newInstance() {
        return new RatingFragment();
    }

    /**
     * Link view elements from xml file
     * @param view
     */
    private void linkViewElements(View view) {
        mRatingBar = view.findViewById(R.id.rating_bar);
        mRatingScale = view.findViewById(R.id.rating_scale_text_view);
        mFeedback = view.findViewById(R.id.feed_back_edit_text);
        mSendFeedback = view.findViewById(R.id.send_feedback_btn);
        profileImage = view.findViewById(R.id.profile_avatar);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_rating, container, false);
        linkViewElements(view);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        mFeedback.setText("");
        mRatingBar.setRating(0);

        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                mRatingScale.setText(String.valueOf(v));
                switch ((int) ratingBar.getRating()) {
                    case 1:
                        mRatingScale.setText("Very bad");
                        break;
                    case 2:
                        mRatingScale.setText("Need some improvement");
                        break;
                    case 3:
                        mRatingScale.setText("Good");
                        break;
                    case 4:
                        mRatingScale.setText("Great");
                        break;
                    case 5:
                        mRatingScale.setText("Awesome");
                        break;
                    default:
                        mRatingScale.setText("");
                }
            }
        });

        mSendFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Thank you for sharing your feedback", Toast.LENGTH_SHORT).show();
                BookingViewModel bookingViewModel = ViewModelProviders.of(requireActivity()).get(BookingViewModel.class);
                bookingViewModel.setFeedBackRating((int) mRatingBar.getRating());
                dismiss();
            }
        });
        return view;
    }

    /**
     * Set profile image
     */
    private void setProfileImage(){
        db.collection(Constants.FSUser.userCollection)
                .whereEqualTo(Constants.FSUser.emailField, driver.getEmail())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            User driver = doc.toObject(User.class);

//                            assert driver != null;
                            StorageReference fref = mStorageRef.child("profileImages").child(driver.getDocId() + ".jpeg");

                            fref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Picasso.get().load(uri).into(profileImage);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {

                                }
                            });
                        }
                    }
                });
    }

    @SuppressLint("SetTextI18n")
    private void setDriverInfo(){
        setProfileImage();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel =  ViewModelProviders.of(requireActivity()).get(RatingViewModel.class);
        mViewModel.getDriver().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                driver = user;
                setDriverInfo();
            }
        });
    }

}
