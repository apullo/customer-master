package co.cvdcc.brojekcustomer.api;

import android.content.Context;

//import com.bumptech.glide.request.Request;
import com.google.android.gms.maps.model.LatLng;

import co.cvdcc.brojekcustomer.gmap.GMapDirection;
import co.cvdcc.brojekcustomer.gmap.directions.Directions;
import co.cvdcc.brojekcustomer.gmap.directions.Leg;
import co.cvdcc.brojekcustomer.gmap.directions.Route;
import co.cvdcc.brojekcustomer.gmap.directions.Step;
import co.cvdcc.brojekcustomer.model.MboxLocation;

import java.util.ArrayList;
import java.util.List;


import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by bradhawk on 10/16/2016.
 */

public class MapDirectionAPI {

    public static okhttp3.Call getDirection(LatLng pickUp, LatLng destination) {
        OkHttpClient client = new OkHttpClient();
        GMapDirection gMapDirection = new GMapDirection();

        Request request = new Request.Builder()
                .url(gMapDirection.getUrl(pickUp, destination, GMapDirection.MODE_DRIVING, false))
                .build();

        return client.newCall(request);
    }

    public static Call getDirectionVia(LatLng pickUp, LatLng... destination) {
        OkHttpClient client = new OkHttpClient();
        GMapDirection gMapDirection = new GMapDirection();

        Request request = new Request.Builder()
                .url(gMapDirection.getUrlVia(GMapDirection.MODE_DRIVING, false, pickUp, destination))
                .build();

        return client.newCall(request);
    }

    public static okhttp3.Call getViaDirection(MboxLocation pickUp, ArrayList<MboxLocation> destination) {
        OkHttpClient client = new OkHttpClient();
        GMapDirection gMapDirection = new GMapDirection();

        Request request = new Request.Builder()
                .url(gMapDirection.getViaUrl(pickUp, destination, GMapDirection.MODE_DRIVING, false))
                .build();

        return client.newCall(request);
    }





    public static long getDistance(Context context, String json) {
        long dist = 0;
        if (json != null) {
            Directions directions = new Directions(context);
            List<Route> routes;

            try {
                routes = directions.parse(json);
            } catch (Exception e) {
                e.printStackTrace();
                return -1L;
            }

            for (Route route : routes) {
                for (Leg leg : route.getLegs()) {
                    for (Step step : leg.getSteps()) {
                        dist += step.getDistance().getValue();
                    }
                }
            }

            if (routes.size() == 0) return -1L;

        }
        return dist;
    }

    public static long getTimeDistance(Context context, String json) {
        long time = 0;
        if (json != null) {
            Directions directions = new Directions(context);
            List<Route> routes;

            try {
                routes = directions.parse(json);
            } catch (Exception e) {
                e.printStackTrace();
                return -1L;
            }

            for (Route route : routes) {
                for (Leg leg : route.getLegs()) {
                    for (Step step : leg.getSteps()) {
                        time += step.getDuration().getValue();
                    }
                }
            }

            if (routes.size() == 0) return -1L;

        }
        return time;
    }


}
