package com.tindo.carshareapp.ui.customer.booking.rating;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tindo.carshareapp.model.User;

public class RatingViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private MutableLiveData<User> driver;

    public RatingViewModel() {
        driver = new MutableLiveData<>();
    }

    public void setDriver(User driver) {
        this.driver.setValue(driver);
    }

    public MutableLiveData<User> getDriver() {
        return driver;
    }
}