package com.example.tal;

public class Location {
    double x, y;

    public Location(double x, double y){
        this.x = x;
        this.y = y;
    }

    //returns a double representing the approximate absolute displacement between two locations in miles
    public static double displacement_between(Location one, Location two) {
        return Math.sqrt(Math.pow((one.x-two.x)*69.172, 2) + Math.pow(((Math.cos(one.x*Math.PI/180)*one.y-(Math.cos(two.x*Math.PI/180)*two.y)))*69.172, 2));
        //uh I hope that was right. Then again we're using small distances so... why don't we just use a planar version...

        /*return 2*asin(sqrt((sin((one.x-two.x)/2))^2 +
                cos(one.x)*cos(two.x)*pow((sin((one.y-two.y)/2), 2)));*/
    }
    public String toString(){
        return "("+Double.toString(x)+", "+Double.toString(y)+")";
    }
}
