package mmpud.project.daycountwidget.data;

/**
 * Data model for day count widget.
 */
public class DayCountWidget {

    public final int widgetId;
    public final String title;
    public final String description;
    public final long targetDate;
    public final String headerStyle;
    public final String bodyStyle;

    public DayCountWidget(int widgetId, String title, String description, long targetDate, String
        headerStyle, String bodyStyle) {
        this.widgetId = widgetId;
        this.title = title;
        this.description = description;
        this.targetDate = targetDate;
        this.headerStyle = headerStyle;
        this.bodyStyle = bodyStyle;
    }

}