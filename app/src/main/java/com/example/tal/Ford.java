package com.example.tal;

import static java.lang.Math.ceil;

public class Ford extends Service {//before execute this stuff on Ford, calculate bike_dest

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
        String origin ="&origins="+loc.x+","+loc.y;
        String destination = "&destinations="+bike_dest.x+","+bike_dest.y;
        String mode = "&mode=bicycling";
        url=
        return from api bike;
    }

    int get_extra_time(Location bike_dest, Location final_dest){
        return walking value;
    }


}
