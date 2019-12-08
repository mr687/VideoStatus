package com.socialgaming.appsclub.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.socialgaming.appsclub.InterFace.InterstitialAdView;
import com.socialgaming.appsclub.R;
import com.socialgaming.appsclub.Util.Method;
import com.socialgaming.appsclub.Util.TouchImageView;

import java.io.File;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class StatusDetailAdapter extends PagerAdapter {

    private Method method;
    private Activity activity;
    private String type;
    private List<File> fileList;
    private LayoutInflater layoutInflater;

    public StatusDetailAdapter(Activity activity, List<File> fileList, InterstitialAdView interstitialAdView, String type) {
        this.activity = activity;
        this.fileList = fileList;
        this.type = type;
        method = new Method(activity, interstitialAdView);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {

        layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.status_detail_adapter, container, false);

        TouchImageView imageView = view.findViewById(R.id.imageView_status_detail_adapter);
        ImageView imageView_play = view.findViewById(R.id.imageView_play_status_detail_adapter);
        Glide.with(activity).load("file://" + fileList.get(position).toString())
                .placeholder(R.drawable.placeholder_portable)
                .into(imageView);

        if (type.equals("image")) {
            imageView_play.setVisibility(View.GONE);
        } else {
            imageView_play.setVisibility(View.VISIBLE);
        }

        imageView_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                method.interstitialAdShow(position, "", "");
            }
        });

        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return fileList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object obj) {
        return view == obj;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

}
