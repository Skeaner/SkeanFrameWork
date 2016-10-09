package skean.me.base.net;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

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
