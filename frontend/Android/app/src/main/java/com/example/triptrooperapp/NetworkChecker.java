package com.example.triptrooperapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;

// do not make the methods in this class static
// since every class needs to pass its context to it.

public class NetworkChecker {

    private volatile boolean isConnected = false;
    private final ConnectivityManager connectivityManager;
    private final ConnectivityManager.NetworkCallback networkCallback;

    public NetworkChecker(Context context) {
        connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Initialize the isConnected variable with the current state
        updateCurrentConnectionState();

        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                isConnected = true;
            }

            @Override
            public void onLost(Network network) {
                isConnected = false;
            }

            @Override
            public void onCapabilitiesChanged(Network network,
                                              NetworkCapabilities networkCapabilities) {
                isConnected =
                        networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
            }
        };

        // Register the callback to start listening for updates
        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();

        connectivityManager.registerNetworkCallback(networkRequest,
                networkCallback);
    }

    private void updateCurrentConnectionState() {
        if (connectivityManager != null) {
            Network currentNetwork = connectivityManager.getActiveNetwork();
            if (currentNetwork != null) {
                NetworkCapabilities caps =
                        connectivityManager.getNetworkCapabilities(currentNetwork);
                isConnected =
                        caps != null && caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
            }
        }
    }

    public boolean haveNetworkConnection() {
        return isConnected;
    }

    public void unregisterNetworkCallback() {
        connectivityManager.unregisterNetworkCallback(networkCallback);
    }
}
