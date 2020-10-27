package com.locator.stationcontroller.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Station {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo
    public String name;

    @ColumnInfo
    public String phone1;

    @ColumnInfo
    public String phone2;

    @ColumnInfo
    public String sms1;

    @ColumnInfo
    public String sms2;

    @ColumnInfo
    public String sms3;

    @ColumnInfo
    public float voltage;

    @ColumnInfo
    public boolean r1;

    @ColumnInfo
    public boolean r2;

    @Ignore
    public boolean isEditing;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return id == station.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone1() {
        return phone1;
    }

    public void setPhone1(String phone1) {
        if(phone1.startsWith("0")){
            phone1 = phone1.replace("0","+374");
        }
        this.phone1 = phone1;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        if(phone2.startsWith("0")){
            phone2 = phone2.replace("0","+374");
        }
        this.phone2 = phone2;
    }

    public String getSms1() {
        return sms1;
    }

    public void setSms1(String sms1) {
        this.sms1 = sms1;
    }

    public String getSms2() {
        return sms2;
    }

    public void setSms2(String sms2) {
        this.sms2 = sms2;
    }

    public String getSms3() {
        return sms3;
    }

    public void setSms3(String sms3) {
        this.sms3 = sms3;
    }

    public float getVoltage() {
        return voltage;
    }

    public void setVoltage(float voltage) {
        this.voltage = voltage;
    }

    public boolean isR1() {
        return r1;
    }

    public void setR1(boolean r1) {
        this.r1 = r1;
    }

    public boolean isR2() {
        return r2;
    }

    public void setR2(boolean r2) {
        this.r2 = r2;
    }

    public boolean isEditing() {
        return isEditing;
    }

    public void setEditing(boolean editing) {
        isEditing = editing;
    }

    public Station(String name, String phone1, String phone2, String sms1, String sms2, String sms3) {
        if(phone1.startsWith("0")){
            phone1 = phone1.replace("0","+374");
        }
        if(phone2.startsWith("0")){
            phone2 = phone2.replace("0","+374");
        }
        this.name = name;
        this.phone1 = phone1;
        this.phone2 = phone2;
        this.sms1 = sms1;
        this.sms2 = sms2;
        this.sms3 = sms3;
    }
}
