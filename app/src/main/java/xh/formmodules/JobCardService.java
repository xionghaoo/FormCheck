package xh.formmodules;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by G1494458 on 2017/12/23.
 */

public interface JobCardService {
    //http://10.182.34.124:8999/MachinePaperLess/GetAllPadUserAuthList.aspx?softno=323828838
    @GET("/MachinePaperLess/GetAllPadUserAuthList.aspx")
    Observable<WorkNo> getWorkNo(@Query("softno") String code);
}
