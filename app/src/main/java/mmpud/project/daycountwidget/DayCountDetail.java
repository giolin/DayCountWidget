package mmpud.project.daycountwidget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import mmpud.project.daycountwidget.util.Utils;
import timber.log.Timber;

public class DayCountDetail extends Activity {

    private RelativeLayout mRlDetailPage;
    private LinearLayout mLlDetailbox;
    private TextView mTvDetailDiffDays;
    private TextView mTvDetailTargetDay;
    private TextView mTvDetailTitle;
    private Button mBtnEdit;

    private int mAppWidgetId;

    View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btn_detail_edit) {
                // click to configure the widget
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

        // get the widget id from the intent. If is it new created Android will put the id
        // in the intent with key AppWidgetManager.EXTRA_APPWIDGET_ID
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        Timber.d("Widget [" + mAppWidgetId + "]'s detail is shown");
        mLlDetailbox = (LinearLayout) findViewById(R.id.ll_detailbox);
        mTvDetailDiffDays = (TextView) findViewById(R.id.tv_detail_diffdays);
        mTvDetailTargetDay = (TextView) findViewById(R.id.tv_detail_targetday);
        mTvDetailTitle = (TextView) findViewById(R.id.tv_detail_title);

        mRlDetailPage = (RelativeLayout) findViewById(R.id.rl_detail_page);
        mRlDetailPage.setOnClickListener(mOnClickListener);

        mBtnEdit = (Button) findViewById(R.id.btn_detail_edit);
        mBtnEdit.setOnClickListener(mOnClickListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // update the layout info when resuming
        updateLayoutInfo();
    }

    private void updateLayoutInfo() {
        // get information: 1. YYYY-MM-DD
        //					2. title
        //					3. body style
        // from shared preferences according to the appWidgetId
        SharedPreferences prefs = this.getSharedPreferences(Utils.PREFS_NAME, 0);
        String targetDate = prefs.getString(Utils.KEY_TARGET_DATE + mAppWidgetId, "0-0-0");
        String targetTitle = prefs.getString(Utils.KEY_TITLE + mAppWidgetId, "");
        String bodyStyle = prefs.getString(Utils.KEY_STYLE_BODY + mAppWidgetId, "body_black");

        mTvDetailTargetDay.setText(targetDate);
        mTvDetailTitle.setText(targetTitle);

        // set the background color of the detail box
        int resourceIdStyle = getResources().getIdentifier(bodyStyle + "_config",
                "drawable", "mmpud.project.daycountwidget");
        Bitmap bitmapBg = BitmapFactory.decodeResource(getResources(), resourceIdStyle);
        Bitmap onePixelBitmap = Bitmap.createScaledBitmap(bitmapBg, 1, 1, true);
        int pixel = onePixelBitmap.getPixel(0,0);
        mLlDetailbox.setBackgroundColor(pixel);

        // evaluate the day difference
        Calendar calToday = Calendar.getInstance();
        Calendar calTarget = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            calTarget.setTime(sdf.parse(targetDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int diffDays = Utils.daysBetween(calToday, calTarget);

        if (diffDays > 0) {
            String strDaysLeft = getResources().getQuantityString(R.plurals.detail_days_left,
                    diffDays, diffDays);
            mTvDetailDiffDays.setText(strDaysLeft);
        } else {
            diffDays = -diffDays;
            String strDaysLeft = getResources().getQuantityString(R.plurals.detail_days_since,
                    diffDays, diffDays);
            mTvDetailDiffDays.setText(strDaysLeft);
        }
    }

}
