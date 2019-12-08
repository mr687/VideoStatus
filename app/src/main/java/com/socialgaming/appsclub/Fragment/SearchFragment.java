package com.socialgaming.appsclub.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.socialgaming.appsclub.Activity.MainActivity;
import com.socialgaming.appsclub.Adapter.SubCategoryAdapter;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cz.msebera.android.httpclient.Header;

public class SearchFragment extends Fragment {

    private Method method;
    private String search, typeLayout;
    private ProgressBar progressBar;
    private TextView textView_noData;
    private RecyclerView recyclerView;
    private SubCategoryAdapter subCategoryAdapter;
    private List<SubCategoryList> searchLists;
    private InterstitialAdView interstitialAdView;
    private LayoutAnimationController animation;
    private Boolean isOver = false;
    private int pagination_index = 1;
    private FloatingActionButton floatingActionButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.sub_cat_fragment, container, false);

        GlobalBus.getBus().register(this);

        searchLists = new ArrayList<>();

        interstitialAdView = new InterstitialAdView() {
            @Override
            public void position(int position, String type, String id) {
                SCDetailFragment scDetailFragment = new SCDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putString("id", searchLists.get(position).getId());
                bundle.putString("type", type);
                bundle.putInt("position", position);
                scDetailFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, scDetailFragment, searchLists.get(position).getVideo_title()).addToBackStack(searchLists.get(position).getVideo_title()).commitAllowingStateLoss();
            }
        };
        method = new Method(getActivity(), interstitialAdView);

        search = getArguments().getString("search");
        typeLayout = getArguments().getString("typeLayout");
        MainActivity.toolbar.setTitle(search);

        int resId = R.anim.layout_animation_fall_down;
        animation = AnimationUtils.loadLayoutAnimation(getActivity(), resId);

        floatingActionButton = view.findViewById(R.id.fab_sub_category);
        progressBar = view.findViewById(R.id.progressbar_sub_category);
        textView_noData = view.findViewById(R.id.textView_sub_category);

        floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.portrait_ic));

        recyclerView = view.findViewById(R.id.recyclerView_sub_category);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
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
                    subCategoryAdapter.hideHeader();
                }
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchPortraitFragment searchPortraitFragment = new SearchPortraitFragment();
                Bundle bundle = new Bundle();
                bundle.putString("search", search);
                bundle.putString("typeLayout", "Portrait");
                searchPortraitFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, searchPortraitFragment, search).commitAllowingStateLoss();
            }
        });

        callData();

        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        //inflater.inflate(R.menu.profile_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Subscribe
    public void getNotify(Events.FavouriteNotify favouriteNotify) {
        for (int i = 0; i < searchLists.size(); i++) {
            if (searchLists.get(i).getId().equals(favouriteNotify.getId())) {
                subCategoryAdapter.notifyItemChanged(i);
            }
        }
    }

    @Subscribe
    public void getMessage(Events.SearchFragmentNotify searchFragmentNotify) {
        String type = searchFragmentNotify.getType();
        int position = searchFragmentNotify.getPosition();
        switch (type) {
            case "like":
                searchLists.get(position).setTotal_likes(searchFragmentNotify.getSearch_TotalLike());
                searchLists.get(position).setAlready_like(searchFragmentNotify.getSearch_alreadyLike());
                break;
            case "view":
                searchLists.get(position).setTotal_viewer(searchFragmentNotify.getSearch_View());
                break;
            default:
                searchLists.get(position).setTotal_likes(searchFragmentNotify.getSearch_TotalLike());
                searchLists.get(position).setAlready_like(searchFragmentNotify.getSearch_alreadyLike());
                searchLists.get(position).setTotal_viewer(searchFragmentNotify.getSearch_View());
                break;
        }
        if (subCategoryAdapter != null) {
            subCategoryAdapter.notifyItemChanged(position);
        }
    }

    private void callData() {
        if (getActivity() != null) {
            if (Method.isNetworkAvailable(getActivity())) {
                if (method.pref.getBoolean(method.pref_login, false)) {
                    subCategory(method.pref.getString(method.profileId, null));
                } else {
                    subCategory("0");
                }
            } else {
                method.alertBox(getResources().getString(R.string.internet_connection));
            }
        }
    }

    private void subCategory(String userId) {

        if (subCategoryAdapter == null) {
            searchLists.clear();
            progressBar.setVisibility(View.VISIBLE);
        }

        if (getActivity() != null) {

            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("method_name", "search_video");
            jsObj.addProperty("search_text", search);
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
                                if(status.equals("-2")){
                                    method.suspend(message);
                                }else {
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

                                    searchLists.add(new SubCategoryList("", id, cat_id, video_title, video_url, video_layout, video_thumbnail_b, video_thumbnail_s, total_viewer, total_likes, category_name, already_like));

                                }

                                if (jsonArray.length() == 0) {
                                    if (subCategoryAdapter != null) {
                                        isOver = true;
                                        subCategoryAdapter.hideHeader();
                                    }
                                }

                                if (subCategoryAdapter == null) {
                                    if (searchLists.size() == 0) {
                                        textView_noData.setVisibility(View.VISIBLE);
                                    } else {
                                        textView_noData.setVisibility(View.GONE);
                                        subCategoryAdapter = new SubCategoryAdapter(getActivity(), searchLists, interstitialAdView, "search");
                                        recyclerView.setAdapter(subCategoryAdapter);
                                        recyclerView.setLayoutAnimation(animation);
                                    }
                                } else {
                                    subCategoryAdapter.notifyDataSetChanged();
                                }

                            }

                            progressBar.setVisibility(View.GONE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            isOver = true;
                            progressBar.setVisibility(View.GONE);
                            textView_noData.setVisibility(View.VISIBLE);
                            if (subCategoryAdapter != null) {
                                isOver = true;
                                subCategoryAdapter.hideHeader();
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
