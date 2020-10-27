package com.locator.stationcontroller.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity
public class Station {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo
    private String name;

    @ColumnInfo
    private String voltage;

    @ColumnInfo
    private boolean r1;

    @ColumnInfo
    private boolean r2;

    @ColumnInfo
    private String phone1;

    @ColumnInfo
    private String phone2;

    @ColumnInfo
    private String sms1;

    @ColumnInfo
    private String sms2;

    @ColumnInfo
    private String sms3;

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

    public String getVoltage() {
        return voltage;
    }

    public void setVoltage(String voltage) {
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

    public String getPhone1() {
        return phone1;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
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
}
