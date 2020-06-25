package co.cvdcc.brojekcustomer.home.submenu.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import co.cvdcc.brojekcustomer.MangJekApplication;
import co.cvdcc.brojekcustomer.R;
import co.cvdcc.brojekcustomer.api.ServiceGenerator;
import co.cvdcc.brojekcustomer.api.service.UserService;
import co.cvdcc.brojekcustomer.model.FirebaseToken;
import co.cvdcc.brojekcustomer.model.User;
import co.cvdcc.brojekcustomer.model.json.user.ChangePasswordRequestJson;
import co.cvdcc.brojekcustomer.model.json.user.ChangePasswordResponseJson;
import co.cvdcc.brojekcustomer.model.json.user.LoginRequestJson;
import co.cvdcc.brojekcustomer.model.json.user.LoginResponseJson;
import co.cvdcc.brojekcustomer.utils.DialogActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends DialogActivity {
    @BindView(R.id.current_password)
    EditText currentpassword;
    @BindView(R.id.new_password)
    EditText newPassword;
    @BindView(R.id.confirm_new_password)
    EditText confirmNewPassword;
    @BindView(R.id.change_password)
    Button changePassword;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);
        user = MangJekApplication.getInstance(this).getLoginUser();

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!newPassword.getText().toString().equals(confirmNewPassword.getText().toString())){
                    Toast.makeText(getApplicationContext(), "Password not match!", Toast.LENGTH_SHORT).show();
                    return;
                }
                showProgressDialog("Loading...");
                ChangePasswordRequestJson request = new ChangePasswordRequestJson();
                request.id = user.getId();
                request.email = user.getEmail();
                request.current_password = currentpassword.getText().toString();
                request.new_password = newPassword.getText().toString();

                UserService service = ServiceGenerator.createService(UserService.class, user.getEmail(), user.getPassword());
                service.changePassword(request).enqueue(new Callback<ChangePasswordResponseJson>() {
                    @Override
                    public void onResponse(Call<ChangePasswordResponseJson> call, Response<ChangePasswordResponseJson> response) {
                        if (response.isSuccessful()) {
                            if (response.body().message.equals("success")) {
                                hideProgressDialog();
                                update_data();
                                Toast.makeText(ChangePasswordActivity.this, "Updated", Toast.LENGTH_SHORT).show();

                            } else {
                                String message = response.body().message;
                                Toast.makeText(ChangePasswordActivity.this, message, Toast.LENGTH_SHORT).show();
                                hideProgressDialog();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ChangePasswordResponseJson> call, Throwable t) {
                        t.printStackTrace();
                        hideProgressDialog();
                        Toast.makeText(ChangePasswordActivity.this, "System error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }

    private void update_data() {
        showProgressDialog(R.string.dialog_loading);
        LoginRequestJson request = new LoginRequestJson();
        request.setEmail(user.getEmail());
        request.setPassword(newPassword.getText().toString());

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
            public void onResponse(Call<LoginResponseJson> call, Response<LoginResponseJson> response) {
                hideProgressDialog();
                if (response.isSuccessful()) {
                    if (response.body().getMessage().equalsIgnoreCase("found")) {
                        User user = response.body().getData().get(0);

                        saveUser(user);

                        Intent intent = new Intent(ChangePasswordActivity.this, ChangePasswordActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(ChangePasswordActivity.this, "Username atau Password salah", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponseJson> call, Throwable t) {
                hideProgressDialog();
                t.printStackTrace();
                Toast.makeText(ChangePasswordActivity.this, "System error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveUser(User user) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(User.class);
        realm.copyToRealm(user);
        realm.commitTransaction();

        MangJekApplication.getInstance(ChangePasswordActivity.this).setLoginUser(user);
    }

}
