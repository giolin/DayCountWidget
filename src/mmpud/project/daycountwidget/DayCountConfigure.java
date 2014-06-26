package mmpud.project.daycountwidget;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.TextView;

public class DayCountConfigure extends Activity {
	
	private static final String PREFS_NAME = "mmpud.project.daycountwidget.DayCountWidget";
	private static final String TAG_NAME = "mmpud";
	
	private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	
	private DatePicker datePicker;
	private TextView txtDaysSinceLeft;
	private TextView txtDaysCount;
	private TextView txtTitle;
	private Button btnOK;
	
	private Calendar calToday;
	private Calendar calTarget;
		
	private int todayYear;
	private int todayMonth;
	private int todayDate;
	
	private long diffDays;
	
    public DayCountConfigure() {
        super();
    }

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);    
		
		// Remove title bar
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
	    // Get the widget id from the intent. 
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        
		// Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if they press the back button.
        setResult(RESULT_CANCELED);
        
        // If they gave us an intent without the widget id, just bail.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
        
        // Set up the view layout resource to use.
        setContentView(R.layout.day_count_configure_layout);
        
        datePicker = (DatePicker) findViewById(R.id.date_picker);
        txtDaysSinceLeft = (TextView) findViewById(R.id.txt_days_since_left);
		txtDaysCount = (TextView) findViewById(R.id.txt_days_count);
		txtTitle = (TextView) findViewById(R.id.txt_title);
		btnOK = (Button) findViewById(R.id.btn_ok);
		
		btnOK.setOnClickListener(mOnClickListener);
		txtTitle.setOnClickListener(mOnClickListener);

		calToday = Calendar.getInstance();
		calTarget = Calendar.getInstance();
		
		todayYear = calToday.get(Calendar.YEAR);
		todayMonth = calToday.get(Calendar.MONTH);
		todayDate = calToday.get(Calendar.DAY_OF_MONTH);
		
		// Get target YYYY/MM/DD from shared preferences according to the appWidgetId
		SharedPreferences prefs = this.getSharedPreferences(PREFS_NAME,0);
		int initYear = prefs.getInt("year"+mAppWidgetId, todayYear);
		int initMonth = prefs.getInt("month"+mAppWidgetId, todayMonth);
		int initDate = prefs.getInt("date"+mAppWidgetId, todayDate);
		String initTitle = prefs.getString("title"+mAppWidgetId, getResources().getString(R.string.enter_title));
 
		// Update the day difference
		calTarget.set(initYear, initMonth, initDate);
		diffDays = daysBetween(calToday, calTarget);
		if(diffDays > 0) {
			txtDaysSinceLeft.setText(R.string.days_left);
			txtDaysCount.setText(Long.toString(diffDays));
		} else {
			txtDaysSinceLeft.setText(R.string.days_since);
			txtDaysCount.setText(Long.toString(-diffDays));
		}
		
		// Set title
		txtTitle.setText(initTitle);
		
		// Set current date into datePicker
		datePicker.init(initYear, initMonth, initDate, new OnDateChangedListener() {
			@Override
			public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				Log.d(TAG_NAME, "date changed" + year + "/" + monthOfYear + "/" + dayOfMonth);
				// Update the bays difference
				calTarget.set(year, monthOfYear, dayOfMonth);
				diffDays = daysBetween(calToday, calTarget);
				if(diffDays > 0) {
					txtDaysSinceLeft.setText(R.string.days_left);
					txtDaysCount.setText(Long.toString(diffDays));
				} else {
					txtDaysSinceLeft.setText(R.string.days_since);
					txtDaysCount.setText(Long.toString(-diffDays));
				}
			}
		} );
	}
	
	// Pop up for title input
	public void popUpInputWindow() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("TITLE");

		// Set up the input
		final EditText input = new EditText(this);
		
		// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
		input.setInputType(InputType.TYPE_CLASS_TEXT);
		builder.setView(input);

		// Set up the buttons
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    	txtTitle.setText(input.getText().toString());
		    }
		});
		builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        dialog.cancel();
		    }
		});
		builder.show();
	}
	
	View.OnClickListener mOnClickListener = new View.OnClickListener() {
    	final Context context = DayCountConfigure.this;
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			
			case R.id.txt_title:
				popUpInputWindow();
				break;
				
			case R.id.btn_ok:
				
				// Save target YYYY/MM/DD and title in shared preferences
				// We also need to save the widget style in the shared preferences
				SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
		        prefs.putInt("year"+mAppWidgetId, datePicker.getYear());
		        prefs.putInt("month"+mAppWidgetId,  datePicker.getMonth());
		        prefs.putInt("date"+mAppWidgetId, datePicker.getDayOfMonth());
		        prefs.putString("title"+mAppWidgetId, txtTitle.getText().toString());
		        prefs.commit();
				
		        RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.day_count_widget_layout);
		        if(diffDays > 0) {
					views.setTextViewText(R.id.widget_since_left, getResources().getString(R.string.days_left));
					views.setTextViewText(R.id.widget_diffdays, Long.toString(diffDays));
				} else {
					views.setTextViewText(R.id.widget_since_left, getResources().getString(R.string.days_since));
					views.setTextViewText(R.id.widget_diffdays, Long.toString(-diffDays));
				}
				
//				// Click on the widget for edit
//				Intent intent = new Intent(context, DayCountConfigure.class);
//				intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId); 
//				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
//				// No request code and no flags for this example
//				PendingIntent pender = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//				views.setOnClickPendingIntent(R.id.widget, pender);
		        
		        Log.d(TAG_NAME, "The widget [" + mAppWidgetId + "] is set");
				
		        // Click on the widget for editing
				Intent intent = new Intent(context, DayCountDetailDialog.class);
				intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId); 
//				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));		
				
				// No request code and no flags for this example
				PendingIntent pender = PendingIntent.getActivity(context, 0, intent, 0);
				views.setOnClickPendingIntent(R.id.widget, pender);
				
				// Push widget update to surface
	            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
				appWidgetManager.updateAppWidget(mAppWidgetId, views);
				
		        // Make sure we pass back the original appWidgetId
	            Intent resultValue = new Intent();
	            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
	            setResult(RESULT_OK, resultValue);
	            finish();
				break;
			}
		}
	};
	
	public long daysBetween(Calendar startDay, Calendar endDate) {
		long startTime = startDay.getTime().getTime();
		long endTime = endDate.getTime().getTime();
		long diffTime = endTime - startTime;
		return (diffTime / (1000 * 60 * 60 * 24));
	}
}
