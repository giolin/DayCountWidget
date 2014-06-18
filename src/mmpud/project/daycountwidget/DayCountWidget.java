package mmpud.project.daycountwidget;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

public class DayCountWidget extends AppWidgetProvider 
{
	private static final String PREFS_NAME = "mmpud.project.daycountwidget.DayCountWidget";
	private static int targetYear;
	private static int targetMonth;
	private static int targetDate;

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) 
	{
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME,0);
		for (int appWidgetId : appWidgetIds) {
			targetYear = prefs.getInt("year"+appWidgetId, 0);
			targetMonth = prefs.getInt("month"+appWidgetId, 0);
			targetDate = prefs.getInt("date"+appWidgetId, 0);
			RemoteViews views=buildUpdate(context, appWidgetId);
			
			AppWidgetManager manager = AppWidgetManager.getInstance(context);
			manager.updateAppWidget(appWidgetId, views);
			
		}
	}
	
	@Override
    public void onDeleted(Context context, int[] appWidgetIds) {
            // TODO Auto-generated method stub
            for (int appWidgetId : appWidgetIds) {
            	SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME,0);
            	prefs.edit().remove("year"+appWidgetId).commit();
            	prefs.edit().remove("month"+appWidgetId).commit();
            	prefs.edit().remove("date"+appWidgetId).commit();
                Log.i("mmpud", "this is [" + appWidgetId + "] onDelete!");
            }
    }
	
	public RemoteViews buildUpdate(Context ctx, int appWidgetId)
	{
		
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		start.set(targetYear, targetMonth, targetDate);
		Date startDate = start.getTime();
		Date endDate = end.getTime();
		long startTime = startDate.getTime();
		long endTime = endDate.getTime();
		long diffTime = endTime - startTime;
		long diffDays = diffTime / (1000 * 60 * 60 * 24);
		DateFormat dateFormat = DateFormat.getDateInstance();

		RemoteViews view = new RemoteViews(ctx.getPackageName(),R.layout.day_count_widget_layout);
		view.setTextViewText(R.id.first, "The difference between "+
				  dateFormat.format(startDate)+" and "+
				  dateFormat.format(endDate)+" is "+
				  diffDays+" days.");
		//Click on the widget for edit
		Intent intent = new Intent(ctx, DayCountConfigure.class);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId); 
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
		//no request code and no flags for this example
		PendingIntent pender = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		view.setOnClickPendingIntent(R.id.widget, pender);
		return view;
	}
}