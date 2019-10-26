package com.example.tal;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
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
    private static String addParam(String origLink, String param, String add){
        if(origLink==null||param==null||add==null){
            Log.i(MapsActivity.LOG_TAG, "problem in addParam");
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
    private static URL makeURL(String base_link,String query) {
        if(query==null){
            Log.i(MapsActivity.LOG_TAG, "makeURL received null query");
            return null;
        }
        try {
            String base= "";//addParam(base_link, "api-key", MapsActivity.API_KEY);

            return new URL(addParam(base, "q", query));
        }
        catch(MalformedURLException e){
            Log.i(MapsActivity.LOG_TAG, "problem making URL");
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
    /*
    public static ArrayList<Document> extractDocs(String json){
        ArrayList<Document> docList= new ArrayList<Document>();
        try{
            JSONObject baseJson= new JSONObject(json);
            JSONObject response= baseJson.getJSONObject("response");
            JSONArray jsonDocs = response.getJSONArray("results");
            for(int i=0; i<jsonDocs.length(); i++){
                JSONObject jsonDoc= jsonDocs.getJSONObject(i);
                String section=jsonDoc.getString("sectionName");
                String title= jsonDoc.getString("webTitle");
                String date= jsonDoc.getString("webPublicationDate");
                date=date.substring(0,date.indexOf("T"));
                String link= jsonDoc.getString("webUrl");
                Document doc= new Document(title,date,section,link);
                if(jsonDoc.has("author")){
                    String author=jsonDoc.getString("author");
                    doc.setAuthor(author);
                }
                docList.add(doc);
            }
        }
        catch(JSONException e){
            Log.i(MainActivity.LOG_TAG, "Problem parsing json");
        }
        return docList;

    }*/
    public static String makeHttpRequest(String query){
        URL link=makeURL("",query);
        HttpURLConnection urlConnection=null;
        String jsonResponse=null;
        InputStream stream=null;
        if(link!=null){

            try {
                urlConnection = (HttpURLConnection) link.openConnection();
                urlConnection.setConnectTimeout(15000);
                urlConnection.setReadTimeout(10000);
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                if(urlConnection.getResponseCode()==200){
                    stream=urlConnection.getInputStream();
                    jsonResponse=readFromStream(stream);

                }
                else{
                    Log.i(MapsActivity.LOG_TAG,"error code:" + urlConnection.getResponseCode());
                }
            }
            catch(IOException e){
                Log.i(MapsActivity.LOG_TAG, "problem with connection");
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
                        Log.i(MapsActivity.LOG_TAG,"problem with inputStream");
                    }
                }
            }
        }

        return jsonResponse;
    }
    /*public static ArrayList<Service> extractServices(String json){
        ArrayList<Service>

    }*/
}
