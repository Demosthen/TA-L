package com.example.tal;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Ford_park extends Service_park {

    public Ford_park(Location d) {
        super(d);
        name = "Ford_park";
    }

    ArrayList< Location > extractParking(String json, Location d) {
        ArrayList< Location > docList = new ArrayList<>();
        //get all the ford parking spots within radius of location of final destination
        /*try {
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
        }*/
        return docList;
    }
}
