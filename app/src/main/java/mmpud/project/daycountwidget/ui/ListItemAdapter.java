package mmpud.project.daycountwidget.ui;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import mmpud.project.daycountwidget.Counter;
import mmpud.project.daycountwidget.R;

/**
 * The adapter for the ListFragment in ForumListView
 *
 * @author George Lin
 */
public class ListItemAdapter extends ArrayAdapter<Counter> {

    Context mContext;
    int layoutResourceId;
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
        CounterLayoutHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new CounterLayoutHolder();
            holder.tvTargetDate = (TextView) row.findViewById(R.id.tv_target_date);
            holder.tvTitle = (TextView) row.findViewById(R.id.tv_title);

            row.setTag(holder);
        } else {
            holder = (CounterLayoutHolder) row.getTag();
        }

        Counter counter = counters.get(position);
        holder.tvTargetDate.setText(counter.getTargetDate());
        holder.tvTitle.setText(counter.getTitle());

        return row;
    }

    private static class CounterLayoutHolder {

        TextView tvTargetDate;
        TextView tvTitle;

    }

}
