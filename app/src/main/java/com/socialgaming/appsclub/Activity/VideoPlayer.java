package com.socialgaming.appsclub.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifest;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.socialgaming.appsclub.R;
import com.socialgaming.appsclub.Util.Method;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;


public class VideoPlayer extends AppCompatActivity {

    private Method method;
    SimpleExoPlayer player;
    private ImageView imageView;
    private PlayerView playerView;
    private String video_url, video_type;
    private ProgressBar progressBar;
    private boolean isFullScreen = false;
    private SsManifest ssUri;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        Method.forceRTLIfSupported(getWindow(), VideoPlayer.this);

        method = new Method(VideoPlayer.this);
        changeStatusBarColor();

        Intent in = getIntent();
        video_url = in.getStringExtra("Video_url");
        video_type = in.getStringExtra("video_type");

        imageView = findViewById(R.id.imageView_full_video_play);
        playerView = findViewById(R.id.player_view);
        progressBar = findViewById(R.id.progresbar_video_play);
        progressBar.setVisibility(View.VISIBLE);

        DefaultTrackSelector trackSelector = new DefaultTrackSelector();
        player = ExoPlayerFactory.newSimpleInstance(VideoPlayer.this, trackSelector);
        playerView.setPlayer(player);

        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory =
                new DefaultHttpDataSourceFactory(Util.getUserAgent(VideoPlayer.this, getResources().getString(R.string.app_name)));
        // This is the MediaSource representing the media to be played.
        MediaSource SsMediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(video_url));
        // Prepare the player with the source.
        player.prepare(SsMediaSource);
        player.setPlayWhenReady(true);

        player.addListener(
                new Player.EventListener() {
                    @Override
                    public void onTimelineChanged(
                            Timeline timeline,
                            @Nullable Object manifest,
                            @Player.TimelineChangeReason int reason) {
                        if (manifest != null) {
                            SsManifest ssManifest = (SsManifest) manifest;
                            // Do something with the manifest.
                        }
                    }
                });
        com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource ssMediaSource =
                new SsMediaSource.Factory(
                        () -> {
                            HttpDataSource dataSource = new DefaultHttpDataSource(video_url);
                            // Set a custom authentication request header.
                            dataSource.setRequestProperty("Header", "Value");
                            return dataSource;
                        })
                        .createMediaSource(ssUri);

        if (!video_type.equals("Landscape")) {
            imageView.setVisibility(View.GONE);
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFullScreen) {
                    isFullScreen = false;
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.full_screen));
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    getWindow().clearFlags(1024);
                } else {
                    isFullScreen = true;
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.exitfull_screen));
                    getWindow().setFlags(1024, 1024);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (player != null) {
            player.setPlayWhenReady(false);
            player.stop();
            player.release();
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        if (player != null) {
            player.setPlayWhenReady(false);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (player != null) {
            player.setPlayWhenReady(false);
            player.stop();
            player.release();
        }
        super.onDestroy();
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
