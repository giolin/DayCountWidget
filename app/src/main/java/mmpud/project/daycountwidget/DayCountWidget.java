package mmpud.project.daycountwidget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.RemoteViews;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import mmpud.project.daycountwidget.util.Utils;
import timber.log.Timber;

public class DayCountWidget extends AppWidgetProvider {

    private static final String PREFS_NAME = "mmpud.project.daycountwidget.DayCountWidget";
    private static final String WIDGET_UPDATE_MIDNIGHT = "android.appwidget.action.WIDGET_UPDATE_MIDNIGHT";

    private static final int ALARM_ID = 0;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        Timber.d("Update Widget");

        AppWidgetManager manager = AppWidgetManager.getInstance(context);

        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = buildRemoteViews(context, appWidgetId);
            manager.updateAppWidget(appWidgetId, views);
            Timber.d("widget [" + appWidgetId + "] is updated");
        }
    }
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        // When Receiving the midnight alarm, update all the widgets so that one more day is counted
//        if (WIDGET_UPDATE_MIDNIGHT.equals(intent.getAction())) {
//            AppWidgetManager manager = AppWidgetManager.getInstance(context);
//            ComponentName widgetComponent = new ComponentName(context, DayCountWidget.class);
//
//            for (int appWidgetId : manager.getAppWidgetIds(widgetComponent)) {
//                RemoteViews views = buildRemoteViews(context, appWidgetId);
//                manager.updateAppWidget(appWidgetId, views);
//                Timber.d("widget [" + appWidgetId + "] updated at midnight");
//            }
//
//        }
//
//        super.onReceive(context, intent);
//    }


    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);

        // Start the alarm when the first widget is added

        // One day in milliseconds
        int INTERVAL_MILLIS = 1000 * 60 * 60 * 24;
        // Set the calendar to midnight on the next day
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.AM_PM, Calendar.AM);
        calendar.add(Calendar.DAY_OF_MONTH, 1);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisAppWidget = new ComponentName(context, DayCountWidget.class);
        Intent updateIntent = new Intent(context, DayCountWidget.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
        updateIntent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        context.sendBroadcast(updateIntent);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ALARM_ID, updateIntent, PendingIntent.FLAG_CANCEL_CURRENT);

//        Intent alarmIntent = new Intent(WIDGET_UPDATE_MIDNIGHT);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ALARM_ID, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        // RTC does not wake the device up
        alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), INTERVAL_MILLIS, pendingIntent);

    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
            prefs.edit().remove("targetDate" + appWidgetId).commit();
            prefs.edit().remove("styleNum" + appWidgetId).commit();
            prefs.edit().remove("title" + appWidgetId).commit();
            Timber.d("The widget [" + appWidgetId + "] onDelete!");
        }

    }

    @Override
    public void onDisabled(Context context) {
        // Delete the alarm
        Intent alarmIntent = new Intent(WIDGET_UPDATE_MIDNIGHT);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ALARM_ID, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        Timber.d("[Alarm is deldeted]");
    }

    public static RemoteViews buildRemoteViews(Context context, int mAppWidgetId) {
        // Get information: 1. YYYY/MM/DD
        //					2. widget style
        //					3. title
        // from shared preferences according to the appWidgetId
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String targetDate = prefs.getString("targetDate" + mAppWidgetId, "");
        String title = prefs.getString("title" + mAppWidgetId, "");
        int styleNum = prefs.getInt("styleNum" + mAppWidgetId, 1);

        // Get the day difference
        Calendar calToday = Calendar.getInstance();
        Calendar calTarget = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            calTarget.setTime(sdf.parse(targetDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long diffDays = Utils.daysBetween(calToday, calTarget);

        String layoutName = "widget_layout" + styleNum;
        int resourceIDStyle = context.getResources().getIdentifier(layoutName, "layout", "mmpud.project.daycountwidget");

        RemoteViews views = new RemoteViews(context.getPackageName(), resourceIDStyle);

        views.setTextViewText(R.id.widget_title, title);

        // Adjust the digits' textSize according to the number of digits
        float textSize = Utils.textSizeGenerator(diffDays);
        views.setFloat(R.id.widget_diffdays, "setTextSize", textSize);

        if (diffDays > 0) {
            views.setTextViewText(R.id.widget_since_left, context.getResources().getString(R.string.days_left));
            views.setTextViewText(R.id.widget_diffdays, Long.toString(diffDays));
        } else {
            views.setTextViewText(R.id.widget_since_left, context.getResources().getString(R.string.days_since));
            views.setTextViewText(R.id.widget_diffdays, Long.toString(-diffDays));
        }

        // Create intent for clicking on the widget for detail
        Intent intent = new Intent(context, DayCountDetailDialog.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

        // No request code and no flags
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widget, pendingIntent);

        return views;
    }

}
