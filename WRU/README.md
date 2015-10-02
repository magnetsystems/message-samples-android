#WRU - Where are you?

## Overview
The WRU sample application is a slightly different take on messaging.  From the start, WRU was designed as a simple way for a group of people (friends, family, etc.) to quick create an adhoc location sharing group.  Example use cases:

* a multiple family trip to Disneyland 
* a group drive to Napa
* know where your kids are when they need to be picked up

Here's how it works:

  1.  Start the app.
  2.  Choose a display name.
  3.  `Create` a group with a passphrase
  4.  Share the generated `group key` and the `passphrase` with other friends who can then `Join` with these pieces of information.

Once part of the group, all members of the group will begin sending and receiving location updates to and from each other.  Anyone can leave the group at any time and join again later. 

## Technical Notes
The app highlights several Magnet Message features:

1. A public MMXChannel used by members of a group to publish their locations.
2. A public MMXChannel used by members of the group to chat with the group.
3. Direct signaling messages to multiple recipients that allow GCM wakeup events to trigger location updates. 
4. Dynamically provisioned user accounts to allow for easy onboarding.
5. Dynamically provisioned channels for easy setup
6. Attach images using the provided MediaUtil to scale/resample an image to the appropriate size that can be embedded in a message.

##Start Using WRU
1.  Create a new app in the Magnet sandbox (http://sandbox.magnet.com)
2.  Get a Google Maps API key and place it into the `google_maps_key` element of the `app/src/debug/res/values/google_maps_api.xml` file.  https://developers.google.com/maps/documentation/android-api/
3.  OPTIONAL: For the "Request Updates" wakeup functionality.  This enhances the app greatly.
  1. Get a GCM configuration from Google (https://developers.google.com/cloud-messaging/android/client)
  2. Input the senderId and API key into the the Magnet sandbox app's settings screen
  3.  From this same settings screen, download the properties file and replace it in `app/src/main/res/raw/wru.properties`.
4.  Compile and install the app.  

## Additional Notes
This application depends on Android SDK API level 19 (4.4.2) and requires the installation of the Google Play services and Google Repository module using Android SDK Manager.

Extras-->Google Play services

Extras-->Google Repository

This app depends on Google Location Services for location updates.  It will not work if these services are disabled on the Android device.

