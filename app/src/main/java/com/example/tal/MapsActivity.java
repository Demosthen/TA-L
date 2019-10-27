package com.example.tal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import android.app.LoaderManager;
import android.content.Loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.transition.AutoTransition;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LoaderManager.LoaderCallbacks<List<Service>>{

    private GoogleMap mMap;
    public static String query = "";
    public static String baseLink = "https://data.lime.bike/api/partners/v1/gbfs/los_angeles/free_bike_status";
    public static String apiKey = "https://api.birdapp.com/bird/nearby?latitude=37.77184&longitude=-122.40910&radius=100";
    public static String LOG_TAG = "TEAMAVATARPLUSLARRY";

    public ArrayList<Service> services = new ArrayList<Service>();
    HashMap<String, Boolean> buttons = new HashMap<String, Boolean>();
    int iconHeight = 125;
    int iconWidth = 125;
    Transition transition = new AutoTransition();
    ServiceAdapter adapter;
    private boolean firstQuery = true;
    Place startPlace = null;
    Place destPlace = null;
    BitmapDescriptor smallBikeIcon;
    BitmapDescriptor smallScooterIcon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        // Initialize the Places SDK
        Places.initialize(getApplicationContext(), "AIzaSyA8CApQee8fXVHI3FLEP6IE8bK_B6_oIpY");
        // Create a new Places client instance
        PlacesClient placesClient = Places.createClient(this);
        mapFragment.getMapAsync(this);
        //initialize ListView for available services and attach adapter
        ListView listView=(ListView) findViewById(R.id.list);
        adapter = new ServiceAdapter(this, services);
        listView.setAdapter(adapter);
        //start loader
        getLoaderManager().initLoader(0,null, MapsActivity.this);

//      RequestQueue queue = Volley.newRequestQueue(this);
//      queue.add(Utils.makeVolleyQueueRequest("",baseLink,"",""));

        // Initialize the Destination AutocompleteSupportFragment.
        final AutocompleteSupportFragment destinationFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.dest_fragment);

        // Specify the types of place data to return.
        destinationFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        destinationFragment.setText("Destination");
        // Set up a PlaceSelectionListener to handle the response.
        destinationFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                /*TextView startText = findViewById(R.id.start_input);
                TextView destText = findViewById(R.id.dest_input);
                //For transitions
                ViewGroup sceneRoot = (ViewGroup) findViewById(R.id.transition_root);
                Scene start = Scene.getSceneForLayout(sceneRoot, R.layout.start, getApplicationContext());
                final Scene no_start = Scene.getSceneForLayout(sceneRoot, R.layout.no_start, getApplicationContext());

                if(destText.getText().length() == 0){
                    destPlace = place;
                    Log.i(LOG_TAG, destPlace.getName());
                    TransitionManager.go(start, transition);
                    Log.i(LOG_TAG, destPlace.getName());
                    ((TextView)findViewById(R.id.dest_input)).setText(destPlace.getName());
                }*/
                /*else{
                    startPlace = place;

                    startText.setText(startPlace.getName());
                }*/
                // Move camera
                mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title("Marker at Destination"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
                destinationFragment.setText(place.getName());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(LOG_TAG, "An error occurred: " + status);
            }
        });

        // Initialize the Destination AutocompleteSupportFragment.
        final AutocompleteSupportFragment startFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.start_fragment);

        // Specify the types of place data to return.
        startFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        startFragment.setText("Start");
        // Set up a PlaceSelectionListener to handle the response.
        startFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                /*TextView startText = findViewById(R.id.start_input);
                TextView destText = findViewById(R.id.dest_input);
                //For transitions
                ViewGroup sceneRoot = (ViewGroup) findViewById(R.id.transition_root);
                Scene start = Scene.getSceneForLayout(sceneRoot, R.layout.start, getApplicationContext());
                final Scene no_start = Scene.getSceneForLayout(sceneRoot, R.layout.no_start, getApplicationContext());

                if(destText.getText().length() == 0){
                    destPlace = place;
                    Log.i(LOG_TAG, destPlace.getName());
                    TransitionManager.go(start, transition);
                    Log.i(LOG_TAG, destPlace.getName());
                    ((TextView)findViewById(R.id.dest_input)).setText(destPlace.getName());
                }*/
                /*else{
                    startPlace = place;

                    startText.setText(startPlace.getName());
                }*/
                // Move camera
                mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title("Marker at Destination"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
                startFragment.setText(place.getName());

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(LOG_TAG, "An error occurred: " + status);
            }
        });

        ToggleButton temp = findViewById(R.id.BirdButton);
        buttons.put(temp.getText().toString(), false);
        temp = findViewById(R.id.FordButton);
        buttons.put(temp.getText().toString(), false);
        temp = findViewById(R.id.ZipcarButton);
        buttons.put(temp.getText().toString(), false);
        initializeIcons();
    }

    void initializeIcons(){
        Bitmap bike = BitmapFactory.decodeResource(getResources(), R.mipmap.bike);
        Bitmap scooter = BitmapFactory.decodeResource(getResources(), R.mipmap.scooter);
        Bitmap smallBike = Bitmap.createScaledBitmap(bike, iconWidth, iconHeight, false);
        Bitmap smallScooter = Bitmap.createScaledBitmap(scooter,iconWidth,iconHeight,false);
        smallBikeIcon = BitmapDescriptorFactory.fromBitmap(smallBike);
        smallScooterIcon = BitmapDescriptorFactory.fromBitmap(smallScooter);
    }
    public void toggle(View v){
        String name = ((ToggleButton) v).getText().toString();
        Log.v(LOG_TAG,name);
        boolean currState = buttons.get(name);
        buttons.replace(name, !currState);
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


        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney").icon(smallScooterIcon));
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
        return new ServeAsyncTaskLoader(this, query,baseLink,apiKey,LOG_TAG);
    }

    @Override
    public void onLoadFinished(Loader< List< Service > > loader, List< Service > data) {
        getLoaderManager().destroyLoader(0);
        if(data!=null){
            if(!adapter.isEmpty()) {
                adapter.clear();
            }
            adapter.addAll(data);
        }
        /*if(data.size()==0&&!firstQuery){
            TextView emptyView= (TextView) findViewById(R.id.emptyView);
            emptyView.setVisibility(View.VISIBLE);
        }*/
    }

    @Override
    public void onLoaderReset(Loader<List<Service>> loader) {
        adapter.clear();
    }


}
