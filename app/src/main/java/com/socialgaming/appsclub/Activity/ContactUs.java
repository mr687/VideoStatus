package com.socialgaming.appsclub.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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

import com.socialgaming.appsclub.Item.ContactList;
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

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import cz.msebera.android.httpclient.Header;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;


public class ContactUs extends AppCompatActivity {

    public Toolbar toolbar;
    private Method method;
    private String contact_type, contact_id;
    private Spinner spinner;
    private Button button_submit;
    private List<ContactList> contactLists;
    private EditText editText_name, editText_email, editText_message;
    private String name, email, message;
    private LinearLayout linearLayout;
    private InputMethodManager imm;
    private ProgressDialog progressDialog;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        Method.forceRTLIfSupported(getWindow(), ContactUs.this);

        method = new Method(ContactUs.this);

        contactLists = new ArrayList<>();

        toolbar = findViewById(R.id.toolbar_contact_us);
        toolbar.setTitle(getResources().getString(R.string.contact_us));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        progressDialog = new ProgressDialog(ContactUs.this);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        spinner = findViewById(R.id.spinner_contact_us);
        editText_name = findViewById(R.id.editText_name_contact_us);
        editText_email = findViewById(R.id.editText_email_contact_us);
        editText_message = findViewById(R.id.editText_message_contact_us);

        if (method.pref.getBoolean(method.pref_login, false)) {
            editText_name.setText(method.pref.getString(method.userName, ""));
            editText_email.setText(method.pref.getString(method.userEmail, ""));
        }

        linearLayout = findViewById(R.id.linearLayout_contact_us);

        if (method.personalization_ad) {
            method.showPersonalizedAds(linearLayout);
        } else {
            method.showNonPersonalizedAds(linearLayout);
        }

        button_submit = findViewById(R.id.button_contact_us);

        if (Method.isNetworkAvailable(ContactUs.this)) {
            getContact();
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

    }


    private boolean isValidMail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void form() {

        editText_name.setError(null);
        editText_email.setError(null);
        editText_message.setError(null);

        if (contact_type.equals(getResources().getString(R.string.select_contact_type)) || contact_type.equals("") || contact_type.isEmpty()) {
            method.alertBox(getResources().getString(R.string.please_select_category));
        } else if (name.equals("") || name.isEmpty()) {
            editText_name.requestFocus();
            editText_name.setError(getResources().getString(R.string.please_enter_name));
        } else if (!isValidMail(email) || email.isEmpty()) {
            editText_email.requestFocus();
            editText_email.setError(getResources().getString(R.string.please_enter_email));
        } else if (message.equals("") || message.isEmpty()) {
            editText_message.requestFocus();
            editText_message.setError(getResources().getString(R.string.please_enter_message));
        } else {

            editText_name.clearFocus();
            editText_email.clearFocus();
            editText_message.clearFocus();
            imm.hideSoftInputFromWindow(editText_name.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editText_email.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editText_message.getWindowToken(), 0);

            if (Method.isNetworkAvailable(ContactUs.this)) {
                contact_us(email, name, message, contact_id);
            } else {
                method.alertBox(getResources().getString(R.string.internet_connection));
            }
        }
    }

    public void getContact() {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(ContactUs.this));
        jsObj.addProperty("method_name", "get_contact");
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
                            String id = object.getString("id");
                            String subject = object.getString("subject");

                            contactLists.add(new ContactList(id, subject));

                        }

                        contactLists.add(0, new ContactList("", getResources().getString(R.string.select_contact_type)));

                        // Spinner Drop down elements
                        List<String> strings = new ArrayList<String>();
                        for (int i = 0; i < contactLists.size(); i++) {
                            strings.add(contactLists.get(i).getType());
                        }

                        // Creating adapter for spinner_cat
                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ContactUs.this, android.R.layout.simple_spinner_item, strings);
                        // Drop down layout style - list view with radio button
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        // attaching data adapter to spinner_cat
                        spinner.setAdapter(dataAdapter);

                        // Spinner click listener
                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (position == 0) {
                                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.textView_upload_fragment));
                                    contact_type = contactLists.get(position).getType();
                                    contact_id = contactLists.get(position).getId();
                                } else {
                                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.toolbar));
                                    contact_type = contactLists.get(position).getType();
                                    contact_id = contactLists.get(position).getId();
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                        button_submit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                name = editText_name.getText().toString();
                                email = editText_email.getText().toString();
                                message = editText_message.getText().toString();

                                form();

                            }
                        });

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

    public void contact_us(String sendEmail, String sendName, String sendMessage, String contact_subject) {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(ContactUs.this));
        jsObj.addProperty("method_name", "user_contact_us");
        jsObj.addProperty("contact_email", sendEmail);
        jsObj.addProperty("contact_name", sendName);
        jsObj.addProperty("contact_msg", sendMessage);
        jsObj.addProperty("contact_subject", contact_subject);
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

                                editText_name.setText("");
                                editText_email.setText("");
                                editText_message.setText("");

                                spinner.setSelection(0);

                                method.alertBox(msg);
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
