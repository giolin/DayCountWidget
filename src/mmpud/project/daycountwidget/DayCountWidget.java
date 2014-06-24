package mmpud.project.daycountwidget;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

public class DayCountWidget extends AppWidgetProvider {
	
	private static final String PREFS_NAME = "mmpud.project.daycountwidget.DayCountWidget";
	
	private int targetYear;
	private int targetMonth;
	private int targetDate;

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		for (int appWidgetId : appWidgetIds) {
			RemoteViews views = buildUpdate(context, appWidgetId);
			AppWidgetManager manager = AppWidgetManager.getInstance(context);
			manager.updateAppWidget(appWidgetId, views);
		}
	}
	
	@Override
	public void onEnabled(Context context) {
		 super.onEnabled(context);
		 //Setting the Calendar object to midnight time.
		 Calendar calendar = Calendar.getInstance();
		 calendar.setTimeInMillis(System.currentTimeMillis());
		 calendar.set(Calendar.SECOND, 0);
		 calendar.set(Calendar.MINUTE, 0);
		 calendar.set(Calendar.HOUR, 0);
		 calendar.set(Calendar.AM_PM, Calendar.AM);
		 calendar.add(Calendar.DAY_OF_MONTH, 1);

		 //The fired Intent
		 Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE); //custom intent name
		 PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		 AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);  
		 alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
		 calendar.getTimeInMillis(), 1000*60*60*24, pendingIntent);
	}
	
	@Override
    public void onDeleted(Context context, int[] appWidgetIds) {
		for (int appWidgetId : appWidgetIds) {
			SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME,0);
            prefs.edit().remove("year"+appWidgetId).commit();
            prefs.edit().remove("month"+appWidgetId).commit();
            prefs.edit().remove("date"+appWidgetId).commit();
            prefs.edit().remove("title"+appWidgetId).commit();
            Log.i("mmpud", "this is [" + appWidgetId + "] onDelete!");
		}
		
		//The fired Intent
		 Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE); //custom intent name
		 PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		 AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);  
		 alarmManager.cancel(pendingIntent);
    }
	
	public RemoteViews buildUpdate(Context context, int appWidgetId)
	{
		// Get target YYYY/MM/DD from shared preferences according to different appWidgetId
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME,0);
		targetYear = prefs.getInt("year"+appWidgetId, 0);
		targetMonth = prefs.getInt("month"+appWidgetId, 0);
		targetDate = prefs.getInt("date"+appWidgetId, 0);
		
		// Get the day difference. Be aware if it is "days since" or "days left"
		Calendar calToday = Calendar.getInstance();
		Calendar calTarget = Calendar.getInstance();
		calTarget.set(targetYear, targetMonth, targetDate);
		long diffDays = daysBetween(calToday, calTarget);
		
		RemoteViews view = new RemoteViews(context.getPackageName(),R.layout.day_count_widget_layout);
		if(diffDays > 0) {
			view.setTextViewText(R.id.widget_since_left, context.getResources().getString(R.string.days_left));
			view.setTextViewText(R.id.widget_diffdays, Long.toString(diffDays));
		} else {
			view.setTextViewText(R.id.widget_since_left, context.getResources().getString(R.string.days_since));
			view.setTextViewText(R.id.widget_diffdays, Long.toString(-diffDays));
		}

		// Click on the widget for edit
		Intent intent = new Intent(context, DayCountConfigure.class);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId); 
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
		
		// No request code and no flags for this example
		PendingIntent pender = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		view.setOnClickPendingIntent(R.id.widget, pender);
		
//		// Click on the widget for edit
//		Intent intent = new Intent(context, DayCountDetailDialog.class);
//		//intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId); 
//				
//		// No request code and no flags for this example
//		PendingIntent pender = PendingIntent.getActivity(context, 0, intent, 0);
//		view.setOnClickPendingIntent(R.id.widget, pender);
		
		return view;
	}
	
	public long daysBetween(Calendar startDay, Calendar endDate) {
		long startTime = startDay.getTime().getTime();
		long endTime = endDate.getTime().getTime();
		long diffTime = endTime - startTime;
		return (diffTime / (1000 * 60 * 60 * 24));
	}
}