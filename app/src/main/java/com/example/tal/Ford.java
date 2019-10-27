package com.example.tal;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import static java.lang.Math.ceil;

public class Ford extends Service {//before execute this stuff on Ford, calculate bike_dest
    int distance_value, duration_value;
    public static String name = "Ford";
    public Ford(Location loc, Location my_loc, Location final_dest, Location bike_dest){
        super(loc, my_loc, final_dest);
        //this.route = get_route(loc, my_loc, final_dest, bike_dest);
        this.name = name;
        this.extra_time = get_extra_time(bike_dest,final_dest);
    }
    @Override
    double get_cost(Location loc, Location bike_dest) {
        if (time<30*60){
            return 5;
        }
        else{
            return 5+1.75*Math.ceil((time-30*60)/30*60);
        }
    }

    @Override
    int get_time(Location loc, Location bike_dest) {
        String origin ="&origins="+loc.x+","+loc.y;
        String destination = "&destinations="+bike_dest.x+","+bike_dest.y;
        String mode = "&mode=bicycling";
        String new_url = url+origin+destination+mode+API_key;
        return extract_url(new_url, "duration");

    }

    int get_extra_time(Location bike_dest, Location final_dest){
        String origin = "&origins="+bike_dest.x+","+bike_dest.y;
        String destination = "&destinations="+final_dest.x+","+final_dest.y;
        String mode = "&mode=walking";
        String new_url = url+origin+destination+mode+API_key;
        return extract_url(new_url, "duration");
        //return walking value;
    }

    ArrayList<Location> get_route(Location loc, Location my_loc, Location final_dest,Location bike_dest){
        ArrayList<Location> route = new ArrayList<Location>();
        for (Location p : google_route(my_loc,loc)){
            route.add(p);
        }
        for (Location p: google_route(loc,bike_dest)){
            route.add(p);
        }
        for (Location p: google_route(bike_dest,final_dest)){
            route.add(p);
        }
        return route;
    }
    private static double radius = 0.5; // in miles, I think

    @Override
    ArrayList<Service> extractServices(String json) {
        ArrayList<Service> bikeshare_list = new ArrayList<Service>();
        try {
//            ArrayList<Location> possible_destinations = Ford_park.extractParking(json, Service.end);
            JSONObject baseJSON = new JSONObject(json);
            JSONArray stations = baseJSON.getJSONArray("features");
            for (int i=0; i<stations.length(); i++) {
                JSONObject st = stations.getJSONObject(i).getJSONObject("properties");
                if (st.getInt("bikesAvailable") > 0) {
                    Location st_loc  = new Location(st.getDouble("latitude"), st.getDouble("longitude"));
                    if (Location.displacement_between(st_loc, Service.start) < radius) {
                        bikeshare_list.add(new Ford(st_loc, Service.start, Service.end, Service.end));
                        Log.i("ford_locations",st_loc + "");
                    }
                }

            }
        } catch (JSONException e) {
            Log.i("Oops gbfs_maker", "Problem parsing json");
        }
        Log.i("ford_size",bikeshare_list.size() + "");
        return bikeshare_list;
    }


}
