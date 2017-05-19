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
import android.widget.SeekBar;
import android.widget.TextView;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringSystem;
import com.facebook.rebound.SpringUtil;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.temporal.ChronoUnit;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mmpud.project.daycountwidget.data.db.Contract;
import mmpud.project.daycountwidget.data.db.DayCountDbHelper;
import mmpud.project.daycountwidget.util.Texts;
import mmpud.project.daycountwidget.util.Times;

import static android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID;
import static android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID;
import static mmpud.project.daycountwidget.data.db.Contract.COUNT_BY_DAY;
import static mmpud.project.daycountwidget.data.db.Contract.COUNT_BY_MONTH;
import static mmpud.project.daycountwidget.data.db.Contract.COUNT_BY_WEEK;
import static mmpud.project.daycountwidget.data.db.Contract.COUNT_BY_YEAR;
import static mmpud.project.daycountwidget.data.db.Contract.Widget.ALPHA;
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

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.edt_title) EditText mTitleText;
    @BindView(R.id.tv_date) TextView mDateText;
    @BindView(R.id.radio_group) RadioGroup mRadioGroup;
    @BindView(R.id.preview_window_bg) ImageView mPreviewWindowBg;
    @BindView(R.id.widget_header_bg) ImageView mPreviewWidgetHeaderBg;
    @BindView(R.id.widget_header) TextView mPreviewWidgetHeader;
    @BindView(R.id.widget_body_bg) ImageView mPreviewWidgetBodyBg;
    @BindView(R.id.widget_body) TextView mPreviewWidgetBody;
    @BindView(R.id.head10) View mHeaderPanelBtn;
    @BindView(R.id.body10) View mBodyPanelBtn;
    @BindView(R.id.text_trans) TextView mTextTrans;
    @BindView(R.id.seek_bar_trans) SeekBar mSeekBarTrans;
    @BindView(R.id.color_panel_header) RelativeLayout mHeaderColorPanel;
    @BindView(R.id.color_panel_body) RelativeLayout mBodyColorPanel;
    @BindView(R.id.picker1) ColorPicker mHeaderColorPicker;
    @BindView(R.id.picker2) ColorPicker mBodyColorPicker;
    @BindView(R.id.vbar1) ValueBar mHeaderVBar;
    @BindView(R.id.vbar2) ValueBar mBodyVBar;
    @BindView(R.id.sbar1) SaturationBar mHeaderSBar;
    @BindView(R.id.sbar2) SaturationBar mBodySBar;

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
        float initAlpha;
        if (cursor.moveToFirst()) {
            initTitle = cursor.getString(cursor.getColumnIndexOrThrow(EVENT_TITLE));
            mTimestamp = cursor.getLong(cursor.getColumnIndexOrThrow(TARGET_DATE));
            mHeaderStyle = cursor.getString(cursor.getColumnIndexOrThrow(HEADER_STYLE));
            mBodyStyle = cursor.getString(cursor.getColumnIndexOrThrow(BODY_STYLE));
            //noinspection ResourceType
            mCountBy = cursor.getInt(cursor.getColumnIndexOrThrow(COUNT_BY));
            initAlpha = cursor.getFloat(cursor.getColumnIndexOrThrow(ALPHA));
        } else {
            initTitle = "";
            mTimestamp = Times.getStartOfDayMillis();
            mHeaderStyle = String.valueOf(initHeaderColor);
            mBodyStyle = String.valueOf(initBodyColor);
            mCountBy = COUNT_BY_DAY;
            initAlpha = 1;
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
        // the seek bar
        mSeekBarTrans.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float alpha = (float) (progress / 100.0);
                mTextTrans.setText(String.valueOf(progress));
                mPreviewWidgetBodyBg.setAlpha(alpha);
                mPreviewWidgetHeaderBg.setAlpha(alpha);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // do nothing intended
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // do nothing intended
            }
        });
        int alphaPercent = (int) (initAlpha * 100);
        mTextTrans.setText(String.valueOf(alphaPercent));
        mSeekBarTrans.setProgress(alphaPercent);
        // set time in the text view
        LocalDateTime initDateTime = Times.getLocalDateTime(mTimestamp);
        mDateText.setText(initDateTime.format(Times.getDateFormatter()));
        // set sample widget
        setSampleWidgetContent(initDateTime);
        // set current date into datePicker
        mDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                LocalDateTime targetDay = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
                    .atStartOfDay();
                mTimestamp = targetDay.atZone(ZoneOffset.UTC).toInstant().toEpochMilli();
                mDateText.setText(targetDay.format(Times.getDateFormatter()));
                setSampleWidgetContent(targetDay);
            }
        }, initDateTime.getYear(), initDateTime.getMonthValue() - 1, initDateTime.getDayOfMonth());
        // set radio button
        mRadioGroup.setOnCheckedChangeListener(this);
        ((RadioButton) mRadioGroup.getChildAt(mCountBy)).setChecked(true);
        // set home screen wallpaper to the background sample widget
        mPreviewWindowBg.setImageDrawable(WallpaperManager.getInstance(this).getDrawable());
        // initialize header and body color for sample widget
        mPreviewWidgetHeaderBg.setColorFilter(Integer.valueOf(mHeaderStyle));
        mPreviewWidgetBodyBg.setColorFilter(Integer.valueOf(mBodyStyle));
        mPreviewWidgetBodyBg.setAlpha(initAlpha);
        mPreviewWidgetHeaderBg.setAlpha(initAlpha);
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
        setSampleWidgetContent(Times.getLocalDateTime(mTimestamp));
    }

    @Override public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.btn_ok) {
            Context context = getApplicationContext();
            // save data in database
            if (mDbHelper == null) {
                mDbHelper = new DayCountDbHelper(DayCountConfigure.this);
            }
            float alpha = (float) (mSeekBarTrans.getProgress() / 100.0);
            ContentValues values = new ContentValues();
            values.put(WIDGET_ID, mAppWidgetId);
            values.put(EVENT_TITLE, mTitleText.getText().toString());
            values.put(EVENT_DESCRIPTION, "");
            values.put(TARGET_DATE, mTimestamp);
            values.put(COUNT_BY, mCountBy);
            values.put(HEADER_STYLE, mHeaderStyle);
            values.put(BODY_STYLE, mBodyStyle);
            values.put(ALPHA, alpha);
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

    private void setSampleWidgetContent(LocalDateTime localDateTime) {
        int diff;
        String str;
        Resources res = getResources();
        switch (mCountBy) {
        case COUNT_BY_DAY: {
            diff = (int) ChronoUnit.DAYS.between(LocalDate.now(), localDateTime.toLocalDate());
            str = res.getQuantityString(diff > 0 ? R.plurals.widget_day_left
                : R.plurals.widget_day_since, diff, Math.abs(diff));
            break;
        }
        case COUNT_BY_WEEK: {
            diff = (int) ChronoUnit.DAYS.between(LocalDate.now(), localDateTime.toLocalDate()) / 7;
            str = res.getQuantityString(diff > 0 ? R.plurals.widget_week_left
                : R.plurals.widget_week_since, diff, Math.abs(diff));
            break;
        }
        case COUNT_BY_MONTH: {
            diff = (int) ChronoUnit.MONTHS.between(LocalDate.now(), localDateTime.toLocalDate());
            str = res.getQuantityString(diff > 0 ? R.plurals.widget_month_left
                : R.plurals.widget_month_since, diff, Math.abs(diff));
            break;
        }
        case COUNT_BY_YEAR: {
            diff = (int) ChronoUnit.YEARS.between(LocalDate.now(), localDateTime.toLocalDate());
            str = res.getQuantityString(diff > 0 ? R.plurals.widget_year_left
                : R.plurals.widget_year_since, diff, Math.abs(diff));
            break;
        }
        default: {
            diff = (int) ChronoUnit.DAYS.between(LocalDate.now(), localDateTime.toLocalDate());
            str = res.getQuantityString(diff > 0 ? R.plurals.widget_day_left
                : R.plurals.widget_day_since, diff, Math.abs(diff));
        }
        }
        mPreviewWidgetBody.setText(Texts.getResizedText(str));
    }

}
