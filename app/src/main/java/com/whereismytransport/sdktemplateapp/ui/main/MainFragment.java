package com.whereismytransport.sdktemplateapp.ui.main;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.whereismytransport.sdktemplateapp.BitmapHelper;
import com.whereismytransport.sdktemplateapp.R;

public class MainFragment extends Fragment {
    private static final String LOG_TAG = "MainFragment";

    private static final int PERMISSIONS_REQUEST_CODE = 1;

    private MainViewModel mViewModel;

    private MapView mMapView;
    private MapboxMap mMap;
    private FloatingActionButton mCenterLocationButton;

    private Marker mOriginMarker;
    private Marker mDestinationMarker;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);

        mMapView = view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mCenterLocationButton = view.findViewById(R.id.centerLocationButton);
        mCenterLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOriginMarker != null) {
                    CameraPosition.Builder camPositionBuilder = new CameraPosition.Builder();
                    camPositionBuilder.target(mOriginMarker.getPosition());
                    camPositionBuilder.zoom(11.0);

                    mMap.setCameraPosition(camPositionBuilder.build());
                }
            }
        });

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

            mMapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(MapboxMap mapboxMap) {
                    mMap = mapboxMap;

                    setupViewModelRelations();
                }
            });
        }
    }

    private void setupViewModelRelations() {
        mViewModel.getLocation(getContext()).observe(this, new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                Log.i(LOG_TAG, "Location update!");
            }
        });

        mViewModel.getStartLocation().observe(this, new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                if (mOriginMarker == null) {
                    final int markerWidth = getContext().getResources().getDimensionPixelSize(R.dimen.waypoint_end_map_marker_width);
                    final int markerHeight = getContext().getResources().getDimensionPixelSize(R.dimen.waypoint_end_map_marker_height);

                    Icon icon = BitmapHelper.getVectorAsMapBoxIcon(getContext(), R.drawable.ic_map_pin_a, markerWidth, markerHeight);
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.setIcon(icon);

                    markerOptions.setPosition(new LatLng(location));
                    mOriginMarker = mMap.addMarker(markerOptions);
                } else {
                    mOriginMarker.setPosition(new LatLng(location));
                }

                CameraPosition.Builder camPositionBuilder = new CameraPosition.Builder();
                camPositionBuilder.target(mOriginMarker.getPosition());
                camPositionBuilder.zoom(11.0);

                mMap.setCameraPosition(camPositionBuilder.build());
            }
        });

        mViewModel.getEndLocation().observe(this, new Observer<LatLng>() {
            @Override
            public void onChanged(LatLng location) {
                if (mDestinationMarker == null) {
                    final int markerWidth = getContext().getResources().getDimensionPixelSize(R.dimen.waypoint_end_map_marker_width);
                    final int markerHeight = getContext().getResources().getDimensionPixelSize(R.dimen.waypoint_end_map_marker_height);

                    Icon icon = BitmapHelper.getVectorAsMapBoxIcon(getContext(), R.drawable.ic_map_pin_b, markerWidth, markerHeight);
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.setIcon(icon);
                    markerOptions.setPosition(location);
                    mDestinationMarker = mMap.addMarker(markerOptions);
                } else {
                    mDestinationMarker.setPosition(location);
                }
            }
        });

        mMap.addOnMapLongClickListener((@NonNull LatLng point) -> {
            mViewModel.setEndLocation(point);
        });
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
