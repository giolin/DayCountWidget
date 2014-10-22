package mmpud.project.daycountwidget.util;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

/**
 * Created by georgelin on 10/10/14.
 */
public class Utils {

    public static int getScreenWidth(Context context) {

        int screenWidth;

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            final Point point = new Point();
            display.getSize(point);
            screenWidth = point.x;
        } else { // For older version
            screenWidth = display.getWidth();
        }
        return screenWidth;
    }

    public static long daysBetween(Calendar startDate, Calendar endDate) {
        endDate.set(Calendar.HOUR_OF_DAY, 0);
        endDate.set(Calendar.MINUTE, 0);
        endDate.set(Calendar.SECOND, 0);
        endDate.set(Calendar.MILLISECOND, 0);
        startDate.set(Calendar.HOUR_OF_DAY, 0);
        startDate.set(Calendar.MINUTE, 0);
        startDate.set(Calendar.SECOND, 0);
        startDate.set(Calendar.MILLISECOND, 0);
        long startTime = startDate.getTime().getTime();
        long endTime = endDate.getTime().getTime();
        Timber.d("time" + startDate.getTime() + ", " + endDate.getTime());
        long diffTime = endTime - startTime;
        Timber.d("miliseconds: " + diffTime + ", hours: " + diffTime/(1000*60*60));
        return TimeUnit.DAYS.convert(diffTime, TimeUnit.MILLISECONDS);
    }

}
