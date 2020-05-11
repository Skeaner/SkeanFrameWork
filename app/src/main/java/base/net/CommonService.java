package base.net;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
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

}
