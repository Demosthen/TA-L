package com.example.tal;

import android.app.Activity;
import android.app.Application;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Austin on 9/6/2017.
 */

public class Utils {
    private static String replaceSpace(String add){
        while(add.contains(" ")){
            add=add.replace(" ", "%20");
        }
        return add;
    }
    private static String addParam(String origLink, String param, String add,String log_tag){
        if(origLink==null||param==null||add==null){
            Log.i(log_tag, "problem in addParam");
            return null;
        }
        add=replaceSpace(add);
        if(origLink.endsWith("?")){
            origLink+=param+"="+add;
        }
        else{
            origLink+="&"+param+"="+add;
        }
        return origLink;
    }
    private static URL makeURL(String query, String base_link, String api_key, String log_tag) {
        if(query==null){
            Log.i(log_tag, "makeURL received null query");
            return null;
        }
        try {
            String base= addParam(base_link, "api-key", api_key,log_tag);

            return new URL(addParam(base, "q", query,log_tag));
        }
        catch(MalformedURLException e){
            Log.i(log_tag, "problem making URL");
            return null;
        }
    }
    private static String readFromStream(InputStream stream)throws IOException{
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

    public static String makeHttpRequest(String query, String base_link, String api_key, String log_tag){
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
    public static StringRequest makeVolleyQueueRequest(String query, String base_link, String api_key, String log_tag){
//      URL link=makeURL(query,base_link,api_key,log_tag);
        URL link = null;
        try {
            link = new URL(base_link);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        StringRequest request = new StringRequest(Request.Method.GET, base_link, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!response.equals(null)) {
                    Log.i("Your Array Response", response);
                } else {
                    Log.i("Your Array Response", "Data Null");
                }
            }
        }
        , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error is ", "" + error);
            }
        }) {

        //This is for Headers If You Needed
        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> params = new HashMap<String, String>();
            params.put("Content-Type", "application/json; charset=UTF-8");
            params.put("Authorization", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJBVVRIIiwidXNlcl9pZCI6IjlhNjVkYjRjLTU4NDctNDU1OS05ZDFmLWIzY2Y5Mjg1ODhmNyIsImRldmljZV9pZCI6IjI5M2ZlYzhjLWE2YTUtMTFlOS05YjViLWEwOTk5YjEwNTM1NSIsImV4cCI6MTU5NDY5MjM4OH0.09T6VCGDt-mWz6oYiGawzl0gJa-a4Fq2Y3qaOqVE8nA");
            params.put("Device-id","121E4567-E89B-12D3-A456-426655440200");
            params.put("App-Version","4.41.0");
            params.put("Location","{latitude:37.77249,longitude:-122.40910,altitude:500,accuracy:100,speed:-1,heading:-1}");
            return params;
        }

        //Pass Your Parameters here
        @Override
        protected Map<String, String> getParams() {
            Map<String, String> params = new HashMap<String, String>();
            params.put("latitude", "37.77184");
            params.put("longitude", "-122.40910");
            params.put("radius", "100");
            return params;
        }
    };
    return request;
    }
}
