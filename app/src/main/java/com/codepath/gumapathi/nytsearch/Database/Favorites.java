package com.codepath.gumapathi.nytsearch.Database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by gumapathi on 9/23/2017.
 */
@Table(database = NYTDatabase.class)
public class Favorites extends BaseModel {
    @Column
    @PrimaryKey(autoincrement = true)
    public int id;

    @Column
    public String ArticleTitle;

    @Column
    public String ArticleURL;

    public Favorites() {
    }

    public Favorites(String ArticleTitle, String ArticleURL) {
        this.ArticleTitle = ArticleTitle;
        this.ArticleURL = ArticleURL;
    }

}
