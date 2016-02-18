package mmpud.project.daycountwidget;

import android.app.DatePickerDialog;
import android.app.WallpaperManager;
import android.appwidget.AppWidgetManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringSystem;
import com.facebook.rebound.SpringUtil;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.Weeks;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mmpud.project.daycountwidget.data.db.Contract;
import mmpud.project.daycountwidget.data.db.DayCountDbHelper;
import mmpud.project.daycountwidget.util.Texts;

import static android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID;
import static android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID;
import static mmpud.project.daycountwidget.data.db.Contract.COUNT_BY_DAY;
import static mmpud.project.daycountwidget.data.db.Contract.COUNT_BY_MONTH;
import static mmpud.project.daycountwidget.data.db.Contract.COUNT_BY_WEEK;
import static mmpud.project.daycountwidget.data.db.Contract.COUNT_BY_YEAR;
import static mmpud.project.daycountwidget.data.db.Contract.Widget.BODY_STYLE;
import static mmpud.project.daycountwidget.data.db.Contract.Widget.COUNT_BY;
import static mmpud.project.daycountwidget.data.db.Contract.Widget.EVENT_DESCRIPTION;
import static mmpud.project.daycountwidget.data.db.Contract.Widget.EVENT_TITLE;
import static mmpud.project.daycountwidget.data.db.Contract.Widget.HEADER_STYLE;
import static mmpud.project.daycountwidget.data.db.Contract.Widget.TABLE_NAME;
import static mmpud.project.daycountwidget.data.db.Contract.Widget.TARGET_DATE;
import static mmpud.project.daycountwidget.data.db.Contract.Widget.WIDGET_ID;

public class DayCountConfigure extends AppCompatActivity
    implements RadioGroup.OnCheckedChangeListener, Toolbar.OnMenuItemClickListener {

    private final DateTimeFormatter mDateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");

    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.edt_title) EditText mTitleText;
    @Bind(R.id.tv_date) TextView mDateText;
    @Bind(R.id.radio_group) RadioGroup mRadioGroup;
    @Bind(R.id.preview_window_bg) ImageView mPreviewWindowBg;
    @Bind(R.id.widget_header_bg) ImageView mPreviewWidgetHeaderBg;
    @Bind(R.id.widget_header) TextView mPreviewWidgetHeader;
    @Bind(R.id.widget_body_bg) ImageView mPreviewWidgetBodyBg;
    @Bind(R.id.widget_body) TextView mPreviewWidgetBody;
    @Bind(R.id.head10) View mHeaderPanelBtn;
    @Bind(R.id.body10) View mBodyPanelBtn;
    @Bind(R.id.color_panel_header) RelativeLayout mHeaderColorPanel;
    @Bind(R.id.color_panel_body) RelativeLayout mBodyColorPanel;
    @Bind(R.id.picker1) ColorPicker mHeaderColorPicker;
    @Bind(R.id.picker2) ColorPicker mBodyColorPicker;
    @Bind(R.id.vbar1) ValueBar mHeaderVBar;
    @Bind(R.id.vbar2) ValueBar mBodyVBar;
    @Bind(R.id.sbar1) SaturationBar mHeaderSBar;
    @Bind(R.id.sbar2) SaturationBar mBodySBar;

    @BindColor(R.color.header_green) int initHeaderColor;
    @BindColor(R.color.body_green) int initBodyColor;

    private DayCountDbHelper mDbHelper;
    private DatePickerDialog mDatePickerDialog;
    private int mAppWidgetId;
    private long mTimestamp;
    @Contract.CountBy private int mCountBy;
    private String mHeaderStyle;
    private String mBodyStyle;
    private Spring mSpring1;
    private Spring mSpring2;


    private int mHeaderColorPanelTransY = -1;
    private int mBodyColorPanelTransY = -1;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        // set result CANCELED. this will cause the widget host to cancel out of the widget
        // placement if they press the back button.
        setResult(RESULT_CANCELED);
        // get widget id from the intent.
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            finish();
            return;
        }
        mAppWidgetId = extras.getInt(EXTRA_APPWIDGET_ID, INVALID_APPWIDGET_ID);
        if (mAppWidgetId == INVALID_APPWIDGET_ID) {
            finish();
            return;
        }
        setContentView(R.layout.day_count_configure_layout);
        ButterKnife.bind(this);
        // set up the dimens of color panels
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        final int width = size.x;
        mHeaderColorPanel.post(new Runnable() {
            @Override public void run() {
                ViewGroup.LayoutParams params = mHeaderColorPanel.getLayoutParams();
                params.width = width / 2;
                mHeaderColorPanel.setLayoutParams(params);
                mHeaderColorPanelTransY = (int) (mHeaderColorPanel.getHeight() * 1.2);
                mHeaderColorPanel.setTranslationY(mHeaderColorPanelTransY);
                mHeaderColorPanel.setVisibility(View.VISIBLE);
            }
        });
        mBodyColorPanel.post(new Runnable() {
            @Override public void run() {
                ViewGroup.LayoutParams params = mBodyColorPanel.getLayoutParams();
                params.width = width / 2;
                mBodyColorPanel.setLayoutParams(params);
                mBodyColorPanelTransY = (int) (mBodyColorPanel.getHeight() * 1.2);
                mBodyColorPanel.setTranslationY(mBodyColorPanelTransY);
                mBodyColorPanel.setVisibility(View.VISIBLE);
            }
        });
        mToolbar.setNavigationIcon(R.drawable.ic_cross);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                finish();
            }
        });
        mToolbar.inflateMenu(R.menu.configure_menu);
        mToolbar.setOnMenuItemClickListener(this);

        // query from database
        if (mDbHelper == null) {
            mDbHelper = new DayCountDbHelper(this);
        }
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, WIDGET_ID + "=?",
            new String[] {String.valueOf(mAppWidgetId)}, null, null, null);
        String initTitle;
        if (cursor.moveToFirst()) {
            initTitle = cursor.getString(cursor.getColumnIndexOrThrow(EVENT_TITLE));
            mTimestamp = cursor.getLong(cursor.getColumnIndexOrThrow(TARGET_DATE));
            mHeaderStyle = cursor.getString(cursor.getColumnIndexOrThrow(HEADER_STYLE));
            mBodyStyle = cursor.getString(cursor.getColumnIndexOrThrow(BODY_STYLE));
            //noinspection ResourceType
            mCountBy = cursor.getInt(cursor.getColumnIndexOrThrow(COUNT_BY));
        } else {
            initTitle = "";
            mTimestamp = DateTime.now().getMillis();
            mHeaderStyle = String.valueOf(initHeaderColor);
            mBodyStyle = String.valueOf(initBodyColor);
            mCountBy = COUNT_BY_DAY;
        }
        cursor.close();
        db.close();
        // set title and sample widget title
        if (!TextUtils.isEmpty(initTitle)) {
            mTitleText.setText(initTitle);
            mPreviewWidgetHeader.setText(initTitle);
        }
        // make the widget title change with user input
        mTitleText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // do nothing intended
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // do nothing intended
            }

            @Override
            public void afterTextChanged(Editable s) {
                mPreviewWidgetHeader.setText(mTitleText.getText().toString());
            }
        });
        // set time in the text view
        mDateText.setText(mDateTimeFormatter.print(mTimestamp));
        // set sample widget
        DateTime dateTime = new DateTime(mTimestamp);
        setSampleWidgetContent(dateTime);
        // set current date into datePicker
        mDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                DateTime dateTime = new DateTime()
                    .withYear(year)
                    .withMonthOfYear(monthOfYear + 1)
                    .withDayOfMonth(dayOfMonth)
                    .withTimeAtStartOfDay();
                mTimestamp = dateTime.getMillis();
                mDateText.setText(mDateTimeFormatter.print(mTimestamp));
                setSampleWidgetContent(dateTime);
            }
        }, dateTime.getYear(), dateTime.getMonthOfYear() - 1, dateTime.getDayOfMonth());
        // set radio button
        mRadioGroup.setOnCheckedChangeListener(this);
        ((RadioButton) mRadioGroup.getChildAt(mCountBy)).setChecked(true);
        // set home screen wallpaper to the background sample widget
        mPreviewWindowBg.setImageDrawable(WallpaperManager.getInstance(this).getDrawable());
        // initialize header and body color for sample widget
        mPreviewWidgetHeaderBg.setColorFilter(Integer.valueOf(mHeaderStyle));
        mPreviewWidgetBodyBg.setColorFilter(Integer.valueOf(mBodyStyle));
        // initialize the color the open color panel buttons
        mHeaderPanelBtn.setBackgroundColor(Integer.parseInt(mHeaderStyle));
        mBodyPanelBtn.setBackgroundColor(Integer.parseInt(mBodyStyle));
        // set up the color panels
        mHeaderColorPicker.addValueBar(mHeaderVBar);
        mHeaderColorPicker.addSaturationBar(mHeaderSBar);
        mHeaderColorPicker.setShowOldCenterColor(false);
        mHeaderColorPicker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
            @Override public void onColorChanged(int color) {
                mPreviewWidgetHeaderBg.setColorFilter(color);
                mHeaderStyle = String.valueOf(color);
            }
        });
        mBodyColorPicker.addValueBar(mBodyVBar);
        mBodyColorPicker.addSaturationBar(mBodySBar);
        mBodyColorPicker.setShowOldCenterColor(false);
        mBodyColorPicker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
            @Override public void onColorChanged(int color) {
                mPreviewWidgetBodyBg.setColorFilter(color);
                mBodyStyle = String.valueOf(color);
            }
        });
        SpringSystem mSpringSystem = SpringSystem.create();
        mSpring1 = mSpringSystem.createSpring();
        mSpring2 = mSpringSystem.createSpring();
        mSpring1.addListener(new SimpleSpringListener() {
            @Override public void onSpringUpdate(Spring spring) {
                if (mHeaderColorPanelTransY <= 0) {
                    return;
                }
                float transY = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(),
                    0, 1, mHeaderColorPanelTransY, 0);
                mHeaderColorPanel.setTranslationY(transY);
            }
        });
        mSpring2.addListener(new SimpleSpringListener() {
            @Override public void onSpringUpdate(Spring spring) {
                if (mBodyColorPanelTransY <= 0) {
                    return;
                }
                float transY = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(),
                    0, 1, mBodyColorPanelTransY, 0);
                mBodyColorPanel.setTranslationY(transY);
            }
        });
    }

    @Override protected void onPause() {
        super.onPause();
        finish();
    }

    @OnClick(R.id.tv_date) void showDatePicker() {
        if (mDatePickerDialog != null) {
            mDatePickerDialog.show();
        }
    }

    @OnClick({R.id.head1, R.id.head2, R.id.head3, R.id.head4, R.id.head5, R.id.head6,
        R.id.head7, R.id.head8, R.id.head9}) void onHeadColorPicked(View view) {
        int color = ((ColorDrawable) view.getBackground()).getColor();
        mPreviewWidgetHeaderBg.setColorFilter(color);
        mHeaderStyle = String.valueOf(color);
    }

    @OnClick({R.id.body1, R.id.body2, R.id.body3, R.id.body4, R.id.body5, R.id.body6,
        R.id.body7, R.id.body8, R.id.body9}) void onBodyColorPicked(View view) {
        int color = ((ColorDrawable) view.getBackground()).getColor();
        mPreviewWidgetBodyBg.setColorFilter(color);
        mBodyStyle = String.valueOf(color);
    }

    @OnClick(R.id.head10) void openColorPanelHeader() {
        mHeaderColorPicker.setColor(((ColorDrawable) mHeaderPanelBtn.getBackground()).getColor());
        if (mSpring1 != null) {
            mSpring1.setEndValue(1);
        }
    }

    @OnClick(R.id.close_head) void closeColorPanelHeader() {
        mHeaderPanelBtn.setBackgroundColor(mHeaderColorPicker.getColor());
        if (mSpring1 != null) {
            mSpring1.setEndValue(0);
        }
    }

    @OnClick(R.id.body10) public void openColorPanelBody() {
        mBodyColorPicker.setColor(((ColorDrawable) mBodyPanelBtn.getBackground()).getColor());
        if (mSpring2 != null) {
            mSpring2.setEndValue(1);
        }
    }

    @OnClick(R.id.close_body) void closeColorPanelBody() {
        mBodyPanelBtn.setBackgroundColor(mBodyColorPicker.getColor());
        if (mSpring2 != null) {
            mSpring2.setEndValue(0);
        }
    }

    @Override public void onCheckedChanged(RadioGroup group, int checkedId) {
        //noinspection ResourceType
        mCountBy = group.indexOfChild(group.findViewById(checkedId));
        setSampleWidgetContent(new DateTime(mTimestamp));
    }

    @Override public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.btn_ok) {
            Context context = getApplicationContext();
            // save data in database
            if (mDbHelper == null) {
                mDbHelper = new DayCountDbHelper(DayCountConfigure.this);
            }
            ContentValues values = new ContentValues();
            values.put(WIDGET_ID, mAppWidgetId);
            values.put(EVENT_TITLE, mTitleText.getText().toString());
            values.put(EVENT_DESCRIPTION, "");
            values.put(TARGET_DATE, mTimestamp);
            values.put(COUNT_BY, mCountBy);
            values.put(HEADER_STYLE, mHeaderStyle);
            values.put(BODY_STYLE, mBodyStyle);
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase
                .CONFLICT_REPLACE);
            db.close();
            // push widget update to surface
            RemoteViews views = DayCountWidgetProvider.buildRemoteViews(context, mAppWidgetId);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            appWidgetManager.updateAppWidget(mAppWidgetId, views);
            // make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
            return true;
        }
        return false;
    }

    private void setSampleWidgetContent(DateTime targetDate) {
        int diff;
        DateTime today = DateTime.now().withTimeAtStartOfDay();
        targetDate = targetDate.withTimeAtStartOfDay();
        String str;
        Resources res = getResources();
        switch (mCountBy) {
        case COUNT_BY_DAY: {
            diff = Days.daysBetween(today, targetDate).getDays();
            str = res.getQuantityString(diff > 0 ? R.plurals.widget_day_left
                : R.plurals.widget_day_since, diff, Math.abs(diff));
            break;
        }
        case COUNT_BY_WEEK: {
            diff = Weeks.weeksBetween(today, targetDate).getWeeks();
            str = res.getQuantityString(diff > 0 ? R.plurals.widget_week_left
                : R.plurals.widget_week_since, diff, Math.abs(diff));
            break;
        }
        case COUNT_BY_MONTH: {
            diff = Months.monthsBetween(today, targetDate).getMonths();
            str = res.getQuantityString(diff > 0 ? R.plurals.widget_month_left
                : R.plurals.widget_month_since, diff, Math.abs(diff));
            break;
        }
        case COUNT_BY_YEAR: {
            diff = Years.yearsBetween(today, targetDate).getYears();
            str = res.getQuantityString(diff > 0 ? R.plurals.widget_year_left
                : R.plurals.widget_year_since, diff, Math.abs(diff));
            break;
        }
        default: {
            diff = Days.daysBetween(today, targetDate).getDays();
            str = res.getQuantityString(diff > 0 ? R.plurals.widget_day_left
                : R.plurals.widget_day_since, diff, Math.abs(diff));
        }
        }
        mPreviewWidgetBody.setText(Texts.getResizedText(str));
    }

}
