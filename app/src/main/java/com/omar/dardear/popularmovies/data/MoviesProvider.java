package com.omar.dardear.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by Omar on 9/27/2015.
 */
public class MoviesProvider extends ContentProvider {


    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MoviesOpenHelper mOpenHelper;

    static final int MOVIES = 100;
    static final int TRAILERS = 200;
    static final int REVIEWS = 300;

    static final int MOVIE_WITH_ID = 101;
    static final int TRAILER_WITH_MOVIE = 201;
    static final int REVIEW_WITH_MOVIE = 301;

    private static final SQLiteQueryBuilder sTrailerByMovieID;
    private static final SQLiteQueryBuilder sReviewByMovieID;


    static {
        sTrailerByMovieID = new SQLiteQueryBuilder();

        sTrailerByMovieID.setTables(
                MoviesContract.TrailersEntry.TABLE_NAME + " INNER JOIN " +
                        MoviesContract.MoviesEntry.TABLE_NAME +
                        " ON " + MoviesContract.TrailersEntry.TABLE_NAME +
                        "." + MoviesContract.TrailersEntry.COLUMN_MOVIE_ID +
                        " = " + MoviesContract.MoviesEntry.TABLE_NAME +
                        "." + MoviesContract.MoviesEntry._ID);
    }

    static {
        sReviewByMovieID = new SQLiteQueryBuilder();

        sReviewByMovieID.setTables(
                MoviesContract.ReviewsEntry.TABLE_NAME + " INNER JOIN " +
                        MoviesContract.MoviesEntry.TABLE_NAME +
                        " ON " + MoviesContract.ReviewsEntry.TABLE_NAME +
                        "." + MoviesContract.ReviewsEntry.COLUMN_MOVIE_ID +
                        " = " + MoviesContract.MoviesEntry.TABLE_NAME +
                        "." + MoviesContract.MoviesEntry._ID);
    }


    public static final String sMovieIDSelection =
            MoviesContract.MoviesEntry.TABLE_NAME +
                    "." + MoviesContract.MoviesEntry._ID + " = ? ";


    public static final String sItemMovieIDSelection =
            MoviesContract.MoviesEntry.COLUMN_MOVIE_ID + " = ? ";

    public static final String sItemFavouriteSelection =
            MoviesContract.MoviesEntry.COLUMN_FAVOURITE + " = ? ";

    public static final String sItemOrderSelection =
            MoviesContract.MoviesEntry.COLUMN_SORT_INDEX + " >= ? ";

    public static final String sItemFavouriteAndOrderSelection =
            MoviesContract.MoviesEntry.COLUMN_FAVOURITE + " = ? AND " +
                    MoviesContract.MoviesEntry.COLUMN_SORT_INDEX + " >= ? ";


    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, MoviesContract.PATH_MOVIES, MOVIES);
        matcher.addURI(authority, MoviesContract.PATH_MOVIES + "/#", MOVIE_WITH_ID);

        matcher.addURI(authority, MoviesContract.PATH_TRAILERS, TRAILERS);
        matcher.addURI(authority, MoviesContract.PATH_TRAILERS + "/#", TRAILER_WITH_MOVIE);

        matcher.addURI(authority, MoviesContract.PATH_REVIEWS, REVIEWS);
        matcher.addURI(authority, MoviesContract.PATH_REVIEWS + "/#", REVIEW_WITH_MOVIE);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MoviesOpenHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "weather/*/*"
            case MOVIES: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MoviesContract.MoviesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "weather/*"
            case MOVIE_WITH_ID: {
                selectionArgs = new String[]{Long.toString(MoviesContract.MoviesEntry.getMovieIDFromUri(uri))};
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MoviesContract.MoviesEntry.TABLE_NAME,
                        projection,
                        sItemMovieIDSelection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "weather"
            case TRAILERS: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MoviesContract.TrailersEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "location"
            case TRAILER_WITH_MOVIE: {
                selectionArgs = new String[]{Long.toString(MoviesContract.TrailersEntry.getMovieIDFromUri(uri))};
                retCursor = sTrailerByMovieID.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        sMovieIDSelection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "weather"
            case REVIEWS: {

                retCursor = mOpenHelper.getReadableDatabase().query(
                        MoviesContract.ReviewsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "location"
            case REVIEW_WITH_MOVIE: {
                selectionArgs = new String[]{Long.toString(MoviesContract.ReviewsEntry.getMovieIDFromUri(uri))};
                retCursor = sReviewByMovieID.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        sMovieIDSelection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIES:
                return MoviesContract.MoviesEntry.CONTENT_TYPE;
            case MOVIE_WITH_ID:
                return MoviesContract.MoviesEntry.CONTENT_ITEM_TYPE;
            case TRAILERS:
                return MoviesContract.TrailersEntry.CONTENT_TYPE;
            case TRAILER_WITH_MOVIE:
                return MoviesContract.TrailersEntry.CONTENT_TYPE;
            case REVIEWS:
                return MoviesContract.ReviewsEntry.CONTENT_TYPE;
            case REVIEW_WITH_MOVIE:
                return MoviesContract.ReviewsEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIES: {
                long _id = db.insert(MoviesContract.MoviesEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MoviesContract.MoviesEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case TRAILERS: {
                long _id = db.insert(MoviesContract.TrailersEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MoviesContract.TrailersEntry.buildTrailerUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case REVIEWS: {
                long _id = db.insert(MoviesContract.ReviewsEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MoviesContract.ReviewsEntry.buildReviewUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";
        switch (match) {
            case MOVIES:

                rowsDeleted = db.delete(
                        MoviesContract.MoviesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIE_WITH_ID:
                selectionArgs = new String[]{Long.toString(MoviesContract.MoviesEntry.getMovieIDFromUri(uri))};
                rowsDeleted = db.delete(
                        MoviesContract.MoviesEntry.TABLE_NAME, sItemMovieIDSelection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;

    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOVIES:
                rowsUpdated = db.update(MoviesContract.MoviesEntry.TABLE_NAME, contentValues, selection,
                        selectionArgs);
                break;
            case MOVIE_WITH_ID:
                selectionArgs = new String[]{Long.toString(MoviesContract.MoviesEntry.getMovieIDFromUri(uri))};
                rowsUpdated = db.update(MoviesContract.MoviesEntry.TABLE_NAME,
                        contentValues,
                        sItemMovieIDSelection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
