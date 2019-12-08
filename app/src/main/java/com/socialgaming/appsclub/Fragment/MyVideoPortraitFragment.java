package com.socialgaming.appsclub.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.socialgaming.appsclub.Adapter.MyVideoPortraitAdapter;
import com.socialgaming.appsclub.InterFace.InterstitialAdView;
import com.socialgaming.appsclub.Item.SubCategoryList;
import com.socialgaming.appsclub.R;
import com.socialgaming.appsclub.Util.API;
import com.socialgaming.appsclub.Util.Constant_Api;
import com.socialgaming.appsclub.Util.EndlessRecyclerViewScrollListener;
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

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cz.msebera.android.httpclient.Header;


public class MyVideoPortraitFragment extends Fragment {

    private Method method;
    private ProgressBar progressBar;
    private String typeLayout, type, id;
    private TextView textView_noData;
    private RecyclerView recyclerView;
    private MyVideoPortraitAdapter myVideoPortraitAdapter;
    private List<SubCategoryList> myVideoLists;
    private LayoutAnimationController animation;
    private InterstitialAdView interstitialAdView;
    private Boolean isOver = false;
    private int pagination_index = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.my_upload_fragment, container, false);

        interstitialAdView = new InterstitialAdView() {
            @Override
            public void position(int position, String type, String id) {
                SCDetailFragment scDetailFragment = new SCDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putString("id", id);
                bundle.putString("type", type);
                bundle.putInt("position", position);
                scDetailFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, scDetailFragment, myVideoLists.get(position).getVideo_title()).addToBackStack(myVideoLists.get(position).getVideo_title()).commitAllowingStateLoss();
            }
        };
        method = new Method(getActivity(), interstitialAdView);

        myVideoLists = new ArrayList<>();

        assert getArguments() != null;
        typeLayout = getArguments().getString("typeLayout");
        type = getArguments().getString("type");
        id = getArguments().getString("id");

        int resId = R.anim.layout_animation_fall_down;
        animation = AnimationUtils.loadLayoutAnimation(getActivity(), resId);

        progressBar = view.findViewById(R.id.progressbar_myUpload_fragment);
        textView_noData = view.findViewById(R.id.textView_myUpload_fragment);
        recyclerView = view.findViewById(R.id.recyclerView_myUpload_fragment);

        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (myVideoPortraitAdapter.getItemViewType(position)) {
                    case 0:
                        return 2;
                    case 2:
                        return 2;
                    default:
                        return 1;
                }
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (!isOver) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pagination_index++;
                            callData();
                        }
                    }, 1000);
                } else {
                    myVideoPortraitAdapter.hideHeader();
                }
            }
        });

        textView_noData.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        callData();

        return view;

    }

    private void callData() {
        if (getActivity() != null) {
            if (Method.isNetworkAvailable(getActivity())) {
                if (getActivity() != null) {
                    MyVideo(id, typeLayout);
                }
            } else {
                textView_noData.setVisibility(View.VISIBLE);
                textView_noData.setText(getResources().getString(R.string.no_data_found));
                recyclerView.setVisibility(View.GONE);
                method.alertBox(getResources().getString(R.string.internet_connection));
            }
        }
    }

    private void MyVideo(final String id, String typeLayout) {

        if (myVideoPortraitAdapter == null) {
            myVideoLists.clear();
            progressBar.setVisibility(View.VISIBLE);
        }

        if (getActivity() != null) {

            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("method_name", "user_video_list");
            jsObj.addProperty("user_id", id);
            if (method.pref.getBoolean(method.pref_login, false)) {
                if (id.equals(method.pref.getString(method.profileId, null))) {
                    jsObj.addProperty("login_user", "true");
                } else {
                    jsObj.addProperty("login_user", "false");
                }
            } else {
                jsObj.addProperty("login_user", "false");
            }
            jsObj.addProperty("page", pagination_index);
            jsObj.addProperty("filter_value", typeLayout);
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
                                    String video_title = object.getString("video_title");
                                    String video_url = object.getString("video_url");
                                    String video_layout = object.getString("video_layout");
                                    String video_thumbnail_b = object.getString("video_thumbnail_b");
                                    String video_thumbnail_s = object.getString("video_thumbnail_s");
                                    String total_likes = object.getString("total_likes");
                                    String total_viewer = object.getString("totel_viewer");
                                    String category_name = object.getString("category_name");
                                    String already_like = object.getString("already_like");

                                    myVideoLists.add(new SubCategoryList("", id, null, video_title, video_url, video_layout, video_thumbnail_b, video_thumbnail_s, total_viewer, total_likes, category_name, already_like));

                                }

                                if (jsonArray.length() == 0) {
                                    if (myVideoPortraitAdapter != null) {
                                        myVideoPortraitAdapter.hideHeader();
                                        isOver = true;
                                    }
                                }

                                if (myVideoPortraitAdapter == null) {
                                    if (myVideoLists.size() == 0) {
                                        textView_noData.setVisibility(View.VISIBLE);
                                    } else {
                                        textView_noData.setVisibility(View.GONE);
                                        myVideoPortraitAdapter = new MyVideoPortraitAdapter(getActivity(), myVideoLists, id, "my_video", interstitialAdView);
                                        recyclerView.setAdapter(myVideoPortraitAdapter);
                                        recyclerView.setLayoutAnimation(animation);
                                    }
                                } else {
                                    myVideoPortraitAdapter.notifyDataSetChanged();
                                }

                            }

                            progressBar.setVisibility(View.GONE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            isOver = true;
                            progressBar.setVisibility(View.GONE);
                            textView_noData.setVisibility(View.VISIBLE);
                            if (myVideoPortraitAdapter != null) {
                                myVideoPortraitAdapter.hideHeader();
                                isOver = true;
                                textView_noData.setVisibility(View.GONE);
                            }
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }

                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    progressBar.setVisibility(View.GONE);
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }

    }

}
