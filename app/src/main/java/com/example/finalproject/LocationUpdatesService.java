package com.example.finalproject;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LocationUpdatesService extends Service {

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    private static final String CHANNEL_ID = "LocationUpdatesServiceChannel";
    private static final int NOTIFICATION_ID = 12345; // Notification ID

    @Override
    public void onCreate() {
        super.onCreate();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000); // Set the desired interval for active location updates, in milliseconds.
        locationRequest.setFastestInterval(500); // Set the fastest rate for active location updates, in milliseconds.

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    String carnumber = getSharedPreferences("LocationUpdatesService", MODE_PRIVATE).getString("carnumber", "");
                    String phonenumber = getSharedPreferences("LocationUpdatesService", MODE_PRIVATE).getString("phonenumber", "");

                    Log.i("MyApp", "Latitude: " + latitude + ", Longitude: " + longitude + " , carnumber : " + carnumber + " , phonenumber : " + phonenumber);


                    ///여기에 넣은거임
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("carnumber", carnumber);
                        jsonObject.put("latitude", latitude);
                        jsonObject.put("longitude", longitude);
                        jsonObject.put("phonenumber", phonenumber);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        System.out.println(e.getMessage());
                    }

                    // creating request body
                    RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

                    // creating request
                    Request request = new Request.Builder()
//                            .url("http://10.0.2.2:7070/hihi")
//                            .url("http://127.0.0.1:7070/hihi")
//
//                            .url("http://192.168.35.39:7070/hihi")
//                            이게 집
//
//                            .url("http://218.153.162.95:7070/hihi")

//                            .url("http://221.148.138.87:7070/hihi")
                            //이게학원

//                            .url("http://192.168.0.67:9090/hihi")
                            .url("http://192.168.0.220:8081/getlocationfromandroid")
                            //이게학원
                            .post(requestBody)
                            .build();

                    // creating client
                    OkHttpClient client = new OkHttpClient();

                    // making the request
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            // handle request failure
                            Log.i("MyApp", "실패함" + e.getMessage());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            // handle successful response
                            Log.i("MyApp", "성공함" + call.toString());
                            Log.i("MyApp", "성공함" + response.toString());


                        }
                    });

                    //여기에 넣은거임









                    // ... The rest of your code for sending location updates ...
                }
            }
        };

        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        // Create the notification.
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Location Update Service")
                .setContentText("Running...")
                .setSmallIcon(R.mipmap.ic_launcher)  // TODO: replace with your own icon
                .setContentIntent(pendingIntent)
                .build();

        // Start foreground service.
        startForeground(NOTIFICATION_ID, notification);

        startLocationUpdates();
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                    locationCallback,
                    null /* Looper */);
        } else {
            // Log or handle the lack of required permission.
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String carnumber = intent.getStringExtra("carnumber");
        String phonenumber = intent.getStringExtra("phonenumber");
        getSharedPreferences("LocationUpdatesService", MODE_PRIVATE).edit().putString("carnumber", carnumber).putString("phonenumber",phonenumber).apply();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Location Update Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
