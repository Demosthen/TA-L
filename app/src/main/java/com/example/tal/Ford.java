package com.example.tal;

import static java.lang.Math.ceil;

public class Ford extends Service {

    @Override
    double get_cost(Location loc, Location final_dest) {
        if (this.time<30*60){
            return 3;
        }
        else{
            return 3+3*Math.ceil((this.time/60-30)/15);
        }
    }

    @Override
    int get_time(Location loc, Location final_dest) {
        return from api bike;
    }


}