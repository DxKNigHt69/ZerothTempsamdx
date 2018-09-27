package com.whereismytransport.sdktemplateapp.ui.main;

import android.content.Context;

import com.whereismytransport.sdktemplateapp.LocationService;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public final class MainViewModel extends ViewModel {

    @Nullable
    private LocationService.LocationListener mLocationListener;

    @Nullable
    private MutableLiveData<android.location.Location> mLocationLiveData;

    @Nullable
    public LiveData<android.location.Location> getLocation(Context context) {
        if (context == null) {
            return null;
        }

        if (mLocationLiveData == null) {
            mLocationLiveData = new MutableLiveData<>();
            mLocationListener = new LocationService.LocationListener() {
                @Override
                public void onLocationChanged(android.location.Location location) {
                    mLocationLiveData.postValue(location);
                }
            };
            LocationService.getInstance().addLocationListener(mLocationListener);
            LocationService.getInstance().startLocationUpdates(context);
        }
        return mLocationLiveData;
    }

    @Override
    public void onCleared() {
        if (mLocationListener != null) {
            LocationService.getInstance().removeLocationListener(mLocationListener);
        }
    }
}
