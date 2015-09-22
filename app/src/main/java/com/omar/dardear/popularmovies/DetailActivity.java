package com.omar.dardear.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


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
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
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
                    && intent.hasExtra("Date")) {

              ImageView img=(ImageView) rootView.findViewById(R.id.imageView);
             Picasso.with(getActivity())
                     .load("http://image.tmdb.org/t/p/w185/"+intent.getStringExtra("Poster"))
                     .placeholder(R.drawable.loading342)
                     .into(img);

                ((TextView) rootView.findViewById(R.id.original_title))
                        .setText(intent.getStringExtra("Title"));

                ((TextView) rootView.findViewById(R.id.overview))
                        .setText(intent.getStringExtra("Over"));

                ((TextView) rootView.findViewById(R.id.rate))
                        .setText(intent.getStringExtra("Rate")+"/10");

                ((TextView) rootView.findViewById(R.id.date))
                        .setText(intent.getStringExtra("Date"));
            }



            return rootView;
        }
    }
}
