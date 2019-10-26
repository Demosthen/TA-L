package com.example.tal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class GigCar extends Service {
    double d;
    public GigCar(Location loc, Location my_loc, Location final_dest){
        super(loc, my_loc, final_dest);
        name = "GigCar";
    }
    @Override
    double get_cost(Location loc, Location final_dest) {
        if (d < 6){
            return d*2.50;
        }
        else if (time < 8*60*60){
            return 15*time/3600;
        }
        else{
            Date now = new Date();

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(now);
            //day = calendar.get(Calendar.DAY_OF_WEEK);

            /*if (day == 7 || day == 1){
                return 85;
            }
            else{
                return 69;
            }*/
        }
        return 5;
    }

    @Override
    ArrayList< Service > extractServices(String json){
        return null; //TODO:need to be changed
    }

    @Override
    int get_time(Location loc, Location final_dest) {
        //d=converted value
        //return duration;
        return 5;
    }
}
