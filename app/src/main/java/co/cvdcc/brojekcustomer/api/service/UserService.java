package co.cvdcc.brojekcustomer.api.service;

import co.cvdcc.brojekcustomer.model.json.book.RateDriverRequestJson;
import co.cvdcc.brojekcustomer.model.json.book.RateDriverResponseJson;
import co.cvdcc.brojekcustomer.model.json.fcm.CancelBookRequestJson;
import co.cvdcc.brojekcustomer.model.json.fcm.CancelBookResponseJson;
import co.cvdcc.brojekcustomer.model.json.menu.HelpRequestJson;
import co.cvdcc.brojekcustomer.model.json.menu.HelpResponseJson;
import co.cvdcc.brojekcustomer.model.json.menu.HistoryRequestJson;
import co.cvdcc.brojekcustomer.model.json.menu.HistoryResponseJson;
import co.cvdcc.brojekcustomer.model.json.menu.VersionRequestJson;
import co.cvdcc.brojekcustomer.model.json.menu.VersionResponseJson;
import co.cvdcc.brojekcustomer.model.json.user.ChangePasswordRequestJson;
import co.cvdcc.brojekcustomer.model.json.user.ChangePasswordResponseJson;
import co.cvdcc.brojekcustomer.model.json.user.GetBannerResponseJson;
import co.cvdcc.brojekcustomer.model.json.user.GetFiturResponseJson;
import co.cvdcc.brojekcustomer.model.json.user.GetSaldoRequestJson;
import co.cvdcc.brojekcustomer.model.json.user.GetSaldoResponseJson;
import co.cvdcc.brojekcustomer.model.json.user.LoginRequestJson;
import co.cvdcc.brojekcustomer.model.json.user.LoginResponseJson;
import co.cvdcc.brojekcustomer.model.json.user.RegisterRequestJson;
import co.cvdcc.brojekcustomer.model.json.user.RegisterResponseJson;
import co.cvdcc.brojekcustomer.model.json.user.TopupRequestJson;
import co.cvdcc.brojekcustomer.model.json.user.TopupResponseJson;
import co.cvdcc.brojekcustomer.model.json.user.UpdateProfileRequestJson;
import co.cvdcc.brojekcustomer.model.json.user.UpdateProfileResponseJson;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by bradhawk on 10/13/2016.
 */

public interface UserService {

    @POST("pelanggan/login")
    Call<LoginResponseJson> login(@Body LoginRequestJson param);

    @POST("pelanggan/register_user")
    Call<RegisterResponseJson> register(@Body RegisterRequestJson param);

    @POST("pelanggan/get_saldo")
    Call<GetSaldoResponseJson> getSaldo(@Body GetSaldoRequestJson param);

    @GET("pelanggan/detail_fitur")
    Call<GetFiturResponseJson> getFitur();

    @POST("pelanggan/user_send_help")
    Call<HelpResponseJson> sendHelp(@Body HelpRequestJson param);

    @POST("pelanggan/update_profile")
    Call<UpdateProfileResponseJson> updateProfile(@Body UpdateProfileRequestJson param);

    @POST("pelanggan/change_password")
    Call<ChangePasswordResponseJson> changePassword(@Body ChangePasswordRequestJson param);

    @POST("book/user_cancel_transaction")
    Call<CancelBookResponseJson> cancelOrder(@Body CancelBookRequestJson param);

    @POST("pelanggan/check_version")
    Call<VersionResponseJson> checkVersion(@Body VersionRequestJson param);

    @POST("book/user_rate_driver")
    Call<RateDriverResponseJson> rateDriver(@Body RateDriverRequestJson param);

    @POST("pelanggan/verifikasi_topup")
    Call<TopupResponseJson> topUp(@Body TopupRequestJson param);

    @POST("pelanggan/complete_transaksi")
    Call<HistoryResponseJson> getCompleteHistory(@Body HistoryRequestJson param);

    @POST("pelanggan/inprogress_transaksi")
    Call<HistoryResponseJson> getOnProgressHistory(@Body HistoryRequestJson param);

    @GET("pelanggan/banner_promosi")
    Call<GetBannerResponseJson> getBanner();

}
