package com.socialgaming.appsclub.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.socialgaming.appsclub.Activity.MainActivity;
import com.socialgaming.appsclub.Adapter.FavAdapter;
import com.socialgaming.appsclub.DataBase.DatabaseHandler;
import com.socialgaming.appsclub.InterFace.InterstitialAdView;
import com.socialgaming.appsclub.Item.SubCategoryList;
import com.socialgaming.appsclub.R;
import com.socialgaming.appsclub.Util.Events;
import com.socialgaming.appsclub.Util.GlobalBus;
import com.socialgaming.appsclub.Util.Method;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class FavouriteFragment extends Fragment {

    private Method method;
    public Toolbar toolbar;
    private String typeLayout;
    private ProgressBar progressBar;
    private TextView textView_noData_found;
    private RecyclerView recyclerView;
    private DatabaseHandler db;
    private List<SubCategoryList> favouriteLists;
    private FavAdapter favAdapter;
    private InterstitialAdView interstitialAdView;
    private FloatingActionButton floatingActionButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.sub_cat_fragment, container, false);

        MainActivity.toolbar.setTitle(getResources().getString(R.string.favorites));

        GlobalBus.getBus().register(this);

        favouriteLists = new ArrayList<>();

        assert getArguments() != null;
        typeLayout = getArguments().getString("typeLayout");

        interstitialAdView = new InterstitialAdView() {
            @Override
            public void position(int position, String type, String id) {
                SCDetailFragment scDetailFragment = new SCDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putString("id", favouriteLists.get(position).getId());
                bundle.putString("type", type);
                bundle.putInt("position", position);
                scDetailFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, scDetailFragment, favouriteLists.get(position).getVideo_title()).addToBackStack(favouriteLists.get(position).getVideo_title()).commitAllowingStateLoss();
            }
        };
        method = new Method(getActivity(), interstitialAdView);

        db = new DatabaseHandler(getActivity());
        favouriteLists = db.getVideoDetailFav(typeLayout);

        floatingActionButton = view.findViewById(R.id.fab_sub_category);
        progressBar = view.findViewById(R.id.progressbar_sub_category);
        textView_noData_found = view.findViewById(R.id.textView_sub_category);
        recyclerView = view.findViewById(R.id.recyclerView_sub_category);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        progressBar.setVisibility(View.GONE);

        floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.portrait_ic));

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FavouritePortraitFragment favouritePortraitFragment = new FavouritePortraitFragment();
                Bundle bundle_fav = new Bundle();
                bundle_fav.putString("typeLayout", "Portrait");
                favouritePortraitFragment.setArguments(bundle_fav);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, favouritePortraitFragment, getResources().getString(R.string.favorites)).commit();
            }
        });

        setData();

        return view;

    }

    private void setData() {
        if (favouriteLists.size() == 0) {
            textView_noData_found.setVisibility(View.VISIBLE);
        } else {
            textView_noData_found.setVisibility(View.GONE);
            favAdapter = new FavAdapter(getActivity(), favouriteLists, interstitialAdView, "favorites");
            recyclerView.setAdapter(favAdapter);
        }
    }

    @Subscribe
    public void getNotify(Events.FavouriteNotify favouriteNotify) {
        if (favAdapter != null) {
            db = new DatabaseHandler(getActivity());
            favouriteLists.clear();
            favouriteLists = db.getVideoDetailFav(typeLayout);
            setData();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Unregister the registered event.
        GlobalBus.getBus().unregister(this);
    }

}
