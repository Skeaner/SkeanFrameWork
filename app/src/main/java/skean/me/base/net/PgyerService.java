package skean.me.base.net;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * 蒲公英托管的服务
 */
public interface PgyerService {

    String BASE_URL = "http://www.pgyer.com/apiv1/app/";

    @FormUrlEncoded
    @POST("viewGroup")
    Observable<PgyAppInfo> getAppInfo(@Field("aId") String appId, @Field("_api_key") String apiKey);

    @GET("install")
    Call<ResponseBody> downLoadApk(@Query("aId") String appId, @Query("_api_key") String apiKey);

    @Multipart
    @POST("upload")
    Call<ResponseBody> uploadApk(@Part("uKey") RequestBody userkey, @Part("_api_key") RequestBody apiKey, @Part MultipartBody.Part file);

}
