package mmpud.project.daycountwidget;

import java.util.Calendar;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
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
	private TextView sampleWidgetHeader;
	private TextView sampleWidgetBody;
	private EditText edtTitle;
	private Button btnOK;
	private Button btnChangeHeaderColor;
	private Button btnChangeBodyColor;
	
	private Calendar calToday;
	private Calendar calTarget;
		
	private int todayYear;
	private int todayMonth;
	private int todayDate;
	private int headerColor;
	private int bodyColor;
	
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
		edtTitle = (EditText) findViewById(R.id.edt_title);
		sampleWidgetHeader = (TextView) findViewById(R.id.sample_widget_header);
		sampleWidgetBody = (TextView) findViewById(R.id.sample_widget_body);
		btnOK = (Button) findViewById(R.id.btn_ok);
		btnChangeHeaderColor = (Button) findViewById(R.id.btn_change_header_color);
		btnChangeBodyColor = (Button) findViewById(R.id.btn_change_body_color);
		
		btnOK.setOnClickListener(mOnClickListener);
		btnChangeHeaderColor.setOnClickListener(mOnClickListener);
		btnChangeBodyColor.setOnClickListener(mOnClickListener);
		edtTitle.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(edtTitle.getText().toString().equals(getResources().getString(R.string.enter_title))){
					edtTitle.setText("");
				}
				return false;
			}
		});

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
		headerColor = prefs.getInt("headerColor"+mAppWidgetId, 1);
		bodyColor = prefs.getInt("bodyColor"+mAppWidgetId, 1);
		String initTitle = prefs.getString("title"+mAppWidgetId, getResources().getString(R.string.enter_title));
 
		// Set the initial color of Header and Body in the configure page
		switch(headerColor) {
		case 1:
			sampleWidgetHeader.setBackground(getResources().getDrawable(R.drawable.shape_header1));
			btnChangeHeaderColor.setBackgroundColor(Color.parseColor("#c0392b"));
			break;
		case 2:
			sampleWidgetHeader.setBackground(getResources().getDrawable(R.drawable.shape_header2));
			btnChangeHeaderColor.setBackgroundColor(Color.parseColor("#d35400"));
			break;
		case 3:
			sampleWidgetHeader.setBackground(getResources().getDrawable(R.drawable.shape_header3));
			btnChangeHeaderColor.setBackgroundColor(Color.parseColor("#f39c12"));
			break;
		case 4:
			sampleWidgetHeader.setBackground(getResources().getDrawable(R.drawable.shape_header4));
			btnChangeHeaderColor.setBackgroundColor(Color.parseColor("#16a085"));
			break;
		case 5:
			sampleWidgetHeader.setBackground(getResources().getDrawable(R.drawable.shape_header5));
			btnChangeHeaderColor.setBackgroundColor(Color.parseColor("#2980b9"));
			break;
		case 6:
			sampleWidgetHeader.setBackground(getResources().getDrawable(R.drawable.shape_header6));
			btnChangeHeaderColor.setBackgroundColor(Color.parseColor("#2c3e50"));
			break;
		case 7:
			sampleWidgetHeader.setBackground(getResources().getDrawable(R.drawable.shape_header7));
			btnChangeHeaderColor.setBackgroundColor(Color.parseColor("#8e44ad"));
			break;
		case 8:
			sampleWidgetHeader.setBackground(getResources().getDrawable(R.drawable.shape_header8));
			btnChangeHeaderColor.setBackgroundColor(Color.parseColor("#7f8c8d"));
			break;		
		}
		
		switch(bodyColor) {
		case 1:
			sampleWidgetBody.setBackground(getResources().getDrawable(R.drawable.shape_body1));
			btnChangeBodyColor.setBackgroundColor(Color.parseColor("#e74c3c"));
			break;
		case 2:
			sampleWidgetBody.setBackground(getResources().getDrawable(R.drawable.shape_body2));
			btnChangeBodyColor.setBackgroundColor(Color.parseColor("#e67e22"));
			break;
		case 3:
			sampleWidgetBody.setBackground(getResources().getDrawable(R.drawable.shape_body3));
			btnChangeBodyColor.setBackgroundColor(Color.parseColor("#f1c40f"));
			break;
		case 4:
			sampleWidgetBody.setBackground(getResources().getDrawable(R.drawable.shape_body4));
			btnChangeBodyColor.setBackgroundColor(Color.parseColor("#1abc9c"));
			break;
		case 5:
			sampleWidgetBody.setBackground(getResources().getDrawable(R.drawable.shape_body5));
			btnChangeBodyColor.setBackgroundColor(Color.parseColor("#3498db"));
			break;
		case 6:
			sampleWidgetBody.setBackground(getResources().getDrawable(R.drawable.shape_body6));
			btnChangeBodyColor.setBackgroundColor(Color.parseColor("#34495e"));
			break;
		case 7:
			sampleWidgetBody.setBackground(getResources().getDrawable(R.drawable.shape_body7));
			btnChangeBodyColor.setBackgroundColor(Color.parseColor("#9b59b6"));
			break;
		case 8:
			sampleWidgetBody.setBackground(getResources().getDrawable(R.drawable.shape_body8));
			btnChangeBodyColor.setBackgroundColor(Color.parseColor("#95a5a6"));
			break;
		}
		
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
		edtTitle.setText(initTitle);
		
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
//	public void popUpInputWindow() {
//		AlertDialog.Builder builder = new AlertDialog.Builder(this);
//		builder.setTitle("TITLE");
//
//		// Set up the input
//		final EditText input = new EditText(this);
//		
//		// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
//		input.setInputType(InputType.TYPE_CLASS_TEXT);
//		builder.setView(input);
//
//		// Set up the buttons
//		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
//		    @Override
//		    public void onClick(DialogInterface dialog, int which) {
//		    	txtTitle.setText(input.getText().toString());
//		    }
//		});
//		builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
//		    @Override
//		    public void onClick(DialogInterface dialog, int which) {
//		        dialog.cancel();
//		    }
//		});
//		builder.show();
//	}
	
	@Override
	protected void onPause() {
		   super.onPause();
		   finish();
	}
	
	View.OnClickListener mOnClickListener = new View.OnClickListener() {
    	final Context context = DayCountConfigure.this;
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			
			case R.id.btn_change_header_color:
				// Pop up a window to choose color
				// custom dialog
				final Dialog dialogHeaderColor = new Dialog(context);
				dialogHeaderColor.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialogHeaderColor.setContentView(R.layout.select_header_color_dialog);
				// set the custom dialog components - text, image and button
				View headerColor1 = (View) dialogHeaderColor.findViewById(R.id.header_color1);
				View headerColor2 = (View) dialogHeaderColor.findViewById(R.id.header_color2);
				View headerColor3 = (View) dialogHeaderColor.findViewById(R.id.header_color3);
				View headerColor4 = (View) dialogHeaderColor.findViewById(R.id.header_color4);
				View headerColor5 = (View) dialogHeaderColor.findViewById(R.id.header_color5);
				View headerColor6 = (View) dialogHeaderColor.findViewById(R.id.header_color6);
				View headerColor7 = (View) dialogHeaderColor.findViewById(R.id.header_color7);
				View headerColor8 = (View) dialogHeaderColor.findViewById(R.id.header_color8);

				View.OnClickListener selectHeaderColorListener = new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						switch(v.getId()) {
						case R.id.header_color1:
							sampleWidgetHeader.setBackground(getResources().getDrawable(R.drawable.shape_header1));
							btnChangeHeaderColor.setBackgroundColor(Color.parseColor("#c0392b"));
							headerColor = 1;
							dialogHeaderColor.dismiss();
							break;
						case R.id.header_color2:
							sampleWidgetHeader.setBackground(getResources().getDrawable(R.drawable.shape_header2));
							btnChangeHeaderColor.setBackgroundColor(Color.parseColor("#d35400"));
							headerColor = 2;
							dialogHeaderColor.dismiss();
							break;
						case R.id.header_color3:
							sampleWidgetHeader.setBackground(getResources().getDrawable(R.drawable.shape_header3));
							btnChangeHeaderColor.setBackgroundColor(Color.parseColor("#f39c12"));
							headerColor = 3;
							dialogHeaderColor.dismiss();
							break;
						case R.id.header_color4:
							sampleWidgetHeader.setBackground(getResources().getDrawable(R.drawable.shape_header4));
							btnChangeHeaderColor.setBackgroundColor(Color.parseColor("#16a085"));
							headerColor = 4;
							dialogHeaderColor.dismiss();
							break;
						case R.id.header_color5:
							sampleWidgetHeader.setBackground(getResources().getDrawable(R.drawable.shape_header5));
							btnChangeHeaderColor.setBackgroundColor(Color.parseColor("#2980b9"));
							headerColor = 5;
							dialogHeaderColor.dismiss();
							break;
						case R.id.header_color6:
							sampleWidgetHeader.setBackground(getResources().getDrawable(R.drawable.shape_header6));
							btnChangeHeaderColor.setBackgroundColor(Color.parseColor("#2c3e50"));
							headerColor = 6;
							dialogHeaderColor.dismiss();
							break;
						case R.id.header_color7:
							sampleWidgetHeader.setBackground(getResources().getDrawable(R.drawable.shape_header7));
							btnChangeHeaderColor.setBackgroundColor(Color.parseColor("#8e44ad"));
							headerColor = 7;
							dialogHeaderColor.dismiss();
							break;
						case R.id.header_color8:
							sampleWidgetHeader.setBackground(getResources().getDrawable(R.drawable.shape_header8));
							btnChangeHeaderColor.setBackgroundColor(Color.parseColor("#7f8c8d"));
							headerColor = 8;
							dialogHeaderColor.dismiss();
							break;
						}
						
					}
					
				};
				
				headerColor1.setOnClickListener(selectHeaderColorListener);
				headerColor2.setOnClickListener(selectHeaderColorListener);
				headerColor3.setOnClickListener(selectHeaderColorListener);
				headerColor4.setOnClickListener(selectHeaderColorListener);
				headerColor5.setOnClickListener(selectHeaderColorListener);
				headerColor6.setOnClickListener(selectHeaderColorListener);
				headerColor7.setOnClickListener(selectHeaderColorListener);
				headerColor8.setOnClickListener(selectHeaderColorListener);
				
				dialogHeaderColor.show();
				
				break;
			
			case R.id.btn_change_body_color:
				// Pop up a window to choose color
				// custom dialog
				final Dialog dialogBodyColor = new Dialog(context);
				dialogBodyColor.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialogBodyColor.setContentView(R.layout.select_body_color_dialog);
				// set the custom dialog components - text, image and button
				View bodyColor1 = (View) dialogBodyColor.findViewById(R.id.body_color1);
				View bodyColor2 = (View) dialogBodyColor.findViewById(R.id.body_color2);
				View bodyColor3 = (View) dialogBodyColor.findViewById(R.id.body_color3);
				View bodyColor4 = (View) dialogBodyColor.findViewById(R.id.body_color4);
				View bodyColor5 = (View) dialogBodyColor.findViewById(R.id.body_color5);
				View bodyColor6 = (View) dialogBodyColor.findViewById(R.id.body_color6);
				View bodyColor7 = (View) dialogBodyColor.findViewById(R.id.body_color7);
				View bodyColor8 = (View) dialogBodyColor.findViewById(R.id.body_color8);

				View.OnClickListener selectBodyColorListener = new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						switch(v.getId()) {
						case R.id.body_color1:
							sampleWidgetBody.setBackground(getResources().getDrawable(R.drawable.shape_body1));
							btnChangeBodyColor.setBackgroundColor(Color.parseColor("#e74c3c"));
							bodyColor = 1;
							dialogBodyColor.dismiss();
							break;
						case R.id.body_color2:
							sampleWidgetBody.setBackground(getResources().getDrawable(R.drawable.shape_body2));
							btnChangeBodyColor.setBackgroundColor(Color.parseColor("#e67e22"));
							bodyColor = 2;
							dialogBodyColor.dismiss();
							break;
						case R.id.body_color3:
							sampleWidgetBody.setBackground(getResources().getDrawable(R.drawable.shape_body3));
							btnChangeBodyColor.setBackgroundColor(Color.parseColor("#f1c40f"));
							bodyColor = 3;
							dialogBodyColor.dismiss();
							break;
						case R.id.body_color4:
							sampleWidgetBody.setBackground(getResources().getDrawable(R.drawable.shape_body4));
							btnChangeBodyColor.setBackgroundColor(Color.parseColor("#1abc9c"));
							bodyColor = 4;
							dialogBodyColor.dismiss();
							break;
						case R.id.body_color5:
							sampleWidgetBody.setBackground(getResources().getDrawable(R.drawable.shape_body5));
							btnChangeBodyColor.setBackgroundColor(Color.parseColor("#3498db"));
							bodyColor = 5;
							dialogBodyColor.dismiss();
							break;
						case R.id.body_color6:
							sampleWidgetBody.setBackground(getResources().getDrawable(R.drawable.shape_body6));
							btnChangeBodyColor.setBackgroundColor(Color.parseColor("#34495e"));
							bodyColor = 6;
							dialogBodyColor.dismiss();
							break;
						case R.id.body_color7:
							sampleWidgetBody.setBackground(getResources().getDrawable(R.drawable.shape_body7));
							btnChangeBodyColor.setBackgroundColor(Color.parseColor("#9b59b6"));
							bodyColor = 7;
							dialogBodyColor.dismiss();
							break;
						case R.id.body_color8:
							sampleWidgetBody.setBackground(getResources().getDrawable(R.drawable.shape_body8));
							btnChangeBodyColor.setBackgroundColor(Color.parseColor("#95a5a6"));
							bodyColor = 8;
							dialogBodyColor.dismiss();
							break;
						}
						
					}
					
				};
				
				bodyColor1.setOnClickListener(selectBodyColorListener);
				bodyColor2.setOnClickListener(selectBodyColorListener);
				bodyColor3.setOnClickListener(selectBodyColorListener);
				bodyColor4.setOnClickListener(selectBodyColorListener);
				bodyColor5.setOnClickListener(selectBodyColorListener);
				bodyColor6.setOnClickListener(selectBodyColorListener);
				bodyColor7.setOnClickListener(selectBodyColorListener);
				bodyColor8.setOnClickListener(selectBodyColorListener);
				
				dialogBodyColor.show();
				break;
				
			case R.id.btn_ok:
				
				// Save target YYYY/MM/DD and title in shared preferences
				// We also need to save the widget style in the shared preferences
				SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
		        prefs.putInt("year"+mAppWidgetId, datePicker.getYear());
		        prefs.putInt("month"+mAppWidgetId,  datePicker.getMonth());
		        prefs.putInt("date"+mAppWidgetId, datePicker.getDayOfMonth());
		        prefs.putInt("headerColor"+mAppWidgetId, headerColor);
		        prefs.putInt("bodyColor"+mAppWidgetId, bodyColor);
		        prefs.putString("title"+mAppWidgetId, edtTitle.getText().toString());
		        prefs.commit();
				
		        RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.day_count_widget_layout);
		        // Set the initial color of Header and Body in the configure page
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
