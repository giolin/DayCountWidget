package mmpud.project.daycountwidget;

import java.util.Calendar;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.TextView;

public class DayCountConfigure extends Activity{
	private static final String PREFS_NAME = "mmpud.project.daycountwidget.DayCountWidget";
	int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	
	private TextView txtDate, txtDaysSince, txtDaysLeft;
	private DatePicker datePicker;
	private EditText edtTitle;
	private Button btnOK, btnCancel;
	
	private int targetYear;
	private int targetMonth;
	private int targetDate;
	
	private int todayYear;
	private int todayMonth;
	private int todayDate;
	
	long diffDays;
	
    public DayCountConfigure() {
        super();
    }

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		// Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if they press the back button.
        setResult(RESULT_CANCELED);

        // Set the view layout resource to use.
        setContentView(R.layout.day_count_configure_layout);
        
        txtDate = (TextView) findViewById(R.id.txt_date);
		txtDaysSince = (TextView) findViewById(R.id.txt_days_since);
		txtDaysLeft = (TextView) findViewById(R.id.txt_days_left);
		datePicker = (DatePicker) findViewById(R.id.date_picker);
		edtTitle = (EditText) findViewById(R.id.edt_title);
		btnOK = (Button) findViewById(R.id.btn_ok);
		btnCancel = (Button) findViewById(R.id.btn_cancel);
		btnOK.setOnClickListener(mOnClickListener);
		btnCancel.setOnClickListener(mOnClickListener);
		
		final Calendar now = Calendar.getInstance();
		final Calendar pick = Calendar.getInstance();
		todayYear = now.get(Calendar.YEAR);
		todayMonth = now.get(Calendar.MONTH);
		todayDate = now.get(Calendar.DAY_OF_MONTH);
 
		// set current date into textview
		txtDate.setText( new StringBuilder().append("Today: ")
				// Month is 0 based, just add 1
				.append(todayMonth + 1).append("-").append(todayDate).append("-")
				.append(todayYear).append(" "));
 
		// set current date into Date Picker
		datePicker.init(todayYear, todayMonth, todayDate, new OnDateChangedListener(){

			@Override
			public void onDateChanged(DatePicker view, int year,
					int monthOfYear, int dayOfMonth) {
				 	pick.set(year, monthOfYear, dayOfMonth);
					long nowTime = now.getTime().getTime();
					long pickTime = pick.getTime().getTime();
					long diffTime = nowTime - pickTime;
					diffDays = diffTime / (1000 * 60 * 60 * 24);
					
					txtDaysSince.setText("Days since:\n" + diffDays);
					txtDaysLeft.setText("Days left:\n" + -diffDays);
			}
		} );
        
        // Find the widget id from the intent. 
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If they gave us an intent without the widget id, just bail.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
	}
	
	View.OnClickListener mOnClickListener = new View.OnClickListener() {
    	final Context context = DayCountConfigure.this;
		@Override
		public void onClick(View v) {
			if(v.getId()==R.id.btn_ok) {   
	            
				targetYear = datePicker.getYear();
				targetMonth = datePicker.getMonth();
				targetDate = datePicker.getDayOfMonth();
				
				SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
		        prefs.putInt("year", targetYear);
		        prefs.putInt("month", targetMonth);
		        prefs.putInt("date", targetDate);
		        prefs.putString("title", edtTitle.getText().toString());
		        prefs.commit();
				
				RemoteViews views = new RemoteViews(context.getPackageName(),
	            		R.layout.day_count_widget_layout);
	            		
				views.setTextViewText(R.id.first, "The difference between "+
						  targetYear+"-"+(targetMonth+1)+"-"+targetDate+" and "+
						  todayYear+"-"+(todayMonth+1)+"-"+todayDate+" is "+
						  diffDays+" days.");
				
				// Push widget update to surface with newly set prefix
	            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
				appWidgetManager.updateAppWidget(mAppWidgetId, views);
				
		        // Make sure we pass back the original appWidgetId
	            Intent resultValue = new Intent();
	            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
	            setResult(RESULT_OK, resultValue);
	            finish();
			} else if(v.getId()==R.id.btn_cancel) {
				//quit
				finish();
			}
		}
	};
}
