package com.tindo.carshareapp.model.alert;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.tindo.carshareapp.R;
import com.tindo.carshareapp.model.Booking;
import com.tindo.carshareapp.ui.driver.alert.DriverAlertViewModel;
import com.tindo.carshareapp.ui.driver.home.DriverHomeViewModel;

import java.util.Locale;
import java.util.Objects;

public class DriverAlertFragment extends DialogFragment {

    public static String TAG = "DriverAlertDialog";

    private static final String SELECTED_LANGUAGE = "Locale.Helper.Selected.Language";
    private TextView priceText;
    private TextView distanceText;
    private TextView pickUpLocationText;
    private TextView dropOffLocationText;

    private Button declineBtn;
    private Button acceptBtn;

    public static DriverAlertFragment newInstance() {
        return new DriverAlertFragment();
    }

    /**
     * Link view elements
     * @param rootView
     */
    private void linkViewElements(View rootView) {
        priceText = rootView.findViewById(R.id.priceTextView);
        distanceText = rootView.findViewById(R.id.text_distance);
        pickUpLocationText = rootView.findViewById(R.id.text_pickUpLocation);
        dropOffLocationText = rootView.findViewById(R.id.text_dropLocation);
        declineBtn = rootView.findViewById(R.id.btn_decline);
        acceptBtn = rootView.findViewById(R.id.btn_accept);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Locale.getDefault().getLanguage();
        setStyle(DialogFragment.STYLE_NORMAL,
                android.R.style.Theme_DeviceDefault_Dialog);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_driver_alert, container, false);
        linkViewElements(root);
        Objects.requireNonNull(getDialog()).getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DriverHomeViewModel driverHomeViewModel = ViewModelProviders.of(requireActivity()).get(DriverHomeViewModel.class);


        declineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                driverHomeViewModel.setAcceptBookingBtnPressed(false);
                dismiss();
            }
        });

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                driverHomeViewModel.setAcceptBookingBtnPressed(true);
                dismiss();
            }
        });
    }

    /**
     * Set booking detail on layout file
     * @param booking
     */
    private void setBookingDetails(Booking booking){

        if (booking == null) {
            return;
        }

        priceText.setText(booking.getPriceInVND());
        distanceText.setText(booking.getDistanceInKm());
        pickUpLocationText.setText(booking.getPickupPlaceAddress());
        dropOffLocationText.setText(booking.getDropOffPlaceAddress());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        com.tindo.carshareapp.ui.driver.alert.DriverAlertViewModel driverAlertViewModel = ViewModelProviders.of(requireActivity()).get(DriverAlertViewModel.class);
        driverAlertViewModel.getBooking().observe(getViewLifecycleOwner(), new Observer<Booking>() {
            @Override
            public void onChanged(Booking booking) {
                try {
                    setBookingDetails(booking);
                } catch (Exception e) {
                    // Handle error
                    Log.e("DriverAlertFragment", "Error setting booking details", e);
                }
            }
        });
    }

}
