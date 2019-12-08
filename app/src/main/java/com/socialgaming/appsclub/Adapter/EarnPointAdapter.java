package com.socialgaming.appsclub.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.socialgaming.appsclub.Item.EarnPointList;
import com.socialgaming.appsclub.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class EarnPointAdapter extends RecyclerView.Adapter<EarnPointAdapter.ViewHolder> {

    private Activity activity;
    private List<EarnPointList> earnPointLists;

    public EarnPointAdapter(Activity activity, List<EarnPointList> earnPointLists) {
        this.activity = activity;
        this.earnPointLists = earnPointLists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.earn_point_adapter, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.textView_title.setText(earnPointLists.get(position).getTitle());
        holder.textView_point.setText(earnPointLists.get(position).getPoint());

    }

    @Override
    public int getItemCount() {
        return earnPointLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textView_title, textView_point;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textView_title = itemView.findViewById(R.id.textView_title_ep_adapter);
            textView_point = itemView.findViewById(R.id.textView_ep_adapter);

        }
    }
}
