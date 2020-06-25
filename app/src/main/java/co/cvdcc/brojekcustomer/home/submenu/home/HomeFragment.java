package co.cvdcc.brojekcustomer.home.submenu.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.google.android.material.snackbar.Snackbar;

import co.cvdcc.brojekcustomer.MangJekApplication;
import co.cvdcc.brojekcustomer.R;
import co.cvdcc.brojekcustomer.api.ServiceGenerator;
import co.cvdcc.brojekcustomer.api.service.UserService;
import co.cvdcc.brojekcustomer.home.submenu.TopUpActivity;
import co.cvdcc.brojekcustomer.mBox.BoxActivity;
import co.cvdcc.brojekcustomer.mFood.FoodActivity;
import co.cvdcc.brojekcustomer.mMart.MartActivity;
import co.cvdcc.brojekcustomer.mMassage.MassageActivity;
import co.cvdcc.brojekcustomer.mRideCar.RideCarActivity;
import co.cvdcc.brojekcustomer.mSend.SendActivity;
import co.cvdcc.brojekcustomer.mService.mServiceActivity;
import co.cvdcc.brojekcustomer.model.Banner;
import co.cvdcc.brojekcustomer.model.Fitur;
import co.cvdcc.brojekcustomer.model.User;
import co.cvdcc.brojekcustomer.model.json.user.GetBannerResponseJson;
import co.cvdcc.brojekcustomer.model.json.user.GetSaldoRequestJson;
import co.cvdcc.brojekcustomer.model.json.user.GetSaldoResponseJson;
import co.cvdcc.brojekcustomer.splash.SplashActivity;
import co.cvdcc.brojekcustomer.utils.ConnectivityUtils;
import co.cvdcc.brojekcustomer.utils.Log;
import co.cvdcc.brojekcustomer.utils.SnackbarController;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;
import io.realm.Realm;
import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by bradhawk on 10/10/2016.
 */

public class HomeFragment extends Fragment {

    @BindView(R.id.home_mCar)
    RelativeLayout buttonMangCar;

    @BindView(R.id.home_mRide)
    RelativeLayout buttonMangRide;

    @BindView(R.id.home_mSend)
    RelativeLayout buttonMangSend;

    @BindView(R.id.home_mBox)
    RelativeLayout buttonMangBox;

    @BindView(R.id.home_mMart)
    RelativeLayout buttonMangMart;

    @BindView(R.id.home_mMassage)
    RelativeLayout buttonMangMassage;

    @BindView(R.id.home_mFood)
    RelativeLayout buttonMangFood;

    @BindView(R.id.home_mService)
    RelativeLayout butonMangService;

    @BindView(R.id.home_mPayBalance)
    TextView mPayBalance;

    @BindView(R.id.home_topUpButton)
    Button topUpButton;

    @BindView(R.id.slide_viewPager)
    AutoScrollViewPager slideViewPager;

    @BindView(R.id.slide_viewPager_indicator)
    CircleIndicator slideIndicator;

    private SnackbarController snackbarController;

    private boolean connectionAvailable;
    private boolean isDataLoaded = false;

    private Realm realm;

    private int successfulCall;

    public ArrayList<Banner> banners = new ArrayList<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SnackbarController) {
            snackbarController = (SnackbarController) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);


        buttonMangRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMangRideClick();
            }
        });
        buttonMangCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMangCarClick();
            }
        });
        buttonMangSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMangSendClick();
            }
        });
        buttonMangBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMangBoxClick();
            }
        });
        connectionAvailable = false;
        topUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTopUpClick();
            }
        });
        buttonMangMart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMangMartClick();
            }
        });
        butonMangService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMServiceClick();
            }
        });
        buttonMangMassage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMMassageClick();
            }
        });
        buttonMangFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMangFoodClick();
            }
        });

        realm = MangJekApplication.getInstance(getActivity()).getRealmInstance();
        getImageBanner();


    }

    private void getImageBanner() {
        User loginUser = new User();
        if (MangJekApplication.getInstance(getActivity()).getLoginUser() != null) {
            loginUser = MangJekApplication.getInstance(getActivity()).getLoginUser();
        } else {
            startActivity(new Intent(getActivity(), SplashActivity.class));
            getActivity().finish();
        }

        UserService userService = ServiceGenerator.createService(UserService.class,
                loginUser.getEmail(), loginUser.getPassword());
        userService.getBanner().enqueue(new Callback<GetBannerResponseJson>() {
            @Override
            public void onResponse(Call<GetBannerResponseJson> call, Response<GetBannerResponseJson> response) {
                if (response.isSuccessful()) {
                    banners = response.body().data;
                    Log.e("Image", response.body().data.get(0).foto);
                    MyPagerAdapter pagerAdapter = new MyPagerAdapter(getFragmentManager(), banners);
                    slideViewPager.setAdapter(pagerAdapter);
                    slideIndicator.setViewPager(slideViewPager);
                    slideViewPager.setInterval(20000);
                    slideViewPager.startAutoScroll(20000);
                }
            }

            @Override
            public void onFailure(Call<GetBannerResponseJson> call, Throwable t) {

            }
        });
    }

    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 5;
        public ArrayList<Banner> banners = new ArrayList<>();

        public MyPagerAdapter(FragmentManager fragmentManager, ArrayList<Banner> banners) {
            super(fragmentManager);
            this.banners = banners;
        }

        @Override
        public int getCount() {
            return banners.size();
        }

        @Override
        public Fragment getItem(int position) {
            return SlideFragment.newInstance(banners.get(position).id, banners.get(position).foto);
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
    public void onResume() {
        super.onResume();
        successfulCall = 0;
        connectionAvailable = ConnectivityUtils.isConnected(getActivity());
        if (!connectionAvailable) {
            if (snackbarController != null) snackbarController.showSnackbar(
                    R.string.text_noInternet, Snackbar.LENGTH_INDEFINITE, R.string.text_close,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            return;
                        }
                    });
        } else {
            updateMPayBalance();
        }
    }

    private void onMangSendClick() {
        Fitur selectedFitur = realm.where(Fitur.class).equalTo("idFitur", 5).findFirst();
        Intent intent = new Intent(getActivity(), SendActivity.class);
        intent.putExtra(SendActivity.FITUR_KEY, selectedFitur.getIdFitur());
        getActivity().startActivity(intent);
    }

    private void onTopUpClick() {
        Intent intent = new Intent(getActivity(), TopUpActivity.class);
        startActivity(intent);
    }

    private void onMangRideClick() {
        Fitur selectedFitur = realm.where(Fitur.class).equalTo("idFitur", 1).findFirst();
        Intent intent = new Intent(getActivity(), RideCarActivity.class);
        intent.putExtra(RideCarActivity.FITUR_KEY, selectedFitur.getIdFitur());
        getActivity().startActivity(intent);
    }

    private void onMangCarClick() {
        Fitur selectedFitur = realm.where(Fitur.class).equalTo("idFitur", 2).findFirst();
        Intent intent = new Intent(getActivity(), RideCarActivity.class);
        intent.putExtra(RideCarActivity.FITUR_KEY, selectedFitur.getIdFitur());
        getActivity().startActivity(intent);
    }

    private void onMangMartClick() {
        Fitur selectedFitur = realm.where(Fitur.class).equalTo("idFitur", 4).findFirst();
        Intent intent = new Intent(getActivity(), MartActivity.class);
        intent.putExtra(MartActivity.FITUR_KEY, selectedFitur.getIdFitur());
        getActivity().startActivity(intent);
    }

    private void onMangBoxClick() {
        Fitur selectedFitur = realm.where(Fitur.class).equalTo("idFitur", 7).findFirst();
        Intent intent = new Intent(getActivity(), BoxActivity.class);
        intent.putExtra(BoxActivity.FITUR_KEY, selectedFitur.getIdFitur());
        getActivity().startActivity(intent);
    }

    private void onMServiceClick() {
        Fitur selectedFitur = realm.where(Fitur.class).equalTo("idFitur", 8).findFirst();
        Intent intent = new Intent(getActivity(), mServiceActivity.class);
        intent.putExtra(mServiceActivity.FITUR_KEY, selectedFitur.getIdFitur());
        getActivity().startActivity(intent);
    }

    private void onMMassageClick() {
        Fitur selectedFitur = realm.where(Fitur.class).equalTo("idFitur", 6).findFirst();
        Intent intent = new Intent(getActivity(), MassageActivity.class);
        intent.putExtra(mServiceActivity.FITUR_KEY, selectedFitur.getIdFitur());
        getActivity().startActivity(intent);
    }

    private void onMangFoodClick() {
        Fitur selectedFitur = realm.where(Fitur.class).equalTo("idFitur", 3).findFirst();
        Intent intent = new Intent(getActivity(), FoodActivity.class);
        intent.putExtra(FoodActivity.FITUR_KEY, selectedFitur.getIdFitur());
        getActivity().startActivity(intent);
    }


    private void updateMPayBalance() {
        User loginUser = MangJekApplication.getInstance(getActivity()).getLoginUser();
        UserService userService = ServiceGenerator.createService(
                UserService.class, loginUser.getEmail(), loginUser.getPassword());

        GetSaldoRequestJson param = new GetSaldoRequestJson();
        param.setId(loginUser.getId());
        userService.getSaldo(param).enqueue(new Callback<GetSaldoResponseJson>() {
            @Override
            public void onResponse(Call<GetSaldoResponseJson> call, Response<GetSaldoResponseJson> response) {
                if (response.isSuccessful()) {
                    String formattedText = String.format(Locale.US, "Rp. %s ,-",
                            NumberFormat.getNumberInstance(Locale.US).format(response.body().getData()));
                    mPayBalance.setText(formattedText);
                    successfulCall++;

                    if(HomeFragment.this.getActivity() != null) {
                        Realm realm = MangJekApplication.getInstance(HomeFragment.this.getActivity()).getRealmInstance();
                        User loginUser = MangJekApplication.getInstance(HomeFragment.this.getActivity()).getLoginUser();
                        realm.beginTransaction();
                        loginUser.setmPaySaldo(response.body().getData());
                        realm.commitTransaction();
                    }
                }
            }

            @Override
            public void onFailure(Call<GetSaldoResponseJson> call, Throwable t) {

            }
        });
    }


}
