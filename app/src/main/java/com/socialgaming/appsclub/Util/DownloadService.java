package com.socialgaming.appsclub.Util;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.daasuu.gpuv.composer.GPUMp4Composer;
import com.daasuu.gpuv.egl.filter.GlWatermarkFilter;
import com.socialgaming.appsclub.DataBase.DatabaseHandler;
import com.socialgaming.appsclub.R;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import io.github.lizhangqu.coreprogress.ProgressHelper;
import io.github.lizhangqu.coreprogress.ProgressUIListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

import static org.greenrobot.eventbus.EventBus.TAG;

public class DownloadService extends Service {

    private DatabaseHandler db;
    private NotificationCompat.Builder myNotify;
    private String video_id, downloadUrl, file_path_delete, file_path, file_name, layout_type, watermark_image, watermark_on_off;
    private RemoteViews rv;
    private OkHttpClient client;
    private WaterMarkData waterMarkData;
    private GPUMp4Composer gpuMp4Composer;
    public static final String ACTION_STOP = "com.mydownload.action.STOP";
    public static final String ACTION_START = "com.mydownload.action.START";
    private String NOTIFICATION_CHANNEL_ID = "download_ch_1";
    private static final String CANCEL_TAG = "c_tag";
    NotificationManager mNotificationManager;
    private boolean isWaterMark = false;
    private boolean isResize = false;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            int progress = Integer.parseInt(message.obj.toString());
            switch (message.what) {
                case 1:
                    rv.setTextViewText(R.id.nf_title, getString(R.string.app_name));
                    rv.setProgressBar(R.id.progress, 100, progress, false);
                    if (isWaterMark) {
                        rv.setTextViewText(R.id.nf_percentage, getResources().getString(R.string.watermark) + " " + "(" + progress + " %)");
                    } else {
                        rv.setTextViewText(R.id.nf_percentage, getResources().getString(R.string.downloading) + " " + "(" + progress + " %)");
                    }
                    myNotify.setCustomContentView(rv);
                    startForeground(1002, myNotify.build());
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

        db = new DatabaseHandler(getApplicationContext());

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        myNotify = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        myNotify.setChannelId(NOTIFICATION_CHANNEL_ID);
        myNotify.setSmallIcon(R.mipmap.ic_launcher);
        myNotify.setTicker(getResources().getString(R.string.downloading));
        myNotify.setWhen(System.currentTimeMillis());
        myNotify.setOnlyAlertOnce(true);

        rv = new RemoteViews(getPackageName(),
                R.layout.my_custom_notification);
        rv.setTextViewText(R.id.nf_title, getString(R.string.app_name));
        rv.setProgressBar(R.id.progress, 100, 0, false);
        rv.setTextViewText(R.id.nf_percentage, getResources().getString(R.string.downloading) + " " + "(0%)");

        Intent closeIntent = new Intent(this, DownloadService.class);
        closeIntent.setAction(ACTION_STOP);
        PendingIntent pcloseIntent = PendingIntent.getService(this, 0,
                closeIntent, 0);
        rv.setOnClickPendingIntent(R.id.nf_close, pcloseIntent);

        myNotify.setCustomContentView(rv);
        NotificationChannel mChannel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Online Channel download";// The user-visible name of the channel.
            mChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(mChannel);
        }
        startForeground(1002, myNotify.build());
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
            video_id = intent.getStringExtra("video_id");
            downloadUrl = intent.getStringExtra("downloadUrl");
            file_path = intent.getStringExtra("file_path");
            file_name = intent.getStringExtra("file_name");
            layout_type = intent.getStringExtra("layout_type");
            watermark_image = intent.getStringExtra("watermark_image");
            watermark_on_off = intent.getStringExtra("watermark_on_off");

            assert watermark_on_off != null;
            if (watermark_on_off.equals("true")) {
                file_path = getExternalCacheDir().getAbsolutePath();
            }


            file_path_delete = file_path;

            init();
        }
        if (intent.getAction() != null && intent.getAction().equals(ACTION_STOP)) {
            stopForeground(true);
            stopSelf();
            if (gpuMp4Composer != null) {
                gpuMp4Composer.cancel();
            }
            if (waterMarkData != null) {
                waterMarkData.cancel(true);
            }
            if (!db.checkId_video_download(video_id)) {
                db.delete_video_download(video_id);
            }
            File file = new File(file_path_delete, file_name);
            if (file.exists()) {
                file.delete();
            }
            Method.isDownload = true;
            if (client != null) {
                for (Call call : client.dispatcher().runningCalls()) {
                    if (call.request().tag().equals(CANCEL_TAG))
                        call.cancel();
                }
            }
        }
        return START_STICKY;
    }


    public void init() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                client = new OkHttpClient();
                Request.Builder builder = new Request.Builder()
                        .url(downloadUrl)
                        .addHeader("Accept-Encoding", "identity")
                        .get()
                        .tag(CANCEL_TAG);

                Call call = client.newCall(builder.build());

                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.e("TAG", "=============onFailure===============");
                        e.printStackTrace();
                        Log.d("error_downloading", e.toString());
                        Method.isDownload = true;
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        Log.e("TAG", "=============onResponse===============");
                        Log.e("TAG", "request headers:" + response.request().headers());
                        Log.e("TAG", "response headers:" + response.headers());
                        assert response.body() != null;
                        final ResponseBody responseBody = ProgressHelper.withProgress(response.body(), new ProgressUIListener() {

                            //if you don't need this method, don't override this methd. It isn't an abstract method, just an empty method.
                            @Override
                            public void onUIProgressStart(long totalBytes) {
                                super.onUIProgressStart(totalBytes);
                                Log.e("TAG", "onUIProgressStart:" + totalBytes);
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.downloading), Toast.LENGTH_SHORT).show();
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

                            //if you don't need this method, don't override this method. It isn't an abstract method, just an empty method.
                            @Override
                            public void onUIProgressFinish() {
                                super.onUIProgressFinish();
                                Log.e("TAG", "onUIProgressFinish:");

                                if (watermark_on_off.equals("true")) {

                                    //call data watermark class add to watermark
                                    waterMarkData = new WaterMarkData();
                                    waterMarkData.execute();

                                } else {
                                    Message msg = mHandler.obtainMessage();
                                    msg.what = 2;
                                    msg.obj = 0 + "";
                                    mHandler.sendMessage(msg);
                                    Method.isDownload = true;
                                    showMedia(file_path, file_name);
                                }

                            }
                        });


                        try {

                            BufferedSource source = responseBody.source();
                            File outFile = new File(file_path + "/" + file_name);
                            BufferedSink sink = Okio.buffer(Okio.sink(outFile));
                            source.readAll(sink);
                            sink.flush();
                            source.close();

                        } catch (Exception e) {
                            Log.d("show_data", e.toString());
                        }

                    }
                });
            }
        }).start();
    }

    @SuppressLint("StaticFieldLeak")
    class WaterMarkData extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {

            //check water mark on or off
            MediaPlayer mp = new MediaPlayer();
            try {
                mp.setDataSource(downloadUrl);
                mp.prepareAsync();
                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                        Log.d("call_data", "sizedata");
                        if (layout_type.equals("Portrait")) {
                            if (height <= 700) {
                                isResize = true;
                            }
                        } else {
                            if (height <= 400 || width <= 400) {
                                isResize = true;
                            }
                        }
                        watermark();//call method water mark adding
                    }
                });
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                watermark();//call method water mark adding
                Log.d("call_data", "error");
            } catch (SecurityException e) {
                e.printStackTrace();
                watermark();//call method water mark adding
                Log.d("call_data", "error");
            } catch (IllegalStateException e) {
                e.printStackTrace();
                watermark();//call method water mark adding
                Log.d("call_data", "error");
            } catch (IOException e) {
                e.printStackTrace();
                watermark();//call method water mark adding
                Log.d("call_data", "error");
            } catch (Exception e) {
                e.printStackTrace();
                watermark();//call method water mark adding
                Log.d("call_data", "error");
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("data", "execute");
        }
    }

    private void watermark() {

        //check water mark on or off

        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap image = null;
                try {
                    URL url = new URL(watermark_image);
                    try {
                        image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    } catch (Exception e) {
                        Log.d("error_data", e.toString());
                    }
                } catch (IOException e) {
                    Log.d("error", e.toString());
                    System.out.println(e);
                    image = BitmapFactory.decodeResource(getResources(), R.drawable.watermark);
                }

                if (isResize) {
                    image = getResizedBitmap(image, 40, 40);
                    isResize = false;
                }

                String destinationPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Social_gaming/";

                file_path_delete = destinationPath;

                gpuMp4Composer = new GPUMp4Composer(file_path + "/" + file_name, destinationPath + file_name)
                        .filter(new GlWatermarkFilter(image, GlWatermarkFilter.Position.RIGHT_BOTTOM))
                        .listener(new GPUMp4Composer.Listener() {
                            @Override
                            public void onProgress(double progress) {
                                isWaterMark = true;
                                double value = progress * 100;
                                int i = (int) value;
                                Message msg = mHandler.obtainMessage();
                                msg.what = 1;
                                msg.obj = i + "";
                                mHandler.sendMessage(msg);
                                Log.d(TAG, "onProgress = " + progress);
                                Log.d("call_data", "watermark");
                            }

                            @Override
                            public void onCompleted() {
                                isWaterMark = false;
                                new File(getExternalCacheDir().getAbsolutePath() + "/" + file_name).delete();//delete file to save in cash folder
                                Message msg = mHandler.obtainMessage();
                                msg.what = 2;
                                msg.obj = 0 + "";
                                mHandler.sendMessage(msg);
                                Method.isDownload = true;
                                showMedia(destinationPath, file_name);
                                Log.d(TAG, "onCompleted()");
                            }

                            @Override
                            public void onCanceled() {
                                isWaterMark = false;
                                Message msg = mHandler.obtainMessage();
                                msg.what = 2;
                                msg.obj = 0 + "";
                                mHandler.sendMessage(msg);
                                Method.isDownload = true;
                                Log.d(TAG, "onCanceled");
                            }

                            @Override
                            public void onFailed(Exception exception) {
                                isWaterMark = false;
                                Message msg = mHandler.obtainMessage();
                                msg.what = 2;
                                msg.obj = 0 + "";
                                mHandler.sendMessage(msg);
                                Method.isDownload = true;
                                Log.e(TAG, "onFailed()", exception);
                            }
                        })
                        .start();
            }
        }).start();

    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    public void showMedia(String file_path, String file_name) {
        try {
            MediaScannerConnection.scanFile(getApplicationContext(), new String[]{file_path + "/" + file_name},
                    null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
