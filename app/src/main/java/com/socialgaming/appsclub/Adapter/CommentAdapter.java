package com.socialgaming.appsclub.Adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.socialgaming.appsclub.Item.CommentList;
import com.socialgaming.appsclub.R;
import com.socialgaming.appsclub.Util.API;
import com.socialgaming.appsclub.Util.Constant_Api;
import com.socialgaming.appsclub.Util.Events;
import com.socialgaming.appsclub.Util.GlobalBus;
import com.socialgaming.appsclub.Util.Method;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private Method method;
    private Animation myAnim;
    private Activity activity;
    private ProgressDialog progressDialog;
    private List<CommentList> commentLists;

    public CommentAdapter(Activity activity, List<CommentList> commentLists) {
        this.activity = activity;
        this.commentLists = commentLists;
        method = new Method(activity);
        myAnim = AnimationUtils.loadAnimation(activity, R.anim.bounce);
        progressDialog = new ProgressDialog(activity);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.comment_adapter, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        if (!commentLists.get(position).getUser_image().equals("")) {
            Glide.with(activity).load(commentLists.get(position).getUser_image())
                    .placeholder(R.drawable.profile)
                    .into(holder.circleImageView);
        }

        if (method.pref.getBoolean(method.pref_login, false)) {
            if (method.pref.getString(method.profileId, null).equals(commentLists.get(position).getUser_id())) {
                holder.textView_delete.setVisibility(View.VISIBLE);
            } else {
                holder.textView_delete.setVisibility(View.GONE);
            }
        } else {
            holder.textView_delete.setVisibility(View.GONE);
        }

        holder.textView_Name.setText(commentLists.get(position).getUser_name());
        holder.textView_date.setText(commentLists.get(position).getComment_date());
        holder.textView_comment.setText(commentLists.get(position).getComment_text());

        holder.textView_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                holder.textView_delete.startAnimation(myAnim);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                alertDialogBuilder.setMessage(activity.getResources().getString(R.string.delete_comment));
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setPositiveButton(activity.getResources().getString(R.string.delete),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                delete(commentLists.get(position).getVideo_id(), commentLists.get(position).getComment_id(), position);
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
        if (commentLists.size() == 0) {
            return 0;
        } else if (commentLists.size() == 1) {
            return 1;
        } else {
            return 2;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView circleImageView;
        private TextView textView_Name, textView_date, textView_comment, textView_delete;

        public ViewHolder(View itemView) {
            super(itemView);

            circleImageView = itemView.findViewById(R.id.imageView_comment_adapter);
            textView_Name = itemView.findViewById(R.id.textView_userName_comment_adapter);
            textView_date = itemView.findViewById(R.id.textView_date_comment_adapter);
            textView_comment = itemView.findViewById(R.id.textView_comment_adapter);
            textView_delete = itemView.findViewById(R.id.textView_delete_adapter);

        }
    }

    public void delete(String video_id, String comment_id, int position) {

        progressDialog.show();
        progressDialog.setMessage(activity.getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(activity));
        jsObj.addProperty("method_name", "delete_comment");
        jsObj.addProperty("video_id", video_id);
        jsObj.addProperty("comment_id", comment_id);
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant_Api.url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                Log.d("Response", new String(responseBody));
                String res = new String(responseBody);

                boolean isComment = false;

                try {
                    JSONObject jsonObject = new JSONObject(res);

                    if (jsonObject.has(Constant_Api.STATUS)) {

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        if (status.equals("-2")) {
                            method.suspend(message);
                        } else {
                            method.alertBox(message);
                        }

                    } else {

                        String msg = jsonObject.getString("msg");
                        String success = jsonObject.getString("success");

                        if (success.equals("1")) {

                            commentLists.remove(position);

                            String status = jsonObject.getString("comment_status");
                            String total_comment = jsonObject.getString("total_comment");

                            if (status.equals("1")) {

                                String comment_id = jsonObject.getString("comment_id");
                                String comment_user_id = jsonObject.getString("user_id");
                                String comment_user_name = jsonObject.getString("user_name");
                                String comment_user_image = jsonObject.getString("user_image");
                                String comment_video_id = jsonObject.getString("video_id");
                                String comment_text = jsonObject.getString("comment_text");
                                String comment_date = jsonObject.getString("comment_date");

                                for (int i = 0; i < commentLists.size(); i++) {
                                    if (commentLists.get(i).getComment_id().equals(comment_id)) {
                                        isComment = true;
                                    }
                                }

                                if (!isComment) {
                                    commentLists.add(position, new CommentList(comment_id, comment_user_id, comment_user_name, comment_user_image, comment_video_id, comment_text, comment_date));
                                }

                            }
                            notifyDataSetChanged();

                            Events.TotalComment totalComment = new Events.TotalComment(total_comment, video_id, comment_id, "comment");
                            GlobalBus.getBus().post(totalComment);


                        } else {
                            method.alertBox(msg);
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    method.alertBox(activity.getResources().getString(R.string.failed_try_again));
                }

                progressDialog.dismiss();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.dismiss();
                method.alertBox(activity.getResources().getString(R.string.failed_try_again));
            }
        });

    }

}
