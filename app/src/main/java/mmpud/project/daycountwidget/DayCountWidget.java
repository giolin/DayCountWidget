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

    private static final int ALARM_ID = 0;

    // Called when new widget is created
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

    // The midnight alarm will call this method with a WIDGET_UPDATE_ALL intent
    // Change Language will also call this method with a WIDGET_UPDATE_ALL intent
    @Override
    public void onReceive(Context context, Intent intent) {
        // When Receiving the midnight alarm, update all the widgets
        if (Utils.WIDGET_UPDATE_ALL.equals(intent.getAction())) {
            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            ComponentName widgetComponent = new ComponentName(context, DayCountWidget.class);

            for (int appWidgetId : manager.getAppWidgetIds(widgetComponent)) {
                RemoteViews views = buildRemoteViews(context, appWidgetId);
                manager.updateAppWidget(appWidgetId, views);
                Timber.d("widget [" + appWidgetId + "] updated at midnight");
            }
        }
        super.onReceive(context, intent);
    }

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

        Intent alarmIntent = new Intent(Utils.WIDGET_UPDATE_ALL);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ALARM_ID, alarmIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        // RTC does not wake the device up
        alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), INTERVAL_MILLIS,
                pendingIntent);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            SharedPreferences prefs = context.getSharedPreferences(Utils.PREFS_NAME, 0);
            prefs.edit().remove(Utils.KEY_TARGET_DATE + appWidgetId).apply();
            prefs.edit().remove(Utils.KEY_TITLE + appWidgetId).apply();
            prefs.edit().remove(Utils.KEY_STYLE_HEADER + appWidgetId).apply();
            prefs.edit().remove(Utils.KEY_STYLE_BODY + appWidgetId).apply();
            Timber.d("The widget [" + appWidgetId + "] onDelete!");
        }

    }

    @Override
    public void onDisabled(Context context) {
        // Delete the alarm
        Intent alarmIntent = new Intent(Utils.WIDGET_UPDATE_ALL);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ALARM_ID, alarmIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        Timber.d("[Alarm is deldeted]");
    }

    public static RemoteViews buildRemoteViews(Context context, int mAppWidgetId) {
        // Get information: 1. YYYY/MM/DD
        //					2. title
        //					3. header and body style
        // from shared preferences according to the appWidgetId
        SharedPreferences prefs = context.getSharedPreferences(Utils.PREFS_NAME, 0);
        String targetDate = prefs.getString(Utils.KEY_TARGET_DATE + mAppWidgetId, "");
        String title = prefs.getString(Utils.KEY_TITLE + mAppWidgetId, "");
        String styleHeader = prefs.getString(Utils.KEY_STYLE_HEADER + mAppWidgetId, "");
        String styleBody = prefs.getString(Utils.KEY_STYLE_BODY + mAppWidgetId, "");

        // Get the day difference
        Calendar calToday = Calendar.getInstance();
        Calendar calTarget = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            calTarget.setTime(sdf.parse(targetDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int diffDays = Utils.daysBetween(calToday, calTarget);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        // Set up the style
        int resourceIdStyleHeader = context.getResources().getIdentifier(styleHeader,
                "drawable", "mmpud.project.daycountwidget");

        int resourceIdStyleBody = context.getResources().getIdentifier(styleBody,
                "drawable", "mmpud.project.daycountwidget");

        views.setInt(R.id.widget_title, "setBackgroundResource", resourceIdStyleHeader);

        views.setInt(R.id.widget, "setBackgroundResource", resourceIdStyleBody);

        views.setTextViewText(R.id.widget_title, title);

        // Adjust the digits' textSize according to the number of digits
        float textSize = Utils.textSizeGenerator(diffDays);
        views.setFloat(R.id.widget_diffdays, "setTextSize", textSize);

        if (diffDays > 0) {
//            String strDaysLeft = context.getResources().getQuantityString(R.plurals.days_left,
//                    diffDays, null);
            views.setTextViewText(R.id.widget_since_left,
                    context.getResources().getText(R.string.days_left));
            views.setTextViewText(R.id.widget_diffdays, Integer.toString(diffDays));
        } else {
            diffDays = -diffDays;
//            String strDaysSince = context.getResources().getQuantityString(R.plurals.days_since,
//                    diffDays, null);
            views.setTextViewText(R.id.widget_since_left,
                    context.getResources().getText(R.string.days_since));
            views.setTextViewText(R.id.widget_diffdays, Integer.toString(diffDays));
        }

        // Create intent for clicking on the widget for detail
        Intent intent = new Intent(context, DayCountDetail.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

        // No request code and no flags
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widget, pendingIntent);

        return views;
    }

}
