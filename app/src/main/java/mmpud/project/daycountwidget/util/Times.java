package mmpud.project.daycountwidget.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.format.DateTimeFormatter;

public class Times {

    public static final String WIDGET_UPDATE_ALL = "android.appwidget.action.WIDGET_UPDATE_ALL";
    public static final int ALARM_ID = 5566;
    public static final long MILLIS_PER_DAY = 24 * 60 * 60 * 1000;

    public static DateTimeFormatter mDateFormatter;

    /**
     * Get start of day in milliseconds. Note that it is UTC timestamp.
     *
     * @return
     */
    public static long getStartOfDayMillis() {
        return LocalDate.now().atStartOfDay().atZone(ZoneOffset.UTC).toInstant().toEpochMilli();
    }

    /**
     * Get local date time from UTC timestamp.
     *
     * @param utcTimestamp
     * @return
     */
    public static LocalDateTime getLocalDateTime(long utcTimestamp) {
        return Instant.ofEpochMilli(utcTimestamp).atZone(ZoneOffset.UTC).toLocalDateTime();
    }

    /**
     * Get {@link DateTimeFormatter} of pattern yyyy-MM-dd
     *
     * @return
     */
    public static DateTimeFormatter getDateFormatter() {
        if (mDateFormatter == null) {
            mDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        }
        return mDateFormatter;
    }

    public static void setMidnightAlarm(Context context) {
        long nextMidnight = LocalDate.now().atStartOfDay().plusDays(1)
            .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        PendingIntent intent = PendingIntent.getBroadcast(context, ALARM_ID,
            new Intent(WIDGET_UPDATE_ALL), PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        // RTC does not wake the device up
        alarmManager.setRepeating(AlarmManager.RTC, nextMidnight, Times.MILLIS_PER_DAY, intent);
    }

    private Times() {}

}
