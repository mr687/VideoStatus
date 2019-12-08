package com.socialgaming.appsclub.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.VideoView;

import com.socialgaming.appsclub.R;
import com.socialgaming.appsclub.Util.API;
import com.socialgaming.appsclub.Util.Constant_Api;
import com.socialgaming.appsclub.Util.Method;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OneSignal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import cz.msebera.android.httpclient.Header;

public class SplashScreen extends AppCompatActivity {

    private Boolean isCancelled = false;
    private Method method;
    private String video_id = "0";
    private String account_id;
    private String user_id = "false";
    private String payment_withdraw = "false";
    private String account_status = "false";
    //Google login
    private GoogleSignInClient mGoogleSignInClient;
    MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        setContentView(R.layout.activity_splace_screen);
        getWindow().setFormat(PixelFormat.UNKNOWN);
        VideoView videoView = (VideoView) findViewById(R.id.videoView1);
        String path = "android.resource://com.socialgaming.appsclub/" + R.raw.splash;
        Uri uri = Uri.parse(path);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.start();

        method = new Method(SplashScreen.this);
        method.login();

        Log.d("user_id", String.valueOf(method.pref.getString(method.profileId, null)));

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        // making notification bar transparent
        changeStatusBarColor();

        Method.forceRTLIfSupported(getWindow(), SplashScreen.this);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        if (getIntent().hasExtra("video_id")) {
            video_id = getIntent().getStringExtra("video_id");
            assert video_id != null;
            Log.d("video_id", video_id);
        }

        if (getIntent().hasExtra("user_id")) {
            user_id = getIntent().getStringExtra("user_id");
            assert user_id != null;
            Log.d("user_id", user_id);
        }

        if (getIntent().hasExtra("payment_withdraw")) {
            payment_withdraw = getIntent().getStringExtra("payment_withdraw");
            assert payment_withdraw != null;
            Log.d("payment_withdraw", payment_withdraw);
        }

        if (getIntent().hasExtra("account_status")) {
            account_status = getIntent().getStringExtra("account_status");
            account_id = getIntent().getStringExtra("account_id");
            assert account_status != null;
            Log.d("account_status", account_status);
        }

        splashScreen();

    }

    public void splashScreen() {

        if (Method.isNetworkAvailable(SplashScreen.this)) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // This method will be executed once the timer is over
                    // Start your app main activity
                    if (!isCancelled) {
                        if (payment_withdraw.equals("true")) {
                            startActivity(new Intent(SplashScreen.this, MainActivity.class)
                                    .putExtra("payment_withdraw", payment_withdraw));
                            finishAffinity();
                        } else if (user_id.equals("true")) {
                            Log.d("user_id", user_id);
                            startActivity(new Intent(SplashScreen.this, AVStatus.class));
                            finishAffinity();
                        } else if (account_status.equals("true")) {
                            Log.d("account_status", account_status);
                            startActivity(new Intent(SplashScreen.this, Suspend.class)
                                    .putExtra("account_id", account_id));
                            finishAffinity();
                        } else if (video_id.equals("0")) {
                            if (method.pref.getBoolean(method.pref_login, false)) {
                                if (method.pref.getString(method.loginType, null).equals("google")) {
                                    googleLogin();
                                } else {
                                    String email = method.pref.getString(method.userEmail, null);
                                    String password = method.pref.getString(method.userPassword, null);
                                    login(email, password, "normal");
                                }
                            } else {
                                if (method.pref.getBoolean(method.is_verification, false)) {
                                    startActivity(new Intent(SplashScreen.this, Verification.class));
                                    finishAffinity();
                                } else {
                                    if (method.pref.getBoolean(method.show_login, true)) {
                                        method.editor.putBoolean(method.show_login, false);
                                        method.editor.commit();
                                        Intent i = new Intent(SplashScreen.this, Login.class);
                                        startActivity(i);
                                        finishAffinity();
                                    } else {
                                        Intent i = new Intent(SplashScreen.this, MainActivity.class);
                                        startActivity(i);
                                        finishAffinity();
                                    }
                                }
                            }
                        } else {
                            Log.d("video_id", video_id);
                            startActivity(new Intent(SplashScreen.this, NotificationDetail.class).putExtra("video_id", video_id));
                            finishAffinity();
                        }
                    }

                }
            }, 2000);

        } else {
            startActivity(new Intent(SplashScreen.this, MainActivity.class));
            finishAffinity();
        }

    }

    public void googleLogin() {

        String email = method.pref.getString(method.userEmail, null);
        String password = method.pref.getString(method.userPassword, null);
        login(email, password, "google");

    }

    public void login(final String sendEmail, final String sendPassword, String type) {

        OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
        status.getPermissionStatus().getEnabled();

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(SplashScreen.this));
        jsObj.addProperty("method_name", "user_login");
        jsObj.addProperty("type", type);
        jsObj.addProperty("email", sendEmail);
        jsObj.addProperty("password", sendPassword);
        jsObj.addProperty("player_id", status.getSubscriptionStatus().getUserId());
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant_Api.url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                Log.d("Response", new String(responseBody));
                String res = new String(responseBody);

                try {
                    JSONObject jsonObject = new JSONObject(res);

                    if (jsonObject.has(Constant_Api.STATUS)) {

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        method.alertBox(message);

                    } else {

                        JSONArray jsonArray = jsonObject.getJSONArray(Constant_Api.tag);

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject object = jsonArray.getJSONObject(i);
                            String success = object.getString("success");
                            if (success.equals("1")) {

                                OneSignal.sendTag("user_id", method.pref.getString(method.profileId, null));
                                OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
                                status.getPermissionStatus().getEnabled();
                                OneSignal.sendTag("player_id", status.getSubscriptionStatus().getUserId());

                                if (type.equals("google")) {
                                    if (GoogleSignIn.getLastSignedInAccount(SplashScreen.this) != null) {
                                        startActivity(new Intent(SplashScreen.this, MainActivity.class));
                                        finishAffinity();
                                    } else {
                                        method.editor.putBoolean(method.pref_login, false);
                                        method.editor.commit();
                                        startActivity(new Intent(SplashScreen.this, Login.class));
                                        finishAffinity();
                                    }
                                } else {
                                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                                    finishAffinity();
                                }

                            } else {

                                OneSignal.sendTag("user_id", method.pref.getString(method.profileId, null));

                                if (type.equals("google")) {

                                    mGoogleSignInClient.signOut()
                                            .addOnCompleteListener(SplashScreen.this, new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                }
                                            });


                                }

                                method.editor.putBoolean(method.pref_login, false);
                                method.editor.commit();
                                startActivity(new Intent(SplashScreen.this, Login.class));
                                finishAffinity();
                            }

                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("error", error.toString());
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });
    }

    @Override
    protected void onDestroy() {
        isCancelled = true;
        super.onDestroy();
    }

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

}


