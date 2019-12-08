package com.socialgaming.appsclub.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.socialgaming.appsclub.Adapter.VPAdapterSS;
import com.socialgaming.appsclub.R;
import com.socialgaming.appsclub.Util.Method;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;


public class StatusSaver extends AppCompatActivity {

    private Method method;
    public Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String type;
    private LinearLayout linearLayout;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_saver);

        Method.forceRTLIfSupported(getWindow(), StatusSaver.this);

        method = new Method(StatusSaver.this);

        String[] tab_title = {getResources().getString(R.string.image), getResources().getString(R.string.video)};

        toolbar = findViewById(R.id.toolbar_ss);
        toolbar.setTitle(getResources().getString(R.string.status_saver));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        type = getIntent().getStringExtra("type");

        tabLayout = findViewById(R.id.tab_layout_ss);
        viewPager = findViewById(R.id.viewpager_ss);

        linearLayout = findViewById(R.id.linearLayout_ss);
        if (method.personalization_ad) {
            method.showPersonalizedAds(linearLayout);
        } else {
            method.showNonPersonalizedAds(linearLayout);
        }

        for (int i = 0; i < 2; i++) {
            tabLayout.addTab(tabLayout.newTab().setText(tab_title[i]));
        }

        //create and set ViewPager adapter
        VPAdapterSS vpAdapterSS = new VPAdapterSS(getSupportFragmentManager(), tabLayout.getTabCount(), StatusSaver.this, type);
        viewPager.setAdapter(vpAdapterSS);

        //change selected tab when viewpager changed page
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        //change viewpager page when tab selected
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.status_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.download_status_menu);
        if (type.equals("status")) {
            menuItem.setVisible(true);
        } else {
            menuItem.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.download_status_menu:
                startActivity(new Intent(StatusSaver.this, StatusSaver.class)
                        .putExtra("type", "download_status"));
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
