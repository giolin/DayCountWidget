package mmpud.project.daycountwidget;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;

import mmpud.project.daycountwidget.provider.CounterContract;
import mmpud.project.daycountwidget.ui.ListItemAdapter;
import timber.log.Timber;

/**
 * Created by georgelin on 10/19/14.
 */
public class DayCounterMain extends ListActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    static final String[] PROJECTION = new String[]{CounterContract.Counter._ID,
            CounterContract.Counter.COLUMN_NAME_TARGET_DATE,
            CounterContract.Counter.COLUMN_NAME_TITLE,
            CounterContract.Counter.COLUMN_NAME_DETAIL,
            CounterContract.Counter.COLUMN_NAME_CREATE_TIME};

    private static final int LOADER_COUNTER_LIST = 0;
    ListItemAdapter mAdapter;
    private int UPDATE_COUNTER_REQUEST = 0;
    private int CREATE_COUNTER_REQUEST = 1;
    private ArrayList<Counter> counters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        counters = new ArrayList<Counter>();
        mAdapter = new ListItemAdapter(this, R.layout.list_item, counters);
        setListAdapter(mAdapter);

        getLoaderManager().initLoader(LOADER_COUNTER_LIST, null, this);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Timber.d("post [" + counters.get(position).getTitle() + "] is clicked");
        // Put info into bundle
        Intent intent = new Intent(this, DayCounterSet.class);
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        bundle.putInt("id", counters.get(position).getId());
        bundle.putString("target_date", counters.get(position).getTargetDate());
        bundle.putString("title", counters.get(position).getTitle());
        bundle.putString("detail", counters.get(position).getDetail());
        intent.putExtras(bundle);
        startActivityForResult(intent, UPDATE_COUNTER_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == UPDATE_COUNTER_REQUEST && resultCode == RESULT_OK) {
            // Update the db and the counters list arrays
            Counter counter = counters.get(data.getIntExtra("position", -1));
            counter.setTargetDate(data.getStringExtra("target_date"));
            counter.setTitle(data.getStringExtra("title"));
            counter.setDetail(data.getStringExtra("detail"));
            // Update DB
            ContentValues contentValues = new ContentValues();
            contentValues.put(CounterContract.Counter.COLUMN_NAME_TARGET_DATE, data.getStringExtra("target_date"));
            contentValues.put(CounterContract.Counter.COLUMN_NAME_TITLE, data.getStringExtra("title"));
            contentValues.put(CounterContract.Counter.COLUMN_NAME_DETAIL, data.getStringExtra("detail"));
            getContentResolver().update(Uri.parse("content://" + CounterContract.CONTENT_AUTHORITY
                    + "/" + CounterContract.Counter.TABLE_NAME),
                    contentValues,
                    "_ID=?",
                    new String[]{Integer.toString(data.getIntExtra("id", -1))});
            mAdapter.notifyDataSetChanged();
        } else if (requestCode == CREATE_COUNTER_REQUEST && resultCode == RESULT_OK) {
            // Insert to DB
            ContentValues contentValues = new ContentValues();
            contentValues.put(CounterContract.Counter.COLUMN_NAME_TARGET_DATE, data.getStringExtra("target_date"));
            contentValues.put(CounterContract.Counter.COLUMN_NAME_TITLE, data.getStringExtra("title"));
            contentValues.put(CounterContract.Counter.COLUMN_NAME_DETAIL, data.getStringExtra("detail"));
            Calendar calendar = Calendar.getInstance();
            contentValues.put(CounterContract.Counter.COLUMN_NAME_CREATE_TIME, calendar.getTime().toString());
            getContentResolver().insert(Uri.parse(CounterContract.BASE_CONTENT_URI
                    + "/" + CounterContract.Counter.TABLE_NAME),
                    contentValues);
            // Reload the data
            getLoaderManager().restartLoader(LOADER_COUNTER_LIST, null, this);
        }
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
        Timber.d("ON LOADER FINISHED!");
        // Confirm it is in offline mode
        // update the adapter here!
        if (data != null) {
            counters.clear();
            while (data.moveToNext()) {
                // ArrayString to StringArray
                int id = data.getInt(data.getColumnIndex(CounterContract.Counter._ID));
                String cTargetDate = data.getString(data
                        .getColumnIndex(CounterContract.Counter.COLUMN_NAME_TARGET_DATE));
                String cTitle = data.getString(data
                        .getColumnIndex(CounterContract.Counter.COLUMN_NAME_TITLE));
                String cDetail = data.getString(data
                        .getColumnIndex(CounterContract.Counter.COLUMN_NAME_DETAIL));
                String cCreateTime = data.getString(data
                        .getColumnIndex(CounterContract.Counter.COLUMN_NAME_CREATE_TIME));
                Counter counter = new Counter(id,
                        cTargetDate,
                        cTitle,
                        cDetail,
                        cCreateTime);
                counters.add(counter);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.day_count_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_create_counter) {
            Intent intent = new Intent(this, DayCounterSet.class);
            startActivityForResult(intent, CREATE_COUNTER_REQUEST);
        }

        return super.onOptionsItemSelected(item);
    }
}
