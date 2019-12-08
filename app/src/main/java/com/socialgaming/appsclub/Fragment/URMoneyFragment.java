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

import com.socialgaming.appsclub.Adapter.UserRMAdapter;
import com.socialgaming.appsclub.InterFace.InterstitialAdView;
import com.socialgaming.appsclub.Item.UserRMList;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cz.msebera.android.httpclient.Header;


public class URMoneyFragment extends Fragment {

    private Method method;
    private ProgressBar progressBar;
    private TextView textView_noData;
    private RecyclerView recyclerView;
    private UserRMAdapter userRMAdapter;
    private List<UserRMList> userRMLists;
    private InterstitialAdView interstitialAdView;
    private LayoutAnimationController animation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.user_rm_fragment, container, false);

        interstitialAdView = new InterstitialAdView() {
            @Override
            public void position(int position, String type, String id) {
                if (type.equals(getResources().getString(R.string.point_status))) {
                    TDFragment tdFragment = new TDFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("redeem_id", id);
                    tdFragment.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, tdFragment, type).addToBackStack(type).commitAllowingStateLoss();
                } else {
                    URHistoryFragment urHistoryFragment = new URHistoryFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("redeem_id", id);
                    urHistoryFragment.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, urHistoryFragment, type).addToBackStack(type).commitAllowingStateLoss();
                }
            }
        };
        method = new Method(getActivity(), interstitialAdView);

        userRMLists = new ArrayList<>();

        int resId = R.anim.layout_animation_fall_down;
        animation = AnimationUtils.loadLayoutAnimation(getActivity(), resId);

        progressBar = view.findViewById(R.id.progressbar_user_rm_fragment);
        textView_noData = view.findViewById(R.id.textView_user_rm_fragment);
        recyclerView = view.findViewById(R.id.recyclerView_user_rm_fragment);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        textView_noData.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        if (Method.isNetworkAvailable(getActivity())) {
            if (method.pref.getBoolean(method.pref_login, false)) {
                history(method.pref.getString(method.profileId, null));
            } else {
                textView_noData.setVisibility(View.VISIBLE);
                textView_noData.setText(getResources().getString(R.string.you_have_not_login));
                recyclerView.setVisibility(View.GONE);
            }
        } else {
            textView_noData.setVisibility(View.VISIBLE);
            textView_noData.setText(getResources().getString(R.string.no_data_found));
            recyclerView.setVisibility(View.GONE);
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

        return view;

    }

    private void history(final String id) {

        userRMLists.clear();
        progressBar.setVisibility(View.VISIBLE);

        if (getActivity() != null) {

            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("method_name", "user_redeem_history");
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
                                if (status.equals("-2")) {
                                    method.suspend(message);
                                } else {
                                    method.alertBox(message);
                                }

                            } else {

                                JSONArray jsonArray = jsonObject.getJSONArray(Constant_Api.tag);

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String redeem_id = object.getString("redeem_id");
                                    String user_points = object.getString("user_points");
                                    String redeem_price = object.getString("redeem_price");
                                    String request_date = object.getString("request_date");
                                    String status = object.getString("status");

                                    userRMLists.add(new UserRMList(redeem_id, user_points, redeem_price, request_date, status));
                                }

                                if (userRMLists.size() == 0) {
                                    textView_noData.setVisibility(View.VISIBLE);
                                } else {
                                    textView_noData.setVisibility(View.GONE);
                                    userRMAdapter = new UserRMAdapter(getActivity(), userRMLists, interstitialAdView, "");
                                    recyclerView.setAdapter(userRMAdapter);
                                    recyclerView.setLayoutAnimation(animation);
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

}
