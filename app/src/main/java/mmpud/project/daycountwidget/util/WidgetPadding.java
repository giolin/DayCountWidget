package mmpud.project.daycountwidget.util;

import android.content.Context;

public class WidgetPadding {

    final static private String PREF_NAME = "WIDGET_PADDING";
    final static private String HORIZONTAL_PADDING = "HORIZONTAL_PADDING";
    final static private String VERTICAL_PADDING = "VERTICAL_PADDING";
    final static private int DEFAULT_PADDING = 0;

    static public void saveHorizontalPadding(Context context, int padding) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .putInt(HORIZONTAL_PADDING, padding)
                .apply();
    }

    static public void saveVerticalPadding(Context context, int padding) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .putInt(VERTICAL_PADDING, padding)
                .apply();
    }

    static public int getHorizontalPadding(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getInt(HORIZONTAL_PADDING, DEFAULT_PADDING);
    }

    static public int getVerticalPadding(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getInt(VERTICAL_PADDING, DEFAULT_PADDING);
    }

    private WidgetPadding() {
    }

}
