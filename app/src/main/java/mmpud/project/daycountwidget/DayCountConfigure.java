package mmpud.project.daycountwidget;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.WallpaperManager;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.util.Calendar;

import it.sephiroth.android.library.widget.HListView;
import mmpud.project.daycountwidget.util.Utils;
import timber.log.Timber;

public class DayCountConfigure extends Activity {

    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    private TextView tvDate;
    private EditText edtTitle;
    private Button btnOK;
    private HorizontalScrollView hsvStyles;
    private FrameLayout[] btnWidget = new FrameLayout[15];
    private FrameLayout sampleWidget;

    private HListView mHlvSelectHeader;
    private HListView mHlvSelectBody;

    private DatePickerDialog datePickerDialog;

//    private int styleNum;

    private String styleHeader;
    private String styleBody;

    View.OnClickListener widgetOnClickListener = new View.OnClickListener() {

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onClick(View v) {
            for (int i = 0; i < btnWidget.length; i++) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    btnWidget[i].setBackgroundDrawable(null);
                } else {
                    btnWidget[i].setBackground(null);
                }
            }
            v.setBackgroundColor(Color.parseColor("#FF6600"));
            styleNum = Integer.parseInt(v.getTag().toString());
        }
    };

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
                    prefs.putString(Utils.KEY_STYLE_HEADER + mAppWidgetId, styleHeader);
                    prefs.putString(Utils.KEY_STYLE_BODY + mAppWidgetId, styleBody);
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

        // Set up the view layout resource to use.
        setContentView(R.layout.day_count_configure_layout);

        tvDate = (TextView) findViewById(R.id.tv_date);
        edtTitle = (EditText) findViewById(R.id.edt_title);
        btnOK = (Button) findViewById(R.id.btn_ok);
        hsvStyles = (HorizontalScrollView) findViewById(R.id.hsv_styles);
        sampleWidget = (FrameLayout) findViewById(R.id.sample_widget);

        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        final Drawable wallpaperDrawable = wallpaperManager.getDrawable();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            sampleWidget.setBackground(wallpaperDrawable);
        } else {
            sampleWidget.setBackgroundDrawable(wallpaperDrawable);
        }

        mHlvSelectHeader = new HListView(this);
        mHlvSelectBody = new HListView(this);

        tvDate.setOnClickListener(mOnClickListener);
        btnOK.setOnClickListener(mOnClickListener);

        for (int i = 0; i < btnWidget.length; i++) {
            // Set header and body color
            String strStyle = "style" + (i + 1);
            int resourceIDStyle = getResources().getIdentifier(strStyle, "id", "mmpud.project.daycountwidget");
            btnWidget[i] = (FrameLayout) findViewById(resourceIDStyle);
            // set tag for the  widgets
            btnWidget[i].setTag(i + 1);
            btnWidget[i].setOnClickListener(widgetOnClickListener);
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
        String initStyleHeader = prefs.getString(Utils.KEY_STYLE_HEADER + mAppWidgetId, "");
        String initStyleBody = prefs.getString(Utils.KEY_STYLE_BODY + mAppWidgetId, "");

        Timber.d("(initTargetDate, initTitle, styleNum): " + "(" + initTargetDate + ", " + initTitle + ", " + styleNum + ")");

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

        // Show the selected layout
//        btnWidget[styleNum - 1].setBackgroundColor(Color.parseColor("#FF6600"));

        // Scroll the HorizontalScrollView to the selected position
//        float density = getApplicationContext().getResources().getDisplayMetrics().density;
//        final float unitWidth = Math.round((float) 80 * density);
//        hsvStyles.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                hsvStyles.smoothScrollBy((int) unitWidth * (styleNum - 1), 0);
//            }
//        }, 100);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

}
