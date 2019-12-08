package com.socialgaming.appsclub.Activity;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import android.text.Html;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.socialgaming.appsclub.R;
import com.socialgaming.appsclub.Util.Constant_Api;
import com.socialgaming.appsclub.Util.Method;



public class PrivacyPolice extends AppCompatActivity {

    public Toolbar toolbar;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        Method.forceRTLIfSupported(getWindow(), PrivacyPolice.this);

        Method method = new Method(PrivacyPolice.this);

        toolbar = findViewById(R.id.toolbar_privacy_policy);
        toolbar.setTitle(getResources().getString(R.string.privacy_policy));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        TextView textView = findViewById(R.id.textview_privacy_policy);

        LinearLayout linearLayout = findViewById(R.id.linearLayout_privacy_policy);

        if (method.personalization_ad) {
            method.showPersonalizedAds(linearLayout);
        } else {
            method.showNonPersonalizedAds(linearLayout);
        }

        if (Constant_Api.aboutUsList != null) {
            textView.setText(Html.fromHtml(Constant_Api.aboutUsList.getApp_privacy_policy()));
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
