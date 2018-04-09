package mmpud.project.daycountwidget.util;

import android.content.Context;
import android.content.res.Resources;
import android.text.Spannable;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.temporal.ChronoUnit;

import mmpud.project.daycountwidget.R;
import mmpud.project.daycountwidget.data.db.Contract;

import static mmpud.project.daycountwidget.data.db.Contract.COUNT_BY_DAY;
import static mmpud.project.daycountwidget.data.db.Contract.COUNT_BY_MONTH;
import static mmpud.project.daycountwidget.data.db.Contract.COUNT_BY_WEEK;
import static mmpud.project.daycountwidget.data.db.Contract.COUNT_BY_YEAR;

public class Dates {

    /**
     * Get the widget's content.
     *
     * @param context
     * @param countBy
     * @param targetDay
     * @return
     */
    static public Spannable getWidgetContentSpannable(Context context,
                                                      @Contract.CountBy int countBy,
                                                      LocalDateTime targetDay) {
        Resources res = context.getResources();
        int diff;
        String str;
        switch (countBy) {
            case COUNT_BY_DAY: {
                diff = (int) ChronoUnit.DAYS.between(LocalDate.now(), targetDay.toLocalDate());
                str = res.getQuantityString(diff > 0 ? R.plurals.widget_day_left
                        : R.plurals.widget_day_since, diff, Math.abs(diff));
                break;
            }
            case COUNT_BY_WEEK: {
                diff = (int) ChronoUnit.DAYS.between(LocalDate.now(), targetDay.toLocalDate()) / 7;
                str = res.getQuantityString(diff > 0 ? R.plurals.widget_week_left
                        : R.plurals.widget_week_since, diff, Math.abs(diff));
                break;
            }
            case COUNT_BY_MONTH: {
                diff = (int) ChronoUnit.MONTHS.between(LocalDate.now(), targetDay.toLocalDate());
                str = res.getQuantityString(diff > 0 ? R.plurals.widget_month_left
                        : R.plurals.widget_month_since, diff, Math.abs(diff));
                break;
            }
            case COUNT_BY_YEAR: {
                diff = (int) ChronoUnit.YEARS.between(LocalDate.now(), targetDay.toLocalDate());
                str = res.getQuantityString(diff > 0 ? R.plurals.widget_year_left
                        : R.plurals.widget_year_since, diff, Math.abs(diff));
                break;
            }
            default: {
                diff = (int) ChronoUnit.DAYS.between(LocalDate.now(), targetDay.toLocalDate());
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
     * @param targetDay
     * @return
     */
    static public String getDiffDaysString(Context context, @Contract.CountBy int countBy,
                                           LocalDateTime targetDay) {
        int diff;
        String str;
        Resources res = context.getResources();
        switch (countBy) {
            case COUNT_BY_DAY: {
                diff = (int) ChronoUnit.DAYS.between(LocalDate.now(), targetDay);
                str = res.getQuantityString(diff > 0 ? R.plurals.list_day_left
                        : R.plurals.list_day_since, diff, Math.abs(diff));
                break;
            }
            case COUNT_BY_WEEK: {
                diff = (int) ChronoUnit.DAYS.between(LocalDate.now(), targetDay)
                        / 7;
                str = res.getQuantityString(diff > 0 ? R.plurals.list_week_left
                        : R.plurals.list_week_since, diff, Math.abs(diff));
                break;
            }
            case COUNT_BY_MONTH: {
                diff = (int) ChronoUnit.MONTHS.between(LocalDate.now(), targetDay);
                str = res.getQuantityString(diff > 0 ? R.plurals.list_month_left
                        : R.plurals.list_month_since, diff, Math.abs(diff));
                break;
            }
            case COUNT_BY_YEAR: {
                diff = (int) ChronoUnit.YEARS.between(LocalDate.now(), targetDay);
                str = res.getQuantityString(diff > 0 ? R.plurals.list_year_left
                        : R.plurals.list_year_since, diff, Math.abs(diff));
                break;
            }
            default: {
                diff = (int) ChronoUnit.DAYS.between(LocalDate.now(), targetDay);
                str = res.getQuantityString(diff > 0 ? R.plurals.list_day_left
                        : R.plurals.list_day_since, diff, Math.abs(diff));
                break;
            }
        }
        return str;
    }

    private Dates() {
    }

}
