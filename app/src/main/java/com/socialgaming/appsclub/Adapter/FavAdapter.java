package com.socialgaming.appsclub.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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


public class FavAdapter extends RecyclerView.Adapter<FavAdapter.ViewHolder> {

    private Activity activity;
    private Method method;
    private DatabaseHandler db;
    private int columnWidth;
    private String type;
    private List<SubCategoryList> subCategoryLists;

    public FavAdapter(Activity activity, List<SubCategoryList> subCategoryLists, InterstitialAdView interstitialAdView, String type) {
        this.activity = activity;
        method = new Method(activity, interstitialAdView);
        db = new DatabaseHandler(activity);
        columnWidth = (method.getScreenWidth());
        this.subCategoryLists = subCategoryLists;
        this.type = type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.sub_category_adapter, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        if (db.checkId_Fav(subCategoryLists.get(position).getId())) {
            holder.imageView_favourite.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_fav));
        } else {
            holder.imageView_favourite.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_fav_hov));
        }

        holder.imageView.setLayoutParams(new RelativeLayout.LayoutParams(columnWidth, columnWidth / 2 - 60));

        Glide.with(activity).load(subCategoryLists.get(position).getVideo_thumbnail_s())
                .placeholder(R.drawable.placeholder_landscape).into(holder.imageView);


        holder.textView_title.setText(subCategoryLists.get(position).getVideo_title());
        holder.textView_subTitle.setText(subCategoryLists.get(position).getCategory_name());
        holder.textView_view.setText(method.format(Double.parseDouble(subCategoryLists.get(position).getTotal_viewer())));
        holder.textView_like.setText(method.format(Double.parseDouble(subCategoryLists.get(position).getTotal_likes())));

        if (subCategoryLists.get(position).getAlready_like().equals("true")) {
            holder.imageView_like.setImageDrawable(activity.getResources().getDrawable(R.drawable.like_video_hov));
        } else {
            holder.imageView_like.setImageDrawable(activity.getResources().getDrawable(R.drawable.like_video));
        }

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                method.interstitialAdShow(position, type, subCategoryLists.get(position).getId());
            }
        });

        holder.imageView_favourite.setOnClickListener(new View.OnClickListener() {
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
        private ImageView imageView, imageView_favourite, imageView_like;
        private TextView textView_title, textView_subTitle, textView_view, textView_like;

        public ViewHolder(View itemView) {
            super(itemView);

            relativeLayout = itemView.findViewById(R.id.relativeLayout_subCat_adapter);
            imageView = itemView.findViewById(R.id.imageView_subCat_adapter);
            imageView_favourite = itemView.findViewById(R.id.imageView_fav_subCat_adapter);
            imageView_like = itemView.findViewById(R.id.imageView_like_subCat_adapter);
            textView_title = itemView.findViewById(R.id.textView_title_subCat_adapter);
            textView_subTitle = itemView.findViewById(R.id.textView_cat_subCat_adapter);
            textView_view = itemView.findViewById(R.id.textView_view_subCat_adapter);
            textView_like = itemView.findViewById(R.id.textView_like_subCategory_adapter);

        }
    }
}
