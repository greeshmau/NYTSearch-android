package com.codepath.gumapathi.nytsearch.Database;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by gumapathi on 9/23/2017.
 */
@Database(name = NYTDatabase.NAME, version = NYTDatabase.VERSION)
public class NYTDatabase {
    public static final String NAME = "NYTDatabase";

    public static final int VERSION = 1;
}
