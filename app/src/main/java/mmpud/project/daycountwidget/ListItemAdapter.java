package mmpud.project.daycountwidget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import mmpud.project.daycountwidget.util.Utils;

/**
 * Created by george on 2014/10/27.
 */
public class ListItemAdapter extends ArrayAdapter<Counter> {

    private Context mContext;
    private int layoutResourceId;
    List<Counter> counters = null;

    public ListItemAdapter(Context context, int resource, List<Counter> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.layoutResourceId = resource;
        this.counters = objects;
    }

    @Override
    public View getView(int position, View row, ViewGroup parent) {

        CounterLayoutHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new CounterLayoutHolder();
            holder.tvTargetDate = (TextView) row.findViewById(R.id.list_item_tv_target_date);
            holder.tvTitle = (TextView) row.findViewById(R.id.list_item_tv_title);
            holder.tvDayDiff = (TextView) row.findViewById(R.id.list_item_tv_day_diff);
            holder.rlCounter = (RelativeLayout) row.findViewById(R.id.list_item_counter);
            row.setTag(holder);
        } else {
            holder = (CounterLayoutHolder) row.getTag();
        }

        Counter counter = counters.get(position);
        holder.tvTargetDate.setText(counter.getTargetDate());
        holder.tvTitle.setText(counter.getTitle());
        // TODO - set background of each row
        int resourceIdStyle = mContext.getResources().getIdentifier(counter.getStyleNum() + "_config", "drawable", "mmpud.project.daycountwidget");
//        Drawable d = mContext.getResources().getDrawable(resourceIdStyle);
        Bitmap bitmapBg = BitmapFactory.decodeResource(mContext.getResources(),
                resourceIdStyle);
        Bitmap onePixelBitmap = Bitmap.createScaledBitmap(bitmapBg, 1, 1, true);
        int pixel = onePixelBitmap.getPixel(0,0);
        holder.rlCounter.setBackgroundColor(pixel);
        // Get the target date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Calendar startDate = Calendar.getInstance();
        Calendar targetDate = Calendar.getInstance();
        try {
            targetDate.setTime(sdf.parse(counter.getTargetDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Long diffDays = Utils.daysBetween(startDate, targetDate);

        if (diffDays > 0) {
            int diffDaysInt = diffDays.intValue();
            String strDaysLeft = mContext.getResources().getQuantityString(R.plurals.list_days_left, diffDaysInt, (int) diffDaysInt);
            holder.tvDayDiff.setText(strDaysLeft);
        } else {
            int diffDaysInt = -diffDays.intValue();
            String strDaysSince = mContext.getResources().getQuantityString(R.plurals.list_days_since, diffDaysInt, (int) diffDaysInt);
            holder.tvDayDiff.setText(strDaysSince);
        }
        counter.getStyleNum();
        // TODO - set row's background!
        return row;
    }

    private static class CounterLayoutHolder {
        RelativeLayout rlCounter;
        TextView tvTargetDate;
        TextView tvTitle;
        TextView tvDayDiff;
    }
}
