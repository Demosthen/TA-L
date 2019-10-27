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

    public static double radius = 1; //remove if needed to ensure a station

    public static ArrayList<Location> extractParking(String json) {
        ArrayList<Location> park_list = new ArrayList<>();
        try {
            JSONObject baseJSON = new JSONObject(json);
            JSONArray stations = baseJSON.getJSONObject("data").getJSONArray("bikes");
            for (int i = 0; i<stations.length(); i++) {
                JSONObject s = stations.getJSONObject(i);
                Location s_loc = new Location(s.getDouble("lat"), s.getDouble("lon"));
                if (Location.displacement_between(s_loc, Service.end) < radius) {
                    park_list.add(s_loc);
                }
            }
            return park_list; //accuracy still not guaranteed
        } catch (JSONException e) {
            Log.i("Oops gbfs_maker", "Problem parsing json");
        }
        return park_list;
    }
}


//    ArrayList< Location > extractParking(String json, Location d) {
//        ArrayList< Location > docList = new ArrayList<>();
//        //get all the ford parking spots within radius of location of final destination
//        /*try {
//            JSONObject baseJson = new JSONObject(json);
//            JSONObject response = baseJson.getJSONObject("response");
//            JSONArray jsonBirds = response.getJSONArray("birds");
//            for (int i = 0; i < jsonBirds.length(); i++) {
//                JSONObject jsonBird = jsonBirds.getJSONObject(i);
//                JSONObject jsonLocation = jsonBird.getJSONObject("location");
//                Location location = new Location(jsonLocation.getDouble("latitude"),jsonLocation.getDouble("longitude"));
//                Bird bird = new Bird(location, Service.start, Service.end);
//                bird.battery = jsonBird.getInt("battery_level");
//                docList.add(bird);
//            }
//        } catch (JSONException e) {
//            Log.i(Bird.name, "Problem parsing json");
//        }*/
//        return docList;
//    }
//}
