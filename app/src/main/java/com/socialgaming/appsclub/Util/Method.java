package com.socialgaming.appsclub.Util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.socialgaming.appsclub.DataBase.DatabaseHandler;
import com.socialgaming.appsclub.InterFace.FullScreen;
import com.socialgaming.appsclub.InterFace.InterstitialAdView;
import com.socialgaming.appsclub.InterFace.VideoAd;
import com.socialgaming.appsclub.Item.SubCategoryList;
import com.socialgaming.appsclub.R;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;

import androidx.appcompat.app.AlertDialog;

public class Method {

    private Activity activity;
    public static boolean loginBack = false, allowPermitionExternalStorage = false;
    public static boolean isUpload = true, isDownload = true;
    public boolean personalization_ad = false;
    public static String search_title;
    private InterstitialAdView interstitialAdView;
    private VideoAd videoAd;
    private FullScreen fullScreen;

    public SharedPreferences pref;
    public SharedPreferences.Editor editor;
    private final String myPreference = "login";
    public String pref_login = "pref_login";
    private String firstTime = "firstTime";
    public String profileId = "profileId";
    public String userEmail = "userEmail";
    public String userPassword = "userPassword";
    public String userName = "userName";
    public String userImage = "userImage";
    public String loginType = "loginType";
    public String show_login = "show_login";
    public String notification = "notification";
    public String verification_code = "verification_code";
    public String is_verification = "is_verification";

    public String reg_name = "reg_name";
    public String reg_email = "reg_email";
    public String reg_password = "reg_password";
    public String reg_phoneNo = "reg_phoneNo";
    public String reg_reference = "reg_reference";

    private String filename;
    private String storageFile;
    private DatabaseHandler db;

    @SuppressLint("StaticFieldLeak")
    public static Activity activity_upload;

    @SuppressLint("CommitPrefEdits")
    public Method(Activity activity) {
        this.activity = activity;
        db = new DatabaseHandler(activity);
        pref = activity.getSharedPreferences(myPreference, 0); // 0 - for private mode
        editor = pref.edit();
    }

    @SuppressLint("CommitPrefEdits")
    public Method(Activity activity, VideoAd videoAd) {
        this.activity = activity;
        db = new DatabaseHandler(activity);
        pref = activity.getSharedPreferences(myPreference, 0); // 0 - for private mode
        editor = pref.edit();
        this.videoAd = videoAd;
    }

    @SuppressLint("CommitPrefEdits")
    public Method(Activity activity, InterstitialAdView interstitialAdView) {
        this.activity = activity;
        db = new DatabaseHandler(activity);
        this.interstitialAdView = interstitialAdView;
        pref = activity.getSharedPreferences(myPreference, 0); // 0 - for private mode
        editor = pref.edit();
    }

    @SuppressLint("CommitPrefEdits")
    public Method(Activity activity, InterstitialAdView interstitialAdView, VideoAd videoAd, FullScreen fullScreen) {
        this.activity = activity;
        db = new DatabaseHandler(activity);
        this.interstitialAdView = interstitialAdView;
        this.videoAd = videoAd;
        this.fullScreen = fullScreen;
        pref = activity.getSharedPreferences(myPreference, 0); // 0 - for private mode
        editor = pref.edit();
    }

    public void login() {
        if (!pref.getBoolean(firstTime, false)) {
            editor.putBoolean(pref_login, false);
            editor.putBoolean(firstTime, true);
            editor.commit();
        }
    }

    //rtl
    public static void forceRTLIfSupported(Window window, Activity activity) {
        if (activity.getResources().getString(R.string.isRTL).equals("true")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                window.getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }
    }

    //Whatsapp application installation or not check
    public boolean isAppInstalled_Whatsapp(Activity activity) {
        String packageName = "com.whatsapp";
        Intent mIntent = activity.getPackageManager().getLaunchIntentForPackage(packageName);
        if (mIntent != null) {
            return true;
        } else {
            return false;
        }
    }

    //instagram application installation or not check
    public boolean isAppInstalled_Instagram(Activity activity) {
        String packageName = "com.instagram.android";
        Intent mIntent = activity.getPackageManager().getLaunchIntentForPackage(packageName);
        if (mIntent != null) {
            return true;
        } else {
            return false;
        }
    }

    //facebook application installation or not check
    public boolean isAppInstalled_facebook(Activity activity) {
        String packageName = "com.facebook.katana";
        Intent mIntent = activity.getPackageManager().getLaunchIntentForPackage(packageName);
        if (mIntent != null) {
            return true;
        } else {
            return false;
        }
    }

    //twitter application installation or not check
    public boolean isAppInstalled_twitter(Activity activity) {
        String packageName = "com.twitter.android";
        Intent mIntent = activity.getPackageManager().getLaunchIntentForPackage(packageName);
        if (mIntent != null) {
            return true;
        } else {
            return false;
        }
    }

    //network check
    public static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //get screen width
    public int getScreenWidth() {
        int columnWidth;
        WindowManager wm = (WindowManager) activity
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();

        point.x = display.getWidth();
        point.y = display.getHeight();

        columnWidth = point.x;
        return columnWidth;
    }

    //get screen height
    public int getScreenHeight() {
        int columnHeight;
        WindowManager wm = (WindowManager) activity
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();

        point.x = display.getWidth();
        point.y = display.getHeight();

        columnHeight = point.y;
        return columnHeight;
    }

    //---------------Rewarded video ad---------------//

    public void showVideoAd(final String video_ad_type) {

        final ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(activity.getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();

        if (Constant_Api.aboutUsList != null) {
            if (Constant_Api.aboutUsList.isRewarded_video_ads()) {
                Constant_Api.REWARD_VIDEO_AD_COUNT = Constant_Api.REWARD_VIDEO_AD_COUNT + 1;
                if (Constant_Api.REWARD_VIDEO_AD_COUNT == Constant_Api.REWARD_VIDEO_AD_COUNT_SHOW) {
                    Constant_Api.REWARD_VIDEO_AD_COUNT = 0;
                    final RewardedVideoAd mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(activity);
                    if (mRewardedVideoAd != null) {
                        AdRequest adRequest;
                        if (personalization_ad) {
                            Bundle extras = new Bundle();
                            extras.putBoolean("_noRefresh", true);
                            adRequest = new AdRequest.Builder()
                                    .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                                    .build();
                        } else {
                            Bundle extras = new Bundle();
                            extras.putString("npa", "1");
                            extras.putBoolean("_noRefresh", true);
                            adRequest = new AdRequest.Builder()
                                    .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                                    .build();
                        }
                        mRewardedVideoAd.loadAd(Constant_Api.aboutUsList.getRewarded_video_ads_id(), adRequest);
                        mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
                            @Override
                            public void onRewarded(RewardItem reward) {
                                Log.d("reward_video_ad", "reward");
                            }

                            @Override
                            public void onRewardedVideoAdLeftApplication() {
                                Log.d("reward_video_ad", "AdLeftApplication");
                            }

                            @Override
                            public void onRewardedVideoAdFailedToLoad(int i) {
                                callVideoAdData(video_ad_type);
                                progressDialog.dismiss();
                                Log.d("reward_video_ad", "Failed");
                            }

                            @Override
                            public void onRewardedVideoAdClosed() {
                                callVideoAdData(video_ad_type);
                                Log.d("reward_video_ad", "close");
                            }

                            @Override
                            public void onRewardedVideoAdLoaded() {
                                mRewardedVideoAd.show();
                                progressDialog.dismiss();
                                Log.d("reward_video_ad", "load");
                            }

                            @Override
                            public void onRewardedVideoAdOpened() {
                                Log.d("reward_video_ad", "open");
                            }

                            @Override
                            public void onRewardedVideoStarted() {
                                Log.d("reward_video_ad", "start");
                            }

                            @Override
                            public void onRewardedVideoCompleted() {
                                Log.d("reward_video_ad", "completed");
                            }
                        });
                    } else {
                        progressDialog.dismiss();
                        callVideoAdData(video_ad_type);
                    }
                } else {
                    progressDialog.dismiss();
                    callVideoAdData(video_ad_type);
                }
            } else {
                progressDialog.dismiss();
                callVideoAdData(video_ad_type);
            }
        } else {
            progressDialog.dismiss();
            callVideoAdData(video_ad_type);
        }

    }

    //call interface
    private void callVideoAdData(String video_ad_type) {
        videoAd.videoAdClick(video_ad_type);
    }

    //---------------Rewarded video ad---------------//

    //---------------Interstitial Ad---------------//

    public void interstitialAdShow(final int position, final String type, final String id) {

        if (Constant_Api.aboutUsList != null) {
            if (Constant_Api.aboutUsList.isInterstital_ad()) {
                Constant_Api.AD_COUNT = Constant_Api.AD_COUNT + 1;
                if (Constant_Api.AD_COUNT == Constant_Api.AD_COUNT_SHOW) {
                    Constant_Api.AD_COUNT = 0;
                    final InterstitialAd interstitialAd = new InterstitialAd(activity);
                    AdRequest adRequest;
                    if (personalization_ad) {
                        adRequest = new AdRequest.Builder()
                                .build();
                    } else {
                        Bundle extras = new Bundle();
                        extras.putString("npa", "1");
                        adRequest = new AdRequest.Builder()
                                .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                                .build();
                    }
                    interstitialAd.setAdUnitId(Constant_Api.aboutUsList.getInterstital_ad_id());
                    interstitialAd.loadAd(adRequest);
                    interstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
                            interstitialAd.show();
                        }

                        public void onAdClosed() {
                            interstitialAdView.position(position, type, id);
                            super.onAdClosed();
                        }

                        @Override
                        public void onAdFailedToLoad(int i) {
                            Log.d("admob_error", String.valueOf(i));
                            interstitialAdView.position(position, type, id);
                            super.onAdFailedToLoad(i);
                        }

                    });
                } else {
                    interstitialAdView.position(position, type, id);
                }
            } else {
                interstitialAdView.position(position, type, id);
            }
        } else {
            interstitialAdView.position(position, type, id);
        }
    }

    //---------------Interstitial Ad---------------//

    //---------------Banner Ad---------------//

    public void showPersonalizedAds(LinearLayout linearLayout) {

        if (Constant_Api.aboutUsList != null) {
            if (Constant_Api.aboutUsList.isBanner_ad()) {
                AdView adView = new AdView(activity);
                AdRequest adRequest = new AdRequest.Builder()
                        .build();
                adView.setAdUnitId(Constant_Api.aboutUsList.getBanner_ad_id());
                adView.setAdSize(AdSize.BANNER);
                linearLayout.addView(adView);
                adView.loadAd(adRequest);
            } else {
                linearLayout.setVisibility(View.GONE);
            }
        } else {
            linearLayout.setVisibility(View.GONE);
        }
    }

    public void showNonPersonalizedAds(LinearLayout linearLayout) {

        Bundle extras = new Bundle();
        extras.putString("npa", "1");
        if (Constant_Api.aboutUsList != null) {
            if (Constant_Api.aboutUsList.isBanner_ad()) {
                AdView adView = new AdView(activity);
                AdRequest adRequest = new AdRequest.Builder()
                        .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                        .build();
                adView.setAdUnitId(Constant_Api.aboutUsList.getBanner_ad_id());
                adView.setAdSize(AdSize.BANNER);
                linearLayout.addView(adView);
                adView.loadAd(adRequest);
            } else {
                linearLayout.setVisibility(View.GONE);
            }
        } else {
            linearLayout.setVisibility(View.GONE);
        }
    }

    //---------------Banner Ad---------------//


    //---------------Full Screen---------------//

    //call interface full screen
    public void ShowFullScreen(boolean isFullScreen) {
        fullScreen.fullscreen(isFullScreen);
    }

    //---------------Full Screen---------------//

    //---------------Download status video---------------//
    public void download(String video_id, String video_category_id, String video_name, String category, String video_image, String video_uri, String layout_type,String watermark_image,String watermark_on_off) {

        storageFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Social_gaming/" + "filename-" + video_id + ".mp4";
        File file = new File(storageFile);
        if (!file.exists()) {

            Method.isDownload = false;

            File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Social_gaming/");
            if (!root.exists()) {
                root.mkdirs();
            }

            filename = "filename-" + video_id + ".mp4";

            Intent serviceIntent = new Intent(activity, DownloadService.class);
            serviceIntent.setAction(DownloadService.ACTION_START);
            serviceIntent.putExtra("video_id", video_id);
            serviceIntent.putExtra("downloadUrl", video_uri);
            serviceIntent.putExtra("file_path", root.toString());
            serviceIntent.putExtra("file_name", filename);
            serviceIntent.putExtra("layout_type", layout_type);
            serviceIntent.putExtra("watermark_image", watermark_image);
            serviceIntent.putExtra("watermark_on_off", watermark_on_off);
            activity.startService(serviceIntent);


        } else {
            filename = "filename-" + video_id + ".mp4";
            Toast.makeText(activity, activity.getResources().getString(R.string.you_have_already_download_video), Toast.LENGTH_SHORT).show();
        }

        new DownloadImage().execute(video_image, video_id, video_category_id, video_name, category, layout_type);

    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadImage extends AsyncTask<String, String, String> {

        private String video_id, video_category_id, video_name, category, layout_type;
        Bitmap bitmapDownload;

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                video_id = params[1];
                video_category_id = params[2];
                video_name = params[3];
                category = params[4];
                layout_type = params[5];
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                bitmapDownload = BitmapFactory.decodeStream(input);
            } catch (IOException e) {
                // Log exception
                Log.w("error", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            downloadImage(bitmapDownload, video_id, video_category_id, video_name, category, layout_type);

            super.onPostExecute(s);
        }

    }

    private void downloadImage(Bitmap bitmap, String video_id, String video_category_id, String video_name, String category, String layout_type) {

        String filePath = null;

        String iconsStoragePath = Environment.getExternalStorageDirectory() + "/Social_gaming/";
        File sdIconStorageDir = new File(iconsStoragePath);

        //create storage directories, if they don't exist
        if (!sdIconStorageDir.exists()) {
            sdIconStorageDir.mkdirs();
        }

        String fname = "Image-" + video_id;
        filePath = iconsStoragePath + fname + ".jpg";
        File file = new File(iconsStoragePath, filePath);
        if (file.exists()) {
            Log.d("file_exists", "file_exists");
        } else {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(filePath);

                BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);

                //choose another format if PNG doesn't suit you
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);

                bos.flush();
                bos.close();

            } catch (FileNotFoundException e) {
                Log.w("TAG", "Error saving image file: " + e.getMessage());
            } catch (IOException e) {
                Log.w("TAG", "Error saving image file: " + e.getMessage());
            }
        }


        if (db.checkId_video_download(video_id)) {
            db.addVideoDownload(new SubCategoryList(video_id, video_category_id, video_name, iconsStoragePath + filename, filePath, filePath, category, layout_type));
        }
    }
    //---------------Download status video---------------//


    //add to favourite
    public void addToFav(DatabaseHandler db, List<SubCategoryList> subCategoryLists, int position) {
        db.addDetailFav(new SubCategoryList("", subCategoryLists.get(position).getId(), subCategoryLists.get(position).getCid(),
                subCategoryLists.get(position).getVideo_title(), subCategoryLists.get(position).getVideo_url(),
                subCategoryLists.get(position).getVideo_layout(), subCategoryLists.get(position).getVideo_thumbnail_b(),
                subCategoryLists.get(position).getVideo_thumbnail_s(), subCategoryLists.get(position).getTotal_viewer(),
                subCategoryLists.get(position).getTotal_likes(), subCategoryLists.get(position).getCategory_name(),
                subCategoryLists.get(position).getAlready_like()));

    }

    //alert message box
    public void alertBox(String message) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setMessage(Html.fromHtml(message));
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(activity.getResources().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    //alert message box
    public void suspend(String message){

        if(pref.getBoolean(pref_login,false)){
            editor.putBoolean(pref_login, false);
            editor.commit();
            Events.Login loginNotify = new Events.Login("");
            GlobalBus.getBus().post(loginNotify);
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setMessage(Html.fromHtml(message));
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(activity.getResources().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }


    //view count and user video like format
    public String format(Number number) {
        char[] suffix = {' ', 'k', 'M', 'B', 'T', 'P', 'E'};
        long numValue = number.longValue();
        int value = (int) Math.floor(Math.log10(numValue));
        int base = value / 3;
        if (value >= 3 && base < suffix.length) {
            return new DecimalFormat("#0.0").format(numValue / Math.pow(10, base * 3)) + suffix[base];
        } else {
            return new DecimalFormat("#,##0").format(numValue);
        }
    }

}
