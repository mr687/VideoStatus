package com.socialgaming.appsclub.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import cz.msebera.android.httpclient.Header;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class AVStatus extends AppCompatActivity {

    private Method method;
    public Toolbar toolbar;
    private ImageView imageView;
    private ProgressBar progressBar;
    private Button button;
    private View view;
    private LinearLayout linearLayout_adminMsg;
    private TextView textView_noData, textView_userName, textView_statusMsg, textView_status,
            textView_date, textView_requestDate, textView_responseDate,
            textView_msg, textView_adminMsg, textView_note;
    private LinearLayout linearLayout;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avstatus);

        Method.forceRTLIfSupported(getWindow(), AVStatus.this);

        method = new Method(AVStatus.this);

        toolbar = findViewById(R.id.toolbar_avs);
        toolbar.setTitle(getResources().getString(R.string.verification_status));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        progressBar = findViewById(R.id.progressbar_avs);
        textView_noData = findViewById(R.id.textView_noData_avs);
        imageView = findViewById(R.id.imageView_avs);
        button = findViewById(R.id.button_avs);
        view = findViewById(R.id.view_date_avs);
        textView_userName = findViewById(R.id.textView_name_avs);
        textView_statusMsg = findViewById(R.id.textView_statusMsg_avs);
        textView_status = findViewById(R.id.textView_avs);
        textView_date = findViewById(R.id.textView_date_avs);
        textView_requestDate = findViewById(R.id.textView_requestDate_avs);
        textView_responseDate = findViewById(R.id.textView_responseDate_avs);
        textView_msg = findViewById(R.id.textView_msg_avs);
        textView_adminMsg = findViewById(R.id.textView_adminMsg_avs);
        textView_note = findViewById(R.id.textView_note_avs);
        linearLayout_adminMsg = findViewById(R.id.linearLayout_adminMsg_avs);

        button.setVisibility(View.GONE);
        linearLayout_adminMsg.setVisibility(View.GONE);

        linearLayout = findViewById(R.id.linearLayout_avs);

        if (method.personalization_ad) {
            method.showPersonalizedAds(linearLayout);
        } else {
            method.showNonPersonalizedAds(linearLayout);
        }

        if (Method.isNetworkAvailable(AVStatus.this)) {
            if (method.pref.getBoolean(method.pref_login, false)) {
                detail(method.pref.getString(method.profileId, null));
            } else {
                progressBar.setVisibility(View.GONE);
                textView_noData.setText(getResources().getString(R.string.you_have_not_login));
            }
        } else {
            progressBar.setVisibility(View.GONE);
            textView_noData.setText(getResources().getString(R.string.no_data_found));
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

    }

    private void detail(String user_id) {

        progressBar.setVisibility(View.VISIBLE);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(AVStatus.this));
        jsObj.addProperty("method_name", "verfication_details");
        jsObj.addProperty("user_id", user_id);
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
                        if (status.equals("-2")) {
                            method.suspend(message);
                        } else {
                            method.alertBox(message);
                        }

                    } else {

                        JSONArray jsonArray = jsonObject.getJSONArray(Constant_Api.tag);

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject object = jsonArray.getJSONObject(i);
                            String document_img = object.getString("document_img");
                            String request_date = object.getString("request_date");
                            String response_date = object.getString("response_date");
                            String user_message = object.getString("user_message");
                            String admin_message = object.getString("admin_message");
                            String user_full_name = object.getString("user_full_name");
                            String status = object.getString("status");

                            textView_noData.setVisibility(View.GONE);

                            if (!document_img.equals("")) {
                                Glide.with(AVStatus.this).load(document_img)
                                        .placeholder(R.drawable.placeholder_portable)
                                        .into(imageView);

                                imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        startActivity(new Intent(AVStatus.this, TDView.class)
                                                .putExtra("path", document_img));
                                    }
                                });

                            }

                            if (status.equals("1") || status.equals("2")) {
                                if (status.equals("1")) {
                                    textView_date.setTextColor(getResources().getColor(R.color.green));
                                    textView_date.setText(getResources().getString(R.string.approve_date));
                                } else {
                                    textView_date.setTextColor(getResources().getColor(R.color.red));
                                    textView_date.setText(getResources().getString(R.string.reject_date));
                                }
                                textView_responseDate.setText(response_date);
                            } else {
                                view.setVisibility(View.GONE);
                                textView_responseDate.setVisibility(View.GONE);
                                textView_date.setVisibility(View.GONE);
                            }

                            textView_userName.setText(user_full_name);
                            textView_requestDate.setText(request_date);
                            textView_msg.setText(user_message);
                            textView_adminMsg.setText(admin_message);

                            switch (status) {
                                case "0":
                                    button.setVisibility(View.VISIBLE);
                                    linearLayout_adminMsg.setVisibility(View.GONE);
                                    textView_note.setVisibility(View.VISIBLE);
                                    textView_note.setText(getResources().getString(R.string.new_request));
                                    textView_status.setText(getResources().getString(R.string.pending));
                                    textView_status.setTextColor(getResources().getColor(R.color.toolbar));
                                    textView_statusMsg.setText(getResources().getString(R.string.account_pending));
                                    textView_statusMsg.setTextColor(getResources().getColor(R.color.toolbar));
                                    break;
                                case "1":
                                    button.setVisibility(View.GONE);
                                    linearLayout_adminMsg.setVisibility(View.GONE);
                                    textView_note.setVisibility(View.GONE);
                                    textView_status.setText(getResources().getString(R.string.approve));
                                    textView_date.setTextColor(getResources().getColor(R.color.green));
                                    textView_statusMsg.setText(getResources().getString(R.string.account_approve));
                                    textView_statusMsg.setTextColor(getResources().getColor(R.color.green));
                                    break;
                                case "2":
                                    button.setVisibility(View.VISIBLE);
                                    linearLayout_adminMsg.setVisibility(View.VISIBLE);
                                    textView_note.setVisibility(View.VISIBLE);
                                    textView_note.setText(getResources().getString(R.string.reject_request));
                                    textView_status.setText(getResources().getString(R.string.reject));
                                    textView_date.setTextColor(getResources().getColor(R.color.red));
                                    textView_statusMsg.setText(getResources().getString(R.string.account_disapprove));
                                    textView_statusMsg.setTextColor(getResources().getColor(R.color.red));
                                    break;
                            }

                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(AVStatus.this, AccountVerification.class)
                                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                    finish();
                                }
                            });

                        }

                    }

                    progressBar.setVisibility(View.GONE);

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

            }


            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressBar.setVisibility(View.GONE);
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
