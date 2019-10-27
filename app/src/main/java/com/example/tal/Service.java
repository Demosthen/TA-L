package com.example.tal;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Dictionary;

public abstract class Service{

    public static String name; //name of service
    public static String appID; //app ID of service 'com.app_name...'
    static String url = "http://maps.googleapis.com/maps/api/distancematrix/outputjson?units=imperial"; //google url for distance between locations
    static String route_url = "https://maps.googleapis.com/maps/api/directions/json?";
    static String API_key = "&key=AIzaSyA8CApQee8fXVHI3FLEP6IE8bK_B6_oIpY";
    public double cost; //cost to get to final destination
    public int time; //time to get to final destination in seconds
    public int walk; //time to get from user current location to service location in seconds
    public Location loc; //Location.x=longitude, Location.y=latitude; Location of Service
    public Location my_loc; //Location of me
    public Location final_dest; //Location of final destination of service
    public static Location start = new Location(34.053740, -118.242643); // will be changed
    public static Location end = new Location(34.068742, -118.444807); // will be changed
    public static int extra_time = 0;
    public ArrayList <Location> route;


    public Service(){

    }

    public Service(Location loc, Location my_loc, Location final_dest){
        this.loc = loc;
        this.my_loc = my_loc;
        this.final_dest = final_dest;
        if(loc!=null && my_loc!=null&&final_dest!=null) {
            this.time = get_time(loc, final_dest);
            this.cost = get_cost(loc, final_dest);
            this.walk = get_walk(loc, my_loc);
        }


    }

    abstract double get_cost(Location loc, Location final_dest);
    abstract int get_time(Location loc, Location final_dest);
    //abstract ArrayList<Location> get_route(Location loc, Location my_loc, Location final_dest);
    abstract ArrayList<Service> extractServices(String json);

    int extract_url(String url, String par){

        try {
            //magic turns url into json string
            //JSONObject baseJson = new JSONObject(); //go to row, elements, par, value
            String json = "";
            JSONObject baseJson = new JSONObject(json); //go to row, elements, par, value
            return baseJson.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject(par).getInt("value");
        } catch (JSONException e) {
            Log.i("google parsing error", "Problem parsing json");
        }
        return -1;
    }

    int get_walk(Location loc, Location my_loc) {
        String origin = "&origins="+my_loc.x+","+my_loc.y;
        String destination = "&destinations="+loc.x+","+loc.y;
        String mode = "&mode=walking";
        String new_url = url+origin+destination+mode+API_key;
        return extract_url(new_url, "duration");
        //return walking_time;
    }

    ArrayList<Location> google_route(Location o, Location d){
        ArrayList<Location> path = new ArrayList<>();
        path.add(o);
        String origin = "origin="+o.x+","+o.y;
        String destination = "&destination="+d.x+","+d.y;
        String url_path = route_url+origin+destination+API_key;
        String jsonResponse = Utils.makeHttpRequest("",url_path,"","");

        try{
            JSONObject baseJson = new JSONObject(jsonResponse);
            JSONArray steps = baseJson.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
            for (int i = 0; i < steps.length(); i++){
                JSONObject jsonStep = steps.getJSONObject(i);
                JSONObject end = jsonStep.getJSONObject("end_location");
                path.add(new Location(end.getDouble("lat"),end.getDouble("lng")));
            }
        } catch (JSONException e) {
            Log.i("google route", "Problem parsing json");
        }
        return path;
    }

}
