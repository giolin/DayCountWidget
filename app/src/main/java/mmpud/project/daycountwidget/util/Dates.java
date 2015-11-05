package mmpud.project.daycountwidget.util;

import android.content.Context;
import android.content.res.Resources;
import android.text.Spannable;
import android.text.TextUtils;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.Weeks;
import org.joda.time.Years;

import mmpud.project.daycountwidget.R;
import mmpud.project.daycountwidget.data.db.Contract;

import static mmpud.project.daycountwidget.data.db.Contract.COUNT_BY_DAY;
import static mmpud.project.daycountwidget.data.db.Contract.COUNT_BY_MONTH;
import static mmpud.project.daycountwidget.data.db.Contract.COUNT_BY_WEEK;
import static mmpud.project.daycountwidget.data.db.Contract.COUNT_BY_YEAR;

public class Dates {

    /**
     * Transform a date string in to timestamp in milliseconds.
     *
     * @param date in the form of yyyy-MM-dd
     * @return milliseconds starting from 1970. 0 if input is empty.
     */
    public static long dateStringToTimestamp(String date) {
        if (TextUtils.isEmpty(date)) {
            return 0;
        }
        String[] ymd = date.split("-");
        if (ymd.length < 3) {
            return 0;
        }
        return new DateTime().withYear(Integer.parseInt(ymd[0]))
            .withMonthOfYear(Integer.parseInt(ymd[1]))
            .withDayOfMonth(Integer.parseInt(ymd[2]))
            .withTimeAtStartOfDay()
            .getMillis();
    }

    /**
     * Get the widget's content.
     *
     * @param context
     * @param countBy
     * @param targetDate
     * @return
     */
    static public Spannable getWidgetContentSpannable(Context context,
        @Contract.CountBy int countBy, DateTime targetDate) {
        Resources res = context.getResources();
        int diff;
        DateTime today = DateTime.now().withTimeAtStartOfDay();
        String str;
        switch (countBy) {
        case COUNT_BY_DAY: {
            diff = Days.daysBetween(today, targetDate).getDays();
            str = res.getQuantityString(diff > 0 ? R.plurals.widget_day_left
                : R.plurals.widget_day_since, diff, Math.abs(diff));
            break;
        }
        case COUNT_BY_WEEK: {
            diff = Weeks.weeksBetween(today, targetDate).getWeeks();
            str = res.getQuantityString(diff > 0 ? R.plurals.widget_week_left
                : R.plurals.widget_week_since, diff, Math.abs(diff));
            break;
        }
        case COUNT_BY_MONTH: {
            diff = Months.monthsBetween(today, targetDate).getMonths();
            str = res.getQuantityString(diff > 0 ? R.plurals.widget_month_left
                : R.plurals.widget_month_since, diff, Math.abs(diff));
            break;
        }
        case COUNT_BY_YEAR: {
            diff = Years.yearsBetween(today, targetDate).getYears();
            str = res.getQuantityString(diff > 0 ? R.plurals.widget_year_left
                : R.plurals.widget_year_since, diff, Math.abs(diff));
            break;
        }
        default: {
            diff = Days.daysBetween(today, targetDate).getDays();
            str = res.getQuantityString(diff > 0 ? R.plurals.widget_day_left
                : R.plurals.widget_day_since, diff, Math.abs(diff));
        }
        }
        return Texts.getResizedText(str);
    }

    /**
     * Get the string showing in the list of target dates and detail.
     *
     * @param context
     * @param countBy
     * @param targetDate
     * @return
     */
    static public String getDiffDaysString(Context context, @Contract.CountBy int countBy,
        DateTime targetDate) {
        DateTime today = DateTime.now().withTimeAtStartOfDay();
        int diffDays;
        String str;
        Resources res = context.getResources();
        switch (countBy) {
        case COUNT_BY_DAY: {
            diffDays = Days.daysBetween(today, targetDate).getDays();
            str = res.getQuantityString(diffDays > 0 ? R.plurals.list_day_left
                : R.plurals.list_day_since, diffDays, Math.abs(diffDays));
            break;
        }
        case COUNT_BY_WEEK: {
            diffDays = Weeks.weeksBetween(today, targetDate).getWeeks();
            str = res.getQuantityString(diffDays > 0 ? R.plurals.list_week_left
                : R.plurals.list_week_since, diffDays, Math.abs(diffDays));
            break;
        }
        case COUNT_BY_MONTH: {
            diffDays = Months.monthsBetween(today, targetDate).getMonths();
            str = res.getQuantityString(diffDays > 0 ? R.plurals.list_month_left
                : R.plurals.list_month_since, diffDays, Math.abs(diffDays));
            break;
        }
        case COUNT_BY_YEAR: {
            diffDays = Years.yearsBetween(today, targetDate).getYears();
            str = res.getQuantityString(diffDays > 0 ? R.plurals.list_year_left
                : R.plurals.list_year_since, diffDays, Math.abs(diffDays));
            break;
        }
        default: {
            diffDays = Days.daysBetween(today, targetDate).getDays();
            str = res.getQuantityString(diffDays > 0 ? R.plurals.list_day_left
                : R.plurals.list_day_since, diffDays, Math.abs(diffDays));
            break;
        }
        }
        return str;
    }

    private Dates() {}

}
