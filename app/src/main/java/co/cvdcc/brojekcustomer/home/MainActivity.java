package co.cvdcc.brojekcustomer.home;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import co.cvdcc.brojekcustomer.MangJekApplication;
import co.cvdcc.brojekcustomer.R;
import co.cvdcc.brojekcustomer.api.ServiceGenerator;
import co.cvdcc.brojekcustomer.api.service.UserService;
import co.cvdcc.brojekcustomer.home.submenu.help.HelpFragment;
import co.cvdcc.brojekcustomer.home.submenu.history.HistoryFragment;
import co.cvdcc.brojekcustomer.home.submenu.home.HomeFragment;
import co.cvdcc.brojekcustomer.home.submenu.setting.SettingFragment;
import co.cvdcc.brojekcustomer.model.DiskonMpay;
import co.cvdcc.brojekcustomer.model.Fitur;
import co.cvdcc.brojekcustomer.model.MfoodMitra;
import co.cvdcc.brojekcustomer.model.User;
import co.cvdcc.brojekcustomer.model.json.user.GetFiturResponseJson;
import co.cvdcc.brojekcustomer.utils.MangJekTabProvider;
import co.cvdcc.brojekcustomer.utils.MenuSelector;
import co.cvdcc.brojekcustomer.utils.SnackbarController;
import co.cvdcc.brojekcustomer.utils.view.CustomViewPager;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by bradhawk on 10/10/2016.
 */

public class MainActivity extends AppCompatActivity implements SnackbarController {

    @BindView(R.id.main_container)
    LinearLayout mainLayout;

    @BindView(R.id.main_tabLayout)
    SmartTabLayout mainTabLayout;

    @BindView(R.id.main_viewPager)
    CustomViewPager mainViewPager;

    private Snackbar snackBar;

    private MenuSelector selector;
    private SmartTabLayout.TabProvider tabProvider;

    private FragmentPagerItemAdapter adapter;

    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setupTabLayoutViewPager();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Silahkan tekan tombol 'BACK' lagi untuk keluar...", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    private void setupTabLayoutViewPager() {
        tabProvider = new MangJekTabProvider(this);
        selector = (MenuSelector) tabProvider;
        mainTabLayout.setCustomTabView(tabProvider);

        adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add(R.string.main_menuHome, HomeFragment.class)
                .add(R.string.main_menuHistory, HistoryFragment.class)
                .add(R.string.main_menuHelp, HelpFragment.class)
                .add(R.string.main_menuSetting, SettingFragment.class)
                .create());
        mainViewPager.setAdapter(adapter);
        mainTabLayout.setViewPager(mainViewPager);
        mainViewPager.setPagingEnabled(false);

        mainTabLayout.setOnTabClickListener(new SmartTabLayout.OnTabClickListener() {
            @Override
            public void onTabClicked(int position) {
                selector.selectMenu(position);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateFiturMangJek();
    }

    @Override
    public void showSnackbar(@StringRes int stringRes, int duration, @StringRes int actionResText, View.OnClickListener onClickListener) {
        snackBar = Snackbar.make(mainLayout, stringRes, duration);
        if (actionResText != -1 && onClickListener != null) {
            snackBar.setAction(actionResText, onClickListener)
                    .setActionTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
        snackBar.show();
    }

    private void updateFiturMangJek() {
        User loginUser = MangJekApplication.getInstance(this).getLoginUser();
        UserService userService = ServiceGenerator.createService(UserService.class,
                loginUser.getEmail(), loginUser.getPassword());
        userService.getFitur().enqueue(new Callback<GetFiturResponseJson>() {
            @Override
            public void onResponse(Call<GetFiturResponseJson> call, Response<GetFiturResponseJson> response) {
                if (response.isSuccessful()) {
                    Realm realm = MangJekApplication.getInstance(MainActivity.this).getRealmInstance();
                    realm.beginTransaction();
                    realm.delete(Fitur.class);
                    realm.copyToRealm(response.body().getData());
                    realm.commitTransaction();

                    DiskonMpay diskonMpay = response.body().getDiskonMpay();
                    realm.beginTransaction();
                    realm.delete(DiskonMpay.class);
                    realm.copyToRealm(response.body().getDiskonMpay());
                    realm.commitTransaction();
                    MangJekApplication.getInstance(MainActivity.this).setDiskonMpay(diskonMpay);

                    MfoodMitra mfoodMitra = response.body().getMfoodMitra();
                    realm.beginTransaction();
                    realm.delete(MfoodMitra.class);
                    realm.copyToRealm(response.body().getMfoodMitra());
                    realm.commitTransaction();
                    MangJekApplication.getInstance(MainActivity.this).setMfoodMitra(mfoodMitra);
                }
            }

            @Override
            public void onFailure(Call<GetFiturResponseJson> call, Throwable t) {

            }
        });
    }
}
