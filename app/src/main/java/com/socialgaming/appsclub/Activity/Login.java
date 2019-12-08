package com.socialgaming.appsclub.Activity;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.socialgaming.appsclub.R;
import com.socialgaming.appsclub.Util.API;
import com.socialgaming.appsclub.Util.Constant_Api;
import com.socialgaming.appsclub.Util.Events;
import com.socialgaming.appsclub.Util.GlobalBus;
import com.socialgaming.appsclub.Util.Method;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import cn.refactor.library.SmoothCheckBox;
import cz.msebera.android.httpclient.Header;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;


public class Login extends AppCompatActivity {

    private EditText editText_email, editText_password;
    private String email, password;

    private Method method;

    public static final String mypreference = "mypref";
    public static final String pref_email = "pref_email";
    public static final String pref_password = "pref_password";
    public static final String pref_check = "pref_check";
    private static SharedPreferences pref;
    private static SharedPreferences.Editor editor;
    private ProgressDialog progressDialog;

    //Google login
    GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 007;

    private InputMethodManager imm;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        setContentView(R.layout.activity_login);

        Method.forceRTLIfSupported(getWindow(), Login.this);

        method = new Method(Login.this);

        pref = getSharedPreferences(mypreference, 0); // 0 - for private mode
        editor = pref.edit();

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        progressDialog = new ProgressDialog(Login.this);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        editText_email = findViewById(R.id.editText_email_login_activity);
        editText_password = findViewById(R.id.editText_password_login_activity);

        Button button_login = findViewById(R.id.button_login_activity);
        final LinearLayout linearLayout_googleSign = findViewById(R.id.linearLayout_google_login);
        Button button_skip = findViewById(R.id.button_skip_login_activity);
        TextView textView_register = findViewById(R.id.textView_register_login);
        TextView textView_forgotPassword = findViewById(R.id.textView_forget_password_login);
        final SmoothCheckBox checkBox = findViewById(R.id.checkbox_login_activity);
        checkBox.setChecked(false);

        if (pref.getBoolean(pref_check, false)) {
            editText_email.setText(pref.getString(pref_email, null));
            editText_password.setText(pref.getString(pref_password, null));
            checkBox.setChecked(true);
        } else {
            editText_email.setText("");
            editText_password.setText("");
            checkBox.setChecked(false);
        }

        checkBox.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
                Log.d("SmoothCheckBox", String.valueOf(isChecked));
                if (isChecked) {
                    editor.putString(pref_email, editText_email.getText().toString());
                    editor.putString(pref_password, editText_password.getText().toString());
                    editor.putBoolean(pref_check, true);
                    editor.commit();
                } else {
                    editor.putBoolean(pref_check, false);
                    editor.commit();
                }
            }
        });

        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = editText_email.getText().toString();
                password = editText_password.getText().toString();

                editText_email.clearFocus();
                editText_password.clearFocus();
                imm.hideSoftInputFromWindow(editText_email.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(editText_password.getWindowToken(), 0);

                login(checkBox);
            }
        });

        linearLayout_googleSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        textView_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Register.class));
            }
        });

        button_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Method.loginBack) {
                    Method.loginBack = false;
                    onBackPressed();
                } else {
                    startActivity(new Intent(Login.this, MainActivity.class));
                    finish();
                }
            }
        });

        textView_forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(Login.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialogbox_forgetpassword);
                dialog.getWindow().setLayout(ViewPager.LayoutParams.FILL_PARENT, ViewPager.LayoutParams.WRAP_CONTENT);
                final EditText editText_forgetPassword = dialog.findViewById(R.id.editText_forget_password);
                Button buttonForgetPassword = dialog.findViewById(R.id.button_forgetPassword);
                buttonForgetPassword.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String stringForgetPassword = editText_forgetPassword.getText().toString();
                        editText_forgetPassword.setError(null);
                        if (!isValidMail(stringForgetPassword) || stringForgetPassword.isEmpty()) {
                            editText_forgetPassword.requestFocus();
                            editText_forgetPassword.setError(getResources().getString(R.string.please_enter_email));
                        } else {
                            if (Method.isNetworkAvailable(Login.this)) {
                                forgetPassword(stringForgetPassword);
                            } else {
                                Toast.makeText(Login.this, getResources().getString(R.string.internet_connection), Toast.LENGTH_SHORT).show();
                            }
                            dialog.dismiss();
                        }
                    }
                });

                dialog.show();
            }
        });

    }

    //Google login
    private void signIn() {
        if (Method.isNetworkAvailable(Login.this)) {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

    }

    //Google login
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    //Google login
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.

            Log.d("Google_login", "login success");

            assert account != null;
            String name = account.getDisplayName();
            String email = account.getEmail();

            registerGoogle(name, email);

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
        }
    }

    private boolean isValidMail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void login(SmoothCheckBox checkBox) {

        editText_email.setError(null);
        editText_password.setError(null);

        if (!isValidMail(email) || email.isEmpty()) {
            editText_email.requestFocus();
            editText_email.setError(getResources().getString(R.string.please_enter_email));
        } else if (password.isEmpty()) {
            editText_password.requestFocus();
            editText_password.setError(getResources().getString(R.string.please_enter_password));
        } else {
            if (Method.isNetworkAvailable(Login.this)) {
                login(email, password, checkBox);
            } else {
                method.alertBox(getResources().getString(R.string.internet_connection));
            }

        }
    }

    public void login(final String sendEmail, final String sendPassword, final SmoothCheckBox checkBox) {

        OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
        status.getPermissionStatus().getEnabled();

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(Login.this));
        jsObj.addProperty("method_name", "user_login");
        jsObj.addProperty("type", "normal");
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
                            String msg = object.getString("msg");

                            if (success.equals("1")) {
                                String user_id = object.getString("user_id");
                                String name = object.getString("name");

                                if (checkBox.isChecked()) {
                                    editor.putString(pref_email, editText_email.getText().toString());
                                    editor.putString(pref_password, editText_password.getText().toString());
                                    editor.putBoolean(pref_check, true);
                                    editor.commit();
                                }

                                OneSignal.sendTag("user_id", user_id);
                                OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
                                status.getPermissionStatus().getEnabled();
                                OneSignal.sendTag("player_id", status.getSubscriptionStatus().getUserId());

                                method.editor.putBoolean(method.pref_login, true);
                                method.editor.putString(method.profileId, user_id);
                                method.editor.putString(method.userName, name);
                                method.editor.putString(method.userEmail, sendEmail);
                                method.editor.putString(method.userPassword, sendPassword);
                                method.editor.putString(method.loginType, "normal");
                                method.editor.commit();
                                editText_email.setText("");
                                editText_password.setText("");

                                if (Method.loginBack) {
                                    Events.Login loginNotify = new Events.Login("");
                                    GlobalBus.getBus().post(loginNotify);
                                    Method.loginBack = false;
                                    onBackPressed();
                                } else {
                                    startActivity(new Intent(Login.this, MainActivity.class)
                                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                    finishAffinity();
                                }


                            } else {
                                method.alertBox(msg);
                            }

                        }

                    }

                    progressDialog.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.dismiss();
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });
    }

    public void forgetPassword(String sendEmail_forget_password) {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(Login.this));
        jsObj.addProperty("method_name", "forgot_pass");
        jsObj.addProperty("email", sendEmail_forget_password);
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
                            String msg = object.getString("msg");
                            String success = object.getString("success");

                            if (success.equals("1")) {
                                Toast.makeText(Login.this, msg, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Login.this, msg, Toast.LENGTH_SHORT).show();
                            }

                        }

                    }

                    progressDialog.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.dismiss();
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });
    }

    @SuppressLint("HardwareIds")
    public void registerGoogle(String sendName, String sendEmail) {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        String device_id;
        try {
            device_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            device_id = "Not Found";
        }


        OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
        status.getPermissionStatus().getEnabled();

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(Login.this));
        jsObj.addProperty("method_name", "user_register");
        jsObj.addProperty("type", "google");
        jsObj.addProperty("name", sendName);
        jsObj.addProperty("email", sendEmail);
        jsObj.addProperty("player_id", status.getSubscriptionStatus().getUserId());
        jsObj.addProperty("device_id", device_id);
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
                            String msg = object.getString("msg");

                            method.editor.putBoolean(method.is_verification, false);
                            method.editor.commit();

                            if (success.equals("1")) {

                                String user_id = object.getString("user_id");
                                String email = object.getString("email");
                                String name = object.getString("name");

                                OneSignal.sendTag("user_id", user_id);
                                OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
                                status.getPermissionStatus().getEnabled();
                                OneSignal.sendTag("player_id", status.getSubscriptionStatus().getUserId());

                                method.editor.putBoolean(method.pref_login, true);
                                method.editor.putString(method.profileId, user_id);
                                method.editor.putString(method.userName, name);
                                method.editor.putString(method.userEmail, email);
                                method.editor.putString(method.loginType, "google");
                                method.editor.commit();

                                if (Method.loginBack) {
                                    Events.Login loginNotify = new Events.Login("");
                                    GlobalBus.getBus().post(loginNotify);
                                    Method.loginBack = false;
                                    onBackPressed();
                                } else {
                                    startActivity(new Intent(Login.this, MainActivity.class)
                                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                    finishAffinity();
                                }

                            } else {
                                method.alertBox(msg);
                            }

                        }

                    }

                    progressDialog.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.dismiss();
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });
    }

}
