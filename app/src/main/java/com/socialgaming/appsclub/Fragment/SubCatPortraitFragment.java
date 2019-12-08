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

import com.socialgaming.appsclub.Activity.MainActivity;
import com.socialgaming.appsclub.Adapter.PortraitAdapter;
import com.socialgaming.appsclub.InterFace.InterstitialAdView;
import com.socialgaming.appsclub.Item.SubCategoryList;
import com.socialgaming.appsclub.R;
import com.socialgaming.appsclub.Util.API;
import com.socialgaming.appsclub.Util.Constant_Api;
import com.socialgaming.appsclub.Util.EndlessRecyclerViewScrollListener;
import com.socialgaming.appsclub.Util.Events;
import com.socialgaming.appsclub.Util.GlobalBus;
import com.socialgaming.appsclub.Util.Method;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cz.msebera.android.httpclient.Header;

public class SubCatPortraitFragment extends Fragment {

    private Method method;
    private String type, id, category_name, typeLayout;
    private ProgressBar progressBar;
    private TextView textView_noData;
    private RecyclerView recyclerView;
    private List<SubCategoryList> portraitLists;
    private PortraitAdapter portraitAdapter;
    private Boolean isOver = false;
    private int pagination_index = 1;
    private LayoutAnimationController animation;
    private InterstitialAdView interstitialAdView;
    private FloatingActionButton floatingActionButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.sub_cat_fragment, container, false);

        GlobalBus.getBus().register(this);

        portraitLists = new ArrayList<>();

        interstitialAdView = new InterstitialAdView() {
            @Override
            public void position(int position, String type, String id) {
                String tag = portraitLists.get(position).getVideo_title();
                SCDetailFragment scDetailFragment = new SCDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putString("id", id);
                bundle.putString("type", type);
                bundle.putInt("position", position);
                scDetailFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, scDetailFragment, tag).addToBackStack(tag).commitAllowingStateLoss();
            }
        };
        method = new Method(getActivity(), interstitialAdView);

        assert getArguments() != null;
        type = getArguments().getString("type");
        id = getArguments().getString("id");
        category_name = getArguments().getString("category_name");
        typeLayout = getArguments().getString("typeLayout");

        int resId = R.anim.layout_animation_fall_down;
        animation = AnimationUtils.loadLayoutAnimation(getActivity(), resId);

        floatingActionButton = view.findViewById(R.id.fab_sub_category);
        progressBar = view.findViewById(R.id.progressbar_sub_category);
        textView_noData = view.findViewById(R.id.textView_sub_category);

        floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.landscape_ic));
        if (type.equals("category")) {
            MainActivity.toolbar.setTitle(category_name);
        } else {
            HomeMainFragment.floating_home.setVisibility(View.VISIBLE);
            HomeMainFragment.floating_home.setImageDrawable(getResources().getDrawable(R.drawable.landscape_ic));
            floatingActionButton.setVisibility(View.GONE);
        }

        recyclerView = view.findViewById(R.id.recyclerView_sub_category);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (portraitAdapter.getItemViewType(position)) {
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

        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (!isOver) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pagination_index++;
                            callData(typeLayout);
                        }
                    }, 1000);
                } else {
                    portraitAdapter.hideHeader();
                }
            }
        });

        HomeMainFragment.floating_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubCategoryFragment subCategoryFragment = new SubCategoryFragment();
                Bundle bundle = new Bundle();
                bundle.putString("id", id);
                bundle.putString("category_name", category_name);
                bundle.putString("type", type);
                bundle.putString("typeLayout", "Landscape");
                subCategoryFragment.setArguments(bundle);
                FragmentManager fragmentManager = getFragmentManager();
                assert fragmentManager != null;
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout_home_main, subCategoryFragment, category_name).commitAllowingStateLoss();
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().getSupportFragmentManager().popBackStack();

                SubCategoryFragment subCategoryFragment = new SubCategoryFragment();
                Bundle bundle = new Bundle();
                bundle.putString("id", id);
                bundle.putString("category_name", category_name);
                bundle.putString("type", type);
                bundle.putString("typeLayout", "Landscape");
                subCategoryFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, subCategoryFragment, category_name).addToBackStack("sub").commitAllowingStateLoss();

            }
        });

        callData(typeLayout);

        return view;
    }

    @Subscribe
    public void getNotify(Events.FavouriteNotify favouriteNotify) {
        for (int i = 0; i < portraitLists.size(); i++) {
            if (portraitLists.get(i).getId().equals(favouriteNotify.getId())) {
                portraitAdapter.notifyItemChanged(i);
            }
        }
    }

    private void callData(String typLayout) {
        if (getActivity() != null) {
            if (Method.isNetworkAvailable(getActivity())) {
                if (method.pref.getBoolean(method.pref_login, false)) {
                    data(method.pref.getString(method.profileId, null), typLayout);
                } else {
                    data("0", typLayout);
                }
            } else {
                method.alertBox(getResources().getString(R.string.internet_connection));
            }
        }

    }

    private void data(String userId, String typeLayout) {

        if (portraitAdapter == null) {
            portraitLists.clear();
            progressBar.setVisibility(View.VISIBLE);
        }

        if (getActivity() != null) {

            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("method_name", "video_by_cat_id");
            jsObj.addProperty("cat_id", id);
            jsObj.addProperty("user_id", userId);
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
                                    String cat_id = object.getString("cat_id");
                                    String video_title = object.getString("video_title");
                                    String video_url = object.getString("video_url");
                                    String video_layout = object.getString("video_layout");
                                    String video_thumbnail_b = object.getString("video_thumbnail_b");
                                    String video_thumbnail_s = object.getString("video_thumbnail_s");
                                    String total_likes = object.getString("total_likes");
                                    String total_viewer = object.getString("totel_viewer");
                                    String category_name = object.getString("category_name");
                                    String already_like = object.getString("already_like");

                                    portraitLists.add(new SubCategoryList("", id, cat_id, video_title, video_url, video_layout, video_thumbnail_b, video_thumbnail_s, total_viewer, total_likes, category_name, already_like));

                                }

                                if (jsonArray.length() == 0) {
                                    if (portraitAdapter != null) {
                                        isOver = true;
                                        portraitAdapter.hideHeader();
                                    }
                                }

                                if (portraitAdapter == null) {
                                    if (portraitLists.size() == 0) {
                                        textView_noData.setVisibility(View.VISIBLE);
                                    } else {
                                        textView_noData.setVisibility(View.GONE);
                                        portraitAdapter = new PortraitAdapter(getActivity(), portraitLists, interstitialAdView, "sub_category");
                                        recyclerView.setAdapter(portraitAdapter);
                                        recyclerView.setLayoutAnimation(animation);
                                    }
                                } else {
                                    portraitAdapter.notifyDataSetChanged();
                                }

                            }

                            progressBar.setVisibility(View.GONE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            isOver = true;
                            progressBar.setVisibility(View.GONE);
                            textView_noData.setVisibility(View.VISIBLE);
                            if (portraitAdapter != null) {
                                isOver = true;
                                portraitAdapter.hideHeader();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Unregister the registered event.
        GlobalBus.getBus().unregister(this);
    }

}
