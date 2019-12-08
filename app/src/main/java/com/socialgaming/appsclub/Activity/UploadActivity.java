package com.socialgaming.appsclub.Activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.socialgaming.appsclub.Item.CategoryList;
import com.socialgaming.appsclub.R;
import com.socialgaming.appsclub.Util.API;
import com.socialgaming.appsclub.Util.Constant_Api;
import com.socialgaming.appsclub.Util.Method;
import com.socialgaming.appsclub.Util.UploadService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nguyenhoanglam.imagepicker.model.Config;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import cz.msebera.android.httpclient.Header;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;


public class UploadActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Method method;
    private ImageView imageView;
    private TextView textView_image, textView_video;
    private EditText editText;
    private Spinner spinner_cat, spinner_videoType;
    private Button button;
    private String categoryId, videoType, video_image;
    private ArrayList<Image> galleryImages;
    private String[] videoTypeList;
    private ProgressBar progressBar;
    private List<CategoryList> categoryLists;
    private String videoPath;
    private int REQUEST_GALLERY_PICKER = 100;
    private int REQUEST_CODE_CHOOSE = 0;
    private int positionVideoType = 0;
    private InputMethodManager imm;
    private CardView cardView_imageUpload;
    private LinearLayout linearLayout, linearLayout_image, linearLayout_video;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        Method.forceRTLIfSupported(getWindow(), UploadActivity.this);

        Method.activity_upload = UploadActivity.this;

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        method = new Method(UploadActivity.this);

        toolbar = findViewById(R.id.toolbar_upload);
        toolbar.setTitle(getResources().getString(R.string.upload_video));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        categoryLists = new ArrayList<>();
        galleryImages = new ArrayList<>();
        videoTypeList = new String[]{getResources().getString(R.string.selected_video_type),
                getResources().getString(R.string.landscape),
                getResources().getString(R.string.portrait)};

        progressBar = findViewById(R.id.progressbar_upload);
        editText = findViewById(R.id.editText_upload);
        button = findViewById(R.id.button_upload);
        imageView = findViewById(R.id.imageView_upload);
        spinner_cat = findViewById(R.id.spinner_upload);
        spinner_videoType = findViewById(R.id.spinner_videoType_upload);
        cardView_imageUpload = findViewById(R.id.cardView_imageUpload);
        linearLayout_image = findViewById(R.id.linearLayout_image_select_upload);
        linearLayout_video = findViewById(R.id.linearLayout_video_select_upload);
        textView_image = findViewById(R.id.textView_image_upload);
        textView_video = findViewById(R.id.textView_video_upload);

        linearLayout = findViewById(R.id.linearLayout_upload);

        cardView_imageUpload.setVisibility(View.GONE);

        if (method.personalization_ad) {
            method.showPersonalizedAds(linearLayout);
        } else {
            method.showNonPersonalizedAds(linearLayout);
        }

        if (Method.isUpload) {
            button.setVisibility(View.VISIBLE);
        } else {
            button.setVisibility(View.GONE);
        }

        if (Method.isNetworkAvailable(UploadActivity.this)) {
            Category();
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
            progressBar.setVisibility(View.GONE);
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GALLERY_PICKER) {
            if (resultCode == RESULT_OK && data != null) {
                galleryImages = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
                Uri uri_banner = Uri.fromFile(new File(galleryImages.get(0).getPath()));
                video_image = galleryImages.get(0).getPath();
                Glide.with(UploadActivity.this).load(uri_banner).into(imageView);
                textView_image.setText(galleryImages.get(0).getPath());
                CropImage.activity(uri_banner).start(UploadActivity.this);
            }
        }

        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {

            File file = null;
            Uri uri = data.getData();
            String file_path = getPath(UploadActivity.this, uri);
            assert file_path != null;
            try {
                file = new File(file_path);
                int file_size = (int) file.length() / (1024 * 1024);
                Log.d("file_size", String.valueOf(file_size));
                String file_name = file.getName();

                if (file_name.contains(getResources().getString(R.string.file_type_extension))) {
                    if (file_size <= Constant_Api.VIDEO_FILE_SIZE) {
                        videoPath = file_path;
                        if (!(getDurationInt(videoPath) <= Constant_Api.VIDEO_FILE_DURATION)) {
                            videoPath = "";
                            textView_video.setTextColor(getResources().getColor(R.color.green));
                            textView_video.setText(getResources().getString(R.string.file_size_duration));
                        } else {
                            textView_video.setTextColor(getResources().getColor(R.color.textView_upload_fragment));
                            textView_video.setText(videoPath);
                            try {
                                Bitmap thumb = ThumbnailUtils.createVideoThumbnail(file_path, MediaStore.Images.Thumbnails.MINI_KIND);
                                if (thumb != null) {
                                    downloadImage(thumb);
                                } else {
                                    cardView_imageUpload.setVisibility(View.VISIBLE);
                                }
                            } catch (Exception e) {
                                cardView_imageUpload.setVisibility(View.VISIBLE);
                            }
                        }
                    } else {
                        videoPath = "";
                        textView_video.setTextColor(getResources().getColor(R.color.green));
                        textView_video.setText(getResources().getString(R.string.file_size));
                    }
                } else {
                    videoPath = "";
                    textView_video.setTextColor(getResources().getColor(R.color.green));
                    textView_video.setText(getResources().getString(R.string.file_type));
                }
            } catch (Exception e) {
                method.alertBox(getResources().getString(R.string.upload_folder_error));
            }

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                video_image = resultUri.getPath();
                Glide.with(UploadActivity.this).load(resultUri).into(imageView);
                textView_image.setText(galleryImages.get(0).getPath());
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

    public void chooseGalleryImage() {
        ImagePicker.with(this)
                .setFolderMode(true)
                .setFolderTitle("Album")
                .setImageTitle(getResources().getString(R.string.app_name))
                .setStatusBarColor("#7E0101")
                .setToolbarColor("#7E0101")
                .setProgressBarColor("#7E0101")
                .setMultipleMode(true)
                .setMaxSize(1)
                .setShowCamera(false)
                .start();
    }

    public void Category() {

        categoryLists.clear();
        progressBar.setVisibility(View.VISIBLE);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(UploadActivity.this));
        jsObj.addProperty("method_name", "cat_list");
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
                        if(status.equals("-2")){
                            method.suspend(message);
                        }else {
                            method.alertBox(message);
                        }

                    } else {

                        JSONArray jsonArray = jsonObject.getJSONArray(Constant_Api.tag);

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject object = jsonArray.getJSONObject(i);
                            String cid = object.getString("cid");
                            String category_name = object.getString("category_name");

                            categoryLists.add(new CategoryList(cid, category_name, null, null, ""));
                        }

                        progressBar.setVisibility(View.GONE);

                        categoryLists.add(0, new CategoryList("", getResources().getString(R.string.selected_category), "", "", ""));

                        // Spinner Drop down elements
                        List<String> categories = new ArrayList<String>();
                        for (int i = 0; i < categoryLists.size(); i++) {
                            categories.add(categoryLists.get(i).getCategory_name());
                        }
                        // Creating adapter for spinner_cat
                        ArrayAdapter<String> dataAdapter_cat = new ArrayAdapter<String>(UploadActivity.this, android.R.layout.simple_spinner_item, categories);
                        // Drop down layout style - list view with radio button
                        dataAdapter_cat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        // attaching data adapter to spinner_cat
                        spinner_cat.setAdapter(dataAdapter_cat);

                        // Creating adapter for spinner video type
                        ArrayAdapter<String> dataAdapter_videoType = new ArrayAdapter<String>(UploadActivity.this, android.R.layout.simple_spinner_item, videoTypeList);
                        // Drop down layout style - list view with radio button
                        dataAdapter_videoType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        // attaching data adapter to video type
                        spinner_videoType.setAdapter(dataAdapter_videoType);

                        //---------------------- code all function -------------------//

                        // Spinner click listener
                        spinner_cat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (position == 0) {
                                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.textView_upload_fragment));
                                    categoryId = categoryLists.get(position).getCid();
                                } else {
                                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.toolbar));
                                    categoryId = categoryLists.get(position).getCid();
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                        // Spinner click listener
                        spinner_videoType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (position == 0) {
                                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.textView_upload_fragment));
                                    videoType = videoTypeList[position];
                                } else {
                                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.toolbar));
                                    videoType = videoTypeList[position];
                                    positionVideoType = position;
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                        linearLayout_image.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (Method.allowPermitionExternalStorage) {
                                    chooseGalleryImage();
                                } else {
                                    Toast.makeText(UploadActivity.this, getResources().getString(R.string.cannot_use_upload_allow), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        linearLayout_video.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (Method.allowPermitionExternalStorage) {
                                    Intent intent_upload = new Intent();
                                    intent_upload.setType("video/mp4");
                                    intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                                    startActivityForResult(intent_upload, REQUEST_CODE_CHOOSE);
                                } else {
                                    Toast.makeText(UploadActivity.this, getResources().getString(R.string.cannot_use_upload_allow), Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                editText.clearFocus();
                                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                                if (Method.isNetworkAvailable(UploadActivity.this)) {
                                    submit_video();
                                } else {
                                    method.alertBox(getResources().getString(R.string.internet_connection));
                                }
                            }
                        });

                        //---------------------- code all function -------------------//

                    }

                    progressBar.setVisibility(View.GONE);

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

            }


            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressBar.setVisibility(View.GONE);
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                try {
                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                    return getDataColumn(context, contentUri, null, null);
                } catch (Exception e) {
                    Log.d("error_data", e.toString());
                }

            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    private long getDurationInt(String filePath) {

        MediaMetadataRetriever metaRetriever_int = new MediaMetadataRetriever();
        metaRetriever_int.setDataSource(filePath);
        String songDuration = metaRetriever_int.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long duration = Long.parseLong(songDuration);
        //int time = (int) (duration % 60000) / 1000;
        // close object
        long time = (int) duration / 1000;
        metaRetriever_int.release();

        return time;
    }

    public void submit_video() {

        String title = editText.getText().toString();

        editText.setError(null);

        if (title.equals("") || title.isEmpty()) {
            editText.requestFocus();
            editText.setError(getResources().getString(R.string.please_enter_title));
        } else if (video_image == null || video_image.equals("") || video_image.isEmpty()) {
            Toast.makeText(UploadActivity.this, getResources().getString(R.string.please_select_image), Toast.LENGTH_SHORT).show();
        } else if (categoryId.equals("") || categoryId.isEmpty()) {
            Toast.makeText(UploadActivity.this, getResources().getString(R.string.please_select_category), Toast.LENGTH_SHORT).show();
        } else if (videoType.equals(getResources().getString(R.string.selected_video_type)) || videoType.isEmpty()) {
            Toast.makeText(UploadActivity.this, getResources().getString(R.string.please_select_videoType), Toast.LENGTH_SHORT).show();
        } else if (videoPath == null || videoPath.equals("") || videoPath.isEmpty()) {
            Toast.makeText(UploadActivity.this, getResources().getString(R.string.please_select_video), Toast.LENGTH_SHORT).show();
        } else {
            if (Method.isNetworkAvailable(UploadActivity.this)) {
                if (method.pref.getBoolean(method.pref_login, false)) {
                    Upload(method.pref.getString(method.profileId, null), categoryId, videoType, title, videoPath, video_image);
                } else {
                    Method.loginBack = true;
                    startActivity(new Intent(UploadActivity.this, Login.class));
                }
            } else {
                method.alertBox(getResources().getString(R.string.internet_connection));
            }
        }

    }

    public void downloadImage(Bitmap bitmap) {

        String iconsStoragePath = getExternalCacheDir().getAbsolutePath();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "image_upload" + n + ".jpg";
        File file = new File(iconsStoragePath, fname);

        //create storage directories, if they don't exist
        if (file.exists()) {
            file.delete();
        }

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);

            BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);

            //choose another format if PNG doesn't suit you
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);

            bos.flush();
            bos.close();

            video_image = file.toString();
            Glide.with(UploadActivity.this).load(file).into(imageView);
            cardView_imageUpload.setVisibility(View.VISIBLE);

        } catch (FileNotFoundException e) {
            Log.w("TAG", "Error saving image file: " + e.getMessage());
            cardView_imageUpload.setVisibility(View.VISIBLE);
        } catch (IOException e) {
            Log.w("TAG", "Error saving image file: " + e.getMessage());
            cardView_imageUpload.setVisibility(View.VISIBLE);
        }
    }


    public void Upload(String user_id, final String cat_id, final String videoType, final String video_title, String video_local, String video_thumbnail) {

        Method.isUpload = false;
        button.setVisibility(View.GONE);

        Toast.makeText(this, getResources().getString(R.string.upload), Toast.LENGTH_SHORT).show();

        String video_type = null;

        if (positionVideoType == 1) {
            video_type = "Landscape";
        } else {
            video_type = "Portrait";
        }


        Intent serviceIntent = new Intent(UploadActivity.this, UploadService.class);
        serviceIntent.setAction(UploadService.ACTION_START);
        serviceIntent.putExtra("uploadUrl", Constant_Api.video_upload_url);
        serviceIntent.putExtra("user_id", user_id);
        serviceIntent.putExtra("cat_id", cat_id);
        serviceIntent.putExtra("videoType", video_type);
        serviceIntent.putExtra("video_title", video_title);
        serviceIntent.putExtra("video_local", video_local);
        serviceIntent.putExtra("video_thumbnail", video_thumbnail);
        serviceIntent.putExtra("layout_type", video_type);
        startService(serviceIntent);

    }

    public void finishUpload() {

        if (editText != null) {
            button.setVisibility(View.VISIBLE);
            editText.setText("");
            categoryId = "";
            videoPath = "";
            videoType = "";
            spinner_cat.setSelection(0);
            spinner_videoType.setSelection(0);
            galleryImages.clear();
            Glide.with(UploadActivity.this).load(R.drawable.placeholder_landscape).into(imageView);
            cardView_imageUpload.setVisibility(View.GONE);
            textView_image.setText(getResources().getString(R.string.no_file_selected));
            textView_video.setTextColor(getResources().getColor(R.color.textView_upload_fragment));
            textView_video.setText(getResources().getString(R.string.no_file_selected_video));
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
