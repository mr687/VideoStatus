package com.socialgaming.appsclub.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.socialgaming.appsclub.Adapter.StatusDetailAdapter;
import com.socialgaming.appsclub.InterFace.InterstitialAdView;
import com.socialgaming.appsclub.R;
import com.socialgaming.appsclub.Util.Constant_Api;
import com.socialgaming.appsclub.Util.Events;
import com.socialgaming.appsclub.Util.GlobalBus;
import com.socialgaming.appsclub.Util.Method;

import java.io.File;



public class DownloadStatusDetail extends AppCompatActivity {

    private Method method;
    public Toolbar toolbar;
    private ViewPager viewPager;
    private String type;
    private Animation myAnim;
    private int selectedPosition = 0;
    private StatusDetailAdapter statusDetailAdapter;
    private InterstitialAdView interstitialAdView;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_detail);

        Method.forceRTLIfSupported(getWindow(), DownloadStatusDetail.this);

        myAnim = AnimationUtils.loadAnimation(DownloadStatusDetail.this, R.anim.bounce);

        toolbar = findViewById(R.id.toolbar_status_detail);
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
        selectedPosition = in.getIntExtra("position", 0);
        type = in.getStringExtra("type");

        interstitialAdView = new InterstitialAdView() {
            @Override
            public void position(int position, String type, String id) {
                startActivity(new Intent(DownloadStatusDetail.this, VideoPlayer.class)
                        .putExtra("Video_url", "file://" + Constant_Api.downloadVideoFilesList.get(position).toString())
                .putExtra("video_type","abcd"));
            }
        };
        method = new Method(DownloadStatusDetail.this, interstitialAdView);

        viewPager = findViewById(R.id.viewpager_status_detail);
        final LinearLayout linearLayout_download = findViewById(R.id.ll_download_sd);
        LinearLayout linearLayout_whatsapp = findViewById(R.id.ll_whatsapp_sd);
        LinearLayout linearLayout_share = findViewById(R.id.ll_share_sd);
        final ImageView imageView_download = findViewById(R.id.imageView_download_sd);
        final ImageView imageView_whatsapp = findViewById(R.id.imageView_whatsapp_sd);
        final ImageView imageView_share = findViewById(R.id.imageView_share_sd);
        TextView textView_download = findViewById(R.id.textView_download_sd);

        imageView_download.setImageDrawable(getResources().getDrawable(R.drawable.del_ic));
        textView_download.setText(getResources().getString(R.string.delete));

        LinearLayout linearLayout = findViewById(R.id.linearLayout_status_detail);
        if (method.personalization_ad) {
            method.showPersonalizedAds(linearLayout);
        } else {
            method.showNonPersonalizedAds(linearLayout);
        }


        if (type.equals("image")) {
            statusDetailAdapter = new StatusDetailAdapter(DownloadStatusDetail.this, Constant_Api.downloadImageFilesList, interstitialAdView, type);
        } else {
            statusDetailAdapter = new StatusDetailAdapter(DownloadStatusDetail.this, Constant_Api.downloadVideoFilesList, interstitialAdView, type);
        }
        viewPager.setAdapter(statusDetailAdapter);

        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        setCurrentItem(selectedPosition);

        linearLayout_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView_download.startAnimation(myAnim);
                if (type.equals("image")) {
                    if (Constant_Api.downloadImageFilesList.size() != 0) {
                        delete();
                    }
                } else {
                    if (Constant_Api.downloadVideoFilesList.size() != 0) {
                        delete();
                    }
                }

            }
        });

        linearLayout_whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView_whatsapp.startAnimation(myAnim);
                if (type.equals("image")) {
                    whatsappStatus(Constant_Api.downloadImageFilesList.get(selectedPosition).toString());
                } else {
                    whatsappStatus(Constant_Api.downloadVideoFilesList.get(selectedPosition).toString());
                }
            }
        });

        linearLayout_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView_share.startAnimation(myAnim);
                if (type.equals("image")) {
                    shareStatus(Constant_Api.downloadImageFilesList.get(selectedPosition).toString());
                } else {
                    shareStatus(Constant_Api.downloadVideoFilesList.get(selectedPosition).toString());
                }
            }
        });

    }

    private void setCurrentItem(int position) {
        viewPager.setCurrentItem(position, false);
    }

    //	page change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            selectedPosition = position;
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

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

    public void delete() {
        if (type.equals("image")) {
            new File(Constant_Api.downloadImageFilesList.get(selectedPosition).toString()).delete();
            Constant_Api.downloadImageFilesList.remove(selectedPosition);
            Events.ImageStatusNotify imageStatusNotify = new Events.ImageStatusNotify("");
            GlobalBus.getBus().post(imageStatusNotify);
        } else {
            new File(Constant_Api.downloadVideoFilesList.get(selectedPosition).toString()).delete();
            Constant_Api.downloadVideoFilesList.remove(selectedPosition);
            Events.VideoStatusNotify videoStatusNotify = new Events.VideoStatusNotify("");
            GlobalBus.getBus().post(videoStatusNotify);
        }
        statusDetailAdapter.notifyDataSetChanged();
        if (type.equals("image")) {
            if (Constant_Api.downloadImageFilesList.size() == 0) {
                finish();
            }
        } else {
            if (Constant_Api.downloadVideoFilesList.size() == 0) {
                finish();
            }
        }
    }

    private void whatsappStatus(String path) {
        Uri uri = Uri.parse(path);
        Intent videoshare = new Intent(Intent.ACTION_SEND);
        videoshare.setType("*/*");
        videoshare.setPackage("com.whatsapp");
        videoshare.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        videoshare.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(videoshare);
    }

    private void shareStatus(String path) {
        Intent share = new Intent(Intent.ACTION_SEND);// Create the new Intent using the 'Send' action.
        share.setType("*/*");   // Set the MIME type
        File media = new File(path); // Create the URI from the media
        Uri uri = Uri.fromFile(media); // Add the URI to the Intent.
        share.putExtra(Intent.EXTRA_STREAM, uri); // Broadcast the Intent.
        startActivity(Intent.createChooser(share, "Share to"));
    }

}
