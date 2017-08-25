# Android SDK


### Requirements

- SDK 16+
- Internet permission

For voice:

- require the following permissions (and features) at install or runtime:
    
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
```