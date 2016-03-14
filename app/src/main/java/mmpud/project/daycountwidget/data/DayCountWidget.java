package mmpud.project.daycountwidget.data;

import mmpud.project.daycountwidget.data.db.Contract;

/**
 * Data model for day count widget.
 */
public class DayCountWidget {

    public final int widgetId;
    public final String title;
    public final String description;
    public final long targetDay;
    @Contract.CountBy public final int countBy;
    public final String headerStyle;
    public final String bodyStyle;

    public DayCountWidget(int widgetId, String title, String description, long targetDate,
        int countBy, String headerStyle, String bodyStyle) {
        this.widgetId = widgetId;
        this.title = title;
        this.description = description;
        this.targetDay = targetDate;
        this.countBy = countBy;
        this.headerStyle = headerStyle;
        this.bodyStyle = bodyStyle;
    }

}