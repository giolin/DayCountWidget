package mmpud.project.daycountwidget.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.squareup.phrase.Phrase;

/**
 * Database helper.
 */
public class DayCountDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "DayCount.db";

    private static final String CREATE_TABLE_WIDGETS = Phrase.from(""
        + "CREATE TABLE {table} ("
        + "{widget_id} INTEGER PRIMARY KEY,"
        + "{title} TEXT,"
        + "{description} TEXT,"
        + "{target_date} INTEGER,"
        + "{header_style} TEXT,"
        + "{body_style} TEXT"
        + ")")
        .put("table", DayCountContract.DayCountWidget.TABLE_NAME)
        .put("widget_id", DayCountContract.DayCountWidget.WIDGET_ID)
        .put("title", DayCountContract.DayCountWidget.EVENT_TITLE)
        .put("description", DayCountContract.DayCountWidget.EVENT_DESCRIPTION)
        .put("target_date", DayCountContract.DayCountWidget.TARGET_DATE)
        .put("header_style", DayCountContract.DayCountWidget.HEADER_STYLE)
        .put("body_style", DayCountContract.DayCountWidget.BODY_STYLE)
        .format().toString();

    private static final String DELETE_TABLE_WIDGETS =
        "DROP TABLE IF EXISTS " + DayCountContract.DayCountWidget.TABLE_NAME;

    public DayCountDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_WIDGETS);
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DELETE_TABLE_WIDGETS);
        onCreate(db);
    }

}
