package com.whereismytransport.sdktemplateapp;

import android.content.Context;
import android.location.Location;
import android.os.Looper;
import android.text.format.DateUtils;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public final class LocationService {
    private static final long UPDATE_INTERVAL_MILLIS = DateUtils.SECOND_IN_MILLIS;
    private static final int LOCATION_SMALLEST_DISPLACEMENT_METRES = 1;

    private static final LocationService INSTANCE = new LocationService();

    public static LocationService getInstance() {
        return INSTANCE;
    }

    // Private constructor prevents outside instantiation of class.
    private LocationService() {
    }

    public interface LocationListener {
        void onLocationChanged(Location location);
    }

    private final List<WeakReference<LocationListener>> mLocationListeners = new ArrayList<>();

    @Nullable
    private CustomFusedLocationCallback mFusedLocationCallback;

    @Nullable
    private FusedLocationProviderClient mFusedLocationProviderClient;

    @Nullable
    private Location mCurrentLocation = null;

    @Nullable
    public Location getCurrentLocation() {
        return mCurrentLocation;
    }

    public void startLocationUpdates(@NonNull Context context) {
        if (context == null) {
            return;
        }

        requestLocationUpdatesInternal(context);
    }

    private void requestLocationUpdatesInternal(@NonNull Context context) {
        if (context == null) {
            return;
        }

        final Context applicationContext = context.getApplicationContext();

        if (mFusedLocationProviderClient == null) {
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(applicationContext);
        } else if (mFusedLocationCallback != null) {
            mFusedLocationProviderClient.removeLocationUpdates(mFusedLocationCallback);
        }

        mFusedLocationCallback = new CustomFusedLocationCallback();

        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_MILLIS)
                .setSmallestDisplacement(LOCATION_SMALLEST_DISPLACEMENT_METRES);

        try {
            mFusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    mFusedLocationCallback,
                    Looper.getMainLooper()); // Callbacks will be invoked on Main (UI) thread.
        } catch (SecurityException e) {
            // Gotta have permission ¯\_(ツ)_/¯
        }
    }

    public void endLocationUpdates() {
        if (mFusedLocationProviderClient != null) {
            if (mFusedLocationCallback != null) {
                mFusedLocationProviderClient.removeLocationUpdates(mFusedLocationCallback);
            }
            mFusedLocationProviderClient = null;
        }
    }

    public void addLocationListener(@NonNull LocationListener locationListener) {
        if (locationListener == null) {
            return;
        }

        mLocationListeners.add(new WeakReference<>(locationListener));

        final Location location = mCurrentLocation;
        if (location != null) {
            for (WeakReference<LocationListener> weakListener : mLocationListeners) {
                if (weakListener.get() != null) {
                    weakListener.get().onLocationChanged(location);
                }
            }
        }
    }

    public void removeLocationListener(@NonNull LocationListener locationListener) {
        if (locationListener == null) {
            return;
        }

        Iterator<WeakReference<LocationListener>> iterator = mLocationListeners.iterator();
        while (iterator.hasNext()) {
            WeakReference<LocationListener> weakListener = iterator.next();
            if (weakListener.get() != null && weakListener.get() == locationListener) {
                iterator.remove();
            } else if (weakListener.get() == null) {
                iterator.remove();
            }
        }
    }

    private void onLocationChangedInternal(LocationResult locationResult) {
        final Location location = locationResult.getLastLocation();
        if (location == null) {
            return;
        }

        mCurrentLocation = location;

        for (WeakReference<LocationListener> weakListener : mLocationListeners) {
            if (weakListener.get() != null) {
                weakListener.get().onLocationChanged(location);
            }
        }
    }

    private final class CustomFusedLocationCallback extends LocationCallback {

        @Override
        public void onLocationAvailability(LocationAvailability locationAvailability) {
        }

        @Override
        public void onLocationResult(LocationResult locationResult) {
            onLocationChangedInternal(locationResult);
        }
    }
}
