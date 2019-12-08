package com.socialgaming.appsclub.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.socialgaming.appsclub.Adapter.AllCommentAdapter;
import com.socialgaming.appsclub.Item.CommentList;
import com.socialgaming.appsclub.R;
import com.socialgaming.appsclub.Util.API;
import com.socialgaming.appsclub.Util.Constant_Api;
import com.socialgaming.appsclub.Util.Events;
import com.socialgaming.appsclub.Util.GlobalBus;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import cz.msebera.android.httpclient.Header;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;


public class AllComment extends AppCompatActivity {

    private Method method;
    public Toolbar toolbar;
    private String videoId;
    private ProgressBar progressBar;
    private TextView textView_NoData;
    private RecyclerView recyclerView;
    private AllCommentAdapter allCommentAdapter;
    private EditText editTextComment;
    private List<CommentList> commentLists;
    private InputMethodManager inputMethodManager;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_comment);

        Method.forceRTLIfSupported(getWindow(), AllComment.this);

        method = new Method(AllComment.this);

        commentLists = new ArrayList<>();

        Intent intent = getIntent();
        videoId = intent.getStringExtra("videoId");

        toolbar = findViewById(R.id.toolbar_all_comment);
        toolbar.setTitle(getResources().getString(R.string.allcomment));
        setSupportActionBar(toolbar);

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        progressBar = findViewById(R.id.progressbar_all_Comment);
        textView_NoData = findViewById(R.id.textView_noComment_all_Comment);
        editTextComment = findViewById(R.id.EditText_comment_allComment);
        ImageView imageView = findViewById(R.id.imageView_allComment);
        recyclerView = findViewById(R.id.recyclerView_all_comment);

        editTextComment.setClickable(true);
        editTextComment.setFocusable(false);

        if (method.pref.getBoolean(method.pref_login, false)) {
            String image = method.pref.getString(method.userImage, null);
            if (image != null && !image.equals("")) {
                Glide.with(AllComment.this).load(image)
                        .placeholder(R.drawable.user_profile)
                        .into(imageView);
            }
        }

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(AllComment.this);
        recyclerView.setLayoutManager(layoutManager);

        editTextComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (method.pref.getBoolean(method.pref_login, false)) {

                    inputMethodManager.toggleSoftInputFromWindow(
                            editTextComment.getApplicationWindowToken(),
                            InputMethodManager.SHOW_FORCED, 0);

                    final Dialog dialog = new Dialog(AllComment.this);
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
                        Glide.with(AllComment.this).load(image)
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
                                if (Method.isNetworkAvailable(AllComment.this)) {
                                    editText.clearFocus();
                                    inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                                    Comment(method.pref.getString(method.profileId, null), comment);
                                } else {
                                    Toast.makeText(AllComment.this, getResources().getString(R.string.internet_connection), Toast.LENGTH_SHORT).show();
                                }
                                dialog.dismiss();
                            }
                        }
                    });

                    dialog.show();

                } else {
                    Method.loginBack = true;
                    startActivity(new Intent(AllComment.this, Login.class));
                }


            }
        });

        if (Method.isNetworkAvailable(AllComment.this)) {
            getComment(videoId);
        } else {
            progressBar.setVisibility(View.GONE);
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void getComment(String video_id) {

        commentLists.clear();
        progressBar.setVisibility(View.VISIBLE);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(AllComment.this));
        jsObj.addProperty("method_name", "get_all_comment");
        jsObj.addProperty("video_id", video_id);
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
                        if(status.equals("-2")){
                            method.suspend(message);
                        }else {
                            method.alertBox(message);
                        }

                    } else {

                        JSONArray jsonArray = jsonObject.getJSONArray(Constant_Api.tag);

                        for (int k = 0; k < jsonArray.length(); k++) {

                            JSONObject object = jsonArray.getJSONObject(k);
                            String comment_id = object.getString("comment_id");
                            String comment_user_id = object.getString("user_id");
                            String comment_user_name = object.getString("user_name");
                            String comment_user_image = object.getString("user_image");
                            String comment_video_id = object.getString("video_id");
                            String comment_text = object.getString("comment_text");
                            String comment_date = object.getString("comment_date");

                            commentLists.add(new CommentList(comment_id, comment_user_id, comment_user_name, comment_user_image, comment_video_id, comment_text, comment_date));

                        }

                        if (commentLists.size() == 0) {
                            textView_NoData.setVisibility(View.VISIBLE);
                        } else {
                            textView_NoData.setVisibility(View.GONE);
                            allCommentAdapter = new AllCommentAdapter(AllComment.this, commentLists);
                            recyclerView.setAdapter(allCommentAdapter);
                        }

                    }

                    progressBar.setVisibility(View.GONE);

                } catch (JSONException e) {
                    progressBar.setVisibility(View.GONE);
                    e.printStackTrace();
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

    public void Comment(final String userId, final String comment) {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(AllComment.this));
        jsObj.addProperty("method_name", "user_video_comment");
        jsObj.addProperty("comment_text", comment);
        jsObj.addProperty("user_id", userId);
        jsObj.addProperty("post_id", videoId);
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
                        if(status.equals("-2")){
                            method.suspend(message);
                        }else {
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

                            textView_NoData.setVisibility(View.GONE);

                            commentLists.add(0, new CommentList(comment_id, comment_user_id, comment_user_name, comment_user_image, comment_video_id, comment_text, comment_date));

                            if (allCommentAdapter == null) {
                                allCommentAdapter = new AllCommentAdapter(AllComment.this, commentLists);
                                recyclerView.setAdapter(allCommentAdapter);
                            } else {
                                allCommentAdapter.notifyDataSetChanged();
                            }
                            Events.Comment commentNotify = new Events.Comment(comment_id, comment_user_id, comment_user_name, comment_user_image, comment_video_id, comment_text, comment_date, total_comment);
                            GlobalBus.getBus().post(commentNotify);

                            Toast.makeText(AllComment.this, msg, Toast.LENGTH_SHORT).show();

                        } else {
                            method.alertBox(msg);
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });
    }

}
