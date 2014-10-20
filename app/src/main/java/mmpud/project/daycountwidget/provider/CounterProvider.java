package mmpud.project.daycountwidget.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import mmpud.project.daycountwidget.db.CounterDBHelper;
import mmpud.project.daycountwidget.db.SelectionBuilder;

/**
 * Created by georgelin on 10/13/14.
 */
public class CounterProvider extends ContentProvider {

    /**
     * Content authority for this provider.
     */
    private static final String AUTHORITY = CounterContract.CONTENT_AUTHORITY;

    /**
     * URI ID for route: /postlist
     */
    private static final int TABLE_COUNTER_LIST = 1;

    /**
     * UriMatcher, used to decode incoming URIs.
     */
    private static final UriMatcher mUriMatcher;

    static {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(AUTHORITY, CounterContract.Counter.TABLE_NAME, TABLE_COUNTER_LIST);
    }

    private CounterDBHelper mCounterDBHelper = null;

    @Override
    public boolean onCreate() {
        mCounterDBHelper = new CounterDBHelper(getContext());
        return false;
    }

    @Override
    public String getType(Uri uri) {
        if (mUriMatcher.match(uri) == TABLE_COUNTER_LIST) {
            return CounterContract.Counter.CONTENT_TYPE;
        } else {
            return null;
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase mDB = mCounterDBHelper.getReadableDatabase();
        SelectionBuilder builder = new SelectionBuilder();
        Cursor out = null;

        if (mUriMatcher.match(uri) == TABLE_COUNTER_LIST) {
            // Return all known entries.
            out = builder.table(CounterContract.Counter.TABLE_NAME)
                    .where(selection, selectionArgs)
                    .query(mDB, projection, sortOrder);
            // Note that the out is a cursor,
            // which initially points to the position before the first row (i.e. -1)
        } else {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        return out;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        SQLiteDatabase mDB = mCounterDBHelper.getWritableDatabase();
        Uri result;
        if (mUriMatcher.match(uri) == TABLE_COUNTER_LIST) {
            long id = mDB.insertOrThrow(CounterContract.Counter.TABLE_NAME, null, values);
            result = Uri.parse(CounterContract.Counter.CONTENT_URI + "/" + id);
        } else {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        return result;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SelectionBuilder builder = new SelectionBuilder();
        final SQLiteDatabase mDB = mCounterDBHelper.getWritableDatabase();
        int count;
        if (mUriMatcher.match(uri) == TABLE_COUNTER_LIST) {
            count = builder.table(CounterContract.Counter.TABLE_NAME)
                    .where(selection, selectionArgs)
                    .delete(mDB);
        } else {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SelectionBuilder builder = new SelectionBuilder();
        final SQLiteDatabase mDB = mCounterDBHelper.getWritableDatabase();
        int count;
        if(mUriMatcher.match(uri)== TABLE_COUNTER_LIST) {
            count = builder.table(CounterContract.Counter.TABLE_NAME)
                    .where(selection, selectionArgs)
                    .update(mDB, values);
        } else {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        return count;
    }
}
