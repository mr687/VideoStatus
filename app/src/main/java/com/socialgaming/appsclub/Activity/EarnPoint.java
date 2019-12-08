package com.socialgaming.appsclub.Activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.socialgaming.appsclub.Adapter.EarnPointAdapter;
import com.socialgaming.appsclub.Item.EarnPointList;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cz.msebera.android.httpclient.Header;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class EarnPoint extends AppCompatActivity {

    private Method method;
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private TextView textView_noData;
    private RecyclerView recyclerView;
    private List<EarnPointList> earnPointLists;
    private EarnPointAdapter earnPointAdapter;
    private LinearLayout linearLayout;
    private LayoutAnimationController animation;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earn_point);

        Method.forceRTLIfSupported(getWindow(), EarnPoint.this);

        method = new Method(EarnPoint.this);

        earnPointLists = new ArrayList<>();

        int resId = R.anim.layout_animation_fall_down;
        animation = AnimationUtils.loadLayoutAnimation(EarnPoint.this, resId);

        toolbar = findViewById(R.id.toolbar_ep);
        toolbar.setTitle(getResources().getString(R.string.earn_point));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        linearLayout = findViewById(R.id.linearLayout_ep);

        if (method.personalization_ad) {
            method.showPersonalizedAds(linearLayout);
        } else {
            method.showNonPersonalizedAds(linearLayout);
        }

        progressBar = findViewById(R.id.progressbar_ep);
        textView_noData = findViewById(R.id.textView_ep);
        recyclerView = findViewById(R.id.recyclerView_ep);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(EarnPoint.this);
        recyclerView.setLayoutManager(layoutManager);

        if (Method.isNetworkAvailable(EarnPoint.this)) {
            Point();
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
            progressBar.setVisibility(View.GONE);
        }

    }

    private void Point() {

        earnPointLists.clear();
        progressBar.setVisibility(View.VISIBLE);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(EarnPoint.this));
        jsObj.addProperty("method_name", "points_details");
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
                            String title = object.getString("title");
                            String point = object.getString("point");


                            earnPointLists.add(new EarnPointList(title, point));
                        }

                        if (earnPointLists.size() == 0) {
                            textView_noData.setVisibility(View.VISIBLE);
                        } else {
                            textView_noData.setVisibility(View.GONE);
                            earnPointAdapter = new EarnPointAdapter(EarnPoint.this, earnPointLists);
                            recyclerView.setAdapter(earnPointAdapter);
                            recyclerView.setLayoutAnimation(animation);
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
