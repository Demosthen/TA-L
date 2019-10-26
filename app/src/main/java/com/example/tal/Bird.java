package com.example.tal;

public class Bird extends Service {
    int speed = 15;
    double base = 1;
    double rate = 0.15/60;

    public Bird(Location loc, Location my_loc, Location final_dest) {
        super(loc, my_loc, final_dest);
    }
    @Override
    double get_cost(Location loc, Location final_dest) {
        return base + this.time*rate;
    }

    @Override
    int get_time(Location loc, Location final_dest) {
        double value = 67;
        return (int)(value*(0.000621371)/speed*(3600/5280));
    }
