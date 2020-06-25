package co.cvdcc.brojekcustomer.api.service;

import co.cvdcc.brojekcustomer.model.json.book.CheckStatusTransaksiRequest;
import co.cvdcc.brojekcustomer.model.json.book.CheckStatusTransaksiResponse;
import co.cvdcc.brojekcustomer.model.json.book.GetAdditionalMboxResponseJson;
import co.cvdcc.brojekcustomer.model.json.book.GetDataMserviceResponseJson;
import co.cvdcc.brojekcustomer.model.json.book.GetDataRestoByKategoriRequestJson;
import co.cvdcc.brojekcustomer.model.json.book.GetDataRestoByKategoriResponseJson;
import co.cvdcc.brojekcustomer.model.json.book.GetDataRestoRequestJson;
import co.cvdcc.brojekcustomer.model.json.book.GetDataRestoResponseJson;
import co.cvdcc.brojekcustomer.model.json.book.LiatLokasiDriverResponse;
import co.cvdcc.brojekcustomer.model.json.book.detailTransaksi.GetDataTransaksiMMartResponse;
import co.cvdcc.brojekcustomer.model.json.book.detailTransaksi.GetDataTransaksiMSendResponse;
import co.cvdcc.brojekcustomer.model.json.book.detailTransaksi.GetDataTransaksiRequest;
import co.cvdcc.brojekcustomer.model.json.book.GetFoodRestoRequestJson;
import co.cvdcc.brojekcustomer.model.json.book.GetFoodRestoResponseJson;
import co.cvdcc.brojekcustomer.model.json.book.GetKendaraanAngkutResponseJson;
import co.cvdcc.brojekcustomer.model.json.book.GetNearBoxRequestJson;
import co.cvdcc.brojekcustomer.model.json.book.GetNearBoxResponseJson;
import co.cvdcc.brojekcustomer.model.json.book.GetNearRideCarRequestJson;
import co.cvdcc.brojekcustomer.model.json.book.GetNearRideCarResponseJson;
import co.cvdcc.brojekcustomer.model.json.book.GetNearServiceRequestJson;
import co.cvdcc.brojekcustomer.model.json.book.GetNearServiceResponseJson;
import co.cvdcc.brojekcustomer.model.json.book.MboxRequestJson;
import co.cvdcc.brojekcustomer.model.json.book.MboxResponseJson;
import co.cvdcc.brojekcustomer.model.json.book.MserviceRequestJson;
import co.cvdcc.brojekcustomer.model.json.book.MserviceResponseJson;
import co.cvdcc.brojekcustomer.model.json.book.RequestFoodRequestJson;
import co.cvdcc.brojekcustomer.model.json.book.RequestFoodResponseJson;
import co.cvdcc.brojekcustomer.model.json.book.RequestMartRequestJson;
import co.cvdcc.brojekcustomer.model.json.book.RequestMartResponseJson;
import co.cvdcc.brojekcustomer.model.json.book.RequestRideCarRequestJson;
import co.cvdcc.brojekcustomer.model.json.book.RequestRideCarResponseJson;
import co.cvdcc.brojekcustomer.model.json.book.RequestSendRequestJson;
import co.cvdcc.brojekcustomer.model.json.book.RequestSendResponseJson;
import co.cvdcc.brojekcustomer.model.json.book.SearchRestoranFoodRequest;
import co.cvdcc.brojekcustomer.model.json.book.SearchRestoranFoodResponse;
import co.cvdcc.brojekcustomer.model.json.book.massage.DetailTransaksiRequest;
import co.cvdcc.brojekcustomer.model.json.book.massage.DetailTransaksiResponse;
import co.cvdcc.brojekcustomer.model.json.book.massage.GetLayananMassageResponseJson;
import co.cvdcc.brojekcustomer.model.json.book.massage.RequestMassageRequestJson;
import co.cvdcc.brojekcustomer.model.json.book.massage.RequestMassageResponseJson;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by bradhawk on 10/17/2016.
 */

public interface BookService {

    @POST("book/list_driver_mride")
    Call<GetNearRideCarResponseJson> getNearRide(@Body GetNearRideCarRequestJson param);

    @POST("book/list_driver_mcar")
    Call<GetNearRideCarResponseJson> getNearCar(@Body GetNearRideCarRequestJson param);

    @POST("book/request_transaksi")
    Call<RequestRideCarResponseJson> requestTransaksi(@Body RequestRideCarRequestJson param);

    @POST("book/request_transaksi_mmart")
    Call<RequestMartResponseJson> requestTransaksiMMart(@Body RequestMartRequestJson param);

    @POST("book/request_transaksi_msend")
    Call<RequestSendResponseJson> requestTransMSend(@Body RequestSendRequestJson param);

    @GET("book/get_kendaraan_angkut")
    Call<GetKendaraanAngkutResponseJson> getKendaraanAngkut();

    @POST("book/list_driver_mbox")
    Call<GetNearBoxResponseJson> getNearBox(@Body GetNearBoxRequestJson param);

    @POST("book/request_transaksi_mbox")
    Call<MboxResponseJson> requestTransaksiMbox(@Body MboxRequestJson param);

    @GET("book/get_additional_mbox")
    Call<GetAdditionalMboxResponseJson> getAdditionalMbox();

    @POST("book/list_driver_mservice")
    Call<GetNearServiceResponseJson> getNearService(@Body GetNearServiceRequestJson param);

    @POST("book/request_transaksi_mservice")
    Call<MserviceResponseJson> requestTransaksi(@Body MserviceRequestJson param);

    @GET("book/get_data_mservice_ac")
    Call<GetDataMserviceResponseJson> getDataMservice();

    @GET("book/get_layanan_massage")
    Call<GetLayananMassageResponseJson> getLayananMassage();

    @POST("book/request_transaksi_mmassage")
    Call<RequestMassageResponseJson> requestTransaksiMMassage(@Body RequestMassageRequestJson param);

    @POST("book/list_driver_mmassage")
    Call<GetNearRideCarResponseJson> getNearMassage(@Body GetNearRideCarRequestJson param);

    @POST("book/get_data_transaksi_mmassage")
    Call<DetailTransaksiResponse> getDetailTransaksiMassage(@Body DetailTransaksiRequest param);

    @POST("book/get_data_restoran")
    Call<GetDataRestoResponseJson> getDataRestoran(@Body GetDataRestoRequestJson param);

    @POST("book/get_food_in_resto")
    Call<GetFoodRestoResponseJson> getFoodResto(@Body GetFoodRestoRequestJson param);

    @POST("book/search_restoran_or_food")
    Call<SearchRestoranFoodResponse> searchRestoranOrFood(@Body SearchRestoranFoodRequest param);

    @POST("book/get_resto_by_kategori")
    Call<GetDataRestoByKategoriResponseJson> getDataRestoranByKategori(@Body GetDataRestoByKategoriRequestJson param);

    @POST("book/request_transaksi_mfood")
    Call<RequestFoodResponseJson> requestTransaksiMFood(@Body RequestFoodRequestJson param);

    @POST("book/check_status_transaksi")
    Call<CheckStatusTransaksiResponse> checkStatusTransaksi(@Body CheckStatusTransaksiRequest param);

	@GET("book/liat_lokasi_driver/{id}")
	Call<LiatLokasiDriverResponse> liatLokasiDriver(@Path("id") String idDriver);
	
	@POST("book/get_data_order_mmassage")
	Call<String> getDataOrderMMassage(@Body GetDataTransaksiRequest param);
	
	@POST("book/get_data_transaksi_mfood")
	Call<String> getDataTransaksiMFood(@Body GetDataTransaksiRequest param);
	
	@POST("book/get_data_transaksi_mservice")
	Call<String> getDataTransaksiMService(@Body GetDataTransaksiRequest param);
	
	@POST("book/get_data_transaksi_mmart")
	Call<GetDataTransaksiMMartResponse> getDataTransaksiMMart(@Body GetDataTransaksiRequest param);
	
	@POST("book/get_data_transaksi_mbox")
	Call<String> getDataTransaksiMBox(@Body GetDataTransaksiRequest param);
	
	@POST("book/get_data_transaksi_msend")
	Call<GetDataTransaksiMSendResponse> getDataTransaksiMSend(@Body GetDataTransaksiRequest param);
}
