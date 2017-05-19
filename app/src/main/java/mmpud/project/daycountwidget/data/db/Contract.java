package mmpud.project.daycountwidget.data.db;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Database contract.
 */
public class Contract {

    @IntDef({COUNT_BY_DAY, COUNT_BY_WEEK, COUNT_BY_MONTH, COUNT_BY_YEAR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CountBy {}

    public static final int COUNT_BY_DAY = 0;
    public static final int COUNT_BY_WEEK = 1;
    public static final int COUNT_BY_MONTH = 2;
    public static final int COUNT_BY_YEAR = 3;

    public static abstract class Widget {

        public static final String TABLE_NAME = "widgets";

        public static final String WIDGET_ID = "id";

        public static final String EVENT_TITLE = "title";

        public static final String EVENT_DESCRIPTION = "description";

        public static final String TARGET_DATE = "td";

        public static final String COUNT_BY = "count_by";

        public static final String HEADER_STYLE = "header";

        public static final String BODY_STYLE = "body";

        public static final String ALPHA = "alpha";

    }

    /**
     * This class is not instantiable.
     */
    private Contract() {}

}
