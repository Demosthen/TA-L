package com.example.tal;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.AsyncTaskLoader;

import static com.example.tal.MapsActivity.LOG_TAG;

public class ServeAsyncTaskLoader extends AsyncTaskLoader< HashMap< String,List<Service> > > {
    private String mQuery;
    private String birdLink = "https://data.lime.bike/api/partners/v1/gbfs/los_angeles/free_bike_status";
    private String bikeShareLink = "https://bikeshare.metro.net/stations/json";
    private String mApiKey;
    private String mLogTag;
    private HashMap dataRepository = new HashMap();

    public ServeAsyncTaskLoader(Context context) {
        super(context);
//        mQuery = query;
//        mBaseLink = base_link;
//        mApiKey = api_key;
//        mLogTag = log_tag;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        Log.i(mLogTag,"Load started");
        forceLoad();
    }

    @Override
    public HashMap< String,List<Service> > loadInBackground() {
//        ArrayList< Service > serviceType = new ArrayList<>();
//        ArrayList< ArrayList > result = new ArrayList<>();
//        ArrayList<Service> output = new Bird(Service.start,Service.start,Service.end).extractServices(jsonResponse);
//        for (int i = 0; i < output.size(); i++) {
//            Log.v("bird",output.get(i).loc+"");
//        }
        //creating ford objects
        String jsonFordResponse = Utils.makeHttpRequest(mQuery,bikeShareLink,mApiKey,mLogTag);
        Ford ford = new Ford(Service.start,Service.start,Service.start,Service.end);
        Log.i("json",jsonFordResponse);
        dataRepository.put(ford.name,ford.extractServices(jsonFordResponse));
        //creating bird objects
        String jsonBirdResponse = Utils.makeHttpRequest(mQuery,birdLink,mApiKey,mLogTag);
        Bird bird = new Bird(Service.start,Service.start,Service.end);
        Log.i("json",jsonBirdResponse);
        dataRepository.put(bird.name,bird.extractServices(jsonBirdResponse));

        return dataRepository;
    }
}
