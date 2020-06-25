package co.cvdcc.brojekcustomer.mFood;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;

import co.cvdcc.brojekcustomer.MangJekApplication;
import co.cvdcc.brojekcustomer.R;
import co.cvdcc.brojekcustomer.api.ServiceGenerator;
import co.cvdcc.brojekcustomer.api.service.BookService;
import co.cvdcc.brojekcustomer.model.FoodResto;
import co.cvdcc.brojekcustomer.model.Makanan;
import co.cvdcc.brojekcustomer.model.MenuMakanan;
import co.cvdcc.brojekcustomer.model.PesananFood;
import co.cvdcc.brojekcustomer.model.Restoran;
import co.cvdcc.brojekcustomer.model.User;
import co.cvdcc.brojekcustomer.model.json.book.GetFoodRestoRequestJson;
import co.cvdcc.brojekcustomer.model.json.book.GetFoodRestoResponseJson;
import co.cvdcc.brojekcustomer.utils.Log;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FoodMenuActivity extends AppCompatActivity {

    @BindView(R.id.btn_home)
    ImageView btnHome;

    @BindView(R.id.resto_title)
    TextView titleResto;

    @BindView(R.id.food_img)
    ImageView foodImage;

    @BindView(R.id.food_address)
    TextView foodAddress;

    @BindView(R.id.food_info)
    TextView foodInfo;

    @BindView(R.id.menu_recycler)
    RecyclerView menuRecycler;

    @BindView(R.id.foodMenu_bottom)
    RelativeLayout priceContainer;

    @BindView(R.id.qty_text)
    TextView qtyText;

    @BindView(R.id.cost_text)
    TextView costText;

    @BindView(R.id.foodMenu_mitra)
    TextView mitra;

    @BindView(R.id.foodMenu_closed)
    TextView closed;

    public static final String ID_RESTO = "idResto";
    public static final String NAMA_RESTO = "namaResto";
    public static final String ALAMAT_RESTO = "alamatResto";
    public static final String DISTANCE_RESTO = "distanceResto";
    public static final String JAM_BUKA = "jamBuka";
    public static final String JAM_TUTUP = "jamTutup";
    public static final String IS_MITRA = "IsMitra";
    public static final String IS_OPEN = "IsOpen";
    public static final String PICTURE_URL = "PicUrl";

    private int idResto;
    private String namaResto;
    private String alamatResto;
    private double distanceResto;
    private String jamBuka;
    private String jamTutup;
    private boolean isOpenFromParent;
    private boolean isMitra;
    private String picUrl;

    private boolean isOpen;

    private Realm realm;
    private List<MenuMakanan> menuMakanan;
    private List<Makanan> makanan;
    private FastItemAdapter<MenuItem> menuAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_menu);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        idResto = intent.getIntExtra(ID_RESTO, -1);
        namaResto = intent.getStringExtra(NAMA_RESTO);
        alamatResto = intent.getStringExtra(ALAMAT_RESTO);
        distanceResto = intent.getDoubleExtra(DISTANCE_RESTO, -8);
        jamBuka = intent.getStringExtra(JAM_BUKA);
        jamTutup = intent.getStringExtra(JAM_TUTUP);
        isOpenFromParent = intent.getBooleanExtra(IS_OPEN, false);
        isMitra = intent.getBooleanExtra(IS_MITRA, false);
        picUrl = intent.getStringExtra(PICTURE_URL);

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        realm = Realm.getDefaultInstance();

        Glide.with(this).load(picUrl).centerCrop().into(foodImage);
        if(isOpenFromParent) {
            isOpen = calculateJamBukaTutup();
        } else {
            isOpen = false;
        }

        closed.setVisibility(isOpen ? View.GONE : View.VISIBLE);
        mitra.setVisibility(isMitra? View.VISIBLE : View.GONE);

        titleResto.setText(namaResto);
        foodAddress.setText(alamatResto);
        NumberFormat formatter = new DecimalFormat("#0.00");
        foodInfo.setText("" + formatter.format(distanceResto) + " KM | OPEN " + jamBuka + " - " + jamTutup);

        menuAdapter = new FastItemAdapter<>();
        MenuRecycler();
        getMenuResto();

        priceContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FoodMenuActivity.this, BookingActivity.class);
                FoodMenuActivity.this.startActivity(intent);
            }
        });
    }

    private boolean calculateJamBukaTutup() {
        String[] parsedJamBuka = jamBuka.split(":");
        String[] parsedJamTutup = jamTutup.split(":");

        int jamBuka = Integer.parseInt(parsedJamBuka[0]), menitBuka = Integer.parseInt(parsedJamBuka[1]);
        int jamTutup = Integer.parseInt(parsedJamTutup[0]), menitTutup = Integer.parseInt(parsedJamTutup[1]);

        int totalMenitBuka = (jamBuka * 60) + menitBuka;
        int totalMenitTutup = (jamTutup * 60) + menitTutup;

        Calendar now = Calendar.getInstance();
        int totalMenitNow = (now.get(Calendar.HOUR_OF_DAY) * 60) + now.get(Calendar.MINUTE);

        return totalMenitNow <= totalMenitTutup && totalMenitNow >= totalMenitBuka;
    }

    private void MenuRecycler() {
        menuRecycler.setLayoutManager(new LinearLayoutManager(this));
        menuRecycler.setAdapter(menuAdapter);

        menuAdapter.withOnClickListener(new FastAdapter.OnClickListener<MenuItem>() {
            @Override
            public boolean onClick(View v, IAdapter<MenuItem> adapter, MenuItem item, int position) {
                if(isOpen) {
                    Log.e("BUTTON", "CLICKED");
                    MenuMakanan selectedMenu = menuMakanan.get(position);
                    int idMenu = selectedMenu.getIdMenu();
                    Intent intent = new Intent(FoodMenuActivity.this, MakananActivity.class);
                    intent.putExtra(MakananActivity.ID_MENU, selectedMenu.getIdMenu());
                    intent.putExtra(MakananActivity.NAMA_RESTO, selectedMenu.getMenuMakanan());
                    Log.e("ID RESTO", idMenu + "");
                    startActivity(intent);
                } else {
                    Toast.makeText(FoodMenuActivity.this, "Your restaurant is still closed. :(", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        List<PesananFood> existingFood = realm.where(PesananFood.class).findAll();

        int quantity = 0;
        for (int p = 0; p < existingFood.size(); p++) {
            quantity += existingFood.get(p).getQty();
        }

        if(quantity > 0) {
            new AlertDialog.Builder(this)
                    .setTitle("Delete current order(s)?")
                    .setMessage("Leaving this page will cause you lose all the order you've made. Continue?")
                    .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FoodMenuActivity.this.finish();
                        }
                    })
                    .setNegativeButton("Back", null)
                    .show();
        } else {
            finish();
        }
    }

    private void getMenuResto() {
        User loginUser = MangJekApplication.getInstance(this).getLoginUser();
        BookService service = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());
        GetFoodRestoRequestJson param = new GetFoodRestoRequestJson();
        param.setIdResto(idResto);

        service.getFoodResto(param).enqueue(new Callback<GetFoodRestoResponseJson>() {
            @Override
            public void onResponse(Call<GetFoodRestoResponseJson> call, Response<GetFoodRestoResponseJson> response) {
                if (response.isSuccessful()) {
                    FoodResto foodResto = response.body().getFoodResto();
                    Restoran restoranList = foodResto.getDetailRestoran().get(0);

                    menuMakanan = foodResto.getMenuMakananList();
                    makanan = foodResto.getMakananList();
                    Log.d(FoodMenuActivity.class.getSimpleName(), "Number of kategori: " + menuMakanan.size());
                    Log.d(FoodMenuActivity.class.getSimpleName(), "Number of restoran: " + makanan.size());
                    Realm realm = MangJekApplication.getInstance(FoodMenuActivity.this).getRealmInstance();
                    realm.beginTransaction();
                    realm.delete(Makanan.class);
                    realm.copyToRealm(makanan);
                    realm.commitTransaction();

                    realm.beginTransaction();
                    realm.delete(Restoran.class);
                    realm.copyToRealm(restoranList);
                    realm.commitTransaction();

                    MenuItem menuItem;
                    for (int i = 0; i < menuMakanan.size(); i++) {
                        menuItem = new MenuItem(FoodMenuActivity.this);
                        menuItem.idMenu = menuMakanan.get(i).getIdMenu();
                        menuItem.menuMakanan = menuMakanan.get(i).getMenuMakanan();
                        menuAdapter.add(menuItem);
                        Log.e("ADD RESTO", menuItem.idMenu + "");
                        Log.e("ADD RESTO", menuItem.menuMakanan + "");
                    }
                }
            }

            @Override
            public void onFailure(Call<GetFoodRestoResponseJson> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Connection to server lost, check your internet connection.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        CalculatePrice();
    }

    public void CalculatePrice() {
        List<PesananFood> existingFood = realm.where(PesananFood.class).findAll();

        int quantity = 0;
        long cost = 0;
        for (int p = 0; p < existingFood.size(); p++) {
            quantity += existingFood.get(p).getQty();
            cost += existingFood.get(p).getTotalHarga();
        }

        if(quantity > 0)
            priceContainer.setVisibility(View.VISIBLE);
        else
            priceContainer.setVisibility(View.GONE);

        qtyText.setText("" + quantity);
        String formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(cost);
        String formattedText = String.format(Locale.US, "Rp. %s ,-", formattedTotal);
        costText.setText(formattedText);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
