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

    public static ArrayList<Service> get_ford(){
        ArrayList<Service> ford_list = new ArrayList<Service>();
        try {
            JSONObject baseJson = new JSONObject(json); //magically get it from the file somehow
            JSONArray station_list = baseJson.getJSONArray("stationBeanList");
            for (int i = 0; i < station_list.length(); i++) {
                JSONObject station = jsonBirds.getJSONObject(i);
                if (station.getInt("availableBikes") > 0 && station.getString("statusValue").equals("In Service")) {
                    Location station_location = new Location(station.getDouble("latitude"), station.getDouble("longitude"));
                    if (Location.displacement_between(station_location, Service.start) < radius) {
                        ford_list.add(new Ford(station_location, Service.start, Service.end));
                        //also need to set the bike station destinations somehow???
                    }
                }
            }
            return ford_list; //check if this works please
        } catch (JSONException e) {
            Log.i("Oops fordmaker", "Problem parsing json");
        }


        return new ArrayList<Service>();
        //return arraylist of stufff;
    }

    public static ArrayList<Service> get_gigcar(){
        return new ArrayList<Service>();
        //return arraylist of stufff;
    }
}
