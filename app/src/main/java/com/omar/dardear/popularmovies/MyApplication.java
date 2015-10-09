package com.omar.dardear.popularmovies;

import android.app.Application;
import android.content.Intent;

/**
 * Created by Omar on 10/9/2015.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();


        String Order = Utility.getOrder(getApplicationContext());
        if (!Order.equals(getString(R.string.pref_sort_Favorite))) {
            Intent i = new Intent(this, MovieService.class);
            i.putExtra(MovieService.Order_Query, Order);
            startService(i);
        }

    }

}

