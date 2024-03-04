package com.tindo.carshareapp.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Menu;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tindo.carshareapp.model.User;
import com.tindo.carshareapp.Constants;
import com.tindo.carshareapp.R;
import com.tindo.carshareapp.ui.customer.booking.BookingViewModel;
import com.tindo.carshareapp.ui.customer.booking.checkout.CheckoutViewModel;
import com.tindo.carshareapp.ui.customer.booking.dropoff.DropoffViewModel;
import com.tindo.carshareapp.ui.customer.booking.pickup.PickupViewModel;
import com.tindo.carshareapp.ui.customer.home.CustomerHomeViewModel;
import com.tindo.carshareapp.ui.driver.home.DriverHomeViewModel;
import com.tindo.carshareapp.ui.user_profile.UserProfileViewModel;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    private TextView navHeaderEmailTextView;
    private TextView navHeaderUsernameTextView;

    //Firebase, FireStore
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    private FusedLocationProviderClient mFusedLocationClient;

    private double wayLatitude = 0.0, wayLongitude = 0.0;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private android.widget.Button btnLocation;
    private TextView txtLocation;
    private android.widget.Button btnContinueLocation;
    private TextView txtContinueLocation;
    private StringBuilder stringBuilder;

    private boolean isContinue = false;
    private boolean isGPS = false;

    //Current user in
    User currentUserObject = null;

    //View models
    CustomerHomeViewModel customerHomeViewModel;
    DriverHomeViewModel driverHomeViewModel;
    DropoffViewModel dropoffViewModel;
    PickupViewModel pickupViewModel;
    BookingViewModel bookingViewModel;
    CheckoutViewModel checkoutViewModel;
    UserProfileViewModel userProfileViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //setup navigation drawer


        linkViewElements(); //Get view elements
        initAllChildFragmentsViewModel(); //Init all child fragments viewModels
        initFirebaseCurrentUserInfo();

        //Get all fireStore instances


        loadLocale();


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000); // 10 seconds

        locationRequest.setFastestInterval(5 * 1000); // 5 seconds

        new GpsUtils(this).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                // turn on GPS
                isGPS = isGPSEnable;
            }
        });

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();
                        if (!isContinue) {
                            txtLocation.setText(String.format(Locale.US, "%s - %s", wayLatitude, wayLongitude));
                        } else {
                            stringBuilder.append(wayLatitude);
                            stringBuilder.append("-");
                            stringBuilder.append(wayLongitude);
                            stringBuilder.append("\n\n");
                            txtContinueLocation.setText(stringBuilder.toString());
                        }
                        if (!isContinue && mFusedLocationClient != null) {
                            mFusedLocationClient.removeLocationUpdates(locationCallback);
                        }
                    }
                }
            }
        };


    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    Constants.LOCATION_REQUEST);

        } else {
            if (isContinue) {
                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            } else {
                mFusedLocationClient.getLastLocation().addOnSuccessListener(MainActivity.this, location -> {
                    if (location != null) {
                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();
                        txtLocation.setText(String.format(Locale.US, "%s - %s", wayLatitude, wayLongitude));
                    } else {
                        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                    }
                });
            }
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (isContinue) {
                        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                    } else {
                        mFusedLocationClient.getLastLocation().addOnSuccessListener(MainActivity.this, location -> {
                            if (location != null) {
                                wayLatitude = location.getLatitude();
                                wayLongitude = location.getLongitude();
                                txtLocation.setText(String.format(Locale.US, "%s - %s", wayLatitude, wayLongitude));
                            } else {
                                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                            }
                        });
                    }
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.GPS_REQUEST) {
                isGPS = true; // flag maintain before get location
            }
        }
    }
    /**
     * Connect view elements of layout to this class variable
     */
    private void linkViewElements() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        LinearLayout navHeaderView = (LinearLayout) navigationView.getHeaderView(0);
        navHeaderUsernameTextView = (TextView) navHeaderView.getChildAt(1);
        navHeaderEmailTextView = (TextView) navHeaderView.getChildAt(2);
    }

    /**
     * Set up navigation drawer activity
     */
    private void navigationDrawerSetup() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_customer_home,
                R.id.nav_driver_home,
                R.id.nav_profile)
                .setDrawerLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigateAndHideAccordingMenuBasedOnRole(navController);
    }

    /**
     * Logout menu item listener (sits in 3-dots collapsing menu)
     */
    private void onLogoutOptionClick() {
        mAuth.signOut();
        Intent i = new Intent(MainActivity.this, OtpActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_logout:
                onLogoutOptionClick();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    /**
     * Get instances of Firebase FireStore Auth, db, current user
     */
    private void initFirebaseCurrentUserInfo() {
        //Get instances of Firebase FireStore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
        getCurrentUserObject(); //Get current user object info
    }

    /**
     * Get current user object from FireStore
     */
    private void getCurrentUserObject() {
        db.collection(Constants.FSUser.userCollection)
                .whereEqualTo(Constants.FSUser.emailField, currentUser.getEmail())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            currentUserObject = doc.toObject(User.class);
                            setNavHeaderEmailAndUsername(); //Set nav header username and email
                            setAllChildFragmentsViewModelData();
                            navigationDrawerSetup();
                        }
                    }
                });
//                .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                        for (QueryDocumentSnapshot doc : value) {
//                            currentUserObject = doc.toObject(User.class);
//                            setNavHeaderEmailAndUsername(); //Set nav header username and email
//                            setAllChildFragmentsViewModelData();
//                            navigationDrawerSetup();
//                        }
//                    }
//                });
    }

    /**
     * navigate to the right home and remove according home menu based on user role
     * @param navController navController object of this layout
     */
    private void navigateAndHideAccordingMenuBasedOnRole(NavController navController){
        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();

        //Hide according menu and Navigate to the right fragment based on
        if (currentUserObject.getRole().equals("Customer")){
            MenuItem driverHomeMenuItem = menu.getItem(1);
            driverHomeMenuItem.setVisible(false);
            navController.navigate(R.id.nav_customer_home);
        } else {
            MenuItem customerHomeMenuItem = menu.getItem(0);
            customerHomeMenuItem.setVisible(false);
            navController.navigate(R.id.nav_driver_home);
        }
    }

    private void setLocale(String lang) {

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Configuration config = new Configuration();

        config.locale = locale;

        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());


        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My Language", lang);
        editor.apply();

    }




    public void loadLocale()  {


        SharedPreferences prefs =  getSharedPreferences("Settings", Activity.MODE_PRIVATE);

        String language = prefs.getString("My Language","");

        setLocale(language);
    }



    /**
     * Init all child fragments' view models
     */
    private void initAllChildFragmentsViewModel() {
        customerHomeViewModel = ViewModelProviders.of(this).get(CustomerHomeViewModel.class);
        driverHomeViewModel = ViewModelProviders.of(this).get(DriverHomeViewModel.class);
        dropoffViewModel = ViewModelProviders.of(this).get(DropoffViewModel.class);
        pickupViewModel = ViewModelProviders.of(this).get(PickupViewModel.class);
        bookingViewModel = ViewModelProviders.of(this).get(BookingViewModel.class);
        checkoutViewModel = ViewModelProviders.of(this).get(CheckoutViewModel.class);
        userProfileViewModel = ViewModelProviders.of(this).get(UserProfileViewModel.class);
    }

    /**
     * Set nav header username and email
     */
    private void setNavHeaderEmailAndUsername() {
        navHeaderEmailTextView.setText(currentUser.getEmail());
        navHeaderUsernameTextView.setText(currentUserObject.getUsername());
    }

    /**
     * Send current user data through child fragments' view models
     */
    private void setAllChildFragmentsViewModelData() {
        if (currentUserObject.getRole().equals("Customer")){
            customerHomeViewModel.setCurrentUserObject(currentUserObject);
        } else {
            driverHomeViewModel.setCurrentUserObject(currentUserObject);
        }
        dropoffViewModel.setCurrentUserObject(currentUserObject);
        pickupViewModel.setCurrentUserObject(currentUserObject);
        bookingViewModel.setCurrentUserObject(currentUserObject);
        userProfileViewModel.setCurrentUserObject(currentUserObject);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}