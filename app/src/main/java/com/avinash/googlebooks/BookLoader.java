package com.avinash.googlebooks;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 *
 * [@link)BookLoader does background work of network call and is created by LoaderManager Callback method.
 */

class BookLoader extends AsyncTaskLoader<List<Book>> {
    private static final String LOG_TAG = BookLoader.class.getName();
    private String [] urls;

    BookLoader(Context context,String... url){
        super(context);
        urls = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Book> loadInBackground() {
        if(urls.length < 1 || urls[0] == null){
            return null;
        }
        return QueryUtils.fetchBooksData(urls[0]);
    }

}
