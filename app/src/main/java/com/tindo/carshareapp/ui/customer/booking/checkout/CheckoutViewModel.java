package com.tindo.carshareapp.ui.customer.booking.checkout;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tindo.carshareapp.model.User;

/**
 * View model for CheckoutFragment
 */
public class CheckoutViewModel extends ViewModel {
    private MutableLiveData<User> currentUserObject;
    private MutableLiveData<String> transportationType;
    private MutableLiveData<String> distanceInKmString;
    private MutableLiveData<String> priceInVNDString;

    public CheckoutViewModel() {
        currentUserObject = new MutableLiveData<>();
        transportationType = new MutableLiveData<>();
        distanceInKmString = new MutableLiveData<>();
        priceInVNDString = new MutableLiveData<>();
    }

    public void setCurrentUserObject(User currentUserObject) {
        this.currentUserObject.setValue(currentUserObject);
    }

    public void setTransportationType(String transportationType) {
        this.transportationType.setValue(transportationType);
    }

    public void setPriceInVNDString(String priceInVNDString) {
        this.priceInVNDString.setValue(priceInVNDString);
    }

    public void setDistanceInKmString(String distanceInKmString) {
        this.distanceInKmString.setValue(distanceInKmString);
    }

    public MutableLiveData<String> getDistanceInKmString() {
        return distanceInKmString;
    }

    public MutableLiveData<String> getPriceInVNDString() {
        return priceInVNDString;
    }

    public MutableLiveData<User> getCurrentUserObject(){
        return this.currentUserObject;
    }

    public MutableLiveData<String> getTransportationType() {
        return transportationType;
    }
}