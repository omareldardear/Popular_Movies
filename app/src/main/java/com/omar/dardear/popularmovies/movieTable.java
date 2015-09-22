package com.omar.dardear.popularmovies;

import ckm.simple.sql_provider.annotation.SimpleSQLColumn;
import ckm.simple.sql_provider.annotation.SimpleSQLTable;

/**
 * Created by Omar on 9/22/2015.
 */

@SimpleSQLTable(table = "Movies", provider ="MovieProvider")
public class movieTable {

    @SimpleSQLColumn("Original_Title")
    public String original_title;

    @SimpleSQLColumn("Poster_Attr")
    public String poster_attr;

    @SimpleSQLColumn("Overview")
    public String overview;

    @SimpleSQLColumn("Vote_Average")
    public String vote_average;

    @SimpleSQLColumn("Release_Date")
    public String release_date;



}
