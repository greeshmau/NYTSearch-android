package com.codepath.gumapathi.nytsearch.Helpers;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gumapathi on 9/20/2017.
 */

public class APIQueryStringBuilder {
    String queryTerm;
    boolean oldestFirst;
    String beginDate;
    String endDate;
    final String  APIKey = "227c750bb7714fc39ef1559ef1bd8329";
    List<NewsDesk> newsDesks;

    public String getQueryTerm() {
        return queryTerm;
    }

    public void setQueryTerm(String queryTerm) {
        this.queryTerm = queryTerm;
    }

    public boolean isOldestFirst() {
        return oldestFirst;
    }

    public void setOldestFirst(boolean oldestFirst) {
        this.oldestFirst = oldestFirst;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public List<NewsDesk> getNewsDesks() {
        return newsDesks;
    }

    public void setNewsDesks(List<NewsDesk> newsDesks) {
        this.newsDesks = newsDesks;
    }

    public void addNewsDesks(NewsDesk newsDesk) {
        if(!newsDesks.contains(newsDesk)) {
            this.newsDesks.add(newsDesk);
        }
    }


    /*public boolean isArt() {
        return art;
    }

    public void setArt(boolean art) {
        this.art = art;
    }

    public boolean isFashion() {
        return fashion;
    }

    public void setFashion(boolean fashion) {
        this.fashion = fashion;
    }

    public boolean isSports() {
        return sports;
    }

    public void setSports(boolean oldestFirst) {
        this.sports = sports;
    }
    */

    public APIQueryStringBuilder(  String queryTerm, boolean oldestFirst, String beginDate,
            String endDate) {
        this.queryTerm = queryTerm;
        this.oldestFirst = oldestFirst;
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.newsDesks = new ArrayList<NewsDesk>();
    }

    public String getQueryString() {
        StringBuilder query = new StringBuilder();
        query.append("?api-key=" + this.APIKey);

        if(!this.queryTerm.isEmpty()) {
            query.append("&q=" + this.queryTerm);
        }

        if(this.oldestFirst == true) {
            query.append("&sort=oldest");
        }
        else {
            query.append("&sort=newest");
        }

        if(!this.beginDate.isEmpty()) {
            query.append("&begin_date=" + beginDate);
        }
        if(!this.endDate.isEmpty()) {
            query.append("&end_date=" + endDate);
        }

        if(!newsDesks.isEmpty()) {
            query.append("&fq=news_desk:(");
            for (NewsDesk item :
                    newsDesks) {
                query.append("\"" + item+"\"%20");
            }
            query.delete(query.length()-3,query.length());
            query.append(")");
        }
        Log.i("SAMY-APIQB", query.toString());
        return query.toString();
    }
}

