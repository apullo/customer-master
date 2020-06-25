package co.cvdcc.brojekcustomer.home.submenu.history.details;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import co.cvdcc.brojekcustomer.MangJekApplication;
import co.cvdcc.brojekcustomer.R;
import co.cvdcc.brojekcustomer.api.ServiceGenerator;
import co.cvdcc.brojekcustomer.api.service.BookService;
import co.cvdcc.brojekcustomer.model.DetailTransaksiMSend;
import co.cvdcc.brojekcustomer.model.User;
import co.cvdcc.brojekcustomer.model.json.book.detailTransaksi.GetDataTransaksiMSendResponse;
import co.cvdcc.brojekcustomer.model.json.book.detailTransaksi.GetDataTransaksiRequest;

import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by fathony on 24/02/2017.
 */

public class MSendDetailActivity extends AppCompatActivity {

    public static final String ID_TRANSAKSI = "IDTransaksi";

    @BindView(R.id.mSendDetail_title)
    TextView title;

    @BindView(R.id.mSendDetail_tanggal)
    TextView tanggalPengiriman;
    @BindView(R.id.mSendDetail_alamatAsal)
    TextView alamatAsal;
    @BindView(R.id.mSendDetail_alamatTujuan)
    TextView alamatTujuan;
    @BindView(R.id.mSendDetail_namaPenerima)
    TextView namaPenerima;
    @BindView(R.id.mSendDetail_nomorPenerima)
    TextView nomorPenerima;
    @BindView(R.id.mSendDetail_namaBarang)
    TextView namaBarang;

    private String idTransaksi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msend_detail);
        ButterKnife.bind(this);

        title.setText("Detail M-Send");
        idTransaksi = getIntent().getStringExtra(ID_TRANSAKSI);
        Realm realm = MangJekApplication.getInstance(this).getRealmInstance();
        User loginUser = realm.copyFromRealm(MangJekApplication.getInstance(this).getLoginUser());
        BookService service = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());

        GetDataTransaksiRequest param = new GetDataTransaksiRequest();
        param.setIdTransaksi(idTransaksi);
        service.getDataTransaksiMSend(param).enqueue(new Callback<GetDataTransaksiMSendResponse>() {
            @Override
            public void onResponse(Call<GetDataTransaksiMSendResponse> call, Response<GetDataTransaksiMSendResponse> response) {
                if(response.isSuccessful()) {
                    if(response.body().getDataTransaksi().isEmpty()) {
                        onFailure(call, new Exception());
                    } else {
                        DetailTransaksiMSend detail = response.body().getDataTransaksi().get(0);
                        updateUI(detail);
                    }
                } else {
                    onFailure(call, new Exception());
                }
            }

            @Override
            public void onFailure(Call<GetDataTransaksiMSendResponse> call, Throwable t) {
                Toast.makeText(MSendDetailActivity.this, "Silahkan coba lagi lain waktu.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(DetailTransaksiMSend detail) {
        SimpleDateFormat sdfTo = new SimpleDateFormat("dd MM yyyy hh:mm", Locale.US);

        String formattedDate = sdfTo.format(detail.getWaktuOrder());
        tanggalPengiriman.setText(formattedDate);
        alamatAsal.setText(detail.getAlamatAsal());
        alamatTujuan.setText(detail.getAlamatTujuan());
        namaPenerima.setText(detail.getNamaPenerima());
        nomorPenerima.setText(detail.getTeleponPenerima());
        namaBarang.setText(detail.getNamaBarang());
    }
}
