package com.socialgaming.appsclub.Adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.socialgaming.appsclub.Activity.VideoPlayer;
import com.socialgaming.appsclub.DataBase.DatabaseHandler;
import com.socialgaming.appsclub.Item.SubCategoryList;
import com.socialgaming.appsclub.R;
import com.socialgaming.appsclub.Util.Method;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;


public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.ViewHolder> {

    private Activity activity;
    private Method method;
    private DatabaseHandler db;
    private Animation myAnim;
    private int columnWidth;
    private List<SubCategoryList> downloadLists;

    public DownloadAdapter(Activity activity, List<SubCategoryList> subCategoryLists) {
        this.activity = activity;
        method = new Method(activity);
        db = new DatabaseHandler(activity);
        columnWidth = (method.getScreenWidth());
        this.downloadLists = subCategoryLists;
        myAnim = AnimationUtils.loadAnimation(activity, R.anim.bounce);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.download_adapter, parent, false);

        return new DownloadAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.textView_name.setText(downloadLists.get(position).getVideo_title());
        holder.textView_subName.setText(downloadLists.get(position).getCategory_name());

        holder.imageView.setLayoutParams(new RelativeLayout.LayoutParams(columnWidth, columnWidth / 2));
        Glide.with(activity).load("file://" + downloadLists.get(position).getVideo_thumbnail_s()).into(holder.imageView);

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, VideoPlayer.class);
                intent.putExtra("Video_url", downloadLists.get(position).getVideo_url());
                intent.putExtra("video_type", downloadLists.get(position).getVideo_layout());
                activity.startActivity(intent);
            }
        });

        holder.imageView_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.imageView_delete.startAnimation(myAnim);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                alertDialogBuilder.setMessage(activity.getResources().getString(R.string.delete_msg));
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setPositiveButton(activity.getResources().getString(R.string.delete),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                db.delete_video_download(downloadLists.get(position).getId());
                                File file = new File(downloadLists.get(position).getVideo_url());
                                File file_image = new File(downloadLists.get(position).getVideo_thumbnail_b());
                                file_image.delete();
                                file.delete();
                                downloadLists.remove(position);
                                notifyDataSetChanged();
                            }
                        });
                alertDialogBuilder.setNegativeButton(activity.getResources().getString(R.string.cancel_dialog),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return downloadLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private RoundedImageView imageView;
        private RelativeLayout relativeLayout;
        private TextView textView_name, textView_subName;
        private ImageView imageView_delete;

        public ViewHolder(View itemView) {
            super(itemView);

            relativeLayout = itemView.findViewById(R.id.relativeLayout_imageView_download_adapter);
            textView_name = itemView.findViewById(R.id.textView_title_download_adapter);
            textView_subName = itemView.findViewById(R.id.textView_sub_title_download_adapter);
            imageView = itemView.findViewById(R.id.imageView_download_adapter);
            imageView_delete = itemView.findViewById(R.id.imageView_delete_download_adapter);

        }
    }
}
