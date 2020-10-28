package com.locator.stationcontroller.utils;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.locator.stationcontroller.db.Station;


public class Validator {
    public static Result validate(@Nullable Station station) {
        if (station == null) {
            return new Result(false, "Station can't be null");
        }
        if (TextUtils.isEmpty(station.getName())) {
            return new Result(false, "Name is required");
        }
        if (TextUtils.isEmpty(station.getPhone1())) {
            return new Result(false, "Phone1 is required");
        }
        if (TextUtils.isEmpty(station.getPhone2())) {
            return new Result(false, "Phone2 is required");
        }
        if (TextUtils.isEmpty(station.getSms1())) {
            return new Result(false, "Sms 1 is required");
        }
        if (TextUtils.isEmpty(station.getSms2())) {
            return new Result(false, "Sms 2 is required");
        }
        if (TextUtils.isEmpty(station.getSms3())) {
            return new Result(false, "Sms 3 is required");
        }
        return new Result(true, "");
    }

    public static class Result {
        private final boolean success;
        private final String message;

        public Result(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }
}
