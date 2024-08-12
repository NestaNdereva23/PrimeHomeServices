package com.example.primehomeservices;

import android.util.Base64;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {
    public static String getTimestamp() {
        return new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
    }

    public static String sanitizePhoneNumber(String phone) {
        if (phone == null || phone.isEmpty()) {
            return "";
        }

        // Remove any whitespace, hyphens, or parentheses
        String cleanedPhone = phone.replaceAll("\\s+|-|\\(|\\)", "");

        if (cleanedPhone.startsWith("+254")) {
            return cleanedPhone.substring(1); // Remove the '+' sign
        } else if (cleanedPhone.startsWith("254")) {
            return cleanedPhone;
        } else if (cleanedPhone.startsWith("0")) {
            return "254" + cleanedPhone.substring(1);
        } else if (cleanedPhone.length() == 9) {
            return "254" + cleanedPhone;
        }

        // If none of the above conditions are met, return the original cleaned number
        return cleanedPhone;
    }

    public static String getPassword(String businessShortCode, String passkey, String timestamp) {
        String str = businessShortCode + passkey + timestamp;
        // Encode the password to Base64
        return Base64.encodeToString(str.getBytes(), Base64.NO_WRAP);
    }
}