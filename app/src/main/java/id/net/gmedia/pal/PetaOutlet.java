package id.net.gmedia.pal;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import id.net.gmedia.pal.Util.Constant;
import id.net.gmedia.pal.Util.GoogleLocationManager;
import id.net.gmedia.pal.Util.Haversine;

public class PetaOutlet extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng lokasi_outlet;
    private LatLng lokasi_user;
    private Marker user_marker;
    private Marker outlet_marker;
    private GoogleLocationManager manager;

    private double distance = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peta_outlet);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        Gson gson = new Gson();
        lokasi_outlet = gson.fromJson(getIntent().getStringExtra(Constant.EXTRA_LOKASI_OUTLET), LatLng.class);
        if(getIntent().hasExtra(Constant.EXTRA_LOKASI_USER)){
            lokasi_user = gson.fromJson(getIntent().getStringExtra(Constant.EXTRA_LOKASI_USER), LatLng.class);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        manager = new GoogleLocationManager(this, new GoogleLocationManager.LocationUpdateListener() {
            @Override
            public void onChange(Location location) {
                lokasi_user = new LatLng(location.getLatitude(), location.getLongitude());
                if(user_marker == null){
                    BitmapDescriptor icon_user = BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(BitmapFactory.
                            decodeResource(getResources(), R.drawable.ic_user), (int)getResources().getDimension(R.dimen.gmap_marker_width),
                            (int)getResources().getDimension(R.dimen.gmap_marker_height), false));
                    user_marker = mMap.addMarker(new MarkerOptions().position(lokasi_user).title("Posisi Anda").icon(icon_user));

                    int width = getResources().getDisplayMetrics().widthPixels;
                    int height = getResources().getDisplayMetrics().heightPixels;
                    int padding = (int) (width * 0.1);

                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(outlet_marker.getPosition());
                    builder.include(user_marker.getPosition());
                    LatLngBounds bounds = builder.build();
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                    mMap.animateCamera(cu);
                }
                user_marker.setPosition(lokasi_user);
            }
        });
        manager.startLocationUpdates();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        manager.stopLocationUpdates();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        BitmapDescriptor icon_outlet = BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(BitmapFactory.
                decodeResource(getResources(), R.drawable.ic_outlet), (int)getResources().getDimension(R.dimen.gmap_marker_width),
                (int)getResources().getDimension(R.dimen.gmap_marker_height), false));
        outlet_marker = mMap.addMarker(new MarkerOptions().position(lokasi_outlet).title("Posisi Outlet").icon(icon_outlet));

        if(lokasi_user != null){
            BitmapDescriptor icon_user = BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(BitmapFactory.
                    decodeResource(getResources(), R.drawable.ic_user), (int)getResources().getDimension(R.dimen.gmap_marker_width),
                    (int)getResources().getDimension(R.dimen.gmap_marker_height), false));
            user_marker = mMap.addMarker(new MarkerOptions().position(lokasi_user).title("Posisi Anda").icon(icon_user));

            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;
            int padding = (int) (width * 0.1);

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(outlet_marker.getPosition());
            builder.include(user_marker.getPosition());
            LatLngBounds bounds = builder.build();
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
            mMap.animateCamera(cu);
        }
    }


}
