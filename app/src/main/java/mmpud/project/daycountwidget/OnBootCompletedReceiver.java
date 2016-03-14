package mmpud.project.daycountwidget;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import mmpud.project.daycountwidget.util.Times;

/**
 * Receiver when the device is booted.
 */
public class OnBootCompletedReceiver extends BroadcastReceiver {

    @Override public void onReceive(Context context, Intent intent) {
        // if there exists some widgets on the desktop, reset the alarm.
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        ComponentName componentName = new ComponentName(context, DayCountWidgetProvider.class);
        if (manager.getAppWidgetIds(componentName).length > 0) {
            Times.setMidnightAlarm(context);
        }
    }

}
