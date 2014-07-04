package mmpud.project.daycountwidget;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

public class DayCountWidget extends AppWidgetProvider {
	
	private static final String PREFS_NAME = "mmpud.project.daycountwidget.DayCountWidget";
	private static final String TAG_NAME = "mmpud";
	private static final String WIDGET_UPDATE_MIDNIGHT = "android.appwidget.action.WIDGET_UPDATE_MIDNIGHT";
	
	private static final int ALARM_ID = 0;
	
	private int targetYear;
	private int targetMonth;
	private int targetDate;
	private int headerColor;
	private int bodyColor;;
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		
		Log.d(TAG_NAME, "Update Widget");

		AppWidgetManager manager = AppWidgetManager.getInstance(context);
		
		for (int appWidgetId : appWidgetIds) {
			RemoteViews views = buildUpdate(context, appWidgetId);
			manager.updateAppWidget(appWidgetId, views);
		}
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// When Receiving the midnight alarm, update all the widgets so that one more day is counted
	    if (WIDGET_UPDATE_MIDNIGHT.equals(intent.getAction())) {
	        AppWidgetManager manager = AppWidgetManager.getInstance(context);
	        ComponentName widgetComponent = new ComponentName(context, DayCountWidget.class);
			
	        for (int appWidgetId : manager.getAppWidgetIds(widgetComponent)) {
				RemoteViews views = buildUpdate(context, appWidgetId);
				manager.updateAppWidget(appWidgetId, views);
				Log.d(TAG_NAME, "widget [" + appWidgetId + "] updated at midnight");
			}
	        
	    }
	    
	    super.onReceive(context, intent);
	}
	
	
	@Override
	public void onEnabled(Context context) {
		 super.onEnabled(context);

		 // Start the alarm when the first widget is added
		 
		 // One day in milliseconds
		 int INTERVAL_MILLIS = 1000*60*60*24;
	     // Set the calendar to midnight on the next day
		 Calendar calendar = Calendar.getInstance();
		 calendar.setTimeInMillis(System.currentTimeMillis());
		 calendar.set(Calendar.SECOND, 0);
		 calendar.set(Calendar.MINUTE, 0);
		 calendar.set(Calendar.HOUR, 0);
		 calendar.set(Calendar.AM_PM, Calendar.AM);
		 calendar.add(Calendar.DAY_OF_MONTH, 1);

	     Intent alarmIntent = new Intent(WIDGET_UPDATE_MIDNIGHT);
	     PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ALARM_ID, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);

	     AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	     // RTC does not wake the device up
	     alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), INTERVAL_MILLIS, pendingIntent);

	}
	
	@Override
    public void onDeleted(Context context, int[] appWidgetIds) {
		for (int appWidgetId : appWidgetIds) {
			SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME,0);
            prefs.edit().remove("year"+appWidgetId).commit();
            prefs.edit().remove("month"+appWidgetId).commit();
            prefs.edit().remove("date"+appWidgetId).commit();
            prefs.edit().remove("headerColor"+appWidgetId).commit();
            prefs.edit().remove("bodyColor"+appWidgetId).commit();
            prefs.edit().remove("title"+appWidgetId).commit();
            Log.d(TAG_NAME, "The widget [" + appWidgetId + "] onDelete!");
		}

    }
	
    @Override
    public void onDisabled(Context context)
    {
        // Delete the alarm
		Intent alarmIntent = new Intent(WIDGET_UPDATE_MIDNIGHT);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ALARM_ID, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        Log.d(TAG_NAME, "[Alarm is deldeted]");
    }
	
	public RemoteViews buildUpdate(Context context, int mAppWidgetId)
	{
		// Get information: 1. YYYY/MM/DD
		//					2. widget style
		// from shared preferences according to the appWidgetId
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME,0);
		targetYear = prefs.getInt("year"+mAppWidgetId, 0);
		targetMonth = prefs.getInt("month"+mAppWidgetId, 0);
		targetDate = prefs.getInt("date"+mAppWidgetId, 0);
		headerColor = prefs.getInt("headerColor"+mAppWidgetId, 1);
		bodyColor = prefs.getInt("bodyColor"+mAppWidgetId, 1);
		
		// Get the day difference
		Calendar calToday = Calendar.getInstance();
		Calendar calTarget = Calendar.getInstance();
		calTarget.set(targetYear, targetMonth, targetDate);
		long diffDays = daysBetween(calToday, calTarget);
		
		RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.day_count_widget_layout);
		
		// Adjust the digits' textSize according to the number of digits
		float textSize = textSizeGenerator(diffDays);
		views.setFloat(R.id.widget_diffdays,"setTextSize", textSize);
		
		if(diffDays > 0) {
			views.setTextViewText(R.id.widget_since_left, context.getResources().getString(R.string.days_left));
			views.setTextViewText(R.id.widget_diffdays, Long.toString(diffDays));
		} else {
			views.setTextViewText(R.id.widget_since_left, context.getResources().getString(R.string.days_since));
			views.setTextViewText(R.id.widget_diffdays, Long.toString(-diffDays));
		}

		switch(headerColor) {
		case 1:
			views.setInt(R.id.widget_since_left, "setBackgroundResource", R.drawable.shape_header1);
			break;
		case 2:
			views.setInt(R.id.widget_since_left, "setBackgroundResource", R.drawable.shape_header2);
			break;
		case 3:
			views.setInt(R.id.widget_since_left, "setBackgroundResource", R.drawable.shape_header3);
			break;
		case 4:
			views.setInt(R.id.widget_since_left, "setBackgroundResource", R.drawable.shape_header4);
			break;
		case 5:
			views.setInt(R.id.widget_since_left, "setBackgroundResource", R.drawable.shape_header5);
			break;
		case 6:
			views.setInt(R.id.widget_since_left, "setBackgroundResource", R.drawable.shape_header6);
			break;
		case 7:
			views.setInt(R.id.widget_since_left, "setBackgroundResource", R.drawable.shape_header7);
			break;
		case 8:
			views.setInt(R.id.widget_since_left, "setBackgroundResource", R.drawable.shape_header8);
			break;		
		}
		
		switch(bodyColor) {
		case 1:
	        views.setInt(R.id.widget_diffdays, "setBackgroundResource", R.drawable.shape_body1);
			break;
		case 2:
	        views.setInt(R.id.widget_diffdays, "setBackgroundResource", R.drawable.shape_body2);
			break;
		case 3:
	        views.setInt(R.id.widget_diffdays, "setBackgroundResource", R.drawable.shape_body3);
			break;
		case 4:
	        views.setInt(R.id.widget_diffdays, "setBackgroundResource", R.drawable.shape_body4);
			break;
		case 5:
	        views.setInt(R.id.widget_diffdays, "setBackgroundResource", R.drawable.shape_body5);
			break;
		case 6:
	        views.setInt(R.id.widget_diffdays, "setBackgroundResource", R.drawable.shape_body6);
			break;
		case 7:
	        views.setInt(R.id.widget_diffdays, "setBackgroundResource", R.drawable.shape_body7);
			break;
		case 8:
	        views.setInt(R.id.widget_diffdays, "setBackgroundResource", R.drawable.shape_body8);
			break;
		}
					
		// Click on the widget for editing
		Intent intent = new Intent(context, DayCountDetailDialog.class);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId); 
		intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));		
		
		// No request code and no flags for this example
		PendingIntent pender = PendingIntent.getActivity(context, 0, intent, 0);
		views.setOnClickPendingIntent(R.id.widget, pender);
		
		return views;
	}
	
	public float textSizeGenerator(long num) {
		if(num<0) {
			num=-num;
		}
		if(num >= 0 && num < 100) {
			return 42;
		} else if (num >= 100 && num < 1000) {
			return 34;
		} else if (num >= 1000 && num < 10000) {
			return 26;
		} else if (num >= 10000 && num < 100000) {
			return 22;
		} else {
			return 18;
		}
	}
	
	public long daysBetween(Calendar startDay, Calendar endDate) {
		long startTime = startDay.getTime().getTime();
		long endTime = endDate.getTime().getTime();
		long diffTime = endTime - startTime;
		return (diffTime / (1000 * 60 * 60 * 24));
	}
}
