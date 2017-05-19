package mmpud.project.daycountwidget.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.squareup.phrase.Phrase;

import static mmpud.project.daycountwidget.data.db.Contract.Widget;

/**
 * Database helper.
 */
public class DayCountDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "DayCount.db";

    private static final String CREATE_TABLE_WIDGETS = Phrase.from(""
            + "CREATE TABLE {table} ("
            + "{widget_id} INTEGER PRIMARY KEY,"
            + "{title} TEXT,"
            + "{description} TEXT,"
            + "{target_date} INTEGER,"
            + "{count_by} INTEGER,"
            + "{header_style} TEXT,"
            + "{body_style} TEXT,"
            + "{alpha} REAL"
            + ")")
            .put("table", Widget.TABLE_NAME)
            .put("widget_id", Widget.WIDGET_ID)
            .put("title", Widget.EVENT_TITLE)
            .put("description", Widget.EVENT_DESCRIPTION)
            .put("target_date", Widget.TARGET_DATE)
            .put("count_by", Widget.COUNT_BY)
            .put("header_style", Widget.HEADER_STYLE)
            .put("body_style", Widget.BODY_STYLE)
            .put("alpha", Widget.ALPHA)
            .format().toString();
    private static final String ALTER_TABLE_ADD_COLUMN_ALPHA = Phrase.from(""
            + "ALTER TABLE {table} "
            + "ADD COLUMN {column_alpha} REAL DEFAULT 0")
            .put("table", Widget.TABLE_NAME)
            .put("column_alpha", Widget.ALPHA)
            .format().toString();

    private static final String DELETE_TABLE_WIDGETS = "DROP TABLE IF EXISTS " + Widget.TABLE_NAME;

    public DayCountDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_WIDGETS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            if (oldVersion == 1) {
                db.execSQL(ALTER_TABLE_ADD_COLUMN_ALPHA);
            } else {
                // TODO need to do data migration before dropping the table
                db.execSQL(DELETE_TABLE_WIDGETS);
                onCreate(db);
            }
        }
    }

}
