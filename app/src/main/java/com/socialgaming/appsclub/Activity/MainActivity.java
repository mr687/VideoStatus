package com.socialgaming.appsclub.Activity;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.daimajia.slider.library.SliderLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;
import com.socialgaming.appsclub.Fragment.CategoryFragment;
import com.socialgaming.appsclub.Fragment.DownloadFragment;
import com.socialgaming.appsclub.Fragment.FavouriteFragment;
import com.socialgaming.appsclub.Fragment.HomeMainFragment;
import com.socialgaming.appsclub.Fragment.ProfileFragment;
import com.socialgaming.appsclub.Fragment.ReferenceCodeFragment;
import com.socialgaming.appsclub.Fragment.RewardPointFragment;
import com.socialgaming.appsclub.Fragment.SearchFragment;
import com.socialgaming.appsclub.Fragment.SettingFragment;
import com.socialgaming.appsclub.InterFace.FullScreen;
import com.socialgaming.appsclub.InterFace.VideoAd;
import com.socialgaming.appsclub.Item.AboutUsList;
import com.socialgaming.appsclub.R;
import com.socialgaming.appsclub.Util.API;
import com.socialgaming.appsclub.Util.Constant_Api;
import com.socialgaming.appsclub.Util.Events;
import com.socialgaming.appsclub.Util.GlobalBus;
import com.socialgaming.appsclub.Util.Method;
import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.irfaan008.irbottomnavigation.SpaceItem;
import com.irfaan008.irbottomnavigation.SpaceNavigationView;
import com.irfaan008.irbottomnavigation.SpaceOnClickListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.onesignal.OneSignal;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import cz.msebera.android.httpclient.Header;
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;


@SuppressLint("StaticFieldLeak")
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Method method;
    public static Toolbar toolbar;
    public MenuItem searchItem;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private FrameLayout frameLayout;
    private View view;
    private SpaceNavigationView bottom_navigation;
    private TextView textView_appName;
    private ProgressBar progressBar;
    private String payment_withdraw = "false";
    private ConsentForm form;
    private int dpAsPixels_bottom;
    final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 101;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.parseColor("#20111111"));
        setContentView(R.layout.activity_main);
        View yourView = findViewById(R.id.bottom_navigation);
        new SimpleTooltip.Builder(this)
                .anchorView(yourView)
                .text("Upload Video Get 10 Point")
                .gravity(Gravity.TOP)
                .animated(true)
                .transparentOverlay(false)
                .build()
                .show();
        GlobalBus.getBus().register(this);

        Method.forceRTLIfSupported(getWindow(), MainActivity.this);
        Method.search_title = getResources().getString(R.string.home);

        VideoAd videoAd = new VideoAd() {
            @Override
            public void videoAdClick(String type) {
                startActivity(new Intent(MainActivity.this, UploadActivity.class));
            }
        };

        FullScreen fullScreen = new FullScreen() {
            @Override
            public void fullscreen(boolean isFull) {
                checkFullScreen(isFull);
            }
        };

        method = new Method(MainActivity.this, null, videoAd, fullScreen);

        if (getIntent().hasExtra("payment_withdraw")) {
            payment_withdraw = getIntent().getStringExtra("payment_withdraw");
            Log.d("payment_withdraw", payment_withdraw);
        }

        toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(toolbar);

        frameLayout = findViewById(R.id.frameLayout_main);
        progressBar = findViewById(R.id.progressbar_main);

        float scale = getResources().getDisplayMetrics().density;
        dpAsPixels_bottom = (int) (65 * scale + 0.5f);
        frameLayout.setPadding(0, 0, 0, dpAsPixels_bottom);

        LinearLayout linearLayout = findViewById(R.id.linearLayout_main);
        view = findViewById(R.id.view_home_main);

        bottom_navigation = findViewById(R.id.bottom_navigation);
        bottom_navigation.initWithSaveInstanceState(savedInstanceState);
        bottom_navigation.changeCenterButtonIcon(R.drawable.white_upload);
        bottom_navigation.addSpaceItem(new SpaceItem(getResources().getString(R.string.home), R.drawable.home_white));
        bottom_navigation.addSpaceItem(new SpaceItem(getResources().getString(R.string.reward), R.drawable.reward_white));
        bottom_navigation.addSpaceItem(new SpaceItem(getResources().getString(R.string.favorites), R.drawable.ic_fav));
        bottom_navigation.addSpaceItem(new SpaceItem(getResources().getString(R.string.profile), R.drawable.profile_white));
        bottom_navigation.setCentreButtonColor(getResources().getColor(R.color.colorPrimary));

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        toolbar.setNavigationIcon(R.drawable.ic_side_nav);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        final View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        textView_appName = headerLayout.findViewById(R.id.textView_name_nav);

        if (method.pref.getBoolean(method.pref_login, false)) {
            navigationView.getMenu().getItem(7).setIcon(R.drawable.logout);
            navigationView.getMenu().getItem(7).setTitle(getResources().getString(R.string.action_logout));
        }

        navigationView.getMenu().getItem(0).setChecked(true);
        navigationView.getMenu().getItem(5).setVisible(false);

        bottom_navigation.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                stopPlaying();
                if (Constant_Api.aboutUsList != null) {
                    if (Constant_Api.aboutUsList.isVideo_add_status_ad()) {
                        method.showVideoAd("upload");
                    } else {
                        startActivity(new Intent(MainActivity.this, UploadActivity.class));
                    }
                } else {
                    startActivity(new Intent(MainActivity.this, UploadActivity.class));
                }
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                bottomNavigation(itemIndex);
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
                bottomNavigation(itemIndex);
            }
        });
        new FancyAlertDialog.Builder(this)
                .setTitle("Video Upload Requirements :")
                .setBackgroundColor(Color.parseColor("#feb007"))  //Don't pass R.color.colorvalue
                .setMessage("1. Please follow the instruction of video file size, duration and format.\n" +
                        "\n" +
                        "2. The better your video, the more points you will get\n" +
                        "\n" +
                        "3. dont upload video from any source like tiktok and Others")
                .setNegativeBtnText("No")
                .setPositiveBtnBackground(Color.parseColor("#198de0"))  //Don't pass R.color.colorvalue
                .setPositiveBtnText("Ok")
                .setNegativeBtnBackground(Color.parseColor("#FFA9A7A8"))  //Don't pass R.color.colorvalue
                .setAnimation(Animation.POP)
                .isCancellable(true)
                .setIcon(R.drawable.ic_notif,Icon.Visible)
                .build();

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPlaying();
                startActivity(new Intent(MainActivity.this, StatusSaver.class)
                        .putExtra("type", "status"));
                drawer.closeDrawers();
            }
        });

        if (Method.isNetworkAvailable(MainActivity.this)) {
            aboutUs();
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
            progressBar.setVisibility(View.GONE);
        }

        checkPer();

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
            }
            if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
                String title = getSupportFragmentManager().getFragments().get(getSupportFragmentManager().getBackStackEntryCount() - 1).getTag();
                if (title != null) {
                    method.ShowFullScreen(false);
                    toolbar.setTitle(title);

                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    getWindow().clearFlags(1024);

                    stopPlaying();

                }
                super.onBackPressed();
            } else {
                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, getResources().getString(R.string.Please_click_BACK_again_to_exit), Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            }

        }
    }

    public void stopPlaying() {
        Events.StopPlay stopPlay = new Events.StopPlay("");
        GlobalBus.getBus().post(stopPlay);
    }

    public void search_title(String title) {
        Method.search_title = title;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        searchItem = menu.findItem(R.id.ic_searchView);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener((new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (Method.isNetworkAvailable(MainActivity.this)) {
                    stopPlaying();
                    backStackRemove();
                    SearchFragment searchFragment = new SearchFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("search", query);
                    bundle.putString("typeLayout", "Landscape");
                    searchFragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, searchFragment, query).commitAllowingStateLoss();
                    return false;
                } else {
                    method.alertBox(getResources().getString(R.string.internet_connection));
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        }));


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            default:
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle bottom_navigation view item clicks here.
        //Checking if the item is in checked state or not, if not make it in checked state
        if (item.isChecked())
            item.setChecked(false);
        else
            item.setChecked(true);

        //Closing drawer on item click
        drawer.closeDrawers();

        // Handle bottom_navigation view item clicks here.
        int id = item.getItemId();

        //bottom_navigation.getMenu().setGroupCheckable(0, false, true);

        switch (id) {

            case R.id.home:
                bottom_navigation.changeCurrentItem(0);
                return true;

            case R.id.category:
                invisible_bottomNavigation();
                stopPlaying();
                backStackRemove();
                CategoryFragment categoryFragment = new CategoryFragment();
                Bundle bundle = new Bundle();
                bundle.putString("type", "drawer_category");
                categoryFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, categoryFragment, getResources().getString(R.string.category)).commit();
                search_title(getResources().getString(R.string.category));
                return true;

            case R.id.download:
                invisible_bottomNavigation();
                stopPlaying();
                backStackRemove();
                DownloadFragment downloadFragment = new DownloadFragment();
                Bundle bundle_download = new Bundle();
                bundle_download.putString("typeLayout", "Landscape");
                downloadFragment.setArguments(bundle_download);
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, downloadFragment, getResources().getString(R.string.download)).commit();
                search_title(getResources().getString(R.string.my_download));
                return true;

            case R.id.upload:
                invisible_bottomNavigation();
                stopPlaying();
                if (Constant_Api.aboutUsList != null) {
                    if (Constant_Api.aboutUsList.isVideo_add_status_ad()) {
                        method.showVideoAd("upload");
                    } else {
                        startActivity(new Intent(MainActivity.this, UploadActivity.class));
                    }
                } else {
                    startActivity(new Intent(MainActivity.this, UploadActivity.class));
                }
                return true;

            case R.id.reference_code:
                invisible_bottomNavigation();
                stopPlaying();
                backStackRemove();
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, new ReferenceCodeFragment(), getResources().getString(R.string.reference_code)).commit();
                return true;

            case R.id.earn_point:
                invisible_bottomNavigation();
                stopPlaying();
                startActivity(new Intent(MainActivity.this, Spinner.class));
                return true;

            case R.id.setting:
                invisible_bottomNavigation();
                stopPlaying();
                backStackRemove();
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, new SettingFragment(), getResources().getString(R.string.setting)).commit();
                search_title(getResources().getString(R.string.setting));
                return true;

            case R.id.login:
                stopPlaying();
                if (method.pref.getBoolean(method.pref_login, false)) {

                    OneSignal.sendTag("user_id", method.pref.getString(method.profileId, null));

                    if (method.pref.getString(method.loginType, null).equals("google")) {

                        // Configure sign-in to request the user's ID, email address, and basic
                        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
                        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestEmail()
                                .build();

                        // Build a GoogleSignInClient with the options specified by gso.
                        //Google login
                        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

                        mGoogleSignInClient.signOut()
                                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        method.editor.putBoolean(method.pref_login, false);
                                        method.editor.commit();
                                        startActivity(new Intent(MainActivity.this, Login.class));
                                        finishAffinity();
                                    }
                                });
                    } else {
                        method.editor.putBoolean(method.pref_login, false);
                        method.editor.commit();
                        startActivity(new Intent(MainActivity.this, Login.class));
                        finishAffinity();
                        FirebaseAuth mauth = FirebaseAuth.getInstance();
                        mauth.signOut();
                    }
                } else {
                    startActivity(new Intent(MainActivity.this, Login.class));
                    finishAffinity();
                }

                return true;

            default:
                return true;
        }
    }

    private void bottomNavigation(int itemIndex) {

        unCheck();

        switch (itemIndex) {
            case 0:
                navigationView.getMenu().getItem(0).setChecked(true);
                stopPlaying();
                backStackRemove();
                toolbar.setTitle(getResources().getString(R.string.home));
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, new HomeMainFragment(), getResources().getString(R.string.home)).commitAllowingStateLoss();
                search_title(getResources().getString(R.string.home));
                break;
            case 1:
                payment_withdraw = "false";
                stopPlaying();
                backStackRemove();
                toolbar.setTitle(getResources().getString(R.string.reward_point));
                RewardPointFragment rewardPointFragment_nav = new RewardPointFragment();
                Bundle bundle = new Bundle();
                bundle.putString("payment_withdraw", payment_withdraw);
                rewardPointFragment_nav.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, rewardPointFragment_nav, getResources().getString(R.string.reward_point)).commit();
                break;
            case 2:
                stopPlaying();
                backStackRemove();
                toolbar.setTitle(getResources().getString(R.string.favorites));
                FavouriteFragment favouriteFragment = new FavouriteFragment();
                Bundle bundle_fav = new Bundle();
                bundle_fav.putString("typeLayout", "Landscape");
                favouriteFragment.setArguments(bundle_fav);
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, favouriteFragment, getResources().getString(R.string.favorites)).commit();
                search_title(getResources().getString(R.string.favorites));
                break;
            case 3:
                stopPlaying();
                backStackRemove();
                toolbar.setTitle(getResources().getString(R.string.profile));
                ProfileFragment profileFragment = new ProfileFragment();
                Bundle bundle_profile = new Bundle();
                bundle_profile.putString("type", "user");
                bundle_profile.putString("id", method.pref.getString(method.profileId, null));
                profileFragment.setArguments(bundle_profile);
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, profileFragment, getResources().getString(R.string.profile)).commit();
                break;
        }

    }

    public void invisible_bottomNavigation() {
        bottom_navigation.changeCurrentItem(-1);
    }

    public void backStackRemove() {
        for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
            getSupportFragmentManager().popBackStack();
        }
    }

    public void unCheck() {
        int size = navigationView.getMenu().size();
        for (int i = 0; i < size; i++) {
            navigationView.getMenu().getItem(i).setChecked(false);
        }
    }

    public void checkPer() {
        if ((ContextCompat.checkSelfPermission(MainActivity.this, "android.permission.WRITE_EXTERNAL_STORAGE"
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
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.cannot_use_save_permission), Toast.LENGTH_SHORT).show();
                    Method.allowPermitionExternalStorage = false;
                }
            }
        }
    }

    public void aboutUs() {

        progressBar.setVisibility(View.VISIBLE);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(MainActivity.this));
        jsObj.addProperty("method_name", "app_settings");
        params.put("data", API.toBase64(jsObj.toString()));
        Log.d("Key_generate", API.toBase64(jsObj.toString()));// Log print
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
                        if (status.equals("-2")) {
                            method.suspend(message);
                        } else {
                            method.alertBox(message);
                        }

                    } else {

                        JSONArray jsonArray = jsonObject.getJSONArray(Constant_Api.tag);

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject object = jsonArray.getJSONObject(i);
                            String app_name = object.getString("app_name");
                            String app_logo = object.getString("app_logo");
                            String app_version = object.getString("app_version");
                            String app_author = object.getString("app_author");
                            String app_contact = object.getString("app_contact");
                            String app_email = object.getString("app_email");
                            String app_website = object.getString("app_website");
                            String app_description = object.getString("app_description");
                            String app_developed_by = object.getString("app_developed_by");
                            String app_faq = object.getString("app_faq");
                            String app_privacy_policy = object.getString("app_privacy_policy");
                            String publisher_id = object.getString("publisher_id");
                            boolean interstital_ad = Boolean.parseBoolean(object.getString("interstital_ad"));
                            String interstital_ad_id = object.getString("interstital_ad_id");
                            String interstital_ad_click = object.getString("interstital_ad_click");
                            boolean banner_ad = Boolean.parseBoolean(object.getString("banner_ad"));
                            String banner_ad_id = object.getString("banner_ad_id");
                            boolean rewarded_video_ads = Boolean.parseBoolean(object.getString("rewarded_video_ads"));
                            String rewarded_video_ads_id = object.getString("rewarded_video_ads_id");
                            String rewarded_video_click = object.getString("rewarded_video_click");
                            String redeem_currency = object.getString("redeem_currency");
                            String redeem_points = object.getString("redeem_points");
                            String redeem_money = object.getString("redeem_money");
                            String minimum_redeem_points = object.getString("minimum_redeem_points");
                            String payment_method1 = object.getString("payment_method1");
                            String payment_method2 = object.getString("payment_method2");
                            String payment_method3 = object.getString("payment_method3");
                            String payment_method4 = object.getString("payment_method4");
                            boolean video_views_status_ad = Boolean.parseBoolean(object.getString("video_views_status"));
                            boolean video_add_status_ad = Boolean.parseBoolean(object.getString("video_add_status"));
                            boolean like_video_points_status_ad = Boolean.parseBoolean(object.getString("like_video_points_status"));
                            boolean download_video_points_status_ad = Boolean.parseBoolean(object.getString("download_video_points_status"));
                            String spinner_opt = object.getString("spinner_opt");

                            Constant_Api.aboutUsList = new AboutUsList(app_name, app_logo, app_version, app_author, app_contact, app_email, app_website, app_description, app_developed_by,
                                    app_faq, app_privacy_policy, publisher_id, interstital_ad_id, interstital_ad_click, banner_ad_id, rewarded_video_ads_id, rewarded_video_click, redeem_currency, redeem_points,
                                    redeem_money, minimum_redeem_points, payment_method1, payment_method2, payment_method3, payment_method4, spinner_opt,
                                    interstital_ad, banner_ad, rewarded_video_ads, video_views_status_ad, video_add_status_ad, like_video_points_status_ad,
                                    download_video_points_status_ad);

                        }

                        if (Constant_Api.aboutUsList.getSpinner_opt().equals("true")) {
                            navigationView.getMenu().getItem(5).setVisible(true);
                        } else {
                            navigationView.getMenu().getItem(5).setVisible(false);
                        }

                        Constant_Api.AD_COUNT_SHOW = Integer.parseInt(Constant_Api.aboutUsList.getInterstital_ad_click());
                        Constant_Api.REWARD_VIDEO_AD_COUNT_SHOW = Integer.parseInt(Constant_Api.aboutUsList.getRewarded_video_click());

                        textView_appName.setText(Constant_Api.aboutUsList.getApp_name());

                        if (payment_withdraw.equals("true")) {
                            try {
                                stopPlaying();
                                backStackRemove();
                                toolbar.setTitle(getResources().getString(R.string.reward_point));
                                RewardPointFragment rewardPointFragment = new RewardPointFragment();
                                Bundle bundle = new Bundle();
                                bundle.putString("payment_withdraw", payment_withdraw);
                                rewardPointFragment.setArguments(bundle);
                                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, rewardPointFragment, getResources().getString(R.string.reward_point)).commit();
                            } catch (Exception e) {
                                Toast.makeText(MainActivity.this, getResources().getString(R.string.wrong), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            try {
                                HomeMainFragment homeMainFragment = new HomeMainFragment();
                                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, homeMainFragment, getResources().getString(R.string.home)).commitAllowingStateLoss();
                            } catch (Exception e) {
                                Toast.makeText(MainActivity.this, getResources().getString(R.string.wrong), Toast.LENGTH_SHORT).show();
                            }
                        }

                        checkForConsent();

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

    public void checkForConsent() {

        ConsentInformation consentInformation = ConsentInformation.getInstance(MainActivity.this);
        String[] publisherIds = {Constant_Api.aboutUsList.getPublisher_id()};
        consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                Log.d("consentStatus", consentStatus.toString());
                // User's consent status successfully updated.
                switch (consentStatus) {
                    case PERSONALIZED:
                        method.personalization_ad = true;
                        break;
                    case NON_PERSONALIZED:
                        method.personalization_ad = false;
                        break;
                    case UNKNOWN:
                        if (ConsentInformation.getInstance(getBaseContext())
                                .isRequestLocationInEeaOrUnknown()) {
                            requestConsent();
                        } else {
                            method.personalization_ad = true;
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription) {
                // User's consent status failed to update.
            }
        });

    }

    public void requestConsent() {
        URL privacyUrl = null;
        try {
            // TODO: Replace with your app's privacy policy URL.
            privacyUrl = new URL(getResources().getString(R.string.admob_privacy_link));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            // Handle error.
        }
        form = new ConsentForm.Builder(MainActivity.this, privacyUrl)
                .withListener(new ConsentFormListener() {
                    @Override
                    public void onConsentFormLoaded() {
                        showForm();
                        // Consent form loaded successfully.
                    }

                    @Override
                    public void onConsentFormOpened() {
                        // Consent form was displayed.
                    }

                    @Override
                    public void onConsentFormClosed(ConsentStatus consentStatus, Boolean userPrefersAdFree) {
                        Log.d("consentStatus_form", consentStatus.toString());
                        switch (consentStatus) {
                            case PERSONALIZED:
                                method.personalization_ad = true;
                                break;
                            case NON_PERSONALIZED:
                                method.personalization_ad = false;
                                break;
                            case UNKNOWN:
                                method.personalization_ad = false;
                        }
                    }

                    @Override
                    public void onConsentFormError(String errorDescription) {
                        Log.d("errorDescription", errorDescription);
                    }
                })
                .withPersonalizedAdsOption()
                .withNonPersonalizedAdsOption()
                .build();
        form.load();
    }

    private void showForm() {
        if (form != null) {
            form.show();
        }
    }

    @Override
    protected void onPause() {
        stopPlaying();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().clearFlags(1024);
        checkFullScreen(false);
        super.onPause();
    }


    @Subscribe
    public void getLogin(Events.Login login) {
        if (method != null) {
            if (navigationView != null) {
                if (method.pref.getBoolean(method.pref_login, false)) {
                    navigationView.getMenu().getItem(7).setIcon(R.drawable.logout);
                    navigationView.getMenu().getItem(7).setTitle(getResources().getString(R.string.action_logout));
                } else {
                    navigationView.getMenu().getItem(7).setIcon(R.drawable.login);
                    navigationView.getMenu().getItem(7).setTitle(getResources().getString(R.string.login));
                }
            }
        }
    }

    @Subscribe
    public void getFullscreen(Events.FullScreenNotify fullScreenNotify) {
        checkFullScreen(fullScreenNotify.isFullscreen());
    }

    @Subscribe
    public void geString(Events.Select Select) {
        if (navigationView != null) {
            navigationView.getMenu().getItem(1).setChecked(true);
        }
    }

    public void checkFullScreen(boolean isFull) {
        if (isFull) {

            frameLayout.setPadding(0, 0, 0, 0);

            toolbar.setVisibility(View.GONE);
            bottom_navigation.setVisibility(View.GONE);
            view.setVisibility(View.GONE);

        } else {

            frameLayout.setPadding(0, 0, 0, dpAsPixels_bottom);

            toolbar.setVisibility(View.VISIBLE);
            bottom_navigation.setVisibility(View.VISIBLE);
            view.setVisibility(View.VISIBLE);

        }
    }

    @Override
    protected void onDestroy() {
        GlobalBus.getBus().unregister(this);
        super.onDestroy();
    }

}
