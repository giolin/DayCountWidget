package mmpud.project.daycountwidget.util;

/**
 * Created by george on 2014/10/27.
 */
public class Counter {
    private String targetDate;
    private String title;
    private String bodyStyle;

    public Counter(String targetDate, String title, String styleNum) {
        this.targetDate = targetDate;
        this.title = title;
        this.bodyStyle = styleNum;
    }

    public String getTargetDate() {
        return targetDate;
    }

    public String getTitle() {
        return title;
    }

    public String getBodyStyle() {
        return bodyStyle;
    }

    public void setTargetDate(String targetDate) {
        this.targetDate = targetDate;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBodyStyle(String bodyStyle) {
        this.bodyStyle = bodyStyle;
    }
}
