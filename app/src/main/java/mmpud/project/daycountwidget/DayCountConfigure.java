package mmpud.project.daycountwidget;

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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import mmpud.project.daycountwidget.misc.ClickableRecyclerAdapter;
import mmpud.project.daycountwidget.misc.RecyclerViewOnItemClickListener;
import mmpud.project.daycountwidget.util.Utils;
import timber.log.Timber;

public class DayCountConfigure extends Activity {

    @Bind(R.id.tv_date) TextView mTvDate;
    @Bind(R.id.edt_title) EditText mEdtTitle;
    @Bind(R.id.btn_ok) Button mBtnOK;
    @Bind(R.id.sample_widget_bg) FrameLayout mFlSampleWidget;
    @Bind(R.id.sample_widget) LinearLayout mLlSampleWidgetBody;
    @Bind(R.id.sample_widget_title) TextView mTvSampleWidgetHeader;
    @Bind(R.id.sample_widget_diffdays) TextView mTvSampleWidgetDiffDays;
    @Bind(R.id.sample_widget_since_left) TextView mTvSampleWidgetSinceLeft;
    @Bind(R.id.hlv_style_select_header) RecyclerView mHlvSelectHeader;
    @Bind(R.id.hlv_style_select_body) RecyclerView mHlvSelectBody;

    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private String mStyleHeader;
    private String mStyleBody;
    private MyAdapter mHeaderAdapter;
    private MyAdapter mBAdapter;
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
                // save information: 1. YYYY-MM-DD
                //		    		 2. title
                //					 3. header and body style
                // to shared preferences according to the appWidgetId
                SharedPreferences.Editor prefs = context.getSharedPreferences(Utils.PREFS_NAME,
                    0).edit();
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

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setResult(RESULT_CANCELED); // set the result to CANCELED.
        // this will cause the widget host to cancel out of the
        // widget placement if they press the back button.
        setContentView(R.layout.day_count_configure_layout);
        ButterKnife.bind(this);
        // get the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        Timber.d("mAppWidgetId: " + mAppWidgetId);
        // if they gave us an intent without the widget id, just bail.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        // instantiate calendars for today
        Calendar calToday = Calendar.getInstance();
        String strToday = calToday.get(Calendar.YEAR)
            + "-" + (calToday.get(Calendar.MONTH) + 1)
            + "-" + calToday.get(Calendar.DAY_OF_MONTH);

        // get information: 1. YYYY-MM-DD
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

        mTvDate.setOnClickListener(mOnClickListener);
        mBtnOK.setOnClickListener(mOnClickListener);

        // set current date into datePicker
        // set the date picker dialog
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

        // set title and sample widget title
        if (!initTitle.isEmpty()) {
            mEdtTitle.setText(initTitle);
            mTvSampleWidgetHeader.setText(initTitle);
        }

        mEdtTitle.addTextChangedListener(new TextWatcher() {
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
                mTvSampleWidgetHeader.setText(mEdtTitle.getText().toString());
            }
        });

        // set the home screen wallpaper to the sample widget
        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        final Drawable wallpaperDrawable = wallpaperManager.getDrawable();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mFlSampleWidget.setBackground(wallpaperDrawable);
        } else {
            mFlSampleWidget.setBackgroundDrawable(wallpaperDrawable);
        }

        // initialize the header and body style of the sample widget
        int resourceIdStyleHeader = getResources().getIdentifier(mStyleHeader,
            "drawable", "mmpud.project.daycountwidget");
        Drawable dH = getResources().getDrawable(resourceIdStyleHeader);
        mTvSampleWidgetHeader.setBackground(dH);
        int resourceIdStyleBody = getResources().getIdentifier(mStyleBody,
            "drawable", "mmpud.project.daycountwidget");
        Drawable dB = getResources().getDrawable(resourceIdStyleBody);
        mLlSampleWidgetBody.setBackground(dB);

        // set up the horizontal list for header style selection
        Timber.d("set up the horizontal scroll recycler view");
        mHeaderAdapter = new MyAdapter(
            Arrays.asList(getResources().getStringArray(R.array.header_style_list)));
        mHeaderAdapter.setOnItemClickListener(new RecyclerViewOnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.Adapter adapter, View view, int position) {
                mStyleHeader = mHeaderAdapter.getItem(position);
                int resourceIdStyleHeader = getResources().getIdentifier(mStyleHeader,
                    "drawable", "mmpud.project.daycountwidget");
                mTvSampleWidgetHeader.setBackgroundResource(resourceIdStyleHeader);
            }
        });
        mHlvSelectHeader.setHasFixedSize(true);
        mHlvSelectHeader.setItemAnimator(null);
        mHlvSelectHeader.setLayoutManager(new LinearLayoutManager(this,
            LinearLayoutManager.HORIZONTAL, false));
        mHlvSelectHeader.setAdapter(mHeaderAdapter);
        // set up the horizontal list for body style selection
        mBAdapter = new MyAdapter(
            Arrays.asList(getResources().getStringArray(R.array.body_style_list))
        );
        mBAdapter.setOnItemClickListener(new RecyclerViewOnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.Adapter adapter, View view, int position) {
                mStyleBody = mBAdapter.getItem(position);
                int resourceIdStyleHeader = getResources().getIdentifier(mStyleBody,
                    "drawable", "mmpud.project.daycountwidget");
                mLlSampleWidgetBody.setBackgroundResource(resourceIdStyleHeader);
            }
        });
        mHlvSelectBody.setHasFixedSize(true);
        mHlvSelectBody.setItemAnimator(null);
        mHlvSelectBody.setLayoutManager(new LinearLayoutManager(this,
            LinearLayoutManager.HORIZONTAL, false));
        mHlvSelectBody.setAdapter(mBAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    private void setSampleWidgetDayDiff(String strTargetDate) {
        // get the day difference
        Calendar calToday = Calendar.getInstance();
        Calendar calTarget = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            calTarget.setTime(sdf.parse(strTargetDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int diffDays = Utils.daysBetween(calToday, calTarget);
        // adjust the digits' textSize according to the number of digits
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

    public static class MyAdapter extends ClickableRecyclerAdapter<MyViewHolder> {

        private final List<String> mItems;

        private LayoutInflater mInflater;

        public MyAdapter(List<String> mItems) {
            this.mItems = mItems;
        }

        public String getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public MyViewHolder onCreateClickableViewHolder(ViewGroup parent, int viewType) {
            if (mInflater == null) {
                mInflater = LayoutInflater.from(parent.getContext());
            }
            return new MyViewHolder(mInflater.inflate(R.layout.style_list_item, parent, false));
        }

        @Override public void onBindViewHolder(MyViewHolder holder, int position) {
            Context context = holder.itemView.getContext();
            String strStyle = mItems.get(position);
            int resourceIDStyle = context.getResources()
                .getIdentifier(strStyle + "_config", "drawable", "mmpud.project.daycountwidget");
            Timber.d("on bind resource name %s resource id %d", strStyle, resourceIDStyle);
            Picasso.with(context)
                .load(resourceIDStyle)
                .into((ImageView) holder.itemView);
        }

        @Override public int getItemCount() {
            return mItems.size();
        }

    }

    public static class MyViewHolder extends ClickableRecyclerAdapter.ClickableViewHolder {

        public MyViewHolder(View view) {
            super(view);
        }

    }

}
