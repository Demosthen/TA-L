package com.example.tal;

public class GigCar extends Service {
    double d;

    @Override
    double get_cost(Location loc, Location final_dest) {
        if (d < 6){
            return d*2.50;
        }
        else if (time < 8*60*60){
            return 15*time/3600;
        }
        else{
            if (date == Sat/Sun){
                return 85;
            }
            else{
                return 69;
            }
        }
    }

    @Override
    int get_time(Location loc, Location final_dest) {
        d=converted value
        return duration;
    }
}
