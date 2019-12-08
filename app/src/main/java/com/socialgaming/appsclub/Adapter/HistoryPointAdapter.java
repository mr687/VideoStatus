package com.socialgaming.appsclub.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.socialgaming.appsclub.Item.RewardPointList;
import com.socialgaming.appsclub.R;
import com.socialgaming.appsclub.Util.Method;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HistoryPointAdapter extends RecyclerView.Adapter<HistoryPointAdapter.ViewHolder> {

    private Method method;
    private Activity activity;
    private List<RewardPointList> rewardPointLists;

    public HistoryPointAdapter(Activity activity, List<RewardPointList> rewardPointLists) {
        this.activity = activity;
        this.rewardPointLists = rewardPointLists;
        method = new Method(activity);
    }

    @NonNull
    @Override
    public HistoryPointAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.history_point_adapter, parent, false);

        return new HistoryPointAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryPointAdapter.ViewHolder holder, final int position) {

        holder.textView_type.setText(rewardPointLists.get(position).getActivity_type());
        holder.textView_date.setText(rewardPointLists.get(position).getDate());
        holder.textView_point.setText(rewardPointLists.get(position).getPoints());

    }

    @Override
    public int getItemCount() {
        return rewardPointLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout relativeLayout;
        private TextView textView_type, textView_date, textView_point;

        public ViewHolder(View itemView) {
            super(itemView);

            relativeLayout = itemView.findViewById(R.id.relativeLayout_history_point_adapter);
            textView_type = itemView.findViewById(R.id.textView_type_history_point_adapter);
            textView_date = itemView.findViewById(R.id.textView_date_history_point_adapter);
            textView_point = itemView.findViewById(R.id.textView_point_history_point_adapter);

        }
    }
}
