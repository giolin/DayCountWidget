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

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mmpud.project.daycountwidget.data.db.Contract;
import mmpud.project.daycountwidget.data.db.DayCountDbHelper;
import mmpud.project.daycountwidget.util.Dates;
import mmpud.project.daycountwidget.util.Times;

import static mmpud.project.daycountwidget.data.db.Contract.COUNT_BY_DAY;
import static mmpud.project.daycountwidget.data.db.Contract.Widget.BODY_STYLE;
import static mmpud.project.daycountwidget.data.db.Contract.Widget.COUNT_BY;
import static mmpud.project.daycountwidget.data.db.Contract.Widget.EVENT_TITLE;
import static mmpud.project.daycountwidget.data.db.Contract.Widget.TARGET_DATE;

public class DayCountDetail extends AppCompatActivity {

    @Bind(R.id.ll_detailbox) LinearLayout mDetailBox;
    @Bind(R.id.tv_detail_diffdays) TextView mDiffDays;
    @Bind(R.id.tv_detail_targetday) TextView mDate;
    @Bind(R.id.tv_detail_title) TextView mTitle;

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
        Cursor cursor = db.query(Contract.Widget.TABLE_NAME, null,
            Contract.Widget.WIDGET_ID + "=?",
            new String[] {String.valueOf(mAppWidgetId)}, null, null, null);

        long targetDateMillis;
        String title;
        String bodyStyle;
        @Contract.CountBy int countBy;
        LocalDateTime targetDay;
        if (cursor.moveToFirst()) {
            targetDay = Times.getLocalDateTime(cursor.getLong(cursor
                .getColumnIndexOrThrow(TARGET_DATE)));
            title = cursor.getString(cursor.getColumnIndexOrThrow(EVENT_TITLE));
            bodyStyle = cursor.getString(cursor.getColumnIndexOrThrow(BODY_STYLE));
            //noinspection ResourceType
            countBy = cursor.getInt(cursor.getColumnIndexOrThrow(COUNT_BY));
        } else {
            targetDay = LocalDate.now().atStartOfDay();
            title = "";
            bodyStyle = String.valueOf(ContextCompat.getColor(this, R.color.body_black));
            countBy = COUNT_BY_DAY;
        }
        cursor.close();
        db.close();
        mDate.setText(targetDay.format(Times.getDateFormatter()));
        mTitle.setText(title);
        mDetailBox.setBackgroundColor(Integer.parseInt(bodyStyle));
        mDiffDays.setText(Dates.getDiffDaysString(this, countBy, targetDay));
    }

    @OnClick(R.id.btn_detail_edit) void onEditBtnClicked() {
        // click to configure the widget
        Intent intent = new Intent(this, DayCountConfigure.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
