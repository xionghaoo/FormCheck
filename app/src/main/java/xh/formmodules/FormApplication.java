package xh.formmodules;

import com.zdtco.BaseApplication;
import com.zdtco.datafetch.Repository;

import org.jetbrains.annotations.NotNull;

/**
 * Created by G1494458 on 2017/12/23.
 */

public class FormApplication extends BaseApplication {
    @NotNull
    @Override
    public Repository initRepo() {
        return new AppRepository(this);
    }
}
