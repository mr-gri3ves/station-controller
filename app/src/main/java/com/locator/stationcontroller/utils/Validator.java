package com.locator.stationcontroller.utils;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.locator.stationcontroller.Synchronizer;
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

    public static Result validate(String message) {
        if (TextUtils.isEmpty(message)) {
            return new Result(false, "Message is empty");
        }
        final String REGEX_U_BAT = "U_Bat=([0-9]*[.])?[0-9]+";
        final String KEY_OUT1 = "Out1=[0-1]";
        final String KEY_OUT2 = "Out2=[0-1]";

        String[] lines = null;

        if(lines == null) {
            lines = message.split(System.getProperty("line.separator"));
        }

        if (lines.length != 3) {
            return new Result(false, "Invalid data");
        }

        return lines[0].matches(REGEX_U_BAT) && lines[1].matches(KEY_OUT1) && lines[2].matches(KEY_OUT2)
                ? new Result(true, null)
                : new Result(false, "Not valid");
    }

    public static class Result {
        private boolean success;
        private String message;

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
