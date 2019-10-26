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
    }

    @Override
    double get_cost(Location loc, Location final_dest) {
        return base + this.time * rate;
    }

    @Override
    int get_time(Location loc, Location final_dest) {
        double value = 67;
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
}
