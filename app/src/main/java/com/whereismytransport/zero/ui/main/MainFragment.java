package com.whereismytransport.zero.ui.main;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import transportapisdk.models.Itinerary;

import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.whereismytransport.zero.BitmapHelper;
import com.whereismytransport.zero.MapboxHelper;
import com.whereismytransport.zero.R;

import java.util.List;

import static android.content.RestrictionsManager.RESULT_ERROR;
import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

public class MainFragment extends Fragment {
    private static final String LOG_TAG = "MainFragment";
    private static final String LOG_TAGsrc = "sourceLoc";

    private static final int PERMISSIONS_REQUEST_CODE = 1;

    private MainViewModel mViewModel;

    private MapView mMapView;
    private MapboxMap mMap;
    //buttons
    private FloatingActionButton mCenterLocationButton;
    private Button clearbutton;
    private Button searchbutton1;
    private EditText sourceloctn;
    private EditText destnloctn;
    private static final int REQUEST_CODE_src_AUTOCOMPLETE = 1;
    private static final int REQUEST_CODE_destn_AUTOCOMPLETE = 2;

    private MarkerOptions mOriginMarkerOptions;
    private Marker mOriginMarker;

    private MarkerOptions mDestinationMarkerOptions;
    private Marker mDestinationMarker;
    private String symbolIconId = "symbolIconId";
    private String geojsonSourceLayerId = "geojsonSourceLayerId";

    private Location location;


    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);

        // Initialise the Mapbox instance.
        Mapbox.getInstance(getContext(), getString(R.string.mapBoxAccessToken));

        mMapView = view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);


        //gps button
        mCenterLocationButton =  view.findViewById(R.id.centerLocationButton);
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


        //Clear All marker
        clearbutton=view.findViewById(R.id.clear);
        clearbutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                Toast bread = Toast.makeText(getApplicationContext(), "clear all", Toast.LENGTH_LONG);
                bread.show();
                Log.i(LOG_TAG, "Location Cleared all!");
                mMap.clear();
            }
        });

        //Search the location entered
//        //Custom
        sourceloctn =(EditText) view.findViewById(R.id.source_loc);
        sourceloctn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                try{
                    Intent intent = new PlaceAutocomplete.IntentBuilder()
                            .accessToken(Mapbox.getAccessToken())
                            .placeOptions(PlaceOptions.builder()
                                    .backgroundColor(Color.parseColor("#EEEEEE"))
                                    .limit(10)
                                    .build(PlaceOptions.MODE_CARDS))
                            .build(getActivity());
                    startActivityForResult(intent, REQUEST_CODE_src_AUTOCOMPLETE);
                }catch (NullPointerException ne){
                    //logging code
                    Log.i(LOG_TAGsrc, "Null end location Error");
                }catch (Exception e){
                    Log.i(LOG_TAGsrc, "Error");
                }

                //initSearchFab();

            }


        });

        destnloctn =(EditText) view.findViewById(R.id.destntion_loc);
        destnloctn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

             try{
                 Intent intent = new PlaceAutocomplete.IntentBuilder()
                         .accessToken(Mapbox.getAccessToken())
                         .placeOptions(PlaceOptions.builder()
                                 .backgroundColor(Color.parseColor("#EEEEEE"))
                                 .limit(10)
                                 .build(PlaceOptions.MODE_CARDS))
                         .build(getActivity());
                 startActivityForResult(intent, REQUEST_CODE_destn_AUTOCOMPLETE);
             }catch (NullPointerException ne){
                 //logging code
                 Log.i(LOG_TAGsrc, "Null end location Error");
             }catch (Exception e){
                 Log.i(LOG_TAGsrc, "Error");
             }


                //initSearchFab();

            }


        });
//
//        //setupViewModelConnections
//        Button locate=(Button) view.findViewById(R.id.locate);
//        locate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//
//                setupViewModelConnections();
//
//                //initSearchFab();
//
//            }
//
//
//        });






        //custom


        return view;

    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // This ViewModel is scoped to this Fragment's parent Activity.
        mViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
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

        // Check permission to get location.
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_CODE);
        } else {
            mMapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(MapboxMap mapboxMap) {
                    mMap = mapboxMap;

                    setupViewModelConnections();
                }
            });
        }
    }



    //custommmmmmmmmmmmmmmmmmmmmm

    private void initSearchFab() {
        // Add the symbol layer icon to map for future use
        Bitmap icon = BitmapFactory.decodeResource(
                MainFragment.this.getResources(), R.drawable.ic_person_pin_circle_black_24dp);
        mMap.addImage(symbolIconId, icon);

        // Create an empty GeoJSON source using the empty feature collection
        setUpSource();

        // Set up a new symbol layer for displaying the searched location's feature coordinates
        setupLayer();
    }

    //custommmmmmmmmmmmmmmmmmmmmmmm

    private void setUpSource() {
        GeoJsonSource geoJsonSource = new GeoJsonSource(geojsonSourceLayerId);
        mMap.addSource(geoJsonSource);
    }

    private void setupLayer() {
        SymbolLayer selectedLocationSymbolLayer = new SymbolLayer("SYMBOL_LAYER_ID", geojsonSourceLayerId);
        selectedLocationSymbolLayer.withProperties(PropertyFactory.iconImage(symbolIconId));
        mMap.addLayer(selectedLocationSymbolLayer);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_src_AUTOCOMPLETE) {
            if (resultCode == Activity.RESULT_OK) {


                sourceloctn.setText(PlaceAutocomplete.getPlace(data).text());
                sourceloctn.setTextColor(Color.BLACK);

                mMap.clear();
                //Retrieve selected location's CarmenFeature
                CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);

                LatLng srcLctn=new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
                        ((Point) selectedCarmenFeature.geometry()).longitude());


                // Create a new FeatureCollection and add a new Feature to it using selectedCarmenFeature above
                FeatureCollection featureCollection = FeatureCollection.fromFeatures(
                        new Feature[]{Feature.fromJson(selectedCarmenFeature.toJson())});

                // Retrieve and update the source designated for showing a selected location's symbol layer icon
                GeoJsonSource source = mMap.getSourceAs(geojsonSourceLayerId);
                if (source != null) {
                    source.setGeoJson(featureCollection);
                }
                if (mOriginMarker == null) {
                    final int markerWidth = getContext().getResources().getDimensionPixelSize(R.dimen.waypoint_end_map_marker_width);
                    final int markerHeight = getContext().getResources().getDimensionPixelSize(R.dimen.waypoint_end_map_marker_height);

                    Icon icon = BitmapHelper.getVectorAsMapBoxIcon(getContext(), R.drawable.ic_map_pin_a, markerWidth, markerHeight);
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.setIcon(icon);

                    markerOptions.setPosition(srcLctn);
                    mOriginMarker = mMap.addMarker(markerOptions);
                    mOriginMarkerOptions = markerOptions;
                } else {

                    mOriginMarker = mMap.addMarker(mOriginMarkerOptions);
                    mOriginMarker.setPosition(srcLctn);
                    mOriginMarkerOptions.setPosition(srcLctn);

                }

                CameraPosition.Builder camPositionBuilder = new CameraPosition.Builder();
                camPositionBuilder.target(mOriginMarker.getPosition());
                camPositionBuilder.zoom(11.0);


                mMap.setCameraPosition(camPositionBuilder.build());

                //start loctn setup

                Location srcLctnMarker=new Location("startPoint");
                srcLctnMarker.setLatitude(srcLctn.getLatitude());
                srcLctnMarker.setLongitude(srcLctn.getLongitude());
                mViewModel.setStrtLocation(srcLctnMarker);

                // Retrieve selected location's CarmenFeature
//            CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);
//
//            // Create a new FeatureCollection and add a new Feature to it using selectedCarmenFeature above
//            FeatureCollection featureCollection = FeatureCollection.fromFeatures(
//                    new Feature[]{Feature.fromJson(selectedCarmenFeature.toJson())});
//
//            // Retrieve and update the source designated for showing a selected location's symbol layer icon
//            GeoJsonSource source = mMap.getSourceAs(geojsonSourceLayerId);
//            if (source != null) {
//                source.setGeoJson(featureCollection);
//            }
//
//            // Move map camera to the selected location
//            CameraPosition newCameraPosition = new CameraPosition.Builder()
//                    .target(new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
//                            ((Point) selectedCarmenFeature.geometry()).longitude()))
//                    .zoom(14)
//                    .build();
//            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(newCameraPosition), 4000);
            }
            else if(resultCode == RESULT_ERROR){
                sourceloctn.requestFocus();
                // TODO: Handle the error.
                Log.i(LOG_TAGsrc, "Error");
            }

        }else if(requestCode == REQUEST_CODE_destn_AUTOCOMPLETE){
            if (resultCode == Activity.RESULT_OK) {


                destnloctn.setText(PlaceAutocomplete.getPlace(data).text());
                destnloctn.setTextColor(Color.BLACK);

                //Retrieve selected location's CarmenFeature
                CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);
                LatLng destnLctn=new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
                        ((Point) selectedCarmenFeature.geometry()).longitude());
                mViewModel.setEndLocation(destnLctn);

//                mMap.clear();
//                //Retrieve selected location's CarmenFeature
//                CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);
//
//                LatLng srcLctn=new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
//                        ((Point) selectedCarmenFeature.geometry()).longitude());
//
//                // Create a new FeatureCollection and add a new Feature to it using selectedCarmenFeature above
//                FeatureCollection featureCollection = FeatureCollection.fromFeatures(
//                        new Feature[]{Feature.fromJson(selectedCarmenFeature.toJson())});
//
//                // Retrieve and update the source designated for showing a selected location's symbol layer icon
//                GeoJsonSource source = mMap.getSourceAs(geojsonSourceLayerId);
//                if (source != null) {
//                    source.setGeoJson(featureCollection);
//                }
//
//                if (mDestinationMarker == null) {
//                    final int markerWidth = getContext().getResources().getDimensionPixelSize(R.dimen.waypoint_end_map_marker_width);
//                    final int markerHeight = getContext().getResources().getDimensionPixelSize(R.dimen.waypoint_end_map_marker_height);
//
//                    Icon icon = BitmapHelper.getVectorAsMapBoxIcon(getContext(), R.drawable.ic_map_pin_a, markerWidth, markerHeight);
//                    MarkerOptions markerOptions = new MarkerOptions();
//                    markerOptions.setIcon(icon);
//
//                    markerOptions.setPosition(srcLctn);
//                    mDestinationMarker = mMap.addMarker(markerOptions);
//                    mDestinationMarkerOptions = markerOptions;
//                } else {
//
//                    mDestinationMarker = mMap.addMarker(mDestinationMarkerOptions);
//                    mDestinationMarker.setPosition(srcLctn);
//                    mDestinationMarkerOptions.setPosition(srcLctn);
//
//                }
//
//                CameraPosition.Builder camPositionBuilder = new CameraPosition.Builder();
//                camPositionBuilder.target(mDestinationMarker.getPosition());
//                camPositionBuilder.zoom(11.0);
//
//                mMap.setCameraPosition(camPositionBuilder.build());
            }
            else if(resultCode == RESULT_ERROR){

                // TODO: Handle the error.
                Log.i(LOG_TAGsrc, "Error");

            }
        }
    }

    ///custommmmmmmmmmmmmmmmmmmmmm



    // Observe LiveData from the MainViewModel.
    private void setupViewModelConnections() {
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
                    mOriginMarkerOptions = markerOptions;
                } else {
                    mOriginMarker.setPosition(new LatLng(location));
                    mOriginMarkerOptions.setPosition(new LatLng(location));
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
                    mDestinationMarkerOptions = markerOptions;
                } else {
                    mDestinationMarker.setPosition(location);
                    mDestinationMarkerOptions.setPosition(location);
                }
            }
        });

        mMap.addOnMapLongClickListener((@NonNull LatLng point) -> {
            try {
                Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(50L);
            } catch (SecurityException e) {
                // Gotta have permission ¯\_(ツ)_/¯
            }

            mViewModel.setEndLocation(point);
        });

        mViewModel.getItineraries().observe(this, new Observer<List<Itinerary>>() {

            @Override
            public void onChanged(List<Itinerary> itineraries) {
                mMap.clear();
                MapboxHelper.drawItineraryOnMap(getContext(), mMap, itineraries.get(0));
                mMap.addMarker(mOriginMarkerOptions);
                mMap.addMarker(mDestinationMarkerOptions);
            }
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
    public void onDestroyView() {
        super.onDestroyView();

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
