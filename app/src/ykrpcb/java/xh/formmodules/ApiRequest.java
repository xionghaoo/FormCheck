package xh.formmodules;

import com.zdtco.FUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import xh.formmodules.data.GeneralFormResult;
import xh.formmodules.data.GeneralPostResult;
import xh.formmodules.data.MachineResult;
import xh.formmodules.data.MultiColFormContentResult;
import xh.formmodules.data.MultiColFormTitleResult;
import xh.formmodules.data.UserResult;

/**
 * Created by G1494458 on 2017/12/8.
 */

public class ApiRequest {

    //http://10.182.15.165
    //http://pmd04.eavarytech.com:8999
    //10.52.38.95:8999
    
//    public static final String BASE_URL = "http://10.52.38.95:8999";
//    public static final String BASE_URL_CARD = "http://10.52.38.95:8999";

    //测试用
    public static final String BASE_URL = "http://10.182.15.165:8999";
    public static final String BASE_URL_CARD = "http://10.182.34.124:8999";

    ApiService apiService;
    JobCardService_YK jobCardService;

    public ApiRequest() {
        this.apiService = getRetrofit(BASE_URL).create(ApiService.class);
        this.jobCardService = getRetrofit(BASE_URL_CARD).create(JobCardService_YK.class);
    }

    public Observable<MachineResult> getMachineList(String factory) {
        return apiService.getMachineList(factory);
    }

    public Observable<GeneralFormResult> getGeneralFormList(String factory) {
        return apiService.getGeneralFormList(factory);
    }

    public Observable<MultiColFormTitleResult> getMultiColFormTitles(String factory) {
        return apiService.getMultiColFormTitles(factory);
    }

    public Observable<MultiColFormContentResult> getMultiColFormContents(String factory) {
        return apiService.getMultiColFormContents(factory);
    }

    public Observable<UserResult> getUserList(String factory) {
        return apiService.getUserList(factory);
    }

    public Observable<WorkNo> getWorkNo(String code) {
        return jobCardService.getWorkNo(code);
    }

    public Observable<GeneralPostResult> boundNFCCard(String db, String bindData) {
        return apiService.boundNFCCard(db, bindData);
    }

    public Observable<GeneralPostResult> postFormData(String db, String data) {
        return apiService.postFormData(db, data);
    }

    public Retrofit getRetrofit(String url) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .build();
        return new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(url)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }


}
