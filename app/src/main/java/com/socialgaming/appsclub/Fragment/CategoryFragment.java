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

import com.socialgaming.appsclub.Activity.MainActivity;
import com.socialgaming.appsclub.Adapter.CategoryAdapter;
import com.socialgaming.appsclub.InterFace.InterstitialAdView;
import com.socialgaming.appsclub.Item.CategoryList;
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

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cz.msebera.android.httpclient.Header;

public class CategoryFragment extends Fragment {

    private Method method;
    private String type;
    private ProgressBar progressBar;
    private TextView textView_noData;
    private RecyclerView recyclerView;
    private CategoryAdapter categoryAdapter;
    private List<CategoryList> categoryLists;
    private InterstitialAdView interstitialAdView;
    private LayoutAnimationController animation;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.category_fragment, container, false);

        categoryLists = new ArrayList<>();

        HomeMainFragment.floating_home.setVisibility(View.GONE);

        assert getArguments() != null;
        type = getArguments().getString("type");
        assert type != null;
        MainActivity.toolbar.setTitle(getResources().getString(R.string.category));

        if (type.equals("home_category")) {
            Events.Select select = new Events.Select("");
            GlobalBus.getBus().post(select);
        }

        interstitialAdView = new InterstitialAdView() {
            @Override
            public void position(int position, String type, String id) {
                Method.search_title = type;
                SubCategoryFragment subCategoryFragment = new SubCategoryFragment();
                Bundle bundle = new Bundle();
                bundle.putString("id", categoryLists.get(position).getCid());
                bundle.putString("category_name", categoryLists.get(position).getCategory_name());
                bundle.putString("type", "category");
                bundle.putString("typeLayout", "Landscape");
                subCategoryFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, subCategoryFragment, categoryLists.get(position).getCategory_name()).addToBackStack("sub").commitAllowingStateLoss();
            }
        };
        method = new Method(getActivity(), interstitialAdView);

        int resId = R.anim.layout_animation_fall_down;
        animation = AnimationUtils.loadLayoutAnimation(getActivity(), resId);

        progressBar = view.findViewById(R.id.progressbar_category);
        textView_noData = view.findViewById(R.id.textView_category);
        recyclerView = view.findViewById(R.id.recyclerView_category);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(layoutManager);

        if (Method.isNetworkAvailable(getActivity())) {
            Category();
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
            progressBar.setVisibility(View.GONE);
        }

        return view;
    }


    private void Category() {

        categoryLists.clear();
        progressBar.setVisibility(View.VISIBLE);

        if (getActivity() != null) {

            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("method_name", "cat_list");
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
                                    String cid = object.getString("cid");
                                    String category_name = object.getString("category_name");
                                    String category_image = object.getString("category_image");
                                    String category_image_thumb = object.getString("category_image_thumb");
                                    String cat_total_video = object.getString("cat_total_video");

                                    categoryLists.add(new CategoryList(cid, category_name, category_image, category_image_thumb, cat_total_video));
                                }

                                if (categoryLists.size() == 0) {
                                    textView_noData.setVisibility(View.VISIBLE);
                                } else {
                                    textView_noData.setVisibility(View.GONE);
                                    categoryAdapter = new CategoryAdapter(getActivity(), categoryLists, "", interstitialAdView);
                                    recyclerView.setAdapter(categoryAdapter);
                                    recyclerView.setLayoutAnimation(animation);
                                }

                            }

                            progressBar.setVisibility(View.GONE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);
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
