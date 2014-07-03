package mmpud.project.daycountwidget;

import java.util.Calendar;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DayCountDetailDialog extends Activity {
	
	private static final String PREFS_NAME = "mmpud.project.daycountwidget.DayCountWidget";
	private static final String TAG_NAME = "mmpud";
	
	private RelativeLayout rlDetail;
	private LinearLayout linearDetailbox;
	private TextView txtDetailDiffDays;
	private TextView txtDetailTargetDay;
	private TextView txtDetailTitle;
	private Button btnEdit;
	
	private int mAppWidgetId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.day_count_detail_dialog);
		
        // Get the widget id from the intent. 
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        
        }
        Log.d(TAG_NAME, "The widget [" + mAppWidgetId +"]'s detail is showing");
        rlDetail = (RelativeLayout)findViewById(R.id.rl_detail);
        rlDetail.setOnClickListener(mOnClickListener);
        
        linearDetailbox = (LinearLayout)findViewById(R.id.linear_detailbox);
        txtDetailDiffDays = (TextView)findViewById(R.id.txt_detail_diffdays);
        txtDetailTargetDay = (TextView)findViewById(R.id.txt_detail_targetday);
        txtDetailTitle = (TextView)findViewById(R.id.txt_detail_title);
   
		btnEdit = (Button)findViewById(R.id.btn_detail_edit);
		btnEdit.setOnClickListener(mOnClickListener);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		// Update the layout info when resuming
		updateLayoutInfo();
	}
	
	@Override
	protected void onPause() {
		   super.onPause();
		   finish();
	}

	View.OnClickListener mOnClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(v.getId()==R.id.btn_detail_edit) {
				// Click to configure the widget
				Intent intent = new Intent(DayCountDetailDialog.this , DayCountConfigure.class);
				intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			} else {
				finish();
			}
		}
	};
	
	private void updateLayoutInfo() {
		// Get target YYYY/MM/DD from shared preferences according to different appWidgetId
		SharedPreferences prefs = this.getSharedPreferences(PREFS_NAME,0);
		int targetYear = prefs.getInt("year"+mAppWidgetId, 0);
		int targetMonth = prefs.getInt("month"+mAppWidgetId, 0);
		int targetDate = prefs.getInt("date"+mAppWidgetId, 0);
		int bodyColor = prefs.getInt("bodyColor"+mAppWidgetId, 1);
		String targetTitle = prefs.getString("title"+mAppWidgetId, "");
			
		Calendar calToday = Calendar.getInstance();
		Calendar calTarget =  Calendar.getInstance(); // Make calTarget and calTaday the same
				
		// Update the day difference
		calTarget.set(targetYear, targetMonth, targetDate);
		long diffDays = daysBetween(calToday, calTarget);
		if(diffDays > 0) {
			txtDetailDiffDays.setText(diffDays + " DAYS LEFT UNTIL");
			txtDetailTargetDay.setText(targetYear + "/" + (targetMonth+1) + "/" + targetDate);
			txtDetailTitle.setText(targetTitle);
		} else {
			txtDetailDiffDays.setText(-diffDays + " DAYS SINCE");
			txtDetailTargetDay.setText(targetYear + "/" + (targetMonth+1) + "/" + targetDate);
			txtDetailTitle.setText(targetTitle);
		}
		switch(bodyColor) {
		case 1:
			linearDetailbox.setBackgroundColor(Color.parseColor("#e74c3c"));
			break;
		case 2:
			linearDetailbox.setBackgroundColor(Color.parseColor("#e67e22"));
			break;
		case 3:
			linearDetailbox.setBackgroundColor(Color.parseColor("#f1c40f"));
			break;
		case 4:
			linearDetailbox.setBackgroundColor(Color.parseColor("#1abc9c"));
			break;
		case 5:
			linearDetailbox.setBackgroundColor(Color.parseColor("#3498db"));
			break;
		case 6:
			linearDetailbox.setBackgroundColor(Color.parseColor("#34495e"));
			break;
		case 7:
			linearDetailbox.setBackgroundColor(Color.parseColor("#9b59b6"));
			break;
		case 8:
			linearDetailbox.setBackgroundColor(Color.parseColor("#95a5a6"));
			break;
		}
	}
	
	public long daysBetween(Calendar startDay, Calendar endDate) {
		long startTime = startDay.getTime().getTime();
		long endTime = endDate.getTime().getTime();
		long diffTime = endTime - startTime;
		return (diffTime / (1000 * 60 * 60 * 24));
	}
}
