package co.cvdcc.brojekcustomer.mService;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import co.cvdcc.brojekcustomer.MangJekApplication;
import co.cvdcc.brojekcustomer.R;
import co.cvdcc.brojekcustomer.api.FCMHelper;
import co.cvdcc.brojekcustomer.api.ServiceGenerator;
import co.cvdcc.brojekcustomer.api.service.UserService;
import co.cvdcc.brojekcustomer.home.ChatActivity;
import co.cvdcc.brojekcustomer.home.MainActivity;
import co.cvdcc.brojekcustomer.mRideCar.RateDriverActivity;
import co.cvdcc.brojekcustomer.model.Driver;
import co.cvdcc.brojekcustomer.model.User;
import co.cvdcc.brojekcustomer.model.json.fcm.CancelBookRequestJson;
import co.cvdcc.brojekcustomer.model.json.fcm.CancelBookResponseJson;
import co.cvdcc.brojekcustomer.model.json.fcm.DriverRequest;
import co.cvdcc.brojekcustomer.model.json.fcm.DriverResponse;
import co.cvdcc.brojekcustomer.model.json.fcm.FCMMessage;
import co.cvdcc.brojekcustomer.utils.Log;
import co.cvdcc.brojekcustomer.utils.db.DBHandler;
import co.cvdcc.brojekcustomer.utils.db.Queries;

import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static co.cvdcc.brojekcustomer.config.General.FCM_KEY;
import static co.cvdcc.brojekcustomer.model.FCMType.ORDER;
import static co.cvdcc.brojekcustomer.model.ResponseCode.ACCEPT;
import static co.cvdcc.brojekcustomer.model.ResponseCode.CANCEL;
import static co.cvdcc.brojekcustomer.model.ResponseCode.FINISH;
import static co.cvdcc.brojekcustomer.model.ResponseCode.REJECT;
import static co.cvdcc.brojekcustomer.model.ResponseCode.START;
import static co.cvdcc.brojekcustomer.service.MangJekMessagingService.BROADCAST_ORDER;

public class mServiceProgress extends AppCompatActivity {

    @BindView(R.id.btn_home)
    ImageView btnHome;

    @BindView(R.id.text_ordernum)
    TextView textOrdernum;

    @BindView(R.id.driver_image)
    RoundedImageView driverImage;

    @BindView(R.id.driver_name)
    TextView driverName;

    @BindView(R.id.driver_number)
    TextView driverNumber;

    @BindView(R.id.btn_chat)
    ImageView btnChat;

    @BindView(R.id.btn_call)
    ImageView btnCall;

    @BindView(R.id.value_service)
    TextView textService;

    @BindView(R.id.value_actype)
    TextView textActype;

    @BindView(R.id.value_quantity)
    TextView textQuantity;

    @BindView(R.id.value_problem)
    TextView textProblem;

    @BindView(R.id.value_location)
    TextView textLocation;

    @BindView(R.id.value_price)
    TextView textPrice;

    @BindView(R.id.btn_cancel)
    Button btnCancel;

    private static final int REQUEST_PERMISSION_CALL = 992;

    private Context context;
    Bundle orderBundle;
    private boolean isCancelable = true;
    Driver driver;
    DriverRequest request;
    User loginUser;
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mservice_progress);
        ButterKnife.bind(this);

        context = getApplicationContext();
        realm = Realm.getDefaultInstance();
        loginUser = MangJekApplication.getInstance(mServiceProgress.this).getLoginUser();

        driver = (Driver) getIntent().getSerializableExtra("driver");
        request = (DriverRequest) getIntent().getSerializableExtra("request");

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent home = new Intent(mServiceProgress.this, MainActivity.class);
                startActivity(home);
            }
        });

        textOrdernum.setText("Order no. " + request.getIdTransaksi());
        Glide.with(getApplicationContext()).load(driver.getFoto()).into(driverImage);
        driverName.setText(driver.getNamaDepan() + " " + driver.getNamaBelakang());
        driverNumber.setText(driver.getNoTelepon());

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("reg_id", driver.getRegId());
                startActivity(intent);
            }
        });

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mServiceProgress.this);
                alertDialogBuilder.setTitle("Call driver");
                alertDialogBuilder.setMessage("Do you want to call "+driver.getNoTelepon()+"?");
                alertDialogBuilder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                if (ActivityCompat.checkSelfPermission(mServiceProgress.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(mServiceProgress.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PERMISSION_CALL);
                                    return;
                                }

                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse("tel:"+driver.getNoTelepon()));
                                startActivity(callIntent);
                            }
                        });

                alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        textService.setText(request.getJenisService());
        textActype.setText(request.getAcType());
        textQuantity.setText(""+request.getQuantity());
        textProblem.setText(request.getProblem());
        textLocation.setText(request.getAlamatAsal());

        String formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(request.getHarga());
        String formattedText = String.format(Locale.US, "Rp %s ,-", formattedTotal);
        textPrice.setText(formattedText);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isCancelable){
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mServiceProgress.this);
                    alertDialogBuilder.setTitle("Batalkan Pesanan");
                    alertDialogBuilder.setMessage("Yakin ingin membatalkan pesanan ini?");
                    alertDialogBuilder.setPositiveButton("yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    cancelOrder();
                                }
                            });

                    alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }else {
                    Toast.makeText(getApplicationContext(), "You can't cancel order, trip already started!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void cancelOrder(){
        User loginUser = MangJekApplication.getInstance(mServiceProgress.this).getLoginUser();
        CancelBookRequestJson request = new CancelBookRequestJson();
        request.id = loginUser.getId();
        request.id_transaksi = this.request.getIdTransaksi();

        UserService service = ServiceGenerator.createService(UserService.class, loginUser.getEmail(), loginUser.getPassword());
        service.cancelOrder(request).enqueue(new Callback<CancelBookResponseJson>() {
            @Override
            public void onResponse(Call<CancelBookResponseJson> call, Response<CancelBookResponseJson> response) {
                if (response.isSuccessful()) {
                    if (response.body().mesage.equals("Pesanan Dibatalkan")) {
                        Toast.makeText(mServiceProgress.this, "Pesanan Dibatalkan!", Toast.LENGTH_SHORT).show();
                        new Queries(new DBHandler(getApplicationContext())).truncate(DBHandler.TABLE_CHAT);
                        finish();
                    } else {
                        Toast.makeText(mServiceProgress.this, "Gagal!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<CancelBookResponseJson> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(mServiceProgress.this, "System error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });

        DriverResponse response = new DriverResponse();
        response.type = ORDER;
        response.setIdTransaksi(this.request.getIdTransaksi());
        response.setResponse(DriverResponse.REJECT);

        FCMMessage message = new FCMMessage();
        message.setTo(driver.getRegId());
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

    private void orderHandler(int code){
        switch (code){
            case REJECT:
                Log.e("DRIVER RESPONSE", "reject");
                isCancelable = false;
                break;
            case ACCEPT:
                Log.e("DRIVER RESPONSE", "accept");
                break;
            case CANCEL:
                Log.e("DRIVER RESPONSE", "cancel");
                finish();
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
                intent.putExtra("id_transaksi", request.getIdTransaksi());
                intent.putExtra("id_pelanggan",loginUser.getId());
                intent.putExtra("driver_photo",driver.getFoto());
                intent.putExtra("id_driver",driver.getId());
                startActivity(intent);
                finish();
                break;
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onMessageEvent(final DriverResponse response) {
        Log.e("IN PROGRESS", response.getResponse()+" "+response.getId()+" "+response.getIdTransaksi());

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
