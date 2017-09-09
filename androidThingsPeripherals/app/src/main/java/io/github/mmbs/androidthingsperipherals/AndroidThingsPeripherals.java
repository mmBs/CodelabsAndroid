package io.github.mmbs.androidthingsperipherals;

import android.app.Application;
import timber.log.Timber;

/**
 * Created by michal on 08/09/2017.
 */

public class AndroidThingsPeripherals extends Application {

  @Override public void onCreate() {
    super.onCreate();

    if (BuildConfig.DEBUG) {
      Timber.plant(new Timber.DebugTree());
    }
  }
}
