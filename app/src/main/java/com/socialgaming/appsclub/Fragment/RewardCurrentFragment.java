package com.socialgaming.appsclub.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.socialgaming.appsclub.Adapter.RewardPointAdapter;
import com.socialgaming.appsclub.InterFace.InterstitialAdView;
import com.socialgaming.appsclub.Item.RewardPointList;
import com.socialgaming.appsclub.R;
import com.socialgaming.appsclub.Util.API;
import com.socialgaming.appsclub.Util.Constant_Api;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cz.msebera.android.httpclient.Header;

public class RewardCurrentFragment extends Fragment {

    private Method method;
    private ProgressBar progressBar;
    private TextView textView_noData;
    private RecyclerView recyclerView;
    private RewardPointAdapter rewardPointAdapter;
    private InterstitialAdView interstitialAdView;
    private List<RewardPointList> rewardPointLists;
    private LayoutAnimationController layoutAnimationController;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.user_rm_fragment, container, false);

        rewardPointLists = new ArrayList<>();

        interstitialAdView = new InterstitialAdView() {
            @Override
            public void position(int position, String type, String id) {

            }
        };
        method = new Method(getActivity(), interstitialAdView);

        int resId = R.anim.layout_animation_fall_down;
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getActivity(), resId);

        progressBar = view.findViewById(R.id.progressbar_user_rm_fragment);
        textView_noData = view.findViewById(R.id.textView_user_rm_fragment);
        recyclerView = view.findViewById(R.id.recyclerView_user_rm_fragment);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        callData();

        return view;
    }

    private void rewardPoint(String id) {

        rewardPointLists.clear();
        progressBar.setVisibility(View.VISIBLE);

        if (getActivity() != null) {

            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("method_name", "user_rewads_point");
            jsObj.addProperty("user_id", id);
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
                                if(status.equals("-2")){
                                    method.suspend(message);
                                }else {
                                    method.alertBox(message);
                                }

                            } else {

                                JSONArray jsonArray = jsonObject.getJSONArray(Constant_Api.tag);

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject object = jsonArray.getJSONObject(i);

                                    JSONArray jsonArrayReward = object.getJSONArray("user_rewads_point");

                                    for (int j = 0; j < jsonArrayReward.length(); j++) {

                                        JSONObject object_reward = jsonArrayReward.getJSONObject(j);
                                        String video_id = object_reward.getString("video_id");
                                        String video_title = object_reward.getString("video_title");
                                        String video_thumbnail = object_reward.getString("video_thumbnail");
                                        String user_id = object_reward.getString("user_id");
                                        String activity_type = object_reward.getString("activity_type");
                                        String points = object_reward.getString("points");
                                        String date = object_reward.getString("date");
                                        String time = object_reward.getString("time");

                                        rewardPointLists.add(new RewardPointList(video_id, video_title, video_thumbnail, user_id, activity_type, points, date, time));

                                    }
                                }

                                if (rewardPointLists.size() == 0) {
                                    textView_noData.setVisibility(View.VISIBLE);
                                } else {
                                    textView_noData.setVisibility(View.GONE);
                                    rewardPointAdapter = new RewardPointAdapter(getActivity(), rewardPointLists);
                                    recyclerView.setAdapter(rewardPointAdapter);
                                    recyclerView.setLayoutAnimation(layoutAnimationController);
                                }

                            }

                            progressBar.setVisibility(View.GONE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                            textView_noData.setVisibility(View.VISIBLE);
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }

                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    progressBar.setVisibility(View.GONE);
                    textView_noData.setVisibility(View.VISIBLE);
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }

    }

    private void callData() {
        if (Method.isNetworkAvailable(getActivity())) {
            if (method.pref.getBoolean(method.pref_login, false)) {
                rewardPoint(method.pref.getString(method.profileId, null));
                textView_noData.setVisibility(View.GONE);
            } else {
                textView_noData.setVisibility(View.VISIBLE);
                textView_noData.setText(getResources().getString(R.string.you_have_not_login));
                recyclerView.setVisibility(View.GONE);
            }
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
            textView_noData.setVisibility(View.VISIBLE);
            textView_noData.setText(getResources().getString(R.string.no_data_found));
            recyclerView.setVisibility(View.GONE);
        }

    }

}
