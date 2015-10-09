package com.omar.dardear.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.omar.dardear.popularmovies.data.MoviesContract;
import com.omar.dardear.popularmovies.data.MoviesProvider;

/**
 * Created by Omar on 10/9/2015.
 */
public class Utility {

    public static String getOrder(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_sort_key),
                context.getString(R.string.pref_sort_pop));
    }
    public static void ResetMovieTable(Context context) {
        context.getContentResolver().delete(MoviesContract.MoviesEntry.CONTENT_URI,
                MoviesProvider.sItemFavouriteSelection,
                new String[]{"0"});

        ContentValues MovieValue = new ContentValues();
        MovieValue.put(MoviesContract.MoviesEntry.COLUMN_SORT_INDEX, 0);
        context.getContentResolver().update(MoviesContract.MoviesEntry.CONTENT_URI, MovieValue, null, null);
    }
}
