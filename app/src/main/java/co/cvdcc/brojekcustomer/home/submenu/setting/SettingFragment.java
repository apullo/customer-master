package co.cvdcc.brojekcustomer.home.submenu.setting;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import co.cvdcc.brojekcustomer.MangJekApplication;
import co.cvdcc.brojekcustomer.R;
import co.cvdcc.brojekcustomer.model.User;
import co.cvdcc.brojekcustomer.splash.SplashActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

/**
 * Created by bradhawk on 10/30/2016.
 */

public class SettingFragment extends Fragment {

    @BindView(R.id.setting_name)
    TextView settingProfileName;

    @BindView(R.id.setting_email)
    TextView settingProfileEmail;

    @BindView(R.id.setting_phone)
    TextView settingProfilePhone;

    @BindView(R.id.setting_editProfile)
    Button settingEditProfile;

    @BindView(R.id.setting_changePassword)
    LinearLayout settingChangePassword;

    @BindView(R.id.setting_termOfService)
    LinearLayout settingTermOfService;

    @BindView(R.id.setting_privacyPolicy)
    LinearLayout settingPrivacyPolicy;

    @BindView(R.id.setting_faq)
    LinearLayout settingFaq;



    @BindView(R.id.setting_rateThisApps)
    LinearLayout settingRateThisApps;

    @BindView(R.id.setting_logout)
    RelativeLayout settingLogout;

    private User loginUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);



        settingEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), UpdateProfileActivity.class));
            }
        });

        settingChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ChangePasswordActivity.class));
            }
        });

        settingTermOfService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), TermOfServiceActivity.class));
            }
        });

        settingPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), PrivacyPolicyActivity.class));
            }
        });

        settingRateThisApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String appPackageName = getActivity().getPackageName();
//                final String appPackageName = "net.gumcode.drivermangjek";
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });

        settingFaq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), FAQActivity.class));
            }
        });

        settingLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Realm realm = MangJekApplication.getInstance(getContext()).getRealmInstance();
                realm.beginTransaction();
                realm.delete(User.class);
                realm.commitTransaction();
                MangJekApplication.getInstance(getContext()).setLoginUser(null);
                startActivity(new Intent(getContext(), SplashActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loginUser = MangJekApplication.getInstance(getActivity()).getLoginUser();
        settingProfileName.setText(String.format("%s %s", loginUser.getNamaDepan(), loginUser.getNamaBelakang()));
        settingProfileEmail.setText(loginUser.getEmail());
        settingProfilePhone.setText(loginUser.getNoTelepon());
    }
}
