package com.socialgaming.appsclub.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.socialgaming.appsclub.R;
import com.socialgaming.appsclub.Util.API;
import com.socialgaming.appsclub.Util.Constant_Api;
import com.socialgaming.appsclub.Util.Events;
import com.socialgaming.appsclub.Util.GlobalBus;
import com.socialgaming.appsclub.Util.Method;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import cz.msebera.android.httpclient.Header;
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;


public class RewardPointClaim extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Method method;
    private Toolbar toolbar;
    private String payment_type;
    private Spinner spinner;
    private ArrayList<String> arrayList;
    private EditText editText_detail;
    private Button button_submit;
    private String user_id, user_points;
    private ProgressDialog progressDialog;
    private InputMethodManager imm;
    private LinearLayout linearLayout;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_point_claim);
        TextView textView_faq = findViewById(R.id.textView_faq_setting);
        Method.forceRTLIfSupported(getWindow(), RewardPointClaim.this);
        View yourView = findViewById(R.id.textView_faq_setting);
        new SimpleTooltip.Builder(this)
                .anchorView(yourView)
                .text("Please Read This")
                .gravity(Gravity.BOTTOM)
                .animated(true)
                .transparentOverlay(false)
                .build()
                .show();
        method = new Method(RewardPointClaim.this);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        arrayList = new ArrayList<>();

        Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");
        user_points = intent.getStringExtra("user_points");

        progressDialog = new ProgressDialog(RewardPointClaim.this);

        Method.forceRTLIfSupported(getWindow(), RewardPointClaim.this);

        toolbar = findViewById(R.id.toolbar_reward_point_claim);
        toolbar.setTitle(getResources().getString(R.string.detail));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        linearLayout = findViewById(R.id.linearLayout_reward_point_claim);

        if (method.personalization_ad) {
            method.showPersonalizedAds(linearLayout);
        } else {
            method.showNonPersonalizedAds(linearLayout);
        }

        spinner = findViewById(R.id.spinner_reward_point_claim);
        editText_detail = findViewById(R.id.editText_detail_reward_point_claim);
        button_submit = findViewById(R.id.button_reward_point_claim);

        // Spinner click listener
        spinner.setOnItemSelectedListener(RewardPointClaim.this);

        if (Method.isNetworkAvailable(RewardPointClaim.this)) {
            new Payment().execute();
        } else {
            Toast.makeText(this, getResources().getString(R.string.internet_connection), Toast.LENGTH_SHORT).show();
        }
        textView_faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RewardPointClaim.this, Faq.class));
            }
        });

        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText_detail.clearFocus();
                imm.hideSoftInputFromWindow(editText_detail.getWindowToken(), 0);
                detail();
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //first list item selected by default and sets the preset accordingly
        if (position == 0) {
            ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.textView_upload_fragment));
            payment_type = arrayList.get(position);
        } else {
            ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.toolbar));
            payment_type = arrayList.get(position);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @SuppressLint("StaticFieldLeak")
    private class Payment extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.show();
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.setCancelable(false);

        }

        @Override
        protected String doInBackground(String... strings) {

            arrayList.add(getResources().getString(R.string.select_payment_type));
            if (Constant_Api.aboutUsList != null) {
                if (!Constant_Api.aboutUsList.getPayment_method1().equals("")) {
                    arrayList.add(Constant_Api.aboutUsList.getPayment_method1());
                }
                if (!Constant_Api.aboutUsList.getPayment_method2().equals("")) {
                    arrayList.add(Constant_Api.aboutUsList.getPayment_method2());
                }
                if (!Constant_Api.aboutUsList.getPayment_method3().equals("")) {
                    arrayList.add(Constant_Api.aboutUsList.getPayment_method3());
                }
                if (!Constant_Api.aboutUsList.getPayment_method4().equals("")) {
                    arrayList.add(Constant_Api.aboutUsList.getPayment_method4());
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            // Creating adapter for spinner
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(RewardPointClaim.this, android.R.layout.simple_spinner_item, arrayList);
            // Drop down layout style - list view with radio button
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // attaching data adapter to spinner
            spinner.setAdapter(dataAdapter);

            progressDialog.dismiss();

            super.onPostExecute(s);
        }
    }

    public void detail() {

        String detail = editText_detail.getText().toString();

        editText_detail.setError(null);
        if (payment_type.equals(getResources().getString(R.string.select_payment_type)) || payment_type.equals("") || payment_type.isEmpty()) {
            Toast.makeText(RewardPointClaim.this, getResources().getString(R.string.please_select_payment), Toast.LENGTH_SHORT).show();
        } else if (detail.equals("") || detail.isEmpty()) {
            editText_detail.requestFocus();
            editText_detail.setError(getResources().getString(R.string.please_enter_detail));
        } else {
            if (Method.isNetworkAvailable(RewardPointClaim.this)) {
                detail_submit(user_id, user_points, payment_type, detail);
            } else {
                method.alertBox(getResources().getString(R.string.internet_connection));
            }
        }

    }

    public void detail_submit(final String user_id, final String user_points, String payment_mode, String detail) {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(RewardPointClaim.this));
        jsObj.addProperty("method_name", "user_redeem_request");
        jsObj.addProperty("user_id", user_id);
        jsObj.addProperty("user_points", user_points);
        jsObj.addProperty("payment_mode", payment_mode);
        jsObj.addProperty("bank_details", detail);
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
                                Toast.makeText(RewardPointClaim.this, msg, Toast.LENGTH_SHORT).show();
                                Events.RewardNotify rewardNotify = new Events.RewardNotify("");
                                GlobalBus.getBus().post(rewardNotify);
                                onBackPressed();
                            } else {
                                Toast.makeText(RewardPointClaim.this, msg, Toast.LENGTH_SHORT).show();
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
