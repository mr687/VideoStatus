package com.socialgaming.appsclub.Util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;

import com.socialgaming.appsclub.Activity.UploadActivity;
import com.socialgaming.appsclub.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import io.github.lizhangqu.coreprogress.ProgressHelper;
import io.github.lizhangqu.coreprogress.ProgressUIListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadService extends Service {

    NotificationCompat.Builder myNotify;
    private String uploadUrl, user_id, cat_id, videoType, video_title, video_local, video_thumbnail;
    RemoteViews rv;
    OkHttpClient client;
    public static final String ACTION_STOP = "com.myupload.action.STOP";
    public static final String ACTION_START = "com.myupload.action.START";
    private String NOTIFICATION_CHANNEL_ID = "upload_ch_1";
    private static final String CANCEL_TAG = "c_tag";
    NotificationManager mNotificationManager;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            int progress = Integer.parseInt(message.obj.toString());
            switch (message.what) {
                case 1:
                    rv.setTextViewText(R.id.nf_title, getString(R.string.app_name));
                    rv.setProgressBar(R.id.progress, 100, progress, false);
                    rv.setTextViewText(R.id.nf_percentage, getResources().getString(R.string.upload_video) + " " + "(" + progress + " %)");
                    myNotify.setCustomContentView(rv);
                    startForeground(1001, myNotify.build());
                    break;
                case 2:
                    stopForeground(true);
                    stopSelf();
                    break;
            }
            return false;
        }
    });

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        myNotify = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        myNotify.setChannelId(NOTIFICATION_CHANNEL_ID);
        myNotify.setSmallIcon(R.mipmap.ic_launcher);
        myNotify.setTicker(getResources().getString(R.string.ready_to_upload));
        myNotify.setWhen(System.currentTimeMillis());
        myNotify.setOnlyAlertOnce(true);

        rv = new RemoteViews(getPackageName(),
                R.layout.my_custom_notification);
        rv.setTextViewText(R.id.nf_title, getString(R.string.app_name));
        rv.setProgressBar(R.id.progress, 100, 0, false);
        rv.setTextViewText(R.id.nf_percentage, getResources().getString(R.string.upload_video) + " " + "(0%)");

        Intent closeIntent = new Intent(this, UploadService.class);
        closeIntent.setAction(ACTION_STOP);
        PendingIntent pcloseIntent = PendingIntent.getService(this, 0,
                closeIntent, 0);
        rv.setOnClickPendingIntent(R.id.nf_close, pcloseIntent);

        myNotify.setCustomContentView(rv);
        NotificationChannel mChannel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Online Channel upload";// The user-visible name of the channel.
            mChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(mChannel);
        }
        startForeground(1001, myNotify.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (intent.getAction() != null && intent.getAction().equals(ACTION_START)) {
            uploadUrl = intent.getStringExtra("uploadUrl");
            user_id = intent.getStringExtra("user_id");
            cat_id = intent.getStringExtra("cat_id");
            videoType = intent.getStringExtra("videoType");
            video_title = intent.getStringExtra("video_title");
            video_local = intent.getStringExtra("video_local");
            video_thumbnail = intent.getStringExtra("video_thumbnail");

            init();

        }

        if (intent.getAction() != null && intent.getAction().equals(ACTION_STOP)) {
            stopForeground(true);
            stopSelf();
            if (client != null) {
                for (Call call : client.dispatcher().runningCalls()) {
                    if (call.request().tag().equals(CANCEL_TAG))
                        call.cancel();
                }
            }
            Method.isUpload = true;
            ((UploadActivity) Method.activity_upload).finishUpload();
        }
        return START_STICKY;
    }


    public void init() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                client = new OkHttpClient();
                Request.Builder builder = new Request.Builder()
                        .url(uploadUrl)
                        .tag(CANCEL_TAG);

                File videoFile = new File(video_local);
                File imageFile = new File(video_thumbnail);

                MultipartBody.Builder bodyBuilder = new MultipartBody.Builder();
                bodyBuilder.setType(MultipartBody.FORM);
                JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(Method.activity_upload));
                jsObj.addProperty("method_name", "user_video_upload");
                bodyBuilder.addFormDataPart("video_local", videoFile.getName(), RequestBody.create(null, videoFile));
                bodyBuilder.addFormDataPart("video_thumbnail", imageFile.getName(), RequestBody.create(null, imageFile));
                bodyBuilder.addFormDataPart("cat_id", cat_id);
                bodyBuilder.addFormDataPart("video_layout", videoType);
                bodyBuilder.addFormDataPart("user_id", user_id);
                bodyBuilder.addFormDataPart("video_title", video_title);
                bodyBuilder.addFormDataPart("data", API.toBase64(jsObj.toString()));
                MultipartBody build = bodyBuilder.build();

                RequestBody requestBody = ProgressHelper.withProgress(build, new ProgressUIListener() {

                    //if you don't need this method, don't override this methd. It isn't an abstract method, just an empty method.
                    @Override
                    public void onUIProgressStart(long totalBytes) {
                        super.onUIProgressStart(totalBytes);
                        Log.e("TAG", "onUIProgressStart:" + totalBytes);
                    }

                    @Override
                    public void onUIProgressChanged(long numBytes, long totalBytes, float percent, float speed) {
                        Log.e("TAG", "=============start===============");
                        Log.e("TAG", "numBytes:" + numBytes);
                        Log.e("TAG", "totalBytes:" + totalBytes);
                        Log.e("TAG", "percent:" + percent);
                        Log.e("TAG", "speed:" + speed);
                        Log.e("TAG", "============= end ===============");

                        Message msg = mHandler.obtainMessage();
                        msg.what = 1;
                        msg.obj = (int) (100 * percent) + "";
                        mHandler.sendMessage(msg);
                    }

                    //if you don't need this method, don't override this methd. It isn't an abstract method, just an empty method.
                    @Override
                    public void onUIProgressFinish() {
                        super.onUIProgressFinish();
                        Log.e("TAG", "onUIProgressFinish:");
                        Message msg = mHandler.obtainMessage();
                        msg.what = 2;
                        msg.obj = 0 + "";
                        mHandler.sendMessage(msg);
                        Method.isUpload = true;
                        ((UploadActivity) Method.activity_upload).finishUpload();
                    }
                });
                builder.post(requestBody);

                Call call = client.newCall(builder.build());

                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("TAG", "=============onFailure===============");
                        e.printStackTrace();
                        Method.isUpload = true;
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.e("TAG", "=============onResponse===============");
                        Log.e("TAG", "request headers:" + response.request().headers());
                        Log.e("TAG", "response headers:" + response.headers());
                    }
                });
            }
        }).start();
    }
}
