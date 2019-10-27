package com.example.tal;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Retrieval {

    public static void main(String[] args){ //for all, get all instances around certain radius
        //birds = get_bird();
        //fords = get_ford();
        //gigcars = get_gigcar();


    }

    public static ArrayList<Service> get_bird(){ //for bird, get also if the battery is enough (i charge=15 miles)
        return new ArrayList<Service>();
        //return arraylist of stufff;
    }

    public static double radius = 10; // in miles, I think

    public static ArrayList<Service> get_gbfs(){
        ArrayList<Service> gbfs_list = new ArrayList<Service>();
        try {
            String json = ""; // I hope you're giving me a real string somehow
            JSONObject baseJSON = new JSONObject(json);
            JSONArray bikes = baseJSON.getJSONObject("data").getJSONArray("bikes");
            for (int i = 0; i<bikes.length(); i++) {
                JSONObject b = bikes.getJSONObject(i);
                if (b.getInt("is_disabled") != 0 && b.getInt("is_reserved") != 0) {
                    Location b_loc = new Location(b.getDouble("lat"), b.getDouble("lon"));
                    if (Location.displacement_between(b_loc, Service.start) > radius) {
                        gbfs_list.add(new Ford(b_loc, Service.start, Service.end, Service.end));
                        //also need to figure out how to set the bike station thing
                    }
                }
            }
            return gbfs_list; //accuracy still not guaranteed
        } catch (JSONException e) {
            Log.i("Oops gbfs_maker", "Problem parsing json");
        }
        return gbfs_list;
    }

//    public static ArrayList<Service> get_ford(){
//        ArrayList<Service> ford_list = new ArrayList<Service>();
//        try {
//            String json = "";
//            JSONObject baseJson = new JSONObject(json); //magically get it from the file somehow
//            JSONArray station_list = baseJson.getJSONArray("stationBeanList");
//            JSONArray jsonBirds = new JSONArray();
//            for (int i = 0; i < station_list.length(); i++) {
//                JSONObject station = station_list.getJSONObject(i);
//                if (station.getInt("availableBikes") > 0 && station.getString("statusValue").equals("In Service")) {
//                    Location station_location = new Location(station.getDouble("latitude"), station.getDouble("longitude"));
//                    if (Location.displacement_between(station_location, Service.start) < radius) {
//                        ford_list.add(new Ford(station_location, Service.start, Service.end, Service.end));
//                        //also need to set the bike station destinations somehow???
//                    }
//                }
//            }
//            return ford_list; //check if this works please
//        } catch (JSONException e) {
//            Log.i("Oops fordmaker", "Problem parsing json");
//        }
//
//
//        return new ArrayList<Service>();
//        //return arraylist of stufff;
//    }

    public static ArrayList<Service> get_gigcar(){
        return new ArrayList<Service>();
        //return arraylist of stufff;
    }
}
