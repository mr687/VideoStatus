package com.socialgaming.appsclub.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.socialgaming.appsclub.DataBase.DatabaseHandler;
import com.socialgaming.appsclub.InterFace.InterstitialAdView;
import com.socialgaming.appsclub.Item.SubCategoryList;
import com.socialgaming.appsclub.R;
import com.socialgaming.appsclub.Util.Events;
import com.socialgaming.appsclub.Util.GlobalBus;
import com.socialgaming.appsclub.Util.Method;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class FavPortraitAdapter extends RecyclerView.Adapter<FavPortraitAdapter.ViewHolder> {

    private Activity activity;
    private Method method;
    private DatabaseHandler db;
    private String type;
    private List<SubCategoryList> subCategoryLists;

    public FavPortraitAdapter(Activity activity, List<SubCategoryList> subCategoryLists, InterstitialAdView interstitialAdView, String type) {
        this.activity = activity;
        method = new Method(activity, interstitialAdView);
        db = new DatabaseHandler(activity);
        this.subCategoryLists = subCategoryLists;
        this.type = type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.portrait_adapter, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        if (db.checkId_Fav(subCategoryLists.get(position).getId())) {
            holder.imageView_favourite.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_fav));
        } else {
            holder.imageView_favourite.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_fav_hov));
        }

        Glide.with(activity).load(subCategoryLists.get(position).getVideo_thumbnail_s())
                .placeholder(R.drawable.placeholder_portable).into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                method.interstitialAdShow(position, type, subCategoryLists.get(position).getId());
            }
        });

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (db.checkId_Fav(subCategoryLists.get(position).getId())) {
                    method.addToFav(db, subCategoryLists, position);
                    holder.imageView_favourite.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_fav_hov));
                } else {
                    db.deleteFav(subCategoryLists.get(position).getId());
                    holder.imageView_favourite.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_fav));
                }
                Events.FavouriteNotify homeNotify = new Events.FavouriteNotify(subCategoryLists.get(position).getId(), subCategoryLists.get(position).getVideo_layout());
                GlobalBus.getBus().post(homeNotify);

            }
        });

    }

    @Override
    public int getItemCount() {
        return subCategoryLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout relativeLayout;
        private ImageView imageView, imageView_favourite;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView_portrait_adapter);

        }
    }
}
