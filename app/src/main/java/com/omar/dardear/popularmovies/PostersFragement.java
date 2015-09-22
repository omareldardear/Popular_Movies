package com.omar.dardear.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

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
 * Created by Omar on 9/5/2015.
 */
public class PostersFragement extends Fragment {
    public PostersFragement() {

    }

    private PicassoAdapter adapter;
    private ArrayList<Movie> MoviesData=new ArrayList<Movie>();

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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateMovies();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        GridView gridView = (GridView) rootView.findViewById(R.id.GridView_Imgs);

        ArrayList<String> urls = new ArrayList<String>();




         adapter = new PicassoAdapter(getActivity(), urls);

        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Toast.makeText(getActivity(),MoviesData.get(position).getOriginal_title() , Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra("Title", MoviesData.get(position).getOriginal_title())
                        .putExtra("Poster", MoviesData.get(position).getPoster_attr())
                        .putExtra("Over", MoviesData.get(position).getOverview())
                        .putExtra("Rate", MoviesData.get(position).getVote_average())
                        .putExtra("Date", MoviesData.get(position).getRelease_date());
                startActivity(intent);
            }
        });




        return rootView;
    }
    private void updateMovies()
    {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni==null)
        {

        }
        else {
            FetchMoviesTask weatherTask = new FetchMoviesTask();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String sort = prefs.getString(getString(R.string.pref_sort_key),
                    getString(R.string.pref_sort_pop));
            weatherTask.execute(sort);
        }

    }


    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }



    public class FetchMoviesTask extends AsyncTask<String, Void, Movie[]> {


        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        private Movie[] getDataFromJson(String MovieJsonStr)
                throws JSONException {


            JSONObject MoviesJson = new JSONObject(MovieJsonStr);
            JSONArray ResultArray = MoviesJson.getJSONArray("results");

            Movie[] resultObject = new Movie[ResultArray.length()];

            for (int i = 0; i < ResultArray.length(); i++) {

                JSONObject movieTemp = ResultArray.getJSONObject(i);
                String original_title = movieTemp.getString("original_title");
                String poster_attr = movieTemp.getString("poster_path");
                String overview = movieTemp.getString("overview");
                String vote_average = Double.toString(movieTemp.getDouble("vote_average"));
                String release_date = movieTemp.getString("release_date");

                resultObject[i] = new Movie(original_title, poster_attr, overview, vote_average, release_date);



            }
            return resultObject;

        }

        @Override
        protected Movie[] doInBackground(String... strings) {

            if (strings.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String MovieJsonStr = null;


           // String MovieUrl =
           // "http://api.themoviedb.org/3/discover/movie?include_adult=no&sort_by=" + strings[0] + ".desc&api_key=9d5b10665b26ce8aadae42604e92f82a";

            String SortingQuery="sort_by";
            String AdultQuery="include_adult";
            String ApiKeyQuery="api_key";


            String MyKey="9d5b10665b26ce8aadae42604e92f82a";


            Uri builtUri = Uri.parse("http://api.themoviedb.org/3/discover/movie?").buildUpon()
                    .appendQueryParameter(SortingQuery, strings[0] )
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
                return getDataFromJson(MovieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }


        @Override
        protected void onPostExecute(Movie[] movies) {


            if (movies != null) {


                MoviesData.clear();
                adapter.clear();
                for(Movie movieTemp : movies) {
                    adapter.add("http://image.tmdb.org/t/p/w185/"+movieTemp.getPoster_attr());
                    MoviesData.add(movieTemp);
                }

            }


        }
    }


}
