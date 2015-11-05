package mmpud.project.daycountwidget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.common.collect.Lists;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import mmpud.project.daycountwidget.data.db.Contract;
import mmpud.project.daycountwidget.data.db.DayCountDbHelper;
import mmpud.project.daycountwidget.misc.ClickableRecyclerAdapter;
import mmpud.project.daycountwidget.util.Dates;

import static mmpud.project.daycountwidget.data.db.Contract.COUNT_BY_DAY;
import static mmpud.project.daycountwidget.data.db.Contract.CountBy;
import static mmpud.project.daycountwidget.data.db.Contract.Widget.BODY_STYLE;
import static mmpud.project.daycountwidget.data.db.Contract.Widget.COUNT_BY;
import static mmpud.project.daycountwidget.data.db.Contract.Widget.EVENT_TITLE;
import static mmpud.project.daycountwidget.data.db.Contract.Widget.TARGET_DATE;

public class DayCountMainActivity extends AppCompatActivity
    implements Toolbar.OnMenuItemClickListener {

    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(android.R.id.list) RecyclerView mList;
    @Bind(android.R.id.text1) TextView mNoWidgetMsg;

    private DayCounterAdapter mAdapter;
    private DayCountDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_count_main);
        ButterKnife.bind(this);
        mAdapter = new DayCounterAdapter(this);
        mList.setHasFixedSize(true);
        mList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mList.setAdapter(mAdapter);
        mToolbar.inflateMenu(R.menu.day_count_menu);
        mToolbar.setOnMenuItemClickListener(this);
        // query the data, ok we need to use sqlite to query
        updateAdapter();
        if (mAdapter.getItemCount() == 0) {
            mNoWidgetMsg.setVisibility(View.VISIBLE);
        } else {
            mNoWidgetMsg.setVisibility(View.GONE);
        }
    }

    private void updateAdapter() {
        mAdapter.clear();
        // query from database
        if (mDbHelper == null) {
            mDbHelper = new DayCountDbHelper(this);
        }
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // get all available day count widget ids
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        ComponentName component = new ComponentName(this, DayCountWidgetProvider.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(component);
        for (int appWidgetId : appWidgetIds) {
            Cursor cursor = db.query(Contract.Widget.TABLE_NAME, null,
                Contract.Widget.WIDGET_ID + "=?",
                new String[] {String.valueOf(appWidgetId)}, null, null, null);
            long targetDateMillis;
            String title;
            String bodyStyle;
            @CountBy int countBy;
            if (cursor.moveToFirst()) {
                targetDateMillis = cursor.getLong(cursor.getColumnIndexOrThrow(TARGET_DATE));
                title = cursor.getString(cursor.getColumnIndexOrThrow(EVENT_TITLE));
                bodyStyle = cursor.getString(cursor.getColumnIndexOrThrow(BODY_STYLE));
                //noinspection ResourceType
                countBy = cursor.getInt(cursor.getColumnIndexOrThrow(COUNT_BY));
            } else {
                targetDateMillis = DateTime.now().getMillis();
                title = "";
                bodyStyle = String.valueOf(ContextCompat.getColor(this, R.color.body_black));
                countBy = COUNT_BY_DAY;
            }
            mAdapter.add(new Counter(targetDateMillis, title, bodyStyle, countBy));
            cursor.close();
        }
        db.close();
    }

    @Override public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.language_settings) {
            Intent intent = new Intent(this, LanguageActivity.class);
            startActivityForResult(intent, LanguageActivity.LANGUAGE_SELECT_REQUEST_CODE);
            return true;
        }
        return false;
    }

    public static class DayCounterAdapter extends ClickableRecyclerAdapter<DayCounterViewHolder> {

        private final DateTimeFormatter mFormatter;
        private final List<Counter> mItems;
        private final Context mContext;
        private LayoutInflater mInflater;

        public DayCounterAdapter(Context context) {
            this.mContext = context;
            this.mItems = Lists.newArrayList();
            this.mFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        }

        public void add(Counter item) {
            synchronized (mItems) {
                mItems.add(item);
            }
            notifyItemInserted(getItemCount() - 1);
        }

        public void clear() {
            synchronized (mItems) {
                mItems.clear();
            }
            notifyDataSetChanged();
        }

        public Counter getItem(int position) {
            synchronized (mItems) {
                return mItems.get(position);
            }
        }

        @Override public int getItemCount() {
            synchronized (mItems) {
                return mItems.size();
            }
        }

        @Override
        public DayCounterViewHolder onCreateClickableViewHolder(ViewGroup parent, int viewType) {
            if (mInflater == null) {
                mInflater = LayoutInflater.from(mContext);
            }
            return new DayCounterViewHolder(mInflater.inflate(R.layout.list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(DayCounterViewHolder holder, int position) {
            Counter counter = getItem(position);
            holder.title.setText(counter.getTitle());
            holder.targetDay.setText(mFormatter.print(counter.getTargetDate()));
            holder.itemView.setBackgroundColor(Integer.parseInt(counter.getBodyStyle()));
            DateTime targetDate = new DateTime(counter.getTargetDate()).withTimeAtStartOfDay();
            holder.dayDiff.setText(Dates.getDiffDaysString(mContext, counter.getCountBy(),
                targetDate));
        }

    }

    static class DayCounterViewHolder extends ClickableRecyclerAdapter.ClickableViewHolder {

        @Bind(R.id.list_item_tv_title) TextView title;
        @Bind(R.id.list_item_tv_target_date) TextView targetDay;
        @Bind(R.id.list_item_tv_day_diff) TextView dayDiff;

        public DayCounterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }

    static class Counter {

        private long targetDate;
        private String title;
        private String bodyStyle;
        @CountBy private int countBy;

        public Counter(long targetDate, String title, String bodyStyle, @CountBy int countBy) {
            this.targetDate = targetDate;
            this.title = title;
            this.bodyStyle = bodyStyle;
            this.countBy = countBy;
        }

        public long getTargetDate() {
            return targetDate;
        }

        public String getTitle() {
            return title;
        }

        public String getBodyStyle() {
            return bodyStyle;
        }

        @CountBy public int getCountBy() {
            return countBy;
        }

    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LanguageActivity.LANGUAGE_SELECT_REQUEST_CODE
            && resultCode == RESULT_OK) {
            // refresh the activity itself
            Intent refresh = new Intent(this, DayCountMainActivity.class);
            startActivity(refresh);
            finish();
        }
    }

}
