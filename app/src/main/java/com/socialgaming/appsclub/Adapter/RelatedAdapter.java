package com.socialgaming.appsclub.Adapter;

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
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class RelatedAdapter extends RecyclerView.Adapter<RelatedAdapter.ViewHolder> {

    private Method method;
    private Activity activity;
    private DatabaseHandler db;
    private String type;
    private List<SubCategoryList> relatedLists;

    public RelatedAdapter(Activity activity, List<SubCategoryList> relatedLists, InterstitialAdView interstitialAdView, String type) {
        this.activity = activity;
        method = new Method(activity, interstitialAdView);
        db = new DatabaseHandler(activity);
        this.relatedLists = relatedLists;
        this.type = type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.related_adapter, parent, false);

        return new RelatedAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        if (db.checkId_Fav(relatedLists.get(position).getId())) {
            holder.imageView_favourite.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_fav));
        } else {
            holder.imageView_favourite.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_fav_hov));
        }

        Glide.with(activity).load(relatedLists.get(position).getVideo_thumbnail_s())
                .placeholder(R.drawable.placeholder_portable).into(holder.imageView);

        holder.textView_title.setText(relatedLists.get(position).getVideo_title());
        holder.textView_subTitle.setText(relatedLists.get(position).getCategory_name());

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                method.interstitialAdShow(position, type, relatedLists.get(position).getId());
            }
        });

        holder.imageView_favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (db.checkId_Fav(relatedLists.get(position).getId())) {
                    method.addToFav(db, relatedLists, position);
                    holder.imageView_favourite.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_fav_hov));
                } else {
                    db.deleteFav(relatedLists.get(position).getId());
                    holder.imageView_favourite.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_fav));
                }
                Events.FavouriteNotify homeNotify = new Events.FavouriteNotify(relatedLists.get(position).getId(), relatedLists.get(position).getVideo_layout());
                GlobalBus.getBus().post(homeNotify);

            }
        });

    }

    @Override
    public int getItemCount() {
        if (relatedLists.size() == 0) {
            return 0;
        } else if (relatedLists.size() == 1) {
            return 1;
        } else if (relatedLists.size() == 2) {
            return 2;
        } else {
            return 3;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private RoundedImageView imageView;
        private ImageView imageView_favourite;
        private TextView textView_title, textView_subTitle;
        private RelativeLayout relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            relativeLayout = itemView.findViewById(R.id.linearLayout_home_adapter);
            imageView = itemView.findViewById(R.id.imageView_home_adapter);
            imageView_favourite = itemView.findViewById(R.id.imageView_favourite_home_adapter);
            textView_title = itemView.findViewById(R.id.textView_title_home_adapter);
            textView_subTitle = itemView.findViewById(R.id.textView_subtitle_home_adapter);

        }
    }
}
