package com.socialgaming.appsclub.Fragment;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.socialgaming.appsclub.Activity.MainActivity;
import com.socialgaming.appsclub.Adapter.DownloadPortraitAdapter;
import com.socialgaming.appsclub.DataBase.DatabaseHandler;
import com.socialgaming.appsclub.Item.SubCategoryList;
import com.socialgaming.appsclub.R;
import com.socialgaming.appsclub.Util.Method;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DownloadPortraitFragment extends Fragment {

    private Method method;
    public Toolbar toolbar;
    private String typeLayout;
    private ProgressBar progressBar;
    private TextView textView_noData_found;
    private RecyclerView recyclerView;
    private FloatingActionButton fab_button;
    private DownloadPortraitAdapter downloadPortraitAdapter;
    private List<SubCategoryList> subCategoryLists;
    private DatabaseHandler db;
    private String root;
    private List<File> inFiles;
    private List<SubCategoryList> downloadListsCompair;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.sub_cat_fragment, container, false);

        MainActivity.toolbar.setTitle(getResources().getString(R.string.my_download));

        method = new Method(getActivity());

        assert getArguments() != null;
        typeLayout = getArguments().getString("typeLayout");

        db = new DatabaseHandler(getActivity());

        inFiles = new ArrayList<>();
        subCategoryLists = new ArrayList<>();
        downloadListsCompair = new ArrayList<>();
        root = Environment.getExternalStorageDirectory() + "/Video_Status/";

        progressBar = view.findViewById(R.id.progressbar_sub_category);
        textView_noData_found = view.findViewById(R.id.textView_sub_category);
        fab_button = view.findViewById(R.id.fab_sub_category);
        recyclerView = view.findViewById(R.id.recyclerView_sub_category);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(layoutManager);
        progressBar.setVisibility(View.GONE);

        fab_button.setImageDrawable(getResources().getDrawable(R.drawable.landscape_ic));

        fab_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadFragment downloadFragment = new DownloadFragment();
                Bundle bundle_fav = new Bundle();
                bundle_fav.putString("typeLayout", "Landscape");
                downloadFragment.setArguments(bundle_fav);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, downloadFragment, getResources().getString(R.string.favorites)).commit();
            }
        });


        new Execute().execute();

        return view;

    }

    @SuppressLint("StaticFieldLeak")
    class Execute extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            subCategoryLists.clear();
            inFiles.clear();
            downloadListsCompair.clear();
            subCategoryLists = db.getVideoDownload(typeLayout);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            File file = new File(root);
            getListFiles(file);
            getDownloadLists(inFiles);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            if (downloadListsCompair.size() == 0) {
                textView_noData_found.setVisibility(View.VISIBLE);
            } else {
                textView_noData_found.setVisibility(View.GONE);
                downloadPortraitAdapter = new DownloadPortraitAdapter(getActivity(), downloadListsCompair);
                recyclerView.setAdapter(downloadPortraitAdapter);
            }
            progressBar.setVisibility(View.GONE);
            super.onPostExecute(s);
        }
    }

    private List<File> getListFiles(File parentDir) {
        try {
            Queue<File> files = new LinkedList<>();
            files.addAll(Arrays.asList(parentDir.listFiles()));
            while (!files.isEmpty()) {
                File file = files.remove();
                if (file.isDirectory()) {
                    files.addAll(Arrays.asList(file.listFiles()));
                } else if (file.getName().endsWith(".mp4")) {
                    inFiles.add(file);
                }
            }
        } catch (Exception e) {
            Log.d("error", e.toString());
        }
        return inFiles;
    }

    private List<SubCategoryList> getDownloadLists(List<File> list) {
        for (int i = 0; i < subCategoryLists.size(); i++) {
            for (int j = 0; j < list.size(); j++) {
                if (list.get(j).toString().contains(subCategoryLists.get(i).getVideo_url())) {
                    downloadListsCompair.add(subCategoryLists.get(i));
                    break;
                } else {
                    if (j == list.size() - 1) {
                        db.delete_video_download(subCategoryLists.get(i).getId());
                    }
                }
            }
        }
        return downloadListsCompair;
    }

}
