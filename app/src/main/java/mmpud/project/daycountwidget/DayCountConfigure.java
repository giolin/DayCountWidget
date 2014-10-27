package mmpud.project.daycountwidget;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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

import mmpud.project.daycountwidget.util.Utils;
import timber.log.Timber;

public class DayCountConfigure extends Activity {

    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    private TextView tvDate;
    private EditText edtTitle;
    private Button btnOK;
    private HorizontalScrollView hsvStyles;
    private FrameLayout[] btnWidget = new FrameLayout[15];

    private DatePickerDialog datePickerDialog;

    private int styleNum;

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
                    if(datePickerDialog != null) {
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
                    prefs.putString("targetDate" + mAppWidgetId, targetDate);
                    prefs.putString("title" + mAppWidgetId, edtTitle.getText().toString());
                    prefs.putInt("styleNum" + mAppWidgetId, styleNum);
                    prefs.commit();


                    // Get layout resource id with styleNum
//                    String layoutName = "widget_layout" + styleNum;
//                    int resourceIDStyle = context.getResources().getIdentifier(layoutName, "layout", "mmpud.project.daycountwidget");
//
//

                    // Start to build up the remote views
//                    RemoteViews views = new RemoteViews(context.getPackageName(), resourceIDStyle);
//
//                    views.setTextViewText(R.id.widget_title, edtTitle.getText().toString());
//
//                    Calendar calToday = Calendar.getInstance();
//                    Calendar calTarget = Calendar.getInstance();
//                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
//                    try {
//                        calTarget.setTime(sdf.parse(targetDate));
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//
//                    long diffDays = Utils.daysBetween(calToday, calTarget);
//
//                    Timber.d("Diff days: " + diffDays);
//
//                    // Adjust the digits' textSize according to the number of digits
//                    float textSize = Utils.textSizeGenerator(diffDays);
//                    views.setFloat(R.id.widget_diffdays, "setTextSize", textSize);
//
//                    // Put in day difference info
//                    if (diffDays > 0) {
//                        views.setTextViewText(R.id.widget_since_left, getResources().getString(R.string.days_left));
//                        views.setTextViewText(R.id.widget_diffdays, Long.toString(diffDays));
//                    } else {
//                        views.setTextViewText(R.id.widget_since_left, getResources().getString(R.string.days_since));
//                        views.setTextViewText(R.id.widget_diffdays, Long.toString(-diffDays));
//                    }


//                    // Click on the widget for editing
//                    Intent intent = new Intent(context, DayCountDetailDialog.class);
//                    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
//                    intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
//
//                    // No request code and no flags for this example
//                    PendingIntent pending = PendingIntent.getActivity(context, 0, intent, 0);
//                    views.setOnClickPendingIntent(R.id.widget, pending);

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
        String initTargetDate = prefs.getString("targetDate" + mAppWidgetId, strToday);
        String initTitle = prefs.getString("title" + mAppWidgetId, "");
        styleNum = prefs.getInt("styleNum" + mAppWidgetId, 1);

        Timber.d("(initTargetDate, initTitle, styleNum): " + "(" + initTargetDate + ", " + initTitle + ", " + styleNum + ")");

        // Set current date into datePicker
        tvDate.setText(initTargetDate);
        String[] ymd = initTargetDate.split("-");
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                tvDate.setText(year + "-" + monthOfYear + "-" + dayOfMonth);
            }
        }, Integer.parseInt(ymd[0]), Integer.parseInt(ymd[1]), Integer.parseInt(ymd[2]));

        // Set title
        if (!initTitle.isEmpty()) {
            edtTitle.setText(initTitle);
        }

        // Show the selected layout
        btnWidget[styleNum - 1].setBackgroundColor(Color.parseColor("#FF6600"));

        // Scroll the HorizontalScrollView to the selected position
        float density = getApplicationContext().getResources().getDisplayMetrics().density;
        final float unitWidth = Math.round((float) 80 * density);
        hsvStyles.postDelayed(new Runnable() {
            @Override
            public void run() {
                hsvStyles.smoothScrollBy((int) unitWidth * (styleNum - 1), 0);
            }
        }, 100);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

}
