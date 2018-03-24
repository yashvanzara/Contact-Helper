package com.yashvanzara.www.contacthelper;

import java.io.Serializable;

/**
 * Created by User on 18-03-2018.
 */

public class Contact implements Serializable{

    private String contactNumber;
    private String contactName;
    private String contactNickName;
    private double contactLatitude;
    private double contactLongtitude;

    public String getContactNickName() {
        return contactNickName;
    }

    public void setContactNickName(String contactNickName) {
        this.contactNickName = contactNickName;
    }

    public Contact(String contactNumber, String contactName, String contactNickName, double contactLatitude, double contactLongtitude) {
        this.contactNumber = contactNumber;
        this.contactName = contactName;
        this.contactNickName = contactNickName;
        this.contactLatitude = contactLatitude;
        this.contactLongtitude = contactLongtitude;
    }

    public double getContactLatitude() {
        return contactLatitude;
    }

    public void setContactLatitude(double contactLatitude) {
        this.contactLatitude = contactLatitude;
    }

    public double getContactLongtitude() {
        return contactLongtitude;
    }

    public void setContactLongtitude(double contactLongtitude) {
        this.contactLongtitude = contactLongtitude;
    }

    public Contact(){

    }

    @Override
    public String toString() {
        return "Contact{" +
                "contactNumber='" + contactNumber + '\'' +
                ", contactName='" + contactName + '\'' +
                ", contactNickName='" + contactNickName + '\'' +
                ", contactLatitude=" + contactLatitude +
                ", contactLongtitude=" + contactLongtitude +
                '}';
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

}
