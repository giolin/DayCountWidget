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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import mmpud.project.daycountwidget.util.Utils;
import timber.log.Timber;

public class DayCountDetail extends Activity {

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
                Intent intent = new Intent(DayCountDetail.this, DayCountConfigure.class);
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

        // Get the widget id from the intent. If is it new created Android will put the id
        // in the intent with key AppWidgetManager.EXTRA_APPWIDGET_ID
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        Timber.d("Widget [" + mAppWidgetId + "]'s detail is shown");
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
        // Get information: 1. YYYY-MM-DD
        //					2. title
        //					3. widget style
        // from shared preferences according to the appWidgetId
        SharedPreferences prefs = this.getSharedPreferences(PREFS_NAME, 0);
        String targetDate = prefs.getString(Utils.KEY_TARGET_DATE + mAppWidgetId, "0-0-0");
        String targetTitle = prefs.getString(Utils.KEY_TITLE + mAppWidgetId, "");

        Calendar calToday = Calendar.getInstance();
        Calendar calTarget = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            calTarget.setTime(sdf.parse(targetDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Update the day difference
        long diffDays = Utils.daysBetween(calToday, calTarget);

        if (diffDays > 0) {
            txtDetailDiffDays.setText(diffDays + " " + getResources().getString(R.string.detail_days_left));
            // Better if different types of day format can be shown according to the local habit of use
            txtDetailTargetDay.setText(targetDate);
            txtDetailTitle.setText(targetTitle);
        } else {
            txtDetailDiffDays.setText(-diffDays + " " + getResources().getString(R.string.detail_days_since));
            txtDetailTargetDay.setText(targetDate);
            txtDetailTitle.setText(targetTitle);
        }

    }

}
