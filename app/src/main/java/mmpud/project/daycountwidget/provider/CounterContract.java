package mmpud.project.daycountwidget.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by george on 2014/10/2.
 * <p/>
 * This class contains constants for the PostProvider
 */
public class CounterContract {
    /**
     * Content provider authority.
     */
    public static final String CONTENT_AUTHORITY = "mmpud.project.daycountwidget.provider";
    /**
     * Base URI. (content://mmpud.project.daycountwidget.provider)
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    /**
     * Path component for "entry"-type resources..
     */
    private static final String PATH_ENTRIES = "entries";

    /**
     * Columns supported by "entries" records.
     */
    public static class Counter implements BaseColumns {
        /**
         * MIME type for lists of entries.
         */
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.daycountwidget.entries";

        /**
         * MIME type for individual entry.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.daycountwidget.entry";

        /**
         * Fully qualified URI for "entry" resources.
         */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ENTRIES).build();

        /**
         * Table name where records are stored for "entry" resources.
         */
        public static final String TABLE_NAME = "counterlist";

        /**
         * Target date of the counter
         */
        public static final String COLUMN_NAME_TARGET_DATE = "target_date";

        /**
         * Title of the counter
         */
        public static final String COLUMN_NAME_TITLE = "title";

        /**
         * Detail of the counter
         */
        public static final String COLUMN_NAME_DETAIL = "detail";

        /**
         * Timestamp of the counter
         */
        public static final String COLUMN_NAME_CREATE_TIME = "create_time";
    }

}
