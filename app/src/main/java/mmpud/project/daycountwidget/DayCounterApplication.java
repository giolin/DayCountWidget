package mmpud.project.daycountwidget;

import android.app.Application;

import mortar.Mortar;
import mortar.MortarScope;
import timber.log.Timber;

import static timber.log.Timber.DebugTree;

/**
 * Created by georgelin on 10/10/14.
 */
public class DayCounterApplication extends Application {
    private MortarScope rootScope;

    @Override
    public void onCreate() {
        super.onCreate();

        rootScope = Mortar.createRootScope(BuildConfig.DEBUG);

        if (BuildConfig.DEBUG) {
            Timber.plant(new DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }
    }

    public MortarScope getRootScope() {
        return rootScope;
    }

    /**
     * A tree which logs important information for crash reporting.
     */
    private static class CrashReportingTree extends Timber.HollowTree {
        @Override
        public void i(String message, Object... args) {
            // TODO e.g., Crashlytics.log(String.format(message, args));
        }

        @Override
        public void i(Throwable t, String message, Object... args) {
            i(message, args); // Just add to the log.
        }

        @Override
        public void e(String message, Object... args) {
            i("ERROR: " + message, args); // Just add to the log.
        }

        @Override
        public void e(Throwable t, String message, Object... args) {
            e(message, args);

            // TODO e.g., Crashlytics.logException(t);
        }
    }
}
