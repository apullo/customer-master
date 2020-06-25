package co.cvdcc.brojekcustomer.home.submenu.history;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
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
import com.makeramen.roundedimageview.RoundedImageView;

import co.cvdcc.brojekcustomer.MangJekApplication;
import co.cvdcc.brojekcustomer.R;
import co.cvdcc.brojekcustomer.api.FCMHelper;
import co.cvdcc.brojekcustomer.api.MapDirectionAPI;
import co.cvdcc.brojekcustomer.api.ServiceGenerator;
import co.cvdcc.brojekcustomer.api.service.BookService;
import co.cvdcc.brojekcustomer.api.service.UserService;
import co.cvdcc.brojekcustomer.gmap.directions.Directions;
import co.cvdcc.brojekcustomer.gmap.directions.Route;
import co.cvdcc.brojekcustomer.home.ChatActivity;
import co.cvdcc.brojekcustomer.home.submenu.history.details.MMartDetailActivity;
import co.cvdcc.brojekcustomer.home.submenu.history.details.MSendDetailActivity;
import co.cvdcc.brojekcustomer.mRideCar.RateDriverActivity;
import co.cvdcc.brojekcustomer.model.ItemHistory;
import co.cvdcc.brojekcustomer.model.LokasiDriver;
import co.cvdcc.brojekcustomer.model.Transaksi;
import co.cvdcc.brojekcustomer.model.User;
import co.cvdcc.brojekcustomer.model.json.book.LiatLokasiDriverResponse;
import co.cvdcc.brojekcustomer.model.json.fcm.CancelBookRequestJson;
import co.cvdcc.brojekcustomer.model.json.fcm.CancelBookResponseJson;
import co.cvdcc.brojekcustomer.model.json.fcm.DriverResponse;
import co.cvdcc.brojekcustomer.model.json.fcm.FCMMessage;
import co.cvdcc.brojekcustomer.utils.Log;
import co.cvdcc.brojekcustomer.utils.db.DBHandler;
import co.cvdcc.brojekcustomer.utils.db.Queries;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static co.cvdcc.brojekcustomer.config.General.FCM_KEY;
import static co.cvdcc.brojekcustomer.model.FCMType.ORDER;
import static co.cvdcc.brojekcustomer.model.ResponseCode.ACCEPT;
import static co.cvdcc.brojekcustomer.model.ResponseCode.FINISH;
import static co.cvdcc.brojekcustomer.model.ResponseCode.REJECT;
import static co.cvdcc.brojekcustomer.model.ResponseCode.START;
import static co.cvdcc.brojekcustomer.service.MangJekMessagingService.BROADCAST_ORDER;

public class HistoryDetailActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "HistoryDetailActivity";

    @BindView(R.id.rideCar_title)
    TextView title;
    @BindView(R.id.rideCar_pickUpText)
    EditText pickUpText;
    @BindView(R.id.rideCar_destinationText)
    EditText destinationText;
    @BindView(R.id.rideCar_distance)
    TextView distanceText;
    @BindView(R.id.rideCar_price)
    TextView priceText;
    @BindView(R.id.driver_image)
    RoundedImageView driverImage;
    @BindView(R.id.driver_name)
    TextView driverName;
    @BindView(R.id.order_number)
    TextView orderNumber;
    @BindView(R.id.driver_police_number)
    TextView driverPoliceNumber;
    @BindView(R.id.driver_car)
    TextView driverCar;
    @BindView(R.id.driver_arriving_time)
    TextView driverArrivingTime;
    @BindView(R.id.chat_driver)
    ImageView chatDriver;
    @BindView(R.id.call_driver)
    ImageView callDriver;
    @BindView(R.id.cancelBook)
    CardView cancelBook;
    @BindView(R.id.splitter)
    LinearLayout sparatorLayout;
    @BindView(R.id.communication_layout)
    LinearLayout communicationLayout;

    @BindView(R.id.historyDetail_detail)
    Button detailButton;

    private GoogleMap gMap;
    private boolean isMapReady = false;
    private Location lastKnownLocation;
    private static final int REQUEST_PERMISSION_LOCATION = 991;
    private static final int REQUEST_PERMISSION_CALL = 992;
    private GoogleApiClient googleApiClient;
    private LatLng pickUpLatLng;
    private LatLng destinationLatLng;
    private Polyline directionLine;
    private Marker pickUpMarker;
    private Marker destinationMarker;
    private Marker driverMarker;
    private Context context;
    Bundle orderBundle;
    private boolean isCancelable = true;
    //    Driver driver;
//    DriverRequest request;
    User loginUser;
    Realm realm;
    ItemHistory transaction;
    private boolean isCompleted;

    private BookService bookService;
    private long delayInMillis = 10000;
    private Handler handler;
    private Runnable updateDriverRunnable = new Runnable() {
        @Override
        public void run() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.d(TAG, "Fire to Update!");
                        Response<LiatLokasiDriverResponse> response = bookService.liatLokasiDriver(transaction.id_driver).execute();
                        if (!response.isSuccessful()) throw new Exception("Connection failed.");
                        if (response.body().getData().isEmpty())
                            throw new Exception("No Driver found.");
                        LokasiDriver lokasi = response.body().getData().get(0);
                        final LatLng latLng = new LatLng(lokasi.getLatitude(), lokasi.getLongitude());
                        Log.d(TAG, "Latitude = " + latLng.latitude + " Longitude = " + latLng.longitude);
                        HistoryDetailActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateDriverMarker(latLng);
                            }
                        });
                    } catch (Exception e) {
                        Log.d(TAG, "Error = " + e.getLocalizedMessage());
                        e.printStackTrace();
                    } finally {
                        handler.postDelayed(updateDriverRunnable, delayInMillis);
                    }
                }
            }).start();
        }
    };

    private okhttp3.Callback updateRouteCallback = new okhttp3.Callback() {
        @Override
        public void onFailure(okhttp3.Call call, IOException e) {

        }

        @Override
        public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
            if (response.isSuccessful()) {
                final String json = response.body().string();
                final long distance = MapDirectionAPI.getDistance(HistoryDetailActivity.this, json);
                if (distance >= 0) {
                    HistoryDetailActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateLineDestination(json);
//                            updateDistance(distance);
                        }
                    });
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detail);
        ButterKnife.bind(this);


        context = getApplicationContext();
        realm = Realm.getDefaultInstance();

//        readTransaction();

        loginUser = MangJekApplication.getInstance(HistoryDetailActivity.this).getLoginUser();


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.rideCar_mapView);
        mapFragment.getMapAsync(this);

        transaction = (ItemHistory) getIntent().getSerializableExtra("transaction");
        isCompleted = getIntent().getBooleanExtra("isCompleted", false);
//        driver = (Driver) getIntent().getSerializableExtra("driver");
//        request = (DriverRequest) getIntent().getSerializableExtra("request");
        Log.e("DATA DRIVER", transaction.nama_depan_driver + " " + transaction.nama_belakang_driver);
        Log.e("DATA REQUEST", transaction.alamat_asal + " to " + transaction.alamat_tujuan);
        if (transaction.status.equals("start")) {
            isCancelable = false;
        }

        String fitur = transaction.order_fitur;
        if(fitur.equalsIgnoreCase("m-send")
                || fitur.equalsIgnoreCase("m-mart")) {
            detailButton.setVisibility(View.VISIBLE);
        } else {
            detailButton.setVisibility(View.GONE);
        }

        //|| fitur.equalsIgnoreCase("m-food") || fitur.equalsIgnoreCase("m-service")

        detailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fitur = transaction.order_fitur;
                if(fitur.equalsIgnoreCase("m-send")) {
                    Intent intent = new Intent(HistoryDetailActivity.this, MSendDetailActivity.class);
                    intent.putExtra(MSendDetailActivity.ID_TRANSAKSI, transaction.id_transaksi);
                    startActivity(intent);
                } else if(fitur.equalsIgnoreCase("m-mart")) {
                    Intent intent = new Intent(HistoryDetailActivity.this, MMartDetailActivity.class);
                    intent.putExtra(MMartDetailActivity.ID_TRANSAKSI, transaction.id_transaksi);
                    startActivity(intent);
                } else if(fitur.equalsIgnoreCase("m-food")) {

                } else if(fitur.equalsIgnoreCase("m-service")) {

                }
            }
        });
        
        title.setText(transaction.order_fitur);

        pickUpText.setText(transaction.alamat_asal);
        destinationText.setText(transaction.alamat_tujuan);
        String format = String.format(Locale.US, "Jarak (%.1f Km)", transaction.jarak);
        distanceText.setText(format);

        String formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(transaction.harga);
        String formattedText = String.format(Locale.US, "Rp %s ,-", formattedTotal);
        priceText.setText(formattedText);

        Glide.with(getApplicationContext()).load(transaction.foto).into(driverImage);
        driverName.setText(transaction.nama_depan_driver + " " + transaction.nama_belakang_driver);
        orderNumber.setText("Order no. " + transaction.id_transaksi);
        driverPoliceNumber.setText(transaction.nomor_kendaraan);
        driverCar.setText(transaction.merek + " " + transaction.tipe + "(" + transaction.warna + ")");
        driverArrivingTime.setText("Driver arriving in " + 0 + " min"); // fill this

        pickUpLatLng = new LatLng(transaction.start_latitude, transaction.start_longitude);
        destinationLatLng = new LatLng(transaction.end_latitude, transaction.end_longitude);

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }


        chatDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Chat with driver", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("reg_id", transaction.reg_id);
                startActivity(intent);
            }
        });

        callDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HistoryDetailActivity.this);
                alertDialogBuilder.setTitle("Call driver");
                alertDialogBuilder.setMessage("Do you want to call " + transaction.no_telepon + "?");
                alertDialogBuilder.setPositiveButton("yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                if (ActivityCompat.checkSelfPermission(HistoryDetailActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(HistoryDetailActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PERMISSION_CALL);
                                    return;
                                }

                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse("tel:" + transaction.no_telepon));
                                startActivity(callIntent);
                            }
                        });

                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();


            }
        });

        cancelBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCancelable) {
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HistoryDetailActivity.this);
                    alertDialogBuilder.setTitle("Cancel order");
                    alertDialogBuilder.setMessage("Do you want to cancel this order?");
                    alertDialogBuilder.setPositiveButton("yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    cancelOrder();
                                }
                            });

                    alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                } else {
                    Toast.makeText(getApplicationContext(), "Anda tidak bisa membatalkan pesanan, perjalanan sudah dimulai!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (isCompleted) {
//            chatDriver.setVisibility(View.INVISIBLE);
//            callDriver.setVisibility(View.INVISIBLE);
            cancelBook.setVisibility(View.INVISIBLE);
            sparatorLayout.setVisibility(View.GONE);
            communicationLayout.setVisibility(View.GONE);
            driverArrivingTime.setVisibility(View.INVISIBLE);
        }

        bookService = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());
        handler = new Handler();

    }

    private void cancelOrder() {
        User loginUser = MangJekApplication.getInstance(HistoryDetailActivity.this).getLoginUser();
        CancelBookRequestJson request = new CancelBookRequestJson();
        request.id = loginUser.getId();
        request.id_transaksi = transaction.id_transaksi;

        UserService service = ServiceGenerator.createService(UserService.class, loginUser.getEmail(), loginUser.getPassword());
        service.cancelOrder(request).enqueue(new Callback<CancelBookResponseJson>() {
            @Override
            public void onResponse(Call<CancelBookResponseJson> call, Response<CancelBookResponseJson> response) {
                if (response.isSuccessful()) {
                    if (response.body().mesage.equals("order canceled")) {
                        Toast.makeText(HistoryDetailActivity.this, "Order Canceled!", Toast.LENGTH_SHORT).show();
                        new Queries(new DBHandler(getApplicationContext())).truncate(DBHandler.TABLE_CHAT);
                        finish();
                    } else {
                        Toast.makeText(HistoryDetailActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<CancelBookResponseJson> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(HistoryDetailActivity.this, "System error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });

        DriverResponse response = new DriverResponse();
        response.type = ORDER;
        response.setIdTransaksi(transaction.id_transaksi);
        response.setResponse(DriverResponse.REJECT);

        FCMMessage message = new FCMMessage();
        message.setTo(transaction.reg_id);
        message.setData(response);


        FCMHelper.sendMessage(FCM_KEY, message).enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                Log.e("CANCEL REQUEST", "sent");
            }

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
                Log.e("CANCEL REQUEST", "failed");
            }
        });
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

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
        requestRoute();

        if (pickUpMarker != null) pickUpMarker.remove();
        pickUpMarker = gMap.addMarker(new MarkerOptions()
                .position(pickUpLatLng)
                .title("Pick Up")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_loc_blue)));


        if (destinationMarker != null) destinationMarker.remove();
        destinationMarker = gMap.addMarker(new MarkerOptions()
                .position(destinationLatLng)
                .title("Destination")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_loc_blue)));

        if (!isCompleted) startDriverLocationUpdate();
    }

    private void updateLastLocation(boolean move) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_LOCATION);
            return;
        }
        lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(
                googleApiClient);
        gMap.setMyLocationEnabled(true);

        if (pickUpLatLng != null) {
            if (move) {
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        pickUpLatLng, 15f)
                );

                gMap.animateCamera(CameraUpdateFactory.zoomTo(15f));
            }
//            fetchNearDriver();
        }
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

        if (requestCode == REQUEST_PERMISSION_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Call permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Call permission restricted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void requestRoute() {
        if (pickUpLatLng != null && destinationLatLng != null) {
            MapDirectionAPI.getDirection(pickUpLatLng, destinationLatLng).enqueue(updateRouteCallback);
        }
    }

    private void updateLineDestination(String json) {
        Directions directions = new Directions(HistoryDetailActivity.this);
        try {
            List<Route> routes = directions.parse(json);

            if (directionLine != null) directionLine.remove();
            if (routes.size() > 0) {
                directionLine = gMap.addPolyline((new PolylineOptions())
                        .addAll(routes.get(0).getOverviewPolyLine())
                        .color(ContextCompat.getColor(HistoryDetailActivity.this, R.color.directionLine))
                        .width(5));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(BROADCAST_ORDER));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            orderBundle = intent.getExtras();
            orderHandler(orderBundle.getInt("code"));
        }
    };

    private void orderHandler(int code) {
        switch (code) {
            case REJECT:
                Log.e("DRIVER RESPONSE", "reject");
                isCancelable = false;
                break;
            case ACCEPT:
                Log.e("DRIVER RESPONSE", "accept");
                break;
            case START:
                Log.e("DRIVER RESPONSE", "start");
                isCancelable = false;
                Toast.makeText(getApplicationContext(), "Your trip is started", Toast.LENGTH_SHORT).show();
                break;
            case FINISH:
                Log.e("DRIVER RESPONSE", "finish");
                isCancelable = false;
//                new Queries(new DBHandler(getApplicationContext())).truncate(DBHandler.TABLE_CHAT);
                Toast.makeText(getApplicationContext(), "Your trip is finished", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), RateDriverActivity.class);
                intent.putExtra("id_transaksi", transaction.id_transaksi);
                intent.putExtra("id_pelanggan", loginUser.getId());
                intent.putExtra("driver_photo", transaction.foto);
                intent.putExtra("id_driver", transaction.id_driver);
                startActivity(intent);
                finish();
                break;
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onMessageEvent(final DriverResponse response) {
        Log.e("IN PROGRESS", response.getResponse() + " " + response.getId() + " " + response.getIdTransaksi());

    }


    private void readTransaction() {
        RealmResults<Transaksi> results = realm.where(Transaksi.class).findAll();

        Log.e("ALL TRANSACTION", results.toString());
        Log.e("TRANSACTION SIZE", results.size() + "");
        for (int i = 0; i < results.size(); i++) {
            Log.e("TRANSACTION ID", results.get(i).getId() + "");
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
        if (!isCompleted) stopDriverLocationUpdate();
    }

    private void startDriverLocationUpdate() {
        handler.postDelayed(updateDriverRunnable, 0);
    }

    private void stopDriverLocationUpdate() {
        handler.removeCallbacks(updateDriverRunnable);
    }

    private void updateDriverMarker(LatLng latLng) {
        String fitur = transaction.order_fitur;
        int iconRes = R.drawable.ic_m_ride_pin;
        if(fitur.equalsIgnoreCase("m-ride") || fitur.equalsIgnoreCase("m-send")
                || fitur.equalsIgnoreCase("m-mart")
                || fitur.equalsIgnoreCase("m-food")) {
            iconRes = R.drawable.ic_m_ride_pin;
        } else if(fitur.equalsIgnoreCase("m-car")) {
            iconRes = R.drawable.ic_m_car_pin;
        } else if(fitur.equalsIgnoreCase("m-massage")) {
            iconRes = R.drawable.ic_massage_pin;
        } else if(fitur.equalsIgnoreCase("m-box")) {
            iconRes = R.drawable.ic_mbox_pin;
        } else if(fitur.equalsIgnoreCase("m-service")) {
            iconRes = R.drawable.ic_service_pin;
        }
        if(driverMarker != null) driverMarker.remove();
        driverMarker = gMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Driver")
                .icon(BitmapDescriptorFactory.fromResource(iconRes)));
    }
}
