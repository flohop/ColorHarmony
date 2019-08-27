package com.example.colorharmony;

import android.database.Cursor;
import android.os.AsyncTask;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.ListFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class FavoriteColorsFragment extends ListFragment {
    private SQLiteHelper db=null;
    private Cursor current = null;
    private MyCursorAdapter adapter;
    private AsyncTask task = null;
    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.favorite_colors_fragment, container, false);

        listView = (ListView) rootView.findViewById(android.R.id.list);

        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();


        listView = (ListView) getView().findViewById(android.R.id.list);
        EventBus.getDefault().register(this);
        db = SQLiteHelper.getInstance(getActivity());
        db.loadFavorites();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFavoritesLoaded(FavoritesLoadedEvent event){
            current = event.getCursor();
            adapter = new MyCursorAdapter(getActivity(), current, 0);
            listView.setAdapter(adapter);
    }
}
