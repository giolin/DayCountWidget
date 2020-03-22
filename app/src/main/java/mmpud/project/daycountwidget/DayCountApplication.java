package mmpud.project.daycountwidget;

import android.app.Application;
import com.jakewharton.threetenabp.AndroidThreeTen;

public class DayCountApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    // init AndroidThreeTen
    AndroidThreeTen.init(this);
  }
}
