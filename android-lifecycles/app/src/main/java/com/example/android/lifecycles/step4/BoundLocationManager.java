/*
 * Copyright 2017, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.lifecycles.step4;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class BoundLocationManager {

  public static void bindLocationListenerIn(AppCompatActivity lifecycleOwner,
      LocationListener listener, Context context) {
    new BoundLocationListener(lifecycleOwner, listener, context);
  }

  @SuppressWarnings("MissingPermission") static class BoundLocationListener
      implements LifecycleObserver {

    private final Context context;
    private LocationManager locationManager;
    private final LocationListener listener;

    public BoundLocationListener(AppCompatActivity lifecycleOwner, LocationListener listener,
        Context context) {
      this.context = context;
      this.listener = listener;
      //TODO: Add lifecycle observer
      lifecycleOwner.getLifecycle().addObserver(this);
    }

    //TODO: Call this on resume
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME) void addLocationListener() {
      // Note: Use the Fused Location Provider from Google Play Services instead.
      // https://developers.google.com/android/reference/com/google/android/gms/location/FusedLocationProviderApi

      locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
      locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
      Log.d("BoundLocationMgr", "Listener added");

      // Force an update with the last location, if available.
      Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
      if (lastLocation != null) {
        listener.onLocationChanged(lastLocation);
      }
    }

    //TODO: Call this on pause
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE) void removeLocationListener() {
      if (locationManager == null) {
        return;
      }
      locationManager.removeUpdates(listener);
      locationManager = null;
      Log.d("BoundLocationMgr", "Listener removed");
    }
  }
}
