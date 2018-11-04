package com.siddhantkushwaha.lucy;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Place {

    private String name;
    @SerializedName("abstract")
    private String description;
    private int population;
    private String latlng;
    private ArrayList<String> isPartOf;
    private String utcOffset;
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

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public String getLatlng() {
        return latlng;
    }

    public void setLatlng(String latlng) {
        this.latlng = latlng;
    }

    public ArrayList<String> getIsPartOf() {
        return isPartOf;
    }

    public void setIsPartOf(ArrayList<String> isPartOf) {
        this.isPartOf = isPartOf;
    }

    public String getUtcOffset() {
        return utcOffset;
    }

    public void setUtcOffset(String utcOffset) {
        this.utcOffset = utcOffset;
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