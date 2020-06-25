package co.cvdcc.brojekcustomer.mRideCar;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import co.cvdcc.brojekcustomer.MangJekApplication;
import co.cvdcc.brojekcustomer.R;
import co.cvdcc.brojekcustomer.api.MapDirectionAPI;
import co.cvdcc.brojekcustomer.api.ServiceGenerator;
import co.cvdcc.brojekcustomer.api.service.BookService;
import co.cvdcc.brojekcustomer.gmap.directions.Directions;
import co.cvdcc.brojekcustomer.gmap.directions.Route;
import co.cvdcc.brojekcustomer.home.submenu.TopUpActivity;
import co.cvdcc.brojekcustomer.mMart.PlaceAutocompleteAdapter;
import co.cvdcc.brojekcustomer.model.Driver;
import co.cvdcc.brojekcustomer.model.Fitur;
import co.cvdcc.brojekcustomer.model.User;
import co.cvdcc.brojekcustomer.model.json.book.GetNearRideCarRequestJson;
import co.cvdcc.brojekcustomer.model.json.book.GetNearRideCarResponseJson;
import co.cvdcc.brojekcustomer.model.json.book.RequestRideCarRequestJson;
import co.cvdcc.brojekcustomer.utils.Log;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static co.cvdcc.brojekcustomer.config.General.BOUNDS;

/**
 * Created by bradhawk on 10/26/2016.
 */

public class RideCarActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String FITUR_KEY = "FiturKey";
    private static final String TAG = "RideCarActivity";
    private static final int REQUEST_PERMISSION_LOCATION = 991;
    @BindView(R.id.rideCar_title)
    TextView title;
    @BindView(R.id.rideCar_pickUpContainer)
    LinearLayout setPickUpContainer;
    @BindView(R.id.rideCar_destinationContainer)
    LinearLayout setDestinationContainer;
    @BindView(R.id.rideCar_pickUpButton)
    Button setPickUpButton;
    @BindView(R.id.rideCar_destinationButton)
    Button setDestinationButton;
    @BindView(R.id.rideCar_pickUpText)
    AutoCompleteTextView pickUpText;
    @BindView(R.id.rideCar_destinationText)
    AutoCompleteTextView destinationText;
    @BindView(R.id.rideCar_detail)
    LinearLayout detail;
    @BindView(R.id.rideCar_distance)
    TextView distanceText;
    @BindView(R.id.rideCar_price)
    TextView priceText;
    @BindView(R.id.rideCar_paymentGroup)
    RadioGroup paymentGroup;
    @BindView(R.id.rideCar_mPayPayment)
    RadioButton mPayButton;
    @BindView(R.id.rideCar_cashPayment)
    RadioButton cashButton;
    @BindView(R.id.rideCar_topUp)
    Button topUpButton;
    @BindView(R.id.rideCar_order)
    Button orderButton;
    @BindView(R.id.rideCar_mPayBalance)
    TextView mPayBalanceText;
    @BindView(R.id.rideCar_select_car_container)
    RelativeLayout carSelectContainer;
    @BindView(R.id.rideCar_select_car)
    ImageView carSelect;
    @BindView(R.id.rideCar_select_car_text)
    TextView carSelectText;
    @BindView(R.id.rideCar_select_ride_container)
    RelativeLayout rideSelectContainer;
    @BindView(R.id.rideCar_select_ride)
    ImageView rideSelect;
    @BindView(R.id.ride_car_select_ride_text)
    TextView rideSelectText;
    @BindView(R.id.discountText)
    TextView discountText;

    private GoogleMap gMap;
    private GoogleApiClient googleApiClient;
    private Location lastKnownLocation;
    private LatLng pickUpLatLang;
    private LatLng destinationLatLang;
    private Polyline directionLine;
    private Marker pickUpMarker;
    private Marker destinationMarker;
    private List<Driver> driverAvailable;
    private List<Marker> driverMarkers;
    private Realm realm;
    private Fitur designedFitur;
    private double jarak;
    private double timeDistance = 0;
    private long harga;
    private long saldoMpay;
    private boolean isMapReady = false;
    //    private long minPrice = 0 ;
    private PlaceAutocompleteAdapter mAdapter;
//    DiskonMpay diskonMpay;

    //    private static final LatLngBounds BOUNDS = new LatLngBounds(
//            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));
//    private static final LatLngBounds BOUNDS = new LatLngBounds(
//            new LatLng(-10.104765, 88.933331), // southwest
//            new LatLng(8.015870, 142.285892)); // northeast


    private okhttp3.Callback updateRouteCallback = new okhttp3.Callback() {
        @Override
        public void onFailure(okhttp3.Call call, IOException e) {

        }

        @Override
        public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
            if (response.isSuccessful()) {
                final String json = response.body().string();
                final long distance = MapDirectionAPI.getDistance(RideCarActivity.this, json);
                final long time = MapDirectionAPI.getTimeDistance(RideCarActivity.this, json);
                if (distance >= 0) {
                    RideCarActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateLineDestination(json);
                            updateDistance(distance);
                            timeDistance = time/60;
                        }
                    });
                }
            }
        }
    };

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_car);
        ButterKnife.bind(this);

//        diskonMpay = MangJekApplication.getInstance(this).getDiskonMpay();
        setPickUpContainer.setVisibility(View.GONE);
        setDestinationContainer.setVisibility(View.GONE);
        detail.setVisibility(View.GONE);

        User userLogin = MangJekApplication.getInstance(this).getLoginUser();
        saldoMpay = userLogin.getmPaySaldo();

        pickUpText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                setPickUpContainer.setVisibility((b) ? View.VISIBLE : View.GONE);
            }
        });

        destinationText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                setDestinationContainer.setVisibility((b) ? View.VISIBLE : View.GONE);
            }
        });

        setPickUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPickUpClick();


            }
        });

        setDestinationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDestinationClick();

            }
        });

        topUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), TopUpActivity.class));
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.rideCar_mapView);
        mapFragment.getMapAsync(this);

        driverAvailable = new ArrayList<>();
        driverMarkers = new ArrayList<>();

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .build();
        }

        realm = Realm.getDefaultInstance();

        Intent intent = getIntent();
        int fiturId = intent.getIntExtra(FITUR_KEY, -1);

        Log.e("FITUR_ID", fiturId+"");
        if (fiturId != -1)
            designedFitur = realm.where(Fitur.class).equalTo("idFitur", fiturId).findFirst();

        RealmResults<Fitur> fiturs = realm.where(Fitur.class).findAll();

        for(Fitur fitur : fiturs){
            Log.e("ID_FITUR",fitur.getIdFitur()+" "+fitur.getFitur()+" "+fitur.getBiayaAkhir());
        }

        setupFitur();

        discountText.setText("Diskon "+designedFitur.getDiskon()+" jika menggunakan SALDO");
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOrderButtonClick();
            }
        });

        carSelectContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectCar();
            }
        });

        rideSelectContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectRide();
            }
        });

        paymentGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (paymentGroup.getCheckedRadioButtonId()) {
                    case R.id.rideCar_mPayPayment:
                        long biayaTotal = (long)(harga*designedFitur.getBiayaAkhir());
                        String formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(biayaTotal);
                        String formattedText = String.format(Locale.US, "Rp. %s ,-", formattedTotal);
                        priceText.setText(formattedText);
                        break;
                    case R.id.rideCar_cashPayment:
                        biayaTotal = harga;
                        formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(biayaTotal);
                        formattedText = String.format(Locale.US, "Rp. %s ,-", formattedTotal);
                        priceText.setText(formattedText);
                        break;

                }
            }
        });

        setupAutocompleteTextView();
    }

    private void setupAutocompleteTextView() {
        mAdapter = new PlaceAutocompleteAdapter(this, googleApiClient, BOUNDS, null);
        pickUpText.setAdapter(mAdapter);
        pickUpText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InputMethodManager inputManager =
                        (InputMethodManager) RideCarActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(pickUpText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                AutocompletePrediction item = mAdapter.getItem(position);

//                LatLng latLng = getLocationFromAddress(item.getFullText(null).toString());
//                gMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//                onPickUpClick();

                getLocationFromPlaceId(item.getPlaceId(), new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(@NonNull PlaceBuffer places) {
                        final Place place = places.get(0);
                        LatLng latLng = place.getLatLng();
                        if(latLng != null) {
                            gMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                            onPickUpClick();
                        }
                    }
                });

            }
        });
        destinationText.setAdapter(mAdapter);
        destinationText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InputMethodManager inputManager =
                        (InputMethodManager) RideCarActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(destinationText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                AutocompletePrediction item = mAdapter.getItem(position);
//                LatLng latLng = getLocationFromAddress(item.getFullText(null).toString());
//                gMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//                onDestinationClick();
                getLocationFromPlaceId(item.getPlaceId(), new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(@NonNull PlaceBuffer places) {
                        final Place place = places.get(0);
                        LatLng latLng = place.getLatLng();
                        if(latLng != null) {
                            gMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                            onDestinationClick();
                        }
                    }
                });
            }
        });

    }

    private void getLocationFromPlaceId(String placeId, ResultCallback<PlaceBuffer> callback) {
        Places.GeoDataApi.getPlaceById(googleApiClient, placeId).setResultCallback(callback);
    }

    private LatLng getLocationFromAddress(String strAddress) {
        Log.e("LOCATION", strAddress);
        strAddress = "Garut, Jawa Barat, Indonesia";
        Log.e("LOCATION", strAddress);
        Geocoder coder = new Geocoder(this, getResources().getConfiguration().locale);
        List<Address> address;
        LatLng p1;

        try {
            address = coder.getFromLocationName(strAddress, 5);

            Log.e("LOCATION", address.toString()+" Length : "+address.size());
            if (address == null || address.isEmpty()) {
                return null;
            }

            int tentatives = 0;
            while (address.size()==0 && (tentatives < 10)) {
                address = coder.getFromLocationName(strAddress, 1);
                tentatives ++;
            }

            if(address.size() > 0){
                Log.e("LOC", "reverse Geocoding : locationName " + strAddress + "Latitude " + address.get(0).getLatitude() );
                return new LatLng(address.get(0).getLatitude(), address.get(0).getLongitude());
            }else{
                Log.e("LOC", "Not Found!");
                return new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            }



//            Address location = address.get(0);
//            location.getLatitude();
//            location.getLongitude();
//
//            p1 = new LatLng(location.getLatitude(), location.getLongitude());
//            return p1;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        User userLogin = MangJekApplication.getInstance(this).getLoginUser();
        String formattedText = String.format(Locale.US, "Rp. %s ,-",
                NumberFormat.getNumberInstance(Locale.US).format(userLogin.getmPaySaldo()));
        mPayBalanceText.setText(formattedText);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateLastLocation(true);
            } else {
                // TODO: 10/15/2016 Tell user to use GPS
            }
        }
    }

    private void cancelOrder(){

    }



    private void selectCar() {
        carSelect.setSelected(true);
        carSelectText.setSelected(true);
        rideSelect.setSelected(false);
        rideSelectText.setSelected(false);

        designedFitur = realm.where(Fitur.class).equalTo("idFitur", 2).findFirst();

//        minPrice = designedFitur.getBiaya_minimum();
        updateFitur();
    }

    private void selectRide() {
        rideSelect.setSelected(true);
        rideSelectText.setSelected(true);
        carSelect.setSelected(false);
        carSelectText.setSelected(false);

        designedFitur = realm.where(Fitur.class).equalTo("idFitur", 1).findFirst();
//        minPrice = designedFitur.getBiaya_minimum();

        updateFitur();
    }

    private void updateFitur() {
        driverAvailable.clear();

        for (Marker m : driverMarkers) {
            m.remove();
        }
        driverMarkers.clear();

        if (designedFitur.getIdFitur() == 1) {
            title.setText(R.string.home_mRide);
        } else if (designedFitur.getIdFitur() == 2) {
            title.setText(R.string.home_mCar);
        }

        if (isMapReady) updateLastLocation(false);
    }

    private void setupFitur() {
        if (designedFitur.getIdFitur() == 1) {
            title.setText(R.string.home_mRide);
            selectRide();
        } else if (designedFitur.getIdFitur() == 2) {
            title.setText(R.string.home_mCar);
            selectCar();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        updateLastLocation(true);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.getUiSettings().setMyLocationButtonEnabled(true);

        isMapReady = true;

        updateLastLocation(true);
    }

    private void updateLastLocation(boolean move) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_LOCATION);
            return;
        }
        lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(
                googleApiClient);
        gMap.setMyLocationEnabled(true);

        if (lastKnownLocation != null) {
            if (move) {
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), 15f)
                );

                gMap.animateCamera(CameraUpdateFactory.zoomTo(15f));
            }
            fetchNearDriver();
        }
    }

    private void fetchNearDriver(double latitude, double longitude) {
        if (lastKnownLocation != null) {
            User loginUser = MangJekApplication.getInstance(this).getLoginUser();

            BookService service = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());
            GetNearRideCarRequestJson param = new GetNearRideCarRequestJson();
            param.setLatitude(latitude);
            param.setLongitude(longitude);

            if (designedFitur.getIdFitur() == 1) {
                service.getNearRide(param).enqueue(new Callback<GetNearRideCarResponseJson>() {
                    @Override
                    public void onResponse(Call<GetNearRideCarResponseJson> call, Response<GetNearRideCarResponseJson> response) {
                        if (response.isSuccessful()) {
                            driverAvailable = response.body().getData();
                            createMarker();
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<GetNearRideCarResponseJson> call, Throwable t) {

                    }
                });
            } else if (designedFitur.getIdFitur() == 2) {
                service.getNearCar(param).enqueue(new Callback<GetNearRideCarResponseJson>() {
                    @Override
                    public void onResponse(Call<GetNearRideCarResponseJson> call, Response<GetNearRideCarResponseJson> response) {
                        if (response.isSuccessful()) {
                            driverAvailable = response.body().getData();
                            createMarker();
                        }
                    }

                    @Override
                    public void onFailure(Call<GetNearRideCarResponseJson> call, Throwable t) {

                    }
                });
            }
        }
    }

    private void fetchNearDriver() {
        if (lastKnownLocation != null) {
            User loginUser = MangJekApplication.getInstance(this).getLoginUser();

            BookService service = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());
            GetNearRideCarRequestJson param = new GetNearRideCarRequestJson();
            param.setLatitude(lastKnownLocation.getLatitude());
            param.setLongitude(lastKnownLocation.getLongitude());

            if (designedFitur.getIdFitur() == 1) {
                service.getNearRide(param).enqueue(new Callback<GetNearRideCarResponseJson>() {
                    @Override
                    public void onResponse(Call<GetNearRideCarResponseJson> call, Response<GetNearRideCarResponseJson> response) {
                        if (response.isSuccessful()) {
                            driverAvailable = response.body().getData();
                            createMarker();
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<GetNearRideCarResponseJson> call, Throwable t) {

                    }
                });
            } else if (designedFitur.getIdFitur() == 2) {
                service.getNearCar(param).enqueue(new Callback<GetNearRideCarResponseJson>() {
                    @Override
                    public void onResponse(Call<GetNearRideCarResponseJson> call, Response<GetNearRideCarResponseJson> response) {
                        if (response.isSuccessful()) {
                            driverAvailable = response.body().getData();
                            createMarker();
                        }
                    }

                    @Override
                    public void onFailure(Call<GetNearRideCarResponseJson> call, Throwable t) {

                    }
                });
            }
        }
    }

    private void createMarker() {
        if (!driverAvailable.isEmpty()) {
            for (Marker m : driverMarkers) {
                m.remove();
            }
            driverMarkers.clear();

            for (Driver driver : driverAvailable) {
                LatLng currentDriverPos = new LatLng(driver.getLatitude(), driver.getLongitude());
                if (designedFitur.getIdFitur() == 1) {
                    driverMarkers.add(
                            gMap.addMarker(new MarkerOptions()
                                    .position(currentDriverPos)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_m_ride_pin)))
                    );
                } else {
                    driverMarkers.add(
                            gMap.addMarker(new MarkerOptions()
                                    .position(currentDriverPos)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_m_car_pin)))
                    );
                }
            }
        }
    }

    private void onOrderButtonClick() {
        switch (paymentGroup.getCheckedRadioButtonId()) {
            case R.id.rideCar_mPayPayment:
                if (driverAvailable.isEmpty()) {
                    AlertDialog dialog = new AlertDialog.Builder(RideCarActivity.this)
                            .setMessage("Mohon maaf, tidak ada driver disekitar Anda.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .create();
                    dialog.show();
                } else {
                    RequestRideCarRequestJson param = new RequestRideCarRequestJson();
                    User userLogin = MangJekApplication.getInstance(this).getLoginUser();
                    param.setIdPelanggan(userLogin.getId());
                    param.setOrderFitur(String.valueOf(designedFitur.getIdFitur()));
                    param.setStartLatitude(pickUpLatLang.latitude);
                    param.setStartLongitude(pickUpLatLang.longitude);
                    param.setEndLatitude(destinationLatLang.latitude);
                    param.setEndLongitude(destinationLatLang.longitude);
                    param.setJarak(this.jarak);
                    param.setHarga((long)(this.harga*designedFitur.getBiayaAkhir()));
                    param.setAlamatAsal(pickUpText.getText().toString());
                    param.setAlamatTujuan(destinationText.getText().toString());

                    Log.e("M-PAY", "used");
                    param.setPakaiMpay(1);


                    Intent intent = new Intent(RideCarActivity.this, WaitingActivity.class);
                    intent.putExtra(WaitingActivity.REQUEST_PARAM, param);
                    intent.putExtra(WaitingActivity.DRIVER_LIST, (ArrayList) driverAvailable);
                    intent.putExtra("time_distance", timeDistance);
                    startActivity(intent);
                }


                break;
            case R.id.rideCar_cashPayment:
                if (driverAvailable.isEmpty()) {
                    AlertDialog dialog = new AlertDialog.Builder(RideCarActivity.this)
                            .setMessage("Mohon maaf, tidak ada driver disekitar Anda.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .create();
                    dialog.show();
                } else {
                    RequestRideCarRequestJson param = new RequestRideCarRequestJson();
                    User userLogin = MangJekApplication.getInstance(this).getLoginUser();
                    param.setIdPelanggan(userLogin.getId());
                    param.setOrderFitur(String.valueOf(designedFitur.getIdFitur()));
                    param.setStartLatitude(pickUpLatLang.latitude);
                    param.setStartLongitude(pickUpLatLang.longitude);
                    param.setEndLatitude(destinationLatLang.latitude);
                    param.setEndLongitude(destinationLatLang.longitude);
                    param.setJarak(this.jarak);
                    param.setHarga(this.harga);
                    param.setAlamatAsal(pickUpText.getText().toString());
                    param.setAlamatTujuan(destinationText.getText().toString());


                    Log.e("M-PAY", "not using m pay");


                    Intent intent = new Intent(RideCarActivity.this, WaitingActivity.class);
                    intent.putExtra(WaitingActivity.REQUEST_PARAM, param);
                    intent.putExtra(WaitingActivity.DRIVER_LIST, (ArrayList) driverAvailable);
                    intent.putExtra("time_distance", timeDistance);
                    startActivity(intent);
                }
                break;
        }
    }

    private void onDestinationClick() {
        if (destinationMarker != null) destinationMarker.remove();
        LatLng centerPos = gMap.getCameraPosition().target;
        destinationMarker = gMap.addMarker(new MarkerOptions()
                .position(centerPos)
                .title("Destination")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_loc_blue)));
        destinationLatLang = centerPos;

        fillAddress(destinationText, destinationLatLang);

        requestRoute();
    }

    private void onPickUpClick() {
        if (pickUpMarker != null) pickUpMarker.remove();
        LatLng centerPos = gMap.getCameraPosition().target;
        pickUpMarker = gMap.addMarker(new MarkerOptions()
                .position(centerPos)
                .title("Pick Up")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_loc_blue)));
        pickUpLatLang = centerPos;

        fillAddress(pickUpText, pickUpLatLang);

        destinationText.requestFocus();
        fetchNearDriver(pickUpLatLang.latitude, pickUpLatLang.longitude);
        requestRoute();
    }

    private void requestRoute() {
        if (pickUpLatLang != null && destinationLatLang != null) {
            MapDirectionAPI.getDirection(pickUpLatLang, destinationLatLang).enqueue(updateRouteCallback);
        }
    }

    private void fillAddress(final EditText editText, final LatLng latLng) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Geocoder geocoder = new Geocoder(RideCarActivity.this, Locale.getDefault());
                    final List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    RideCarActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!addresses.isEmpty()) {
                                if (addresses.size() > 0) {
                                    String address = addresses.get(0).getAddressLine(0);
                                    editText.setText(address);
                                }
                            } else {
                                editText.setText(R.string.text_addressNotAvailable);
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void updateLineDestination(String json) {
        Directions directions = new Directions(RideCarActivity.this);
        try {
            List<Route> routes = directions.parse(json);

            if (directionLine != null) directionLine.remove();
            if (routes.size() > 0) {
                directionLine = gMap.addPolyline((new PolylineOptions())
                        .addAll(routes.get(0).getOverviewPolyLine())
                        .color(ContextCompat.getColor(RideCarActivity.this, R.color.directionLine))
                        .width(5));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateDistance(long distance) {
        detail.setVisibility(View.VISIBLE);
        float km = ((float) distance) / 1000f;

        this.jarak = km;

        String format = String.format(Locale.US, "Jarak (%.1f Km)", km);
        distanceText.setText(format);

        long biayaTotal = (long) (designedFitur.getBiaya() * km);

        if (biayaTotal % 1000 != 0)
            biayaTotal = (1000 - (biayaTotal % 1000)) + biayaTotal;

        this.harga = biayaTotal;
        if(biayaTotal < designedFitur.getBiaya_minimum()){
            this.harga = designedFitur.getBiaya_minimum();
            biayaTotal = designedFitur.getBiaya_minimum();
        }

        if(mPayButton.isChecked()){
            Log.e("MPAY","Biaya total :"+biayaTotal+" kali "+designedFitur.getBiayaAkhir());
            biayaTotal *= designedFitur.getBiayaAkhir();

        }

        String formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(biayaTotal);
        String formattedText = String.format(Locale.US, "Rp. %s ,-", formattedTotal);
        priceText.setText(formattedText);


        if(saldoMpay < (harga*designedFitur.getBiayaAkhir())){
            mPayButton.setEnabled(false);
            cashButton.toggle();
        }else {
            mPayButton.setEnabled(true);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
