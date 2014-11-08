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
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import it.sephiroth.android.library.widget.AdapterView;
import it.sephiroth.android.library.widget.HListView;
import mmpud.project.daycountwidget.util.Utils;
import timber.log.Timber;

public class DayCountConfigure extends Activity {

    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    private TextView tvDate;
    private EditText edtTitle;
    private Button btnOK;
//    private HorizontalScrollView hsvStyles;
    private FrameLayout[] btnWidget = new FrameLayout[15];
    private FrameLayout sampleWidget;
    private LinearLayout sampleWidgetBody;
    private TextView sampleWidgetHeader;

    private HListView mHlvSelectHeader;
    private SelectStyleAdapter mHeaderAdapter;
    private List<String> mHeaderStyleList;
    private HListView mHlvSelectBody;
    private SelectStyleAdapter mBodyAdapter;
    private List<String> mBodyStyleList;

    private DatePickerDialog datePickerDialog;

//    private int styleNum;

    private String mStyleHeader;
    private String mStyleBody;

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        final Context context = DayCountConfigure.this;

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_date:
                    if (datePickerDialog != null) {
                        datePickerDialog.show();
                    }
                    break;
                case R.id.btn_ok:
                    // Save information: 1. YYYY-MM-DD
                    //		    		 2. widget style
                    //					 3. title
                    // to shared preferences according to the appWidgetId
                    SharedPreferences.Editor prefs = context.getSharedPreferences(Utils.PREFS_NAME, 0).edit();
                    String targetDate = tvDate.getText().toString();
                    Timber.d("Date: " + targetDate);
                    prefs.putString(Utils.KEY_TARGET_DATE + mAppWidgetId, targetDate);
                    prefs.putString(Utils.KEY_TITLE + mAppWidgetId, edtTitle.getText().toString());
                    prefs.putString(Utils.KEY_STYLE_HEADER + mAppWidgetId, mStyleHeader);
                    prefs.putString(Utils.KEY_STYLE_BODY + mAppWidgetId, mStyleBody);
                    prefs.commit();

                    RemoteViews views = DayCountWidget.buildRemoteViews(context, mAppWidgetId);

                    Timber.d("The widget [" + mAppWidgetId + "] is set");

                    // Push widget update to surface
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                    appWidgetManager.updateAppWidget(mAppWidgetId, views);

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
        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if they press the back button.
        setResult(RESULT_CANCELED);

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

        // Instantiate calendars for today and the target day
        Calendar calToday = Calendar.getInstance();
        String strToday = calToday.get(Calendar.YEAR)
            + "-" + (calToday.get(Calendar.MONTH) + 1)
            + "-" + calToday.get(Calendar.DAY_OF_MONTH);

        // Get information: 1. YYYY-MM-DD
        //					2. title
        //					3. widget style
        // from shared preferences according to the appWidgetId
        SharedPreferences prefs = this.getSharedPreferences(Utils.PREFS_NAME, 0);
        String initTargetDate = prefs.getString(Utils.KEY_TARGET_DATE + mAppWidgetId, strToday);
        String initTitle = prefs.getString(Utils.KEY_TITLE + mAppWidgetId, "");
        mStyleHeader = prefs.getString(Utils.KEY_STYLE_HEADER + mAppWidgetId, "header_black");
        mStyleBody = prefs.getString(Utils.KEY_STYLE_BODY + mAppWidgetId, "body_black");

        Timber.d("(initTargetDate, initTitle): " + "(" + initTargetDate + ", " + initTitle + ")");

        // Set up the view layout resource to use.
        setContentView(R.layout.day_count_configure_layout);

        tvDate = (TextView) findViewById(R.id.tv_date);
        tvDate.setOnClickListener(mOnClickListener);
        edtTitle = (EditText) findViewById(R.id.edt_title);
        btnOK = (Button) findViewById(R.id.btn_ok);
        btnOK.setOnClickListener(mOnClickListener);
        sampleWidget = (FrameLayout) findViewById(R.id.sample_widget_bg);

        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        final Drawable wallpaperDrawable = wallpaperManager.getDrawable();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            sampleWidget.setBackground(wallpaperDrawable);
        } else {
            sampleWidget.setBackgroundDrawable(wallpaperDrawable);
        }



        sampleWidgetHeader = (TextView) findViewById(R.id.sample_widget_title);
        int resourceIdStyleHeader = getResources().getIdentifier(mStyleHeader, "drawable", "mmpud.project.daycountwidget");
        Drawable dH = getResources().getDrawable(resourceIdStyleHeader);
        sampleWidgetHeader.setBackground(dH);
        sampleWidgetBody = (LinearLayout) findViewById(R.id.sample_widget);
        int resourceIdStyleBody = getResources().getIdentifier(mStyleBody, "drawable", "mmpud.project.daycountwidget");
        Drawable dB = getResources().getDrawable(resourceIdStyleBody);
        sampleWidgetBody.setBackground(dB);

        mHlvSelectHeader = (HListView) findViewById(R.id.hlv_style_select_header);
        mHeaderStyleList = new ArrayList<String>();
        mHeaderStyleList = Arrays.asList(getResources().getStringArray(R.array.header_style_list));
        mHeaderAdapter = new SelectStyleAdapter(this, R.layout.list_item_style, mHeaderStyleList);
        mHlvSelectHeader.setAdapter(mHeaderAdapter);
        mHlvSelectHeader.setSelection(mHeaderStyleList.indexOf(mStyleHeader));
        mHlvSelectHeader.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mStyleHeader = mHeaderStyleList.get(i);
                int resourceIdStyleHeader = getResources().getIdentifier(mStyleHeader, "drawable", "mmpud.project.daycountwidget");
                Drawable d = getResources().getDrawable(resourceIdStyleHeader);
                sampleWidgetHeader.setBackground(d);
            }
        });

        mHlvSelectBody = (HListView) findViewById(R.id.hlv_style_select_body);
        mBodyStyleList = new ArrayList<String>();
        mBodyStyleList = Arrays.asList(getResources().getStringArray(R.array.body_style_list));
        mBodyAdapter = new SelectStyleAdapter(this, R.layout.list_item_style, mBodyStyleList);
        mHlvSelectBody.setAdapter(mBodyAdapter);
        mHlvSelectBody.setSelection(mBodyStyleList.indexOf(mStyleBody));
        mHlvSelectBody.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mStyleBody = mBodyStyleList.get(i);
                int resourceIdStyleHeader = getResources().getIdentifier(mStyleBody, "drawable", "mmpud.project.daycountwidget");
                Drawable d = getResources().getDrawable(resourceIdStyleHeader);
                sampleWidgetBody.setBackground(d);
            }
        });

        // Set current date into datePicker
        // Set the date picker dialog
        tvDate.setText(initTargetDate);
        String[] ymd = initTargetDate.split("-");
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                tvDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
            }
        }, Integer.parseInt(ymd[0]), Integer.parseInt(ymd[1]) - 1, Integer.parseInt(ymd[2]));

        // Set title
        if (!initTitle.isEmpty()) {
            edtTitle.setText(initTitle);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

}
