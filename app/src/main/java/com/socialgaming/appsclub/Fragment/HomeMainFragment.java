package com.socialgaming.appsclub.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.socialgaming.appsclub.Activity.MainActivity;
import com.socialgaming.appsclub.Adapter.HomeCategoryAdapter;
import com.socialgaming.appsclub.InterFace.InterstitialAdView;
import com.socialgaming.appsclub.Item.CategoryList;
import com.socialgaming.appsclub.R;
import com.socialgaming.appsclub.Util.API;
import com.socialgaming.appsclub.Util.Constant_Api;
import com.socialgaming.appsclub.Util.Method;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cz.msebera.android.httpclient.Header;

public class HomeMainFragment extends Fragment {

    private Method method;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private TextView textView_noData;
    private LinearLayout linearLayout;
    private List<CategoryList> categoryLists;
    private List<CategoryList> setCategoryLists;
    private HomeCategoryAdapter homeCategoryAdapter;
    private InterstitialAdView interstitialAdView;
    static FloatingActionButton floating_home;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.home_main_fragment, container, false);

        MainActivity.toolbar.setTitle(getResources().getString(R.string.home));

        categoryLists = new ArrayList<>();
        setCategoryLists = new ArrayList<>();

        interstitialAdView = new InterstitialAdView() {
            @Override
            public void position(int position, String type, String id) {
                if (position == setCategoryLists.size() - 1) {
                    CategoryFragment categoryFragment = new CategoryFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "home_category");
                    categoryFragment.setArguments(bundle);
                    FragmentManager fragmentManager = getFragmentManager();
                    assert fragmentManager != null;
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frameLayout_main, categoryFragment, getResources().getString(R.string.category)).commitAllowingStateLoss();
                } else {

                    linearLayout.setVisibility(View.VISIBLE);
                    if (linearLayout.getChildCount() == 0) {
                        if (method.personalization_ad) {
                            method.showPersonalizedAds(linearLayout);
                        } else {
                            method.showNonPersonalizedAds(linearLayout);
                        }
                    }

                    Method.search_title = type;
                    SubCategoryFragment subCategoryFragment = new SubCategoryFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("id", setCategoryLists.get(position).getCid());
                    bundle.putString("category_name", setCategoryLists.get(position).getCategory_name());
                    bundle.putString("type", "home_category");
                    bundle.putString("typeLayout", "Landscape");
                    subCategoryFragment.setArguments(bundle);
                    getChildFragmentManager().beginTransaction().replace(R.id.frameLayout_home_main, subCategoryFragment, setCategoryLists.get(position).getCategory_name()).commitAllowingStateLoss();
                }
            }
        };
        method = new Method(getActivity(), interstitialAdView);

        floating_home = view.findViewById(R.id.fab_home_main);
        floating_home.setVisibility(View.GONE);

        linearLayout = view.findViewById(R.id.linearLayout_home_main);

        textView_noData = view.findViewById(R.id.textView_homeMain_fragment);
        progressBar = view.findViewById(R.id.progressBar_home_main_fragment);
        recyclerView = view.findViewById(R.id.recyclerView_home_main_fragment);

        textView_noData.setVisibility(View.GONE);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayout.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);

        if (Method.isNetworkAvailable(getActivity())) {
            home_category();
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
            textView_noData.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }

        return view;
    }

    private void home_category() {

        categoryLists.clear();
        setCategoryLists.clear();
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

                                int size = 0;
                                if (categoryLists.size() >= Constant_Api.CATEGORY_SHOW) {
                                    size = Constant_Api.CATEGORY_SHOW;
                                    categoryLists.add(Constant_Api.CATEGORY_SHOW - 1, new CategoryList("", getResources().getString(R.string.view_all), "no", "no", ""));
                                } else {
                                    size = categoryLists.size() + 1;
                                    categoryLists.add(categoryLists.size(), new CategoryList("", getResources().getString(R.string.view_all), "no", "no", ""));
                                }

                                for (int i = 0; i < size; i++) {
                                    setCategoryLists.add(categoryLists.get(i));
                                }

                                homeCategoryAdapter = new HomeCategoryAdapter(getActivity(), setCategoryLists, "", interstitialAdView);
                                recyclerView.setAdapter(homeCategoryAdapter);

                                getChildFragmentManager().beginTransaction().replace(R.id.frameLayout_home_main, new HomeFragment(), getResources().getString(R.string.home)).commitAllowingStateLoss();

                                linearLayout.setVisibility(View.GONE);

                                progressBar.setVisibility(View.GONE);
                                textView_noData.setVisibility(View.GONE);

                            }

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
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }

    }

}
