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

import com.whereismytransport.sdktemplateapp.R;

import java.util.List;

public class MainFragment extends Fragment {
    private static final String LOG_TAG = "MainFragment";

    private MainViewModel mViewModel;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
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

                String clientId = getString(R.string.clientId);
                String clientSecret = getString(R.string.clientSecret);

                TransportApiClient defaultClient = new TransportApiClient(new TransportApiClientSettings(clientId, clientSecret));

                TransportApiResult<List<Agency>> agencies = defaultClient.getAgencies(AgencyQueryOptions.defaultQueryOptions());

                Log.i(LOG_TAG, "done");

                Log.i(LOG_TAG, "agencies.data.size(): " + agencies.data.size());
            }
        });
        thread.start();
    }

}
