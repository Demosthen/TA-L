package com.example.tal;

import java.util.ArrayList;
import java.util.Dictionary;

public abstract class Service{

    static String name; //name of service
    static String appID; //app ID of service 'com.app_name...'
    double cost; //cost to get to final destination
    int time; //time to get to final destination in seconds
    int walk; //time to get from user current location to service location in seconds
    Location loc; //Location.x=longitude, Location.y=latitude; Location of Service
    Location my_loc; //Location of me
    Location final_dest; //Location of final destination of service

    public Service(){

    }

    public Service(Location loc, Location my_loc, Location final_dest){
        this.loc = loc;
        this.my_loc = my_loc;
        this.final_dest = final_dest;
        this.time = get_time(loc,final_dest);
        this.cost = get_cost(loc,final_dest);
        this.walk = get_walk(loc,my_loc);
    }

    abstract double get_cost(Location loc, Location final_dest);
    abstract int get_time(Location loc, Location final_dest);

    int get_walk(Location loc, Location my_loc) {
        return 5;
        //return walking_time;
    }


}
