package mmpud.project.daycountwidget;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LanguageActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener {

    public static final int LANGUAGE_SELECT_REQUEST_CODE = 5566;

    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.language_settings) RadioGroup mLanguageRadioButtons;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.language_screen);
        ButterKnife.bind(this);

        mToolbar.setTitle(R.string.language_settings);
        mToolbar.inflateMenu(R.menu.language_menu);
        mToolbar.setOnMenuItemClickListener(this);
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                finish();
            }
        });

        Locale current = getResources().getConfiguration().locale;
        int id = R.id.english;
        if (current.equals(Locale.ENGLISH)) {
            id = R.id.english;
        } else if (current.equals(Locale.TRADITIONAL_CHINESE)) {
            id = R.id.chinese;
        } else if (current.equals(Locale.SIMPLIFIED_CHINESE)) {
            id = R.id.chinese_sim;
        } else if (current.equals(Locale.JAPANESE)) {
            id = R.id.japanese;
        } else if (current.equals(Locale.FRENCH)) {
            id = R.id.french;
        } else if (current.getLanguage().equals("tr")) {
            id = R.id.turkish;
        }
        mLanguageRadioButtons.check(id);
    }

    @Override public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.btn_ok) {
            Resources res = getResources();
            Configuration config = res.getConfiguration();
            switch (mLanguageRadioButtons.getCheckedRadioButtonId()) {
            case R.id.english: {
                config.locale = Locale.ENGLISH;
                break;
            }
            case R.id.chinese: {
                config.locale = Locale.TRADITIONAL_CHINESE;
                break;
            }
            case R.id.chinese_sim: {
                config.locale = Locale.SIMPLIFIED_CHINESE;
                break;
            }
            case R.id.japanese: {
                config.locale = Locale.JAPANESE;
                break;
            }
            case R.id.french: {
                config.locale = Locale.FRENCH;
                break;
            }
            case R.id.turkish: {
                config.locale = new Locale("tr");
                break;
            }
            default: {
                config.locale = Locale.ENGLISH;
                break;
            }
            }
            res.updateConfiguration(config, res.getDisplayMetrics());
            // force to update the widgets
            Intent i = new Intent(DayCountWidgetProvider.WIDGET_UPDATE_ALL);
            sendBroadcast(i);

            // tell the last page the language is set successfully and return to that page
            setResult(RESULT_OK);
            finish();
            return true;
        }
        return false;
    }

}
