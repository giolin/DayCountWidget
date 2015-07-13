package mmpud.project.daycountwidget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mmpud.project.daycountwidget.data.db.DayCountContract;
import mmpud.project.daycountwidget.data.db.DayCountDbHelper;

public class DayCountDetail extends AppCompatActivity {

    @Bind(R.id.ll_detailbox) LinearLayout mLlDetailbox;
    @Bind(R.id.tv_detail_diffdays) TextView mTvDetailDiffDays;
    @Bind(R.id.tv_detail_targetday) TextView mTvDetailTargetDay;
    @Bind(R.id.tv_detail_title) TextView mTvDetailTitle;

    private DayCountDbHelper mDbHelper;
    private int mAppWidgetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.day_count_detail_dialog);
        ButterKnife.bind(this);
        // get the widget id from the intent. If is it new created Android will put the id
        // in the intent with key AppWidgetManager.EXTRA_APPWIDGET_ID
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
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
        // query from database
        if (mDbHelper == null) {
            mDbHelper = new DayCountDbHelper(this);
        }
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.query(DayCountContract.DayCountWidget.TABLE_NAME, null,
            DayCountContract.DayCountWidget.WIDGET_ID + "=?",
            new String[] {String.valueOf(mAppWidgetId)}, null, null, null);

        long targetDateMillis;
        String title;
        String bodyStyle;
        if (cursor.moveToFirst()) {
            targetDateMillis = cursor.getLong(cursor.getColumnIndexOrThrow(DayCountContract
                .DayCountWidget.TARGET_DATE));
            title = cursor.getString(cursor.getColumnIndexOrThrow(DayCountContract.DayCountWidget
                .EVENT_TITLE));
            bodyStyle = cursor.getString(cursor.getColumnIndexOrThrow(DayCountContract
                .DayCountWidget.BODY_STYLE));
        } else {
            targetDateMillis = DateTime.now().getMillis();
            title = "";
            bodyStyle = String.valueOf(ContextCompat.getColor(this, R.color.body_black));
        }
        cursor.close();
        db.close();

        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");

        mTvDetailTargetDay.setText(formatter.print(targetDateMillis));
        mTvDetailTitle.setText(title);

        mLlDetailbox.setBackgroundColor(Integer.parseInt(bodyStyle));

        DateTime targetDate = new DateTime(targetDateMillis);
        int diffDays = Days.daysBetween(DateTime.now().withTimeAtStartOfDay(),
            targetDate.withTimeAtStartOfDay()).getDays();

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

    @OnClick(R.id.btn_detail_edit) void onEditBtnClicked() {
        // click to configure the widget
        Intent intent = new Intent(this, DayCountConfigure.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
