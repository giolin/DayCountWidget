package mmpud.project.daycountwidget.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.squareup.phrase.Phrase;

import static mmpud.project.daycountwidget.data.db.DayCountContract.DayCountWidget;

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
        + "{count_by} INTEGER,"
        + "{header_style} TEXT,"
        + "{body_style} TEXT"
        + ")")
        .put("table", DayCountWidget.TABLE_NAME)
        .put("widget_id", DayCountWidget.WIDGET_ID)
        .put("title", DayCountWidget.EVENT_TITLE)
        .put("description", DayCountWidget.EVENT_DESCRIPTION)
        .put("target_date", DayCountWidget.TARGET_DATE)
        .put("count_by", DayCountWidget.COUNT_BY)
        .put("header_style", DayCountWidget.HEADER_STYLE)
        .put("body_style", DayCountWidget.BODY_STYLE)
        .format().toString();

    private static final String DELETE_TABLE_WIDGETS =
        "DROP TABLE IF EXISTS " + DayCountWidget.TABLE_NAME;

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
