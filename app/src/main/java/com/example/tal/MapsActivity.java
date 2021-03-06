package com.example.tal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import android.app.LoaderManager;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LoaderManager.LoaderCallbacks<HashMap<String,List<Service>>>{

    private GoogleMap mMap;
    public static String LOG_TAG = "TEAMAVATARPLUSLARRY";

    public HashMap< String, List<Service> > services = new HashMap<String, List<Service>>();
    HashMap<String, Boolean> buttons = new HashMap<String, Boolean>();
    int bikeIconHeight = 125;
    int bikeIconWidth = 125;
    int scootIconWidth = 75;
    int scootIconHeight = 75;
    Transition transition = new AutoTransition();
    ServiceAdapter adapter;
    private boolean firstQuery = true;
    Place startPlace = null;
    Place destPlace = null;
    boolean startCalled = false;
    boolean destCalled = false;
    BitmapDescriptor smallBikeIcon;
    BitmapDescriptor smallScooterIcon;
    BitmapDescriptor smallBikeParkIcon;
    BitmapDescriptor smallBikeLAIcon;
    int markerCounter = 0;
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
        //adapter = new ServiceAdapter(this, services);
        //listView.setAdapter(adapter);
        //start loader


//      RequestQueue queue = Volley.newRequestQueue(this);
//      queue.add(Utils.makeVolleyQueueRequest("",baseLink,"",""));

        // Initialize the Destination AutocompleteSupportFragment.
        final AutocompleteSupportFragment destinationFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.dest_fragment);

        // Specify the types of place data to return.
        destinationFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        destinationFragment.setHint("Destination");
        // Set up a PlaceSelectionListener to handle the response.
        destinationFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                // Move camera
                Marker mark = mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title("Marker at Destination"));
                mark.setTag(99999+markerCounter);//give mark a negative ID to differentiate from service markers
                markerCounter++;
                mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
                destinationFragment.setText(place.getName());
                destPlace = place;
                Service.end = new Location(destPlace.getLatLng().latitude, destPlace.getLatLng().longitude);
                if(startCalled){
                    getLoaderManager().initLoader(0, null, MapsActivity.this);
                }
                destCalled = true;

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
        startFragment.setHint("Start");
        // Set up a PlaceSelectionListener to handle the response.
        startFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                // Move camera
                Marker mark = mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title("Marker at Start"));
                mark.setTag(99999+markerCounter);//give mark a negative ID to differentiate from service markers
                markerCounter++;
                mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
                startPlace = place;
                startFragment.setText(place.getName());
                Service.start = new Location(startPlace.getLatLng().latitude, startPlace.getLatLng().longitude);
                if(destCalled){
                    getLoaderManager().initLoader(0, null, MapsActivity.this);
                }
                startCalled = true;

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(LOG_TAG, "An error occurred: " + status);
            }
        });
        //initialize buttons
        ToggleButton temp = findViewById(R.id.BirdButton);
        buttons.put(temp.getText().toString(), true);
        Log.i("buttons",temp.getText().toString());
        temp = findViewById(R.id.FordButton);
        buttons.put(temp.getText().toString(), true);
        Log.i("buttons",temp.getText().toString());
        temp = findViewById(R.id.ZipcarButton);
        buttons.put(temp.getText().toString(), true);
        Log.i("buttons",temp.getText().toString());
        //initialize icons
        initializeIcons();
        //initialize service summary
        final FrameLayout summary = findViewById(R.id.service_summary);
        summary.setVisibility(View.GONE);
        summary.setFocusable(true);

        summary.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    summary.setVisibility(View.GONE);
                }
            }
        });
        summary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                summary.setVisibility(View.GONE);
            }
        });
        //initialize services Hashmap
        services.put(Bird.name, new ArrayList<Service>());
        services.put(Ford.name, new ArrayList<Service>());
        services.put(Ford_park.name, new ArrayList<Service>());
        services.put(BikeLAsmart.name, new ArrayList<Service>());

    }

    void initializeIcons(){
        Bitmap bike = BitmapFactory.decodeResource(getResources(), R.mipmap.bike);
        Bitmap scooter = BitmapFactory.decodeResource(getResources(), R.mipmap.scooter);
        Bitmap smallBike = Bitmap.createScaledBitmap(bike, bikeIconWidth, bikeIconHeight, false);
        Bitmap smallScooter = Bitmap.createScaledBitmap(scooter,scootIconWidth,scootIconHeight,false);
        Bitmap bikeLA = BitmapFactory.decodeResource(getResources(), R.mipmap.bikelasmart);
        Bitmap smallBikeLA = Bitmap.createScaledBitmap(bikeLA,bikeIconWidth,bikeIconHeight,false);
        Bitmap bikePark = BitmapFactory.decodeResource(getResources(), R.mipmap.bikepark);
        Bitmap smallBikePark = Bitmap.createScaledBitmap(bikePark,scootIconWidth,scootIconHeight,false);
        smallBikeIcon = BitmapDescriptorFactory.fromBitmap(smallBike);
        smallScooterIcon = BitmapDescriptorFactory.fromBitmap(smallScooter);
        smallBikeLAIcon = BitmapDescriptorFactory.fromBitmap(smallBikeLA);
        smallBikeParkIcon = BitmapDescriptorFactory.fromBitmap(smallBikePark);
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
        final FrameLayout summary = findViewById(R.id.service_summary);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                int id = (int) marker.getTag();
                final Marker mark = marker;
                if(id >= 0 && id<99999) {// check that it is a service
                    String name = marker.getTitle();
                    final Service service = services.get(name).get(id);
                    TextView serviceView = (TextView) findViewById(R.id.service_name);
                    TextView ETAView = (TextView) findViewById(R.id.ETA);
                    TextView EPriceView = (TextView) findViewById(R.id.EP);
                    Button orderButton = (Button) findViewById(R.id.order);
                    Button setButton = (Button) findViewById(R.id.setStart);
                    serviceView.setText(name);
                    ETAView.setText(Integer.toString(service.time+service.walk+service.extra_time));
                    EPriceView.setText("$"+service.cost);
                    summary.setVisibility(View.VISIBLE);
                    summary.requestFocus();
                    orderButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent launchIntent = getPackageManager().getLaunchIntentForPackage(Service.appID);
                            if (launchIntent != null) {
                                startActivity(launchIntent);//null pointer check in case package name was not found
                            }
                        }
                    });
                    if(Service.end!=null){
                        setButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LatLng coords = mark.getPosition();

                                Service.start = new Location(coords.latitude, coords.longitude);
                                LatLng startCoords = startPlace.getLatLng();
                                LatLng destCoords = destPlace.getLatLng();
                                if(mark.getTitle().equals(Bird.name)){
                                    Bird b = (Bird) service;
                                    MyAsync birdie = new MyAsync();
                                    Bird Birdie = new Bird(Service.start, new Location(startCoords.latitude, startCoords.longitude), Service.end);
                                    Birdie.name2 = Bird.name;
                                    ArrayList borOut = new ArrayList();
                                    borOut.add(new Location(1,0));
                                    borOut.add(Service.start);
                                    borOut.add(new Location(startCoords.latitude, startCoords.longitude));
                                    borOut.add(Service.end);
                                    Log.v(LOG_TAG,"EXECUTING BIRDIES");
                                    birdie.execute(borOut);

                                }
                                if(mark.getTitle().equals(Ford.name)){
                                    Ford b = (Ford) service;
                                    MyAsync fordie = new MyAsync();
                                    Ford Fordie =  new Ford(Service.start, new Location(startCoords.latitude, startCoords.longitude), Service.end,new Location(destCoords.latitude, destCoords.longitude));
                                    Fordie.name2 = Ford.name;
                                    ArrayList ForOut = new ArrayList();
                                    ForOut.add(new Location(0,0));
                                    ForOut.add(Service.start);
                                    ForOut.add(new Location(startCoords.latitude, startCoords.longitude));
                                    ForOut.add(Service.end);
                                    ForOut.add(new Location(destCoords.latitude, destCoords.longitude));
                                    Log.v(LOG_TAG,"EXECUTING FORDIES");
                                    fordie.execute(ForOut);

                                }
                            }
                        });
                    }
                    else{
                        setButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LatLng coords = mark.getPosition();
                                Service.start = new Location(coords.latitude, coords.longitude);
                            }
                        });
                    }


                    return false;
                }
                return false;
            }
        });


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
    public Loader< HashMap< String,List<Service> > > onCreateLoader(int id, Bundle args){
        return new ServeAsyncTaskLoader(this, buttons);
    }

    @Override
    public void onLoadFinished(Loader< HashMap< String,List<Service> > > loader, HashMap<String, List<Service> > data) {
        getLoaderManager().destroyLoader(0);
        Log.v(LOG_TAG, "FINALLY FINISHED LOADING DATA");
        if(data!=null){
            for(HashMap.Entry<String, List<Service> > datum : data.entrySet()){
                ArrayList<Service> datumList = (ArrayList) datum.getValue();
                for(Service service : datumList){
                    if(service != null){
                        try{
                            MarkerOptions options = new MarkerOptions().position(new LatLng(service.loc.x, service.loc.y)).title(datum.getKey());
                            if(datum.getKey() == Bird.name){
                                options.icon(smallScooterIcon);
                            }
                            else if(datum.getKey() == Ford.name){
                                options.icon(smallBikeIcon);
                            }
                            else if(datum.getKey() == Ford_park.name){
                                options.icon(smallBikeParkIcon);
                            }
                            else if(datum.getKey() == BikeLAsmart.name){
                                options.icon(smallBikeLAIcon);
                            }
                            Marker mark = mMap.addMarker(options);

                            mark.setTag(services.get(datum.getKey()).size());//give mark an ID
                            services.get(datum.getKey()).add(service);
                        } catch (NullPointerException n) {
                            try {
                                Log.e(LOG_TAG, datum.getKey());
                            }
                            catch(NullPointerException e){
                                Log.e(LOG_TAG, datumList.toString());
                            }
                        }
                    }
                }

            }

        }
    }

    @Override
    public void onLoaderReset(Loader< HashMap< String,List<Service> > > loader) {
        adapter.clear();
    }

    // @Override
    public void onLoadFinished(androidx.loader.content.Loader<List<Service>> loader, List<Service> data) {

        /*if(data.size()==0&&!firstQuery){
            TextView emptyView= (TextView) findViewById(R.id.emptyView);
            emptyView.setVisibility(View.VISIBLE);
        }*/
    }

    /**
     * {@link AsyncTask} to perform the network request on a background thread, and then
     * update the UI with the first earthquake in the response.
     */
    public class MyAsync extends AsyncTask<ArrayList<Location>, Void, ArrayList<Location>> {


        /**
         * Update
         */
        @Override
        protected void onPostExecute(ArrayList<Location> locs) {
            if (locs == null) {
                return;
            }

            drawLines(locs);
        }
        @Override
        protected ArrayList<Location> doInBackground(ArrayList<Location>... a ){
            Log.v(LOG_TAG, "WTF");
            if(a[0].get(0).x == 1){
                Log.v(LOG_TAG,"It's a BIRDIE");
                Bird newFord = new Bird (a[0].get(1), a[0].get(2), a[0].get(3));
                return newFord.get_route( a[0].get(1),  a[0].get(2), a[0].get(3));
            }
            else if(a[0].get(0).x == 0){
                Log.v(LOG_TAG,"It's a FORDIE");
                Ford newFord = new Ford(a[0].get(1), a[0].get(2), a[0].get(3), a[0].get(4));
                return  newFord.get_route(a[0].get(1), a[0].get(2), a[0].get(3), a[0].get(4));
            }
            else{
                Log.v(LOG_TAG, a.toString());
                return null;
            }

        }
        /**
         * Returns new URL object from the given string URL.
         */
        private URL createUrl(String stringUrl) {
            URL url = null;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException exception) {
                Log.e(LOG_TAG, "Error with creating URL", exception);
                return null;
            }
            return url;
        }

        /**
         * Make an HTTP request to the given URL and return a String as the response.
         */
        public String makeHttpRequest(String query, String base_link, String api_key, String log_tag){
//        URL link=makeURL(query,base_link,api_key,log_tag);
            URL link = null;
            try {
                link = new URL(base_link);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection urlConnection=null;
            String jsonResponse=null;
            InputStream stream=null;
            if(link!=null){

                try {
                    urlConnection = (HttpURLConnection) link.openConnection();
                    urlConnection.setConnectTimeout(15000);
                    urlConnection.setReadTimeout(10000);
                    urlConnection.setDefaultUseCaches(false);
                    urlConnection.setUseCaches(false);
                    urlConnection.setRequestProperty("Request Method","GET");
                    urlConnection.connect();
                    if(urlConnection.getResponseCode()==HttpURLConnection.HTTP_OK){
                        stream=urlConnection.getInputStream();
                        jsonResponse=readFromStream(stream);

                    }
                    else{
                        Log.i(log_tag,"error code:" + urlConnection.getResponseCode() + "; info: " + urlConnection.getResponseMessage());
                    }
                }
                catch(IOException e){
                    Log.i(log_tag, "problem with connection");
                }
                finally{
                    if(urlConnection!=null){
                        urlConnection.disconnect();
                    }
                    if(stream!=null){
                        try{
                            stream.close();
                        }
                        catch(IOException e){
                            Log.i(log_tag,"problem with inputStream");
                        }
                    }
                }
            }

            return jsonResponse;
        }

        /**
         * Convert the {@link InputStream} into a String which contains the
         * whole JSON response from the server.
         */
        private String readFromStream(InputStream stream)throws IOException{
            if(stream!=null){
                InputStreamReader inReader= new InputStreamReader(stream, Charset.forName("UTF-8"));
                BufferedReader reader= new BufferedReader(inReader);
                StringBuilder response=new StringBuilder("");
                String line=reader.readLine();
                while(line!=null){
                    response.append(line);
                    line=reader.readLine();
                }
                return response.toString();
            }
            return null;
        }

    }
}


