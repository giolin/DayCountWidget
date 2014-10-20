package mmpud.project.daycountwidget.util;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by georgelin on 10/10/14.
 */
public class Utils {

    public static int getScreenWidth(Context context) {

        int screenWidth;

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            final Point point = new Point();
            display.getSize(point);
            screenWidth = point.x;
        } else { // For older version
            screenWidth = display.getWidth();
        }
        return screenWidth;
    }

}
