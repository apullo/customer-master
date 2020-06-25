package co.cvdcc.brojekcustomer.mBox;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;

import co.cvdcc.brojekcustomer.MangJekApplication;
import co.cvdcc.brojekcustomer.R;
import co.cvdcc.brojekcustomer.api.ServiceGenerator;
import co.cvdcc.brojekcustomer.api.service.BookService;
import co.cvdcc.brojekcustomer.model.KendaraanAngkut;
import co.cvdcc.brojekcustomer.model.User;
import co.cvdcc.brojekcustomer.model.json.book.GetKendaraanAngkutResponseJson;
import co.cvdcc.brojekcustomer.utils.Log;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BoxActivity extends AppCompatActivity {

//    @BindView(R.cargoId.button_pickup)
//    LinearLayout buttonPickup;
//
//    @BindView(R.cargoId.button_truck)
//    LinearLayout buttonTruck;

    @BindView(R.id.cargo_type_recyclerView)
    RecyclerView cargoTypeRecyclerView;

    public static final String FITUR_KEY = "FiturKey";
    public static final String CARGO = "cargo";

    private Realm realm;
    int featureId;
    FastItemAdapter<CargoItem> itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mbox);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        featureId = intent.getIntExtra(FITUR_KEY, -1);

        realm = Realm.getDefaultInstance();
        LoadKendaraan();
        itemAdapter = new FastItemAdapter<>();
        cargoTypeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cargoTypeRecyclerView.setAdapter(itemAdapter);

        itemAdapter.withItemEvent(new ClickEventHook<CargoItem>() {
            @Nullable
            @Override
            public View onBind(@NonNull RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof CargoItem.ViewHolder) {
                    return ((CargoItem.ViewHolder) viewHolder).itemView;
                }
                return null;
            }

            @Override
            public void onClick(View v, int position, FastAdapter<CargoItem> fastAdapter, CargoItem item) {
                KendaraanAngkut selectedCargo = realm.where(KendaraanAngkut.class).equalTo("idKendaraan", itemAdapter.getAdapterItem(position).id).findFirst();
                Log.e("BUTTON","CLICKED, ID : "+selectedCargo.getIdKendaraan());
                Intent intent = new Intent(BoxActivity.this, BoxOrder.class);
                intent.putExtra(BoxOrder.FITUR_KEY, featureId);
                intent.putExtra(BoxOrder.KENDARAAN_KEY, selectedCargo.getIdKendaraan());
                startActivity(intent);
            }
        });

//        buttonPickup.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                KendaraanAngkut selectedCargo = realm.where(KendaraanAngkut.class).equalTo("idKendaraan", 4).findFirst();
//                Intent intent = new Intent(BoxActivity.this, BoxOrder.class);
//                intent.putExtra(BoxOrder.FITUR_KEY, featureId);
//                intent.putExtra(BoxOrder.KENDARAAN_KEY, selectedCargo.getIdKendaraan());
//                startActivity(intent);
//            }
//        });
//
//        buttonTruck.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                KendaraanAngkut selectedCargo = realm.where(KendaraanAngkut.class).equalTo("idKendaraan", 5).findFirst();
//                Intent intent = new Intent(BoxActivity.this, BoxOrder.class);
//                intent.putExtra(BoxOrder.KENDARAAN_KEY, selectedCargo.getIdKendaraan());
//                startActivity(intent);
//            }
//        });
    }

    private void LoadKendaraan() {
        User loginUser = MangJekApplication.getInstance(this).getLoginUser();
        BookService service = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());
        service.getKendaraanAngkut().enqueue(new Callback<GetKendaraanAngkutResponseJson>() {
            @Override
            public void onResponse(Call<GetKendaraanAngkutResponseJson> call, Response<GetKendaraanAngkutResponseJson> response) {
                if(response.isSuccessful()) {

                    Realm realm = MangJekApplication.getInstance(BoxActivity.this).getRealmInstance();
                    realm.beginTransaction();
                    realm.delete(KendaraanAngkut.class);
                    realm.copyToRealm(response.body().getData());
                    realm.commitTransaction();


                    CargoItem cargoItem;
                    for (KendaraanAngkut cargo:response.body().getData() ){
                        cargoItem = new CargoItem(BoxActivity.this);
                        cargoItem.featureId = featureId;
                        cargoItem.id = cargo.getIdKendaraan();
                        cargoItem.type = cargo.getKendaraanAngkut();
                        cargoItem.price = cargo.getHarga();
                        cargoItem.image = cargo.getFotoKendaraan();
                        cargoItem.dimension = cargo.getDimensiKendaraan();
                        cargoItem.maxWeight = cargo.getMaxweightKendaraan();
                        itemAdapter.add(cargoItem);
                        Log.e("ADD CARGO", cargo.getIdKendaraan()+"");
                    }
                }
            }

            @Override
            public void onFailure(Call<GetKendaraanAngkutResponseJson> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
