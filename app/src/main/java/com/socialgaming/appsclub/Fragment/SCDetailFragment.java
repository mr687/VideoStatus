package com.socialgaming.appsclub.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.socialgaming.appsclub.Activity.AllComment;
import com.socialgaming.appsclub.Activity.Login;
import com.socialgaming.appsclub.Activity.MainActivity;
import com.socialgaming.appsclub.Activity.NotificationDetail;
import com.socialgaming.appsclub.Adapter.CommentAdapter;
import com.socialgaming.appsclub.Adapter.RelatedAdapter;
import com.socialgaming.appsclub.Adapter.RelatedPortraitAdapter;
import com.socialgaming.appsclub.DataBase.DatabaseHandler;
import com.socialgaming.appsclub.InterFace.FullScreen;
import com.socialgaming.appsclub.InterFace.InterstitialAdView;
import com.socialgaming.appsclub.InterFace.VideoAd;
import com.socialgaming.appsclub.Item.CommentList;
import com.socialgaming.appsclub.Item.SubCategoryList;
import com.socialgaming.appsclub.R;
import com.socialgaming.appsclub.Util.API;
import com.socialgaming.appsclub.Util.Constant_Api;
import com.socialgaming.appsclub.Util.Events;
import com.socialgaming.appsclub.Util.GlobalBus;
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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

public class SCDetailFragment extends Fragment {

    private Method method;
    private DatabaseHandler db;
    private SimpleExoPlayer player;
    private PlayerView playerView;
    private Animation myAnim;
    private String passId, type;
    private int position;
    private int columnWidth, columnHeight;
    private ImageView imageView_fullscreen;
    private ImageView imageView_like, imageView_fav;
    private ImageView imageView, imageView_play, imageView_download;
    private ImageView imageViewFacebook, imageViewInstagram, imageViewWhatsApp, imageViewTwitter, imageView_more;
    private TextView textView_view, textView_like, textView_noData, textView_noDataMain, textViewNoCommentFound;
    private Button button;
    private Button button_all_comment;
    private ProgressBar progressBar, progressBar_player;
    private RecyclerView recyclerView;
    private RecyclerView recyclerViewComment;
    private RelatedAdapter relatedAdapter;
    private RelatedPortraitAdapter relatedPortraitAdapter;
    private CommentAdapter commentAdapter;
    private List<SubCategoryList> relatedLists;
    private InterstitialAdView interstitialAdView;
    private boolean isFullScreen = false;
    private ProgressDialog progressDialog;
    private TextView textView_userName;
    private Button button_follow;
    private CircleImageView circleImageView;
    private CardView cardView;
    private LinearLayout linearLayout_download;
    private LinearLayout linearLayout_detail;
    private LinearLayout linearLayout_comment;
    private RelativeLayout relativeLayout_player, relativeLayout_related;
    private InputMethodManager inputMethodManager;
    private EditText editTextComment;
    private CircleImageView imageView_comment;
    private List<SubCategoryList> videoLists;
    private LinearLayout linearLayout_padding;
    private int dpAsPixels_bottom;
    private boolean isView = true;
    private LinearLayout linearLayout_adView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.scdetail_fragment, container, false);

        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        GlobalBus.getBus().register(this);

        videoLists = new ArrayList<>();
        progressDialog = new ProgressDialog(getActivity());

        db = new DatabaseHandler(getActivity());
        myAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.bounce);

        interstitialAdView = new InterstitialAdView() {
            @Override
            public void position(int position, String type, String id) {

                viewHide();
                playerStop();

                getActivity().getSupportFragmentManager().popBackStack();

                SCDetailFragment scDetailFragment = new SCDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putString("id", id);
                bundle.putString("type", type);
                bundle.putInt("position", position);
                scDetailFragment.setArguments(bundle);
                if (type.equals("notification")) {
                    getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main_notification_detail, scDetailFragment, relatedLists.get(position).getVideo_title()).addToBackStack(relatedLists.get(position).getVideo_title()).commitAllowingStateLoss();
                } else {
                    getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, scDetailFragment, relatedLists.get(position).getVideo_title()).addToBackStack(relatedLists.get(position).getVideo_title()).commitAllowingStateLoss();
                }

            }
        };
        VideoAd videoAd = new VideoAd() {
            @Override
            public void videoAdClick(String type) {
                switch (type) {
                    case "download":
                        downloadVideo();
                        break;
                    case "like_video":
                        likeVideo();
                        break;
                    default:
                        playVideo();
                        break;
                }
            }
        };
        FullScreen fullScreen = new FullScreen() {
            @Override
            public void fullscreen(boolean isFull) {
                Events.FullScreenNotify fullScreenNotify = new Events.FullScreenNotify(isFull);
                GlobalBus.getBus().post(fullScreenNotify);
            }
        };
        method = new Method(getActivity(), interstitialAdView, videoAd, fullScreen);
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        columnWidth = (method.getScreenWidth());
        columnHeight = (method.getScreenHeight());
        relatedLists = new ArrayList<>();

        assert getArguments() != null;
        passId = getArguments().getString("id");
        type = getArguments().getString("type");
        position = getArguments().getInt("position");//use in subcategory array

        button = view.findViewById(R.id.button_related_scd);
        imageView = view.findViewById(R.id.imageView_scd);
        imageView_play = view.findViewById(R.id.imageView_play_scd);
        imageView_download = view.findViewById(R.id.imageView_download_scd);
        imageView_fullscreen = view.findViewById(R.id.imageView_fullscreen_scd);
        imageView_fav = view.findViewById(R.id.imageView_fav_scd);
        imageView_like = view.findViewById(R.id.imageView_like_scd);
        imageViewFacebook = view.findViewById(R.id.imageView_facebook_scd);
        imageViewInstagram = view.findViewById(R.id.imageView_instagram_scd);
        imageViewWhatsApp = view.findViewById(R.id.imageView_whatsapp_scd);
        imageViewTwitter = view.findViewById(R.id.imageView_twitter_scd);
        imageView_more = view.findViewById(R.id.imageView_more_scd);
        textView_view = view.findViewById(R.id.textView_view_scd);
        textView_like = view.findViewById(R.id.textView_like_scd);
        textView_noDataMain = view.findViewById(R.id.textView_noData_main_scd_fragment);
        textView_noData = view.findViewById(R.id.textView_noData_scd_fragment);
        relativeLayout_player = view.findViewById(R.id.relativeLayout_imageView_player_scd);
        relativeLayout_related = view.findViewById(R.id.relativeLayout_related_scd);
        linearLayout_download = view.findViewById(R.id.linearLayout_download_scd);
        linearLayout_detail = view.findViewById(R.id.linearLayout_detail_scd);
        linearLayout_comment = view.findViewById(R.id.linearLayout_comment_scd);
        linearLayout_adView = view.findViewById(R.id.linearLayout_scd);
        button_all_comment = view.findViewById(R.id.button_comment_scd);
        editTextComment = view.findViewById(R.id.editText_comment_scd);
        imageView_comment = view.findViewById(R.id.imageView_circle_comment);
        recyclerViewComment = view.findViewById(R.id.recyclerView_comment_scd);
        textViewNoCommentFound = view.findViewById(R.id.textView_noComment_scdetail);

        circleImageView = view.findViewById(R.id.imageView_profile_scd);
        textView_userName = view.findViewById(R.id.textView_userName_scd);
        button_follow = view.findViewById(R.id.button_follow_scd);
        cardView = view.findViewById(R.id.cardView_user_scd);

        textView_noDataMain.setVisibility(View.GONE);

        linearLayout_padding = view.findViewById(R.id.linearLayout_parent_scd);
        linearLayout_padding.setVisibility(View.GONE);

        float scale = getResources().getDisplayMetrics().density;
        dpAsPixels_bottom = (int) (15 * scale + 0.5f);

        linearLayout_padding.setPadding(0, 0, 0, dpAsPixels_bottom);

        textViewNoCommentFound.setVisibility(View.GONE);
        editTextComment.setClickable(true);
        editTextComment.setFocusable(false);

        recyclerViewComment.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager_comment = new LinearLayoutManager(getActivity());
        recyclerViewComment.setLayoutManager(layoutManager_comment);
        recyclerViewComment.setFocusable(false);
        recyclerViewComment.setNestedScrollingEnabled(false);

        if (method.personalization_ad) {
            method.showPersonalizedAds(linearLayout_adView);
        } else {
            method.showNonPersonalizedAds(linearLayout_adView);
        }

        imageView_fullscreen.setVisibility(View.GONE);

        imageView_fullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imageView_fullscreen.startAnimation(myAnim);

                if (isFullScreen) {
                    isFullScreen = false;

                    linearLayout_padding.setPadding(0, 0, 0, dpAsPixels_bottom);

                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    getActivity().getWindow().clearFlags(1024);

                    imageView_fullscreen.setImageDrawable(getResources().getDrawable(R.drawable.full_screen));
                    if (videoLists.get(0).getVideo_layout().equals("Portrait")) {
                        imageView.setLayoutParams(new RelativeLayout.LayoutParams(columnWidth, columnHeight / 2 + 140));
                        playerView.setLayoutParams(new RelativeLayout.LayoutParams(columnWidth, columnHeight / 2 + 140));
                    } else {
                        imageView.setLayoutParams(new RelativeLayout.LayoutParams(columnWidth, columnWidth / 2));
                        playerView.setLayoutParams(new RelativeLayout.LayoutParams(columnWidth, columnWidth / 2));
                    }

                    relativeLayout_player.setVisibility(View.VISIBLE);
                    linearLayout_detail.setVisibility(View.VISIBLE);
                    linearLayout_adView.setVisibility(View.VISIBLE);
                    cardView.setVisibility(View.VISIBLE);
                    relativeLayout_related.setVisibility(View.VISIBLE);
                    linearLayout_comment.setVisibility(View.VISIBLE);

                    if (!type.equals("notification")) {
                        method.ShowFullScreen(isFullScreen);
                    }

                } else {
                    isFullScreen = true;

                    linearLayout_padding.setPadding(0, 0, 0, 0);

                    imageView_fullscreen.setImageDrawable(getResources().getDrawable(R.drawable.exitfull_screen));
                    playerView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                    if (!videoLists.get(0).getVideo_layout().equals("Portrait")) {
                        getActivity().getWindow().setFlags(1024, 1024);
                        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    }

                    relativeLayout_player.setVisibility(View.GONE);
                    linearLayout_detail.setVisibility(View.GONE);
                    linearLayout_adView.setVisibility(View.GONE);
                    cardView.setVisibility(View.GONE);
                    relativeLayout_related.setVisibility(View.GONE);
                    linearLayout_comment.setVisibility(View.GONE);

                    if (!type.equals("notification")) {
                        method.ShowFullScreen(isFullScreen);
                    }

                }
            }
        });

        playerView = view.findViewById(R.id.player_view);
        playerView.setVisibility(View.GONE);

        if (type.equals("notification")) {
            button.setVisibility(View.GONE);
        }

        progressBar = view.findViewById(R.id.progressbar_scd_fragment);
        progressBar_player = view.findViewById(R.id.progressbar_player_scd_fragment);

        progressBar_player.setVisibility(View.GONE);

        recyclerView = view.findViewById(R.id.recyclerView_scd_fragment);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setFocusable(false);

        if (Method.isNetworkAvailable(getActivity())) {
            if (method.pref.getBoolean(method.pref_login, false)) {
                detail(passId, method.pref.getString(method.profileId, null));
            } else {
                detail(passId, "0");
            }
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

        setHasOptionsMenu(true);
        if (type.equals("notification")) {
            setHasOptionsMenu(false);
            button_follow.setVisibility(View.GONE);
            imageView_fullscreen.setVisibility(View.GONE);
        }
        return view;

    }

    @Subscribe
    public void getPlay(Events.StopPlay stopPlay) {

        if (imageView_fullscreen != null) {

            isFullScreen = false;

            if (videoLists.size() != 0) {
                imageView_fullscreen.setImageDrawable(getResources().getDrawable(R.drawable.full_screen));
                if (videoLists.get(0).getVideo_layout().equals("Portrait")) {
                    imageView.setLayoutParams(new RelativeLayout.LayoutParams(columnWidth, columnHeight / 2 + 140));
                    playerView.setLayoutParams(new RelativeLayout.LayoutParams(columnWidth, columnHeight / 2 + 140));
                } else {
                    imageView.setLayoutParams(new RelativeLayout.LayoutParams(columnWidth, columnWidth / 2));
                    playerView.setLayoutParams(new RelativeLayout.LayoutParams(columnWidth, columnWidth / 2));
                }
            }

        }

        viewHide();
        playerStop();
    }

    private void viewHide() {
        if (imageView_fullscreen != null) {
            imageView_fullscreen.setVisibility(View.GONE);
            playerView.setVisibility(View.GONE);
            relativeLayout_player.setVisibility(View.VISIBLE);
            linearLayout_detail.setVisibility(View.VISIBLE);
            linearLayout_adView.setVisibility(View.VISIBLE);
            cardView.setVisibility(View.VISIBLE);
            relativeLayout_related.setVisibility(View.VISIBLE);
            linearLayout_comment.setVisibility(View.VISIBLE);
            linearLayout_padding.setPadding(0, 0, 0, dpAsPixels_bottom);
            if (type.equals("notification")) {
                imageView_fullscreen.setVisibility(View.GONE);
            }
        }
    }

    @Subscribe
    public void getComment(Events.Comment comment) {
        if (videoLists.get(0).getId().equals(comment.getVideo_id())) {
            videoLists.get(0).getCommentLists().add(0, new CommentList(comment.getComment_id(),
                    comment.getUser_id(), comment.getUser_name(), comment.getUser_image(),
                    comment.getVideo_id(), comment.getComment_text(), comment.getComment_date()));
            if (commentAdapter != null) {
                commentAdapter.notifyDataSetChanged();
                String buttonTotal = getResources().getString(R.string.view_all) + " " + "(" + comment.getTotal_comment() + ")";
                button_all_comment.setText(buttonTotal);
            }
            if (videoLists.get(0).getCommentLists().size() != 0) {
                textViewNoCommentFound.setVisibility(View.GONE);
            }
        }
    }

    @Subscribe
    public void getNotify(Events.TotalComment totalComment) {
        if (videoLists.get(0).getId().equals(totalComment.getVideo_id())) {
            if (button_all_comment != null) {
                String buttonTotal = getResources().getString(R.string.view_all) + " " + "(" + totalComment.getTotal_comment() + ")";
                button_all_comment.setText(buttonTotal);
            }
        }
        if (totalComment.getType().equals("all_comment")) {
            if (videoLists.get(0).getId().equals(totalComment.getVideo_id())) {
                for (int i = 0; i < videoLists.get(0).getCommentLists().size(); i++) {
                    if (totalComment.getComment_id().equals(videoLists.get(0).getCommentLists().get(i).getComment_id())) {
                        videoLists.get(0).getCommentLists().remove(i);
                        if (commentAdapter != null) {
                            commentAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        }
    }

    private void playerStop() {
        if (player != null) {
            player.setPlayWhenReady(false);
            player.stop();
            player.release();
        }
    }

    @Subscribe
    public void getNotify(Events.FavouriteNotify favouriteNotify) {

        for (int i = 0; i < relatedLists.size(); i++) {
            if (relatedLists.get(i).getId().equals(favouriteNotify.getId())) {
                if (relatedAdapter != null) {
                    relatedAdapter.notifyItemChanged(i);
                }
                if (relatedPortraitAdapter != null) {
                    relatedPortraitAdapter.notifyItemChanged(i);
                }
            }
        }

        if (videoLists.get(0).getId().equals(favouriteNotify.getId())) {
            if (db.checkId_Fav(favouriteNotify.getId())) {
                imageView_fav.setImageDrawable(getResources().getDrawable(R.drawable.ic_fav));
            } else {
                imageView_fav.setImageDrawable(getResources().getDrawable(R.drawable.ic_fav_hov));
            }
        }

    }


    private void detail(final String id, final String user_id) {

        videoLists.clear();
        relatedLists.clear();
        progressBar.setVisibility(View.VISIBLE);

        if (getActivity() != null) {

            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("method_name", "single_video");
            jsObj.addProperty("video_id", id);
            jsObj.addProperty("user_id", user_id);
            params.put("data", API.toBase64(jsObj.toString()));
            client.post(Constant_Api.url, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    if (getActivity() != null) {

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
                                    String id = object.getString("id");
                                    String cat_id = object.getString("cat_id");
                                    String category_name = object.getString("category_name");
                                    String video_title = object.getString("video_title");
                                    String video_url = object.getString("video_url");
                                    String video_layout = object.getString("video_layout");
                                    String video_thumbnail_b = object.getString("video_thumbnail_b");
                                    String video_thumbnail_s = object.getString("video_thumbnail_s");
                                    String total_likes = object.getString("total_likes");
                                    String total_viewer = object.getString("totel_viewer");
                                    String already_like = object.getString("already_like");
                                    String user_id = object.getString("user_id");
                                    String user_name_single = object.getString("user_name");
                                    String user_image = object.getString("user_image");
                                    String already_follow = object.getString("already_follow");
                                    String is_verified = object.getString("is_verified");
                                    String total_comment = object.getString("total_comment");
                                    String watermark_image = object.getString("watermark_image");
                                    String watermark_on_off = object.getString("watermark_on_off");

                                    JSONArray jsonArray_related = object.getJSONArray("related");

                                    for (int j = 0; j < jsonArray_related.length(); j++) {

                                        JSONObject object_related = jsonArray_related.getJSONObject(j);
                                        String id_related = object_related.getString("id");
                                        String cat_id_related = object_related.getString("cat_id");
                                        String category_name_related = object_related.getString("category_name");
                                        String video_title_related = object_related.getString("video_title");
                                        String video_url_related = object_related.getString("video_url");
                                        String video_layout_related = object_related.getString("video_layout");
                                        String video_thumbnail_b_related = object_related.getString("video_thumbnail_b");
                                        String video_thumbnail_s_related = object_related.getString("video_thumbnail_s");
                                        String total_likes_related = object_related.getString("total_likes");
                                        String total_viewer_related = object_related.getString("totel_viewer");
                                        String already_like_related = object_related.getString("already_like");

                                        relatedLists.add(new SubCategoryList("", id_related, cat_id_related, video_title_related, video_url_related, video_layout_related, video_thumbnail_b_related, video_thumbnail_s_related, total_viewer_related, total_likes_related, category_name_related, already_like_related));

                                    }

                                    JSONArray jsonArray_comment = object.getJSONArray("user_comments");
                                    List<CommentList> arrayList = new ArrayList<>();

                                    for (int k = 0; k < jsonArray_comment.length(); k++) {

                                        JSONObject object_comment = jsonArray_comment.getJSONObject(k);
                                        String comment_id = object_comment.getString("comment_id");
                                        String comment_user_id = object_comment.getString("user_id");
                                        String comment_user_name = object_comment.getString("user_name");
                                        String comment_user_image = object_comment.getString("user_image");
                                        String comment_video_id = object_comment.getString("video_id");
                                        String comment_text = object_comment.getString("comment_text");
                                        String comment_date = object_comment.getString("comment_date");

                                        arrayList.add(new CommentList(comment_id, comment_user_id, comment_user_name, comment_user_image, comment_video_id, comment_text, comment_date));

                                    }

                                    videoLists.add(new SubCategoryList("", id, cat_id, video_title, video_url, video_layout, video_thumbnail_b, video_thumbnail_s, total_viewer, total_likes, category_name, already_like, user_id, user_name_single, user_image, already_follow, is_verified, total_comment, watermark_image, watermark_on_off, relatedLists, arrayList));

                                }

                                if (videoLists.size() != 0) {

                                    if (videoLists.get(0).getVideo_layout().equals("Portrait")) {
                                        imageView.setLayoutParams(new RelativeLayout.LayoutParams(columnWidth, columnHeight / 2 + 140));
                                        playerView.setLayoutParams(new RelativeLayout.LayoutParams(columnWidth, columnHeight / 2 + 140));
                                    } else {
                                        imageView.setLayoutParams(new RelativeLayout.LayoutParams(columnWidth, columnWidth / 2));
                                        playerView.setLayoutParams(new RelativeLayout.LayoutParams(columnWidth, columnWidth / 2));
                                    }

                                    //visible single video detail view
                                    linearLayout_padding.setVisibility(View.VISIBLE);

                                    if (db.checkId_Fav(videoLists.get(0).getId())) {
                                        imageView_fav.setImageDrawable(getResources().getDrawable(R.drawable.ic_fav));
                                    } else {
                                        imageView_fav.setImageDrawable(getResources().getDrawable(R.drawable.ic_fav_hov));
                                    }

                                    if (relatedLists.size() == 0) {
                                        textView_noData.setVisibility(View.VISIBLE);
                                    } else {
                                        textView_noData.setVisibility(View.GONE);
                                        if (videoLists.get(0).getVideo_layout().equals("Portrait")) {
                                            if (type.equals("notification")) {
                                                relatedPortraitAdapter = new RelatedPortraitAdapter(getActivity(), relatedLists, interstitialAdView, "notification");
                                            } else {
                                                relatedPortraitAdapter = new RelatedPortraitAdapter(getActivity(), relatedLists, interstitialAdView, "related_single");
                                            }
                                            recyclerView.setAdapter(relatedPortraitAdapter);
                                        } else {
                                            if (type.equals("notification")) {
                                                relatedAdapter = new RelatedAdapter(getActivity(), relatedLists, interstitialAdView, "notification");
                                            } else {
                                                relatedAdapter = new RelatedAdapter(getActivity(), relatedLists, interstitialAdView, "related_single");
                                            }
                                            recyclerView.setAdapter(relatedAdapter);
                                        }

                                    }

                                    if (videoLists.get(0).getCommentLists().size() == 0) {
                                        textViewNoCommentFound.setVisibility(View.VISIBLE);
                                    }
                                    commentAdapter = new CommentAdapter(getActivity(), videoLists.get(0).getCommentLists());
                                    recyclerViewComment.setAdapter(commentAdapter);

                                    progressBar.setVisibility(View.GONE);

                                    if (videoLists.size() != 0) {

                                        if (MainActivity.toolbar != null) {
                                            MainActivity.toolbar.setTitle(videoLists.get(0).getVideo_title());
                                        }
                                        if (type.equals("notification")) {
                                            if (NotificationDetail.toolbar_notification != null) {
                                                NotificationDetail.toolbar_notification.setTitle(videoLists.get(0).getVideo_title());
                                            }
                                        }

                                        textView_view.setText(method.format(Double.parseDouble(videoLists.get(0).getTotal_viewer())));
                                        textView_like.setText(method.format(Double.parseDouble(videoLists.get(0).getTotal_likes())));

                                        Glide.with(getActivity().getApplicationContext()).load(videoLists.get(0).getVideo_thumbnail_b())
                                                .placeholder(R.drawable.placeholder_landscape).into(imageView);

                                        String buttonTotal = getResources().getString(R.string.view_all) + " " + "(" + videoLists.get(0).getTotal_comment() + ")";
                                        button_all_comment.setText(buttonTotal);

                                        if (videoLists.get(0).getAlready_like().equals("true")) {
                                            imageView_like.setImageDrawable(getResources().getDrawable(R.drawable.like_video_hov));
                                        } else {
                                            imageView_like.setImageDrawable(getResources().getDrawable(R.drawable.like_video));
                                        }

                                        updateData();

                                        linearLayout_download.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                imageView_download.startAnimation(myAnim);
                                                if (Constant_Api.aboutUsList != null) {
                                                    if (Constant_Api.aboutUsList.isDownload_video_points_status_ad()) {
                                                        if (Constant_Api.REWARD_VIDEO_AD_COUNT + 1 == Constant_Api.REWARD_VIDEO_AD_COUNT_SHOW) {
                                                            playerStop();
                                                            viewHide();
                                                        }
                                                        method.showVideoAd("download");
                                                    } else {
                                                        downloadVideo();
                                                    }
                                                } else {
                                                    downloadVideo();
                                                }
                                            }
                                        });

                                        imageView_play.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                imageView_play.startAnimation(myAnim);
                                                if (Constant_Api.aboutUsList != null) {
                                                    if (Constant_Api.aboutUsList.isVideo_views_status_ad()) {
                                                        method.showVideoAd("video_play");
                                                    } else {
                                                        playVideo();
                                                    }
                                                } else {
                                                    playVideo();
                                                }
                                            }
                                        });

                                        imageView_like.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                imageView_like.startAnimation(myAnim);
                                                if (method.pref.getBoolean(method.pref_login, false)) {
                                                    if (!videoLists.get(0).getUser_id().equals(method.pref.getString(method.profileId, null))) {
                                                        if (Constant_Api.aboutUsList != null) {
                                                            if (Constant_Api.aboutUsList.isLike_video_points_status_ad()) {
                                                                if (Constant_Api.REWARD_VIDEO_AD_COUNT + 1 == Constant_Api.REWARD_VIDEO_AD_COUNT_SHOW) {
                                                                    playerStop();
                                                                    viewHide();
                                                                }
                                                                method.showVideoAd("like_video");
                                                            } else {
                                                                likeVideo();
                                                            }
                                                        } else {
                                                            likeVideo();
                                                        }
                                                    } else {
                                                        method.alertBox(getResources().getString(R.string.you_have_not_like_video));
                                                    }
                                                } else {
                                                    viewHide();
                                                    playerStop();
                                                    Method.loginBack = true;
                                                    startActivity(new Intent(getActivity(), Login.class));
                                                }
                                            }
                                        });

                                        imageView_fav.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                imageView_fav.startAnimation(myAnim);
                                                if (db.checkId_Fav(videoLists.get(0).getId())) {
                                                    method.addToFav(db, videoLists, 0);
                                                    imageView_fav.setImageDrawable(getResources().getDrawable(R.drawable.ic_fav_hov));
                                                } else {
                                                    db.deleteFav(videoLists.get(0).getId());
                                                    imageView_fav.setImageDrawable(getResources().getDrawable(R.drawable.ic_fav));
                                                }
                                                Events.FavouriteNotify homeNotify = new Events.FavouriteNotify(videoLists.get(0).getId(), videoLists.get(0).getVideo_layout());
                                                GlobalBus.getBus().post(homeNotify);
                                            }
                                        });

                                        imageViewWhatsApp.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                imageViewWhatsApp.startAnimation(myAnim);
                                                shareType("whatsapp");
                                            }
                                        });

                                        imageViewFacebook.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                imageViewFacebook.startAnimation(myAnim);
                                                shareType("facebook");
                                            }
                                        });

                                        imageViewInstagram.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                imageViewInstagram.startAnimation(myAnim);
                                                shareType("instagram");
                                            }
                                        });

                                        imageViewTwitter.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                imageViewTwitter.startAnimation(myAnim);
                                                shareType("twitter");
                                            }
                                        });

                                        imageView_more.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                viewHide();
                                                playerStop();
                                                imageView_more.startAnimation(myAnim);
                                                BottomSheetDialogFragment bottomSheetDialogFragment = new BottomSheetOption();
                                                Bundle args = new Bundle();
                                                args.putString("id", videoLists.get(0).getId());
                                                args.putString("url", videoLists.get(0).getVideo_url());
                                                bottomSheetDialogFragment.setArguments(args);
                                                bottomSheetDialogFragment.show(getActivity().getSupportFragmentManager(), "Bottom Sheet Dialog Fragment");
                                            }

                                        });

                                        checkLogin_following();

                                        cardView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (!type.equals("notification")) {
                                                    viewHide();
                                                    playerStop();
                                                    ProfileFragment profileFragment = new ProfileFragment();
                                                    Bundle bundle_profile = new Bundle();
                                                    bundle_profile.putString("type", "other_user");
                                                    bundle_profile.putString("id", videoLists.get(0).getUser_id());
                                                    profileFragment.setArguments(bundle_profile);
                                                    getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, profileFragment, getResources().getString(R.string.profile)).addToBackStack(getResources().getString(R.string.profile)).commitAllowingStateLoss();
                                                }
                                            }
                                        });

                                        button.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                viewHide();
                                                playerStop();

                                                String string = getResources().getString(R.string.related_video);
                                                MainActivity.toolbar.setTitle(getResources().getString(R.string.related_video));
                                                RelatedFragment relatedFragment = new RelatedFragment();
                                                Bundle bundle = new Bundle();
                                                bundle.putString("type", "related");
                                                bundle.putString("typeLayout", videoLists.get(0).getVideo_layout());
                                                bundle.putSerializable("array", (Serializable) videoLists.get(0).getRelatedList());
                                                relatedFragment.setArguments(bundle);
                                                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, relatedFragment, string).addToBackStack(string).commit();
                                            }
                                        });

                                        if (method.pref.getBoolean(method.pref_login, false)) {
                                            String image = method.pref.getString(method.userImage, null);
                                            if (image != null && !image.equals("")) {
                                                Glide.with(getActivity().getApplicationContext()).load(image)
                                                        .placeholder(R.drawable.user_profile)
                                                        .into(imageView_comment);
                                            }
                                        }

                                        button_all_comment.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                viewHide();
                                                playerStop();
                                                startActivity(new Intent(getActivity(), AllComment.class)
                                                        .putExtra("videoId", videoLists.get(0).getId())
                                                        .putExtra("array", (Serializable) videoLists.get(0).getCommentLists()));
                                            }
                                        });

                                        editTextComment.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                viewHide();
                                                playerStop();

                                                inputMethodManager.toggleSoftInputFromWindow(
                                                        editTextComment.getApplicationWindowToken(),
                                                        InputMethodManager.SHOW_FORCED, 0);

                                                if (method.pref.getBoolean(method.pref_login, false)) {

                                                    final Dialog dialog = new Dialog(getActivity());
                                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                    dialog.setContentView(R.layout.dialogbox_comment);
                                                    dialog.getWindow().setLayout(ViewPager.LayoutParams.FILL_PARENT, ViewPager.LayoutParams.WRAP_CONTENT);
                                                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                                                    Window window = dialog.getWindow();
                                                    WindowManager.LayoutParams wlp = window.getAttributes();
                                                    wlp.gravity = Gravity.BOTTOM;
                                                    wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                                                    window.setAttributes(wlp);
                                                    ImageView imageView_profile = dialog.findViewById(R.id.imageView_profile_dialogBox_comment);
                                                    ImageView imageView = dialog.findViewById(R.id.imageView_dialogBox_comment);
                                                    final EditText editText = dialog.findViewById(R.id.editText_dialogbox_comment);

                                                    String image = method.pref.getString(method.userImage, null);
                                                    if (image != null && !image.equals("")) {
                                                        Glide.with(getActivity().getApplicationContext()).load(image)
                                                                .placeholder(R.drawable.user_profile)
                                                                .into(imageView_profile);
                                                    }

                                                    imageView.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            editText.setError(null);
                                                            String comment = editText.getText().toString();
                                                            if (comment.equals("") || comment.isEmpty()) {
                                                                editText.requestFocus();
                                                                editText.setError(getResources().getString(R.string.please_enter_comment));
                                                            } else {
                                                                if (Method.isNetworkAvailable(getActivity())) {
                                                                    editText.clearFocus();
                                                                    inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                                                                    Comment(method.pref.getString(method.profileId, null), comment);
                                                                } else {
                                                                    Toast.makeText(getActivity(), getResources().getString(R.string.internet_connection), Toast.LENGTH_SHORT).show();
                                                                }
                                                                dialog.dismiss();
                                                            }
                                                        }
                                                    });

                                                    dialog.show();

                                                } else {
                                                    Method.loginBack = true;
                                                    startActivity(new Intent(getActivity(), Login.class));
                                                }
                                            }
                                        });
                                    }
                                }

                            }

                            progressBar.setVisibility(View.GONE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                            textView_noDataMain.setVisibility(View.VISIBLE);
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }

                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    progressBar.setVisibility(View.GONE);
                    textView_noDataMain.setVisibility(View.VISIBLE);
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }

    }

    private void checkLogin_following() {

        if (!videoLists.get(0).getUser_image().equals("")) {
            Glide.with(getActivity().getApplicationContext()).load(videoLists.get(0).getUser_image())
                    .placeholder(R.drawable.user_profile).into(circleImageView);
        }
        textView_userName.setText(videoLists.get(0).getUser_name());
        if (videoLists.get(0).getIs_verified().equals("true")) {
            textView_userName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_verification, 0);
        }
        if (method.pref.getBoolean(method.pref_login, false)) {
            if (videoLists.get(0).getAlready_follow().equals("true")) {
                button_follow.setText(getResources().getString(R.string.unfollow));
            } else {
                button_follow.setText(getResources().getString(R.string.follow));
            }
        } else {
            button_follow.setText(getResources().getString(R.string.follow));
        }

        button_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (method.pref.getBoolean(method.pref_login, false)) {
                    String user_id = method.pref.getString(method.profileId, null);
                    assert user_id != null;
                    if (user_id.equals(videoLists.get(0).getUser_id())) {
                        method.alertBox(getResources().getString(R.string.you_have_not_onFollow));
                    } else {
                        follow(user_id, videoLists.get(0).getUser_id());
                    }
                } else {
                    viewHide();
                    playerStop();
                    Method.loginBack = true;
                    startActivity(new Intent(getActivity(), Login.class));
                }
            }
        });
    }

    private void playVideo() {

        if (type.equals("notification")) {
            imageView_fullscreen.setVisibility(View.GONE);
        } else {
            imageView_fullscreen.setVisibility(View.VISIBLE);
        }

        playerView.setVisibility(View.VISIBLE);
        progressBar_player.setVisibility(View.VISIBLE);

        DefaultTrackSelector trackSelector = new DefaultTrackSelector();
        player = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector);
        playerView.setPlayer(player);

        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getActivity(),
                Util.getUserAgent(getActivity(), getResources().getString(R.string.app_name)));
        // This is the MediaSource representing the media to be played.
        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(videoLists.get(0).getVideo_url()));
        // Prepare the player with the source.
        player.prepare(videoSource);
        player.setPlayWhenReady(true);
        player.addListener(new Player.DefaultEventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playWhenReady) {
                    progressBar_player.setVisibility(View.GONE);
                }
                if (playbackState == Player.STATE_ENDED) {
                    if (isView) {
                        isView = false;
                        if (method.pref.getBoolean(method.pref_login, false)) {
                            if (!method.pref.getString(method.profileId, null).equals(videoLists.get(0).getUser_id())) {
                                video_view(method.pref.getString(method.profileId, null));
                            }
                        }
                    }
                }
                super.onPlayerStateChanged(playWhenReady, playbackState);
            }
        });
    }

    private void shareType(String share_type) {

        viewHide();
        playerStop();

        if (Method.isNetworkAvailable(getActivity())) {

            if (Method.allowPermitionExternalStorage) {

                switch (share_type) {

                    case "whatsapp":
                        if (method.isAppInstalled_Whatsapp(getActivity())) {
                            new ShareVideo().execute(videoLists.get(0).getVideo_url(), videoLists.get(0).getId(), "whatsapp");
                        } else {
                            method.alertBox(getResources().getString(R.string.please_install_whatsapp));
                        }
                        break;
                    case "facebook":
                        if (method.isAppInstalled_facebook(getActivity())) {
                            new ShareVideo().execute(videoLists.get(0).getVideo_url(), videoLists.get(0).getId(), "facebook");
                        } else {
                            method.alertBox(getResources().getString(R.string.please_install_facebook));
                        }
                        break;
                    case "twitter":
                        if (method.isAppInstalled_twitter(getActivity())) {
                            new ShareVideo().execute(videoLists.get(0).getVideo_url(), videoLists.get(0).getId(), "twitter");
                        } else {
                            method.alertBox(getResources().getString(R.string.please_install_twitter));
                        }
                        break;
                    case "instagram":
                        if (method.isAppInstalled_Instagram(getActivity())) {
                            new ShareVideo().execute(videoLists.get(0).getVideo_url(), videoLists.get(0).getId(), "instagram");
                        } else {
                            method.alertBox(getResources().getString(R.string.please_install_instagram));
                        }
                        break;

                }

            } else {
                Toast.makeText(getActivity(), getResources().getString(R.string.cannot_use_save_permission), Toast.LENGTH_SHORT).show();
            }
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

    }

    private void downloadVideo() {

        if (Method.isNetworkAvailable(getActivity())) {
            if (Method.isDownload) {
                if (Method.allowPermitionExternalStorage) {
                    method.download(videoLists.get(0).getId(), videoLists.get(0).getCid(),
                            videoLists.get(0).getVideo_title(), videoLists.get(0).getCategory_name(),
                            videoLists.get(0).getVideo_thumbnail_s(),
                            videoLists.get(0).getVideo_url(),
                            videoLists.get(0).getVideo_layout(),
                            videoLists.get(0).getWatermark_image(),
                            videoLists.get(0).getWatermark_on_off());
                    if (method.pref.getBoolean(method.pref_login, false)) {
                        if (!method.pref.getString(method.profileId, null).equals(videoLists.get(0).getUser_id())) {
                            downloadCount(method.pref.getString(method.profileId, null));
                        }
                    }
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.cannot_use_save_permission), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), getResources().getString(R.string.download_later), Toast.LENGTH_SHORT).show();
            }
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

    }

    private void likeVideo() {
        like_video(method.pref.getString(method.profileId, null));
    }

    @SuppressLint("StaticFieldLeak")
    public class ShareVideo extends AsyncTask<String, String, String> {

        private ProgressDialog progressDialog;
        private String iconsStoragePath;
        private File sdIconStorageDir;
        private String type;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setIndeterminate(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMessage(getResources().getString(R.string.please_wait));
            progressDialog.setCancelable(false);
            progressDialog.setMax(100);
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.cancel_dialog), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (sdIconStorageDir != null) {
                        sdIconStorageDir.delete();
                    }
                    dialog.dismiss();
                    cancel(true);
                }
            });
            progressDialog.show();
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(String... params) {

            int count;
            try {
                URL url = new URL(params[0]);
                String id = params[1];
                type = params[2];
                iconsStoragePath = getActivity().getExternalCacheDir().getAbsolutePath();
                String filePath = "file" + id + ".mp4";

                sdIconStorageDir = new File(iconsStoragePath, filePath);

                //create storage directories, if they don't exist
                if (sdIconStorageDir.exists()) {
                    Log.d("File_name", sdIconStorageDir.toString());
                } else {
                    URLConnection conection = url.openConnection();
                    conection.setRequestProperty("Accept-Encoding", "identity");
                    conection.connect();
                    // getting file length
                    int lenghtOfFile = conection.getContentLength();
                    // input stream to read file - with 8k buffer
                    InputStream input = new BufferedInputStream(url.openStream(), 8192);
                    // Output stream to write file
                    OutputStream output = new FileOutputStream(sdIconStorageDir);
                    byte data[] = new byte[1024];
                    long total = 0;
                    while ((count = input.read(data)) != -1) {
                        total += count;
                        // publishing the progress....
                        progressDialog.setProgress((int) (total * 100 / lenghtOfFile));
                        Log.d("progressDialog", String.valueOf((int) (total * 100 / lenghtOfFile)));
                        output.write(data, 0, count);
                    }
                    output.flush(); // flushing output
                    output.close();// closing streams
                    input.close();
                }

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            Log.d("exiqute", "exiqute");

            progressDialog.dismiss();
            switch (type) {
                case "whatsapp":
                    shareVideoWhatsApp(sdIconStorageDir.toString());
                    break;
                case "facebook":
                    Intent share_fb = new Intent(Intent.ACTION_SEND);// Create the new Intent using the 'Send' action.
                    share_fb.setType("video/*");   // Set the MIME type
                    share_fb.setPackage("com.facebook.katana");
                    File media_fb = new File(sdIconStorageDir.toString()); // Create the URI from the media
                    Uri uri_fb = Uri.fromFile(media_fb); // Add the URI to the Intent.
                    share_fb.putExtra(Intent.EXTRA_STREAM, uri_fb); // Broadcast the Intent.
                    startActivity(Intent.createChooser(share_fb, "Share to"));
                    break;
                case "instagram":
                    shareInstagram(sdIconStorageDir);
                    break;
                case "twitter":
                    Intent share_tw = new Intent(Intent.ACTION_SEND);// Create the new Intent using the 'Send' action.
                    share_tw.setType("video/*");   // Set the MIME type
                    share_tw.setPackage("com.twitter.android");
                    File media_tw = new File(sdIconStorageDir.toString()); // Create the URI from the media
                    Uri uri_tw = Uri.fromFile(media_tw); // Add the URI to the Intent.
                    share_tw.putExtra(Intent.EXTRA_STREAM, uri_tw); // Broadcast the Intent.
                    startActivity(Intent.createChooser(share_tw, "Share to"));
                    break;
                default:
                    break;
            }

        }

    }

    private void shareVideoWhatsApp(String path) {

        Uri uri = Uri.parse(path);
        Intent videoshare = new Intent(Intent.ACTION_SEND);
        videoshare.setType("*/*");
        videoshare.setPackage("com.whatsapp");
        videoshare.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        videoshare.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(videoshare);

    }

    private void shareInstagram(File sdIconStorageDir) {

        try {
            Intent intent = new Intent("com.instagram.share.ADD_TO_STORY");
            intent.setDataAndType(Uri.fromFile(sdIconStorageDir), "video/*");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra("content_url", "");

            // Instantiate activity and verify it will resolve implicit intent
            Activity activity = getActivity();
            if (activity.getPackageManager().resolveActivity(intent, 0) != null) {
                activity.startActivityForResult(intent, 0);
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    private void like_video(String profileId) {

        if (getActivity() != null) {

            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("method_name", "user_video_like");
            jsObj.addProperty("like", "1");
            jsObj.addProperty("device_id", profileId);
            jsObj.addProperty("post_id", videoLists.get(0).getId());
            params.put("data", API.toBase64(jsObj.toString()));
            client.post(Constant_Api.url, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    if (getActivity() != null) {

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
                                    String msg = object.getString("msg");
                                    String success = object.getString("success");

                                    if (success.equals("1")) {

                                        String activity_status = object.getString("activity_status");
                                        String total_likes = object.getString("total_likes");

                                        String isString = "";

                                        if (activity_status.equals("1")) {
                                            videoLists.get(0).setTotal_likes(total_likes);
                                            textView_like.setText(method.format(Double.parseDouble(videoLists.get(0).getTotal_likes())));
                                            isString = "true";
                                            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                                        } else {
                                            isString = "false";
                                            videoLists.get(0).setTotal_likes(total_likes);
                                            textView_like.setText(method.format(Double.parseDouble(videoLists.get(0).getTotal_likes())));
                                            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                                        }

                                        if (passId.equals(videoLists.get(0).getId())) {
                                            if (type.equals("sub_category")) {
                                                Events.SubCatNotify subCatNotify = new Events.SubCatNotify("",
                                                        total_likes, isString,
                                                        "like", position);
                                                GlobalBus.getBus().post(subCatNotify);
                                            }
                                            if (type.equals("home_sub")) {
                                                Events.HomeSubCatNotify homeSubCatNotify = new Events.HomeSubCatNotify("",
                                                        total_likes, isString,
                                                        "like", position);
                                                GlobalBus.getBus().post(homeSubCatNotify);
                                            }
                                            if (type.equals("my_video")) {
                                                Events.MyVideoView myVideoView = new Events.MyVideoView("",
                                                        total_likes, isString,
                                                        "like", position);
                                                GlobalBus.getBus().post(myVideoView);
                                            }
                                            if (type.equals("related")) {
                                                Events.RelatedFragmentNotify relatedFragmentNotify = new Events.RelatedFragmentNotify("",
                                                        total_likes, isString, "like", videoLists.get(0).getId(), position);
                                                GlobalBus.getBus().post(relatedFragmentNotify);
                                            }
                                            if (type.equals("favorites")) {
                                                Events.FavouriteNotify homeNotify = new Events.FavouriteNotify("", "");
                                                GlobalBus.getBus().post(homeNotify);
                                            }
                                            if (type.equals("search")) {
                                                Events.SearchFragmentNotify searchFragmentNotify = new Events.SearchFragmentNotify("",
                                                        total_likes, isString, "like", position);
                                                GlobalBus.getBus().post(searchFragmentNotify);
                                            }
                                        }

                                        if (!db.checkId_Fav(videoLists.get(0).getId())) {
                                            db.updateVideoLike(videoLists.get(0).getId(), total_likes, isString);
                                        }

                                        if (activity_status.equals("1")) {
                                            imageView_like.setImageDrawable(getResources().getDrawable(R.drawable.like_video_hov));
                                        } else {
                                            imageView_like.setImageDrawable(getResources().getDrawable(R.drawable.like_video));
                                        }

                                    }

                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }

                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }

    }

    private void video_view(String profileId) {

        if (getActivity() != null) {

            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("method_name", "single_video_view_count");
            jsObj.addProperty("video_id", videoLists.get(0).getId());
            jsObj.addProperty("owner_id", videoLists.get(0).getUser_id());
            jsObj.addProperty("user_id", profileId);
            params.put("data", API.toBase64(jsObj.toString()));
            client.post(Constant_Api.url, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    if (getActivity() != null) {

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
                                    String msg = object.getString("msg");
                                    String success = object.getString("success");
                                }

                                int total_view = Integer.parseInt(videoLists.get(0).getTotal_viewer());
                                total_view++;
                                videoLists.get(0).setTotal_viewer(String.valueOf(total_view));
                                textView_view.setText(method.format(Double.parseDouble(String.valueOf(total_view))));

                                if (passId.equals(videoLists.get(0).getId())) {

                                    if (type.equals("slider")) {
                                        Events.FragmentSliderNotify fragmentSliderNotify = new Events.FragmentSliderNotify(String.valueOf(total_view), position);
                                        GlobalBus.getBus().post(fragmentSliderNotify);
                                    }

                                    if (type.equals("my_video")) {
                                        Events.MyVideoView myVideoView = new Events.MyVideoView(String.valueOf(total_view),
                                                "", "", "view", position);
                                        GlobalBus.getBus().post(myVideoView);
                                    }

                                    if (type.equals("related")) {
                                        Events.RelatedFragmentNotify relatedFragmentNotify = new Events.RelatedFragmentNotify(String.valueOf(total_view),
                                                "", "", "view", videoLists.get(0).getId(), position);
                                        GlobalBus.getBus().post(relatedFragmentNotify);
                                    }

                                    if (type.equals("sub_category")) {
                                        Events.SubCatNotify subCatNotify = new Events.SubCatNotify(String.valueOf(total_view),
                                                "", "", "view", position);
                                        GlobalBus.getBus().post(subCatNotify);
                                    }

                                    if (type.equals("home_sub")) {
                                        Events.HomeSubCatNotify homeSubCatNotify = new Events.HomeSubCatNotify(String.valueOf(total_view),
                                                "", "", "view", position);
                                        GlobalBus.getBus().post(homeSubCatNotify);
                                    }

                                    if (type.equals("favorites")) {
                                        Events.FavouriteNotify homeNotify = new Events.FavouriteNotify("", "");
                                        GlobalBus.getBus().post(homeNotify);
                                    }

                                    if (type.equals("search")) {
                                        Events.SearchFragmentNotify searchFragmentNotify = new Events.SearchFragmentNotify(String.valueOf(total_view),
                                                "", "", "view", position);
                                        GlobalBus.getBus().post(searchFragmentNotify);
                                    }

                                }

                                if (!db.checkId_Fav(videoLists.get(0).getId())) {
                                    db.updateVideoView(videoLists.get(0).getId(), String.valueOf(total_view));
                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }

                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }

    }

    private void downloadCount(String profileId) {

        if (getActivity() != null) {

            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("method_name", "single_video_download");
            jsObj.addProperty("video_id", videoLists.get(0).getId());
            jsObj.addProperty("user_id", profileId);
            params.put("data", API.toBase64(jsObj.toString()));
            client.post(Constant_Api.url, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    if (getActivity() != null) {

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
                                    String msg = object.getString("msg");
                                    String success = object.getString("success");
                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }

                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }

    }

    private void updateData() {
        if (passId.equals(videoLists.get(0).getId())) {

            if (type.equals("slider")) {
                Events.FragmentSliderNotify fragmentSliderNotify = new Events.FragmentSliderNotify(videoLists.get(0).getTotal_viewer(), position);
                GlobalBus.getBus().post(fragmentSliderNotify);
            }

            if (type.equals("my_video")) {
                Events.MyVideoView myVideoView = new Events.MyVideoView(videoLists.get(0).getTotal_viewer(),
                        videoLists.get(0).getTotal_likes(), videoLists.get(0).getAlready_like(),
                        "all", position);
                GlobalBus.getBus().post(myVideoView);
            }

            if (type.equals("related")) {
                Events.RelatedFragmentNotify relatedFragmentNotify = new Events.RelatedFragmentNotify(videoLists.get(0).getTotal_viewer(),
                        videoLists.get(0).getTotal_likes(),
                        videoLists.get(0).getAlready_like(),
                        "all", videoLists.get(0).getId(), position);
                GlobalBus.getBus().post(relatedFragmentNotify);
            }

            if (type.equals("sub_category")) {
                Events.SubCatNotify subCatNotify = new Events.SubCatNotify(videoLists.get(0).getTotal_viewer(),
                        videoLists.get(0).getTotal_likes(), videoLists.get(0).getAlready_like(),
                        "all", position);
                GlobalBus.getBus().post(subCatNotify);
            }

            if (type.equals("home_sub")) {
                Events.HomeSubCatNotify homeSubCatNotify = new Events.HomeSubCatNotify(videoLists.get(0).getTotal_viewer(),
                        videoLists.get(0).getTotal_likes(), videoLists.get(0).getAlready_like(),
                        "all", position);
                GlobalBus.getBus().post(homeSubCatNotify);
            }

            if (type.equals("favorites")) {
                Events.FavouriteNotify homeNotify = new Events.FavouriteNotify("", "");
                GlobalBus.getBus().post(homeNotify);
            }

            if (type.equals("search")) {
                Events.SearchFragmentNotify searchFragmentNotify = new Events.SearchFragmentNotify(videoLists.get(0).getTotal_viewer(),
                        videoLists.get(0).getTotal_likes(),
                        videoLists.get(0).getAlready_like(), "all", position);
                GlobalBus.getBus().post(searchFragmentNotify);
            }

        }

        if (!db.checkId_Fav(videoLists.get(0).getId())) {
            db.updateVideoView(videoLists.get(0).getId(), videoLists.get(0).getTotal_viewer());
            db.updateVideoLike(videoLists.get(0).getId(), videoLists.get(0).getTotal_likes(), videoLists.get(0).getAlready_like());
        }

    }

    private void Comment(final String userId, final String comment) {

        if (getActivity() != null) {

            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("method_name", "user_video_comment");
            jsObj.addProperty("comment_text", comment);
            jsObj.addProperty("user_id", userId);
            jsObj.addProperty("post_id", videoLists.get(0).getId());
            params.put("data", API.toBase64(jsObj.toString()));
            client.post(Constant_Api.url, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    if (getActivity() != null) {

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

                                String msg = jsonObject.getString("msg");
                                String success = jsonObject.getString("success");

                                if (success.equals("1")) {

                                    String total_comment = jsonObject.getString("total_comment");

                                    String comment_id = jsonObject.getString("comment_id");
                                    String comment_user_id = jsonObject.getString("user_id");
                                    String comment_user_name = jsonObject.getString("user_name");
                                    String comment_user_image = jsonObject.getString("user_image");
                                    String comment_video_id = jsonObject.getString("video_id");
                                    String comment_text = jsonObject.getString("comment_text");
                                    String comment_date = jsonObject.getString("comment_date");

                                    videoLists.get(0).getCommentLists().add(0, new CommentList(comment_id, comment_user_id, comment_user_name, comment_user_image, comment_video_id, comment_text, comment_date));

                                    textViewNoCommentFound.setVisibility(View.GONE);

                                    commentAdapter.notifyDataSetChanged();
                                    String buttonTotal = getResources().getString(R.string.view_all) + " " + "(" + total_comment + ")";
                                    button_all_comment.setText(buttonTotal);

                                    Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();

                                } else {
                                    method.alertBox(msg);
                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }

                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }

    }

    private void follow(String user_id, String other_user) {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        if (getActivity() != null) {

            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("method_name", "user_follow");
            jsObj.addProperty("user_id", other_user);
            jsObj.addProperty("follower_id", user_id);
            params.put("data", API.toBase64(jsObj.toString()));
            client.post(Constant_Api.url, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    if (getActivity() != null) {

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
                                    String msg = object.getString("msg");
                                    String success = object.getString("success");

                                    if (success.equals("1")) {
                                        String activity_status = object.getString("activity_status");
                                        if (activity_status.equals("1")) {
                                            button_follow.setText(getResources().getString(R.string.unfollow));
                                        } else {
                                            button_follow.setText(getResources().getString(R.string.follow));
                                        }
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

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    progressDialog.dismiss();
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Unregister the registered event.
        GlobalBus.getBus().unregister(this);
    }

}
