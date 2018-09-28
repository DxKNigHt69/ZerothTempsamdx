# SDKTemplateApp

![WIMTLogo](https://www.whereismytransport.com/wp-content/themes/wpwimt/assets/images/logos/logo-black.svg)

###Thanks for checking out our Github profile. 
[WhereIsMyTransport](https://www.whereismytransport.com) is all about public transport data. 
We host our data on an API, and we've put together some resources on our Github to help people use the API.

This Android app is a learning tool to help developers understand how to query the WhereIsMyTransport Journey Planner and use the relevant information in conjunction with the [Mapbox Maps SDK for Android](https://www.mapbox.com/help/first-steps-android-sdk/). 
This app only uses one of many endpoints available on the WhereIsMyTransport API. It should be easy enough to extend this application to get the full benefit of the API.


For more information on our API, and to get an access token just visit the [developer portal](https://developer.whereismytransport.com). And click on **Get Started**.

To integrate your own Android application with our API check out [TransportAPISdk.Java](https://github.com/WhereIsMyTransport/TransportApiSdk.Java).

## Get going

* Clone or download this project and open it in Android Studio.
* Log onto the developer portal and get your client key and secret.
* Setup your credentials for the WIMT API in /app/src/res/values/transportapisdk.xml

```xml
<resources>

    <!-- Transport API Client ID, replace with your own. -->
    <string name="transportApiClientId" translatable="false"><!-- YOUR API CLIENT ID HERE --></string>

    <!-- Transport API Client Secret, replace with your own. -->
    <string name="transportApiClientSecret" translatable="false"><!-- YOUR API CLIENT SECRET HERE --></string>

</resources>
```

* Setup your Mapbox token in /app/src/res/values/mapboxsdk.xml

```xml
<resources>

    <!-- MapBox SDK Access Token, replace with your own. -->
    <string name="mapBoxAccessToken" translatable="false"><!-- YOUR ACCESS TOKEN HERE --></string>

</resources>
```


# Features

## Java SDK
The template application uses our [Java SDK](https://github.com/WhereIsMyTransport/TransportApiSdk.Java/blob/master/README.md).The SDK can be used to access all features of the API, but here it only uses the Journey Planner.

## MapboxHelper
* Covers alot of the boiler-plate code that has to be written to display a the Journey Planner response model on a Mapbox maps.
* Provides easy to understand examples of how you can extend this to display other WhereIsMyTransport data on Mapbox.

## MapMarkers
* Provides VectorDrawables for use as waypoint map-markers and map pins to mark out the beginning and end of journeys, intermediate stops and lines.

## Simple and easy to understand Main Activity
* An easy way to understand how to piece together a Main Activity with a Main Fragment and a MainViewModel to 

## Get in touch
Get in touch with us on Support@whereismytransport.com for feature suggestions. 

# License
SDKTemplateApp is available under the MIT license.
We encourage you to use this code, change it, and keep it open-source.


