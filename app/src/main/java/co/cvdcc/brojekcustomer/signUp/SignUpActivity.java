package co.cvdcc.brojekcustomer.signUp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import co.cvdcc.brojekcustomer.R;
import co.cvdcc.brojekcustomer.api.ServiceGenerator;
import co.cvdcc.brojekcustomer.api.service.UserService;
import co.cvdcc.brojekcustomer.model.FirebaseToken;
import co.cvdcc.brojekcustomer.model.json.user.RegisterRequestJson;
import co.cvdcc.brojekcustomer.model.json.user.RegisterResponseJson;
import co.cvdcc.brojekcustomer.utils.DialogActivity;
import co.cvdcc.brojekcustomer.utils.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by bradhawk on 10/12/2016.
 */

public class SignUpActivity extends DialogActivity implements Validator.ValidationListener {

    public static final int SIGNUP_ID = 110;
    public static final String USER_KEY = "UserKey";
    private static final String TAG = "SignUpActivity";
    @NotEmpty
    @BindView(R.id.signUp_firstName)
    EditText textFirstName;

    @NotEmpty
    @BindView(R.id.signUp_lastName)
    EditText textLastName;

    @NotEmpty
    @Email
    @BindView(R.id.signUp_email)
    EditText textEmail;

    @NotEmpty
    @Password
    @BindView(R.id.signUp_password)
    EditText textPassword;

    @NotEmpty
    @BindView(R.id.signUp_phone)
    EditText textPhone;

    @NotEmpty
    @BindView(R.id.signUp_address)
    EditText textAddress;

//    @NotEmpty
//    @BindView(R.id.signUp_pob)
//    EditText textPlaceOfBirth;
//
//    @NotEmpty
//    @BindView(R.id.signUp_dob)
//    EditText textDateOfBirth;

    @BindView(R.id.signUp_signUpButton)
    Button buttonSignUp;

    @BindView(R.id.signUp_signInButton)
    LinearLayout buttonSignIn;

    Calendar calendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };

    private Validator validator;

    private void updateLabel() {
        String format = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
//        textDateOfBirth.setText(sdf.format(calendar.getTime()));
    }

    private void showDatePicker() {
        new DatePickerDialog(this, date, calendar
                .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        validator = new Validator(this);
        validator.setValidationListener(this);

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validator.validate();
            }
        });

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

//        textDateOfBirth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean b) {
//                if (b) showDatePicker();
//            }
//        });
    }

    private void onSignUpClick() {
        showProgressDialog(R.string.dialog_loading);
        RegisterRequestJson request = new RegisterRequestJson();
        request.setNamaDepan(textFirstName.getText().toString());
        request.setNamaBelakang(textLastName.getText().toString());
        request.setEmail(textEmail.getText().toString());
        request.setPassword(textPassword.getText().toString());
        request.setNoTelepon(textPhone.getText().toString());
        request.setAlamat(textAddress.getText().toString());
//        request.setTempatLahir(textPlaceOfBirth.getText().toString());
//        request.setTglLahir(textDateOfBirth.getText().toString());

        Realm realm = Realm.getDefaultInstance();
        FirebaseToken token = realm.where(FirebaseToken.class).findFirst();
        Log.e(TAG, "onSignUpClick: " + token);
        if (token != null) {
            request.setRegId(token.getTokenId());
        }

        UserService service = ServiceGenerator.createService(UserService.class, request.getEmail(), request.getPassword());
        service.register(request).enqueue(new Callback<RegisterResponseJson>() {
            @Override
            public void onResponse(Call<RegisterResponseJson> call, Response<RegisterResponseJson> response) {
                hideProgressDialog();
                if (response.isSuccessful()) {
                    if (response.body().getMessage().equalsIgnoreCase("success")) {
                        Intent retIntent = getIntent();
                        retIntent.putExtra(USER_KEY, response.body().getData().get(0));
                        setResult(Activity.RESULT_OK, retIntent);
                        finish();
                    } else {
                        Toast.makeText(SignUpActivity.this, "Register gagal", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(SignUpActivity.this, "System error", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<RegisterResponseJson> call, @NonNull Throwable t) {
                hideProgressDialog();
                t.printStackTrace();
                Toast.makeText(SignUpActivity.this, "System error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onValidationSucceeded() {
        onSignUpClick();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    @SuppressWarnings("unused")
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FirebaseToken response) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(FirebaseToken.class);
        realm.copyToRealm(response);
        realm.commitTransaction();
    }
}
