package com.socialgaming.appsclub.Adapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.socialgaming.appsclub.DataBase.DatabaseHandler;
import com.socialgaming.appsclub.InterFace.InterstitialAdView;
import com.socialgaming.appsclub.Item.SubCategoryList;
import com.socialgaming.appsclub.R;
import com.socialgaming.appsclub.Util.Method;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import static com.bumptech.glide.load.DecodeFormat.PREFER_ARGB_8888;
import static com.bumptech.glide.load.engine.DiskCacheStrategy.ALL;


public class PortraitHomeAdapter extends RecyclerView.Adapter<PortraitHomeAdapter.ViewHolder> {

    private Method method;
    private Activity activity;
    private DatabaseHandler db;
    private String type;
    private List<SubCategoryList> portraitLists;

    public PortraitHomeAdapter(Activity activity, List<SubCategoryList> portraitLists, InterstitialAdView interstitialAdView, String type) {
        this.activity = activity;
        method = new Method(activity, interstitialAdView);
        db = new DatabaseHandler(activity);
        this.portraitLists = portraitLists;
        this.type = type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.portrait_home_adapter, parent, false);

        return new PortraitHomeAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        Glide.with(activity)
                .load(portraitLists.get(position).getVideo_thumbnail_s())
                .apply(RequestOptions.diskCacheStrategyOf(ALL))
                .override(150,250)
                .centerInside()
                .format(PREFER_ARGB_8888)
                .placeholder(R.drawable.placeholder_portable)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, com.bumptech.glide.request.target.Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, com.bumptech.glide.request.target.Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                method.interstitialAdShow(position, type, portraitLists.get(position).getId());
            }
        });



    }


    @Override
    public int getItemCount() {
        return portraitLists.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout relativeLayout;
        private RoundedImageView imageView;
        private ImageView imageView_favourite;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView_portrait_home_adapter);

        }
    }
}
