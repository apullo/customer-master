package co.cvdcc.brojekcustomer.home.submenu.history.details;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mikepenz.fastadapter.adapters.FastItemAdapter;

import co.cvdcc.brojekcustomer.MangJekApplication;
import co.cvdcc.brojekcustomer.R;
import co.cvdcc.brojekcustomer.api.ServiceGenerator;
import co.cvdcc.brojekcustomer.api.service.BookService;
import co.cvdcc.brojekcustomer.home.submenu.history.details.items.MMartItem;
import co.cvdcc.brojekcustomer.model.MMartDetailTransaksi;
import co.cvdcc.brojekcustomer.model.MMartItemRemote;
import co.cvdcc.brojekcustomer.model.User;
import co.cvdcc.brojekcustomer.model.json.book.detailTransaksi.GetDataTransaksiMMartResponse;
import co.cvdcc.brojekcustomer.model.json.book.detailTransaksi.GetDataTransaksiRequest;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
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

public class MMartDetailActivity extends AppCompatActivity {

    public static final String ID_TRANSAKSI = "IDTransaksi";

    @BindView(R.id.mMartDetail_title)
    TextView title;
    @BindView(R.id.mMartDetail_recycler)
    RecyclerView recyclerView;
    @BindView(R.id.mMartDetail_total)
    TextView totalField;

    FastItemAdapter<MMartItem> adapter;

    private String idTransaksi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mmart_detail);
        ButterKnife.bind(this);

        idTransaksi = getIntent().getStringExtra(ID_TRANSAKSI);
        Realm realm = MangJekApplication.getInstance(this).getRealmInstance();
        User loginUser = realm.copyFromRealm(MangJekApplication.getInstance(this).getLoginUser());
        BookService service = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());


        title.setText("Detail M-Mart");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FastItemAdapter<>();
        recyclerView.setAdapter(adapter);

        GetDataTransaksiRequest param = new GetDataTransaksiRequest();
        param.setIdTransaksi(idTransaksi);
        service.getDataTransaksiMMart(param).enqueue(new Callback<GetDataTransaksiMMartResponse>() {
            @Override
            public void onResponse(Call<GetDataTransaksiMMartResponse> call, Response<GetDataTransaksiMMartResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getDataTransaksi().isEmpty()) {
                        onFailure(call, new Exception());
                    } else {
                        MMartDetailTransaksi detail = response.body().getDataTransaksi().get(0);
                        updateUI(detail, response.body().getListBarang());
                    }
                } else {
                    onFailure(call, new Exception());
                }
            }

            @Override
            public void onFailure(Call<GetDataTransaksiMMartResponse> call, Throwable t) {
                Toast.makeText(MMartDetailActivity.this, "Silahkan coba lagi lain waktu.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(MMartDetailTransaksi transaksi, List<MMartItemRemote> items) {
        List<MMartItem> mMartItems = new ArrayList<>();
        for (MMartItemRemote item : items) {
            mMartItems.add(new MMartItem(item.getNamaBarang(), item.getJumlah()));
        }
        adapter.clear();
        adapter.set(mMartItems);
        adapter.notifyDataSetChanged();
        String total = "Total : " + String.format(Locale.US, "Rp. %s ,-",
                NumberFormat.getNumberInstance(Locale.US).format(transaksi.getEstimasiBiaya()));
        totalField.setText(total);
    }
}
