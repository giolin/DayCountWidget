package mmpud.project.daycountwidget.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.squareup.phrase.Phrase;

import static mmpud.project.daycountwidget.data.db.Contract.Widget;

/**
 * Database helper.
 */
public class DayCountDbHelper extends SQLiteOpenHelper {

    private static abstract class Version {

        private static final int ORIGIN = 1;

        private static final int ADD_COLUMN_ALPHA = 2;

        private static final int ADD_COLUMN_HORIZONTAL_VERTICAL_PADDING = 3;

    }

    private static final int DATABASE_VERSION = Version.ADD_COLUMN_HORIZONTAL_VERTICAL_PADDING;
    private static final String DATABASE_NAME = "DayCount.db";

    private static final String CREATE_TABLE_WIDGETS = Phrase.from(""
            + "CREATE TABLE {table} ("
            + "{widget_id} INTEGER PRIMARY KEY,"
            + "{title} TEXT,"
            + "{description} TEXT,"
            + "{target_date} INTEGER,"
            + "{count_by} INTEGER,"
            + "{header_style} TEXT,"
            + "{body_style} TEXT,"
            + "{alpha} REAL,"
            + "{horizontal_padding} INTEGER,"
            + "{vertical_padding} INTEGER"
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
            .put("horizontal_padding", Widget.HORIZONTAL_PADDING)
            .put("vertical_padding", Widget.VERTICAL_PADDING)
            .format().toString();
    private static final String ALTER_TABLE_ADD_COLUMN_ALPHA = Phrase.from(""
            + "ALTER TABLE {table} "
            + "ADD COLUMN {column_alpha} REAL DEFAULT 0")
            .put("table", Widget.TABLE_NAME)
            .put("column_alpha", Widget.ALPHA)
            .format().toString();
    private static final String ALTER_TABLE_ADD_COLUMN_HORIZONTAL_PADDING = Phrase.from(""
            + "ALTER TABLE {table} "
            + "ADD COLUMN {horizontal_padding} INTEGER DEFAULT -1")
            .put("table", Widget.TABLE_NAME)
            .put("horizontal_padding", Widget.HORIZONTAL_PADDING)
            .format().toString();
    private static final String ALTER_TABLE_ADD_COLUMN_VERTICAL_PADDING = Phrase.from(""
            + "ALTER TABLE {table} "
            + "ADD COLUMN {vertical_padding} INTEGER DEFAULT -1")
            .put("table", Widget.TABLE_NAME)
            .put("vertical_padding", Widget.VERTICAL_PADDING)
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
            if (oldVersion < Version.ADD_COLUMN_ALPHA) {
                db.execSQL(ALTER_TABLE_ADD_COLUMN_ALPHA);
            }
            if (oldVersion < Version.ADD_COLUMN_HORIZONTAL_VERTICAL_PADDING) {
                db.execSQL(ALTER_TABLE_ADD_COLUMN_HORIZONTAL_PADDING);
                db.execSQL(ALTER_TABLE_ADD_COLUMN_VERTICAL_PADDING);
            }
        }
    }

}
