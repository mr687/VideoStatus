package com.socialgaming.appsclub.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.socialgaming.appsclub.Item.RewardPointList;
import com.socialgaming.appsclub.R;
import com.socialgaming.appsclub.Util.Method;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class RewardPointAdapter extends RecyclerView.Adapter<RewardPointAdapter.ViewHolder> {

    private Method method;
    private Activity activity;
    private List<RewardPointList> rewardPointLists;

    public RewardPointAdapter(Activity activity, List<RewardPointList> rewardPointLists) {
        this.activity = activity;
        this.rewardPointLists = rewardPointLists;
        method = new Method(activity);
    }

    @NonNull
    @Override
    public RewardPointAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.reward_point_adapter, parent, false);

        return new RewardPointAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RewardPointAdapter.ViewHolder holder, final int position) {


        if (rewardPointLists.get(position).getVideo_title().equals("")) {
            Glide.with(activity).load(rewardPointLists.get(position).getVideo_thumbnail())
                    .placeholder(R.drawable.round_icon).into(holder.imageView);
            try {
                String[] type = rewardPointLists.get(position).getActivity_type().split(" - ");
                holder.textView_title.setText(type[0]);
                holder.textView_type.setText(type[1]);
            } catch (Exception e) {
                holder.textView_title.setText(rewardPointLists.get(position).getActivity_type());
                holder.textView_type.setText(rewardPointLists.get(position).getActivity_type());
            }
        } else {
            Glide.with(activity).load(rewardPointLists.get(position).getVideo_thumbnail()).into(holder.imageView);
            holder.textView_title.setText(rewardPointLists.get(position).getVideo_title());
            holder.textView_type.setText(rewardPointLists.get(position).getActivity_type());
        }
        holder.textView_date.setText(rewardPointLists.get(position).getDate());
        holder.textView_time.setText(rewardPointLists.get(position).getTime());
        holder.textView_point.setText(rewardPointLists.get(position).getPoints());

    }

    @Override
    public int getItemCount() {
        return rewardPointLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView imageView;
        private TextView textView_title, textView_date, textView_time, textView_type, textView_point;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView_reward_point_adapter);
            textView_title = itemView.findViewById(R.id.textView_title_reward_point_adapter);
            textView_date = itemView.findViewById(R.id.textView_date_reward_point_adapter);
            textView_time = itemView.findViewById(R.id.textView_time_reward_point_adapter);
            textView_point = itemView.findViewById(R.id.textView_point_reward_point_adapter);
            textView_type = itemView.findViewById(R.id.textView_type_reward_point_adapter);

        }
    }
}
