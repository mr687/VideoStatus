package com.socialgaming.appsclub.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.socialgaming.appsclub.Fragment.SCDetailFragment;
import com.socialgaming.appsclub.R;
import com.socialgaming.appsclub.Util.Events;
import com.socialgaming.appsclub.Util.GlobalBus;
import com.socialgaming.appsclub.Util.Method;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;


public class NotificationDetail extends AppCompatActivity {

    public static Toolbar toolbar_notification;
    final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 101;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_detail);

        Method.forceRTLIfSupported(getWindow(), NotificationDetail.this);

        Method method = new Method(NotificationDetail.this);

        toolbar_notification = findViewById(R.id.toolbar_notification_detail);
        toolbar_notification.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(toolbar_notification);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        LinearLayout linearLayout = findViewById(R.id.linearLayout_notification_detail);

        String video_id = getIntent().getStringExtra("video_id");
        SCDetailFragment scDetailFragment = new SCDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString("id", video_id);
        bundle.putString("type", "notification");
        bundle.putInt("position", 0);//dummy value
        scDetailFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main_notification_detail, scDetailFragment, getResources().getString(R.string.notification)).commitAllowingStateLoss();

        if (method.personalization_ad) {
            method.showPersonalizedAds(linearLayout);
        } else {
            method.showNonPersonalizedAds(linearLayout);
        }

        checkPer();

    }

    public void checkPer() {
        if ((ContextCompat.checkSelfPermission(NotificationDetail.this, "android.permission.WRITE_EXTERNAL_STORAGE"
                + "android.permission.WRITE_INTERNAL_STORAGE" + "android.permission.READ_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED)) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.WRITE_INTERNAL_STORAGE",
                                "android.permission.READ_EXTERNAL_STORAGE"},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            } else {
                Method.allowPermitionExternalStorage = true;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        boolean canUseExternalStorage = false;

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    canUseExternalStorage = true;
                    Method.allowPermitionExternalStorage = true;
                }
                if (!canUseExternalStorage) {
                    Toast.makeText(NotificationDetail.this, getResources().getString(R.string.cannot_use_save_permission), Toast.LENGTH_SHORT).show();
                    Method.allowPermitionExternalStorage = false;
                }
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onPause() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().clearFlags(1024);
        Events.StopPlay stopPlay = new Events.StopPlay("");
        GlobalBus.getBus().post(stopPlay);
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        Events.StopPlay stopPlay = new Events.StopPlay("");
        GlobalBus.getBus().post(stopPlay);
        startActivity(new Intent(NotificationDetail.this, MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
        finishAffinity();
        super.onBackPressed();
    }
}
