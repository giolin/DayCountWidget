package mmpud.project.daycountwidget.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import mmpud.project.daycountwidget.provider.CounterContract;

/**
 * Created by georgelin on 10/10/14.
 */
public class CounterDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "day_count_widget.db";

    private static final String TABLE_NAME = CounterContract.Counter.TABLE_NAME;

    // Column names
    private static final String ID = CounterContract.Counter._ID;
    private static final String TARGET_DATE = CounterContract.Counter.COLUMN_NAME_TARGET_DATE;
    private static final String TITLE = CounterContract.Counter.COLUMN_NAME_TITLE;
    private static final String STYLE = CounterContract.Counter.COLUMN_STYLE;
    private static final String CREATE_TIME = CounterContract.Counter.COLUMN_NAME_CREATE_TIME;


    private static final String TYPE_TEXT = " TEXT";
    private static final String TYPE_INTEGER = " INTEGER";
    private static final String COMMA_SEP = ",";

    public CounterDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_WIDGET_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TARGET_DATE + TYPE_TEXT + COMMA_SEP +
                TITLE + TYPE_TEXT + COMMA_SEP +
                STYLE + TYPE_INTEGER + COMMA_SEP +
                CREATE_TIME + TYPE_TEXT
                + ")";
        db.execSQL(CREATE_WIDGET_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String DROP_WIDGET_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(DROP_WIDGET_TABLE);
    }

//    public void addWidget(Widget widget) {
//        Timber.d("addWidget: " + widget.toString());
//        SQLiteDatabase db = getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(WIDGET_ID, widget.getWidgetId());
//        values.put(TARGET_DATE, widget.getTargetDate());
//        values.put(TITLE, widget.getTitle());
//        values.put(DETAIL, widget.getDetail());
//        values.put(WIDGET_STYLE, widget.getWidgetStyle());
//
//        db.insert(TABLE_NAME, null, values);
//
//        db.close();
//    }
//
//    public Widget getWidget(int widgetId) {
//        SQLiteDatabase db = getReadableDatabase();
//
//        Cursor cursor =
//                db.query(TABLE_NAME, // a. table
//                        PROJECTOR, // b. column names
//                        WIDGET_ID + " = ?", // c. selections
//                        new String[] { Integer.toString(widgetId) }, // d. selections args
//                        null, // e. group by
//                        null, // f. having
//                        null, // g. order by
//                        null); // h. limit
//
//        // Theoretically, there is only one row
//        if (cursor != null) {
//            cursor.moveToFirst();
//        }
//
//        Widget widget = new Widget();
//        widget.setWidgetId(cursor.getInt(cursor.getColumnIndex(WIDGET_ID)));
//        widget.setTargetDate(cursor.getString(cursor.getColumnIndex(TARGET_DATE)));
//        widget.setTitle(cursor.getString(cursor.getColumnIndex(TITLE)));
//        widget.setDetail(cursor.getString(cursor.getColumnIndex(DETAIL)));
//        widget.setWidgetStyle(cursor.getInt(cursor.getColumnIndex(WIDGET_STYLE)));
//
//        Timber.d("getWidget: " + widget.toString());
//
//        return widget;
//    }
//
//    public List<Widget> getAllWidgets() {
//        List<Widget> widgets = new LinkedList<Widget>();
//
//        String query = "SELECT  * FROM " + TABLE_NAME;
//
//        SQLiteDatabase db = getReadableDatabase();
//
//        Cursor cursor = db.rawQuery(query, null);
//        Widget widget = null;
//        while (cursor.moveToNext()) {
//            widget = new Widget();
//            widget.setWidgetId(cursor.getInt(cursor.getColumnIndex(WIDGET_ID)));
//            widget.setTargetDate(cursor.getString(cursor.getColumnIndex(TARGET_DATE)));
//            widget.setTitle(cursor.getString(cursor.getColumnIndex(TITLE)));
//            widget.setDetail(cursor.getString(cursor.getColumnIndex(DETAIL)));
//            widget.setWidgetStyle(cursor.getInt(cursor.getColumnIndex(WIDGET_STYLE)));
//            widgets.add(widget);
//        }
//
//        Timber.d("getAllWidgets: " + widgets.toString());
//
//        return widgets;
//    }
//
//    public int updateWidget(Widget widget) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
////        values.put(WIDGET_ID, widget.getWidgetId());
//        values.put(TARGET_DATE, widget.getTargetDate());
//        values.put(TITLE, widget.getTitle());
//        values.put(DETAIL, widget.getDetail());
//        values.put(WIDGET_STYLE, widget.getWidgetStyle());
//
//        int i = db.update(TABLE_NAME, //table
//                values, // column/value
//                WIDGET_ID+" = ?", // selections
//                new String[] { Integer.toString(widget.getWidgetId()) }); //selection args
//
//        db.close();
//
//        return i;
//    }
//
//    public void deleteWidget(Widget widget) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        db.delete(TABLE_NAME, //table name
//                WIDGET_ID+" = ?",  // selections
//                new String[] { String.valueOf(widget.getWidgetId()) }); //selections args
//
//        // 3. close
//        db.close();
//
//        Timber.d("deleteWidget: ", widget.toString());
//    }
}
