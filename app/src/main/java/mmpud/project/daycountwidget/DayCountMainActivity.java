package mmpud.project.daycountwidget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.google.common.collect.Lists;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneOffset;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import mmpud.project.daycountwidget.data.DayCountWidget;
import mmpud.project.daycountwidget.data.db.Contract;
import mmpud.project.daycountwidget.data.db.DayCountDbHelper;
import mmpud.project.daycountwidget.misc.ClickableRecyclerAdapter;
import mmpud.project.daycountwidget.util.Dates;
import mmpud.project.daycountwidget.util.Times;

import static mmpud.project.daycountwidget.data.db.Contract.COUNT_BY_DAY;
import static mmpud.project.daycountwidget.data.db.Contract.Widget.BODY_STYLE;
import static mmpud.project.daycountwidget.data.db.Contract.Widget.COUNT_BY;
import static mmpud.project.daycountwidget.data.db.Contract.Widget.EVENT_TITLE;
import static mmpud.project.daycountwidget.data.db.Contract.Widget.TARGET_DATE;

public class DayCountMainActivity extends AppCompatActivity {

    @BindView(android.R.id.list)
    RecyclerView mList;
    @BindView(android.R.id.text1)
    TextView mNoWidgetMsg;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private DayCounterAdapter mAdapter;
    private DayCountDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_count_main);
        ButterKnife.bind(this);
        mToolbar.inflateMenu(R.menu.day_count_menu);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.licenses) {
                    showLicensesAlertDialog();
                    return true;
                }
                return false;
            }
        });
        mAdapter = new DayCounterAdapter(this);
        mList.setHasFixedSize(true);
        mList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mList.setAdapter(mAdapter);
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
                    new String[]{String.valueOf(appWidgetId)}, null, null, null);
            long targetDateMillis;
            String title;
            String bodyStyle;
            int countBy;
            if (cursor.moveToFirst()) {
                targetDateMillis = cursor.getLong(cursor.getColumnIndexOrThrow(TARGET_DATE));
                title = cursor.getString(cursor.getColumnIndexOrThrow(EVENT_TITLE));
                bodyStyle = cursor.getString(cursor.getColumnIndexOrThrow(BODY_STYLE));
                countBy = cursor.getInt(cursor.getColumnIndexOrThrow(COUNT_BY));
            } else {
                targetDateMillis = LocalDate.now().atStartOfDay().atZone(ZoneOffset.UTC).toInstant()
                        .toEpochMilli();
                title = "";
                bodyStyle = String.valueOf(ContextCompat.getColor(this, R.color.body_black));
                countBy = COUNT_BY_DAY;
            }
            mAdapter.add(new DayCountWidget(appWidgetId, title, null, targetDateMillis, countBy,
                    null, bodyStyle));
            cursor.close();
        }
        db.close();
    }

    private void showLicensesAlertDialog() {
        WebView view = (WebView) LayoutInflater.from(this).inflate(R.layout.dialog_licenses, null);
        view.loadUrl("file:///android_asset/open_source_licenses.html");
        new AlertDialog.Builder(this)
                .setTitle("Open Source Licenses")
                .setView(view)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    public static class DayCounterAdapter extends ClickableRecyclerAdapter<DayCounterViewHolder> {

        private final List<DayCountWidget> mItems;
        private final Context mContext;
        private LayoutInflater mInflater;

        public DayCounterAdapter(Context context) {
            this.mContext = context;
            this.mItems = Lists.newArrayList();
        }

        public void add(DayCountWidget item) {
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

        public DayCountWidget getItem(int position) {
            synchronized (mItems) {
                return mItems.get(position);
            }
        }

        @Override
        public int getItemCount() {
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
            DayCountWidget counter = getItem(position);
            holder.title.setText(counter.title);
            LocalDateTime targetDay = Times.getLocalDateTime(counter.targetDay);
            holder.targetDay.setText(targetDay.format(Times.getDateFormatter()));
            holder.itemView.setBackgroundColor(Integer.parseInt(counter.bodyStyle));
            holder.dayDiff.setText(Dates.getDiffDaysString(mContext, counter.countBy, targetDay));
        }

    }

    static class DayCounterViewHolder extends ClickableRecyclerAdapter.ClickableViewHolder {

        @BindView(R.id.list_item_tv_title)
        TextView title;
        @BindView(R.id.list_item_tv_target_date)
        TextView targetDay;
        @BindView(R.id.list_item_tv_day_diff)
        TextView dayDiff;

        public DayCounterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }

}
