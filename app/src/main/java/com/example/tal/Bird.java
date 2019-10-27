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
    boolean is_reserved;
    boolean is_disabled;

    public Bird(Location loc, Location my_loc, Location final_dest) {
        super(loc, my_loc, final_dest);
        this.name = "Bird";
        this.route = get_route(loc, my_loc, final_dest);
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
            JSONObject data = baseJson.getJSONObject("data");
            JSONArray jsonBirds = data.getJSONArray("bikes");
            Log.i("bikes",jsonBirds.toString());
            for (int i = 0; i < jsonBirds.length(); i++) {
                JSONObject jsonBird = jsonBirds.getJSONObject(i);
                String Latitude = jsonBird.getString("lat");
                String Longitude = jsonBird.getString("lon");
                Location location = new Location(Double.parseDouble(Latitude),Double.parseDouble(Longitude));
                Bird bird = new Bird(location, Service.start, Service.end);
                bird.is_reserved = Integer.parseInt(jsonBird.get("is_reserved").toString()) == 1;
                bird.is_disabled = Integer.parseInt(jsonBird.get("is_disabled").toString()) == 1;
                docList.add(bird);
            }
        } catch (JSONException e) {
            Log.i(Bird.name, "Problem parsing json");
        }
        Log.i("length",docList.size()+"");
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
