package mmpud.project.daycountwidget.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

/**
 * Created by georgelin on 10/10/14.
 */
public class Utils {

    private Utils() {
        throw new RuntimeException("Utils is not instantiable.");
    }

    public static final String PREFS_NAME = "mmpud.project.daycountwidget.DayCountWidget";

    public static final String KEY_TARGET_DATE = "target_date";

    public static final String KEY_TITLE = "title";

    public static final String KEY_STYLE_HEADER = "style_header";

    public static final String KEY_STYLE_BODY = "style_body";

    public static final String WIDGET_UPDATE_ALL = "android.appwidget.action.WIDGET_UPDATE_ALL";

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
        Timber.d("time: " + startDate.getTime() + ", " + endDate.getTime());
        long diffTime = endTime - startTime;
        Timber.d("milliseconds: " + diffTime + ", hours: " + diffTime / (1000 * 60 * 60));
        return TimeUnit.DAYS.convert(diffTime, TimeUnit.MILLISECONDS);
    }

    public static float textSizeGenerator(long num) {
        if (num < 0) {
            num = -num;
        }
        if (num >= 0 && num < 100) {
            return 36;
        } else if (num >= 100 && num < 1000) {
            return 32;
        } else if (num >= 1000 && num < 10000) {
            return 26;
        } else if (num >= 10000 && num < 100000) {
            return 22;
        } else {
            return 18;
        }
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

}
