package com.example.tal;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Bird extends Service {
    int speed = 15;
    double base = 1;
    double rate = 0.15 / 60;
    int battery;

    public Bird(Location loc, Location my_loc, Location final_dest) {
        super(loc, my_loc, final_dest);
        name = "Bird";
        route = get_route(loc, my_loc, final_dest);
    }

    @Override
    double get_cost(Location loc, Location final_dest) {
        return base + time * rate;
    }

    @Override
    int get_time(Location loc, Location final_dest) {
        String origin = "&origins="+loc.x+","+loc.y;
        String destination = "&destinations="+final_dest.x+","+final_dest.y;
        String new_url = url+origin+destination+API_key;
        double value = extract_url(new_url, "duration");
        return (int) (value * (0.000621371) / speed * (3600 / 5280));
    }

    @Override
    ArrayList< Service > extractServices(String json) {
        ArrayList< Service > docList = new ArrayList<>();
        try {
            JSONObject baseJson = new JSONObject(json);
            JSONObject response = baseJson.getJSONObject("response");
            JSONArray jsonBirds = response.getJSONArray("birds");
            for (int i = 0; i < jsonBirds.length(); i++) {
                JSONObject jsonBird = jsonBirds.getJSONObject(i);
                JSONObject jsonLocation = jsonBird.getJSONObject("location");
                Location location = new Location(jsonLocation.getDouble("latitude"),jsonLocation.getDouble("longitude"));
                Bird bird = new Bird(location, Service.start, Service.end);
                bird.battery = jsonBird.getInt("battery_level");
                docList.add(bird);
            }
        } catch (JSONException e) {
            Log.i(Bird.name, "Problem parsing json");
        }
        return docList;
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
