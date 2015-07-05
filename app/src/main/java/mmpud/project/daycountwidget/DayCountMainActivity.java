package mmpud.project.daycountwidget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import mmpud.project.daycountwidget.misc.ClickableRecyclerAdapter;
import mmpud.project.daycountwidget.util.Counter;
import mmpud.project.daycountwidget.util.Utils;
import timber.log.Timber;


public class DayCountMainActivity extends AppCompatActivity
    implements Toolbar.OnMenuItemClickListener {

    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(android.R.id.list) RecyclerView mList;
    @Bind(android.R.id.text1) TextView mNoWidgetMsg;

    private DayCounterAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_count_main);
        ButterKnife.bind(this);
        mAdapter = new DayCounterAdapter(this);
        mList.setHasFixedSize(true);
        mList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mList.setAdapter(mAdapter);
        mToolbar.setTitle(R.string.app_name);
        mToolbar.inflateMenu(R.menu.day_count_menu);
        mToolbar.setOnMenuItemClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateAdapter();
        if (mAdapter.getItemCount() == 0) {
            mNoWidgetMsg.setVisibility(View.VISIBLE);
        } else {
            mNoWidgetMsg.setVisibility(View.GONE);
        }
    }

    private void updateAdapter() {
        mAdapter.clear();
        // Get all available day count widget ids
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        ComponentName thisAppWidget = new ComponentName(this, DayCountWidget.class);

        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
        // Add all the current day counter widget info to the list
        for (int appWidgetId : appWidgetIds) {
            Timber.d("appWidgetId: " + appWidgetId);
            // Get information: 1. YYYY-MM-DD
            //					2. title
            //					3. body style
            // from shared preferences according to the appWidgetId
            SharedPreferences prefs = this.getSharedPreferences(Utils.PREFS_NAME, 0);
            mAdapter.add(new Counter(prefs.getString(Utils.KEY_TARGET_DATE + appWidgetId, ""),
                    prefs.getString(Utils.KEY_TITLE + appWidgetId, ""),
                    prefs.getString(Utils.KEY_STYLE_BODY + appWidgetId, "")));
        }
    }

    @Override public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.language_settings) {
            // TODO go to language settings page
            return true;
        }
        return false;
    }

    public static class DayCounterAdapter extends ClickableRecyclerAdapter<DayCounterViewHolder> {

        final private List<Counter> mItems;
        private Context mContext;
        private LayoutInflater mInflater;

        public DayCounterAdapter(Context context) {
            this.mContext = context;
            this.mInflater = LayoutInflater.from(context);
            this.mItems = Lists.newArrayList();
        }

        public void add(Counter item) {
            synchronized (mItems) {
                mItems.add(item);
            }
            notifyItemInserted(getItemCount() - 1);
        }

        public void addAll(List<Counter> items) {
            int oldSize = getItemCount();
            synchronized (mItems) {
                mItems.addAll(items);
            }
            notifyItemRangeInserted(oldSize, items.size());
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
            return new DayCounterViewHolder(mInflater.inflate(R.layout.list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(DayCounterViewHolder holder, int position) {
            Counter counter = getItem(position);

            holder.title.setText(counter.getTitle());
            holder.targetDay.setText(counter.getTargetDate());

            int resourceIdStyle = mContext.getResources().getIdentifier(
                counter.getBodyStyle() + "_config", "drawable", "mmpud.project.daycountwidget");
            Bitmap bg = BitmapFactory.decodeResource(mContext.getResources(), resourceIdStyle);
            int bgColor = 0;
            if (bg != null) {
                Bitmap onePixBg = Bitmap.createScaledBitmap(bg, 1, 1, true);
                bgColor = onePixBg.getPixel(0, 0);
            }
            holder.itemView.setBackgroundColor(bgColor);
            // Get the target date
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Calendar startDate = Calendar.getInstance();
            Calendar targetDate = Calendar.getInstance();
            try {
                targetDate.setTime(sdf.parse(counter.getTargetDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            int diffDays = Utils.daysBetween(startDate, targetDate);

            if (diffDays > 0) {
                String strDaysLeft = mContext.getResources().getQuantityString(
                    R.plurals.list_days_left, diffDays, diffDays);
                holder.dayDiff.setText(strDaysLeft);
            } else {
                diffDays = -diffDays;
                String strDaysSince = mContext.getResources().getQuantityString(
                    R.plurals.list_days_since, diffDays, (int) diffDays);
                holder.dayDiff.setText(strDaysSince);
            }
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

//    private String selectedLan = "";
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        switch (item.getItemId()) {
//            case R.id.language_settings:
//
//                final Dialog dialogLanguageSettings = new Dialog(DayCountMainActivity.this);
//                dialogLanguageSettings.setContentView(R.layout.language_settings_dialog);
//                dialogLanguageSettings.setTitle(getResources().getString(R.string.language_settings));
//
//                Spinner spnLanguageSettings = (Spinner) dialogLanguageSettings.findViewById(R.id.spn_language_settings);
//                // Set initial selected item in the spinner according to the locale language
//                Locale current = getResources().getConfiguration().locale;
//                Timber.d("current language: " + current.getLanguage());
//                if (current.getLanguage().equals("en")) {
//                    spnLanguageSettings.setSelection(0);
//                } else if (current.getLanguage().equals("zh")) {
//                    spnLanguageSettings.setSelection(1);
//                } else if (current.getLanguage().equals("ja")) {
//                    spnLanguageSettings.setSelection(3);
//                } else if (current.getLanguage().equals("fr")) {
//                    spnLanguageSettings.setSelection(4);
//                } else if (current.getLanguage().equals("tr")) {
//                    spnLanguageSettings.setSelection(5);
//                }
//
//                Button btnLanguageSettings = (Button) dialogLanguageSettings.findViewById(R.id.btn_language_settings);
//
//                spnLanguageSettings.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//
//                    @Override
//                    public void onItemSelected(AdapterView<?> parent,
//                                               View view, int position, long id) {
//                        selectedLan = parent.getSelectedItem().toString();
//                        Timber.d("Language [" + selectedLan + "] selected");
//                    }
//
//                    @Override
//                    public void onNothingSelected(AdapterView<?> parent) {
//                    }
//                });
//
//                btnLanguageSettings.setOnClickListener(new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//                        if (selectedLan.equals("English")) {
//                            // Set the language
//                            Resources res = getResources();
//                            Configuration conf = res.getConfiguration();
//                            conf.locale = Locale.ENGLISH;
//                            res.updateConfiguration(conf, null);
//                        } else if (selectedLan.equals("繁體中文")) {
//                            Resources res = getResources();
//                            Configuration conf = res.getConfiguration();
//                            conf.locale = Locale.TAIWAN;
//                            res.updateConfiguration(conf, null);
//                        } else if (selectedLan.equals("简体中文")) {
//                            Resources res = getResources();
//                            Configuration conf = res.getConfiguration();
//                            conf.locale = Locale.CHINA;
//                            res.updateConfiguration(conf, null);
//                        } else if (selectedLan.equals("日本語")) {
//                            Resources res = getResources();
//                            Configuration conf = res.getConfiguration();
//                            conf.locale = Locale.JAPAN;
//                            res.updateConfiguration(conf, null);
//                        } else if (selectedLan.equals("Français")) {
//                            Resources res = getResources();
//                            Configuration conf = res.getConfiguration();
//                            conf.locale = Locale.FRANCE;
//                            res.updateConfiguration(conf, null);
//                        } else if (selectedLan.equals("Türkçe")) {
//                            Resources res = getResources();
//                            Configuration conf = res.getConfiguration();
//                            conf.locale = new Locale("tr");
//                            res.updateConfiguration(conf, null);
//                        }
//
//                        dialogLanguageSettings.dismiss();
//
//                        // 1. Force to update the widgets
//                        Intent i = new Intent(Utils.WIDGET_UPDATE_ALL);
//                        sendBroadcast(i);
//
//                        // 2. Restart the main activity
//                        Intent intent = new Intent(DayCountMainActivity.this, DayCountMainActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(intent);
//                    }
//                });
//
//                dialogLanguageSettings.show();
//                break;
//            default:
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }

}
