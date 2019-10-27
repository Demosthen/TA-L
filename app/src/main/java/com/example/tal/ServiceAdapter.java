package com.example.tal;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ServiceAdapter extends ArrayAdapter {
    public ServiceAdapter(@NonNull Context context, @NonNull List services) {
        super(context, 0, services);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent,false);
        }
        final Service currentService = (Service) getItem(position);

        TextView serviceView=(TextView) convertView.findViewById(R.id.service_name);
        TextView ETAView = (TextView) convertView.findViewById(R.id.ETA);
        TextView EPriceView = (TextView) convertView.findViewById(R.id.EP);
        Button orderButton =(Button) convertView.findViewById(R.id.order);
        serviceView.setText(currentService.name);
        ETAView.setText(currentService.time);
        EPriceView.setText(Double.toString(currentService.cost));
        orderButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent launchIntent = getContext().getPackageManager().getLaunchIntentForPackage(Service.appID);
                if (launchIntent != null) {
                    getContext().startActivity(launchIntent);//null pointer check in case package name was not found
                }
            }
        });
        return convertView;
    }
}
