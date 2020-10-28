package com.locator.stationcontroller.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.locator.stationcontroller.db.Station;
import com.locator.stationcontroller.utils.Validator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Synchronizer {

    public static List<Station> syncStations(@NonNull Context context, @NonNull List<Station> stations) {
        if (stations.isEmpty()) {
            return stations;
        }
        List<Station> syncedStations = new ArrayList<>();
        for (Station station : stations) {
            syncedStations.add(syncStation(context, station));
        }
        return syncedStations;
    }

    public static Station syncStation(@NonNull Context context, @NonNull Station station) {
        Set<String> phoneSet = new HashSet<>();
        phoneSet.add(station.getPhone1());
        phoneSet.add(station.getPhone2());
        List<Sms> smsList = getSms(context, phoneSet);
        if (smsList.isEmpty()) {
            return station;
        }
        List<ProcessedSms> processedSmsList = ProcessedSms.process(smsList);
        ProcessedSms processedSmsPhone1 = null;
        ProcessedSms processedSmsPhone2 = null;

        for (ProcessedSms processedSms : processedSmsList) {
            if (processedSmsPhone1 == null && station.getPhone1().equals(processedSms.phone)) {
                processedSmsPhone1 = processedSms;
            }
            if (processedSmsPhone2 == null && station.getPhone2().equals(processedSms.phone)) {
                processedSmsPhone2 = processedSms;
            }
            if (processedSmsPhone1 != null && processedSmsPhone2 != null) {
                break;
            }
        }

        ProcessedSms fresherSms = ProcessedSms.getFresher(processedSmsPhone1, processedSmsPhone2);
        if (fresherSms == null || !fresherSms.hasUpdateFor(station)) {
            return station;
        }

        return fresherSms.apply(station);
    }

    public static List<Station> syncStationsWithSms(@NonNull List<Station> stations, @NonNull String phone,
                                                    @NonNull String message, long date) {
        List<Station> syncedStations = new ArrayList<>();
        Sms sms = new Sms(phone, message, date);
        ProcessedSms processedSms = ProcessedSms.process(sms);
        if (processedSms == null) {
            return syncedStations;
        }
        for (Station station : stations) {
            if (processedSms.hasUpdateFor(station)) {
                syncedStations.add(processedSms.apply(station));
            }
        }
        return syncedStations;
    }

    private static List<Sms> getSms(@NonNull Context context, @NonNull Set<String> phoneNumbers) {
        List<Sms> smsList = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        Uri smsUri = Uri.parse("content://sms/inbox");
        String[] projection = new String[]{"address", "body", "date"};
        String[] selectionArgs = phoneNumbers.toArray(new String[0]);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(");
        for (int i = 0; i < selectionArgs.length; ++i) {
            stringBuilder.append("?");
            if (i != selectionArgs.length - 1) {
                stringBuilder.append(",");
            }
        }
        stringBuilder.append(")");
        String query = "address IN " + stringBuilder.toString();
        Cursor cursor = contentResolver.query(smsUri,
                projection, query,
                selectionArgs, "date DESC");
        while (cursor != null && cursor.moveToNext()) {
            String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
            String body = cursor.getString(cursor.getColumnIndexOrThrow("body"));
            String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
            Sms sms = new Sms(address, body, Long.parseLong(date));
            smsList.add(sms);
        }
        if (cursor != null) {
            cursor.close();
        }
        return smsList;
    }

    private static class ProcessedSms {
        private final String phone;
        private final boolean r1;
        private final boolean r2;
        private final float voltage;
        private final long date;

        public ProcessedSms(String phone, boolean r1, boolean r2, float voltage, long date) {
            this.phone = phone;
            this.r1 = r1;
            this.r2 = r2;
            this.voltage = voltage;
            this.date = date;
        }

        public boolean hasUpdateFor(@NonNull Station station) {
            return this.r1 != station.isR1() || this.r2 != station.isR2() || this.voltage != station.getVoltage();
        }

        public Station apply(@NonNull Station station) {
            station.setR1(r1);
            station.setR2(r2);
            station.setVoltage(voltage);
            return station;
        }

        @Nullable
        public static ProcessedSms getFresher(@Nullable ProcessedSms first, @Nullable ProcessedSms second) {
            if (first == null && second == null) {
                return null;
            }
            if (first == null) {
                return second;
            }
            if (second == null) {
                return first;
            }
            return first.date > second.date ? first : second;
        }

        @Nullable
        public static ProcessedSms process(@NonNull Sms sms) {
            final String REGEX_EQUAL = "=";
            final String REGEX_U_BAT = "U_Bat=([0-9]*[.])?[0-9]+";
            final String KEY_OUT1 = "Out1=[0-1]";
            final String KEY_OUT2 = "Out2=[0-1]";
            String[] lines = sms.message.split(System.getProperty("line.separator"));
            if (lines.length != 3) {
                return null;
            }

            for(int i =0; i < lines.length; ++i){
                lines[i] = lines[i].replaceAll(" ","");
            }

            if (!lines[0].matches(REGEX_U_BAT) || !lines[1].matches(KEY_OUT1) || !lines[2].matches(KEY_OUT2)) {
                return null;
            }

            float voltage = Float.parseFloat(lines[0].split(REGEX_EQUAL)[1]);
            boolean r1 = "1".equals(lines[1].split(REGEX_EQUAL)[1]);
            boolean r2 = "1".equals(lines[2].split(REGEX_EQUAL)[1]);

            return new ProcessedSms(sms.phone, r1, r2, voltage, sms.date);
        }

        @NonNull
        public static List<ProcessedSms> process(@NonNull List<Sms> smsList) {
            List<ProcessedSms> processedSmsList = new ArrayList<>();
            for (Sms sms : smsList) {
                ProcessedSms processedSms = ProcessedSms.process(sms);
                if (processedSms == null) {
                    continue;
                }
                processedSmsList.add(processedSms);
            }
            return processedSmsList;
        }


    }

    private static class Sms {
        private String phone;
        private String message;
        private long date;

        private Sms(String phone, String message, long date) {
            this.phone = phone;
            this.message = message;
            this.date = date;
        }
    }

}
