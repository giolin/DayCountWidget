package mmpud.project.daycountwidget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.widget.RemoteViews;

import org.joda.time.DateTime;

import mmpud.project.daycountwidget.data.db.Contract;
import mmpud.project.daycountwidget.data.db.DayCountDbHelper;
import mmpud.project.daycountwidget.util.Dates;
import mmpud.project.daycountwidget.util.Drawables;

import static mmpud.project.daycountwidget.data.db.Contract.COUNT_BY_DAY;
import static mmpud.project.daycountwidget.data.db.Contract.Widget;
import static org.joda.time.DateTimeConstants.MILLIS_PER_DAY;

public class DayCountWidgetProvider extends AppWidgetProvider {

    public static final String WIDGET_UPDATE_ALL = "android.appwidget.action.WIDGET_UPDATE_ALL";
    private static final int ALARM_ID = 5566;

    private static DayCountDbHelper mDbHelper;

    // called when new widget is created
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = buildRemoteViews(context, appWidgetId);
            manager.updateAppWidget(appWidgetId, views);
        }
    }

    // midnight alarm will trigger this method with a WIDGET_UPDATE_ALL intent
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        // when receiving the midnight alarm, update all the widgets
        if (WIDGET_UPDATE_ALL.equals(intent.getAction())) {
            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            ComponentName component = new ComponentName(context, DayCountWidgetProvider.class);
            int[] appWidgetIds = manager.getAppWidgetIds(component);
            for (int appWidgetId : appWidgetIds) {
                RemoteViews views = buildRemoteViews(context, appWidgetId);
                manager.updateAppWidget(appWidgetId, views);
            }
        }
    }

    // called when first widget of this kind is added
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        // start the alarm when the first widget is added
        // set the calendar to midnight on the next day
        long nextMidnight = DateTime.now()
            .withTimeAtStartOfDay()
            .plusDays(1)
            .getMillis();
        Intent alarmIntent = new Intent(WIDGET_UPDATE_ALL);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ALARM_ID, alarmIntent,
            PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        // RTC does not wake the device up
        alarmManager.setRepeating(AlarmManager.RTC, nextMidnight, MILLIS_PER_DAY, pendingIntent);
    }

    // called when last widget is deleted
    @Override
    public void onDisabled(Context context) {
        // delete the alarm
        Intent alarmIntent = new Intent(WIDGET_UPDATE_ALL);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ALARM_ID, alarmIntent,
            PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        if (mDbHelper == null) {
            mDbHelper = new DayCountDbHelper(context);
        }
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        for (int appWidgetId : appWidgetIds) {
            db.delete(Widget.TABLE_NAME, Widget.WIDGET_ID + "=?",
                new String[] {String.valueOf(appWidgetId)});
        }
        db.close();
    }

    /**
     * Generate remote views for the widget.
     *
     * @param context
     * @param mAppWidgetId
     * @return
     */
    public static RemoteViews buildRemoteViews(Context context, int mAppWidgetId) {
        // query from database
        if (mDbHelper == null) {
            mDbHelper = new DayCountDbHelper(context);
        }
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.query(Widget.TABLE_NAME, null, Widget.WIDGET_ID + "=?",
            new String[] {String.valueOf(mAppWidgetId)}, null, null, null);

        long targetDateMillis;
        String title;
        @Contract.CountBy int countBy;
        String headerStyle;
        String bodyStyle;
        if (cursor.moveToFirst()) {
            targetDateMillis = cursor.getLong(
                cursor.getColumnIndexOrThrow(Widget.TARGET_DATE));
            title = cursor.getString(
                cursor.getColumnIndexOrThrow(Widget.EVENT_TITLE));
            //noinspection ResourceType
            countBy = cursor.getInt(cursor.getColumnIndexOrThrow(Widget.COUNT_BY));
            headerStyle = cursor.getString(
                cursor.getColumnIndexOrThrow(Widget.HEADER_STYLE));
            bodyStyle = cursor.getString(
                cursor.getColumnIndexOrThrow(Widget.BODY_STYLE));
        } else {
            targetDateMillis = DateTime.now().getMillis();
            title = "";
            countBy = COUNT_BY_DAY;
            headerStyle = String.valueOf(ContextCompat.getColor(context, R.color.header_black));
            bodyStyle = String.valueOf(ContextCompat.getColor(context, R.color.body_black));
        }
        cursor.close();
        db.close();

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        Resources res = context.getResources();
        int widgetWidth = res.getDimensionPixelSize(R.dimen.widget_width);
        int headerHeight = res.getDimensionPixelSize(R.dimen.widget_header_height);
        int bodyHeight = res.getDimensionPixelSize(R.dimen.widget_body_height);

        // set header style
        GradientDrawable headerDrawable = Drawables.getHeaderDrawable(context, headerStyle);
        Bitmap bitmapH = Bitmap.createBitmap(widgetWidth, headerHeight, Bitmap.Config.ARGB_8888);
        Canvas headerCanvas = new Canvas(bitmapH);
        headerDrawable.setBounds(0, 0, headerCanvas.getWidth(), headerCanvas.getHeight());
        headerDrawable.draw(headerCanvas);
        views.setImageViewBitmap(R.id.widget_header_bg, bitmapH);
        // set body style
        GradientDrawable bodyDrawable = Drawables.getBodyDrawable(context, bodyStyle);
        Bitmap bitmapB = Bitmap.createBitmap(widgetWidth, bodyHeight, Bitmap.Config.ARGB_8888);
        Canvas bodyCanvas = new Canvas(bitmapB);
        bodyDrawable.setBounds(0, 0, bodyCanvas.getWidth(), bodyCanvas.getHeight());
        bodyDrawable.draw(bodyCanvas);
        views.setImageViewBitmap(R.id.widget_body_bg, bitmapB);
        // set view's title
        views.setTextViewText(R.id.widget_title, title);
        // set view's content
        DateTime targetDate = (new DateTime(targetDateMillis)).withTimeAtStartOfDay();
        views.setTextViewText(R.id.widget_content,
            Dates.getWidgetContentSpannable(context, countBy, targetDate));
        // create intent for clicking on the widget for detail
        Intent intent = new Intent(context, DayCountDetail.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        // no request code and no flags
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widget, pendingIntent);

        return views;
    }

}
