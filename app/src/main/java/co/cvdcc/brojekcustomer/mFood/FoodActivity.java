package co.cvdcc.brojekcustomer.mFood;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;

import co.cvdcc.brojekcustomer.MangJekApplication;
import co.cvdcc.brojekcustomer.R;
import co.cvdcc.brojekcustomer.adapter.ItemOffsetDecoration;
import co.cvdcc.brojekcustomer.api.ServiceGenerator;
import co.cvdcc.brojekcustomer.api.service.BookService;
import co.cvdcc.brojekcustomer.model.DataRestoran;
import co.cvdcc.brojekcustomer.model.KategoriRestoran;
import co.cvdcc.brojekcustomer.model.PromosiMFood;
import co.cvdcc.brojekcustomer.model.Restoran;
import co.cvdcc.brojekcustomer.model.User;
import co.cvdcc.brojekcustomer.model.json.book.GetDataRestoRequestJson;
import co.cvdcc.brojekcustomer.model.json.book.GetDataRestoResponseJson;
import co.cvdcc.brojekcustomer.utils.Log;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;
import io.realm.Realm;
import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FoodActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    @BindView(R.id.btn_home)
    ImageView btnHome;

    @BindView(R.id.food_search)
    LinearLayout foodSearch;

    @BindView(R.id.food_nearme)
    RelativeLayout foodNearme;

    @BindView(R.id.food_explore)
    RelativeLayout foodExplore;

    @BindView(R.id.text_explore)
    TextView textExplore;

    @BindView(R.id.slide_viewPager)
    AutoScrollViewPager autoScrollViewPager;

    @BindView(R.id.slide_viewPager_indicator)
    CircleIndicator circleIndicator;

    @BindView(R.id.kategori_recycler)
    RecyclerView kategoriRecyler;

    public static final String FITUR_KEY = "FiturKey";
    private static final int REQUEST_PERMISSION_LOCATION = 991;
    private GoogleApiClient googleApiClient;
    private Location lastKnownLocation;
    private Realm realm;

    private List<KategoriRestoran> kategoriRestoran;
    private List<Restoran> restoran;
    private FastItemAdapter<KategoriItem> kategoriAdapter;
    private boolean requestUpdate = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);
        ButterKnife.bind(this);

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .build();
        }

        realm = Realm.getDefaultInstance();
        kategoriAdapter = new FastItemAdapter<>();

        foodNearme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FoodActivity.this, NearmeActivity.class);
                startActivity(intent);
            }
        });

        foodSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FoodActivity.this, SearchRestoranActivity.class);
                FoodActivity.this.startActivity(intent);
            }
        });

        requestUpdate = true;
    }


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
    public void onConnected(@Nullable Bundle bundle) {
        updateLastLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void updateLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_LOCATION);
            return;
        }
        lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if(lastKnownLocation != null) {
            if(requestUpdate){
                getDataRestoran();
                KategoriRecycler();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateLastLocation();
            } else {
                // TODO: 10/15/2016 Tell user to use GPS
            }
        }
    }

    private void KategoriRecycler() {
        kategoriRecyler.setLayoutManager(new GridLayoutManager(this, 2));
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.item_offset);
        kategoriRecyler.addItemDecoration(itemDecoration);
        kategoriRecyler.setNestedScrollingEnabled(false);
        kategoriRecyler.setAdapter(kategoriAdapter);
        kategoriAdapter.withOnClickListener(new FastAdapter.OnClickListener<KategoriItem>() {
            @Override
            public boolean onClick(View v, IAdapter<KategoriItem> adapter, KategoriItem item, int position) {
                Log.e("BUTTON", "CLICKED");
//                KategoriRestoran selectedKategori = realm.where(KategoriRestoran.class).equalTo("idKategori", kategoriAdapter.getAdapterItem(position).idKategori).findFirst();
//                Intent intent = new Intent(FoodActivity.this, BoxOrder.class);
//                intent.putExtra(BoxOrder.KENDARAAN_KEY, selectedKategori.getIdKategori());
//                startActivity(intent);
                Intent intent = new Intent(FoodActivity.this, KategoriSelectActivity.class);
                intent.putExtra(KategoriSelectActivity.KATEGORI_ID, String.valueOf(item.idKategori));
                startActivity(intent);
                return true;
            }
        });
    }

    private void getDataRestoran() {
        if (lastKnownLocation != null) {
            User loginUser = MangJekApplication.getInstance(this).getLoginUser();
            BookService service = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());
            GetDataRestoRequestJson param = new GetDataRestoRequestJson();
            param.setLatitude(lastKnownLocation.getLatitude());
            param.setLongitude(lastKnownLocation.getLongitude());

            service.getDataRestoran(param).enqueue(new Callback<GetDataRestoResponseJson>() {
                @Override
                public void onResponse(Call<GetDataRestoResponseJson> call, Response<GetDataRestoResponseJson> response) {
                    if (response.isSuccessful()) {
                        DataRestoran dataRestoran = response.body().getDataRestoran();
                        kategoriRestoran = dataRestoran.getKategoriRestoranList();
                        restoran = dataRestoran.getRestoranList();
                        Log.d(FoodActivity.class.getSimpleName(), "Number of kategori: " + kategoriRestoran.size());
                        Log.d(FoodActivity.class.getSimpleName(), "Number of restoran: " + restoran.size());
                        Realm realm = MangJekApplication.getInstance(FoodActivity.this).getRealmInstance();
                        realm.beginTransaction();
                        realm.delete(KategoriRestoran.class);
                        realm.copyToRealm(kategoriRestoran);
                        realm.commitTransaction();

                        realm.beginTransaction();
                        realm.delete(Restoran.class);
                        realm.copyToRealm(restoran);
                        realm.commitTransaction();

//                        kategoriRealmResults = realm.where(KategoriRestoran.class).findAll();
//                        kategoriRealmResults.sort("idKategori", Sort.DESCENDING);
//                        KategoriItem kategoriItem;
//                        for (int i = 0; i < kategoriRealmResults.size(); i++) {
//                            kategoriItem = new KategoriItem(FoodActivity.this);
//                            kategoriItem.idKategori = kategoriRealmResults.get(i).getIdKategori();
//                            kategoriItem.kategori = kategoriRealmResults.get(i).getKategori();
//                            kategoriItem.image = kategoriRealmResults.get(i).getFotoKategori();
//                            kategoriAdapter.add(kategoriItem);
//                            Log.e("ADD CARGO", kategoriRealmResults.get(i).getIdKategori()+"");
//                        }

                        kategoriAdapter.clear();
                        KategoriItem kategoriItem;
                        for (KategoriRestoran kategori : kategoriRestoran) {
                            kategoriItem = new KategoriItem(FoodActivity.this);
                            kategoriItem.idKategori = kategori.getIdKategori();
                            kategoriItem.kategori = kategori.getKategori();
                            kategoriItem.image = kategori.getFotoKategori();
                            kategoriAdapter.add(kategoriItem);
                            Log.e("ADD KATEGORI", kategori.getIdKategori() + "");
                        }

                        List<PromosiMFood> promosiMFoods = dataRestoran.getPromosiMFood();
//                        Toast.makeText(FoodActivity.this, "size = "+promosiMFoods.size(), Toast.LENGTH_SHORT).show();
                        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), promosiMFoods);
                        autoScrollViewPager.setAdapter(pagerAdapter);
                        circleIndicator.setViewPager(autoScrollViewPager);
                        autoScrollViewPager.setInterval(20000);
                        autoScrollViewPager.startAutoScroll(20000);
                    }
                }

                @Override
                public void onFailure(Call<GetDataRestoResponseJson> call, Throwable t) {
//                    Toast.makeText(getApplicationContext(), "Connection to server lost, check your internet connection.",
//                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 5;
        public List<PromosiMFood> banners = new ArrayList<>();

        public MyPagerAdapter(FragmentManager fragmentManager, List<PromosiMFood> banners) {
            super(fragmentManager);
            this.banners = banners;
        }

        @Override
        public int getCount() {
            return banners.size();
        }

        @Override
        public Fragment getItem(int position) {
            return SlideRestoFragment.newInstance(banners.get(position).getId(),
                    banners.get(position).getFoto(),
                    banners.get(position).getIdResto());
//            switch (position) {
//                case 0:
//                    return SlideFragment.newInstance(0, "Page # 1");
//                case 1:
//                    return SlideFragment.newInstance(1, "Page # 2");
//                case 2:
//                    return SlideFragment.newInstance(2, "Page # 3");
//                case 3:
//                    return SlideFragment.newInstance(3, "Page # 4");
//                case 4:
//                    return SlideFragment.newInstance(4, "Page # 5");
//
//
//                default:
//                    return null;
//            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    protected void onPause() {
        super.onPause();
        requestUpdate = false;
    }
}
