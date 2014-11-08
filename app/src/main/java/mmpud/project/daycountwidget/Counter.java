package mmpud.project.daycountwidget;

/**
 * Created by george on 2014/10/27.
 */
public class Counter {
    private String targetDate;
    private String title;
    private String styleNum;

    public Counter(String targetDate, String title, String styleNum) {
        this.targetDate = targetDate;
        this.title = title;
        this.styleNum = styleNum;
    }

    public String getTargetDate() {
        return targetDate;
    }

    public String getTitle() {
        return title;
    }

    public String getStyleNum() {
        return styleNum;
    }

    public void setTargetDate(String targetDate) {
        this.targetDate = targetDate;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStyleNum(String styleNum) {
        this.styleNum = styleNum;
    }
}
