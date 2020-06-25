package co.cvdcc.brojekcustomer.mBox;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;

import co.cvdcc.brojekcustomer.MangJekApplication;
import co.cvdcc.brojekcustomer.R;
import co.cvdcc.brojekcustomer.model.DiskonMpay;
import co.cvdcc.brojekcustomer.model.Driver;
import co.cvdcc.brojekcustomer.model.Fitur;
import co.cvdcc.brojekcustomer.model.MboxLocation;
import co.cvdcc.brojekcustomer.model.User;
import co.cvdcc.brojekcustomer.model.json.book.MboxRequestJson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

import static co.cvdcc.brojekcustomer.mBox.BoxOrder.INSURANCE;
import static co.cvdcc.brojekcustomer.mBox.BoxOrder.INSURANCE_PRICE;
import static co.cvdcc.brojekcustomer.mBox.BoxOrder.SHIPPER;
import static co.cvdcc.brojekcustomer.mBox.BoxOrder.SHIPPER_PRICE;
import static co.cvdcc.brojekcustomer.mBox.MboxWaiting.DRIVER_LIST;
import static co.cvdcc.brojekcustomer.mBox.MboxWaiting.REQUEST_PARAM;

public class BoxDetailOrder extends AppCompatActivity {

//    @BindView(R.id.detail_pickup)
//    TextView mBoxDetailPickup;

    @BindView(R.id.detail_item)
    TextView mBoxDetailItem;

    @BindView(R.id.detail_orglocation)
    TextView mBoxDetailOriginLoc;

    @BindView(R.id.detail_destlocation)
    TextView mBoxDetailDestinationLoc;

//    @BindView(R.id.mbox_discount)
//    EditText mBoxDiscount;
//
//    @BindView(R.id.btn_discount)
//    Button mBoxBtnDiscount;

    @BindView(R.id.detail_price)
    TextView mBoxDetailPrice;

//    @BindView(R.id.detail_discount)
//    TextView mBoxDetailDiscount;

    @BindView(R.id.mpay_spinner)
    Spinner mPaySpinner;

    @BindView(R.id.total_price)
    TextView mBoxTotalPrice;

    @BindView(R.id.loading_service_price)
    TextView mBoxShipperPrice;

    @BindView(R.id.insurance_price)
    TextView mBoxInsurancePrice;


    @BindView(R.id.order_btn)
    Button orderButton;

    public static final String FITUR_KEY = "order_fitur";
    public static final String STRLATLONG_KEY = "start_latlong";
    public static final String ENDLATLONG_KEY = "end_latlong";
    public static final String JARAK_KEY = "jarak";
    public static final String HARGA_KEY = "harga";
    public static final String ASAL_KEY = "alamat_asal";
    public static final String ASALDETAIL_KEY = "detal_lokasi";
    public static final String ASALNAMA_KEY = "nama_pengirim";
    public static final String ASALPHONE_KEY = "telepon_pengirim";
    public static final String TUJUAN_KEY = "alamat_tujuan";
    public static final String PENERIMA_KEY = "nama_penerima";
    public static final String KONTAK_KEY = "telepon_penerima";
    public static final String KENDARAAN_KEY = "kendaraan_angkut";
    public static final String BARANG_KEY = "nama_barang";
    public static final String TANGGAL_KEY = "tanggal_pelayanan";
    public static final String JAM_KEY = "jam_pelayanan";
    public static final String CATATAN_KEY = "catatan";
    public static final String ORIGIN_LOCATION = "origin_location";
    public static final String DESTINATION_LOCATION = "destination_location";

    private int fitur;
    private LatLng start_latlong;
    private LatLng end_latlong;
    private double jarak;
    private long harga;
    private String alm_asal;
    private String alm_tujuan;
    private String nama_tujuan;
    private String kontak_tujuan;
    private String kendaraan;
    private int idKendaraan;
    private String barang;
    private String tanggal;
    private String jam;
    private String catatan;
    private String mpay;
    private int shipperCount;
    private int insuranceId;
    private long shipperPrice;
    private long insurancePrice;
    private long totalPrice;

    private long diskon;
    private int usingMpay;
    private List<Driver> driverAvailable;
    private Realm realm;
    private User loginUser;
    DiskonMpay diskonMpay;
    Fitur selectedFitur;

    MboxLocation mboxLocationOrigin;
    ArrayList<MboxLocation> mboxLocationListDest = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mbox_detailorder);
        ButterKnife.bind(this);

        realm = Realm.getDefaultInstance();
        loginUser = MangJekApplication.getInstance(this).getLoginUser();
        diskonMpay = MangJekApplication.getInstance(this).getDiskonMpay();

        Intent getDataOrder = getIntent();
        fitur = getDataOrder.getIntExtra(FITUR_KEY,0);
        start_latlong = getDataOrder.getParcelableExtra(STRLATLONG_KEY);
        end_latlong = getDataOrder.getParcelableExtra(ENDLATLONG_KEY);
        jarak = getDataOrder.getDoubleExtra(JARAK_KEY, 0);
        harga = getDataOrder.getLongExtra(HARGA_KEY, 0);
        alm_asal = getDataOrder.getStringExtra(ASAL_KEY);
//        alm_tujuan = getDataOrder.getStringExtra(TUJUAN_KEY);
        nama_tujuan = getDataOrder.getStringExtra(PENERIMA_KEY);
        kontak_tujuan = getDataOrder.getStringExtra(KONTAK_KEY);
        kendaraan = getDataOrder.getStringExtra(KENDARAAN_KEY);
        idKendaraan = getDataOrder.getIntExtra(BoxOrder.KENDARAAN_KEY,0);
        barang = getDataOrder.getStringExtra(BARANG_KEY);
        tanggal = getDataOrder.getStringExtra(TANGGAL_KEY);
        jam = getDataOrder.getStringExtra(JAM_KEY);
        catatan = getDataOrder.getStringExtra(CATATAN_KEY);
        shipperCount = getDataOrder.getIntExtra(SHIPPER,0);
        insuranceId = getDataOrder.getIntExtra(INSURANCE,0);
        shipperPrice = getDataOrder.getLongExtra(SHIPPER_PRICE,0);
        insurancePrice = getDataOrder.getLongExtra(INSURANCE_PRICE,0);
        mboxLocationOrigin = getDataOrder.getParcelableExtra(ORIGIN_LOCATION);
        mboxLocationListDest = getDataOrder.getParcelableArrayListExtra(DESTINATION_LOCATION);
        driverAvailable = (List<Driver>)getDataOrder.getSerializableExtra(DRIVER_LIST);
        alm_tujuan = mboxLocationListDest.get(mboxLocationListDest.size()-1).location;


        if (fitur != -1) {
            selectedFitur = realm.where(Fitur.class).equalTo("idFitur", fitur).findFirst();
        }

//        mBoxDetailPickup.setText(tanggal+", "+jam+" WIB");
        User userLogin = MangJekApplication.getInstance(this).getLoginUser();
        String pengirim = userLogin.getNamaDepan(); userLogin.getNamaBelakang();
        mBoxDetailItem.setText(barang);
        mBoxDetailOriginLoc.setText(alm_asal);
        String destinations = "";
        for (MboxLocation location : mboxLocationListDest){
            destinations += location.location+"\n";
        }


        mBoxDetailDestinationLoc.setText(destinations);

        totalPrice = (harga+(shipperPrice*shipperCount)+insurancePrice);
        mBoxDetailPrice.setText("Rp "+harga);

        mBoxShipperPrice.setText("Rp "+(shipperPrice*shipperCount));
        mBoxInsurancePrice.setText("Rp "+insurancePrice);
        mBoxTotalPrice.setText("Rp "+ totalPrice);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.mpay, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPaySpinner.setAdapter(adapter);

        mPaySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    usingMpay = 0;
                    totalPrice = (harga+(shipperPrice*shipperCount)+insurancePrice);
                    mBoxDetailPrice.setText("Rp "+harga);
                    mBoxTotalPrice.setText("Rp "+ totalPrice);

                } else if (i == 1) {
                    usingMpay = 1;
                    totalPrice = ((long)(harga*selectedFitur.getBiayaAkhir())+(shipperPrice*shipperCount)+insurancePrice);
                    mBoxDetailPrice.setText("Rp "+(long)(harga*selectedFitur.getBiayaAkhir()));
                    mBoxTotalPrice.setText("Rp "+ totalPrice);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                usingMpay = 0;
            }
        });

        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnOrderButtonClick();
            }
        });

        if(mboxLocationOrigin.name.equals("")){
            mboxLocationOrigin.name = loginUser.getNamaDepan()+" "+loginUser.getNamaBelakang();
        }
        if(mboxLocationOrigin.phone.equals("")){
            mboxLocationOrigin.phone = loginUser.getNoTelepon();
        }


    }

    private void OnOrderButtonClick () {
        MboxRequestJson param = new MboxRequestJson();
        User userLogin = MangJekApplication.getInstance(this).getLoginUser();
        param.idPelanggan = userLogin.getId();
        param.orderFitur = fitur;
        param.startLatitude = start_latlong.latitude;
        param.startLongitude = start_latlong.longitude;
        param.endLatitude = end_latlong.latitude;
        param.endLongitude = end_latlong.longitude;
        param.jarak = jarak;
        param.harga = totalPrice;
        param.asuransi = insuranceId;
        param.shipper = shipperCount;
        param.alamatAsal = alm_asal;
        param.alamatTujuan = alm_tujuan;
        param.pakaiMpay = usingMpay;
        param.kendaraanAngkut = idKendaraan;
        param.namaBarang = barang;
        param.destinasi = mboxLocationListDest;
        param.namaPengirim = mboxLocationOrigin.name;
        param.teleponPengirim = mboxLocationOrigin.phone;


//        param.namaPenerima = userLogin.getNamaDepan(); userLogin.getNamaBelakang();

        Intent intent = new Intent(this, MboxWaiting.class);
        intent.putExtra(REQUEST_PARAM, param);
        intent.putExtra(DRIVER_LIST, (ArrayList) driverAvailable);

        intent.putExtra("time_distance", 0);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
