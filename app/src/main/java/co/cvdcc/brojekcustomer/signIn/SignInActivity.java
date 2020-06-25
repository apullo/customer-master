package co.cvdcc.brojekcustomer.signIn;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import co.cvdcc.brojekcustomer.MangJekApplication;
import co.cvdcc.brojekcustomer.R;
import co.cvdcc.brojekcustomer.api.ServiceGenerator;
import co.cvdcc.brojekcustomer.api.service.UserService;
import co.cvdcc.brojekcustomer.home.MainActivity;
import co.cvdcc.brojekcustomer.model.FirebaseToken;
import co.cvdcc.brojekcustomer.model.User;
import co.cvdcc.brojekcustomer.model.json.user.LoginRequestJson;
import co.cvdcc.brojekcustomer.model.json.user.LoginResponseJson;
import co.cvdcc.brojekcustomer.signUp.SignUpActivity;
import co.cvdcc.brojekcustomer.utils.DialogActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Headers;

/**
 * Created by bradhawk on 10/12/2016.
 */

public class SignInActivity extends DialogActivity implements Validator.ValidationListener {

    private static final String TAG = "SignInActivity";

    @NotEmpty
    @Email
    @BindView(R.id.signIn_email)
    EditText textEmail;

    @NotEmpty
    @BindView(R.id.signIn_password)
    EditText textPassword;

    @BindView(R.id.signIn_signInButton)
    Button buttonSignIn;

//    @BindView(R.id.signUpButton)
//    LinearLayout buttonSignUp;

    Validator validator;

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
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);

        validator = new Validator(this);
        validator.setValidationListener(this);

        findViewById(R.id.signUpButton).setOnClickListener(view -> {
            Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
            startActivityForResult(intent, SignUpActivity.SIGNUP_ID);
        });

        buttonSignIn.setOnClickListener(view -> validator.validate());
    }
    @Headers("Content-Type: application/json")
    private void onSignInClick() {
        showProgressDialog(R.string.dialog_loading);
        LoginRequestJson request = new LoginRequestJson();
        request.setEmail(textEmail.getText().toString());
        request.setPassword(textPassword.getText().toString());
//        Gson gson = new GsonBuilder()
//                .setLenient()
//                .create();
        try {

            Realm realm = Realm.getDefaultInstance();
            FirebaseToken token = realm.where(FirebaseToken.class).findFirst();
            if (token.getTokenId() != null) {
                request.setRegId(token.getTokenId());
            } else {
                Toast.makeText(this, R.string.waiting_pleaseWait, Toast.LENGTH_SHORT).show();
                hideProgressDialog();
                return;
            }

            UserService service = ServiceGenerator.createService(UserService.class, request.getEmail(), request.getPassword());
            service.login(request).enqueue(new Callback<LoginResponseJson>() {
                @Override
                public void onResponse(@NonNull Call<LoginResponseJson> call, @NonNull Response<LoginResponseJson> response) {
                    try {

                        hideProgressDialog();
                        if (response.isSuccessful()) {
                            if (response.body().getMessage().equalsIgnoreCase("found")) {
                                User user = response.body().getData().get(0);

                                saveUser(user);

                                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(SignInActivity.this, "Username atau Password salah", Toast.LENGTH_SHORT).show();
                            }
                        }

                    } catch (Exception e) {

                        Toast.makeText(SignInActivity.this, "Error tyr2  "+e.toString(), Toast.LENGTH_SHORT).show();

                    }
                }


                @Override
                public void onFailure(@NonNull Call<LoginResponseJson> call, @NonNull Throwable t) {
                    hideProgressDialog();
                    t.printStackTrace();
                    Toast.makeText(SignInActivity.this, "System error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//                textEmail.setText(t.toString());
                }
            });

        }catch (Exception e)
        {
            Toast.makeText(SignInActivity.this, "try error: " + e.toString(), Toast.LENGTH_LONG).show();

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SignUpActivity.SIGNUP_ID) {
            if (resultCode == Activity.RESULT_OK) {
                User user = (User) data.getSerializableExtra(SignUpActivity.USER_KEY);

                saveUser(user);

                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    public void onValidationSucceeded() {
        onSignInClick();
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

    private void saveUser(User user) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(User.class);
        realm.copyToRealm(user);
        realm.commitTransaction();

        MangJekApplication.getInstance(SignInActivity.this).setLoginUser(user);
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
