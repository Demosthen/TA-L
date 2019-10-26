package com.example.tal;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static java.lang.Math.ceil;

public class Ford extends Service {//before execute this stuff on Ford, calculate bike_dest
    int distance_value, duration_value;

    public Ford(Location loc, Location my_loc, Location final_dest){
        super(loc, my_loc, final_dest);
        name = "Ford";
    }
    @Override
    double get_cost(Location loc, Location bike_dest) {
        if (this.time<30*60){
            return 3;
        }
        else{
            return 3+3*Math.ceil((this.time/60-30)/15);
        }
    }

    @Override
    int get_time(Location loc, Location bike_dest) {
        String origin ="&origins="+loc.x+","+loc.y;
        String destination = "&destinations="+bike_dest.x+","+bike_dest.y;
        String mode = "&mode=bicycling";
        String new_url = this.url+origin+destination+mode+this.API_key;
        return extract_url(new_url, "duration");
\
    }

    int get_extra_time(Location bike_dest, Location final_dest){
        String origin = "&origins="+bike_dest.x+","+bike_dest.y;
        String destination = "&destinations="+final_dest.x+","+final_dest.y;
        String mode = "&mode=walking";
        String new_url = this.url+origin+destination+mode+this.API_key;
        return extract_url(new_url, "duration");
        //return walking value;
    }

    int extract_url(String url, String par){

        try {
            //magic turns url into json string
            JSONObject baseJson = new JSONObject(json); //go to row, elements, par, value
            return baseJson.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject(par).getInt("value");
        } catch (JSONException e) {
            Log.i("Oops：Ford error", "Problem parsing json");
        }

    }


}
