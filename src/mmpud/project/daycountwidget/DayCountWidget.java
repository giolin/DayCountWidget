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
			targetYear = prefs.getInt(appWidgetId+"year", 0);
			targetMonth = prefs.getInt(appWidgetId+"month", 0);
			targetDate = prefs.getInt(appWidgetId+"date", 0);
			RemoteViews views=buildUpdate(context);
			
			AppWidgetManager manager = AppWidgetManager.getInstance(context);
			manager.updateAppWidget(appWidgetId, views);
			
		}
	}
	
	public RemoteViews buildUpdate(Context ctx)
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
		//when a user clicks the widget, show 'em a google search page with will roger results...
		//Need for intent to an activity showing detail info and edit button
		Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("http://en.wikipedia.org/wiki/Will_Rogers"));
		
		//no request code and no flags for this example
		PendingIntent pender = PendingIntent.getActivity(ctx, 0, intent, 0);
		view.setOnClickPendingIntent(R.id.widget, pender);
		return view;
	}
}