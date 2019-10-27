package com.example.tal;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BikeLAsmart extends Service {
    public static String name = "BikeLAsmart";

    public BikeLAsmart(Location loc, Location my_loc, Location final_dest) {
        super(loc, my_loc, final_dest);
        //this.route = get_route(loc, my_loc, final_dest);
        this.name = name;
    }

    @Override
    double get_cost(Location loc, Location final_dest) {
        if (time<30*60){
            return 5;
        }
        else{
            return 5+1.75*Math.ceil((time-30*60)/30*60);
        }
    }

    @Override
    int get_time(Location loc, Location final_dest) {
        String origin = "&origins="+loc.x+","+loc.y;
        String destination = "&destinations="+final_dest.x+","+final_dest.y;
        String new_url = url+origin+destination+API_key;
        return extract_url(new_url, "duration");
    }

    private static double radius = 1; // in miles, I think

    @Override
    ArrayList< Service > extractServices(String json) {
        ArrayList< Service > BikeLAsmartList = new ArrayList<>();
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
                    BikeLAsmartList.add(new BikeLAsmart(location, Service.start, Service.end));
                    Log.i("BikeLAsmart_locations",location + "");
                }
            }
        } catch (JSONException e) {
            Log.i(BikeLAsmart.name, "Problem parsing json");
        }
        Log.i("bird_size",BikeLAsmartList.size()+"");
        return BikeLAsmartList;
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
