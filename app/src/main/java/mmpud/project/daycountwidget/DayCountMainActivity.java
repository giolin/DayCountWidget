package mmpud.project.daycountwidget;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import mmpud.project.daycountwidget.util.Utils;
import timber.log.Timber;


public class DayCountMainActivity extends Activity {

    ListView listViewDayCounter;
    TextView tvNoWidgetMsg;
    ArrayList<Counter> counters;
    ListItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_count_main);
        setTitle(getResources().getString(R.string.app_name));

        counters = new ArrayList<Counter>();
        adapter = new ListItemAdapter(this, R.layout.list_item, counters);

        listViewDayCounter = (ListView) findViewById(R.id.lv_day_counter);
        listViewDayCounter.setAdapter(adapter);
        listViewDayCounter.setClickable(false);
        tvNoWidgetMsg = (TextView) findViewById(R.id.tv_no_widget_msg);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateAdapter();

        if (counters.isEmpty()) {
            tvNoWidgetMsg.setVisibility(View.VISIBLE);
        } else {
            tvNoWidgetMsg.setVisibility(View.GONE);
        }
    }

    private void updateAdapter() {
        counters.clear();
        // Get all available day count widget ids
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        ComponentName thisAppWidget = new ComponentName(this, DayCountWidget.class);

        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);

        for (int appWidgetId : appWidgetIds) {
            Timber.d("appWidgetId: " + appWidgetId);
            // Get information: 1. YYYY-MM-DD
            //					2. title
            //					3. widget body style
            // from shared preferences according to the appWidgetId
            SharedPreferences prefs = this.getSharedPreferences(Utils.PREFS_NAME, 0);
            counters.add(new Counter(prefs.getString(Utils.KEY_TARGET_DATE + appWidgetId, ""),
                    prefs.getString(Utils.KEY_TITLE + appWidgetId, ""),
                    prefs.getString(Utils.KEY_STYLE_BODY + appWidgetId, "")));
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.day_count_menu, menu);
        return true;
    }

    private String selectedLan = "";

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.language_settings:

                final Dialog dialogLanguageSettings = new Dialog(DayCountMainActivity.this);
                dialogLanguageSettings.setContentView(R.layout.language_settings_dialog);
                dialogLanguageSettings.setTitle(getResources().getString(R.string.language_settings));

                Spinner spnLanguageSettings = (Spinner) dialogLanguageSettings.findViewById(R.id.spn_language_settings);
                // Set initial selected item in the spinner according to the locale language
                Locale current = getResources().getConfiguration().locale;
                Timber.d("current language: " + current.getLanguage());
                if (current.getLanguage().equals("en")) {
                    spnLanguageSettings.setSelection(0);
                } else if (current.getLanguage().equals("zh")) {
                    spnLanguageSettings.setSelection(1);
                } else if (current.getLanguage().equals("ja")) {
                    spnLanguageSettings.setSelection(3);
                } else if (current.getLanguage().equals("fr")) {
                    spnLanguageSettings.setSelection(4);
                } else if (current.getLanguage().equals("tr")) {
                    spnLanguageSettings.setSelection(5);
                }

                Button btnLanguageSettings = (Button) dialogLanguageSettings.findViewById(R.id.btn_language_settings);

                spnLanguageSettings.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent,
                                               View view, int position, long id) {
                        selectedLan = parent.getSelectedItem().toString();
                        Timber.d("Language [" + selectedLan + "] selected");
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                btnLanguageSettings.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (selectedLan.equals("English")) {
                            // Set the language
                            Resources res = getResources();
                            Configuration conf = res.getConfiguration();
                            conf.locale = Locale.ENGLISH;
                            res.updateConfiguration(conf, null);
                        } else if (selectedLan.equals("繁體中文")) {
                            Resources res = getResources();
                            Configuration conf = res.getConfiguration();
                            conf.locale = Locale.TAIWAN;
                            res.updateConfiguration(conf, null);
                        } else if (selectedLan.equals("简体中文")) {
                            Resources res = getResources();
                            Configuration conf = res.getConfiguration();
                            conf.locale = Locale.CHINA;
                            res.updateConfiguration(conf, null);
                        } else if (selectedLan.equals("日本語")) {
                            Resources res = getResources();
                            Configuration conf = res.getConfiguration();
                            conf.locale = Locale.JAPAN;
                            res.updateConfiguration(conf, null);
                        } else if (selectedLan.equals("Français")) {
                            Resources res = getResources();
                            Configuration conf = res.getConfiguration();
                            conf.locale = Locale.FRANCE;
                            res.updateConfiguration(conf, null);
                        } else if (selectedLan.equals("Türkçe")) {
                            Resources res = getResources();
                            Configuration conf = res.getConfiguration();
                            conf.locale = new Locale("tr");
                            res.updateConfiguration(conf, null);
                        }

                        dialogLanguageSettings.dismiss();

                        // 1. Force to update the widgets
                        Intent i = new Intent(Utils.WIDGET_UPDATE_ALL);
                        sendBroadcast(i);

                        // 2. Restart the main activity
                        Intent intent = new Intent(DayCountMainActivity.this, DayCountMainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });

                dialogLanguageSettings.show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
