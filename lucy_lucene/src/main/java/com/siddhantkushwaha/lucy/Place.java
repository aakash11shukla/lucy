package com.siddhantkushwaha.lucy;

import com.google.gson.annotations.SerializedName;

public class Place {

    private String name;
    @SerializedName("abstract")
    private String description;
    private String country;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String toString() {
        return getName() + "; " + getCountry();
    }
}