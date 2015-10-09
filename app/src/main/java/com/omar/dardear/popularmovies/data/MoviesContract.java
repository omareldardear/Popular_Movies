package com.omar.dardear.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Omar on 9/27/2015.
 */
public class MoviesContract {

    public static final String CONTENT_AUTHORITY = "com.omar.dardear.popularmovies.data";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static final String PATH_MOVIES = "Movies";
    public static final String PATH_TRAILERS = "Trailers";
    public static final String PATH_REVIEWS = "Reviews";


    public static final class MoviesEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;


        public static final String TABLE_NAME = "Movies";

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER_ATTR = "poster_attr";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_RUN_TIME = "run_time";
        public static final String COLUMN_FAVOURITE = "favourite";
        public static final String COLUMN_SORT_INDEX = "sort_index";


        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static long getMovieIDFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

    }
    public static final class TrailersEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILERS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILERS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILERS;


        public static final String TABLE_NAME = "Trailers";

        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_LINK = "link";
        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static Uri buildTrailerUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildTrailerMovieID(long MovieID) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(MovieID)).build();
        }

        public static long getMovieIDFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }


    }
    public static final class ReviewsEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEWS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEWS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEWS;


        public static final String TABLE_NAME = "Reviews";

        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_URL = "url";
        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static Uri buildReviewUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildReviewMovieID(long MovieID) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(MovieID)).build();
        }

        public static long getMovieIDFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

    }
}

