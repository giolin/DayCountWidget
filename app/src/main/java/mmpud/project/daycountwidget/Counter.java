package mmpud.project.daycountwidget;

/**
 * Created by georgelin on 10/19/14.
 */
public class Counter {
    private int id;
    private String targetDate;
    private String title;
    private String createTime;
    private int style;

    public Counter(int id, String targetDate, String title, int style, String createTime) {
        this.id = id;
        this.targetDate = targetDate;
        this.title = title;
        this.style = style;
        this.createTime = createTime;
    }

    public int getId() {
        return id;
    }

    public String getTargetDate() {
        return targetDate;
    }

    public String getTitle() {
        return title;
    }

    public int getStyle() {
        return style;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTargetDate(String targetDate) {
        this.targetDate = targetDate;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
