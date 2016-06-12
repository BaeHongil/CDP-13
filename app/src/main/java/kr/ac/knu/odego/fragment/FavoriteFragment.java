package kr.ac.knu.odego.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import io.realm.Realm;
import io.realm.RealmResults;
import kr.ac.knu.odego.OdegoApplication;
import kr.ac.knu.odego.R;
import kr.ac.knu.odego.adapter.FavoriteListAdapter;
import kr.ac.knu.odego.item.Favorite;

/**
 * Created by BHI on 2016-05-28.
 */
public class FavoriteFragment extends Fragment {

    private Realm mRealm;
    private FavoriteListAdapter mFavoriteListAdapter;
    private LinearLayout rootView;

    @Override
    public void onStart() {
        Log.i(getClass().getSimpleName(), OdegoApplication.getMethodName(Thread.currentThread().getStackTrace()));
        super.onStart();
        mRealm = Realm.getDefaultInstance();
        RealmResults<Favorite> results = mRealm.where(Favorite.class).findAllSorted("index");
        mFavoriteListAdapter.updateData(results);
    }

    @Override
    public void onStop() {
        Log.i(getClass().getSimpleName(), OdegoApplication.getMethodName(Thread.currentThread().getStackTrace()));
        super.onStop();
        if(mRealm != null)
            mRealm.close();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(getClass().getSimpleName(), OdegoApplication.getMethodName(Thread.currentThread().getStackTrace()));
        rootView = (LinearLayout) inflater.inflate(R.layout.fragment_favorite_list, container, false);

        mFavoriteListAdapter = new FavoriteListAdapter(this, null, true);
        RecyclerView favoriteListView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        favoriteListView.setLayoutManager( new LinearLayoutManager(getContext()) );
        favoriteListView.setAdapter(mFavoriteListAdapter);

        return rootView;
    }

    public Realm getmRealm() {
        return mRealm;
    }
}
