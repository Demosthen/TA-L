package com.example.tal;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import android.content.AsyncTaskLoader;

public class ServeAsyncTaskLoader extends AsyncTaskLoader< List< Service > > {
    private String mQuery;
    private String mBaseLink;
    private String mApiKey;
    private String mLogTag;

    public ServeAsyncTaskLoader(Context context, String query, String base_link, String api_key, String log_tag) {
        super(context);
        mQuery = query;
        mBaseLink = base_link;
        mApiKey = api_key;
        mLogTag = log_tag;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        Log.i(mLogTag,"Load started");
        forceLoad();
    }

    @Override
    public List< Service > loadInBackground() {
        String jsonResponse = Utils.makeHttpRequest(mQuery,mBaseLink,mApiKey,mLogTag);
//        ArrayList< Service > serviceType = new ArrayList<>();
//        ArrayList< ArrayList > result = new ArrayList<>();
//        ArrayList<Service> output = new Bird(Service.start,Service.start,Service.end).extractServices(jsonResponse);
//        for (int i = 0; i < output.size(); i++) {
//            Log.v("bird",output.get(i).loc+"");
//        }
        ArrayList<Service> output = new ArrayList<>();
        Bird test = new Bird(Service.start,Service.start,Service.end);
        test.name = jsonResponse;
        Log.i("json",jsonResponse);
        output.add(test);
        return output;//Utils.extractDocs(jsonResponse);// TODO: change this line
    }
}
