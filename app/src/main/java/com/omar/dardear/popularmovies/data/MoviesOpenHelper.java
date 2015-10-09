package com.omar.dardear.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.omar.dardear.popularmovies.data.MoviesContract.MoviesEntry;
import com.omar.dardear.popularmovies.data.MoviesContract.TrailersEntry;
import com.omar.dardear.popularmovies.data.MoviesContract.ReviewsEntry;


/**
 * Created by Omar on 9/27/2015.
 */
public class MoviesOpenHelper extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "MainMovies.db";

    public MoviesOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {


        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + MoviesEntry.TABLE_NAME + " (" +
                MoviesEntry._ID + " INTEGER PRIMARY KEY ," +
                MoviesEntry.COLUMN_MOVIE_ID + " DOUBLE UNIQUE NOT NULL, " +
                MoviesEntry.COLUMN_TITLE + " VARCHAR, " +
                MoviesEntry.COLUMN_VOTE_AVERAGE + " FLOAT, " +
                MoviesEntry.COLUMN_OVERVIEW + " VARCHAR, " +
                MoviesEntry.COLUMN_POSTER_ATTR + " VARCHAR, " +
                MoviesEntry.COLUMN_RUN_TIME + " INTEGER, " +
                MoviesEntry.COLUMN_RELEASE_DATE + " VARCHAR, " +
                MoviesEntry.COLUMN_FAVOURITE + " INTEGER NOT NULL DEFAULT 0, " +
                MoviesEntry.COLUMN_SORT_INDEX + " INTEGER NOT NULL DEFAULT 0 " +
                " );";


        final String SQL_CREATE_TRAILERS_TABLE = "CREATE TABLE " + TrailersEntry.TABLE_NAME + " (" +
                TrailersEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                TrailersEntry.COLUMN_NAME + " VARCHAR NOT NULL, " +
                TrailersEntry.COLUMN_LINK + " VARCHAR NOT NULL, " +
                TrailersEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +

                " FOREIGN KEY (" + TrailersEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MoviesEntry.TABLE_NAME + " (" + MoviesEntry._ID + ")); ";


        final String SQL_CREATE_REVIEWS_TABLE = "CREATE TABLE " + ReviewsEntry.TABLE_NAME + " (" +
                ReviewsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ReviewsEntry.COLUMN_AUTHOR + " VARCHAR NOT NULL, " +
                ReviewsEntry.COLUMN_CONTENT + " VARCHAR NOT NULL, " +
                ReviewsEntry.COLUMN_URL + " VARCHAR NOT NULL, " +
                ReviewsEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +


                " FOREIGN KEY (" + ReviewsEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MoviesEntry.TABLE_NAME + " (" + ReviewsEntry._ID + ")); ";


        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TRAILERS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEWS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TrailersEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ReviewsEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}