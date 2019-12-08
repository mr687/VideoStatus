package com.socialgaming.appsclub.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.socialgaming.appsclub.Adapter.StatusDetailAdapter;
import com.socialgaming.appsclub.InterFace.InterstitialAdView;
import com.socialgaming.appsclub.R;
import com.socialgaming.appsclub.Util.Constant_Api;
import com.socialgaming.appsclub.Util.Method;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;


public class StatusDetail extends AppCompatActivity {

    private Method method;
    public Toolbar toolbar;
    private ViewPager viewPager;
    private String type;
    private File sdIconStorageDir;
    private Animation myAnim;
    private int selectedPosition = 0;
    private InterstitialAdView interstitialAdView;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_detail);

        Method.forceRTLIfSupported(getWindow(), StatusDetail.this);

        myAnim = AnimationUtils.loadAnimation(StatusDetail.this, R.anim.bounce);

        String iconsStoragePath = Environment.getExternalStorageDirectory() + "/Video_Status/" + "/status_saver/";
        sdIconStorageDir = new File(iconsStoragePath);

        //create storage directories, if they don't exist
        if (!sdIconStorageDir.exists()) {
            sdIconStorageDir.mkdirs();
        }

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
                startActivity(new Intent(StatusDetail.this, VideoPlayer.class)
                        .putExtra("Video_url", "file://" + Constant_Api.videoFilesList.get(position).toString())
                        .putExtra("video_type", "abcd"));
            }
        };
        method = new Method(StatusDetail.this, interstitialAdView);

        viewPager = findViewById(R.id.viewpager_status_detail);
        final LinearLayout linearLayout_download = findViewById(R.id.ll_download_sd);
        LinearLayout linearLayout_whatsapp = findViewById(R.id.ll_whatsapp_sd);
        LinearLayout linearLayout_share = findViewById(R.id.ll_share_sd);
        final ImageView imageView_download = findViewById(R.id.imageView_download_sd);
        final ImageView imageView_whatsapp = findViewById(R.id.imageView_whatsapp_sd);
        final ImageView imageView_share = findViewById(R.id.imageView_share_sd);

        LinearLayout linearLayout = findViewById(R.id.linearLayout_status_detail);
        if (method.personalization_ad) {
            method.showPersonalizedAds(linearLayout);
        } else {
            method.showNonPersonalizedAds(linearLayout);
        }

        StatusDetailAdapter statusDetailAdapter;
        if (type.equals("image")) {
            statusDetailAdapter = new StatusDetailAdapter(StatusDetail.this, Constant_Api.imageFilesList, interstitialAdView, type);
        } else {
            statusDetailAdapter = new StatusDetailAdapter(StatusDetail.this, Constant_Api.videoFilesList, interstitialAdView, type);
        }
        viewPager.setAdapter(statusDetailAdapter);

        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        setCurrentItem(selectedPosition);

        linearLayout_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView_download.startAnimation(myAnim);
                downloadStatus();
            }
        });

        linearLayout_whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView_whatsapp.startAnimation(myAnim);
                if (type.equals("image")) {
                    whatsappStatus(Constant_Api.imageFilesList.get(selectedPosition).toString());
                } else {
                    whatsappStatus(Constant_Api.videoFilesList.get(selectedPosition).toString());
                }
            }
        });

        linearLayout_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView_share.startAnimation(myAnim);
                if (type.equals("image")) {
                    shareStatus(Constant_Api.imageFilesList.get(selectedPosition).toString());
                } else {
                    shareStatus(Constant_Api.videoFilesList.get(selectedPosition).toString());
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

    public static void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        try {
            OutputStream out = new FileOutputStream(dst);
            try {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }


    private void downloadStatus() {

        if (type.equals("image")) {
            String destinationPath = Constant_Api.imageFilesList.get(selectedPosition).toString();
            String[] string = destinationPath.split(".Statuses/");
            try {
                copy(new File(Constant_Api.imageFilesList.get(selectedPosition).toString()), new File(sdIconStorageDir.toString() + "/" + string[1]));
                try {
                    MediaScannerConnection.scanFile(getApplicationContext(), new String[]{sdIconStorageDir.toString() + "/" + string[1]},
                            null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {

                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            String destinationPath = Constant_Api.videoFilesList.get(selectedPosition).toString();
            String[] string = destinationPath.split(".Statuses/");
            try {
                copy(new File(Constant_Api.videoFilesList.get(selectedPosition).toString()), new File(sdIconStorageDir.toString() + "/" + string[1]));
                try {
                    MediaScannerConnection.scanFile(getApplicationContext(), new String[]{sdIconStorageDir.toString() + "/" + string[1]},
                            null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {

                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Toast.makeText(this, getResources().getString(R.string.download), Toast.LENGTH_SHORT).show();


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
