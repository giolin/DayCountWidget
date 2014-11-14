package mmpud.project.daycountwidget;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import mmpud.project.daycountwidget.util.Utils;

/**
 * Created by george on 2014/11/3.
 */
public class SelectStyleAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private int mResource;
    private List<String> mStringList = null;
    private List<Bitmap> mStyleIcon;

    public SelectStyleAdapter(Context context, int resource, List<String> stringList) {
        super(context, resource, stringList);
        mContext = context;
        mResource = resource;
        mStringList = stringList;
        mStyleIcon = new ArrayList<Bitmap>();
        for (int i = 0; i < mStringList.size(); i++) {
            String strStyle = mStringList.get(i);
            int resourceIDStyle = mContext.getResources().getIdentifier(strStyle + "_config", "drawable", "mmpud.project.daycountwidget");
            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), resourceIDStyle);
            if (bitmap != null) {
                mStyleIcon.add(Utils.getRoundedCornerBitmap(bitmap, 40));
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        CounterLayoutHolder holder;

        if (view == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            view = inflater.inflate(mResource, parent, false);

            holder = new CounterLayoutHolder();
            holder.ivStyle = (ImageView) view.findViewById(R.id.list_item_style);
            view.setTag(holder);
        } else {
            holder = (CounterLayoutHolder) view.getTag();
        }

        holder.ivStyle.setImageBitmap(mStyleIcon.get(position));

        return view;
    }

    private static class CounterLayoutHolder {
        ImageView ivStyle;
    }

}
