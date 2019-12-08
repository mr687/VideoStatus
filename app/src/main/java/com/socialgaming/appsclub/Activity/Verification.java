package com.socialgaming.appsclub.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.socialgaming.appsclub.R;
import com.socialgaming.appsclub.Util.API;
import com.socialgaming.appsclub.Util.Constant_Api;
import com.socialgaming.appsclub.Util.Method;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import cz.msebera.android.httpclient.Header;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;


public class Verification extends AppCompatActivity {

    private Method method;
    private PinView pinView;
    private String verification, name, email, password, phoneNo, reference;
    private InputMethodManager imm;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;

    private String mVerificationCode;
    private PhoneAuthProvider.ForceResendingToken mToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        mAuth = FirebaseAuth.getInstance();
        Method.forceRTLIfSupported(getWindow(), Verification.this);

        method = new Method(Verification.this);

        progressDialog = new ProgressDialog(Verification.this);

        Intent intent = getIntent();
        if (intent.hasExtra("name")) {
            name = intent.getStringExtra("name");
            email = intent.getStringExtra("email");
            password = intent.getStringExtra("password");
            phoneNo = intent.getStringExtra("phoneNo");
            reference = intent.getStringExtra("reference");
        } else {
            name = method.pref.getString(method.reg_name, null);
            email = method.pref.getString(method.reg_email, null);
            password = method.pref.getString(method.reg_password, null);
            phoneNo = method.pref.getString(method.reg_phoneNo, null);
            reference = method.pref.getString(method.reg_reference, null);
        }

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        pinView = findViewById(R.id.firstPinView);
        Button button_verification = findViewById(R.id.button_verification);
        Button button_register = findViewById(R.id.button_register_verification);
        TextView textView = findViewById(R.id.resend_verification);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resend_verification();
            }
        });

        button_verification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verification = pinView.getText().toString();
                pinView.clearFocus();
                imm.hideSoftInputFromWindow(pinView.getWindowToken(), 0);

                if (verification == null || verification.equals("") || verification.isEmpty()) {
                    Toast.makeText(Verification.this, getResources().getString(R.string.please_enter_verification_code), Toast.LENGTH_SHORT).show();
                } else {
                    if (Method.isNetworkAvailable(Verification.this)) {
                        pinView.setText("");

                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationCode, verification);
                        signInWithPhoneCredential(credential);

                    } else {
                        method.alertBox(getResources().getString(R.string.internet_connection));
                    }

                }
            }
        });

        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                method.editor.putBoolean(method.is_verification, false);
                method.editor.commit();
                startActivity(new Intent(Verification.this, Register.class));
                finishAffinity();
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                progressDialog.dismiss();
                signInWithPhoneCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                progressDialog.dismiss();
                Log.d("TAGG", e.getMessage());
                method.alertBox("Error in verification : "+ e.getLocalizedMessage());
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                progressDialog.dismiss();
                mVerificationCode = s;
                mToken = forceResendingToken;
            }
        };
        getVerificationOtp("+"+phoneNo);
    }

    private void signInWithPhoneCredential(PhoneAuthCredential phoneAuthCredential) {
        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);
        mAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            register(name, email, password, phoneNo, reference);
//                            FirebaseUser user = task.getResult().getUser();
                        }else{
                            method.alertBox("Error");
                            if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                                method.alertBox(getResources().getString(R.string.verification_message));
                            }
                        }
                    }
                });
    }

    public  void getVerificationOtp(String phoneNumber){
        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        PhoneAuthProvider.getInstance()
                .verifyPhoneNumber(
                        phoneNumber,
                        60,
                        TimeUnit.SECONDS,
                        Verification.this,
                        mCallbacks
                );
    }

    public void verification() {

        pinView.clearFocus();
        imm.hideSoftInputFromWindow(pinView.getWindowToken(), 0);

        if (verification == null || verification.equals("") || verification.isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.please_enter_verification_code), Toast.LENGTH_SHORT).show();
        } else {
            if (Method.isNetworkAvailable(Verification.this)) {
                pinView.setText("");
                if (verification.equals(method.pref.getString(method.verification_code, null))) {
                    register(name, email, password, phoneNo, reference);
                } else {
                    method.alertBox(getResources().getString(R.string.verification_message));
                }
            } else {
                method.alertBox(getResources().getString(R.string.internet_connection));
            }

        }
    }

    @SuppressLint("HardwareIds")
    public void register(String sendName, String sendEmail, String sendPassword, String sendPhone, String reference) {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        String device_id;
        try {
            device_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            device_id = "Not Found";
        }

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(Verification.this));
        jsObj.addProperty("method_name", "user_register");
        jsObj.addProperty("type", "normal");
        jsObj.addProperty("name", sendName);
        jsObj.addProperty("email", sendEmail);
        jsObj.addProperty("password", sendPassword);
        jsObj.addProperty("phone", sendPhone);
        jsObj.addProperty("device_id", device_id);
        jsObj.addProperty("user_refrence_code", reference);
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant_Api.url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                Log.d("Response", new String(responseBody));
                String res = new String(responseBody);

                String msg = null;
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
                            msg = object.getString("msg");
                            String success = object.getString("success");

                            method.editor.putBoolean(method.is_verification, false);
                            method.editor.commit();

                            if (success.equals("1")) {
                                Toast.makeText(Verification.this, msg, Toast.LENGTH_LONG).show();
                                startActivity(new Intent(Verification.this, Login.class));
                                finishAffinity();
                            } else {
                                Toast.makeText(Verification.this, msg, Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Verification.this, Login.class));
                                finishAffinity();
                            }

                        }

                    }

                    progressDialog.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    method.alertBox(getResources().getString(R.string.register_sukses));
                    Toast.makeText(Verification.this, msg, Toast.LENGTH_LONG).show();
                    startActivity(new Intent(Verification.this, Login.class));
                    finishAffinity();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.show();
            }
        });
    }

    public void resend_verification() {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNo,
                60,
                TimeUnit.SECONDS,
                Verification.this,
                mCallbacks,
                mToken
        );
    }
}
