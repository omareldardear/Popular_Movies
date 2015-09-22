package com.omar.dardear.popularmovies;

/**
 * Created by Omar on 9/5/2015.
 */
public class Movie {
    private String original_title;
    private String poster_attr;
    private String overview;
    private String vote_average;
    private String release_date;

    public Movie(String original_title, String poster_attr, String overview, String vote_average, String release_date) {
        this.original_title = original_title;
        this.poster_attr = poster_attr;
        this.overview = overview;
        this.vote_average = vote_average;
        this.release_date = release_date;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public String getPoster_attr() {
        return poster_attr;
    }

    public void setPoster_attr(String poster_attr) {
        this.poster_attr = poster_attr;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getVote_average() {
        return vote_average;
    }

    public void setVote_average(String vote_average) {
        this.vote_average = vote_average;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }
}
