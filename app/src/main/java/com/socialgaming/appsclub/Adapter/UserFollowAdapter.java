package com.socialgaming.appsclub.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.socialgaming.appsclub.InterFace.InterstitialAdView;
import com.socialgaming.appsclub.Item.UserFollowList;
import com.socialgaming.appsclub.R;
import com.socialgaming.appsclub.Util.Method;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserFollowAdapter extends RecyclerView.Adapter<UserFollowAdapter.ViewHolder> {

    private Method method;
    private Activity activity;
    private List<UserFollowList> userFollowLists;

    public UserFollowAdapter(Activity activity, List<UserFollowList> userFollowLists, InterstitialAdView interstitialAdView) {
        this.activity = activity;
        method = new Method(activity, interstitialAdView);
        this.userFollowLists = userFollowLists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.user_follow_adapter, parent, false);

        return new UserFollowAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        if (!userFollowLists.get(position).getUser_image().equals("")) {
            Glide.with(activity).load(userFollowLists.get(position).getUser_image())
                    .placeholder(R.drawable.user_profile).into(holder.circleImageView);
        }

        if (userFollowLists.get(position).getIs_verified().equals("true")) {
            holder.textView_userName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_verification, 0);
        }

        holder.textView_userName.setText(userFollowLists.get(position).getFollow_user_name());

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                method.interstitialAdShow(position, "", userFollowLists.get(position).getFollow_user_id());
            }
        });

    }

    @Override
    public int getItemCount() {
        return userFollowLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout relativeLayout;
        private CircleImageView circleImageView;
        private TextView textView_userName;

        public ViewHolder(View itemView) {
            super(itemView);

            relativeLayout = itemView.findViewById(R.id.relativeLayout_user_follow_adapter);
            circleImageView = itemView.findViewById(R.id.imageView_user_follow_adapter);
            textView_userName = itemView.findViewById(R.id.textView_user_follow_adapter);

        }
    }
}
