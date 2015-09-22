package com.omar.dardear.popularmovies;

import ckm.simple.sql_provider.UpgradeScript;
import ckm.simple.sql_provider.annotation.ProviderConfig;
import ckm.simple.sql_provider.annotation.SimpleSQLConfig;

/**
 * Created by Omar on 9/22/2015.
 */
@SimpleSQLConfig(
        name = "MovieProvider",
        authority = "com.omar.dardear.popularmovies",
        database = "movie.db",
        version = 1)
public class MovieProviderConfig implements ProviderConfig {
    @Override
    public UpgradeScript[] getUpdateScripts() {
        return new UpgradeScript[0];
    }
}
