package com.omar.dardear.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.omar.dardear.popularmovies.data.MoviesContract;
import com.omar.dardear.popularmovies.data.MoviesProvider;

/**
 * Created by Omar on 9/5/2015.
 */
public class PostersFragement extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, SwipeRefreshLayout.OnRefreshListener {


    PicassoCursorAdapter PicassoCAdapter;
    GridView gridView;

    CoordinatorLayout coordinatorLayout;
    SwipeRefreshLayout swipeRefreshLayout;

    private static final String SELECTED_KEY = "selected_position";

    private int mPosition = GridView.INVALID_POSITION;

    private static final int MOVIES_LOADER = 0;
    private static final String[] MOVIES_COLUMNS = {

            MoviesContract.MoviesEntry.TABLE_NAME + "." + MoviesContract.MoviesEntry._ID,
            MoviesContract.MoviesEntry.COLUMN_MOVIE_ID,
            MoviesContract.MoviesEntry.COLUMN_TITLE,
            MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE,
            MoviesContract.MoviesEntry.COLUMN_OVERVIEW,
            MoviesContract.MoviesEntry.COLUMN_POSTER_ATTR,
            MoviesContract.MoviesEntry.COLUMN_RUN_TIME,
            MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE,
            MoviesContract.MoviesEntry.COLUMN_FAVOURITE,
            MoviesContract.MoviesEntry.COLUMN_SORT_INDEX
    };


    static final int COL_ID = 0;
    static final int COL_MOVIE_ID = 1;
    static final int COL_TITLE = 2;
    static final int COL_VOTE_AVERAGE = 3;
    static final int COL_OVERVIEW = 4;
    static final int COL_POSTER_ATTR = 5;
    static final int COL_RUN_TIME = 6;
    static final int COL_RELEASE_DATE = 7;


    public PostersFragement() {
    }


    public interface Callback {

        public void onItemSelected(Uri dateUri);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.poster_fragement_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = cm.getActiveNetworkInfo();
            if (ni == null) {
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "NO INTERNET CONNECTION", Snackbar.LENGTH_LONG);

                snackbar.show();


            } else {
                updateMovies();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        coordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id
                .coordinatorLayout);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);


        gridView = (GridView) rootView.findViewById(R.id.GridView_Imgs);
        PicassoCAdapter = new PicassoCursorAdapter(getActivity(), null, 0);
        gridView.setAdapter(PicassoCAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {

                    Toast.makeText(getActivity(), cursor.getString(COL_TITLE), Toast.LENGTH_SHORT).show();
                    ((Callback) getActivity())
                            .onItemSelected(MoviesContract.MoviesEntry.buildMovieUri(Long.valueOf(cursor.getString(COL_MOVIE_ID))

                            ));
                    mPosition = position;
                }


            }
        });
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                                        NetworkInfo ni = cm.getActiveNetworkInfo();
                                        if (ni == null) {
                                            Snackbar snackbar = Snackbar
                                                    .make(coordinatorLayout, "NO INTERNET CONNECTION", Snackbar.LENGTH_LONG);

                                            snackbar.show();


                                        } else {
                                            updateMovies();
                                        }

                                        swipeRefreshLayout.setRefreshing(false);
                                    }
                                }
        );

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {

            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }


        return rootView;
    }

    @Override
    public void onRefresh() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "NO INTERNET CONNECTION", Snackbar.LENGTH_LONG);

            snackbar.show();


        } else {
            updateMovies();
        }
        swipeRefreshLayout.setRefreshing(false);

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {

        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    private void updateMovies() {
        Utility.ResetMovieTable(getActivity());
        String Order = Utility.getOrder(getActivity());
        if (!Order.equals(getString(R.string.pref_sort_Favorite))) {
            ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = cm.getActiveNetworkInfo();
            if (ni == null) {
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "NO INTERNET CONNECTION", Snackbar.LENGTH_LONG);
                snackbar.show();
            } else {
                Intent i = new Intent(getActivity(), MovieService.class);
                i.putExtra(MovieService.Order_Query, Order);
                getActivity().startService(i);
            }

        }

    }


    void onOrderChanged() {
        updateMovies();
        mPosition = GridView.INVALID_POSITION;
        getLoaderManager().restartLoader(MOVIES_LOADER, null, this);
        ((Callback) getActivity())
                .onItemSelected(null);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort = prefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_pop));
        if (sort.equals(getString(R.string.pref_sort_Favorite))) {
            Uri GetAllMoviesUri = MoviesContract.MoviesEntry.CONTENT_URI;

            return new CursorLoader(getActivity(), GetAllMoviesUri, MOVIES_COLUMNS,
                    MoviesProvider.sItemFavouriteAndOrderSelection, new String[]{"1", "0"}, null);


        } else {
            Uri GetAllMoviesUri = MoviesContract.MoviesEntry.CONTENT_URI;


            return new CursorLoader(getActivity(), GetAllMoviesUri,
                    MOVIES_COLUMNS, MoviesProvider.sItemOrderSelection, new String[]{"1"},
                    MoviesContract.MoviesEntry.COLUMN_SORT_INDEX + " ASC");

        }


    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        PicassoCAdapter.swapCursor(data);
        gridView.setAdapter(PicassoCAdapter);

        if (mPosition != GridView.INVALID_POSITION) {

            gridView.smoothScrollToPosition(mPosition);
//            gridView.setSelection(mPosition);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        PicassoCAdapter.swapCursor(null);
    }


}
