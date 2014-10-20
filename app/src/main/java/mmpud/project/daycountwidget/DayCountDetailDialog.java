package mmpud.project.daycountwidget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;

import timber.log.Timber;

public class DayCountDetailDialog extends Activity {

    private static final String PREFS_NAME = "mmpud.project.daycountwidget.DayCountWidget";

    private RelativeLayout rlDetailPage;
    //	private LinearLayout llDetailbox;
    private TextView txtDetailDiffDays;
    private TextView txtDetailTargetDay;
    private TextView txtDetailTitle;
    private Button btnEdit;

    private int mAppWidgetId;
    View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btn_detail_edit) {
                // Click to configure the widget
                Intent intent = new Intent(DayCountDetailDialog.this, DayCountConfigure.class);
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else {
                finish();
            }
        }
    };

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
        Timber.d("Widget [" + mAppWidgetId + "]'s detail is shown");
//        llDetailbox = (LinearLayout)findViewById(R.id.ll_detailbox);
        txtDetailDiffDays = (TextView) findViewById(R.id.txt_detail_diffdays);
        txtDetailTargetDay = (TextView) findViewById(R.id.txt_detail_targetday);
        txtDetailTitle = (TextView) findViewById(R.id.txt_detail_title);

        rlDetailPage = (RelativeLayout) findViewById(R.id.rl_detail_page);
        rlDetailPage.setOnClickListener(mOnClickListener);

        btnEdit = (Button) findViewById(R.id.btn_detail_edit);
        btnEdit.setOnClickListener(mOnClickListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update the layout info when resuming
        updateLayoutInfo();
    }

    private void updateLayoutInfo() {
        // Get information: 1. YYYY/MM/DD
        //					2. widget style (but body color only)
        //					3. title
        // from shared preferences according to the appWidgetId
        SharedPreferences prefs = this.getSharedPreferences(PREFS_NAME, 0);
        int targetYear = prefs.getInt("year" + mAppWidgetId, 0);
        int targetMonth = prefs.getInt("month" + mAppWidgetId, 0);
        int targetDate = prefs.getInt("date" + mAppWidgetId, 0);
//		int styleNum = prefs.getInt("styleNum"+mAppWidgetId, 1);
        String targetTitle = prefs.getString("title" + mAppWidgetId, "");

        Calendar calToday = Calendar.getInstance();
        Calendar calTarget = Calendar.getInstance();

        // Update the day difference
        calTarget.set(targetYear, targetMonth, targetDate);
        long diffDays = daysBetween(calToday, calTarget);
        if (diffDays > 0) {
            txtDetailDiffDays.setText(diffDays + " " + getResources().getString(R.string.detail_days_left));
            // Better if different types of day format can be shown according to the local habit of use
            txtDetailTargetDay.setText(targetYear + "/" + (targetMonth + 1) + "/" + targetDate);
            txtDetailTitle.setText(targetTitle);
        } else {
            txtDetailDiffDays.setText(-diffDays + " " + getResources().getString(R.string.detail_days_since));
            txtDetailTargetDay.setText(targetYear + "/" + (targetMonth + 1) + "/" + targetDate);
            txtDetailTitle.setText(targetTitle);
        }

//        String layoutName = "widget_layout" + styleNum;
//        int resourceIDStyle = getResources().getIdentifier(layoutName, "layout","mmpud.project.daycountwidget");
//        LayoutInflater factory = LayoutInflater.from(this);
//        View myView = factory.inflate(resourceIDStyle, null);
//        llDetailbox.setBackground(myView.getBackground());

    }

    public long daysBetween(Calendar startDay, Calendar endDate) {
        long startTime = startDay.getTime().getTime();
        long endTime = endDate.getTime().getTime();
        long diffTime = endTime - startTime;
        return (diffTime / (1000 * 60 * 60 * 24));
    }
}
