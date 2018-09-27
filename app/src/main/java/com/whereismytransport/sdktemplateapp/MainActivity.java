package com.whereismytransport.sdktemplateapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import com.mapbox.mapboxsdk.Mapbox;
import com.whereismytransport.sdktemplateapp.ui.main.MainFragment;
import com.whereismytransport.sdktemplateapp.ui.main.MainViewModel;

public class MainActivity extends AppCompatActivity {

    private MainViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        // This ViewModel is scoped to child Fragments of this Activity.
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        // Initialise the Mapbox instance.
        Mapbox.getInstance(this, getString(R.string.mapBoxAccessToken));

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow();
        }
    }
}
