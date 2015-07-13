package mmpud.project.daycountwidget;

import android.app.DatePickerDialog;
import android.app.WallpaperManager;
import android.appwidget.AppWidgetManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.google.common.collect.Lists;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mmpud.project.daycountwidget.data.db.DayCountContract.DayCountWidget;
import mmpud.project.daycountwidget.data.db.DayCountDbHelper;
import mmpud.project.daycountwidget.misc.ClickableRecyclerAdapter;
import mmpud.project.daycountwidget.misc.RecyclerViewOnItemClickListener;
import mmpud.project.daycountwidget.util.Texts;
import timber.log.Timber;

import static android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID;
import static android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID;


public class DayCountConfigure extends AppCompatActivity {

    private final DateTimeFormatter mDateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");

    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.tv_date) TextView mDateText;
    @Bind(R.id.edt_title) EditText mTitleText;
    @Bind(R.id.preview_window_bg) ImageView mPreviewWindowBg;
    @Bind(R.id.widget_body_bg) ImageView mPreviewWidgetBody;
    @Bind(R.id.widget_title) TextView mPreviewWidgetHeader;
    @Bind(R.id.widget_diff_days) TextView mPreviewWidgetDiffDays;
    @Bind(R.id.widget_since_left) TextView mPreviewSinceLeft;
    @Bind(R.id.hlv_style_select_header) RecyclerView mHeaderStyleList;
    @Bind(R.id.hlv_style_select_body) RecyclerView mBodyStyleList;

    @BindDimen(R.dimen.widget_radius) int cornerR;
    @BindColor(R.color.header_black) int initHeaderColor;
    @BindColor(R.color.body_black) int initBodyColor;
    @BindString(R.string.days_since) String daysSince;
    @BindString(R.string.days_left) String daysLeft;

    private DayCountDbHelper mDbHelper;
    private HeaderColorAdapter mHeaderAdapter;
    private BodyColorAdapter mBodyAdapter;
    private DatePickerDialog mDatePickerDialog;
    private int mAppWidgetId;
    private long mTimestamp;
    private String mHeaderStyle;
    private String mBodyStyle;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        // set the result to CANCELED. this will cause the widget host to cancel out of the widget
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

        mToolbar.setNavigationIcon(R.drawable.ic_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                finish();
            }
        });
        mToolbar.inflateMenu(R.menu.configure_menu);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.btn_ok) {
                    Context context = getApplicationContext();
                    // save data in database
                    if (mDbHelper == null) {
                        mDbHelper = new DayCountDbHelper(DayCountConfigure.this);
                    }
                    ContentValues values = new ContentValues();
                    values.put(DayCountWidget.WIDGET_ID, mAppWidgetId);
                    values.put(DayCountWidget.EVENT_TITLE, mTitleText.getText().toString());
                    values.put(DayCountWidget.EVENT_DESCRIPTION, "");
                    values.put(DayCountWidget.TARGET_DATE, mTimestamp);
                    values.put(DayCountWidget.HEADER_STYLE, mHeaderStyle);
                    values.put(DayCountWidget.BODY_STYLE, mBodyStyle);
                    SQLiteDatabase db = mDbHelper.getWritableDatabase();
                    db.insertWithOnConflict(DayCountWidget.TABLE_NAME, null, values, SQLiteDatabase
                        .CONFLICT_REPLACE);
                    db.close();

                    // push widget update to surface
                    RemoteViews views = DayCountWidgetProvider.buildRemoteViews(context,
                        mAppWidgetId);
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                    appWidgetManager.updateAppWidget(mAppWidgetId, views);
                    Timber.d("Menu clicked and The widget [" + mAppWidgetId + "] is updated.");
                    // make sure we pass back the original appWidgetId
                    Intent resultValue = new Intent();
                    resultValue.putExtra(EXTRA_APPWIDGET_ID, mAppWidgetId);
                    setResult(RESULT_OK, resultValue);
                    finish();
                    return true;
                }
                return false;
            }
        });

        // query from database
        if (mDbHelper == null) {
            mDbHelper = new DayCountDbHelper(this);
        }
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.query(DayCountWidget.TABLE_NAME, null, DayCountWidget.WIDGET_ID + "=?",
            new String[] {String.valueOf(mAppWidgetId)}, null, null, null);

        String initTitle;
        if (cursor.moveToFirst()) {
            mTimestamp = cursor.getLong(cursor.getColumnIndexOrThrow(DayCountWidget.TARGET_DATE));
            initTitle = cursor.getString(cursor.getColumnIndexOrThrow(DayCountWidget.EVENT_TITLE));
            mHeaderStyle = cursor.getString(cursor.getColumnIndexOrThrow(DayCountWidget
                .HEADER_STYLE));
            mBodyStyle = cursor.getString(cursor.getColumnIndexOrThrow(DayCountWidget.BODY_STYLE));
        } else {
            mTimestamp = DateTime.now().getMillis();
            initTitle = "";
            mHeaderStyle = String.valueOf(initHeaderColor);
            mBodyStyle = String.valueOf(initBodyColor);
        }
        cursor.close();
        db.close();

        // set current date into datePicker
        // set the date picker dialog
        mDateText.setText(mDateTimeFormatter.print(mTimestamp));

        DateTime dateTime = new DateTime(mTimestamp);
        setSampleWidgetDayDiff(dateTime);
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
                setSampleWidgetDayDiff(dateTime);
            }
        }, dateTime.getYear(), dateTime.getMonthOfYear() - 1, dateTime.getDayOfMonth());

        // set title and sample widget title
        if (!TextUtils.isEmpty(initTitle)) {
            mTitleText.setText(initTitle);
            mPreviewWidgetHeader.setText(initTitle);
        }

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

        // set home screen wallpaper to the background sample widget
        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        final Drawable wallpaperDrawable = wallpaperManager.getDrawable();
        mPreviewWindowBg.setImageDrawable(wallpaperDrawable);

        // initialize header and body color for sample widget
        GradientDrawable headerDrawable = new GradientDrawable();
        headerDrawable.setColor(Integer.parseInt(mHeaderStyle));
        final float[] radii1 = new float[] {cornerR, cornerR, cornerR, cornerR, 0, 0, 0, 0};
        headerDrawable.setCornerRadii(radii1);
        mPreviewWidgetHeader.setBackground(headerDrawable);
        GradientDrawable bodyDrawable = new GradientDrawable();
        bodyDrawable.setColor(Integer.parseInt(mBodyStyle));
        final float[] radii2 = new float[] {0, 0, 0, 0, cornerR, cornerR, cornerR, cornerR};
        bodyDrawable.setCornerRadii(radii2);
        mPreviewWidgetBody.setBackground(bodyDrawable);

        // set up the horizontal list for header style selection
        mHeaderAdapter = new HeaderColorAdapter(this);
        mHeaderAdapter.setOnItemClickListener(new RecyclerViewOnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.Adapter adapter, View view, int position) {
                ((GradientDrawable) mPreviewWidgetHeader.getBackground())
                    .setColor(mHeaderAdapter.getItem(position));
                mHeaderStyle = String.valueOf(mHeaderAdapter.getItem(position));
            }
        });
        mHeaderStyleList.setHasFixedSize(true);
        mHeaderStyleList.setLayoutManager(new LinearLayoutManager(this,
            LinearLayoutManager.HORIZONTAL, false));
        mHeaderStyleList.setAdapter(mHeaderAdapter);

        // set up the horizontal list for body style selection
        mBodyAdapter = new BodyColorAdapter(this);
        mBodyAdapter.setOnItemClickListener(new RecyclerViewOnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.Adapter adapter, View view, int position) {
                ((GradientDrawable) mPreviewWidgetBody.getBackground())
                    .setColor(mBodyAdapter.getItem(position));
                mBodyStyle = String.valueOf(mBodyAdapter.getItem(position));
            }
        });
        mBodyStyleList.setHasFixedSize(true);
        mBodyStyleList.setLayoutManager(new LinearLayoutManager(this,
            LinearLayoutManager.HORIZONTAL, false));
        mBodyStyleList.setAdapter(mBodyAdapter);
    }

    @OnClick(R.id.tv_date) void showDatePicker() {
        if (mDatePickerDialog != null) {
            mDatePickerDialog.show();
        }
    }

    private void setSampleWidgetDayDiff(DateTime targetDate) {
        int diffDays = Days.daysBetween(DateTime.now().withTimeAtStartOfDay(),
            targetDate.withTimeAtStartOfDay()).getDays();
        mPreviewWidgetDiffDays.setText(Integer.toString(Math.abs(diffDays)));
        mPreviewWidgetDiffDays.setTextSize(TypedValue.COMPLEX_UNIT_DIP,
            Texts.getTextSizeDpByDigits(diffDays));
        mPreviewSinceLeft.setText(diffDays > 0 ? daysLeft : daysSince);
    }

    public static class HeaderColorAdapter extends ClickableRecyclerAdapter<ColorViewHolder> {

        private final List<Integer> mItems;

        private LayoutInflater mInflater;

        public HeaderColorAdapter(Context context) {
            this.mItems = Lists.newArrayList(ContextCompat.getColor(context, R.color.header_red),
                ContextCompat.getColor(context, R.color.header_orange),
                ContextCompat.getColor(context, R.color.header_yellow),
                ContextCompat.getColor(context, R.color.header_green),
                ContextCompat.getColor(context, R.color.header_blue),
                ContextCompat.getColor(context, R.color.header_indigo),
                ContextCompat.getColor(context, R.color.header_navy),
                ContextCompat.getColor(context, R.color.header_purple),
                ContextCompat.getColor(context, R.color.header_black));
        }

        public int getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public ColorViewHolder onCreateClickableViewHolder(ViewGroup parent, int viewType) {
            if (mInflater == null) {
                mInflater = LayoutInflater.from(parent.getContext());
            }
            return new ColorViewHolder(mInflater.inflate(R.layout.style_list_item, parent,
                false));
        }

        @Override public void onBindViewHolder(ColorViewHolder holder, int position) {
            holder.itemView.setBackgroundColor(getItem(position));
        }

        @Override public int getItemCount() {
            return mItems.size();
        }

    }

    public static class BodyColorAdapter extends ClickableRecyclerAdapter<ColorViewHolder> {

        private final List<Integer> mItems;

        private LayoutInflater mInflater;

        public BodyColorAdapter(Context context) {
            this.mItems = Lists.newArrayList(ContextCompat.getColor(context, R.color.body_red),
                ContextCompat.getColor(context, R.color.body_orange),
                ContextCompat.getColor(context, R.color.body_yellow),
                ContextCompat.getColor(context, R.color.body_green),
                ContextCompat.getColor(context, R.color.body_blue),
                ContextCompat.getColor(context, R.color.body_navy),
                ContextCompat.getColor(context, R.color.body_purple),
                ContextCompat.getColor(context, R.color.body_pink),
                ContextCompat.getColor(context, R.color.body_black));
        }

        public int getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public ColorViewHolder onCreateClickableViewHolder(ViewGroup parent, int viewType) {
            if (mInflater == null) {
                mInflater = LayoutInflater.from(parent.getContext());
            }
            return new ColorViewHolder(mInflater.inflate(R.layout.style_list_item, parent,
                false));
        }

        @Override public void onBindViewHolder(ColorViewHolder holder, int position) {
            holder.itemView.setBackgroundColor(getItem(position));
        }

        @Override public int getItemCount() {
            return mItems.size();
        }

    }

    public static class ColorViewHolder extends ClickableRecyclerAdapter.ClickableViewHolder {

        public ColorViewHolder(View view) {
            super(view);
        }

    }

}
