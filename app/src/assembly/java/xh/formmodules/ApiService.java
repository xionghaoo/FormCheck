package xh.formmodules;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
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
 * Created by G1494458 on 2017/12/7.
 */

public interface ApiService {
    ///http://10.182.15.165/e_system_interface/GetAllPadMachineListN.aspx?db=SzFpca
    //设备列表
    @GET("e_system_interface/GetAllPadMachineListN.aspx")
    Observable<MachineResult> getMachineList(@Query("db") String db);

    //http://10.182.15.165/e_system_interface/GetAllPadBYDetail.aspx?db=SzFpca
    //保养表
    @GET("e_system_interface/GetAllPadBYDetail.aspx")
    Observable<MultiColFormResult> getMultiColFormList(@Query("db") String db);

    // http://10.182.15.165/e_system_interface/GetAllPadBYDetailT.aspx?db=SzFpca 標題
    @GET("/e_system_interface/GetAllPadBYDetailT.aspx")
    Observable<MultiColFormTitleResult> getMultiColFormTitles(@Query("db") String db);

    //http://10.182.15.165/e_system_interface/GetAllPadBYDetailL.aspx?db=SzFpca 內容
    @GET("/e_system_interface/GetAllPadBYDetailL.aspx")
    Observable<MultiColFormContentResult> getMultiColFormContents(@Query("db") String db);

    //http://10.182.15.165:8999/e_system_interface/GetAllpadDetail.aspx?db=SzFpca
    //通用表单
    @GET("e_system_interface/GetAllpadDetail.aspx")
    Observable<GeneralFormResult> getGeneralFormList(@Query("db") String db);

    //http://10.182.15.165:8999/e_system_interface/GetAllPadUserAuthList.aspx
    //用户列表
    @GET("e_system_interface/GetAllPadUserAuthList.aspx")
    Observable<UserResult> getUserList(@Query("db") String db);

    //初件表单栏位
    @GET("/e_system_interface/GetAllPadCJControl.aspx")
    Observable<CustomCellResult> getCustomCell();

    //绑定NFC标签
    //http://10.182.15.165/e_system_interface/SavePadMachineBankNfc.aspx?db=SzFpca
    @FormUrlEncoded
    @POST("/e_system_interface/SavePadMachineBankNfc.aspx")
    Observable<GeneralPostResult> boundNFCCard(@Query("db") String db, @Field("bindData") String bindData);

    //数据提交
    //http://10.182.15.165/e_system_interface/SavePadMachineData.aspx?db=SzFpca
    @FormUrlEncoded
    @POST("/e_system_interface/SavePadMachineData.aspx")
    Observable<GeneralPostResult> postFormData(@Query("db") String db, @Field("field_data") String data);

    //数据提交
    //http://10.182.15.165/e_system_interface/SavePadMachineData.aspx?db=SzFpca
    @FormUrlEncoded
    @POST("/e_system_interface/SavePadMachineData.aspx")
    Observable<FormPostResult> postCJFormData(@Query("db") String db, @Field("field_data") String data);

    //公式
    //http://10.182.15.165/e_system_interface/GetAllPadDefaultiinfo.aspx?db=SzFpca
    @POST("/e_system_interface/GetAllPadDefaultiinfo.aspx")
    Observable<FormulaResult> getFormulas(@Query("db") String db);

    //执行公式
    //http://10.182.15.165:8999/e_system_interface/GetAllPadDefaultiinfoVlue.aspx?db=SzFpca
    @FormUrlEncoded
    @POST("/e_system_interface/GetAllPadDefaultiinfoVlue.aspx")
    Observable<GeneralPostResult> executeFormula(@Query("db") String db,
                                                 @Field("formula") String formula,
                                                 @Field("calFields") String parameters);

    //线体限制时间设定
    //http://10.182.15.165/e_system_interface/SavePadMachineLimitTime.aspx
    @FormUrlEncoded
    @POST("/e_system_interface/SavePadMachineLimitTime.aspx")
    Observable<GeneralPostResult> bindLineLimitTime(@Query("db") String db,
                                                    @Field("bindData") String bindData);

    //初件规定栏位值
    //http://10.182.15.165/e_system_interface/GetAllPadPartnumitem.aspx?db=SzFpca
    @FormUrlEncoded
    @POST("/e_system_interface/GetAllPadPartnumitem.aspx")
    Observable<CJDefineCellsResult> getCJDefineCells(@Query("db") String db,
                                                     @Field("reportcode") String reportCode,
                                                     @Field("partnum") String partNum,
                                                     @Field("lineno") String lineNo);

    //提交审核意见
    // http://10.182.15.165/e_system_interface/SavePadReportvisa.aspx?db=SzFpca
    @FormUrlEncoded
    @POST("/e_system_interface/SavePadReportvisa.aspx")
    Observable<GeneralPostResult> postAuditJudgement(@Query("db") String db,
                                                     @Field("workno") String workNo,
                                                     @Field("paperno") String paperNo,
                                                     @Field("index") String index,
                                                     @Field("auditdate") String auditdate,
                                                     @Field("audittime") String audittime,
                                                     @Field("judgement") String judgement,
                                                     @Field("reportcode") String reportcode,
                                                     @Field("audited") String audit);

    //查询已完成的审核意见
    // http://10.182.15.165/e_system_interface/GetAllPadReportvisa.aspx?db=SzFpca
    @FormUrlEncoded
    @POST("/e_system_interface/GetAllPadReportvisa.aspx")
    Observable<AuditJudgementResult> getAuditJudgements(@Query("db") String db,
                                                        @Field("paperno") String paperNo);

    //人员设备关系
    //http://10.182.15.165/e_system_interface/GetAllPadUser_machine.aspx?db=SzFpca
    @GET("/e_system_interface/GetAllPadUser_machine.aspx")
    Observable<UserAndMachineResult> getUserMachineBounds(@Query("db") String db);
}
