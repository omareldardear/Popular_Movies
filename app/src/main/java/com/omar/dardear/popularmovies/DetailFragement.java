package com.omar.dardear.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.omar.dardear.popularmovies.data.MoviesContract;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Omar on 10/3/2015.
 */
public class DetailFragement extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private ArrayList<Trailer> TrailersData = new ArrayList<Trailer>();
    private ArrayList<Review> ReviewsData = new ArrayList<Review>();
    private ShareActionProvider mShareActionProvider;
    private String mShare;
    private String ID;
    private Long ID_long;

    private ArrayAdapter<String> TrailersAdapter;
    private ArrayAdapter<String> ReviewsAdapter;

    private int Favourite;
    private Button Favourite_Button;
    private TextView RunTime;
    private TextView Title;
    private TextView Year;
    private TextView Rating;
    private TextView Overview;
    private ImageView Poster;

    private static final int DETAIL_LOADER = 0;
    static final String DETAIL_URI = "URI";
    private Uri mUri;


    public DetailFragement() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragement_detail, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);


        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
//        mShareActionProvider.setShareHistoryFileName(null);


        if (mShare != null) {
            mShareActionProvider.setShareIntent(createShareTrailerIntent());
        }
    }


    private Intent createShareTrailerIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mShare);
        return shareIntent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DETAIL_URI);
            Cursor Det = getActivity().getContentResolver().query(mUri, null, null, null, null);
            Det.moveToFirst();

            if (Det != null && Det.getCount() > 0) {
                ID = Det.getString(Det.getColumnIndexOrThrow(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID));
            }
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);


            Title = ((TextView) rootView.findViewById(R.id.original_title));
            Overview = ((TextView) rootView.findViewById(R.id.overview));
            Rating = ((TextView) rootView.findViewById(R.id.rate));
            Year = ((TextView) rootView.findViewById(R.id.date));
            RunTime = (TextView) rootView.findViewById(R.id.runTime);
            Favourite_Button = (Button) rootView.findViewById(R.id.favourite);
            Poster = (ImageView) rootView.findViewById(R.id.imageView);


            TrailersAdapter = new ArrayAdapter<String>(getActivity(), R.layout.trailer_item, R.id.trailerName);
            ExpandableHeightListView TrailersList = (ExpandableHeightListView) rootView.findViewById(R.id.TrailersList);
            TrailersList.setExpanded(true);
            TrailersList.setAdapter(TrailersAdapter);

            TrailersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(TrailersData.get(position).getLink()));
                    startActivity(intent);

                }
            });

            Favourite_Button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (Favourite == 0) {
                        Favourite = 1;
                        ContentValues MovieValue = new ContentValues();
                        MovieValue.put(MoviesContract.MoviesEntry.COLUMN_FAVOURITE, 1);
                        int updated = getActivity().getContentResolver().update(MoviesContract.MoviesEntry.buildMovieUri(Long.valueOf(ID)), MovieValue, null, null);

                        Favourite_Button.setText("Delete From\nFavourites");


                    } else {
                        Favourite = 0;
                        ContentValues MovieValue = new ContentValues();
                        MovieValue.put(MoviesContract.MoviesEntry.COLUMN_FAVOURITE, 0);
                        int updated = getActivity().getContentResolver().update(MoviesContract.MoviesEntry.buildMovieUri(Long.valueOf(ID)), MovieValue, null, null);

                        Favourite_Button.setText("Mark As\nFavourite");


                    }
                }
            });


            ReviewsAdapter = new ArrayAdapter<String>(getActivity(), R.layout.review_item, R.id.reviewName);
            ExpandableHeightListView ReviewsList = (ExpandableHeightListView) rootView.findViewById(R.id.ReviewsList);
            ReviewsList.setExpanded(true);
            ReviewsList.setAdapter(ReviewsAdapter);

            FetchDetailsTask DetailTask = new FetchDetailsTask();
            DetailTask.execute(ID);
            FetchTrailersTask TrailersTask = new FetchTrailersTask();
            TrailersTask.execute(ID);
            FetchReviewsTask ReviewTask = new FetchReviewsTask();
            ReviewTask.execute(ID);


            return rootView;
        }

        return null;
    }

    void onOrderChanged(String newOrder) {
        Uri uri = mUri;
        if (null != uri) {
            mUri = null;
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != mUri) {
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    null,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        data.moveToFirst();
        if (data != null && data.getCount() > 0) {
            ID = data.getString(data.getColumnIndexOrThrow(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID));
            Picasso.with(getActivity())
                    .load("http://image.tmdb.org/t/p/w185/" + data.getString(data.getColumnIndexOrThrow(MoviesContract.MoviesEntry.COLUMN_POSTER_ATTR)))
                    .placeholder(R.drawable.loading342)
                    .into(Poster);
            Title.setText(data.getString(data.getColumnIndexOrThrow(MoviesContract.MoviesEntry.COLUMN_TITLE)));
            Overview.setText(data.getString(data.getColumnIndexOrThrow(MoviesContract.MoviesEntry.COLUMN_OVERVIEW)));
            Rating.setText(data.getString(data.getColumnIndexOrThrow(MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE)) + "/10");
            Year.setText(data.getString(data.getColumnIndexOrThrow(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE)).substring(0, 4));
            String RunTimeTemp = data.getString(data.getColumnIndexOrThrow(MoviesContract.MoviesEntry.COLUMN_RUN_TIME));
            if (RunTimeTemp == null) {
                RunTime.setText(" ");
            } else {
                RunTime.setText(RunTimeTemp + "min");
            }

            Favourite = data.getInt(data.getColumnIndexOrThrow(MoviesContract.MoviesEntry.COLUMN_FAVOURITE));
            if (Favourite == 0) {
                Favourite_Button.setText("Mark As\nFavourite");
            } else {
                Favourite_Button.setText("Delete From\nFavourites");
            }

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    public class FetchTrailersTask extends AsyncTask<String, Void, Trailer[]> {


        private final String LOG_TAG = FetchTrailersTask.class.getSimpleName();

        private Trailer[] getDataFromJson(String TrailersJsonStr)
                throws JSONException {


            JSONObject MovieJson = new JSONObject(TrailersJsonStr);
            JSONArray ResultArray = MovieJson.getJSONArray("results");

            Trailer[] resultTempObject = new Trailer[ResultArray.length()];
            int s = 0;
            for (int i = 0; i < ResultArray.length(); i++) {

                JSONObject TrailersTemp = ResultArray.getJSONObject(i);
                String site = TrailersTemp.getString("site");
                String Type = TrailersTemp.getString("type");


                if (site.equals("YouTube") && Type.equals("Trailer")) {
                    resultTempObject[s] = new Trailer(TrailersTemp.getString("name"), TrailersTemp.getString("key"));
                    s++;

                }


            }
            Trailer[] resultObject = new Trailer[s];
            for (int i = 0; i < s; i++) {

                resultObject[i] = new Trailer(resultTempObject[i].getName(), resultTempObject[i].getKey());


            }
            return resultObject;

        }

        @Override
        protected Trailer[] doInBackground(String... strings) {

            if (strings.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String MovieJsonStr = null;


            String TrailersUrl =
                    "http://api.themoviedb.org/3/movie/" + strings[0] + "/videos?api_key=9d5b10665b26ce8aadae42604e92f82a";

//                String SortingQuery="sort_by";
//                String AdultQuery="include_adult";
//                String ApiKeyQuery="api_key";
//
//
//                String MyKey="9d5b10665b26ce8aadae42604e92f82a";
//
//
//                Uri builtUri = Uri.parse("http://api.themoviedb.org/3/discover/movie?").buildUpon()
//                        .appendQueryParameter(SortingQuery, strings[0] )
//                        .appendQueryParameter(AdultQuery, "no")
//                        .appendQueryParameter(ApiKeyQuery, MyKey)
//                        .build();

            try {

                URL url = new URL(TrailersUrl);
                Log.v(LOG_TAG, "Built URI " + TrailersUrl);

                Log.v("LINK", "Built URI " + TrailersUrl);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();


                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                MovieJsonStr = buffer.toString();
                Log.v(LOG_TAG, "Trailers string: " + MovieJsonStr);


            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getDataFromJson(MovieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }


        @Override
        protected void onPostExecute(Trailer[] Trailers) {


            if (Trailers != null) {


                TrailersData.clear();
                TrailersAdapter.clear();
                if (Trailers.length > 0) {
                    mShare = Trailers[0].getLink();
                    if (mShareActionProvider != null) {
//                        mShareActionProvider.setShareHistoryFileName(null);
                        mShareActionProvider.setShareIntent(createShareTrailerIntent());
                    }
                }


                for (Trailer TrailerTemp : Trailers) {

                    TrailersData.add(TrailerTemp);
                    TrailersAdapter.add(TrailerTemp.getName());


                }

            }


        }
    }

    public class FetchReviewsTask extends AsyncTask<String, Void, Review[]> {


        private final String LOG_TAG = FetchReviewsTask.class.getSimpleName();

        private Review[] getDataFromJson(String TrailersJsonStr)
                throws JSONException {


            JSONObject MovieJson = new JSONObject(TrailersJsonStr);
            JSONArray ResultArray = MovieJson.getJSONArray("results");

            Review[] resultObject = new Review[ResultArray.length()];

            for (int i = 0; i < ResultArray.length(); i++) {

                JSONObject ReviewsTemp = ResultArray.getJSONObject(i);
                String author = ReviewsTemp.getString("author");
                String content = ReviewsTemp.getString("content");

                resultObject[i] = new Review(author, content);


            }
            return resultObject;

        }

        @Override
        protected Review[] doInBackground(String... strings) {

            if (strings.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String MovieJsonStr = null;


            String ReviewsUrl =
                    "http://api.themoviedb.org/3/movie/" + strings[0] + "/reviews?api_key=9d5b10665b26ce8aadae42604e92f82a";

//                String SortingQuery="sort_by";
//                String AdultQuery="include_adult";
//                String ApiKeyQuery="api_key";
//
//
//                String MyKey="9d5b10665b26ce8aadae42604e92f82a";
//
//
//                Uri builtUri = Uri.parse("http://api.themoviedb.org/3/discover/movie?").buildUpon()
//                        .appendQueryParameter(SortingQuery, strings[0] )
//                        .appendQueryParameter(AdultQuery, "no")
//                        .appendQueryParameter(ApiKeyQuery, MyKey)
//                        .build();

            try {

                URL url = new URL(ReviewsUrl);
                Log.v(LOG_TAG, "Built URI " + ReviewsUrl);

                Log.v("LINK", "Built URI " + ReviewsUrl);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();


                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                MovieJsonStr = buffer.toString();
                Log.v(LOG_TAG, "Reviews string: " + MovieJsonStr);


            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getDataFromJson(MovieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }


        @Override
        protected void onPostExecute(Review[] Reviews) {


            if (Reviews != null) {


                ReviewsData.clear();
                ReviewsAdapter.clear();

                for (Review ReviewTemp : Reviews) {

                    ReviewsData.add(ReviewTemp);
                    ReviewsAdapter.add(ReviewTemp.getAuthor() + ":\n" + ReviewTemp.getContent());


                }

            }


        }
    }


    public class FetchDetailsTask extends AsyncTask<String, Void, int[]> {


        private final String LOG_TAG = FetchDetailsTask.class.getSimpleName();

        private int getDataFromJson(String DetailsJsonStr)
                throws JSONException {


            JSONObject MovieJson = new JSONObject(DetailsJsonStr);
            int runTime = MovieJson.getInt("runtime");

            Log.v(LOG_TAG, "RUN TIME  " + runTime);


            return runTime;

        }

        @Override
        protected int[] doInBackground(String... strings) {

            if (strings.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String MovieJsonStr = null;


            String DetailsUrl =
                    "http://api.themoviedb.org/3/movie/" + strings[0] + "?api_key=9d5b10665b26ce8aadae42604e92f82a";

            try {

                URL url = new URL(DetailsUrl);
                Log.v(LOG_TAG, "Built URI " + DetailsUrl);

                Log.v("run", "Built URI " + DetailsUrl);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();


                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                MovieJsonStr = buffer.toString();
                Log.v(LOG_TAG, "Details string: " + MovieJsonStr);


            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return new int[]{getDataFromJson(MovieJsonStr)};
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }


        @Override
        protected void onPostExecute(int[] runTime) {
            if (runTime != null && runTime.length > 0) {
                RunTime.setText(runTime[0] + "min");
                ContentValues MovieValue = new ContentValues();

                MovieValue.put(MoviesContract.MoviesEntry.COLUMN_RUN_TIME, runTime[0]);


                MovieValue.put(MoviesContract.MoviesEntry.COLUMN_RUN_TIME, runTime[0]);
                int updated = getActivity().getContentResolver().update(MoviesContract.MoviesEntry.buildMovieUri(Long.valueOf(ID)), MovieValue, null, null);
                Log.v("updateeee", " " + updated);


            }

        }
    }
}