package com.socialgaming.appsclub.Activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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

import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class Suspend extends AppCompatActivity {

    private Method method;
    private Toolbar toolbar;
    private String account_id;
    private CircleImageView imageView;
    private ProgressBar progressBar;
    private Button button;
    private LinearLayout linearLayout_msg;
    private RelativeLayout relativeLayout;
    private TextView textView_noData, textView_userName, textView_statusMsg, textView_status, textView_date, textView_admin_msg;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suspend);

        Method.forceRTLIfSupported(getWindow(), Suspend.this);

        method = new Method(Suspend.this);

        toolbar = findViewById(R.id.toolbar_suspend);
        toolbar.setTitle(getResources().getString(R.string.account_status));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        account_id = getIntent().getStringExtra("account_id");

        progressBar = findViewById(R.id.progressbar_suspend);
        textView_noData = findViewById(R.id.textView_noData_suspend);
        relativeLayout = findViewById(R.id.relativeLayout_suspend);
        button = findViewById(R.id.button_suspend);
        imageView = findViewById(R.id.imageView_suspend);
        linearLayout_msg = findViewById(R.id.linearLayout_msg_suspend);
        textView_userName = findViewById(R.id.textView_userName_suspend);
        textView_statusMsg = findViewById(R.id.textView_statusMsg_suspend);
        textView_status = findViewById(R.id.textView_suspend);
        textView_date = findViewById(R.id.textView_date_suspend);
        textView_admin_msg = findViewById(R.id.textView_admin_msg_suspend);

        relativeLayout.setVisibility(View.GONE);

        if (Method.isNetworkAvailable(Suspend.this)) {
            userAccount();
        } else {
            relativeLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            textView_noData.setText(getResources().getString(R.string.no_data_found));
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAffinity();
            }
        });

    }

    public void userAccount() {

        progressBar.setVisibility(View.VISIBLE);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(Suspend.this));
        jsObj.addProperty("method_name", "user_suspend");
        jsObj.addProperty("account_id", account_id);
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

                        textView_noData.setVisibility(View.GONE);
                        relativeLayout.setVisibility(View.VISIBLE);

                        String status = jsonObject.getString("success");
                        String user_image = jsonObject.getString("user_image");
                        String user_name = jsonObject.getString("user_name");
                        String is_verified = jsonObject.getString("is_verified");
                        String date = jsonObject.getString("date");
                        String msg = jsonObject.getString("msg");

                        if (!user_image.equals("")) {
                            Glide.with(Suspend.this).load(user_image)
                                    .placeholder(R.drawable.user_profile).into(imageView);
                        }

                        if (status.equals("1")) {
                            linearLayout_msg.setVisibility(View.GONE);
                            textView_status.setText(getResources().getString(R.string.active));
                            textView_status.setTextColor(getResources().getColor(R.color.green));
                            textView_statusMsg.setText(getResources().getString(R.string.msg_approved));
                            textView_statusMsg.setTextColor(getResources().getColor(R.color.green));
                        } else {

                            linearLayout_msg.setVisibility(View.VISIBLE);

                            if (method.pref.getBoolean(method.pref_login, false)) {
                                method.editor.putBoolean(method.pref_login, false);
                                method.editor.commit();
                            }

                            textView_status.setText(getResources().getString(R.string.suspend));
                            textView_status.setTextColor(getResources().getColor(R.color.red));
                            textView_statusMsg.setText(getResources().getString(R.string.msg_suspend));
                            textView_statusMsg.setTextColor(getResources().getColor(R.color.red));
                        }

                        textView_userName.setText(user_name);
                        if (is_verified.equals("true")) {
                            textView_userName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_verification, 0);
                        }
                        textView_date.setText(date);
                        textView_admin_msg.setText(msg);

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
