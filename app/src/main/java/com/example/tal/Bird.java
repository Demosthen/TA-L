package com.example.tal;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Bird extends Service {
    double speed = 15.0;
    double base = 1;
    double rate = 0.23 / 60;
    public static String name = "Bird";
    static double radius = 0.5; // in miles, I think

    public Bird(Location loc, Location my_loc, Location final_dest) {
        super(loc, my_loc, final_dest);
        //this.route = get_route(loc, my_loc, final_dest);
        this.name = name;
    }

    @Override
    double get_cost(Location loc, Location final_dest) {
        return base + time * rate;
    }


    @Override
    String get_time_url(Location loc, Location final_dest) {
        String origin = "&origins="+loc.x+","+loc.y;
        String destination = "&destinations="+final_dest.x+","+final_dest.y;
        String new_url = Service.url+origin+destination+API_key;
        return new_url;
    }

    @Override
    ArrayList< Service > extractServices(String json) {
        ArrayList< Service > birdList = new ArrayList<>();
        try {
            JSONObject baseJson = new JSONObject(json);
            JSONObject data = baseJson.getJSONObject("data");
            JSONArray jsonBirds = data.getJSONArray("bikes");
            Log.i("bikes",jsonBirds.toString());
            for (int i = 0; i < jsonBirds.length(); i++) {
                JSONObject jsonBird = jsonBirds.getJSONObject(i);
                String Latitude = jsonBird.getString("lat");
                String Longitude = jsonBird.getString("lon");
                Location location = new Location(Double.parseDouble(Latitude),Double.parseDouble(Longitude));
                if (Location.displacement_between(location, Service.start) < radius && Integer.parseInt(jsonBird.get("is_reserved").toString()) == 0 && Integer.parseInt(jsonBird.get("is_disabled").toString()) == 0) {
                    Bird bird = new Bird(location, Service.start, Service.end);
                    birdList.add(bird);
                    Log.i("bird_locations",location + "");
                }
            }
        } catch (JSONException e) {
            Log.i(Bird.name, "Problem parsing json");
        }
        Log.i("bird_size",birdList.size()+"");
        return birdList;
    }

    ArrayList<Location> get_route(Location loc, Location my_loc, Location final_dest){
        ArrayList<Location> route = new ArrayList<Location>();
        for (Location p : google_route(my_loc,loc)){
            route.add(p);
        }
        for (Location p: google_route(loc,final_dest)){
            route.add(p);
        }
        return route;
    }
}
