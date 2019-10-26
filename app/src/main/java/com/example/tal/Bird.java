package com.example.tal;

public class Bird extends Service {

    @Override
    double get_cost(Location loc, Location final_dest) {
        return 0;
    }

    @Override
    int get_time(Location loc, Location final_dest) {
        return 0;
    }

    @Override
    int get_walk(Location loc, Location my_loc) {
        return 0;
    }
}

