package mmpud.project.daycountwidget.data.db;

/**
 * Database contract.
 */
public class DayCountContract {

    public static abstract class DayCountWidget {

        public static final String TABLE_NAME = "widgets";

        public static final String WIDGET_ID = "id";

        public static final String EVENT_TITLE = "title";

        public static final String EVENT_DESCRIPTION = "description";

        public static final String TARGET_DATE = "td";

        public static final String HEADER_STYLE = "header";

        public static final String BODY_STYLE = "body";

    }

    /**
     * This class is not instantiable.
     */
    private DayCountContract() {}

}
