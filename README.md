# SDKTemplateApp
Android App to help developers query the Journey Planner and use the relevant response models.

The official Template Android Application for the [WhereIsMyTransport](https://www.whereismytransport.com) API.

Access to the WhereIsMyTransport Platform for public transport data is free, up to a certain number of calls.

For more information and to get credentials, just visit the [developer portal](https://developer.whereismytransport.com).

To integrate your own Android application with our API check out [TransportAPISdk.Java](https://github.com/WhereIsMyTransport/TransportApiSdk.Java).

## Get Started

Clone or download this project and open it in Android Studio.

Log onto the developer portal and get your client key and secret.

Setup your credentials in transportapisdk.xml in values/transportapisdk.xml

```xml
<resources>

    <!-- Transport API Client ID, replace with your own. -->
    <string name="transportApiClientId" translatable="false"><!-- YOUR API CLIENT ID HERE --></string>

    <!-- Transport API Client Secret, replace with your own. -->
    <string name="transportApiClientSecret" translatable="false"><!-- YOUR API CLIENT SECRET HERE --></string>

</resources>
```

## Features

The template application uses our [Java SDK](https://github.com/WhereIsMyTransport/TransportApiSdk.Java/blob/master/README.md), so it has access to the same endpoints.

# Journey Planner
The template app takes journey planner response model and displays it in an activity.
This demonstrates how you can use the WhereIsMyTransport SDK with the [Mapbox Maps SDK for Android](https://www.mapbox.com/help/first-steps-android-sdk/).






