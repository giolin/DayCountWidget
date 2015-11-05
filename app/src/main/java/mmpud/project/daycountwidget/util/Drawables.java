package mmpud.project.daycountwidget.util;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;

import mmpud.project.daycountwidget.R;

public class Drawables {

    public static GradientDrawable getHeaderDrawable(Context context, String style) {
        int cornerR = context.getResources().getDimensionPixelSize(R.dimen.widget_radius);
        GradientDrawable headerDrawable = new GradientDrawable();
        headerDrawable.setColor(Integer.parseInt(style));
        headerDrawable.setCornerRadii(new float[] {cornerR, cornerR, cornerR, cornerR, 0, 0, 0, 0});
        return headerDrawable;
    }

    public static GradientDrawable getBodyDrawable(Context context, String style) {
        int cornerR = context.getResources().getDimensionPixelSize(R.dimen.widget_radius);
        GradientDrawable bodyDrawable = new GradientDrawable();
        bodyDrawable.setColor(Integer.parseInt(style));
        bodyDrawable.setCornerRadii(new float[] {0, 0, 0, 0, cornerR, cornerR, cornerR, cornerR});
        return bodyDrawable;
    }

    private Drawables() {}

}
