package com.example.tal;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import static java.lang.Math.ceil;

public class Ford extends Service {//before execute this stuff on Ford, calculate bike_dest
    int distance_value, duration_value;

    public Ford(Location loc, Location my_loc, Location final_dest, Location bike_dest){
        super(loc, my_loc, final_dest);
        name = "Ford";
        route = get_route(loc, my_loc, final_dest, bike_dest);
    }
    @Override
    double get_cost(Location loc, Location bike_dest) {
        if (this.time<30*60){
            return 3;
        }
        else{
            return 3+3*Math.ceil((time/60-30)/15);
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

    @Override
    ArrayList< Service > extractServices(String json){
        return null; //TODO:need to be changed
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



}
