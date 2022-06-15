package kr.ac.kpu.diyequipmentapplication.menu;

import java.io.Serializable;

public class LocationDB implements Serializable {
    private String locationEmail;
    private String location;

    public LocationDB() {
    }

    public LocationDB(String locationEmail, String location) {
        this.locationEmail = locationEmail;
        this.location = location;
    }

    public String getLocationEmail() {
        return locationEmail;
    }

    public void setLocationEmail(String locationEmail) {
        this.locationEmail = locationEmail;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
