package base.net;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * 一些普通的下载服务
 */
public interface CommonService {

    /**
     * 没有用处, 会被全url替代
     */
    String BASE_URL = "http://useless.com/";

    @GET
    Call<ResponseBody> downLoad(@Url String url);


    @POST
    @Multipart
    @Streaming
    Call<ResponseBody> upload(@Url String url, @Part MultipartBody.Part file);


}
