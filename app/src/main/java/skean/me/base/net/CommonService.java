package skean.me.base.net;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * 一些普通的下载服务
 */
public interface CommonService {

    @GET
    Call<ResponseBody> downLoad();

}
