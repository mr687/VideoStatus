package com.socialgaming.appsclub.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.socialgaming.appsclub.Adapter.UserFollowAdapter;
import com.socialgaming.appsclub.InterFace.InterstitialAdView;
import com.socialgaming.appsclub.Item.UserFollowList;
import com.socialgaming.appsclub.R;
import com.socialgaming.appsclub.Util.Method;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class UserFollowFragment extends Fragment {

    private Method method;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.category_fragment, container, false);

        List<UserFollowList> userFollowLists = new ArrayList<>();

        InterstitialAdView interstitialAdView = new InterstitialAdView() {
            @Override
            public void position(int position, String type, String id) {
                if (getActivity() != null) {
                    ProfileFragment profileFragment = new ProfileFragment();
                    Bundle bundle_profile = new Bundle();
                    bundle_profile.putString("type", "other_user");
                    bundle_profile.putString("id", id);
                    profileFragment.setArguments(bundle_profile);
                    getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, profileFragment, getResources().getString(R.string.profile)).addToBackStack(getResources().getString(R.string.profile)).commitAllowingStateLoss();
                } else {
                    method.alertBox(getResources().getString(R.string.wrong));
                }
            }
        };
        method = new Method(getActivity(), interstitialAdView);

        assert getArguments() != null;
        String type = getArguments().getString("type");
        userFollowLists = (List<UserFollowList>) getArguments().getSerializable("array");

        int resId = R.anim.layout_animation_fall_down;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getActivity(), resId);

        ProgressBar progressBar = view.findViewById(R.id.progressbar_category);
        TextView textView_noData_found = view.findViewById(R.id.textView_category);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView_category);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        progressBar.setVisibility(View.GONE);
        textView_noData_found.setVisibility(View.GONE);
        UserFollowAdapter userFollowAdapter = new UserFollowAdapter(getActivity(), userFollowLists, interstitialAdView);
        recyclerView.setAdapter(userFollowAdapter);
        recyclerView.setLayoutAnimation(animation);

        setHasOptionsMenu(true);
        return view;
    }


}
