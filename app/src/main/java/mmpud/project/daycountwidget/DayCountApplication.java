package mmpud.project.daycountwidget;

import android.app.Application;
import android.util.Log;

import timber.log.Timber;

import static timber.log.Timber.DebugTree;

public class DayCountApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }
    }

    /** A tree which logs important information for crash reporting. */
    private static class CrashReportingTree extends Timber.Tree {
        @Override protected void log(int priority, String tag, String message, Throwable t) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return;
            }
            if (t != null) {
                if (priority == Log.ERROR) {
                    // TODO log error
                } else if (priority == Log.WARN) {
                    // TODO log warning
                }
            }
        }
    }

}
