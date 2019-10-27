package com.example.tal;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.AsyncTaskLoader;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.tal.MapsActivity.LOG_TAG;

public class ServeAsyncTaskLoader extends AsyncTaskLoader< HashMap< String,List<Service> > > {
    private String mQuery;
    private String birdLink = "https://data.lime.bike/api/partners/v1/gbfs/los_angeles/free_bike_status";
    private String bikeShareLink = "https://bikeshare.metro.net/stations/json";
    private String bikeLAsmartLink = "https://gbfs.bcycle.com/bcycle_lametro/free_bike_status.json";
    private String mApiKey;
    private String mLogTag;
    private HashMap buttons;
    private HashMap dataRepository = new HashMap();

    public ServeAsyncTaskLoader(Context context, HashMap buttons) {
        super(context);
        this.buttons = buttons;
//        mQuery = query;
//        mBaseLink = base_link;
//        mApiKey = api_key;
//        mLogTag = log_tag;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        Log.i(mLogTag,"Load started");
        forceLoad();
    }

    @Override
    public HashMap< String,List<Service> > loadInBackground() {
//        ArrayList< Service > serviceType = new ArrayList<>();
//        ArrayList< ArrayList > result = new ArrayList<>();
//        ArrayList<Service> output = new Bird(Service.start,Service.start,Service.end).extractServices(jsonResponse);
//        for (int i = 0; i < output.size(); i++) {
//            Log.v("bird",output.get(i).loc+"");
//        }
        if((boolean)buttons.get("Bird")){ //When Bird button is initially pressed
            //creating bird objects
            String jsonBirdResponse = Utils.makeHttpRequest(mQuery,birdLink,mApiKey,mLogTag);

            Log.i("json",jsonBirdResponse);
            ArrayList< Service > birdList = new ArrayList<>();
            try {
                JSONObject baseJson = new JSONObject(jsonBirdResponse);
                JSONObject data = baseJson.getJSONObject("data");
                JSONArray jsonBirds = data.getJSONArray("bikes");
                Log.i("bikes",jsonBirds.toString());
                double value;
                for (int i = 0; i < jsonBirds.length(); i++) {
                    JSONObject jsonBird = jsonBirds.getJSONObject(i);
                    String Latitude = jsonBird.getString("lat");
                    String Longitude = jsonBird.getString("lon");
                    Location location = new Location(Double.parseDouble(Latitude),Double.parseDouble(Longitude));
                    if (Location.displacement_between(location, Service.start) < Bird.radius && Integer.parseInt(jsonBird.get("is_reserved").toString()) == 0 && Integer.parseInt(jsonBird.get("is_disabled").toString()) == 0) {
                        Bird bird = new Bird(location, Service.start, Service.end);
                        //get time url and http request and set time instance
                        String new_url = bird.get_time_url(location,Service.end);
                        String jsonResponse = Utils.makeHttpRequest("",new_url,"","");
                        Log.i("url",new_url);
                        JSONObject baseJsonBird = new JSONObject(jsonResponse); //go to row, elements, par, value
                        value = baseJsonBird.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("distance").getInt("value");
                        bird.time = (int) (value * (0.000621371) / bird.speed );
                        //get walk url and http request and set walk instance
                        String new_walk_url = bird.get_walk_url(location,Service.start);
                        String jsonwalkResponse = Utils.makeHttpRequest("",new_walk_url,"","");
                        JSONObject baseJsonwalk = new JSONObject(jsonwalkResponse); //go to row, elements, par, value
                        bird.walk = baseJsonwalk.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("duration").getInt("value");
                        birdList.add(bird);
                        Log.i("bird_locations",location + "");
                    }
                }
            } catch (JSONException e) {
                Log.i(Bird.name, "Problem parsing json");
            }
            Log.i("bird_size",birdList.size()+"");
            dataRepository.put(Bird.name,birdList);
        }
        if((boolean)buttons.get("Go Bike")){ //When Go Bike button is initially pressed
            //creating ford objects
            String jsonFordResponse = Utils.makeHttpRequest(mQuery,bikeShareLink,mApiKey,mLogTag);
            Log.i("json",jsonFordResponse);
            ArrayList< Service > fordList = new ArrayList<>();
            try {
                JSONObject baseJSON = new JSONObject(jsonFordResponse);
                JSONArray stations = baseJSON.getJSONArray("features");
                for (int i=0; i<stations.length(); i++) {
                    JSONObject st = stations.getJSONObject(i).getJSONObject("properties");
                    if (st.getInt("bikesAvailable") > 0) {
                        Location st_loc = new Location(st.getDouble("latitude"), st.getDouble("longitude"));
                        if (Location.displacement_between(st_loc, Service.start) < Ford.radius) {
                            Ford ford = new Ford(st_loc, Service.start, Service.end, Service.end);
                            String new_url = ford.get_time_url(st_loc, Service.end);
                            String jsonResponse = Utils.makeHttpRequest("", new_url, "", "");
                            Log.i("url", new_url);
                            JSONObject baseJsonFord = new JSONObject(jsonResponse); //go to row, elements, par, value
                            ford.time = baseJsonFord.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("duration").getInt("value");
                            String new_walk_url = ford.get_walk_url(st_loc, Service.start);
                            String jsonwalkResponse = Utils.makeHttpRequest("", new_walk_url, "", "");
                            JSONObject baseJsonwalk = new JSONObject(jsonwalkResponse); //go to row, elements, par, value
                            ford.walk = baseJsonwalk.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("duration").getInt("value");
                            String extra_url = ford.get_extra_time(st_loc,Service.end);
                            String jsonextraResponse = Utils.makeHttpRequest("",extra_url,"","");
                            JSONObject baseextraFord = new JSONObject(jsonextraResponse); //go to row, elements, par, value
                            ford.extra_time = baseextraFord.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("duration").getInt("value");
                            fordList.add(ford);
                            Log.i("ford_locations", st_loc + "");

                        }
                    }
                }
            } catch (JSONException e) {
                Log.i(Bird.name, "Problem parsing json");
            }
            Log.i("ford_size",fordList.size()+"");
            dataRepository.put(Ford.name,fordList);

            //create ford_park objects
            String jsonFord_parkResponse = Utils.makeHttpRequest(mQuery,bikeShareLink,mApiKey,mLogTag);
            Ford_park ford_park= new Ford_park(Service.end);
            Log.i("json",jsonFord_parkResponse);
            dataRepository.put(Ford_park.name,ford_park.extractParking(jsonFord_parkResponse));
        }
        if((boolean)buttons.get("Zipcar")){ //When Go Bike button is initially pressed
            //create bikelasmart objects
            String bikeLAsmartResponse = Utils.makeHttpRequest(mQuery,bikeLAsmartLink,mApiKey,mLogTag);
            Log.i("json",bikeLAsmartResponse);
            ArrayList< Service > bikeLAList = new ArrayList<>();
            try {
                JSONObject baseJson = new JSONObject(bikeLAsmartResponse);
                JSONObject data = baseJson.getJSONObject("data");
                JSONArray jsonBirds = data.getJSONArray("bikes");
                Log.i("bikes",jsonBirds.toString());
                for (int i = 0; i < jsonBirds.length(); i++) {
                    JSONObject jsonBird = jsonBirds.getJSONObject(i);
                    String Latitude = jsonBird.getString("lat");
                    String Longitude = jsonBird.getString("lon");
                    Location location = new Location(Double.parseDouble(Latitude),Double.parseDouble(Longitude));
                    if (Location.displacement_between(location, Service.start) < BikeLAsmart.radius && Integer.parseInt(jsonBird.get("is_reserved").toString()) == 0 && Integer.parseInt(jsonBird.get("is_disabled").toString()) == 0) {
                        BikeLAsmart bike = new BikeLAsmart(location, Service.start, Service.end);
                        String new_url = bike.get_time_url(location,Service.end);
                        String jsonResponse = Utils.makeHttpRequest("",new_url,"","");
                        Log.i("url",new_url);
                        JSONObject baseJsonBikeLA = new JSONObject(jsonResponse);
                        bike.time = baseJsonBikeLA.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("duration").getInt("value");
                        String new_walk_url = bike.get_walk_url(location,Service.start);
                        String jsonwalkResponse = Utils.makeHttpRequest("",new_walk_url,"","");
                        JSONObject baseJsonwalk = new JSONObject(jsonwalkResponse); //go to row, elements, par, value
                        bike.walk = baseJsonwalk.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("duration").getInt("value");
                        bikeLAList.add(bike);
                        Log.i("BikeLAsmart_locations",location + "");
                    }
                }
            } catch (JSONException e) {
                Log.i(BikeLAsmart.name, "Problem parsing json");
            }
            Log.i("bikeLA_size",bikeLAList.size()+"");
            dataRepository.put(BikeLAsmart.name,bikeLAList);
          }
        return dataRepository;
    }
}
