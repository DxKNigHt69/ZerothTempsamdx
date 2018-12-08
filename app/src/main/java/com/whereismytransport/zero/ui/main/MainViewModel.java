package com.whereismytransport.zero.ui.main;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.whereismytransport.zero.LocationService;
import com.whereismytransport.zero.R;
import com.whereismytransport.zero.ZerothTemplateApplication;

import java.util.ArrayList;
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

    private static final String LOG_TAGsrc = "hello";
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
//        double startLongitude = mStartLocationLiveData.getValue().getLongitude();
//        double startLatitude = mStartLocationLiveData.getValue().getLatitude();
//        double endLongitude = location.getLongitude();
//        double endLatitude = location.getLatitude();



        try{
            mExecutor.execute(() -> {
                String clientId = ZerothTemplateApplication.getContext().getString(R.string.transportApiClientId);
                String clientSecret = ZerothTemplateApplication.getContext().getString(R.string.transportApiClientSecret);

                TransportApiClient tapiClient = new TransportApiClient(new TransportApiClientSettings(clientId, clientSecret));

                double startLongitude = mStartLocationLiveData.getValue().getLongitude();
                double startLatitude = mStartLocationLiveData.getValue().getLatitude();
                double endLongitude = location.getLongitude();
                double endLatitude = location.getLatitude();


                // Let's restrict our Journey call to only some Modes.
                List<String> onlyModes = new ArrayList<>();
                onlyModes.add("ShareTaxi");
                onlyModes.add("Bus");
                onlyModes.add("Rail");
                // onlyModes.add("Ferry");
                // onlyModes.add("Coach");
                // onlyModes.add("Subway");
                // onlyModes.add("Rail");

                // Request only one Itinerary for now.
                int numItineraries = 1;

                JourneyBodyOptions journeyBodyOptions = new JourneyBodyOptions(
                        null,
                        null,
                        onlyModes,
                        null,
                        numItineraries,
                        null);

                TransportApiResult<Journey> journeyResult = tapiClient.postJourney(
                        journeyBodyOptions,
                        startLatitude,
                        startLongitude,
                        endLatitude,
                        endLongitude,
                        null);

                List<Itinerary> itineraries = journeyResult.data.getItineraries();

                mItinerariesLiveData.postValue(itineraries);
            });
        }catch(NullPointerException e1){
            //logging code
            Log.i(LOG_TAGsrc, "Null end location Error");
        }catch (Exception e){
            Log.i(LOG_TAGsrc, "Error");
        }

    }


    //cutommmmmmmmmmmmmmmmmm

    public void setStrtLocation(Location strtLocation){


        mStartLocationLiveData.postValue(strtLocation);
    }

    //cutommmmmmmmmmmmmmmmmm


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
