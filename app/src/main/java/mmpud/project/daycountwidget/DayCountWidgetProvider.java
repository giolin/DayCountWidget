package mmpud.project.daycountwidget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.widget.RemoteViews;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;

import mmpud.project.daycountwidget.data.db.Contract;
import mmpud.project.daycountwidget.data.db.DayCountDbHelper;
import mmpud.project.daycountwidget.util.Dates;
import mmpud.project.daycountwidget.util.Times;

import static mmpud.project.daycountwidget.data.db.Contract.COUNT_BY_DAY;
import static mmpud.project.daycountwidget.data.db.Contract.Widget;

public class DayCountWidgetProvider extends AppWidgetProvider {

    private static DayCountDbHelper mDbHelper;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = buildRemoteViews(context, appWidgetId);
            manager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        // when receiving the midnight alarm, update all the widgets
        if (Times.WIDGET_UPDATE_ALL.equals(intent.getAction())) {
            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            ComponentName component = new ComponentName(context, DayCountWidgetProvider.class);
            int[] appWidgetIds = manager.getAppWidgetIds(component);
            for (int appWidgetId : appWidgetIds) {
                RemoteViews views = buildRemoteViews(context, appWidgetId);
                manager.updateAppWidget(appWidgetId, views);
            }
        }
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        // start the alarm when the first widget is added
        Times.setMidnightAlarm(context);
    }

    @Override
    public void onDisabled(Context context) {
        // delete the alarm
        Intent alarmIntent = new Intent(Times.WIDGET_UPDATE_ALL);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Times.ALARM_ID,
            alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        if (mDbHelper == null) {
            mDbHelper = new DayCountDbHelper(context);
        }
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        for (int appWidgetId : appWidgetIds) {
            db.delete(Widget.TABLE_NAME, Widget.WIDGET_ID + "=?",
                new String[] {String.valueOf(appWidgetId)});
        }
        db.close();
    }

    /**
     * Generate remote views for the widget.
     *
     * @param context
     * @param mAppWidgetId
     * @return
     */
    public static RemoteViews buildRemoteViews(Context context, int mAppWidgetId) {
        // query from database
        if (mDbHelper == null) {
            mDbHelper = new DayCountDbHelper(context);
        }
        String title;
        @Contract.CountBy int countBy;
        String headerStyle;
        String bodyStyle;
        float alpha;
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = null;
        LocalDateTime targetDay;
        try {
            cursor = db.query(Widget.TABLE_NAME, null, Widget.WIDGET_ID + "=?",
                new String[] {String.valueOf(mAppWidgetId)}, null, null, null);
            if (cursor.moveToFirst()) {
                targetDay = Times.getLocalDateTime(cursor.getLong(cursor
                    .getColumnIndexOrThrow(Widget.TARGET_DATE)));
                title = cursor.getString(
                    cursor.getColumnIndexOrThrow(Widget.EVENT_TITLE));
                // noinspection ResourceType
                countBy = cursor.getInt(cursor.getColumnIndexOrThrow(Widget.COUNT_BY));
                headerStyle = cursor.getString(
                    cursor.getColumnIndexOrThrow(Widget.HEADER_STYLE));
                bodyStyle = cursor.getString(
                    cursor.getColumnIndexOrThrow(Widget.BODY_STYLE));
                // alpha
                alpha = cursor.getFloat(cursor.getColumnIndexOrThrow(Widget.ALPHA));
            } else {
                targetDay = LocalDate.now().atStartOfDay();
                title = "";
                countBy = COUNT_BY_DAY;
                headerStyle = String.valueOf(ContextCompat.getColor(context, R.color.header_black));
                bodyStyle = String.valueOf(ContextCompat.getColor(context, R.color.body_black));
                alpha = 1;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        // set header style
        views.setInt(R.id.widget_header_bg, "setColorFilter", Integer.parseInt(headerStyle));
        // set body style
        views.setInt(R.id.widget_body_bg, "setColorFilter", Integer.parseInt(bodyStyle));
        // set view's title
        views.setTextViewText(R.id.widget_header, title);
        // set view's content
        views.setTextViewText(R.id.widget_body,
            Dates.getWidgetContentSpannable(context, countBy, targetDay));
        // set view's transparency
        int alphaPercent = (int) (alpha * 255);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            views.setInt(R.id.widget_header_bg, "setImageAlpha", alphaPercent);
            views.setInt(R.id.widget_body_bg, "setImageAlpha", alphaPercent);
        } else {
            views.setInt(R.id.widget_header_bg, "setAlpha", alphaPercent);
            views.setInt(R.id.widget_body_bg, "setAlpha", alphaPercent);
        }
        // create intent for clicking on the widget for detail
        Intent intent = new Intent(context, DayCountDetail.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        // no request code and no flags
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widget, pendingIntent);
        return views;
    }

}
