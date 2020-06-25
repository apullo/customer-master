package co.cvdcc.brojekcustomer.mSend;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import co.cvdcc.brojekcustomer.MangJekApplication;
import co.cvdcc.brojekcustomer.R;
import co.cvdcc.brojekcustomer.api.FCMHelper;
import co.cvdcc.brojekcustomer.api.ServiceGenerator;
import co.cvdcc.brojekcustomer.api.service.BookService;
import co.cvdcc.brojekcustomer.api.service.UserService;
import co.cvdcc.brojekcustomer.mRideCar.InProgressActivity;
import co.cvdcc.brojekcustomer.model.Driver;
import co.cvdcc.brojekcustomer.model.Transaksi;
import co.cvdcc.brojekcustomer.model.User;
import co.cvdcc.brojekcustomer.model.json.book.CheckStatusTransaksiRequest;
import co.cvdcc.brojekcustomer.model.json.book.CheckStatusTransaksiResponse;
import co.cvdcc.brojekcustomer.model.json.book.RequestSendRequestJson;
import co.cvdcc.brojekcustomer.model.json.book.RequestSendResponseJson;
import co.cvdcc.brojekcustomer.model.json.fcm.CancelBookRequestJson;
import co.cvdcc.brojekcustomer.model.json.fcm.CancelBookResponseJson;
import co.cvdcc.brojekcustomer.model.json.fcm.DriverRequest;
import co.cvdcc.brojekcustomer.model.json.fcm.DriverResponse;
import co.cvdcc.brojekcustomer.model.json.fcm.FCMMessage;
import co.cvdcc.brojekcustomer.utils.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static co.cvdcc.brojekcustomer.config.General.FCM_KEY;
import static co.cvdcc.brojekcustomer.model.FCMType.ORDER;
import static co.cvdcc.brojekcustomer.model.json.fcm.DriverResponse.REJECT;

/**
 * Created by bradhawk on 10/17/2016.
 */

public class SendWaitingActivity extends AppCompatActivity {

    public static final String REQUEST_PARAM = "RequestParam";
    public static final String DRIVER_LIST = "DriverList";

    private List<Driver> driverList;
    private RequestSendRequestJson param;

    private DriverRequest request;
    private int currentLoop;

    AppCompatActivity activity;

    private Driver driver;

    private double timeDistance;
    Transaksi transaksi;
    Thread thread;
    boolean threadRun = true;

    @BindView(R.id.waiting_cancel)
    Button cancelButton;

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);
        ButterKnife.bind(this);

        activity = this;

        param = (RequestSendRequestJson) getIntent().getSerializableExtra(REQUEST_PARAM);
        driverList = (List<Driver>) getIntent().getSerializableExtra(DRIVER_LIST);

        timeDistance = getIntent().getDoubleExtra("time_distance", 0);
        currentLoop = 0;

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(request != null){
                    cancelOrder();
                }


            }
        });

        sendRequestTransaksi();
    }

    private void sendRequestTransaksi() {
        User loginUser = MangJekApplication.getInstance(this).getLoginUser();
        final BookService service = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());

        service.requestTransMSend(param).enqueue(new Callback<RequestSendResponseJson>() {
            @Override
            public void onResponse(Call<RequestSendResponseJson> call, Response<RequestSendResponseJson> response) {
                if (response.isSuccessful()) {
                    buildDriverRequest(response.body());
//                    fcmBroadcast(currentLoop);


                    thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < driverList.size(); i++) {
                                if (threadRun) {
                                    fcmBroadcast(currentLoop);
                                }
                            }
                            try {
                                Thread.sleep(25000);
//                                    if(!threadRun){
//                                        thread.stop();
//                                    }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (threadRun) {
                                CheckStatusTransaksiRequest param = new CheckStatusTransaksiRequest();
                                param.setIdTransaksi(transaksi.getId());
                                service.checkStatusTransaksi(param).enqueue(new Callback<CheckStatusTransaksiResponse>() {
                                    @Override
                                    public void onResponse(Call<CheckStatusTransaksiResponse> call, Response<CheckStatusTransaksiResponse> response) {
                                        if(response.isSuccessful()) {
                                            CheckStatusTransaksiResponse checkStatus = response.body();
                                            if(checkStatus.isStatus()) {
                                                Intent intent = new Intent(activity, InProgressActivity.class);
                                                intent.putExtra("driver", checkStatus.getListDriver().get(0));
                                                intent.putExtra("request", request);
                                                intent.putExtra("time_distance", timeDistance);
                                                startActivity(intent);
                                            } else {
                                                Log.e("DRIVER STATUS", "Driver not found!");
                                                activity.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(SendWaitingActivity.this, "Belum mendapatkan driver, silahkan coba lagi.", Toast.LENGTH_LONG).show();
                                                    }
                                                });

                                                finish();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<CheckStatusTransaksiResponse> call, Throwable t) {
                                        Log.e("DRIVER STATUS", "Driver not found!");
                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(SendWaitingActivity.this, "Belum mendapatkan driver, silahkan coba lagi.", Toast.LENGTH_LONG).show();
                                            }
                                        });

                                        finish();
                                    }
                                });
                            }
                        }
                    });
                    thread.start();


                }
            }

            @Override
            public void onFailure(Call<RequestSendResponseJson> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }


    private void cancelOrder(){
        User loginUser = MangJekApplication.getInstance(SendWaitingActivity.this).getLoginUser();
        CancelBookRequestJson request = new CancelBookRequestJson();

        request.id = loginUser.getId();
        request.id_transaksi = this.request.getIdTransaksi();
        request.id_driver = "D0";

        Log.d("id_transaksi_cancel", this.request.getIdTransaksi());
        UserService service = ServiceGenerator.createService(UserService.class, loginUser.getEmail(), loginUser.getPassword());
        service.cancelOrder(request).enqueue(new Callback<CancelBookResponseJson>() {
            @Override
            public void onResponse(Call<CancelBookResponseJson> call, Response<CancelBookResponseJson> response) {
                if (response.isSuccessful()) {
                    if (response.body().mesage.equals("order canceled")) {
                        Toast.makeText(SendWaitingActivity.this, "Order Canceled!", Toast.LENGTH_SHORT).show();
                        threadRun = false;
                        finish();
                    } else {
                        Toast.makeText(SendWaitingActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<CancelBookResponseJson> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(SendWaitingActivity.this, "System error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });

        DriverResponse response = new DriverResponse();
        response.type = ORDER;
        response.setIdTransaksi(this.request.getIdTransaksi());
        response.setResponse(REJECT);

        FCMMessage message = new FCMMessage();
        message.setTo(driverList.get(currentLoop-1).getRegId());
        message.setData(response);


        FCMHelper.sendMessage(FCM_KEY, message).enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                Log.e("CANCEL REQUEST", "sent");
                threadRun = false;

            }

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
                Log.e("CANCEL REQUEST", "failed");
            }
        });
    }

    private void buildDriverRequest(RequestSendResponseJson response) {
        transaksi = response.getData().get(0);
        User loginUser = MangJekApplication.getInstance(this).getLoginUser();
        if (request == null) {
            request = new DriverRequest();
            request.setIdTransaksi(transaksi.getId());
            request.setIdPelanggan(transaksi.getIdPelanggan());
            request.setRegIdPelanggan(loginUser.getRegId());
            request.setOrderFitur(transaksi.getOrderFitur());
            request.setStartLatitude(transaksi.getStartLatitude());
            request.setStartLongitude(transaksi.getStartLongitude());
            request.setEndLatitude(transaksi.getEndLatitude());
            request.setEndLongitude(transaksi.getEndLongitude());
            request.setJarak(transaksi.getJarak());
            request.setHarga(transaksi.getHarga());
            request.setWaktuOrder(transaksi.getWaktuOrder());
            request.setAlamatAsal(transaksi.getAlamatAsal());
            request.setAlamatTujuan(transaksi.getAlamatTujuan());
            request.setKodePromo(transaksi.getKodePromo());
            request.setKreditPromo(transaksi.getKreditPromo());
            request.setPakaiMPay(transaksi.isPakaiMpay());


            String namaLengkap = String.format("%s %s", loginUser.getNamaDepan(), loginUser.getNamaBelakang());
            request.setNamaPelanggan(namaLengkap);
            request.setTelepon(loginUser.getNoTelepon());
            request.setType(ORDER);

            request.namaBarang = param.namaBarang;
            request.namaPengirim = param.namaPengirim;
            request.teleponPengirim = param.teleponPengirim;
            request.namaPenerima = param.namaPenerima;
            request.teleponPenerima = param.teleponPenerima;

        }
    }

    private void fcmBroadcast(int index) {
        Driver driverToSend = driverList.get(index);
        currentLoop++;
        request.setTime_accept(new Date().getTime()+"");
        FCMMessage message = new FCMMessage();
        message.setTo(driverToSend.getRegId());
        message.setData(request);

//        Log.e("REQUEST TO DRIVER", message.getData().toString());
        driver = driverToSend;

        FCMHelper.sendMessage(FCM_KEY, message).enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {

            }

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onBackPressed() {

    }


    @SuppressWarnings("unused")
    @Subscribe
    public void onMessageEvent(final DriverResponse response) {
        Log.e("DRIVER RESPONSE (W)", response.getResponse()+" "+response.getId()+" "+response.getIdTransaksi());
//        if (currentLoop < driverList.size()) {
            if (response.getResponse().equalsIgnoreCase(DriverResponse.ACCEPT)) {
                Log.d("DRIVER RESPONSE", "Terima");
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        threadRun = false;

                        for(Driver cDriver : driverList){
                            if(cDriver.getId().equals(response.getId())){
                                driver = cDriver;
                            }
                        }
//                        saveTransaction(transaksi);
//                        saveDriver(driver);
//                        Toast.makeText(getApplicationContext(), "Transaksi " + response.getIdTransaksi() + " ada yang mau!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(activity, InProgressActivity.class);
                        intent.putExtra("driver", driver);
                        intent.putExtra("request", request);
                        intent.putExtra("time_distance", timeDistance);
                        startActivity(intent);
                        finish();
                    }
                });
            }
//            else {
//                Log.d("DRIVER RESPONSE", "Tolak");
//                if(currentLoop == (driverList.size()-1)){
//                    Intent intent = new Intent(activity, InProgressActivity.class);
//                    intent.putExtra("driver", driver);
//                    intent.putExtra("request", request);
//                    intent.putExtra("time_distance", timeDistance);
//                    threadRun = false;
//                    startActivity(intent);
//                    finish();
//                }else{
////                    fcmBroadcast(++currentLoop);
//                    currentLoop++;
//                }
//
//            }
//        }
    }

    private void saveTransaction(Transaksi transaksi) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.insert(transaksi);
        realm.commitTransaction();
    }

    private void saveDriver(Driver driver) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(Driver.class);
        realm.insert(driver);
        realm.commitTransaction();
    }


}
