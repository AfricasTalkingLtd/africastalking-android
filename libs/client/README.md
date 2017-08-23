# Android SDK


### Requirements

- SDK 16+
- Internet permission

For voice:

- add broadcast receiver for intent `com.africastalking.voice.INCOMING_CALL`
- require the following permissions (and features) at install or runtime:
    
```xml
<uses-permission android:name="android.permission.USE_SIP" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />

<uses-feature android:name="android.hardware.sip.voip" android:required="true" />
<uses-feature android:name="android.hardware.microphone" android:required="true" />
```