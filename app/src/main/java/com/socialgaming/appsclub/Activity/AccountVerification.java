package com.socialgaming.appsclub.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.socialgaming.appsclub.R;
import com.socialgaming.appsclub.Util.API;
import com.socialgaming.appsclub.Util.Constant_Api;
import com.socialgaming.appsclub.Util.Method;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nguyenhoanglam.imagepicker.model.Config;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import cz.msebera.android.httpclient.Header;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class AccountVerification extends AppCompatActivity {

    private Method method;
    public Toolbar toolbar;
    private ProgressDialog progressDialog;
    private String document_image;
    private Button button;
    private ImageView imageView;
    private LinearLayout linearLayout_image;
    private TextView textView_noData, textView_title, textView_image;
    private EditText editText_userName, editText_email, editText_full_name, editText_msg;
    private ArrayList<Image> galleryImages;
    private InputMethodManager imm;
    private int REQUEST_GALLERY_PICKER = 100;
    final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 101;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_verification);

        Method.forceRTLIfSupported(getWindow(), AccountVerification.this);

        method = new Method(AccountVerification.this);

        galleryImages = new ArrayList<>();

        toolbar = findViewById(R.id.toolbar_av);
        toolbar.setTitle(getResources().getString(R.string.request_verification));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        progressDialog = new ProgressDialog(AccountVerification.this);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        textView_noData = findViewById(R.id.textView_av);
        textView_title = findViewById(R.id.textView_title_av);
        textView_image = findViewById(R.id.textView_image_av);
        editText_userName = findViewById(R.id.editText_userName_av);
        editText_email = findViewById(R.id.editText_email_av);
        editText_full_name = findViewById(R.id.editText_full_name_av);
        editText_msg = findViewById(R.id.editText_msg_av);
        imageView = findViewById(R.id.imageView_av);
        linearLayout_image = findViewById(R.id.linearLayout_image_av);
        button = findViewById(R.id.button_av);

        editText_userName.clearFocus();
        editText_userName.setFocusable(false);
        editText_email.clearFocus();
        editText_email.setFocusable(false);

        textView_title.setText(getResources().getString(R.string.apply_for)
                + " " + getResources().getString(R.string.app_name)
                + " " + getResources().getString(R.string.verification));

        LinearLayout linearLayout = findViewById(R.id.linearLayout_av);

        if (method.personalization_ad) {
            method.showPersonalizedAds(linearLayout);
        } else {
            method.showNonPersonalizedAds(linearLayout);
        }

        if (Method.isNetworkAvailable(AccountVerification.this)) {
            if (method.pref.getBoolean(method.pref_login, false)) {
                getData();
                textView_noData.setVisibility(View.GONE);
            } else {
                method.alertBox(getResources().getString(R.string.you_have_not_login));
                textView_noData.setText(getResources().getString(R.string.you_have_not_login));
            }
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
            textView_noData.setText(getResources().getString(R.string.no_data_found));
        }

    }

    private Boolean checkPer() {
            if ((ContextCompat.checkSelfPermission(AccountVerification.this, "android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"},
                            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                    return false;
                }
                return true;
            } else {
                return true;
            }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GALLERY_PICKER) {
            if (resultCode == RESULT_OK && data != null) {
                galleryImages = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
                Uri uri_banner = Uri.fromFile(new File(galleryImages.get(0).getPath()));
                document_image = galleryImages.get(0).getPath();
                Glide.with(AccountVerification.this).load(uri_banner)
                        .placeholder(R.drawable.placeholder_landscape).into(imageView);
                textView_image.setText(galleryImages.get(0).getPath());
            }
        }
    }

    public void chooseGalleryImage() {
        ImagePicker.with(this)
                .setFolderMode(true)
                .setFolderTitle("Album")
                .setImageTitle(getResources().getString(R.string.app_name))
                .setStatusBarColor("#f20056")
                .setToolbarColor("#f20056")
                .setProgressBarColor("#f20056")
                .setMultipleMode(true)
                .setMaxSize(1)
                .setShowCamera(false)
                .start();
    }

    private void getData() {


        editText_userName.setText(method.pref.getString(method.userName, null));
        editText_email.setText(method.pref.getString(method.userEmail, null));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = editText_userName.getText().toString();
                String email = editText_email.getText().toString();
                String full_name = editText_full_name.getText().toString();
                String msg = editText_msg.getText().toString();

                form(name, email, full_name, msg, document_image);

            }
        });

        linearLayout_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPer()) {
                    chooseGalleryImage();
                } else {
                    method.alertBox(getResources().getString(R.string.cannot_use_upload_allow));
                }
            }
        });

    }

    private void form(String name, String email, String full_name, String msg, String document) {

        editText_userName.setError(null);
        editText_email.setError(null);
        editText_full_name.setError(null);
        editText_msg.setError(null);

        if (name.equals("") || name.isEmpty()) {
            editText_userName.requestFocus();
            editText_userName.setError(getResources().getString(R.string.please_enter_name));
        } else if (!isValidMail(email) || email.isEmpty()) {
            editText_email.requestFocus();
            editText_email.setError(getResources().getString(R.string.please_enter_email));
        } else if (full_name.equals("") || full_name.isEmpty()) {
            editText_full_name.requestFocus();
            editText_full_name.setError(getResources().getString(R.string.please_enter_full_name));
        } else if (msg.equals("") || msg.isEmpty()) {
            editText_msg.requestFocus();
            editText_msg.setError(getResources().getString(R.string.please_enter_message));
        } else if (document == null || document.equals("") || document.isEmpty()) {
            method.alertBox(getResources().getString(R.string.please_select_image));
        } else {

            editText_full_name.clearFocus();
            editText_msg.clearFocus();
            imm.hideSoftInputFromWindow(editText_full_name.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editText_msg.getWindowToken(), 0);

            if (Method.isNetworkAvailable(AccountVerification.this)) {
                submit(method.pref.getString(method.profileId, null), full_name, msg, document);
            } else {
                method.alertBox(getResources().getString(R.string.internet_connection));
            }
        }

    }

    private boolean isValidMail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void submit(String user_id, String sendFullName, String sendMessage, String document) {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(AccountVerification.this));
        jsObj.addProperty("method_name", "profile_verify");
        jsObj.addProperty("user_id", user_id);
        jsObj.addProperty("full_name", sendFullName);
        jsObj.addProperty("message", sendMessage);
        params.put("data", API.toBase64(jsObj.toString()));
        try {
            params.put("document", new File(document));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
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

                        if(status.equals("-2")){
                            method.suspend(message);
                        }else {
                            method.alertBox(message);
                        }

                    } else {

                        JSONArray jsonArray = jsonObject.getJSONArray(Constant_Api.tag);

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject object = jsonArray.getJSONObject(i);
                            String msg = object.getString("msg");
                            String success = object.getString("success");

                            if (success.equals("1")) {

                                editText_full_name.setText("");
                                editText_msg.setText("");
                                document_image = "";
                                textView_image.setText(getResources().getString(R.string.add_thumbnail_file));
                                Glide.with(AccountVerification.this)
                                        .load(R.drawable.placeholder_landscape).into(imageView);

                                onBackPressed();

                                Toast.makeText(AccountVerification.this, msg, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(AccountVerification.this, msg, Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        super.onBackPressed();
    }

}
