package com.socialgaming.appsclub.Adapter;

import android.app.Activity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.socialgaming.appsclub.Fragment.ImageSSFragment;
import com.socialgaming.appsclub.Fragment.VideoSSFragment;

public class VPAdapterSS extends FragmentPagerAdapter {

    private int Favourite_NumOfTabs;
    private String type;
    public Activity activity;

    public VPAdapterSS(FragmentManager fm, int favourite_NumOfTabs, Activity activity, String type) {
        super(fm);
        Favourite_NumOfTabs = favourite_NumOfTabs;
        this.activity = activity;
        this.type = type;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {

            case 0:
                ImageSSFragment imageSSFragment = new ImageSSFragment();
                Bundle bundle = new Bundle();
                bundle.putString("type", type);
                imageSSFragment.setArguments(bundle);
                return imageSSFragment;

            case 1:
                VideoSSFragment videoSSFragment = new VideoSSFragment();
                Bundle bundle_video = new Bundle();
                bundle_video.putString("type", type);
                videoSSFragment.setArguments(bundle_video);
                return videoSSFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return Favourite_NumOfTabs;
    }

}
