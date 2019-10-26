package com.example.tal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import android.app.LoaderManager;
import android.content.Loader;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LoaderManager.LoaderCallbacks<List<Service>>{

    private GoogleMap mMap;
    public static String query = "";
    public static String baseLink = "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=41.43206,-81.38992|-33.86748,151.20699&destinations=New+York+City,NY&key=AIzaSyA8CApQee8fXVHI3FLEP6IE8bK_B6_oIpY";
    public static String apiKey = "https://api.birdapp.com/bird/nearby?latitude=37.77184&longitude=-122.40910&radius=100";
    public static String logTag = "Bird";

    public ArrayList<Service> services = new ArrayList<Service>();
    ServiceAdapter adapter;
    private boolean firstQuery = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ListView listView=(ListView) findViewById(R.id.list);
        adapter = new ServiceAdapter(this, services);
        listView.setAdapter(adapter);
        getLoaderManager().initLoader(0,null, MapsActivity.this);

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
        LatLng sydney = new LatLng(-34, 151);
        LatLng somewhere = new LatLng(-32, 153);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        ArrayList<Location> locs = new ArrayList<Location>();
        Location sydny = new Location(-34, 151);
        Location somwhere = new Location(-32,153);
        locs.add(sydny);
        locs.add(somwhere);
        locs.add(new Location(30, 149));
        drawLines(locs);
    }

    public void drawLines(ArrayList<Location> route){
        PolylineOptions options = new PolylineOptions().clickable(true);
        for(int i = 0; i< route.size(); i++){
            Location loc = route.get(i);
            options.add(new LatLng(loc.x, loc.y));
        }
        Polyline polyLine = mMap.addPolyline(options);

    }

    @Override
    public Loader<List<Service>> onCreateLoader(int id, Bundle args){
        return new ServeAsyncTaskLoader(this, query,baseLink,apiKey,logTag);
    }

    @Override
    public void onLoadFinished(Loader< List< Service > > loader, List< Service > services) {

    }

    @Override
    public void onLoaderReset(Loader<List<Service>> loader) {
        adapter.clear();
    }

    // @Override
    public void onLoadFinished(androidx.loader.content.Loader<List<Service>> loader, List<Service> data) {
        getLoaderManager().destroyLoader(0);
        if(data!=null){
            if(!adapter.isEmpty()){
                adapter.clear();
            }

            adapter.addAll(data);
        }
        /*if(data.size()==0&&!firstQuery){
            TextView emptyView= (TextView) findViewById(R.id.emptyView);
            emptyView.setVisibility(View.VISIBLE);
        }*/

    }

}
