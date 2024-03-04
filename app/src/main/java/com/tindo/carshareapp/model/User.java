package com.tindo.carshareapp.model;

import com.google.firebase.firestore.DocumentId;

import java.util.Date;
import java.util.List;

/**
 * Data model for user
 */
public class User {

    @DocumentId
    private String docId;
    private String username;
    private String phone;
    private Date birthDate;
    private String gender;
    private String email;
    private String role;
    private String transportationType;
    private String vehiclePlateNumber;
    private List<Integer> rating;
    private Double currentPositionLatitude;
    private Double currentPositionLongitude;


    public User() {
    }

    public User(String docId, String username, String phone, Date birthDate, String gender, String email, String role, String transportationType, String vehiclePlateNumber, List<Integer> rating, Double currentPositionLatitude, Double currentPositionLongitude) {
        this.docId = docId;
        this.username = username;
        this.phone = phone;
        this.birthDate = birthDate;
        this.gender = gender;
        this.email = email;
        this.role = role;
        this.transportationType = transportationType;
        this.vehiclePlateNumber = vehiclePlateNumber;
        this.rating = rating;
        this.currentPositionLatitude = currentPositionLatitude;
        this.currentPositionLongitude = currentPositionLongitude;
    }

    public Double getCurrentPositionLatitude() {
        return currentPositionLatitude;
    }

    public void setCurrentPositionLatitude(Double currentPositionLatitude) {
        this.currentPositionLatitude = currentPositionLatitude;
    }

    public Double getCurrentPositionLongitude() {
        return currentPositionLongitude;
    }

    public void setCurrentPositionLongitude(Double currentPositionLongitude) {
        this.currentPositionLongitude = currentPositionLongitude;
    }

//    public boolean isCustomer(){
//        return this.role.equals("Customer");
//    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getTransportationType() {
        return transportationType;
    }

    public void setTransportationType(String transportationType) {
        this.transportationType = transportationType;
    }

    public void setRating(List<Integer> rating) {
        this.rating = rating;
    }

    public String getVehiclePlateNumber() {
        return vehiclePlateNumber;
    }

    public void setVehiclePlateNumber(String vehiclePlateNumber) {
        this.vehiclePlateNumber = vehiclePlateNumber;
    }

    public List<Integer> getRating() {
        return rating;
    }

    public float getRatingAverage(){
        double total = 0;
        for (int _rating : this.rating){
            total += _rating;
        }
        return (float) (total / this.rating.size());
    }

    public void addNewRating(int newRating) {
        this.rating.add(newRating);
   }
}
