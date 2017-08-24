# Android SDK


This SDK wraps and simplified the various features available over the Africa's Talking API. Developing an app that relies on the Africa's Talking API is much faster and simple with this SDK.

### Features

- SMS

    Send SMS from your Android app.

- Airtime

    Send airtime from your Android app.

- Payment

    Include a payment option in your Android app

- Voice

    Generate and receive VOIP calls in your Android app.

### Requirements

-  Android API: 16+

#### Permissions

1. For all:

- require the following permissions (and features) at install or runtime:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

2. For voice:

- require the following permissions (and features) at install or runtime:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
```

### Libraries used

- [PJSIP](http://pjsip.org/)
- [GRPC](https://grpc.io/)
- [Retrofit](http://square.github.io/retrofit/)

*TODO*

- [ ] Documentation
- [x] Server
- [x] Client

