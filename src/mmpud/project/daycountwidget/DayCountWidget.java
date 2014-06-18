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
		targetYear = prefs.getInt("year", 0);
		targetMonth = prefs.getInt("month", 0);
		targetDate = prefs.getInt("date", 0);
		context.startService(new Intent(context,UpdateService.class));
	}

	public static class UpdateService extends Service
	{	
		@Override
		public void onStart(Intent intent, int startId) 
		{
			//build widget update for today
			RemoteViews views=buildUpdate(this);
			
			//push update for widget to the Home activity
			ComponentName widget = new ComponentName(this,DayCountWidget.class);
			AppWidgetManager manager = AppWidgetManager.getInstance(this);
			manager.updateAppWidget(widget, views);
			
		}

		public RemoteViews buildUpdate(Context ctx)
		{
//			Random random = new Random();
//			Resources resources = ctx.getResources();
//			//get all of will's quotes
//			String[] willQuotes = resources.getStringArray(R.array.will_rogers_quotes);
//			
//			//get a random quote to render
//			String quote=willQuotes[random.nextInt(willQuotes.length-1)];
			
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
		
		@Override
		public IBinder onBind(Intent arg0) 
		{
			return null;
		}
		
	}
}