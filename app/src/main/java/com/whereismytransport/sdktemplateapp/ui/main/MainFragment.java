package com.whereismytransport.sdktemplateapp.ui.main;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import transportapisdk.AgencyQueryOptions;
import transportapisdk.TransportApiClient;
import transportapisdk.TransportApiClientSettings;
import transportapisdk.TransportApiResult;
import transportapisdk.models.Agency;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mapbox.mapboxsdk.maps.MapView;
import com.whereismytransport.sdktemplateapp.R;

public class MainFragment extends Fragment {
    private static final String LOG_TAG = "MainFragment";

    private static final int PERMISSIONS_REQUEST_CODE = 1;

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

        // This ViewModel is scoped to this Fragment's parent Activity.
        mViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);

//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Log.i(LOG_TAG, "starting query...");
//
//                String clientId = getString(R.string.transportApiClientId);
//                String clientSecret = getString(R.string.transportApiClientSecret);
//
//                TransportApiClient defaultClient = new TransportApiClient(new TransportApiClientSettings(clientId, clientSecret));
//
//                TransportApiResult<List<Agency>> agencies = defaultClient.getAgencies(AgencyQueryOptions.defaultQueryOptions());
//
//                Log.i(LOG_TAG, "done");
//
//                Log.i(LOG_TAG, "agencies.data.size(): " + agencies.data.size());
//            }
//        });
//        thread.start();
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

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_CODE);
        } else {
            mViewModel.getLocation(getContext()).observe(this, new Observer<Location>() {
                @Override
                public void onChanged(Location location) {
                    Log.i(LOG_TAG, "Location update!");
                    // TODO: use the location update.
                }
            });
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permissions[0])) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }
            }
        }
    }
}
