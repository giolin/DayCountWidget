package mmpud.project.daycountwidget;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.WallpaperManager;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import it.sephiroth.android.library.widget.AdapterView;
import it.sephiroth.android.library.widget.HListView;
import mmpud.project.daycountwidget.util.Utils;
import timber.log.Timber;

public class DayCountConfigure extends Activity {

    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private String mStyleHeader;
    private String mStyleBody;

    private TextView mTvDate;
    private EditText mEdtTitle;
    private Button mBtnOK;

    // Sample preview widget
    private FrameLayout mFlSampleWidget;
    private LinearLayout mLlSampleWidgetBody;
    private TextView mTvSampleWidgetHeader;
    private TextView mTvSampleWidgetDiffDays;
    private TextView mTvSampleWidgetSinceLeft;

    // Style selection list (header)
    private HListView mHlvSelectHeader;
    private SelectStyleAdapter mHeaderAdapter;
    private List<String> mHeaderStyleList;

    // Style selection list (body)
    private HListView mHlvSelectBody;
    private SelectStyleAdapter mBodyAdapter;
    private List<String> mBodyStyleList;

    private DatePickerDialog mDatePickerDialog;

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        final Context context = DayCountConfigure.this;

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_date:
                    if (mDatePickerDialog != null) {
                        mDatePickerDialog.show();
                    }
                    break;
                case R.id.btn_ok:
                    // Save information: 1. YYYY-MM-DD
                    //		    		 2. title
                    //					 3. header and body style
                    // to shared preferences according to the appWidgetId
                    SharedPreferences.Editor prefs = context.getSharedPreferences(Utils.PREFS_NAME, 0).edit();
                    String targetDate = mTvDate.getText().toString();
                    prefs.putString(Utils.KEY_TARGET_DATE + mAppWidgetId, targetDate);
                    prefs.putString(Utils.KEY_TITLE + mAppWidgetId, mEdtTitle.getText().toString());
                    prefs.putString(Utils.KEY_STYLE_HEADER + mAppWidgetId, mStyleHeader);
                    prefs.putString(Utils.KEY_STYLE_BODY + mAppWidgetId, mStyleBody);
                    prefs.commit();

                    RemoteViews views = DayCountWidget.buildRemoteViews(context, mAppWidgetId);
                    // Push widget update to surface
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                    appWidgetManager.updateAppWidget(mAppWidgetId, views);
                    Timber.d("The widget [" + mAppWidgetId + "] is updated.");
                    // Make sure we pass back the original appWidgetId
                    Intent resultValue = new Intent();
                    resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                    setResult(RESULT_OK, resultValue);
                    finish();
                    break;
            }
        }
    };

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setResult(RESULT_CANCELED); // Set the result to CANCELED.
                                    // This will cause the widget host to cancel out of the
                                    // widget placement if they press the back button.
        setContentView(R.layout.day_count_configure_layout);
        // Get the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    // INVALID_APPWIDGET_ID is 0
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        Timber.d("mAppWidgetId: " + mAppWidgetId);
        // If they gave us an intent without the widget id, just bail.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        // Instantiate calendars for today
        Calendar calToday = Calendar.getInstance();
        String strToday = calToday.get(Calendar.YEAR)
            + "-" + (calToday.get(Calendar.MONTH) + 1)
            + "-" + calToday.get(Calendar.DAY_OF_MONTH);

        // Get information: 1. YYYY-MM-DD
        //					2. title
        //					3. header and body style
        // from shared preferences according to the appWidgetId
        SharedPreferences prefs = this.getSharedPreferences(Utils.PREFS_NAME, 0);
        String initTargetDate = prefs.getString(Utils.KEY_TARGET_DATE + mAppWidgetId, strToday);
        String initTitle = prefs.getString(Utils.KEY_TITLE + mAppWidgetId, "");
        mStyleHeader = prefs.getString(Utils.KEY_STYLE_HEADER + mAppWidgetId, "header_black");
        mStyleBody = prefs.getString(Utils.KEY_STYLE_BODY + mAppWidgetId, "body_black");

        Timber.d("(initTargetDate, initTitle, mStyleHeader, mStyleBody): "
                + "(" + initTargetDate
                + ", " + initTitle
                + ", " + mStyleHeader
                + ", " + mStyleBody + ")");

        mTvDate = (TextView) findViewById(R.id.tv_date);
        mTvDate.setOnClickListener(mOnClickListener);
        mEdtTitle = (EditText) findViewById(R.id.edt_title);
        mBtnOK = (Button) findViewById(R.id.btn_ok);
        mBtnOK.setOnClickListener(mOnClickListener);
        mFlSampleWidget = (FrameLayout) findViewById(R.id.sample_widget_bg);
        mTvSampleWidgetHeader = (TextView) findViewById(R.id.sample_widget_title);
        mTvSampleWidgetDiffDays = (TextView) findViewById(R.id.sample_widget_diffdays);
        mTvSampleWidgetSinceLeft = (TextView) findViewById(R.id.sample_widget_since_left);
        mLlSampleWidgetBody = (LinearLayout) findViewById(R.id.sample_widget);

        // Set current date into datePicker
        // Set the date picker dialog
        mTvDate.setText(initTargetDate);
        String[] ymd = initTargetDate.split("-");
        mDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String strTargetDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                mTvDate.setText(strTargetDate);
                setSampleWidgetDayDiff(strTargetDate);
            }
        }, Integer.parseInt(ymd[0]), Integer.parseInt(ymd[1]) - 1, Integer.parseInt(ymd[2]));
        setSampleWidgetDayDiff(initTargetDate);

        // Set title and sample widget title
        if (!initTitle.isEmpty()) {
            mEdtTitle.setText(initTitle);
            mTvSampleWidgetHeader.setText(initTitle);
        }

        mEdtTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing intended
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do nothing intended
            }

            @Override
            public void afterTextChanged(Editable s) {
                mTvSampleWidgetHeader.setText(mEdtTitle.getText().toString());
            }
        });

        // Set the home screen wallpaper to the sample widget
        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        final Drawable wallpaperDrawable = wallpaperManager.getDrawable();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mFlSampleWidget.setBackground(wallpaperDrawable);
        } else {
            mFlSampleWidget.setBackgroundDrawable(wallpaperDrawable);
        }

        // Initialize the header and body style of the sample widget
        int resourceIdStyleHeader = getResources().getIdentifier(mStyleHeader,
                "drawable", "mmpud.project.daycountwidget");
        Drawable dH = getResources().getDrawable(resourceIdStyleHeader);
        mTvSampleWidgetHeader.setBackground(dH);
        int resourceIdStyleBody = getResources().getIdentifier(mStyleBody,
                "drawable", "mmpud.project.daycountwidget");
        Drawable dB = getResources().getDrawable(resourceIdStyleBody);
        mLlSampleWidgetBody.setBackground(dB);

        // Set up the horizontal list for header style selection
        mHlvSelectHeader = (HListView) findViewById(R.id.hlv_style_select_header);
        mHeaderStyleList = new ArrayList<String>();
        mHeaderStyleList = Arrays.asList(getResources().getStringArray(R.array.header_style_list));
        mHeaderAdapter = new SelectStyleAdapter(this, R.layout.list_item_style, mHeaderStyleList);
        mHlvSelectHeader.setAdapter(mHeaderAdapter);
        mHlvSelectHeader.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mStyleHeader = mHeaderStyleList.get(i);
                int resourceIdStyleHeader = getResources().getIdentifier(mStyleHeader,
                        "drawable", "mmpud.project.daycountwidget");
                Drawable d = getResources().getDrawable(resourceIdStyleHeader);
                mTvSampleWidgetHeader.setBackground(d);
            }
        });
        // Set up the horizontal list for body style selection
        mHlvSelectBody = (HListView) findViewById(R.id.hlv_style_select_body);
        mBodyStyleList = new ArrayList<String>();
        mBodyStyleList = Arrays.asList(getResources().getStringArray(R.array.body_style_list));
        mBodyAdapter = new SelectStyleAdapter(this, R.layout.list_item_style, mBodyStyleList);
        mHlvSelectBody.setAdapter(mBodyAdapter);
        mHlvSelectBody.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mStyleBody = mBodyStyleList.get(i);
                int resourceIdStyleHeader = getResources().getIdentifier(mStyleBody,
                        "drawable", "mmpud.project.daycountwidget");
                Drawable d = getResources().getDrawable(resourceIdStyleHeader);
                mLlSampleWidgetBody.setBackground(d);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    private void setSampleWidgetDayDiff(String strTargetDate) {
        // Get the day difference
        Calendar calToday = Calendar.getInstance();
        Calendar calTarget = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            calTarget.setTime(sdf.parse(strTargetDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int diffDays = Utils.daysBetween(calToday, calTarget);
        // Adjust the digits' textSize according to the number of digits
        float textSize = Utils.textSizeGenerator(diffDays);
        mTvSampleWidgetDiffDays.setTextSize(textSize);

        if (diffDays > 0) {
            mTvSampleWidgetDiffDays.setText(Integer.toString(diffDays));
            mTvSampleWidgetSinceLeft.setText(getResources().getText(R.string.days_left));
        } else {
            diffDays = -diffDays;
            mTvSampleWidgetDiffDays.setText(Integer.toString(diffDays));
            mTvSampleWidgetSinceLeft.setText(getResources().getText(R.string.days_since));
        }
    }

}
