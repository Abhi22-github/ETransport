package com.serviceapps.transport.adapters;

import com.google.firebase.firestore.GeoPoint;

public class ShipmentDataClass {
    private String receiverName;
    private String receiverNumber;
    private String weight;
    private String weightUnit;
    private String height;
    private String heightUnit;
    private String quantity;
    private String priceRange;
    private String pickupAddress;
    private String dropAddress;
    private GeoPoint pickupLocation;
    private GeoPoint dropLocation;

    public ShipmentDataClass(){

    }

    public ShipmentDataClass(String receiverName, String receiverNumber, String weight,
                             String weightUnit, String height, String heightUnit, String quantity,String priceRange,
                             String pickupAddress, String dropAddress, GeoPoint pickupLocation, GeoPoint dropLocation) {
        this.receiverName = receiverName;
        this.receiverNumber = receiverNumber;
        this.weight = weight;
        this.weightUnit = weightUnit;
        this.height = height;
        this.heightUnit = heightUnit;
        this.quantity = quantity;
        this.pickupAddress = pickupAddress;
        this.dropAddress = dropAddress;
        this.pickupLocation = pickupLocation;
        this.dropLocation = dropLocation;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverNumber() {
        return receiverNumber;
    }

    public void setReceiverNumber(String receiverNumber) {
        this.receiverNumber = receiverNumber;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getWeightUnit() {
        return weightUnit;
    }

    public void setWeightUnit(String weightUnit) {
        this.weightUnit = weightUnit;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getHeightUnit() {
        return heightUnit;
    }

    public void setHeightUnit(String heightUnit) {
        this.heightUnit = heightUnit;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPriceRange() {
        return priceRange;
    }

    public void setPriceRange(String priceRange) {
        this.priceRange = priceRange;
    }

    public String getPickupAddress() {
        return pickupAddress;
    }

    public void setPickupAddress(String pickupAddress) {
        this.pickupAddress = pickupAddress;
    }

    public String getDropAddress() {
        return dropAddress;
    }

    public void setDropAddress(String dropAddress) {
        this.dropAddress = dropAddress;
    }

    public GeoPoint getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(GeoPoint pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public GeoPoint getDropLocation() {
        return dropLocation;
    }

    public void setDropLocation(GeoPoint dropLocation) {
        this.dropLocation = dropLocation;
    }
}
