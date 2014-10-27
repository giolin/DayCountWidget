package mmpud.project.daycountwidget;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        CounterLayoutHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new CounterLayoutHolder();
            holder.tvTargetDate = (TextView) row.findViewById(R.id.list_item_tv_target_date);
            holder.tvTitle = (TextView) row.findViewById(R.id.list_item_tv_title);
            holder.tvDayDiff = (TextView) row.findViewById(R.id.list_item_tv_day_diff);
            holder.tvSinceLeft = (TextView) row.findViewById(R.id.list_item_tv_day_since_left);

            row.setTag(holder);
        } else {
            holder = (CounterLayoutHolder) row.getTag();
        }

        Counter counter = counters.get(position);
        holder.tvTargetDate.setText(counter.getTargetDate());
        holder.tvTitle.setText(counter.getTitle());
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
            holder.tvSinceLeft.setText(getContext().getResources().getString(R.string.days_left));
            holder.tvDayDiff.setText(Long.toString(diffDays));
        } else {
            holder.tvSinceLeft.setText(getContext().getResources().getString(R.string.days_since));
            holder.tvDayDiff.setText(Long.toString(-diffDays));
        }

        return row;
    }

    private static class CounterLayoutHolder {
        TextView tvTargetDate;
        TextView tvTitle;
        TextView tvDayDiff;
        TextView tvSinceLeft;
    }
}
