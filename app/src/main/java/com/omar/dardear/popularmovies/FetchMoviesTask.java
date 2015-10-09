package com.omar.dardear.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.omar.dardear.popularmovies.data.MoviesContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Omar on 10/1/2015.
 */
public class FetchMoviesTask extends AsyncTask<String, Void, Void> {


    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
    private Context mContext;

    public FetchMoviesTask(Context mContext) {
        this.mContext = mContext;
    }

    private void getDataFromJson(String MovieJsonStr)
            throws JSONException {

        JSONObject MoviesJson = new JSONObject(MovieJsonStr);
        JSONArray ResultArray = MoviesJson.getJSONArray("results");


        for (int i = 0; i < ResultArray.length(); i++) {

            JSONObject movieTemp = ResultArray.getJSONObject(i);
            String poster_attr = movieTemp.getString("poster_path");

            if (!poster_attr.equals("null")) {
                String original_title = movieTemp.getString("original_title");

                String overview = movieTemp.getString("overview");
                Double vote_average = movieTemp.getDouble("vote_average");
                String release_date = movieTemp.getString("release_date");
                Double movie_id = movieTemp.getDouble("id");

                ContentValues MovieValue = new ContentValues();

                MovieValue.put(MoviesContract.MoviesEntry.COLUMN_TITLE, original_title);
                MovieValue.put(MoviesContract.MoviesEntry.COLUMN_POSTER_ATTR, poster_attr);
                MovieValue.put(MoviesContract.MoviesEntry.COLUMN_OVERVIEW, overview);
                MovieValue.put(MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE, vote_average);
                MovieValue.put(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE, release_date);
                MovieValue.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID, movie_id);
                MovieValue.put(MoviesContract.MoviesEntry.COLUMN_SORT_INDEX, i + 1);


                Cursor Check = mContext.getContentResolver().query(MoviesContract.MoviesEntry.buildMovieUri(movie_id.longValue()), null, null, null, null);
                Check.moveToFirst();
                if (Check != null && Check.getCount() > 0) {

                    mContext.getContentResolver().update(MoviesContract.MoviesEntry.buildMovieUri(movie_id.longValue()), MovieValue, null, null);

                } else {
                    MovieValue.put(MoviesContract.MoviesEntry.COLUMN_FAVOURITE, 0);
                    mContext.getContentResolver().insert(MoviesContract.MoviesEntry.CONTENT_URI, MovieValue);
                }

            }
        }


    }

    @Override
    protected Void doInBackground(String... strings) {





        if (strings.length == 0) {
            return null;
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String MovieJsonStr = null;


        // String MovieUrl =
        // "http://api.themoviedb.org/3/discover/movie?include_adult=no&sort_by=" + strings[0] + ".desc&api_key=9d5b10665b26ce8aadae42604e92f82a";

        String SortingQuery = "sort_by";
        String AdultQuery = "include_adult";
        String ApiKeyQuery = "api_key";


        String MyKey = "9d5b10665b26ce8aadae42604e92f82a";


        Uri builtUri = Uri.parse("http://api.themoviedb.org/3/discover/movie?").buildUpon()
                .appendQueryParameter(SortingQuery, strings[0])
                .appendQueryParameter(AdultQuery, "no")
                .appendQueryParameter(ApiKeyQuery, MyKey)
                .build();

        try {

            URL url = new URL(builtUri.toString());
            Log.v(LOG_TAG, "Built URI " + builtUri.toString());


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
            Log.v(LOG_TAG, "Movie string: " + MovieJsonStr);


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
            getDataFromJson(MovieJsonStr);
            return null;
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }


        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}
