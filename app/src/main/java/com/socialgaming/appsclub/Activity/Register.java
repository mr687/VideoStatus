package com.socialgaming.appsclub.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthActionCodeException;
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


public class Register extends AppCompatActivity {

    private Method method;
    private EditText editText_name, editText_email, editText_password, editText_phoneNo, editText_reference;
    private String name, email, password, phoneNo;
    private String reference = "";
    private InputMethodManager imm;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;

    private String mVerificationCode;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

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
        setContentView(R.layout.activity_register);

        Method.forceRTLIfSupported(getWindow(), Register.this);

        mAuth = FirebaseAuth.getInstance();

        method = new Method(Register.this);

        progressDialog = new ProgressDialog(Register.this);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        editText_name = findViewById(R.id.editText_name_register);
        editText_email = findViewById(R.id.editText_email_register);
        editText_password = findViewById(R.id.editText_password_register);
        editText_phoneNo = findViewById(R.id.editText_phoneNo_register);
        editText_reference = findViewById(R.id.editText_reference_code_register);

        TextView textView_login = findViewById(R.id.textView_login_register);

        textView_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                method.editor.putBoolean(method.is_verification, false);
                method.editor.commit();
                startActivity(new Intent(Register.this, Login.class));
                finishAffinity();
            }
        });

        Button button_submit = findViewById(R.id.button_submit);
        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name = editText_name.getText().toString();
                email = editText_email.getText().toString();
                password = editText_password.getText().toString();
                phoneNo = editText_phoneNo.getText().toString();
                reference = editText_reference.getText().toString();

                form();

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private boolean isValidMail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void form() {

        editText_name.setError(null);
        editText_email.setError(null);
        editText_password.setError(null);
        editText_phoneNo.setError(null);

        if (name.equals("") || name.isEmpty()) {
            editText_name.requestFocus();
            editText_name.setError(getResources().getString(R.string.please_enter_name));
        } else if (!isValidMail(email) || email.isEmpty()) {
            editText_email.requestFocus();
            editText_email.setError(getResources().getString(R.string.please_enter_email));
        } else if (password.equals("") || password.isEmpty()) {
            editText_password.requestFocus();
            editText_password.setError(getResources().getString(R.string.please_enter_password));
        } else if (phoneNo.equals("") || phoneNo.isEmpty()) {
            editText_phoneNo.requestFocus();
            editText_phoneNo.setError(getResources().getString(R.string.please_enter_phone));
        } else {

            editText_name.clearFocus();
            editText_email.clearFocus();
            editText_password.clearFocus();
            editText_phoneNo.clearFocus();
            imm.hideSoftInputFromWindow(editText_name.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editText_email.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editText_password.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editText_phoneNo.getWindowToken(), 0);

            if (Method.isNetworkAvailable(Register.this)) {

                Random generator = new Random();
                int n = generator.nextInt(9999 - 1000) + 1000;

                verification_otp(phoneNo);

            } else {
                method.alertBox(getResources().getString(R.string.internet_connection));
            }

        }
    }

    private void verification_otp(String phoneNumber) {
        method.editor.putBoolean(method.is_verification, true);
        method.editor.putString(method.reg_name, name);
        method.editor.putString(method.reg_email, email);
        method.editor.putString(method.reg_password, password);
        method.editor.putString(method.reg_phoneNo, phoneNo);
        method.editor.putString(method.reg_reference, reference);
        method.editor.commit();

        startActivity(new Intent(Register.this, Verification.class)
                .putExtra("name", name)
                .putExtra("email", email)
                .putExtra("password", password)
                .putExtra("phoneNo", phoneNo)
                .putExtra("reference", reference));
        finishAffinity();
    }

}
