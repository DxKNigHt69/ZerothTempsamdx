package com.whereismytransport.sdktemplateapp.ui.main;

import android.content.Context;
import android.location.Location;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.whereismytransport.sdktemplateapp.LocationService;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public final class MainViewModel extends ViewModel {

    private boolean startLocationSet;
    private MutableLiveData<Location> startLocationLiveData = new MutableLiveData<>();

    private MutableLiveData<LatLng> endLocationLiveData = new MutableLiveData<>();

    @Nullable
    private LocationService.LocationListener mLocationListener;

    @Nullable
    private MutableLiveData<android.location.Location> mLocationLiveData;

    public LiveData<Location> getLocation(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context can't be null.");
        }

        if (mLocationLiveData == null) {
            mLocationLiveData = new MutableLiveData<>();
            mLocationListener = new LocationService.LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    mLocationLiveData.postValue(location);

                    if (!startLocationSet) {
                        startLocationSet = true;
                        startLocationLiveData.postValue(location);
                    }
                }
            };
            LocationService.getInstance().addLocationListener(mLocationListener);
            LocationService.getInstance().startLocationUpdates(context);
        }

        return mLocationLiveData;
    }

    public LiveData<Location> getStartLocation() {
        return startLocationLiveData;
    }

    public LiveData<LatLng> getEndLocation() {
        return endLocationLiveData;
    }

    public void setEndLocation(LatLng location) {
        endLocationLiveData.postValue(location);
    }

    @Override
    public void onCleared() {
        if (mLocationListener != null) {
            LocationService.getInstance().removeLocationListener(mLocationListener);
        }
    }
}
