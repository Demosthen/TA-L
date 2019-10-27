package com.example.tal;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

import static com.example.tal.MapsActivity.LOG_TAG;

public abstract class Service{

    public static String name; //name of service
    public static String appID; //app ID of service 'com.app_name...'
    static String url = "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial"; //google url for distance between locations
    static String route_url = "https://maps.googleapis.com/maps/api/directions/json?";
    static String API_key = "&key=AIzaSyA8CApQee8fXVHI3FLEP6IE8bK_B6_oIpY";
    public static String name2;
    public Location bike_dest;
    public double cost; //cost to get to final destination
    public int time; //time to get to final destination in seconds
    public int walk; //time to get from user current location to service location in seconds
    public Location loc; //Location.x=longitude, Location.y=latitude; Location of Service
    public Location my_loc; //Location of me
    public Location final_dest; //Location of final destination of service
    public static Location start = new Location(34.053740, -118.242643); // will be changed
    public static Location end = new Location(41.43206, -81.38992); // will be changed
    public static int extra_time = 0;
    public ArrayList <Location> route;


    public Service(){

    }

    public Service(Location loc, Location my_loc, Location final_dest){
        this.loc = loc;
        this.my_loc = my_loc;
        this.final_dest = final_dest;
        if(loc!=null && my_loc!=null&&final_dest!=null) {
            this.time = 0;
            this.cost = get_cost(loc, final_dest);
            this.walk = 0;
        }


    }

    abstract double get_cost(Location loc, Location final_dest);
    abstract String get_time_url(Location loc, Location final_dest);
    //abstract ArrayList<Location> get_route(Location loc, Location my_loc, Location final_dest);
    abstract ArrayList<Service> extractServices(String json);

    public static int extract_url(String jsonResponse, String par){
        try {
              JSONObject baseJson = new JSONObject(jsonResponse); //go to row, elements, par, value
//            JSONArray rows = baseJson.getJSONArray("rows");
//            Log.i("par",rows + "");
//            JSONObject elements = rows.getJSONObject(0);
//            Log.i("par",elements + "");
//            JSONArray info = elements.getJSONArray("elements");
//            Log.i("par",info + "");
//            JSONObject information = info.getJSONObject(0);
//            Log.i("par",information + "");
//            JSONObject target = information.getJSONObject(par);
//            Log.i("par",target + "//");
//            int value = (int) target.get("value");
//            Log.i("par",value + "");
//            return value;
            return baseJson.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject(par).getInt("value");
        } catch (JSONException e) {
            Log.i("google parsing error", "Problem parsing json");
        }
        return -500;
    }

    String get_walk_url(Location loc, Location my_loc) {
        String origin = "&origins="+my_loc.x+","+my_loc.y;
        String destination = "&destinations="+loc.x+","+loc.y;
        String mode = "&mode=walking";
        String new_url = url+origin+destination+mode+API_key;
        return new_url;
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


    /**
     * {@link AsyncTask} to perform the network request on a background thread, and then
     * update the UI with the first earthquake in the response.
     */
    public abstract class GoogleAsync extends AsyncTask<Ford, Void, ArrayList<Location>> {


        /**
         * Update
         */
        @Override
        protected void onPostExecute(ArrayList<Location> locs) {
            if (locs == null) {
                return;
            }

            route = (locs);
        }

        /**
         * Returns new URL object from the given string URL.
         */
        private URL createUrl(String stringUrl) {
            URL url = null;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException exception) {
                Log.e(LOG_TAG, "Error with creating URL", exception);
                return null;
            }
            return url;
        }

        /**
         * Make an HTTP request to the given URL and return a String as the response.
         */
        public String makeHttpRequest(String query, String base_link, String api_key, String log_tag){
//        URL link=makeURL(query,base_link,api_key,log_tag);
            URL link = null;
            try {
                link = new URL(base_link);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection urlConnection=null;
            String jsonResponse=null;
            InputStream stream=null;
            if(link!=null){

                try {
                    urlConnection = (HttpURLConnection) link.openConnection();
                    urlConnection.setConnectTimeout(15000);
                    urlConnection.setReadTimeout(10000);
                    urlConnection.setDefaultUseCaches(false);
                    urlConnection.setUseCaches(false);
                    urlConnection.setRequestProperty("Request Method","GET");
                    urlConnection.connect();
                    if(urlConnection.getResponseCode()==HttpURLConnection.HTTP_OK){
                        stream=urlConnection.getInputStream();
                        jsonResponse=readFromStream(stream);

                    }
                    else{
                        Log.i(log_tag,"error code:" + urlConnection.getResponseCode() + "; info: " + urlConnection.getResponseMessage());
                    }
                }
                catch(IOException e){
                    Log.i(log_tag, "problem with connection");
                }
                finally{
                    if(urlConnection!=null){
                        urlConnection.disconnect();
                    }
                    if(stream!=null){
                        try{
                            stream.close();
                        }
                        catch(IOException e){
                            Log.i(log_tag,"problem with inputStream");
                        }
                    }
                }
            }

            return jsonResponse;
        }

        /**
         * Convert the {@link InputStream} into a String which contains the
         * whole JSON response from the server.
         */
        private String readFromStream(InputStream stream)throws IOException{
            if(stream!=null){
                InputStreamReader inReader= new InputStreamReader(stream, Charset.forName("UTF-8"));
                BufferedReader reader= new BufferedReader(inReader);
                StringBuilder response=new StringBuilder("");
                String line=reader.readLine();
                while(line!=null){
                    response.append(line);
                    line=reader.readLine();
                }
                return response.toString();
            }
            return null;
        }

    }
}







