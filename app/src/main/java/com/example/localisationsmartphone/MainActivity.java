package com.example.localisationsmartphone;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final String TAG = "LOCALISATION";

    private double latitude;
    private double longitude;
    private RequestQueue requestQueue;
    private TextView tvInfo;
    private LocationManager locationManager;

    private String insertUrl = "http://192.168.39.2:8080/localisation/createPosition.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvInfo = findViewById(R.id.tvInfo);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Vérifier et demander les permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Demander la permission — le démarrage GPS se fera dans onRequestPermissionsResult
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    }, PERMISSION_REQUEST_CODE);
        } else {
            // Permission déjà accordée → démarrer directement
            startLocationUpdates();
        }
    }

    // ← Méthode MANQUANTE dans votre code original
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permission accordée → démarrage GPS");
                startLocationUpdates();
            } else {
                Toast.makeText(this,
                        "Permission GPS refusée, l'application ne peut pas fonctionner",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) return;

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();

                Log.d(TAG, "Position reçue : " + latitude + ", " + longitude);

                String msg = "Latitude : " + latitude
                        + "\nLongitude : " + longitude
                        + "\nPrécision : " + location.getAccuracy() + " m";

                tvInfo.setText(msg);
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();

                addPosition(latitude, longitude);
            }

            @Override public void onStatusChanged(String p, int s, Bundle e) {}
            @Override public void onProviderEnabled(String p) {}
            @Override public void onProviderDisabled(String p) {
                Toast.makeText(getApplicationContext(),
                        "GPS désactivé !", Toast.LENGTH_SHORT).show();
            }
        };

        // GPS_PROVIDER avec 0,0 pour Genymotion (pas de délai ni distance minimum)
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0,    // ← 0ms : mise à jour immédiate
                    0,    // ← 0m : pas de distance minimum
                    locationListener
            );
            Log.d(TAG, "GPS_PROVIDER enregistré");

            // Récupérer la dernière position connue immédiatement
            Location lastKnown = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnown != null) {
                Log.d(TAG, "Dernière position connue utilisée");
                locationListener.onLocationChanged(lastKnown);
            }
        }

        // Fallback réseau (fonctionne aussi sur Genymotion)
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    0, 0, locationListener
            );
            Log.d(TAG, "NETWORK_PROVIDER enregistré");
        }
    }

    private void addPosition(final double lat, final double lon) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                insertUrl,
                response -> {
                    Log.d(TAG, "Réponse serveur : " + response);
                    Toast.makeText(getApplicationContext(),
                            "Envoyé : " + response, Toast.LENGTH_SHORT).show();
                },
                error -> {
                    // Log détaillé pour déboguer
                    Log.e(TAG, "Erreur Volley : " + error.toString());
                    if (error.networkResponse != null) {
                        Log.e(TAG, "Code HTTP : " + error.networkResponse.statusCode);
                    }
                    Toast.makeText(getApplicationContext(),
                            "Erreur réseau : " + error.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

                params.put("latitude", String.valueOf(lat));
                params.put("longitude", String.valueOf(lon));
                params.put("date_position", sdf.format(new Date()));

                String androidId = android.provider.Settings.Secure.getString(
                        getContentResolver(),
                        android.provider.Settings.Secure.ANDROID_ID
                );
                params.put("imei", androidId);

                return params;
            }
        };

        requestQueue.add(request);
    }
}