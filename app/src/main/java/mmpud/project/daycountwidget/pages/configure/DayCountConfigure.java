package mmpud.project.daycountwidget.pages.configure;

import android.app.DatePickerDialog;
import android.app.WallpaperManager;
import android.appwidget.AppWidgetManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.temporal.ChronoUnit;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import mmpud.project.daycountwidget.DayCountWidgetProvider;
import mmpud.project.daycountwidget.R;
import mmpud.project.daycountwidget.data.db.Contract;
import mmpud.project.daycountwidget.data.db.DayCountDbHelper;
import mmpud.project.daycountwidget.util.NumberPicker;
import mmpud.project.daycountwidget.util.OnProgressChangeListener;
import mmpud.project.daycountwidget.util.Texts;
import mmpud.project.daycountwidget.util.Times;
import mmpud.project.daycountwidget.util.WidgetPadding;

import static mmpud.project.daycountwidget.data.db.Contract.Widget.HORIZONTAL_PADDING;
import static mmpud.project.daycountwidget.data.db.Contract.Widget.VERTICAL_PADDING;
import static mmpud.project.daycountwidget.pages.configure.ColorSelectDialog.OnColorSelectedListener;

import static android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID;
import static android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID;
import static butterknife.OnTextChanged.Callback.AFTER_TEXT_CHANGED;
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
        implements Toolbar.OnMenuItemClickListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.edt_title)
    EditText mTitleText;
    @BindView(R.id.tv_date)
    TextView mDateText;
    @BindView(R.id.radio_group)
    RadioGroup mRadioGroup;
    @BindView(R.id.preview_window_bg)
    ImageView mPreviewWindowBg;
    @BindView(R.id.widget_header_bg)
    ImageView mPreviewWidgetHeaderBg;
    @BindView(R.id.widget_header)
    TextView mPreviewWidgetHeader;
    @BindView(R.id.widget_body_bg)
    ImageView mPreviewWidgetBodyBg;
    @BindView(R.id.widget_body)
    TextView mPreviewWidgetBody;
    @BindView(R.id.text_trans)
    TextView mTextTrans;
    @BindView(R.id.seek_bar_trans)
    SeekBar mSeekBarTrans;
    @BindView(R.id.header_select)
    RecyclerView headerSelect;
    @BindView(R.id.body_select)
    RecyclerView bodySelect;
    @BindView(R.id.number_picker_horizontal_padding)
    NumberPicker numberPickerHorizontalPadding;
    @BindView(R.id.number_picker_vertical_padding)
    NumberPicker numberPickerVerticalPadding;

    @BindColor(R.color.header_red)
    int headerRed;
    @BindColor(R.color.header_orange)
    int headerOrange;
    @BindColor(R.color.header_yellow)
    int headerYellow;
    @BindColor(R.color.header_green)
    int headerGreen;
    @BindColor(R.color.header_blue)
    int headerBlue;
    @BindColor(R.color.header_navy)
    int headerNavy;
    @BindColor(R.color.header_indigo)
    int headerIndigo;
    @BindColor(R.color.header_purple)
    int headerPurple;
    @BindColor(R.color.header_black)
    int headerBlack;

    @BindColor(R.color.body_red)
    int bodyRed;
    @BindColor(R.color.body_orange)
    int bodyOrange;
    @BindColor(R.color.body_yellow)
    int bodyYellow;
    @BindColor(R.color.body_green)
    int bodyGreen;
    @BindColor(R.color.body_blue)
    int bodyBlue;
    @BindColor(R.color.body_navy)
    int bodyNavy;
    @BindColor(R.color.body_purple)
    int bodyPurple;
    @BindColor(R.color.body_pink)
    int bodyPink;
    @BindColor(R.color.body_black)
    int bodyBlack;

    private DayCountDbHelper mDbHelper;
    private DatePickerDialog mDatePickerDialog;
    private int mAppWidgetId;
    private long mTimestamp;
    @Contract.CountBy
    private int mCountBy;
    private String mHeaderStyle;
    private String mBodyStyle;
    private SelectAdapter headerAdapter;
    private SelectAdapter bodyAdapter;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        // get widget id from the intent.
        mAppWidgetId = getAppWidgetId();
        if (mAppWidgetId == INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        // set result CANCELED. this will cause the widget host to cancel out of the widget
        // placement if they press the back button.
        setResult(RESULT_CANCELED, resultValue);

        setContentView(R.layout.day_count_configure_layout);
        ButterKnife.bind(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // toolbar
        mToolbar.setNavigationIcon(R.drawable.ic_cross);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mToolbar.inflateMenu(R.menu.configure_menu);
        mToolbar.setOnMenuItemClickListener(this);

        // query from database
        // TODO need to call the manager to get the object
        if (mDbHelper == null) {
            mDbHelper = new DayCountDbHelper(this);
        }
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, WIDGET_ID + "=?",
                new String[]{String.valueOf(mAppWidgetId)}, null, null, null);
        String initTitle;
        float initAlpha;
        int horizontalPadding;
        int verticalPadding;
        if (cursor.moveToFirst()) {
            initTitle = cursor.getString(cursor.getColumnIndexOrThrow(EVENT_TITLE));
            mTimestamp = cursor.getLong(cursor.getColumnIndexOrThrow(TARGET_DATE));
            mHeaderStyle = cursor.getString(cursor.getColumnIndexOrThrow(HEADER_STYLE));
            mBodyStyle = cursor.getString(cursor.getColumnIndexOrThrow(BODY_STYLE));
            mCountBy = cursor.getInt(cursor.getColumnIndexOrThrow(COUNT_BY));
            initAlpha = cursor.getFloat(cursor.getColumnIndexOrThrow(ALPHA));
            horizontalPadding = cursor.getInt(cursor.getColumnIndexOrThrow(HORIZONTAL_PADDING));
            verticalPadding = cursor.getInt(cursor.getColumnIndexOrThrow(VERTICAL_PADDING));
        } else {
            initTitle = "";
            mTimestamp = Times.getStartOfDayMillis();
            mHeaderStyle = String.valueOf(headerGreen);
            mBodyStyle = String.valueOf(bodyGreen);
            mCountBy = COUNT_BY_DAY;
            initAlpha = 1;
            horizontalPadding = -1;
            verticalPadding = -1;
        }
        cursor.close();
        db.close();

        // set title and sample widget title
        if (!TextUtils.isEmpty(initTitle)) {
            mTitleText.setText(initTitle);
            mPreviewWidgetHeader.setText(initTitle);
        }

        // seek bar
        mSeekBarTrans.setOnSeekBarChangeListener(new OnProgressChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                float alpha = (float) (i / 100.0);
                mTextTrans.setText(String.valueOf(i));
                mPreviewWidgetBodyBg.setAlpha(alpha);
                mPreviewWidgetHeaderBg.setAlpha(alpha);
            }
        });
        int alphaPercent = (int) (initAlpha * 100);
        mTextTrans.setText(String.valueOf(alphaPercent));
        mSeekBarTrans.setProgress(alphaPercent);
        // set time in the text view
        LocalDateTime initDateTime = Times.getLocalDateTime(mTimestamp);
        mDateText.setText(initDateTime.format(Times.getDateFormatter()));
        // set sample widget
        setPreviewContent(initDateTime);
        // set current date into datePicker
        mDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                LocalDateTime targetDay = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
                        .atStartOfDay();
                mTimestamp = targetDay.atZone(ZoneOffset.UTC).toInstant().toEpochMilli();
                mDateText.setText(targetDay.format(Times.getDateFormatter()));
                setPreviewContent(targetDay);
            }
        }, initDateTime.getYear(), initDateTime.getMonthValue() - 1, initDateTime.getDayOfMonth());
        // set radio button
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //noinspection ResourceType
                mCountBy = group.indexOfChild(group.findViewById(checkedId));
                setPreviewContent(Times.getLocalDateTime(mTimestamp));
            }
        });
        ((RadioButton) mRadioGroup.getChildAt(mCountBy)).setChecked(true);
        // set home screen wallpaper to the background sample widget
        mPreviewWindowBg.setImageDrawable(WallpaperManager.getInstance(this).getDrawable());
        // initialize header and body color for sample widget
        mPreviewWidgetHeaderBg.setColorFilter(Integer.valueOf(mHeaderStyle));
        mPreviewWidgetBodyBg.setColorFilter(Integer.valueOf(mBodyStyle));
        mPreviewWidgetBodyBg.setAlpha(initAlpha);
        mPreviewWidgetHeaderBg.setAlpha(initAlpha);
        // header select
        LinearLayoutManager headerLayoutManager = new LinearLayoutManager(this);
        headerLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        headerAdapter = new SelectAdapter(new int[]{headerRed, headerOrange, headerYellow,
                headerGreen, headerBlue, headerNavy, headerIndigo, headerPurple, headerBlack},
                new SelectAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        if (headerAdapter.isLastItem(position)) {
                            openColorSelectDialog(true);
                        } else {
                            setHeaderColor(headerAdapter.getColor(position));
                        }
                    }
                });
        headerSelect.setAdapter(headerAdapter);
        headerSelect.setLayoutManager(headerLayoutManager);
        // body select
        LinearLayoutManager bodyLayoutManager = new LinearLayoutManager(this);
        bodyLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        bodyAdapter = new SelectAdapter(new int[]{bodyRed, bodyOrange, bodyYellow, bodyGreen,
                bodyBlue, bodyNavy, bodyPurple, bodyPink, bodyBlack},
                new SelectAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        if (bodyAdapter.isLastItem(position)) {
                            openColorSelectDialog(false);
                        } else {
                            setBodyColor(bodyAdapter.getColor(position));
                        }
                    }
                });
        bodySelect.setAdapter(bodyAdapter);
        bodySelect.setLayoutManager(bodyLayoutManager);
        if (horizontalPadding == -1) {
            horizontalPadding = WidgetPadding.getHorizontalPadding(this);
        }
        if (verticalPadding == -1) {
            verticalPadding = WidgetPadding.getVerticalPadding(this);
        }
        numberPickerHorizontalPadding.setNumber(horizontalPadding);
        numberPickerVerticalPadding.setNumber(verticalPadding);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @OnClick(R.id.tv_date)
    void showDatePicker() {
        if (mDatePickerDialog != null) {
            mDatePickerDialog.show();
        }
    }

    @OnTextChanged(value = R.id.edt_title, callback = AFTER_TEXT_CHANGED)
    void onTitleChanged() {
        mPreviewWidgetHeader.setText(mTitleText.getText().toString());
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.btn_ok) {
            Context context = getApplicationContext();
            // save data in database
            // TODO please make the object of DayCountWidget and save by the manager
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
            values.put(HORIZONTAL_PADDING, numberPickerHorizontalPadding.getNumber());
            values.put(VERTICAL_PADDING, numberPickerVerticalPadding.getNumber());
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase
                    .CONFLICT_REPLACE);
            db.close();
            // save the padding in pref
            WidgetPadding.saveHorizontalPadding(this,
                    numberPickerHorizontalPadding.getNumber());
            WidgetPadding.saveVerticalPadding(this,
                    numberPickerVerticalPadding.getNumber());
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

    private void setHeaderColor(int color) {
        mPreviewWidgetHeaderBg.setColorFilter(color);
        mHeaderStyle = String.valueOf(color);
    }

    private void setBodyColor(int color) {
        mPreviewWidgetBodyBg.setColorFilter(color);
        mBodyStyle = String.valueOf(color);
    }

    private void openColorSelectDialog(boolean isForHeader) {
        ColorSelectDialog dialog = ColorSelectDialog.newInstance(isForHeader ?
                Integer.valueOf(mHeaderStyle) : Integer.valueOf(mBodyStyle));
        dialog.setOnColorSelectedListener(isForHeader ?
                new OnColorSelectedListener() {
                    @Override
                    public void OnColorSelected(int color) {
                        setHeaderColor(color);
                    }
                } :
                new OnColorSelectedListener() {
                    @Override
                    public void OnColorSelected(int color) {
                        setBodyColor(color);
                    }
                });
        dialog.show(getSupportFragmentManager(), "dialog");
    }

    private void setPreviewContent(LocalDateTime localDateTime) {
        int diff;
        String str;
        Resources res = getResources();
        LocalDate now = LocalDate.now();
        LocalDate targetDay = localDateTime.toLocalDate();
        switch (mCountBy) {
            case COUNT_BY_DAY: {
                diff = (int) ChronoUnit.DAYS.between(now, targetDay);
                str = res.getQuantityString(diff > 0 ? R.plurals.widget_day_left
                        : R.plurals.widget_day_since, diff, Math.abs(diff));
                break;
            }
            case COUNT_BY_WEEK: {
                diff = (int) ChronoUnit.DAYS.between(now, targetDay) / 7;
                str = res.getQuantityString(diff > 0 ? R.plurals.widget_week_left
                        : R.plurals.widget_week_since, diff, Math.abs(diff));
                break;
            }
            case COUNT_BY_MONTH: {
                diff = (int) ChronoUnit.MONTHS.between(now, targetDay);
                str = res.getQuantityString(diff > 0 ? R.plurals.widget_month_left
                        : R.plurals.widget_month_since, diff, Math.abs(diff));
                break;
            }
            case COUNT_BY_YEAR: {
                diff = (int) ChronoUnit.YEARS.between(now, targetDay);
                str = res.getQuantityString(diff > 0 ? R.plurals.widget_year_left
                        : R.plurals.widget_year_since, diff, Math.abs(diff));
                break;
            }
            default: {
                diff = (int) ChronoUnit.DAYS.between(now, targetDay);
                str = res.getQuantityString(diff > 0 ? R.plurals.widget_day_left
                        : R.plurals.widget_day_since, diff, Math.abs(diff));
            }
        }
        mPreviewWidgetBody.setText(Texts.getResizedText(str));
    }

    private int getAppWidgetId() {
        Bundle extras = getIntent().getExtras();
        return extras == null ? INVALID_APPWIDGET_ID
                : extras.getInt(EXTRA_APPWIDGET_ID, INVALID_APPWIDGET_ID);
    }

}
