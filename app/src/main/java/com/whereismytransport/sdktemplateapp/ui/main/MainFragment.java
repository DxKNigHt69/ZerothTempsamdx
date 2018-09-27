package com.whereismytransport.sdktemplateapp.ui.main;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import transportapisdk.AgencyQueryOptions;
import transportapisdk.TransportApiClient;
import transportapisdk.TransportApiClientSettings;
import transportapisdk.TransportApiResult;
import transportapisdk.models.Agency;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mapbox.mapboxsdk.maps.MapView;
import com.whereismytransport.sdktemplateapp.R;

import java.util.List;

public class MainFragment extends Fragment {
    private static final String LOG_TAG = "MainFragment";

    private MainViewModel mViewModel;

    private MapView mMapView;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);

        mMapView = (MapView) view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        // TODO: Use the ViewModel

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(LOG_TAG, "starting query...");

                String clientId = getString(R.string.transportApiClientId);
                String clientSecret = getString(R.string.transportApiClientSecret);

                TransportApiClient defaultClient = new TransportApiClient(new TransportApiClientSettings(clientId, clientSecret));

                TransportApiResult<List<Agency>> agencies = defaultClient.getAgencies(AgencyQueryOptions.defaultQueryOptions());

                Log.i(LOG_TAG, "done");

                Log.i(LOG_TAG, "agencies.data.size(): " + agencies.data.size());
            }
        });
        thread.start();
    }

    @Override
    public void onStart() {
        super.onStart();

        mMapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        mMapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();

        mMapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        mMapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mMapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mMapView.onSaveInstanceState(outState);
    }
}
