package xh.formmodules;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import xh.formmodules.data.AuditJudgementResult;
import xh.formmodules.data.CJDefineCellsResult;
import xh.formmodules.data.CustomCellResult;
import xh.formmodules.data.FormPostResult;
import xh.formmodules.data.FormulaResult;
import xh.formmodules.data.GeneralFormResult;
import xh.formmodules.data.MachineResult;
import xh.formmodules.data.MultiColFormContentResult;
import xh.formmodules.data.MultiColFormResult;
import xh.formmodules.data.MultiColFormTitleResult;
import xh.formmodules.data.GeneralPostResult;
import xh.formmodules.data.UserAndMachineResult;
import xh.formmodules.data.UserResult;

/**
 * Created by G1494458 on 2017/12/8.
 */

public class ApiRequest {

    public static final String BASE_URL = "http://10.182.15.165/";
    public static final String BASE_URL_CARD = "http://10.182.34.124:8999/";

    ApiService apiService;
    JobCardService jobCardService;

    public ApiRequest() {
        this.apiService = getRetrofit(BASE_URL).create(ApiService.class);
        this.jobCardService = getRetrofit(BASE_URL_CARD).create(JobCardService.class);
    }

    public Observable<MachineResult> getMachineList(String factory) {
        return apiService.getMachineList(factory);
    }

    public Observable<GeneralFormResult> getGeneralFormList(String factory) {
        return apiService.getGeneralFormList(factory);
    }

    public Observable<MultiColFormResult> getMultiColFormList(String factory) {
        return apiService.getMultiColFormList(factory);
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

    public Observable<CustomCellResult> getCustomCell() {
        return apiService.getCustomCell();
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

    public Observable<FormPostResult> postCJFormData(String db, String data) {
        return apiService.postCJFormData(db, data);
    }

    public Observable<GeneralPostResult> executeFormula(String db, String formula, String params) {
        return apiService.executeFormula(db, formula, params);
    }

    public Observable<FormulaResult> getFormulas(String db) {
        return apiService.getFormulas(db);
    }

    public Observable<GeneralPostResult> bindLineLimitTime(String db, String bindData) {
        return apiService.bindLineLimitTime(db, bindData);
    }

    public Observable<CJDefineCellsResult> getCJDefineCells(String db, String reportCode, String partNum, String lineNo) {
        return apiService.getCJDefineCells(db, reportCode, partNum, lineNo);
    }

    public Observable<GeneralPostResult> postAuditJudgement(String db,
                                                            String workNo,
                                                            String paperNo,
                                                            String index,
                                                            String auditdate,
                                                            String audittime,
                                                            String judgement,
                                                            String reportcode,
                                                            String audit) {
        return apiService.postAuditJudgement(db, workNo, paperNo, index, auditdate, audittime, judgement, reportcode, audit);
    }

    public Observable<AuditJudgementResult> getAuditJudgements(String db,
                                                               String paperNo) {
        return apiService.getAuditJudgements(db, paperNo);
    }

    public Observable<UserAndMachineResult> getUserMachineBounds(String db) {
        return apiService.getUserMachineBounds(db);
    }

    public Retrofit getRetrofit(String url) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        return new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(url)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }


}
