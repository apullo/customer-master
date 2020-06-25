package co.cvdcc.brojekcustomer.config;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;



public class General {
    public static final String FCM_KEY = "AIzaSyBvfWsS4p-X-MVVnKl0pYfzuGbkFnc7e0s";
    public static final LatLngBounds BOUNDS = new LatLngBounds(
            new LatLng(-7.216001, 0), // southwest
            new LatLng(0, 107.903316)); // northeast
}
