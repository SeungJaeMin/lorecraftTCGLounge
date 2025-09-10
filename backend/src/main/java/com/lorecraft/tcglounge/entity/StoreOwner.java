package com.lorecraft.tcglounge.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "store_owners")
@DiscriminatorValue("STORE_OWNER")
public class StoreOwner extends User {
    
    @Column(name = "store_name")
    private String storeName;
    
    @Column(name = "store_location")
    private String storeLocation;
    
    @Column(name = "store_zipcode")
    private String storeZipcode;
    
    @Column(name = "business_license")
    private String businessLicense;
    
    @Column(name = "contact_number")
    private String contactNumber;
    
    public StoreOwner() {}
    
    public StoreOwner(String userid, String password, String nickname, String email) {
        super(userid, password, nickname, email);
    }
    
    // Getters
    public String getStoreName() { return storeName; }
    public String getStoreLocation() { return storeLocation; }
    public String getStoreZipcode() { return storeZipcode; }
    public String getBusinessLicense() { return businessLicense; }
    public String getContactNumber() { return contactNumber; }
    
    // Setters
    public void setStoreName(String storeName) { this.storeName = storeName; }
    public void setStoreLocation(String storeLocation) { this.storeLocation = storeLocation; }
    public void setStoreZipcode(String storeZipcode) { this.storeZipcode = storeZipcode; }
    public void setBusinessLicense(String businessLicense) { this.businessLicense = businessLicense; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
}