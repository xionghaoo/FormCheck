package xh.formmodules;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by G1494458 on 2018/3/2.
 */

public interface JobCardService_YK {
//    @GET("/GetInfo/get_info.aspx")
//    Observable<WorkNo> getWorkNo(@Query("softno") String code);

    //测试用
    @GET("/MachinePaperLess/GetAllPadUserAuthList.aspx")
    Observable<WorkNo> getWorkNo(@Query("softno") String code);
}
