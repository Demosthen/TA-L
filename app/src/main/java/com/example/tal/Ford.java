package com.example.tal;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

import static com.example.tal.MapsActivity.LOG_TAG;
import static java.lang.Math.ceil;

public class Ford extends Service {//before execute this stuff on Ford, calculate bike_dest
    int distance_value, duration_value;
    public static String name = "Ford";
    public static String name2 = "";
    public static double radius = 0.5; // in miles, I think
    public static int extra_time;
    public static Location bike_dest;
    public Ford(Location loc, Location my_loc, Location final_dest, Location bike_dest){
        super(loc, my_loc, final_dest);
        this.bike_dest = bike_dest;
        //this.route = get_route(loc, my_loc, final_dest, bike_dest);
        this.name = name;
        if(bike_dest!=null && final_dest!=null)
        this.extra_time = 0;
    }
    @Override
    double get_cost(Location loc, Location bike_dest) {
        if (time<30.0*60.0){
            return 5.0;
        }
        else{
            return 5.0+1.75*Math.ceil((time-30.0*60.0)/(30.0*60.0));
        }
    }

    @Override
    String get_time_url(Location loc, Location bike_dest) {
        String origin ="&origins="+loc.x+","+loc.y;
        String destination = "&destinations="+bike_dest.x+","+bike_dest.y;
        String mode = "&mode=bicycling";
        String new_url = url+origin+destination+mode+API_key;
        return new_url;
//        String jsonResponse = Utils.makeHttpRequest("",new_url,"","");
//        return extract_url(jsonResponse, "duration");

    }

    String get_extra_time(Location bike_dest, Location final_dest){
        String origin = "&origins="+bike_dest.x+","+bike_dest.y;
        String destination = "&destinations="+final_dest.x+","+final_dest.y;
        String mode = "&mode=walking";
        String new_url = Service.url+origin+destination+mode+API_key;
        return new_url;
        //return walking value;
    }

    public ArrayList<Location> get_route(Location loc, Location my_loc, Location final_dest,Location bike_dest){
        ArrayList<Location> route = new ArrayList<Location>();
        Log.v(LOG_TAG, "GETTING FORDIE ROUTES");
        for (Location p : google_route(my_loc,loc)){
            route.add(p);
        }
        for (Location p: google_route(loc,bike_dest)){
            route.add(p);
        }
        for (Location p: google_route(bike_dest,final_dest)){
            route.add(p);
        }
        Log.v(LOG_TAG, "DONE WITH FORDIE ROUTES");
        return route;
    }

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
                        Ford ford = new Ford(st_loc, Service.start, Service.end, Service.end);
//                        ford.time = get_time(st_loc,Service.end);
//                        ford.walk = get_walk(st_loc,Service.start);
                        bikeshare_list.add(ford);
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

    public class  FordAsyncTask extends GoogleAsync{
        @Override
        protected ArrayList<Location>doInBackground(Ford... a){
            return get_route(a[0].loc, a[0].my_loc, a[0].final_dest, a[0].bike_dest);
        }
    }
}
