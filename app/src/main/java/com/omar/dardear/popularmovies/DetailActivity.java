package com.omar.dardear.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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


public class DetailActivity extends ActionBarActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private ArrayList<Trailer> TrailersData=new ArrayList<Trailer>();
        String ID;

        public PlaceholderFragment() {
        }

        @Override
        public void onStart() {
            super.onStart();

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            Intent intent = getActivity().getIntent();
            if (intent != null
                    && intent.hasExtra("Title")
                    && intent.hasExtra("Poster")
                    && intent.hasExtra("Over")
                    && intent.hasExtra("Rate")
                    && intent.hasExtra("Date")
                    && intent.hasExtra("Movie_ID")) {

                FetchTrailersTask TrailersTask = new FetchTrailersTask();
                TrailersTask.execute(intent.getStringExtra("Movie_ID"));

                ImageView img = (ImageView) rootView.findViewById(R.id.imageView);
                Picasso.with(getActivity())
                        .load("http://image.tmdb.org/t/p/w185/" + intent.getStringExtra("Poster"))
                        .placeholder(R.drawable.loading342)
                        .into(img);

                ((TextView) rootView.findViewById(R.id.original_title))
                        .setText(intent.getStringExtra("Title"));

                ((TextView) rootView.findViewById(R.id.overview))
                        .setText(intent.getStringExtra("Over"));

                ((TextView) rootView.findViewById(R.id.rate))
                        .setText(intent.getStringExtra("Rate") + "/10");

                ((TextView) rootView.findViewById(R.id.date))
                        .setText(intent.getStringExtra("Date").substring(0, 4));
            }


            return rootView;
        }


        public class FetchTrailersTask extends AsyncTask<String, Void, Trailer[]> {


            private final String LOG_TAG = FetchTrailersTask.class.getSimpleName();

            private Trailer[] getDataFromJson(String TrailersJsonStr)
                    throws JSONException {


                JSONObject MovieJson = new JSONObject(TrailersJsonStr);
                JSONArray ResultArray = MovieJson.getJSONArray("results");

                Trailer[] resultObject = new Trailer[ResultArray.length()];

                for (int i = 0; i < ResultArray.length(); i++) {

                    JSONObject TrailersTemp = ResultArray.getJSONObject(i);
                    String site = TrailersTemp.getString("site");

                    if (site.equals("YouTube"))
                    {
                        resultObject[i]=new Trailer(TrailersTemp.getString("name"),TrailersTemp.getString("key"));

                        Log.v("LINKTAG", "TEST     "+resultObject[i].getName()+"    "+resultObject[i].getLink());

                    }
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

                    for(Trailer TrailerTemp : Trailers) {

                        TrailersData.add(TrailerTemp);

                    }

                }


            }
        }
    }
}
