package com.pratik.agricole.models;

public class FarmModel {
    String farmname , farmsize , soil_moisture,planting_date , gdd, erz;
    String farmnumber , farmimage;



    public FarmModel(String farmname, String farmsize, String farmnumber, String farmimage) {
        this.farmname = farmname;
        this.farmsize = farmsize;
        this.farmnumber = farmnumber;
        this.farmimage = farmimage;
    }

    public String getFarmnumber() {
        return farmnumber;
    }

    public void setFarmnumber(String farmnumber) {
        this.farmnumber = farmnumber;
    }

    public FarmModel(String farmname, String farmsize, String soil_moisture, String planting_date, String gdd, String erz ,String farmimage) {
        this.farmname = farmname;
        this.farmsize = farmsize;
        this.soil_moisture = soil_moisture;
        this.planting_date = planting_date;
        this.gdd = gdd;
        this.erz = erz;
        this.farmimage = farmimage;
    }

    public String getFarmimage() {
        return farmimage;
    }

    public void setFarmimage(String farmimage) {
        this.farmimage = farmimage;
    }

    public String getFarmname() {
        return farmname;
    }

    public void setFarmname(String farmname) {
        this.farmname = farmname;
    }

    public String getFarmsize() {
        return farmsize;
    }

    public void setFarmsize(String farmsize) {
        this.farmsize = farmsize;
    }

    public String getSoil_moisture() {
        return soil_moisture;
    }

    public void setSoil_moisture(String soil_moisture) {
        this.soil_moisture = soil_moisture;
    }

    public String getPlanting_date() {
        return planting_date;
    }

    public void setPlanting_date(String planting_date) {
        this.planting_date = planting_date;
    }

    public String getGdd() {
        return gdd;
    }

    public void setGdd(String gdd) {
        this.gdd = gdd;
    }

    public String getErz() {
        return erz;
    }

    public void setErz(String erz) {
        this.erz = erz;
    }
}
