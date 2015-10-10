package com.omar.dardear.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity implements PostersFragement.Callback {

    static Context context;
    private boolean mTwoPane;
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private String mSort;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        mSort = mPrefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_pop));

        if (findViewById(R.id.movie_detail_container) != null) {

            mTwoPane = true;

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailFragement(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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


    @Override
    public void onItemSelected(Uri dateUri) {
        if (mTwoPane) {
            if (dateUri != null) {
                Bundle args = new Bundle();
                args.putParcelable(DetailFragement.DETAIL_URI, dateUri);

                DetailFragement fragment = new DetailFragement();
                fragment.setArguments(args);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
                        .commit();

            } else {
                DetailFragement fragment = new DetailFragement();
                fragment.setArguments(null);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
                        .commit();

            }


        } else {
            if (dateUri != null) {
                Intent intent = new Intent(this, DetailActivity.class)
                        .setData(dateUri);
                startActivity(intent);
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String sort = mPrefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_pop));
        if (!sort.equals(mSort)) {

            PostersFragement PF = (PostersFragement) getSupportFragmentManager().findFragmentById(R.id.fragment_poster);
            if (PF != null) {
                PF.onOrderChanged();
            }
            DetailFragement df = (DetailFragement) getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
            if (df != null) {
                df.onOrderChanged(sort);
            }

        }
        mSort = sort;
    }

}

