package co.cvdcc.brojekcustomer.mMassage;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import co.cvdcc.brojekcustomer.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by bradhawk on 12/20/2016.
 */

public class MassageActivity extends AppCompatActivity {

    @BindView(R.id.massage_container)
    FrameLayout container;

    private MenuMassageItem massageItem;
    private MassagePreference massagePreference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_massage);
        ButterKnife.bind(this);

        MenuMassageFragment menuMassage = new MenuMassageFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.massage_container, menuMassage).commit();
    }

    public void addFragmentBackstack(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.massage_container, fragment).addToBackStack(null).commit();
    }

    public MenuMassageItem getMassageItem() {
        return massageItem;
    }

    public void setMassageItem(MenuMassageItem massageItem) {
        this.massageItem = massageItem;
    }

    public MassagePreference getMassagePreference() {
        return massagePreference;
    }

    public void setMassagePreference(MassagePreference massagePreference) {
        this.massagePreference = massagePreference;
    }
}
