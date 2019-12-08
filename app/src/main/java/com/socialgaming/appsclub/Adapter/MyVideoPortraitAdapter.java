package com.socialgaming.appsclub.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.socialgaming.appsclub.InterFace.InterstitialAdView;
import com.socialgaming.appsclub.Item.SubCategoryList;
import com.socialgaming.appsclub.R;
import com.socialgaming.appsclub.Util.API;
import com.socialgaming.appsclub.Util.Constant_Api;
import com.socialgaming.appsclub.Util.Method;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import cz.msebera.android.httpclient.Header;


public class MyVideoPortraitAdapter extends RecyclerView.Adapter {

    private Activity activity;
    private Method method;
    private String userId, type;
    private ProgressDialog progressDialog;
    private List<SubCategoryList> myVideoLists;

    private final int VIEW_TYPE_LOADING = 0;
    private final int VIEW_TYPE_ITEM = 1;

    public MyVideoPortraitAdapter(Activity activity, List<SubCategoryList> myVideoLists, String userId, String type, InterstitialAdView interstitialAdView) {
        this.activity = activity;
        method = new Method(activity, interstitialAdView);
        this.userId = userId;
        this.type = type;
        this.myVideoLists = myVideoLists;
        progressDialog = new ProgressDialog(activity);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(activity).inflate(R.layout.my_video_por_adapter, parent, false);
            return new ViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View v = LayoutInflater.from(activity).inflate(R.layout.layout_loading_item, parent, false);
            return new ProgressViewHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        if (holder instanceof ViewHolder) {

            final ViewHolder viewHolder = (ViewHolder) holder;

            if (userId.equals(method.pref.getString(method.profileId, null))) {
                viewHolder.relativeLayout.setVisibility(View.VISIBLE);
            } else {
                viewHolder.relativeLayout.setVisibility(View.GONE);
            }

            Glide.with(activity).load(myVideoLists.get(position).getVideo_thumbnail_s())
                    .placeholder(R.drawable.placeholder_portable)
                    .into(viewHolder.imageView);

            viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    method.interstitialAdShow(position, type, myVideoLists.get(position).getId());
                }
            });

            viewHolder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                    alertDialogBuilder.setMessage(activity.getResources().getString(R.string.delete_msg));
                    alertDialogBuilder.setCancelable(false);
                    alertDialogBuilder.setPositiveButton(activity.getResources().getString(R.string.delete),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    if (Method.isNetworkAvailable(activity)) {
                                        delete_video(position, userId);
                                    } else {
                                        Toast.makeText(activity, activity.getResources().getString(R.string.internet_connection), Toast.LENGTH_SHORT).show();
                                    }
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
    }

    @Override
    public int getItemCount() {
        if (myVideoLists.size() != 0) {
            return myVideoLists.size() + 1;
        } else {
            return myVideoLists.size();
        }
    }

    public void hideHeader() {
        ProgressViewHolder.progressBar.setVisibility(View.GONE);
    }

    private boolean isHeader(int position) {
        return position == myVideoLists.size();
    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private RoundedImageView imageView;
        private ImageView imageView_delete;
        private RelativeLayout relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView_myVideo_por_adapter);
            imageView_delete = itemView.findViewById(R.id.imageView_delete_myVideo_por_adapter);
            relativeLayout = itemView.findViewById(R.id.relativeLayout_myVideo_por_adapter);

        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public static ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar);
        }
    }

    private void delete_video(final int position, String userId) {

        progressDialog.setTitle(activity.getResources().getString(R.string.delete));
        progressDialog.setCancelable(false);
        progressDialog.show();

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(activity));
        jsObj.addProperty("method_name", "user_video_delete");
        jsObj.addProperty("user_id", userId);
        jsObj.addProperty("video_id", myVideoLists.get(position).getId());
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant_Api.url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                Log.d("Response", new String(responseBody));
                String res = new String(responseBody);

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

                        JSONArray jsonArray = jsonObject.getJSONArray(Constant_Api.tag);

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject object = jsonArray.getJSONObject(i);
                            String msg = object.getString("msg");
                            String success = object.getString("success");

                            if (success.equals("1")) {
                                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
                                myVideoLists.remove(position);
                                notifyDataSetChanged();
                            } else {
                                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
                            }
                        }

                    }

                    progressDialog.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    method.alertBox(activity.getResources().getString(R.string.failed_try_again));
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.dismiss();
                method.alertBox(activity.getResources().getString(R.string.failed_try_again));
            }
        });
    }

}
