package com.socialgaming.appsclub.Util;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.socialgaming.appsclub.Activity.SplashScreen;
import com.socialgaming.appsclub.R;
import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationReceivedResult;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import androidx.core.app.NotificationCompat;

public class NotificationExtenderExample extends NotificationExtenderService {

    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    private String message, bigpicture, title, video_id, user_id, payment_withdraw, url,account_id, account_status;
    private String NOTIFICATION_CHANNEL_ID = "video_status_app";

    @Override
    protected boolean onNotificationProcessing(OSNotificationReceivedResult receivedResult) {

        title = receivedResult.payload.title;
        message = receivedResult.payload.body;
        bigpicture = receivedResult.payload.bigPicture;

        Log.d("notificationData", receivedResult.toString());

        try {
            video_id = receivedResult.payload.additionalData.getString("video_id");
            url = receivedResult.payload.additionalData.getString("external_link");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            user_id = receivedResult.payload.additionalData.getString("user_id");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            account_status = receivedResult.payload.additionalData.getString("account_status");
            account_id = receivedResult.payload.additionalData.getString("account_id");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            payment_withdraw = receivedResult.payload.additionalData.getString("payment_withdraw");
        } catch (Exception e) {
            e.printStackTrace();
        }

        sendNotification();
        return true;
    }

    @SuppressLint("WrongConstant")
    private void sendNotification() {
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent;
        if (payment_withdraw != null) {
            intent = new Intent(this, SplashScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("payment_withdraw", payment_withdraw);
        } else if (account_status != null) {
            intent = new Intent(this, SplashScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("account_status", account_status);
            intent.putExtra("account_id", account_id);
        }else if (user_id != null) {
            intent = new Intent(this, SplashScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("user_id", user_id);
        } else if (video_id.equals("0") && !url.equals("false") && !url.trim().isEmpty()) {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
        } else {
            intent = new Intent(this, SplashScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("video_id", video_id);
        }

        NotificationChannel mChannel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "video status app";// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            mChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
            mNotificationManager.createNotificationChannel(mChannel);
        }

        Random random_code = new Random();
        int code = random_code.nextInt(9999 - 1000) + 1000;

        PendingIntent contentIntent = PendingIntent.getActivity(this, code, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setSound(uri)
                .setAutoCancel(true)
                .setChannelId(NOTIFICATION_CHANNEL_ID)
                .setLights(Color.RED, 800, 800);

        mBuilder.setSmallIcon(getNotificationIcon(mBuilder));

        mBuilder.setContentTitle(title);
        mBuilder.setTicker(message);

        if (bigpicture != null) {
            mBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(getBitmapFromURL(bigpicture)).setSummaryText(message));
        } else {
            //mBuilder.setContentText(message);
            mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        }

        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(m, mBuilder.build());
    }

    private int getNotificationIcon(NotificationCompat.Builder notificationBuilder) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setColor(getColour());
            return R.drawable.ic_stat_onesignal_default;
        } else {
            return R.drawable.ic_stat_onesignal_default;
        }
    }

    private int getColour() {
        return 0x3F51B5;
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }
}