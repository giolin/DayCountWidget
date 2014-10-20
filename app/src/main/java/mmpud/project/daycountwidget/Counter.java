package mmpud.project.daycountwidget;

/**
 * Created by georgelin on 10/19/14.
 */
public class Counter {
    private int id;
    private String targetDate;
    private String title;
    private String detail;
    private String createTime;

    public Counter(int id, String targetDate, String title, String detail, String createTime) {
        this.id = id;
        this.targetDate = targetDate;
        this.title = title;
        this.detail = detail;
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

    public String getDetail() {
        return detail;
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

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
