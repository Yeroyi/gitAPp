package com.jbufa.firefighting.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class CotactPerson implements Serializable {
    private String linkManName;
    private String linkManMobile;
    public CotactPerson(){

    }


    public String getName() {
        return linkManName;
    }

    public void setName(String name) {
        this.linkManName = name;
    }

    public String getPhone() {
        return linkManMobile;
    }

    public void setPhone(String phone) {
        this.linkManMobile = phone;
    }

}
