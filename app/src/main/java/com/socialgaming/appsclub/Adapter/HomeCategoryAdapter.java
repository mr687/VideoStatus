package com.socialgaming.appsclub.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.socialgaming.appsclub.InterFace.InterstitialAdView;
import com.socialgaming.appsclub.Item.CategoryList;
import com.socialgaming.appsclub.R;
import com.socialgaming.appsclub.Util.Method;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


public class HomeCategoryAdapter extends RecyclerView.Adapter<HomeCategoryAdapter.ViewHolder> {

    private Activity activity;
    private Method method;
    private String string;
    private List<CategoryList> categoryLists;

    public HomeCategoryAdapter(Activity activity, List<CategoryList> categoryLists, String string, InterstitialAdView interstitialAdView) {
        this.activity = activity;
        method = new Method(activity, interstitialAdView);
        this.string = string;
        this.categoryLists = categoryLists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.home_category_adapter, parent, false);

        return new HomeCategoryAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        if (categoryLists.get(position).getCategory_name().equals(activity.getResources().getString(R.string.view_all))) {
            holder.view.setVisibility(View.GONE);
            holder.imageView.setVisibility(View.GONE);
            holder.textView.setTextSize(18);
            holder.calendarView.setCardBackgroundColor(activity.getResources().getColor(R.color.toolbar));
        } else {
            holder.view.setVisibility(View.VISIBLE);
            holder.imageView.setVisibility(View.VISIBLE);
            holder.textView.setTextSize(16);
            Glide.with(activity).load(categoryLists.get(position).getCategory_image_thumb())
                    .placeholder(R.drawable.placeholder_landscape).into(holder.imageView);
        }

        holder.textView.setText(categoryLists.get(position).getCategory_name());

        holder.calendarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                method.interstitialAdShow(position, string, "");
            }
        });

    }

    @Override
    public int getItemCount() {
        return categoryLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CardView calendarView;
        private RoundedImageView imageView;
        private View view;
        private TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);

            calendarView = itemView.findViewById(R.id.cardView_home_category_adapter);
            imageView = itemView.findViewById(R.id.imageView_home_category_adapter);
            view = itemView.findViewById(R.id.view_home_category_adapter);
            textView = itemView.findViewById(R.id.textView_home_category_adapter);

        }
    }
}
