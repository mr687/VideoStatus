package com.socialgaming.appsclub.Util;

import android.app.Activity;
import android.util.Base64;

import com.google.gson.annotations.SerializedName;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class API {

    @SerializedName("sign")
    private String sign;
    @SerializedName("salt")
    private String salt;
    @SerializedName("package_name")
    private String package_name;

    public API(Activity activity) {
        String apiKey = "viaviweb";
        salt = "" + getRandomSalt();
        sign = md5(apiKey + salt);
        package_name = activity.getApplication().getPackageName();
    }

    private int getRandomSalt() {
        Random random = new Random();
        return random.nextInt(900);
    }

    private String md5(String input) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(input.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < messageDigest.length; i++)
                hexString.append(String.format("%02x", messageDigest[i]));
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String toBase64(String input) {
        byte[] encodeValue = Base64.encode(input.getBytes(), Base64.DEFAULT);
        return new String(encodeValue);
    }

}
