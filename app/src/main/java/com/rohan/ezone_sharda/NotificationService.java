package com.rohan.ezone_sharda;

import android.app.Notification;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Toast;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NotificationService extends NotificationListenerService {

    SharedPreferences ezone_data;
    String previousNotificationName = "";

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        ezone_data = getSharedPreferences("EZONE_DATA", MODE_PRIVATE);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        Bundle bundle = sbn.getNotification().extras;
        if (bundle.getCharSequence(Notification.EXTRA_BIG_TEXT) != null && !Objects.equals(previousNotificationName, sbn.getPackageName())) {
            previousNotificationName = sbn.getPackageName();

            if (sbn.getPackageName().equals("com.google.android.gm")) {
                String text = Objects.requireNonNull(bundle.getCharSequence(Notification.EXTRA_BIG_TEXT)).toString();

                if (Objects.requireNonNull(bundle.getCharSequence(Notification.EXTRA_TITLE)).toString().equals("E-Zone Online Portal")) {
                    Pattern pattern = Pattern.compile("\\b\\d{6}\\b"); // Matches 6 consecutive digits
                    Matcher matcher = pattern.matcher(text);

                    if (matcher.find()) {
                        String otp = matcher.group();
                        Log.d("OTP", otp);
                        if (otp.equals(ezone_data.getString("OTP", ""))) {
                            Log.d("OTP", "Same OTP");
                        } else {
                            Log.d("OTP", "Updating New OTP");
                            SharedPreferences.Editor editor = ezone_data.edit();
                            editor.putString("OTP", otp);
                            editor.apply();
                        }

                    }
                } else {
                    Log.d("Notification", "OTP Not Found");
                }
            }
        }
    }
}
