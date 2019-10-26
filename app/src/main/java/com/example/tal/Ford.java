package com.example.tal;

import java.util.ArrayList;

import static java.lang.Math.ceil;

public class Ford extends Service {//before execute this stuff on Ford, calculate bike_dest

    public Ford(Location loc, Location my_loc, Location final_dest){
        super(loc, my_loc, final_dest);
        name = "Ford";
    }
    @Override
    double get_cost(Location loc, Location bike_dest) {
        if (this.time<30*60){
            return 3;
        }
        else{
            return 3+3*Math.ceil((this.time/60-30)/15);
        }
    }

    @Override
    int get_time(Location loc, Location bike_dest) {
        return 5;
        //return from api bike;
    }

    @Override
    ArrayList< Service > extractServices(String json){
        return null; //TODO:need to be changed
    }

    int get_extra_time(Location bike_dest, Location final_dest){
        return 5;
        //return walking value;
    }


}
