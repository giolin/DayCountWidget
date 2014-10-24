package mmpud.project.daycountwidget;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import mmpud.project.daycountwidget.provider.CounterContract;
import mortar.Blueprint;
import mortar.ViewPresenter;

/**
 * Created by george on 2014/10/24.
 */
public class CounterList implements Blueprint, LoaderManager.LoaderCallbacks<Cursor> {

    static final String[] PROJECTION = new String[]{CounterContract.Counter._ID,
            CounterContract.Counter.COLUMN_NAME_TARGET_DATE,
            CounterContract.Counter.COLUMN_NAME_TITLE,
            CounterContract.Counter.COLUMN_NAME_DETAIL,
            CounterContract.Counter.COLUMN_NAME_CREATE_TIME};

    private static final int LOADER_COUNTER_LIST = 0;


    @Override
    public String getMortarScopeName() {
        return getClass().getName();
    }

    @Override
    public Object getDaggerModule() {
        return new Module();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        switch (id) {
            case LOADER_COUNTER_LIST:
                return new CursorLoader(
                        this,   // Parent activity context
                        Uri.parse(CounterContract.BASE_CONTENT_URI
                                + "/" + CounterContract.Counter.TABLE_NAME), // Table to query
                        PROJECTION, // Return post_id, post_name, post_url, post_time, image_url
                        null,   // Select posts by forum id
                        null,  // The argument would be fid
                        null    // Sorted by timestamp in descending order
                );
            default:
                // An invalid id was passed in
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @dagger.Module(
            injects = {
                    DayCounterMain.class,
                    CounterListView.class
            }
    )
    class Module{ }

    @Singleton
    static class Presenter extends ViewPresenter<CounterListView> {
        @Inject Presenter() { }

        @Override
        protected void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);
        }

    }
}
