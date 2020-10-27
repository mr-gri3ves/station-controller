package com.locator.stationcontroller.utils;

import android.app.PendingIntent;
import android.telephony.SmsManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Utils {
    public static void sendSms(@NonNull String phone, @NonNull String message, @Nullable PendingIntent sentIntent,
                               @Nullable PendingIntent deliveryIntent) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phone, null, message, sentIntent, deliveryIntent);
    }


}
