package com.tindo.carshareapp;


public class Constants {
    public static Object RequestCode;

    public static final int LOCATION_REQUEST = 1000;
    public static final int GPS_REQUEST = 1001;
    public static class Transportation{
        public static class Type {
            public static final String carType = "car";
            public static final String bikeType = "bike";
        }

        public static class UnitPrice {
            public static final double carType = 100.0;
            public static final double bikeType = 80.0;
        }
    }

    //Fields of FireStore 'users' collection
    public static class FSUser {
        public static final String userCollection = "users";
        public static final String usernameField = "username";
        public static final String phoneField = "phone";
        public static final String birthDateField = "birthDate";
        public static final String genderField = "gender";
        public static final String emailField = "email";
        public static final String roleField = "role";
        public static final String transportationType = "transportationType";
        public static final String vehiclePlateNumber = "vehiclePlateNumber";
        public static final String rating = "rating";
        public static final String currentPositionLatitude = "currentPositionLatitude";
        public static final String currentPositionLongitude = "currentPositionLongitude";


        public static final String roleCustomerVal = "Customer";
        public static final String roleDriverVal = "Driver";
        public static String countryCodeField = "countryCode";
    }


}
