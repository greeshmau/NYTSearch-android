package com.codepath.gumapathi.nytsearch.Helpers;

/**
 * Created by gumapathi on 9/20/2017.
 */

public class APIQueryStringBuilder {
    String queryTerm;
    boolean oldestFirst;
    String beginDate;
    String endDate;
    NewsDesks[] newsDesks;

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

    public NewsDesks[] getNewsDesks() {
        return newsDesks;
    }

    public void addNewsDesks(NewsDesks newsDesks) {
        int len = this.newsDesks.length;
        this.newsDesks[len] = newsDesks;
    }

    public void setNewsDesks(NewsDesks[] newsDesks) {
        this.newsDesks = newsDesks;
    }

    public APIQueryStringBuilder(  String queryTerm, boolean oldestFirst, String beginDate,
            String endDate,NewsDesks[] newsDesks) {
        this.queryTerm = queryTerm;
        this.oldestFirst = oldestFirst;
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.newsDesks = newsDesks;
    }

    public String getQueryString() {
        return "";
    }
}

