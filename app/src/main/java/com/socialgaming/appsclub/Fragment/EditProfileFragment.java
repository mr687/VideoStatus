package com.socialgaming.appsclub.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.socialgaming.appsclub.Activity.MainActivity;
import com.socialgaming.appsclub.R;
import com.socialgaming.appsclub.Util.API;
import com.socialgaming.appsclub.Util.Constant_Api;
import com.socialgaming.appsclub.Util.Method;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nguyenhoanglam.imagepicker.model.Config;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileFragment extends Fragment {

    private Method method;
    public Toolbar toolbar;
    private EditText editText_name, editText_email, editText_password,
            editText_confirm_password, editText_phoneNo, editText_instagram, editText_youtube;
    private LinearLayout linearLayout_email, linearLayout_password, linearLayout_conform_password;
    private CircleImageView circleImageView;
    private String profileId;
    private ProgressBar progressBar;
    private int REQUEST_GALLERY_PICKER = 100;
    private ArrayList<Image> galleryImages;
    private String image_profile;
    private boolean is_profile;
    private InputMethodManager imm;
    final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 101;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.edit_profile_fragment, container, false);

        MainActivity.toolbar.setTitle(getResources().getString(R.string.edit_profile));

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        method = new Method(getActivity());
        galleryImages = new ArrayList<>();

        assert getArguments() != null;
        String set_name = getArguments().getString("name");
        String set_email = getArguments().getString("email");
        String set_phone = getArguments().getString("phone");
        String instagram = getArguments().getString("instagram");
        String youtube = getArguments().getString("youtube");
        String user_image = getArguments().getString("user_image");
        profileId = getArguments().getString("profileId");

        progressBar = view.findViewById(R.id.progressbar_editPro);
        circleImageView = view.findViewById(R.id.imageView_user_editPro);
        linearLayout_email = view.findViewById(R.id.ll_email_pro);
        linearLayout_password = view.findViewById(R.id.ll_password_pro);
        linearLayout_conform_password = view.findViewById(R.id.ll_confirm_pass_pro);
        editText_name = view.findViewById(R.id.editText_name_editPro);
        editText_email = view.findViewById(R.id.editText_email_editPro);
        editText_password = view.findViewById(R.id.editText_password_editPro);
        editText_confirm_password = view.findViewById(R.id.editText_confirm_pass_editPro);
        editText_phoneNo = view.findViewById(R.id.editText_phone_editPro);
        editText_instagram = view.findViewById(R.id.editText_insta_editPro);
        editText_youtube = view.findViewById(R.id.editText_youtube_editPro);

        if (method.pref.getString(method.loginType, null).equals("google")) {
            linearLayout_email.setVisibility(View.GONE);
            linearLayout_password.setVisibility(View.GONE);
            linearLayout_conform_password.setVisibility(View.GONE);
        } else {
            linearLayout_email.setVisibility(View.VISIBLE);
            linearLayout_password.setVisibility(View.VISIBLE);
            linearLayout_conform_password.setVisibility(View.VISIBLE);
        }

        editText_email.setFocusable(false);

        progressBar.setVisibility(View.GONE);

        editText_name.setText(set_name);
        editText_email.setText(set_email);
        editText_phoneNo.setText(set_phone);
        editText_instagram.setText(instagram);
        editText_youtube.setText(youtube);

        assert user_image != null;
        if (!user_image.equals("")) {
            Glide.with(getActivity().getApplicationContext()).load(user_image).placeholder(R.drawable.user_profile).into(circleImageView);
        }

        image_profile = user_image;

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPer()) {
                    chooseGalleryImage();
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.cannot_use_upload_image), Toast.LENGTH_SHORT).show();
                }
            }
        });

        setHasOptionsMenu(true);
        return view;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GALLERY_PICKER) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                is_profile = true;
                galleryImages = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
                image_profile = galleryImages.get(0).getPath();
                Uri uri = Uri.fromFile(new File(galleryImages.get(0).getPath()));
                Glide.with(getActivity().getApplicationContext()).load(uri).into(circleImageView);
            }
        }
    }

    private void chooseGalleryImage() {
        try {
            ImagePicker.with(this)
                    .setFolderMode(true)
                    .setFolderTitle("Album")
                    .setImageTitle(getResources().getString(R.string.app_name))
                    .setStatusBarColor("#f20056")
                    .setToolbarColor("#f20056")
                    .setProgressBarColor("#f20056")
                    .setMultipleMode(true)
                    .setMaxSize(1)
                    .setShowCamera(false)
                    .start();
        } catch (Exception e) {
            Log.e("error", e.toString());
        }

    }

    private Boolean checkPer() {
        if (getActivity() != null) {
            if ((ContextCompat.checkSelfPermission(getActivity(), "android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"},
                            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                    return false;
                }
                return true;
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.edit_profile_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private boolean isValidMail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_save:

                String name = editText_name.getText().toString();
                String email = editText_email.getText().toString();
                String password = editText_password.getText().toString();
                String confirm_password = editText_confirm_password.getText().toString();
                String phoneNo = editText_phoneNo.getText().toString();
                String instagram = editText_instagram.getText().toString();
                String youtube = editText_youtube.getText().toString();

                editText_name.clearFocus();
                editText_email.clearFocus();
                editText_password.clearFocus();
                editText_confirm_password.clearFocus();
                editText_phoneNo.clearFocus();
                editText_instagram.clearFocus();
                editText_youtube.clearFocus();
                imm.hideSoftInputFromWindow(editText_name.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(editText_email.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(editText_password.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(editText_confirm_password.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(editText_phoneNo.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(editText_instagram.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(editText_youtube.getWindowToken(), 0);

                editText_name.setError(null);
                editText_email.setError(null);
                editText_phoneNo.setError(null);

                if (name.equals("") || name.isEmpty()) {
                    editText_name.requestFocus();
                    editText_name.setError(getResources().getString(R.string.please_enter_name));
                } else if (!isValidMail(email) || email.isEmpty()) {
                    editText_email.requestFocus();
                    editText_email.setError(getResources().getString(R.string.please_enter_email));
                } else if (phoneNo.equals("") || phoneNo.isEmpty()) {
                    editText_phoneNo.requestFocus();
                    editText_phoneNo.setError(getResources().getString(R.string.please_enter_phone));
                } else if (!password.equals(confirm_password)) {
                    method.alertBox(getResources().getString(R.string.password_and_confirmpassword_does_not_match));
                } else if (image_profile.equals("") || image_profile.isEmpty()) {
                    method.alertBox(getResources().getString(R.string.image_select));
                } else {
                    if (getActivity() != null) {
                        if (Method.isNetworkAvailable(getActivity())) {
                            profileUpdate(profileId, name, email, password, phoneNo, youtube, instagram, image_profile);
                        } else {
                            method.alertBox(getResources().getString(R.string.internet_connection));
                        }
                    } else {
                        method.alertBox(getResources().getString(R.string.wrong));
                    }
                }

                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void profileUpdate(String id, String sendName, String sendEmail, String sendPassword,
                               String sendPhone, String user_youtube, String user_instagram, String profile_image) {

        progressBar.setVisibility(View.VISIBLE);

        if (getActivity() != null) {

            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("method_name", "user_profile_update");
            jsObj.addProperty("user_id", id);
            jsObj.addProperty("name", sendName);
            jsObj.addProperty("email", sendEmail);
            jsObj.addProperty("password", sendPassword);
            jsObj.addProperty("phone", sendPhone);
            jsObj.addProperty("user_youtube", user_youtube);
            jsObj.addProperty("user_instagram", user_instagram);
            try {
                if (is_profile) {
                    params.put("user_image", new File(profile_image));
                } else {
                    params.put("user_image", profile_image);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
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
                                        getActivity().getSupportFragmentManager().popBackStack();
                                        ProfileFragment profileFragment = new ProfileFragment();
                                        Bundle bundle_profile = new Bundle();
                                        bundle_profile.putString("type", "user");
                                        bundle_profile.putString("id", method.pref.getString(method.profileId, null));
                                        profileFragment.setArguments(bundle_profile);
                                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, profileFragment, getResources().getString(R.string.profile)).commit();
                                    } else {
                                        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }

                            progressBar.setVisibility(View.GONE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }

                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    progressBar.setVisibility(View.GONE);
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }

    }

}
