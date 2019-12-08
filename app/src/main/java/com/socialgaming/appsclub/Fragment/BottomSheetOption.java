package com.socialgaming.appsclub.Fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.socialgaming.appsclub.R;
import com.socialgaming.appsclub.Util.API;
import com.socialgaming.appsclub.Util.Constant_Api;
import com.socialgaming.appsclub.Util.Method;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import androidx.viewpager.widget.ViewPager;
import cz.msebera.android.httpclient.Header;

public class BottomSheetOption extends BottomSheetDialogFragment {

    private Method method;
    private Dialog dialog;
    private String type, message;
    private EditText editText;
    private RadioGroup radioGroup;
    private InputMethodManager imm;
    private ProgressDialog progressDialog;

    public BottomSheetOption() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.bottom_sheet_option, container, false);

        Bundle mArgs = getArguments();
        final String video_id = mArgs.getString("id");
        final String video_url = mArgs.getString("url");

        method = new Method(getActivity());
        progressDialog = new ProgressDialog(getActivity());

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        LinearLayout linearLayout_share = view.findViewById(R.id.linearLayout_share_bottomSheet);
        LinearLayout linearLayout_report = view.findViewById(R.id.linearLayout_report_bottomSheet);

        linearLayout_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Method.isNetworkAvailable(getActivity())) {
                    if (Method.allowPermitionExternalStorage) {
                        new ShareVideo().execute(video_url, video_id);
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.cannot_use_save_permission), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.internet_connection), Toast.LENGTH_SHORT).show();
                }
            }
        });

        linearLayout_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (method.pref.getBoolean(method.pref_login, false)) {

                    dialog = new Dialog(getActivity());
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.bottom_sheet_report);
                    dialog.getWindow().setLayout(ViewPager.LayoutParams.FILL_PARENT, ViewPager.LayoutParams.WRAP_CONTENT);
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                    Window window = dialog.getWindow();
                    WindowManager.LayoutParams wlp = window.getAttributes();
                    wlp.gravity = Gravity.BOTTOM;
                    wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                    window.setAttributes(wlp);

                    radioGroup = dialog.findViewById(R.id.radioGroup_report_bottomSheet);
                    editText = dialog.findViewById(R.id.editText_report_bottomSheet);
                    Button button = dialog.findViewById(R.id.button_send_report_bottomSheet);

                    radioGroup.clearCheck();

                    radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @SuppressLint("ResourceType")
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            RadioButton rb = group.findViewById(checkedId);
                            if (null != rb && checkedId > -1) {
                                type = rb.getText().toString();
                            }
                        }
                    });

                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            message = editText.getText().toString();
                            editText.clearFocus();
                            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

                            report(video_id);

                        }
                    });

                    dialog.show();
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.you_have_not_login), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    @SuppressLint("StaticFieldLeak")
    private class ShareVideo extends AsyncTask<String, String, String> {

        private ProgressDialog progressDialog;
        private String iconsStoragePath;
        private File sdIconStorageDir;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setIndeterminate(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMessage(getResources().getString(R.string.please_wait));
            progressDialog.setCancelable(false);
            progressDialog.setMax(100);
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.cancel_dialog), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (sdIconStorageDir != null) {
                        sdIconStorageDir.delete();
                    }
                    dialog.dismiss();
                    cancel(true);
                }
            });
            progressDialog.show();
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(String... params) {

            int count;
            try {
                URL url = new URL(params[0]);
                String id = params[1];
                iconsStoragePath = getActivity().getExternalCacheDir().getAbsolutePath();
                String filePath = "file" + id + ".mp4";

                sdIconStorageDir = new File(iconsStoragePath, filePath);

                //create storage directories, if they don't exist
                if (sdIconStorageDir.exists()) {
                    Log.d("File_name", sdIconStorageDir.toString());
                } else {
                    URLConnection conection = url.openConnection();
                    conection.setRequestProperty("Accept-Encoding", "identity");
                    conection.connect();
                    // getting file length
                    int lenghtOfFile = conection.getContentLength();
                    // input stream to read file - with 8k buffer
                    InputStream input = new BufferedInputStream(url.openStream(), 8192);
                    // Output stream to write file
                    OutputStream output = new FileOutputStream(sdIconStorageDir);
                    byte data[] = new byte[1024];
                    long total = 0;
                    while ((count = input.read(data)) != -1) {
                        total += count;
                        // publishing the progress....
                        progressDialog.setProgress((int) (total * 100 / lenghtOfFile));
                        Log.d("progressDialog", String.valueOf((int) (total * 100 / lenghtOfFile)));
                        output.write(data, 0, count);
                    }
                    output.flush(); // flushing output
                    output.close();// closing streams
                    input.close();
                }

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            progressDialog.dismiss();

            Intent share = new Intent(Intent.ACTION_SEND);// Create the new Intent using the 'Send' action.
            share.setType("video/*");   // Set the MIME type
            File media = new File(sdIconStorageDir.toString()); // Create the URI from the media
            Uri uri = Uri.fromFile(media); // Add the URI to the Intent.
            share.putExtra(Intent.EXTRA_STREAM, uri); // Broadcast the Intent.
            startActivity(Intent.createChooser(share, "Share to"));

        }

    }

    //---------------report-------------//

    private void report(String video_id) {

        editText.setError(null);

        if (message == null || message.equals("") || message.isEmpty()) {
            editText.requestFocus();
            editText.setError(getResources().getString(R.string.please_enter_message));
        } else if (type == null || type.equals("") || type.isEmpty()) {
            Toast.makeText(getActivity(), getResources().getString(R.string.please_select_option), Toast.LENGTH_SHORT).show();
        } else {
            String id = method.pref.getString(method.profileId, null);
            String email = method.pref.getString(method.userEmail, null);
            submit(id, email, video_id, type, message);
        }

    }

    private void submit(String userId, String emailId, String videoId, String reportType, String reportMessage) {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        if (getActivity() != null) {

            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("method_name", "video_report");
            jsObj.addProperty("report_user_id", userId);
            jsObj.addProperty("report_email", emailId);
            jsObj.addProperty("report_video_id", videoId);
            jsObj.addProperty("report_type", reportType);
            jsObj.addProperty("report_text", reportMessage);
            params.put("data", API.toBase64(jsObj.toString()));
            client.post(Constant_Api.url, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    if (getActivity() != null) {

                        Log.d("Response", new String(responseBody));
                        String res = new String(responseBody);

                        try {
                            JSONObject jsonObject = new JSONObject(res);

                            if (jsonObject.has(Constant_Api.STATUS)) {

                                String status = jsonObject.getString("status");
                                String message = jsonObject.getString("message");
                                if(status.equals("-2")){
                                    method.suspend(message);
                                }else {
                                    method.alertBox(message);
                                }

                            } else {

                                JSONArray jsonArray = jsonObject.getJSONArray(Constant_Api.tag);

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String msg = object.getString("msg");
                                    String success = object.getString("success");

                                    if (success.equals("1")) {
                                        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                        dismiss();
                                    } else {
                                        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                                    }

                                    editText.setText("");
                                    radioGroup.clearCheck();

                                }

                            }

                            progressDialog.dismiss();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }

                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    progressDialog.dismiss();
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }
    }

    //---------------report-------------//

}
