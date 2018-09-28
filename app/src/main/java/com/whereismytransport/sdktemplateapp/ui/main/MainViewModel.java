package com.whereismytransport.sdktemplateapp.ui.main;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.whereismytransport.sdktemplateapp.LocationService;
import com.whereismytransport.sdktemplateapp.R;
import com.whereismytransport.sdktemplateapp.SDKTemplateApplication;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import transportapisdk.JourneyBodyOptions;
import transportapisdk.TransportApiClient;
import transportapisdk.TransportApiClientSettings;
import transportapisdk.TransportApiResult;
import transportapisdk.models.Itinerary;
import transportapisdk.models.Journey;

public final class MainViewModel extends ViewModel {

    private final Executor mExecutor = Executors.newSingleThreadExecutor();

    private boolean startLocationSet;
    private MutableLiveData<Location> mStartLocationLiveData = new MutableLiveData<>();

    private MutableLiveData<LatLng> mEndLocationLiveData = new MutableLiveData<>();

    @Nullable
    private LocationService.LocationListener mLocationListener;

    @Nullable
    private MutableLiveData<android.location.Location> mLocationLiveData;

    private MutableLiveData<List<Itinerary>> mItinerariesLiveData = new MutableLiveData<>();

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
                        mStartLocationLiveData.postValue(location);
                    }
                }
            };
            LocationService.getInstance().addLocationListener(mLocationListener);
            LocationService.getInstance().startLocationUpdates(context);
        }

        return mLocationLiveData;
    }

    public LiveData<Location> getStartLocation() {
        return mStartLocationLiveData;
    }

    public LiveData<LatLng> getEndLocation() {
        return mEndLocationLiveData;
    }

    public void setEndLocation(LatLng location) {
        mEndLocationLiveData.postValue(location);

        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                String clientId = SDKTemplateApplication.getContext().getString(R.string.transportApiClientId);
                String clientSecret = SDKTemplateApplication.getContext().getString(R.string.transportApiClientSecret);

                TransportApiClient tapiClient = new TransportApiClient(new TransportApiClientSettings(clientId, clientSecret));

                double startLongitude = mStartLocationLiveData.getValue().getLongitude();
                double startLatitude = mStartLocationLiveData.getValue().getLatitude();
                double endLongitude = mEndLocationLiveData.getValue().getLongitude();
                double endLatitude = mEndLocationLiveData.getValue().getLatitude();

                JourneyBodyOptions journeyBodyOptions = new JourneyBodyOptions(null, null, null, null, 1, null);

                TransportApiResult<Journey> journeyResult = tapiClient.postJourney(journeyBodyOptions, startLatitude, startLongitude, endLatitude, endLongitude, null);

                List<Itinerary> itineraries = journeyResult.data.getItineraries();

                mItinerariesLiveData.postValue(itineraries);
            }
        });
    }

    public LiveData<List<Itinerary>> getItineraries() {
        return mItinerariesLiveData;
    }

    @Override
    public void onCleared() {
        if (mLocationListener != null) {
            LocationService.getInstance().removeLocationListener(mLocationListener);
        }
    }
}
