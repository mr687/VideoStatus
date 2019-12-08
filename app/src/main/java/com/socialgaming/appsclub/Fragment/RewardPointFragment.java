package com.socialgaming.appsclub.Fragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.socialgaming.appsclub.Activity.MainActivity;
import com.socialgaming.appsclub.Activity.RewardPointClaim;
import com.socialgaming.appsclub.Adapter.ViewpagerRewardAdapter;
import com.socialgaming.appsclub.R;
import com.socialgaming.appsclub.Util.API;
import com.socialgaming.appsclub.Util.Constant_Api;
import com.socialgaming.appsclub.Util.Events;
import com.socialgaming.appsclub.Util.GlobalBus;
import com.socialgaming.appsclub.Util.Method;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import cz.msebera.android.httpclient.Header;

public class RewardPointFragment extends Fragment {

    private Method method;
    private ProgressBar progressBar;
    private LinearLayout linearLayout;
    private String total_point = null;
    private TextView textView_menu_point_count, textView_point_menu;
    private TextView textView_point, textView_money, textView_information;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Button button;
    private AppBarLayout appbar;
    FragmentManager childFragMang;
    private String payment_withdraw = "false";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.reward_point_fragment, container, false);

        GlobalBus.getBus().register(this);

        childFragMang = getChildFragmentManager();

        payment_withdraw = getArguments().getString("payment_withdraw");
        Log.d("payment_withdraw", payment_withdraw);

        MainActivity.toolbar.setTitle(getResources().getString(R.string.reward_point));

        method = new Method(getActivity());

        progressBar = view.findViewById(R.id.progressbar_reward_point_fragment);
        linearLayout = view.findViewById(R.id.linearLayout_reward_point_fragment);
        textView_point = view.findViewById(R.id.textView_total_reward_point_fragment);
        textView_money = view.findViewById(R.id.textView_money_reward_point_fragment);
        textView_information = view.findViewById(R.id.textView_information_reward_point_fragment);
        tabLayout = view.findViewById(R.id.tablayout_reward_point_fragment);
        viewPager = view.findViewById(R.id.viewPager_reward_point_fragment);
        button = view.findViewById(R.id.button_reward_point_fragment);
        appbar = view.findViewById(R.id.appbar_reward_point_fragment);

        appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    isShow = true;
                    if (total_point != null) {
                        textView_menu_point_count.setVisibility(View.VISIBLE);
                        textView_point_menu.setVisibility(View.VISIBLE);
                    }
                } else if (isShow) {
                    isShow = false;
                    if (total_point != null) {
                        textView_menu_point_count.setVisibility(View.GONE);
                        textView_point_menu.setVisibility(View.GONE);
                    }
                }
            }
        });

        if (Constant_Api.aboutUsList != null) {
            String money = Constant_Api.aboutUsList.getRedeem_points()
                    + " " + getResources().getString(R.string.point)
                    + " " + getResources().getString(R.string.equal)
                    + " " + Constant_Api.aboutUsList.getRedeem_money()
                    + " " + Constant_Api.aboutUsList.getRedeem_currency();
            textView_money.setText(money);
        } else {
            textView_money.setVisibility(View.GONE);
        }

        textView_information.setVisibility(View.GONE);

        String[] tabName = {getResources().getString(R.string.current_point),
                getResources().getString(R.string.withdrawal_history)};

        for (int i = 0; i < 2; i++) {
            tabLayout.addTab(tabLayout.newTab().setText(tabName[i]));
        }

        callData();

        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.point_menu, menu);
        changeCart(menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void changeCart(Menu menu) {
        View cart = menu.findItem(R.id.action_point).getActionView();
        textView_menu_point_count = cart.findViewById(R.id.textView_menu_point_count_layout);
        textView_point_menu = cart.findViewById(R.id.textView_menu_point_layout);

        textView_menu_point_count.setVisibility(View.GONE);
        textView_point_menu.setVisibility(View.GONE);

        if (total_point != null) {
            if (textView_menu_point_count != null) {
                textView_menu_point_count.setText(total_point);
            }
        }

        textView_menu_point_count.setTypeface(textView_menu_point_count.getTypeface(), Typeface.BOLD);
    }

    private void user_data(String id) {

        progressBar.setVisibility(View.VISIBLE);

        if (getActivity() != null) {

            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("method_name", "user_profile");
            jsObj.addProperty("user_id", id);
            params.put("data", API.toBase64(jsObj.toString()));
            client.post(Constant_Api.url, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    if (getActivity() != null) {

                        Log.d("Response", new String(responseBody));
                        String res = new String(responseBody);

                        String user_id = null;

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
                                    user_id = object.getString("user_id");
                                    total_point = object.getString("total_point");

                                }

                                if (total_point != null) {
                                    textView_menu_point_count.setText(total_point);
                                } else {
                                    textView_menu_point_count.setText("");
                                }


                                //attach tab layout with ViewPager
                                //set gravity for tab bar
                                tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
                                tabLayout.setTabMode(TabLayout.MODE_FIXED);

                                //create and set ViewPager adapter
                                ViewpagerRewardAdapter viewpagerRewardAdapter = new ViewpagerRewardAdapter(childFragMang, tabLayout.getTabCount(), getActivity());
                                viewPager.setAdapter(viewpagerRewardAdapter);

                                if (payment_withdraw.equals("true")) {
                                    viewPager.setCurrentItem(1);
                                }

                                //change selected tab when viewpager changed page
                                viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

                                //change viewpager page when tab selected
                                tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                                    @Override
                                    public void onTabSelected(TabLayout.Tab tab) {
                                        viewPager.setCurrentItem(tab.getPosition());
                                    }

                                    @Override
                                    public void onTabUnselected(TabLayout.Tab tab) {

                                    }

                                    @Override
                                    public void onTabReselected(TabLayout.Tab tab) {

                                    }
                                });

                                textView_point.setText(total_point);

                                final String finalTotal_point = total_point;
                                final String finalUser_id = user_id;

                                assert total_point != null;
                                if (total_point.equals("0")) {
                                    button.setVisibility(View.GONE);
                                } else {
                                    button.setVisibility(View.VISIBLE);
                                }


                                button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (Constant_Api.aboutUsList != null) {

                                            int point = Integer.parseInt(Constant_Api.aboutUsList.getMinimum_redeem_points());
                                            int compair = Integer.parseInt(finalTotal_point);
                                            String minimum_point = getResources().getString(R.string.minimum)
                                                    + " " + Constant_Api.aboutUsList.getMinimum_redeem_points()
                                                    + " " + getResources().getString(R.string.point_require);

                                            if (compair >= point) {
                                                startActivity(new Intent(getActivity(), RewardPointClaim.class)
                                                        .putExtra("user_id", finalUser_id)
                                                        .putExtra("user_points", finalTotal_point));
                                            } else {
                                                method.alertBox(minimum_point);
                                            }

                                        } else {
                                            Toast.makeText(getActivity(), getResources().getString(R.string.wrong), Toast.LENGTH_SHORT).show();
                                        }


                                    }
                                });

                            }

                            progressBar.setVisibility(View.GONE);


                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                            textView_information.setVisibility(View.VISIBLE);
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }

                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    progressBar.setVisibility(View.GONE);
                    textView_information.setVisibility(View.VISIBLE);
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }

    }

    @Subscribe
    public void getReward(Events.RewardNotify rewardNotify) {
        callData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Unregister the registered event.
        GlobalBus.getBus().unregister(this);
    }

    public void callData() {

        if (Method.isNetworkAvailable(getActivity())) {
            if (method.pref.getBoolean(method.pref_login, false)) {
                user_data(method.pref.getString(method.profileId, null));
            } else {
                linearLayout.setVisibility(View.GONE);
                textView_information.setVisibility(View.VISIBLE);
                textView_information.setText(getResources().getString(R.string.you_have_not_login));
                progressBar.setVisibility(View.GONE);
            }
        } else {
            linearLayout.setVisibility(View.GONE);
            textView_information.setVisibility(View.VISIBLE);
            textView_information.setText(getResources().getString(R.string.no_data_found));
            progressBar.setVisibility(View.GONE);
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

    }

}
