package mmpud.project.daycountwidget;

import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import net.danlew.android.joda.JodaTimeAndroid;

import mmpud.project.daycountwidget.data.db.DayCountDbHelper;
import mmpud.project.daycountwidget.util.Dates;
import timber.log.Timber;

import static mmpud.project.daycountwidget.data.db.Contract.COUNT_BY_DAY;
import static mmpud.project.daycountwidget.data.db.Contract.Widget.BODY_STYLE;
import static mmpud.project.daycountwidget.data.db.Contract.Widget.COUNT_BY;
import static mmpud.project.daycountwidget.data.db.Contract.Widget.EVENT_DESCRIPTION;
import static mmpud.project.daycountwidget.data.db.Contract.Widget.EVENT_TITLE;
import static mmpud.project.daycountwidget.data.db.Contract.Widget.HEADER_STYLE;
import static mmpud.project.daycountwidget.data.db.Contract.Widget.TABLE_NAME;
import static mmpud.project.daycountwidget.data.db.Contract.Widget.TARGET_DATE;
import static mmpud.project.daycountwidget.data.db.Contract.Widget.WIDGET_ID;

public class DayCountApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }
        // init joda time
        JodaTimeAndroid.init(this);
        // should do this when user updates app from older version
        SharedPreferences prefs = getSharedPreferences(
            "mmpud.project.daycountwidget.DayCountWidget", MODE_PRIVATE);
        if (!prefs.getAll().isEmpty()) {
            // only do the transferring when the preference is non-empty
            transferDataFromPrefsToDb(prefs);
        }
    }

    private void transferDataFromPrefsToDb(SharedPreferences prefs) {
        // get all available day count widget ids
        int[] appWidgetIds = AppWidgetManager.getInstance(this)
            .getAppWidgetIds(new ComponentName(this, DayCountWidgetProvider.class));
        // get widgets from pref and save them in db
        SQLiteDatabase db = new DayCountDbHelper(this).getReadableDatabase();
        String pkgName = "mmpud.project.daycountwidget";
        for (int id : appWidgetIds) {
            // get widget info from prefs
            String title = prefs.getString("title" + id, "");
            long timeStamp = Dates.dateStringToTimestamp(prefs.getString("target_date" + id, ""));
            String header = prefs.getString("style_header" + id, "");
            int headerColorId = getResources().getIdentifier(header, "color", pkgName);
            int headerColor = ContextCompat.getColor(this, headerColorId > 0 ? headerColorId
                : R.color.header_navy);
            String body = prefs.getString("style_body" + id, "");
            int bodyColorId = getResources().getIdentifier(body, "color", pkgName);
            int bodyColor = ContextCompat.getColor(this, bodyColorId > 0 ? bodyColorId
                : R.color.body_navy);
            // insert to db
            ContentValues values = new ContentValues();
            values.put(WIDGET_ID, id);
            values.put(EVENT_TITLE, title);
            values.put(EVENT_DESCRIPTION, "");
            values.put(TARGET_DATE, timeStamp);
            values.put(COUNT_BY, COUNT_BY_DAY);
            values.put(HEADER_STYLE, String.valueOf(headerColor));
            values.put(BODY_STYLE, String.valueOf(bodyColor));
            db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        }
        db.close();
        // clear all since we no longer need the prefs
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * A tree which logs important information for crash reporting.
     */
    private static class CrashReportingTree extends Timber.Tree {
        @Override protected void log(int priority, String tag, String message, Throwable t) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return;
            }
            if (t != null) {
                if (priority == Log.ERROR) {
                    // TODO log error
                } else if (priority == Log.WARN) {
                    // TODO log warning
                }
            }
        }
    }

}
