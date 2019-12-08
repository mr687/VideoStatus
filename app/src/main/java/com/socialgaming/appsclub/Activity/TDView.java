package com.socialgaming.appsclub.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.socialgaming.appsclub.R;
import com.socialgaming.appsclub.Util.Method;
import com.socialgaming.appsclub.Util.TouchImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class TDView extends AppCompatActivity {

    private Method method;
    public Toolbar toolbar;
    private String path;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tdview);

        Method.forceRTLIfSupported(getWindow(), TDView.this);

        method = new Method(TDView.this);

        toolbar = findViewById(R.id.toolbar_tdview);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        // making notification bar transparent
        changeStatusBarColor();

        Intent in = getIntent();
        path = in.getStringExtra("path");

        TouchImageView imageView = findViewById(R.id.imagView_tdview);
        Glide.with(TDView.this).load(path)
                .placeholder(R.drawable.placeholder_landscape).into(imageView);

        LinearLayout linearLayout = findViewById(R.id.linearLayout_tdview);
        if (method.personalization_ad) {
            method.showPersonalizedAds(linearLayout);
        } else {
            method.showNonPersonalizedAds(linearLayout);
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

}
