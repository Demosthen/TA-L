package com.example.tal;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import androidx.loader.content.AsyncTaskLoader;

public class ServeAsyncTaskLoader extends AsyncTaskLoader<List<Service>> {
    private String mQuery;
    public ServeAsyncTaskLoader(Context context, String query) {
        super(context);
        mQuery=query;

    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        //Log.i(MapsActivity.LOG_TAG,"Load started");
        forceLoad();
    }

    @Override
    public List<Service> loadInBackground() {
        String jsonResponse=Utils.makeHttpRequest(mQuery);
        ArrayList<Service> serviceType = new ArrayList<>();
        ArrayList<ArrayList> result = new ArrayList<>();
        return new Bird(Service.end, Service.start, Service.end).extractServices(jsonResponse);//Utils.extractDocs(jsonResponse);// TODO: change this line
    }
}
